package io.github.jackwener.dsh.feature.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jackwener.dsh.R
import io.github.jackwener.dsh.core.designsystem.BtnSize
import io.github.jackwener.dsh.core.designsystem.BtnVariant
import io.github.jackwener.dsh.core.designsystem.DButton
import io.github.jackwener.dsh.core.designsystem.DotKind
import io.github.jackwener.dsh.core.designsystem.Dsh
import io.github.jackwener.dsh.core.designsystem.DshType
import io.github.jackwener.dsh.core.designsystem.Fmt
import io.github.jackwener.dsh.core.designsystem.SettingsCard
import io.github.jackwener.dsh.core.designsystem.StateDot
import io.github.jackwener.dsh.core.designsystem.Tag
import io.github.jackwener.dsh.core.designsystem.Tone
import io.github.jackwener.dsh.core.platform.AccessibilityKeeper
import kotlinx.coroutines.launch

/**
 * 无障碍保活 card (设置 → 手机权限): shows whether the accessibility service is bound and offers a
 * manual check; the repair itself runs on DSH's own notification (process restart) and every 30 min.
 */
@Composable
fun A11yKeepAliveCard(modifier: Modifier = Modifier) {
    val c = Dsh.colors
    val context = LocalContext.current
    val keeper = remember { a11yKeeperOf(context) }
    val s by keeper.status.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    SettingsCard(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StateDot(if (s.bound) DotKind.Done else DotKind.Warn)
                Text(stringResource(R.string.a11y_keeper_title), Modifier.weight(1f), style = DshType.xs13m, color = c.labelTertiary)
                Tag(stringResource(if (s.bound) R.string.a11y_keeper_ok else R.string.a11y_keeper_down),
                    if (s.bound) Tone.Success else Tone.Warn)
            }
            Text(stringResource(R.string.a11y_keeper_desc), style = DshType.xs13, color = c.labelSecondary)
            Text(
                if (s.lastCheckAt == 0L) stringResource(R.string.a11y_keeper_never)
                else stringResource(R.string.a11y_keeper_last, Fmt.stamp(context, s.lastCheckAt)),
                style = DshType.xxs12, color = c.labelTertiary,
            )
            if (s.lastRepairAt > 0) Text(stringResource(R.string.a11y_keeper_repaired_at, Fmt.stamp(context, s.lastRepairAt)),
                style = DshType.xxs12, color = c.labelTertiary)
            if (s.outcome == AccessibilityKeeper.Outcome.NoShell && !s.bound)
                Text(stringResource(R.string.a11y_keeper_need_shell), style = DshType.xxs12, color = c.warnLabel)
        }
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            if (!s.bound) DButton(stringResource(R.string.a11y_keeper_open), {
                runCatching { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            }, variant = BtnVariant.Outline, enabled = !busy, size = BtnSize.Sm)
            DButton(stringResource(if (busy) R.string.a11y_keeper_checking else R.string.a11y_keeper_check), {
                busy = true
                scope.launch { keeper.checkAndRepair(); busy = false }
            }, variant = BtnVariant.Outline, enabled = !busy, size = BtnSize.Sm)
        }
    }
}
