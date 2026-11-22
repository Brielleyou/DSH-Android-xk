package io.github.jackwener.dsh.feature.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class StorageTest {

    @Test fun formatBytes() {
        assertEquals("0 B", StorageScanner.formatBytes(0))
        assertEquals("999 B", StorageScanner.formatBytes(999))
        assertEquals("1.0 KB", StorageScanner.formatBytes(1000))
        assertEquals("1.5 KB", StorageScanner.formatBytes(1500))
        assertEquals("2.0 MB", StorageScanner.formatBytes(2_000_000))
        assertEquals("3.4 GB", StorageScanner.formatBytes(3_400_000_000))
    }

    @Test fun dirSize() {
        val root = createTempDir()
        File(root, "a.txt").writeText("12345")
        File(root, "sub").mkdirs()
        File(root, "sub/b.bin").writeBytes(ByteArray(10))
        assertEquals(15L, StorageScanner.dirSize(root))
        assertEquals(0L, StorageScanner.dirSize(null))
        assertEquals(0L, StorageScanner.dirSize(File(root, "missing")))
        root.deleteRecursively()
    }

    @Test fun cacheRestExcludesNamedDirs() {
        val root = createTempDir()
        File(root, "node-compile-cache").mkdirs()
        File(root, "tmp").mkdirs()
        File(root, "x").mkdirs()
        File(root, "loose.txt").writeText("hi")
        val rest = StorageScanner.cacheRest(root).map { it.name }.toSet()
        assertEquals(setOf("x", "loose.txt"), rest)
        root.deleteRecursively()
    }

    @Test fun zipWritesFilesAndDirs() {
        val root = createTempDir()
        val file = File(root, "dsh.db").apply { writeText("db") }
        val dir = File(root, "dsh-home").apply { mkdirs() }
        File(dir, "skills").mkdirs()
        File(dir, "skills/s.txt").writeText("skill")
        File(dir, "meta.json").writeText("{}")
        val out = ByteArrayOutputStream()
        StorageScanner.zip(out, listOf("dsh.db" to file, "dsh-home" to dir))
        val entries = mutableMapOf<String, String>()
        ZipInputStream(out.toByteArray().inputStream()).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                entries[e.name] = zin.readBytes().toString(Charsets.UTF_8)
                zin.closeEntry()
                e = zin.nextEntry
            }
        }
        assertEquals(setOf("dsh.db", "dsh-home/skills/s.txt", "dsh-home/meta.json"), entries.keys)
        assertEquals("db", entries["dsh.db"])
        assertEquals("skill", entries["dsh-home/skills/s.txt"])
        root.deleteRecursively()
    }

    @Test fun targetForMapsAndRejectsUnsafeNames() {
        val files = createTempDir()
        val db = createTempDir()
        assertEquals(File(db, "dsh.db"), StorageScanner.targetFor(files, db, "dsh.db"))
        assertEquals(File(db, "dsh.db-wal"), StorageScanner.targetFor(files, db, "dsh.db-wal"))
        assertEquals(File(files, "dsh-home/a/b.txt"), StorageScanner.targetFor(files, db, "dsh-home/a/b.txt"))
        assertEquals(File(files, "home/c.txt"), StorageScanner.targetFor(files, db, "home/c.txt"))
        assertNull(StorageScanner.targetFor(files, db, "other/evil"))
        assertNull(StorageScanner.targetFor(files, db, "dsh-home/../evil"))
        assertNull(StorageScanner.targetFor(files, db, "home/../../evil"))
        assertNull(StorageScanner.targetFor(files, db, "/abs/path"))
        assertNull(StorageScanner.targetFor(files, null, "dsh.db"))
        files.deleteRecursively(); db.deleteRecursively()
    }

    @Test fun restoreRoundTrip() {
        val src = createTempDir()
        val dbFile = File(src, "dsh.db").apply { writeText("DBDATA") }
        val dh = File(src, "dsh-home").apply { mkdirs() }
        File(dh, "skills").mkdirs(); File(dh, "skills/s.txt").writeText("skill")
        File(dh, "meta.json").writeText("{}")
        val home = File(src, "home").apply { mkdirs() }
        File(home, "note.txt").writeText("hi")
        val out = ByteArrayOutputStream()
        StorageScanner.zip(out, listOf("dsh.db" to dbFile, "dsh-home" to dh, "home" to home))

        val zipFile = File.createTempFile("backup", ".zip").apply { writeBytes(out.toByteArray()) }
        val targetFiles = createTempDir()
        val targetDb = createTempDir()
        val restored = StorageScanner.restore(ZipFile(zipFile), targetFiles, targetDb)
        assertTrue(restored > 0)
        assertEquals("DBDATA", File(targetDb, "dsh.db").readText())
        assertEquals("skill", File(targetFiles, "dsh-home/skills/s.txt").readText())
        assertEquals("{}", File(targetFiles, "dsh-home/meta.json").readText())
        assertEquals("hi", File(targetFiles, "home/note.txt").readText())
        src.deleteRecursively(); zipFile.delete(); targetFiles.deleteRecursively(); targetDb.deleteRecursively()
    }

    @Test fun restoreSkipsZipSlip() {
        val targetFiles = createTempDir()
        val targetDb = createTempDir()
        val zipFile = File.createTempFile("evil", ".zip")
        ZipOutputStream(zipFile.outputStream()).use { z ->
            z.putNextEntry(ZipEntry("../evil.txt")); z.write("evil".toByteArray()); z.closeEntry()
            z.putNextEntry(ZipEntry("home/ok.txt")); z.write("ok".toByteArray()); z.closeEntry()
        }
        val restored = StorageScanner.restore(ZipFile(zipFile), targetFiles, targetDb)
        assertEquals(1, restored)
        assertFalse(File(targetFiles.parentFile, "evil.txt").exists())
        assertEquals("ok", File(targetFiles, "home/ok.txt").readText())
        zipFile.delete(); targetFiles.deleteRecursively(); targetDb.deleteRecursively()
    }

    private fun createTempDir(): File = File.createTempFile("storage", "").apply { delete(); mkdirs() }
}
