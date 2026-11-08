package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Body
import dev.clean.stretch.figure.Figure
import dev.clean.stretch.figure.MotionPlayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogTest {
    private val all = SampleCatalog.all

    @Test
    fun keysAreUniqueAndStable() {
        assertEquals(all.size, all.map { it.key }.toSet().size)
        all.forEach { assertTrue(it.key, it.key.matches(Regex("[a-z0-9_]+"))) }
    }

    @Test
    fun everyStretchIsComplete() {
        all.forEach { s ->
            assertTrue("${s.key} primary", s.primary.isNotEmpty())
            assertTrue("${s.key} highlight", s.highlight.isNotEmpty())
            assertTrue("${s.key} cues", s.cues.size >= 3)
            assertTrue("${s.key} summary", s.summary.isNotBlank())
            assertTrue("${s.key} level", s.level in 1..3)
            assertTrue("${s.key} overlap", s.primary.intersect(s.secondary.toSet()).isEmpty())
        }
    }

    @Test
    fun everySourceResolves() {
        all.forEach { s ->
            assertTrue("${s.key} has sources", s.sourceIds.isNotEmpty())
            s.sourceIds.forEach { assertNotNull("${s.key} -> $it", Sources.byId(it)) }
        }
    }

    @Test
    fun wallPropsAreOnlyUsedWithWallEquipment() {
        all.filter { it.prop != null }.forEach { assertEquals(it.key, Equipment.Wall, it.equipment) }
    }

    @Test
    fun keyframesStayOnScreenAndOnTheFloor() {
        all.forEach { s ->
            MotionPlayer.keyframes(s.motion).forEach { pose ->
                val f = Figure.solve(pose)
                val b = f.bounds()
                assertTrue("${s.key} bottom ${b.bottom}", b.bottom in -0.01f..0.01f)
                assertTrue("${s.key} sane width ${b.width}", b.width < 260f)
                f.points.values.forEach { p -> assertTrue("${s.key} finite", p.x.isFinite() && p.y.isFinite()) }
            }
        }
    }

    @Test
    fun healthWordingAvoidsTierCClaims() {
        // docs/EVIDENCE.md §11: never claim these.
        val banned = listOf("prevent injur", "recovery", "posture", "lengthen", "release fascia", "toxin", "cure", "burn fat")
        all.forEach { s ->
            val text = (listOf(s.summary) + s.cues + s.cautions).joinToString(" ").lowercase()
            banned.forEach { assertTrue("${s.key} says '$it'", !text.contains(it)) }
        }
    }

    @Suppress("unused")
    private val bodyLoaded = Body.TORSO
}
