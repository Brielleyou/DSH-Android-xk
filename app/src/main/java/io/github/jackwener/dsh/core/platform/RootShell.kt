package io.github.jackwener.dsh.core.platform

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Root (su) as a second way to get shell privileges next to Shizuku. Only command execution goes
 * through it: the Shizuku user service (virtual display, screen capture, Android/data file access)
 * is a binder service and cannot be replaced by su, so those steps still need Shizuku.
 *
 * The choice is remembered in SharedPreferences and only accepted after `su -c id -u` answered 0.
 */
@Singleton
class RootShell @Inject constructor(@ApplicationContext private val context: Context) {
    private val prefs = context.getSharedPreferences("root_shell", Context.MODE_PRIVATE)

    /** The user authorized root before (the last probe succeeded); drives the UI. */
    @Volatile
    var enabled: Boolean = prefs.getBoolean(KEY_ENABLED, false)
        private set

    private val _state = MutableStateFlow(enabled)
    /** Whether root is authorized, for the Shizuku card to show 已授权 / 授权 root. */
    val state: StateFlow<Boolean> = _state.asStateFlow()

    /** su answered as uid 0 the last time we asked; [probe] refreshes it. */
    @Volatile
    var usable: Boolean = false
        private set

    /** True when root is authorized and still working. */
    val ready: Boolean get() = enabled && usable

    /** Asks su for root (its manager shows the prompt) and remembers the answer. */
    suspend fun request(): Boolean {
        val ok = probe()
        enabled = ok
        _state.value = ok
        prefs.edit().putBoolean(KEY_ENABLED, ok).apply()
        return ok
    }

    /** Forgets the authorization; commands go back to Shizuku. */
    fun forget() {
        enabled = false
        usable = false
        _state.value = false
        prefs.edit().putBoolean(KEY_ENABLED, false).apply()
    }

    /** Cheap enough to run on resume: one short `su -c id -u`. */
    suspend fun probe(): Boolean = withContext(Dispatchers.IO) {
        val ok = runCatching { run("id -u", 10_000L).output.trim() == "0" }.getOrDefault(false)
        usable = ok
        ok
    }

    suspend fun exec(cmd: String, timeoutMs: Long = 30_000): ShellResult =
        withContext(Dispatchers.IO) { run(cmd, timeoutMs) }

    /**
     * `su -c <cmd>`; stderr is merged into the output because most callers only read stdout.
     * A timeout kills the process and reports -1 so callers can fall back instead of hanging.
     */
    private suspend fun run(cmd: String, timeoutMs: Long): ShellResult = withContext(Dispatchers.IO) {
        val process = ProcessBuilder("su", "-c", cmd).redirectErrorStream(true).start()
        try {
            val text = withTimeoutOrNull(timeoutMs) { process.inputStream.bufferedReader().use { it.readText() } }
            if (text == null) {
                process.destroy()
                ShellResult(-1, "")
            } else {
                ShellResult(process.waitFor(), text)
            }
        } finally {
            runCatching { process.destroy() }
        }
    }

    private companion object {
        const val KEY_ENABLED = "enabled"
    }
}
