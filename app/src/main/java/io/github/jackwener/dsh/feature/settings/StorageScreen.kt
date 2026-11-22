package io.github.jackwener.dsh.feature.settings

import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.jackwener.dsh.R
import io.github.jackwener.dsh.core.designsystem.BtnSize
import io.github.jackwener.dsh.core.designsystem.BtnVariant
import io.github.jackwener.dsh.core.designsystem.DButton
import io.github.jackwener.dsh.core.designsystem.DCheckbox
import io.github.jackwener.dsh.core.designsystem.DDialog
import io.github.jackwener.dsh.core.designsystem.DIcon
import io.github.jackwener.dsh.core.designsystem.Dsh
import io.github.jackwener.dsh.core.designsystem.DshType
import io.github.jackwener.dsh.core.designsystem.HDivider
import io.github.jackwener.dsh.core.designsystem.SettingsCard
import io.github.jackwener.dsh.core.designsystem.SettingsGroupTitle
import io.github.jackwener.dsh.core.designsystem.SettingsRow
import io.github.jackwener.dsh.core.designsystem.Tag
import io.github.jackwener.dsh.core.designsystem.Tone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Process

/** 设置 → 存储: 已占用 (detailed breakdown), 清理 (cache options) and 备份 (domains → one zip). */
@Composable
fun StorageSection(vm: SettingsViewModel) {
    val context = LocalContext.current
    val c = Dsh.colors
    val scope = rememberCoroutineScope()

    var occupied by remember { mutableStateOf<List<StorageScanner.Part>>(emptyList()) }
    var cleanables by remember { mutableStateOf<List<StorageScanner.Cleanable>>(emptyList()) }
    var domains by remember { mutableStateOf<List<StorageScanner.Domain>>(emptyList()) }
    var free by remember { mutableStateOf(0L) }
    var scanned by remember { mutableStateOf(false) }

    val defaultDir = remember { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) }

    fun scan() = scope.launch {
        val r = withContext(Dispatchers.IO) {
            ScanResult(StorageScanner.occupied(context), StorageScanner.cleanables(context),
                StorageScanner.domains(context), context.filesDir.usableSpace)
        }
        occupied = r.occupied; cleanables = r.cleanables; domains = r.domains; free = r.free; scanned = true
    }
    LaunchedEffect(Unit) { scan() }

    var cleanSel by remember { mutableStateOf(setOf<String>()) }
    var confirmClean by remember { mutableStateOf(false) }
    var backupSel by remember { mutableStateOf(setOf<String>()) }
    var customUri by remember { mutableStateOf<Uri?>(null) }
    val pickDoc = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri != null) customUri = uri
    }
    val customName = remember(customUri) { customUri?.let { runCatching { uriDisplayName(context, it) }.getOrNull() } }

    var importUri by remember { mutableStateOf<Uri?>(null) }
    var confirmImport by remember { mutableStateOf(false) }
    val pickImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) importUri = uri
    }
    val importName = remember(importUri) { importUri?.let { runCatching { uriDisplayName(context, it) }.getOrNull() } }

    fun toggle(set: Set<String>, key: String) = if (key in set) set - key else set + key
    fun stamp() = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())

    val cleanBytes = cleanables.filter { it.key in cleanSel }.sumOf { it.bytes }
    val usedBytes = occupied.sumOf { it.bytes }

    // ── 已占用 ──
    SettingsGroupTitle(stringResource(R.string.storage_used))
    SettingsCard {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StorageStat(stringResource(R.string.storage_total), StorageScanner.formatBytes(usedBytes))
            StorageStat(stringResource(R.string.storage_free), StorageScanner.formatBytes(free))
        }
        if (!scanned) Text(stringResource(R.string.storage_scanning), Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = DshType.xs13, color = c.labelTertiary)
        else if (occupied.isEmpty()) Text(stringResource(R.string.storage_empty), Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = DshType.xs13, color = c.labelTertiary)
        else {
            val max = occupied.maxOfOrNull { it.bytes }?.coerceAtLeast(1) ?: 1
            val palette = listOf(c.kindMessages, c.kindTools, c.business, c.success, c.warn, c.kindSystem)
            HDivider(Modifier.padding(start = 16.dp))
            occupied.forEachIndexed { i, p ->
                if (i > 0) HDivider(Modifier.padding(start = 16.dp))
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(partLabel(p.key), Modifier.weight(1f), style = DshType.xs13, color = c.labelPrimary)
                        Text(StorageScanner.formatBytes(p.bytes), style = DshType.mono12, color = c.labelTertiary)
                    }
                    Text(partDesc(p.key), style = DshType.xxs12, color = c.labelTertiary)
                    Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(c.borderL2)) {
                        Box(Modifier.fillMaxWidth(p.bytes.toFloat() / max).height(4.dp).clip(CircleShape).background(palette[i % palette.size]))
                    }
                }
            }
        }
    }

    // ── 清理 ──
    SettingsGroupTitle(stringResource(R.string.storage_clean))
    SettingsCard {
        if (scanned && (cleanables.isEmpty() || cleanables.all { it.bytes == 0L })) {
            SettingsRow(stringResource(R.string.storage_clean_none), divider = false)
        } else {
            cleanables.forEachIndexed { i, cl ->
                SettingsRow(cleanLabel(cl.key), StorageScanner.formatBytes(cl.bytes), divider = i > 0,
                    onClick = { cleanSel = toggle(cleanSel, cl.key) },
                    trailing = { DCheckbox(cl.key in cleanSel, null) })
            }
        }
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.CenterEnd) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.storage_clean_free, StorageScanner.formatBytes(cleanBytes)), Modifier.weight(1f),
                    style = DshType.xs13, color = c.labelTertiary)
                DButton(stringResource(R.string.storage_clean_confirm), { confirmClean = true }, size = BtnSize.Sm, enabled = cleanBytes > 0)
            }
        }
    }
    if (confirmClean) DDialog(onDismiss = { confirmClean = false }, title = stringResource(R.string.storage_clean_confirm_title), buttons = {
        DButton(stringResource(R.string.cancel), { confirmClean = false }, variant = BtnVariant.Outline)
        DButton(stringResource(R.string.storage_clean_confirm), {
            confirmClean = false
            val freed = cleanBytes
            scope.launch {
                withContext(Dispatchers.IO) { cleanables.filter { it.key in cleanSel }.forEach(StorageScanner::remove) }
                android.widget.Toast.makeText(context, context.getString(R.string.storage_clean_done, StorageScanner.formatBytes(freed)), android.widget.Toast.LENGTH_SHORT).show()
                cleanSel = emptySet()
                scan()
            }
        })
    }) {
        Text(stringResource(R.string.storage_clean_confirm_body, StorageScanner.formatBytes(cleanBytes)), style = DshType.s14, color = Dsh.colors.labelSecondary)
    }

    // ── 备份 ──
    SettingsGroupTitle(stringResource(R.string.storage_backup))
    SettingsCard {
        Text(stringResource(R.string.storage_backup_hint), Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = DshType.xs13, color = c.labelTertiary)
        domains.forEachIndexed { i, d ->
            SettingsRow(domainLabel(d.key), StorageScanner.formatBytes(d.bytes), divider = i > 0,
                onClick = { backupSel = toggle(backupSel, d.key) },
                trailing = { DCheckbox(d.key in backupSel, null) })
        }
    }
    SettingsGroupTitle(stringResource(R.string.storage_backup_path))
    SettingsCard {
        SettingsRow(stringResource(R.string.storage_backup_default), defaultDir.path, divider = false,
            onClick = { customUri = null },
            trailing = { if (customUri == null) Tag(stringResource(R.string.enabled), Tone.Success) else DIcon(R.drawable.dsw_chevron_right_outline, size = 14.dp, tint = c.labelTertiary) })
        SettingsRow(stringResource(R.string.storage_backup_custom), customName ?: stringResource(R.string.storage_backup_choose),
            onClick = { pickDoc.launch("dsh-backup-" + stamp() + ".zip") },
            trailing = { if (customUri != null) Tag(stringResource(R.string.enabled), Tone.Success) else DIcon(R.drawable.dsw_chevron_right_outline, size = 14.dp, tint = c.labelTertiary) })
    }
    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.CenterEnd) {
        DButton(stringResource(R.string.storage_backup_start), {
            val roots = domains.filter { it.key in backupSel }.flatMap { it.roots }
            if (roots.isEmpty()) return@DButton
            val name = "dsh-backup-" + stamp() + ".zip"
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    runCatching {
                        val out: OutputStream = if (customUri != null) context.contentResolver.openOutputStream(customUri!!)!!
                        else File(defaultDir, name).outputStream()
                        out.use { StorageScanner.zip(it, roots) }
                    }.isSuccess
                }
                android.widget.Toast.makeText(context, if (ok) context.getString(R.string.storage_backup_done, name) else context.getString(R.string.storage_backup_failed),
                    android.widget.Toast.LENGTH_SHORT).show()
            }
        }, size = BtnSize.Md, enabled = backupSel.isNotEmpty())
    }
    // ── 导入 ──
    SettingsGroupTitle(stringResource(R.string.storage_import))
    SettingsCard {
        Text(stringResource(R.string.storage_import_hint), Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = DshType.xs13, color = c.labelTertiary)
        SettingsRow(stringResource(R.string.storage_import_choose), importName ?: stringResource(R.string.storage_import_none), divider = false,
            onClick = { pickImport.launch(arrayOf("application/zip")) },
            trailing = { DIcon(R.drawable.dsw_chevron_right_outline, size = 14.dp, tint = c.labelTertiary) })
    }
    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.CenterEnd) {
        DButton(stringResource(R.string.storage_import_start), { confirmImport = true }, size = BtnSize.Md, enabled = importUri != null)
    }
    if (confirmImport) DDialog(onDismiss = { confirmImport = false }, title = stringResource(R.string.storage_import_confirm_title), buttons = {
        DButton(stringResource(R.string.cancel), { confirmImport = false }, variant = BtnVariant.Outline)
        DButton(stringResource(R.string.storage_import_start), {
            confirmImport = false
            val uri = importUri ?: return@DButton
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    try {
                        vm.stopEngineForRestore()
                        val ins = context.contentResolver.openInputStream(uri) ?: return@withContext false
                        StorageScanner.importZip(context, ins)
                    } catch (e: Exception) {
                        false
                    }
                }
                if (ok) {
                    android.widget.Toast.makeText(context, R.string.storage_import_done, android.widget.Toast.LENGTH_SHORT).show()
                    restartApp(context)
                } else {
                    android.widget.Toast.makeText(context, R.string.storage_import_failed, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        })
    }) {
        Text(stringResource(R.string.storage_import_confirm_body), style = DshType.s14, color = Dsh.colors.labelSecondary)
    }
}

