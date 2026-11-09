package dev.clean.stretch.plan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RomChecksTest {
    @Test
    fun checksAreWellFormed() {
        assertEquals(RomTests.all.size, RomTests.all.map { it.key }.toSet().size)
        RomTests.all.forEach {
            assertTrue(it.key, it.key.matches(Regex("[a-z_]+")))
            assertTrue(it.key, it.levels.size >= 3 && it.howTo.isNotEmpty())
            assertEquals(it.key, it.levels.size, it.pictures.size)
            // Every picture stands on the floor and keeps joints in a real range.
            it.pictures.forEach { p ->
                val f = dev.clean.stretch.figure.Figure.solve(p)
                assertTrue(it.key, f.bounds().bottom in -0.01f..0.01f)
                listOf(p.nearKnee, p.farKnee).forEach { k -> assertTrue("${it.key} knee $k", k in 0f..170f) }
                listOf(p.nearElbow, p.farElbow).forEach { e -> assertTrue("${it.key} elbow $e", e in 0f..160f) }
            }
        }
        // Retired keys must never be reused for a different check.
        listOf("shoulder_reach", "knee_to_wall", "deep_squat").forEach { assertTrue(it, RomTests.byKey(it) == null) }
    }

    @Test
    fun trendComparesFirstAndLatestPerSide() {
        val day = 86_400_000L
        val entries = listOf(
            RomEntry("toe_reach", "both", 2, 0),
            RomEntry("toe_reach", "both", 4, 30 * day),
            RomEntry("toe_reach", "both", 3, 10 * day),
            RomEntry("leg_raise", "left", 1, 0),
            RomEntry("leg_raise", "right", 2, 0),
        )
        val t = RomTrends.of(entries)
        assertEquals(2, t["toe_reach" to "both"]!!.change)
        assertEquals(3, t["toe_reach" to "both"]!!.count)
        assertEquals(0, t["leg_raise" to "left"]!!.change)
        // Never measured is absent, not zero.
        assertNull(t["overhead_reach" to "both"])
        assertEquals(5L, RomTrends.daysSinceLast(entries, 35 * day))
        assertNull(RomTrends.daysSinceLast(emptyList(), 0))
    }
}
