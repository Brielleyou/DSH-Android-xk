package io.github.jackwener.dsh.feature.settings

import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Storage accounting behind the 设置 → 存储 page: what uses the app's own space (已占用), what can
 * be deleted (清理), what can be packed into one archive (备份) and how a backup is restored (导入).
 * Pure file arithmetic, no Compose here, so it unit-tests without Android. Sizes are decimal
 * (1 MB = 1,000,000 B) like the rest of the app.
 */
object StorageScanner {

    /** 已占用: one non-overlapping slice of the app's own data. */
    data class Part(val key: String, val bytes: Long)

    /** 清理: the files to remove and how much that frees. */
    data class Cleanable(val key: String, val bytes: Long, val files: List<File>)

    /** 备份: a domain, as ready-to-zip (top-level entry name, file or dir) roots. */
    data class Domain(val key: String, val bytes: Long, val roots: List<Pair<String, File>>)

    /** Recursive size of a directory; 0 for null / missing. */
    fun dirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        return runCatching { dir.walkTopDown().filter { it.isFile }.sumOf { it.length() } }.getOrDefault(0L)
    }

    fun fileSize(f: File?): Long = if (f != null && f.isFile) runCatching { f.length() }.getOrDefault(0L) else 0L

    fun formatBytes(n: Long): String = when {
        n >= 1_000_000_000L -> String.format(Locale.US, "%.1f GB", n / 1_000_000_000.0)
        n >= 1_000_000L -> String.format(Locale.US, "%.1f MB", n / 1_000_000.0)
        n >= 1_000L -> String.format(Locale.US, "%.1f KB", n / 1000.0)
        else -> "$n B"
    }

    private fun dbFiles(context: Context): List<File> {
        val db = context.getDatabasePath("dsh.db")
        return listOf(db, File(db.path + "-wal"), File(db.path + "-shm"))
    }

    /** 已占用: the app's own data broken into non-overlapping parts (Room DB, skills/plugins, cwd, engine, caches, external). */
    fun occupied(context: Context): List<Part> {
        val f = context.filesDir
        return listOf(
            Part("sessions", dbFiles(context).sumOf { fileSize(it) }),
            Part("plugins", dirSize(File(f, "dsh-home"))),
            Part("home", dirSize(File(f, "home"))),
            Part("engine", dirSize(File(f, "bin")) + dirSize(File(f, "certs"))),
            Part("cache", dirSize(context.cacheDir)),
            Part("external", dirSize(context.getExternalFilesDir(null)) + dirSize(context.externalCacheDir)),
        )
    }

    /** Every cache directory's children except the two named ones (those are their own cleanables). */
    fun cacheRest(dir: File): List<File> =
        dir.listFiles()?.filter { it.name != "node-compile-cache" && it.name != "tmp" } ?: emptyList()

    /** 清理: cache directories, each individually removable and non-overlapping. */
    fun cleanables(context: Context): List<Cleanable> {
        val cache = context.cacheDir
        val nodeCache = File(cache, "node-compile-cache")
        val tmp = File(cache, "tmp")
        val rest = cacheRest(cache)
        val external = context.externalCacheDir
        return listOf(
            Cleanable("nodeCache", dirSize(nodeCache), listOf(nodeCache)),
            Cleanable("tmp", dirSize(tmp), listOf(tmp)),
            Cleanable("otherCache", rest.sumOf { dirSize(it) }, rest),
            Cleanable("externalCache", dirSize(external), listOfNotNull(external)),
        )
    }

    fun remove(c: Cleanable) { c.files.forEach { it.deleteRecursively() } }

    /** 备份: the domains that can be archived. The Room DB is copied file-by-file (with its -wal/-shm). */
    fun domains(context: Context): List<Domain> {
        val f = context.filesDir
        val db = dbFiles(context).map { it.name to it }
        return listOf(
            Domain("sessions", dbFiles(context).sumOf { fileSize(it) }, db),
            Domain("plugins", dirSize(File(f, "dsh-home")), listOf("dsh-home" to File(f, "dsh-home"))),
            Domain("home", dirSize(File(f, "home")), listOf("home" to File(f, "home"))),
        )
    }

    /**
     * Writes [roots] into a zip on [out]. Each root is (top-level entry name, file or dir); a dir
     * adds every file inside as "name/relativePath". Missing roots are skipped.
     */
    fun zip(out: OutputStream, roots: List<Pair<String, File>>) {
        ZipOutputStream(out.buffered()).use { zip ->
            for ((name, file) in roots) {
                when {
                    file.isFile -> {
                        zip.putNextEntry(ZipEntry(name))
                        file.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                    file.isDirectory -> {
                        file.walkTopDown().filter { it.isFile }.forEach { child ->
                            zip.putNextEntry(ZipEntry("$name/" + child.relativeTo(file).path))
                            child.inputStream().use { it.copyTo(zip) }
                            zip.closeEntry()
                        }
                    }
                }
            }
        }
    }

    // ── 导入 ──

    private val DB_NAMES = setOf("dsh.db", "dsh.db-wal", "dsh.db-shm")

    /** Maps a backup zip entry name to its restore target under [filesDir]/[dbDir]; null for unknown/unsafe names. */
    fun targetFor(filesDir: File, dbDir: File?, name: String): File? = when {
        name in DB_NAMES -> dbDir?.let { File(it, name) }
        name.startsWith("dsh-home/") || name.startsWith("home/") -> under(filesDir, name)
        else -> null
    }

    private fun under(root: File, rel: String): File? =
        if (rel.isEmpty() || rel.startsWith("/") || rel.split('/').any { it == ".." }) null else File(root, rel)

    /**
     * Restores a backup zip into [filesDir]/[dbDir], returning the number of files written (0 on an
     * invalid/foreign archive). The DB is restored only when the archive carries DB entries; in that
     * case the current DB files are removed first so no stale -wal survives.
     */
    fun restore(zip: ZipFile, filesDir: File, dbDir: File?): Int {
        val names = zip.entries().asSequence().map { it.name }.toList()
        if (names.any { it in DB_NAMES }) DB_NAMES.forEach { dbDir?.let { d -> File(d, it).delete() } }
        var wrote = 0
        val e = zip.entries()
        while (e.hasMoreElements()) {
            val entry = e.nextElement()
            val target = targetFor(filesDir, dbDir, entry.name) ?: continue
            if (entry.isDirectory) { target.mkdirs(); continue }
            target.parentFile?.mkdirs()
            zip.getInputStream(entry).use { ins -> target.outputStream().use { out -> ins.copyTo(out) } }
            wrote++
        }
        return wrote
    }

    /** Imports a backup zip from [input]; true when at least one file was restored. */
    fun importZip(context: Context, input: InputStream): Boolean {
        val tmp = File(context.cacheDir, "dsh-import.zip")
        val ok = runCatching {
            tmp.outputStream().use { input.copyTo(it) }
            ZipFile(tmp).use { zip -> restore(zip, context.filesDir, context.getDatabasePath("dsh.db").parentFile) > 0 }
        }.getOrDefault(false)
        tmp.delete()
        return ok
    }
}
