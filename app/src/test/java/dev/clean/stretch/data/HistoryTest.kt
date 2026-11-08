package dev.clean.stretch.data

import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.ui.SessionRecorder
import dev.clean.stretch.ui.Side
import dev.clean.stretch.ui.buildSteps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class HistoryTest {
    @Test
    fun stepRecordsRoundTrip() {
        val records = mapOf(1 to StepRecord(40_000L, false), 5 to StepRecord(12_345L, true))
        assertEquals(records, StepRecords.decode(StepRecords.encode(records)))
        assertTrue(StepRecords.decode("").isEmpty())
        assertTrue(StepRecords.decode("garbage;1:x:0").isEmpty())
    }

    @Test
    fun onlyRecordedHoldsBecomeRowsWithSnapshots() {
        val steps = buildSteps(listOf("standing_quad", "seated_forward_fold"), Side.Left)
        // steps: 0 setup L, 1 hold L, 2 setup R, 3 hold R, 4 setup fold, 5 hold fold
        val rows = SessionRecorder.stepRows(steps, mapOf(1 to StepRecord(40_000L, false), 3 to StepRecord(9_000L, true)))
        assertEquals(2, rows.size)
        assertEquals("left", rows[0].side)
        assertEquals("right", rows[1].side)
        assertEquals("Standing quad stretch", rows[0].stretchName)
        assertEquals(40, rows[0].actualSeconds)
        assertTrue(rows[1].skipped)
        assertEquals("Legs", rows[0].primaryRegions.split(",").map { Region.valueOf(it).label }.single())
    }

    @Test
    fun weeklyMinutesAreRecomputedPerRegion() {
        val quad = SampleCatalog.byKey("standing_quad")!!
        val steps = buildSteps(listOf(quad.key))
        val rows = SessionRecorder.stepRows(steps, mapOf(1 to StepRecord(60_000L, false), 3 to StepRecord(30_000L, false)))
        val minutes = Stats.minutesByRegion(rows)
        assertEquals(1.5, minutes[Region.Legs]!!, 1e-9)
        assertEquals(null, minutes[Region.Neck])
    }

    @Test
    fun weekStartsOnMonday() {
        assertEquals(LocalDate.of(2026, 11, 2), Stats.weekStart(LocalDate.of(2026, 11, 8)))
        assertEquals(LocalDate.of(2026, 11, 9), Stats.weekStart(LocalDate.of(2026, 11, 9)))
        val zone = ZoneId.of("UTC")
        val s = SessionEntity(startedAt = Stats.weekStartMillis(LocalDate.of(2026, 11, 8), zone), endedAt = 0, routineTitle = "x", completed = true)
        assertEquals(setOf(LocalDate.of(2026, 11, 2)), Stats.activeDays(listOf(s), zone))
    }
}