private data class ScanResult(
    val occupied: List<StorageScanner.Part>,
    val cleanables: List<StorageScanner.Cleanable>,
    val domains: List<StorageScanner.Domain>,
    val free: Long,
)

@Composable
private fun StorageStat(label: String, value: String) = Column {
    Text(value, style = DshType.l20, color = Dsh.colors.labelPrimary)
    Text(label, style = DshType.xxs12, color = Dsh.colors.labelTertiary)
}

@Composable private fun partLabel(key: String) = stringResource(when (key) {
    "sessions" -> R.string.storage_part_sessions
    "plugins" -> R.string.storage_part_plugins
    "home" -> R.string.storage_part_home
    "engine" -> R.string.storage_part_engine
    "cache" -> R.string.storage_part_cache
    else -> R.string.storage_part_external
})

@Composable private fun partDesc(key: String) = stringResource(when (key) {
    "sessions" -> R.string.storage_part_sessions_desc
    "plugins" -> R.string.storage_part_plugins_desc
    "home" -> R.string.storage_part_home_desc
    "engine" -> R.string.storage_part_engine_desc
    "cache" -> R.string.storage_part_cache_desc
    else -> R.string.storage_part_external_desc
})

@Composable private fun cleanLabel(key: String) = stringResource(when (key) {
    "nodeCache" -> R.string.storage_clean_node_cache
    "tmp" -> R.string.storage_clean_tmp
    "otherCache" -> R.string.storage_clean_other_cache
    else -> R.string.storage_clean_external_cache
})

@Composable private fun domainLabel(key: String) = stringResource(when (key) {
    "sessions" -> R.string.storage_backup_domain_sessions
    "plugins" -> R.string.storage_backup_domain_plugins
    else -> R.string.storage_backup_domain_home
})

private fun uriDisplayName(context: android.content.Context, uri: Uri): String? = runCatching {
    context.contentResolver.query(uri, null, null, null, null)?.use { cur ->
        val i = cur.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (i >= 0 && cur.moveToFirst()) cur.getString(i) else null
    }
}.getOrNull()

/** Restarts the app process so the restored Room DB (and engine) reload from disk. */
private fun restartApp(context: android.content.Context) {
    @Suppress("DEPRECATION")
    runCatching {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        } ?: return
        val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
        context.getSystemService(AlarmManager::class.java).set(AlarmManager.RTC, System.currentTimeMillis() + 300, pending)
    }
    Process.killProcess(Process.myPid())
}
