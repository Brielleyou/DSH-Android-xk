package io.github.jackwener.dsh.core.platform

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jackwener.dsh.service.DshAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 无障碍保活: keep [DshAccessibilityService] bound. Battery savers, "restricted" apps and a reboot
 * before the first unlock unbind it, and the screen tools silently lose the ability to read and tap.
 * Two triggers repair it, as asked:
 *  1. a notification from DSH's own package — the engine posts one whenever the process comes back;
 *  2. a 30-minute fallback check (the process may stay alive without any such notification).
 * The secure setting is rewritten through Shizuku or root; without either the card can only tell
 * the user to turn it back on by hand.
 */
@Singleton
class AccessibilityKeeper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shell: ShizukuShell,
    private val root: RootShell,
) {
    enum class Outcome { Ok, Repaired, Failed, NoShell }

    /** [bound]: the service is connected or listed as enabled; the timestamps are epoch millis. */
    data class Status(
        val bound: Boolean = false,
        val lastCheckAt: Long = 0,
        val lastRepairAt: Long = 0,
        val outcome: Outcome = Outcome.Ok,
    )

    private val prefs = context.getSharedPreferences("a11y_keeper", Context.MODE_PRIVATE)
    private val component = ComponentName(context, DshAccessibilityService::class.java)
    private val name = component.flattenToString()

    private val _status = MutableStateFlow(
        Status(
            bound = false,
            lastCheckAt = prefs.getLong(KEY_CHECK, 0),
            lastRepairAt = prefs.getLong(KEY_REPAIR, 0),
            outcome = runCatching { Outcome.valueOf(prefs.getString(KEY_OUTCOME, Outcome.Ok.name)!!) }.getOrDefault(Outcome.Ok),
        ).copy(bound = isBound()),
    )
    val status: StateFlow<Status> = _status.asStateFlow()

    fun isBound(): Boolean =
        DshAccessibilityService.instance != null || enabledServices().any { it == name || it == component.flattenToShortString() }

    /** Starts the periodic fallback; also exposes this instance to the notification listener. */
    fun start(scope: CoroutineScope) {
        instance = this
        scope.launch {
            // A cold start may not produce an engine notification this time (the service was already up).
            delay(FIRST_PASS_MS)
            runCatching { checkAndRepair() }
            while (true) {
                delay(INTERVAL_MS)
                runCatching { checkAndRepair() }
            }
        }
    }

    /** The engine's own notification means the process just came back: check and repair at once. */
    fun onDshNotification(scope: CoroutineScope) {
        if (System.currentTimeMillis() - _status.value.lastCheckAt < DEDUPE_MS) return
        scope.launch { runCatching { checkAndRepair() } }
    }

    /** Checks now, repairs when the service is not bound; safe to call from any thread. */
    suspend fun checkAndRepair(): Status = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (isBound()) return@withContext save(now, _status.value.lastRepairAt, Outcome.Ok)
        val outcome = when {
            repair() && awaitBound() -> Outcome.Repaired
            !canRepair() -> Outcome.NoShell
            else -> Outcome.Failed
        }
        save(now, if (outcome == Outcome.Repaired) now else _status.value.lastRepairAt, outcome)
    }

    private fun canRepair(): Boolean = root.enabled || shell.access.state.value.ready

    private suspend fun repair(): Boolean {
        val services = enabledServices().toMutableList()
        if (name !in services) services += name
        val put = "settings put secure enabled_accessibility_services ${quote(services.joinToString(":"))} && settings put secure accessibility_enabled 1"
        if (root.enabled) {
            val r = runCatching { root.exec(put) }.getOrNull()
            if (r != null && r.code == 0) return true
        }
        val r = runCatching { shell.exec(put) }.getOrNull()
        return r != null && r.code == 0
    }

    /** The system re-reads the setting asynchronously; give it a moment before calling it failed. */
    private suspend fun awaitBound(): Boolean {
        repeat(10) {
            if (isBound()) return true
            delay(200)
        }
        return isBound()
    }

    private fun save(now: Long, repairedAt: Long, outcome: Outcome): Status {
        val s = Status(isBound(), now, repairedAt, outcome)
        _status.value = s
        prefs.edit().putLong(KEY_CHECK, now).putLong(KEY_REPAIR, repairedAt).putString(KEY_OUTCOME, outcome.name).apply()
        return s
    }

    private fun enabledServices(): List<String> =
        (Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: "")
            .split(':').map { it.trim() }.filter { it.isNotEmpty() }

    private fun quote(s: String) = "'" + s.replace("'", "'\\''") + "'"

    companion object {
        /** Set by [start]; the notification listener has no Hilt injection point. */
        @Volatile var instance: AccessibilityKeeper? = null
            private set

        private const val KEY_CHECK = "lastCheck"
        private const val KEY_REPAIR = "lastRepair"
        private const val KEY_OUTCOME = "outcome"
        private const val FIRST_PASS_MS = 20_000L
        const val INTERVAL_MS = 30 * 60_000L
        private const val DEDUPE_MS = 10_000L
    }
}
