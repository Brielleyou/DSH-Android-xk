package io.github.jackwener.dsh.feature.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RootManagerTest {

    @Test fun detectsFirstManagerInOrder() {
        val versions = mapOf("me.weishu.kernelsu" to "1.0.2", "com.topjohnwu.magisk" to "28.1")
        assertEquals(RootManager.Info("Magisk", "28.1"), RootManager.detect { versions[it] })
    }

    @Test fun kernelsuWhenMagiskMissing() {
        val versions = mapOf("me.weishu.kernelsu" to "1.0.2")
        assertEquals(RootManager.Info("KernelSU", "1.0.2"), RootManager.detect { versions[it] })
    }

    @Test fun apatch() {
        val versions = mapOf("me.bmax.apatch" to "10763")
        assertEquals(RootManager.Info("APatch", "10763"), RootManager.detect { versions[it] })
    }

    @Test fun nullWhenNoneInstalled() {
        assertNull(RootManager.detect { null })
    }

    @Test fun parseProbeParsesNameAndVersion() {
        assertEquals(RootManager.Info("Magisk", "28.1"), RootManager.parseProbe("Magisk|28.1"))
        assertEquals(RootManager.Info("KernelSU", "11458"), RootManager.parseProbe("noise\nKernelSU|11458\nmore"))
    }

    @Test fun parseProbeRejectsEmptyAndMalformed() {
        assertNull(RootManager.parseProbe(""))
        assertNull(RootManager.parseProbe("\n   \n"))
        assertNull(RootManager.parseProbe("Magisk|"))
        assertNull(RootManager.parseProbe("|28.1"))
        assertNull(RootManager.parseProbe("garbage"))
    }
}
