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
        }
    }

    @Test
    fun trendComparesFirstAndLatestPerSide() {
        val day = 86_400_000L
        val entries = listOf(
            RomEntry("toe_reach", "both", 2, 0),
            RomEntry("toe_reach", "both", 4, 30 * day),
            RomEntry("toe_reach", "both", 3, 10 * day),
            RomEntry("shoulder_reach", "left", 1, 0),
            RomEntry("shoulder_reach", "right", 2, 0),
        )
        val t = RomTrends.of(entries)
        assertEquals(2, t["toe_reach" to "both"]!!.change)
        assertEquals(3, t["toe_reach" to "both"]!!.count)
        assertEquals(0, t["shoulder_reach" to "left"]!!.change)
        // Never measured is absent, not zero.
        assertNull(t["deep_squat" to "both"])
        assertEquals(5L, RomTrends.daysSinceLast(entries, 35 * day))
        assertNull(RomTrends.daysSinceLast(emptyList(), 0))
    }
}
