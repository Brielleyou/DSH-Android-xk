package io.github.jackwener.dsh.feature.settings

/**
 * Detects which root manager (su app) is installed and its version. Two channels:
 *  1. package detection — from the installed manager app, no root needed (misses a hidden/renamed Magisk);
 *  2. su probe — runs the manager's own CLI once root is authorized, so the name and version are accurate.
 */
object RootManager {

    data class Info(val name: String, val version: String?)

    /** (display name, package), tried in order; normally only one is installed. */
    val MANAGERS = listOf(
        "Magisk" to "com.topjohnwu.magisk",
        "KernelSU" to "me.weishu.kernelsu",
        "APatch" to "me.bmax.apatch",
        "SuperSU" to "eu.chainfire.supersu",
    )

    /** [versionOf] returns a package's versionName, or null when it is not installed. */
    fun detect(versionOf: (String) -> String?): Info? =
        MANAGERS.firstNotNullOfOrNull { (name, pkg) -> versionOf(pkg)?.let { Info(name, it) } }

    /**
     * One su command that prints `Name|version` for the first installed manager (Magisk → KernelSU →
     * APatch), or nothing. `command -v` uses the su environment's PATH, which Magisk/KernelSU/APatch
     * set up themselves, so a renamed Magisk app is still found.
     */
    val SU_PROBE = """
        command -v magisk >/dev/null 2>&1 && echo "Magisk|$(magisk -v 2>/dev/null)"
        command -v ksud >/dev/null 2>&1 && echo "KernelSU|$(ksud -V 2>/dev/null)"
        command -v apd >/dev/null 2>&1 && echo "APatch|$(apd -V 2>/dev/null)"
    """.trimIndent()

    /** Parses [SU_PROBE]'s output into the first valid `Name|version` pair, skipping any leading noise. */
    fun parseProbe(output: String): Info? =
        output.lineSequence()
            .map { it.trim() }
            .firstNotNullOfOrNull { line ->
                val parts = line.split('|', limit = 2)
                if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) Info(parts[0], parts[1].trim()) else null
            }
}
