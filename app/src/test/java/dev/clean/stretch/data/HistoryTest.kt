package dev.clean.stretch.data

import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.Muscle
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

    // D-039: minutes are per muscle per side; a both-sides move counts for each side; rows saved
    // before v6 (no main muscles stored) fall back to the move's tags.
    @Test
    fun weeklyMinutesArePerMusclePerSide() {
        val steps = buildSteps(listOf("standing_quad", "cobra"))
        // 0 quad setup (right), 1 quad hold right, 2 setup left, 3 hold left, 4 cobra setup, 5 cobra hold
        val rows = SessionRecorder.stepRows(steps, mapOf(1 to StepRecord(60_000L, false), 3 to StepRecord(30_000L, false), 5 to StepRecord(40_000L, false)))
        val m = Stats.minutesByMuscle(rows)
        assertEquals(1.0, m.getValue(Muscle.Quadriceps).right, 1e-9)
        assertEquals(0.5, m.getValue(Muscle.Quadriceps).left, 1e-9)
        assertEquals(0.5, m.getValue(Muscle.Quadriceps).weaker, 1e-9)
        val abs = m.getValue(Muscle.Abdominals)
        assertEquals(40 / 60.0, abs.left, 1e-9)
        assertEquals(40 / 60.0, abs.right, 1e-9)
        val old = rows.map { it.copy(primaryMuscles = "") }
        assertEquals(m, Stats.minutesByMuscle(old))
    }

    // Owner 2026-11-14: a day fills toward its goal; extra minutes later fill earlier unfinished days.
    @Test
    fun daysFillTowardTheGoalAndCanBeCaughtUp() {
        val mon = LocalDate.of(2026, 11, 9)
        val fill = Stats.weekFill(mon, mapOf(mon to 4.0), dayGoal = 8.0)
        assertEquals(0.5f, fill[0], 1e-6f)
        // Wednesday: 20 min fills Wednesday, then catches up Monday (4 more), then Tuesday (8).
        val caught = Stats.weekFill(mon, mapOf(mon to 4.0, mon.plusDays(2) to 20.0), dayGoal = 8.0)
        assertEquals(listOf(1f, 1f, 1f, 0f, 0f, 0f, 0f), caught)
        assertEquals(3, Stats.daysDone(mon, mapOf(mon to 4.0, mon.plusDays(2) to 20.0), 8.0))
        // Extra minutes never fill later days.
        assertEquals(0f, Stats.weekFill(mon, mapOf(mon to 30.0), 8.0)[1], 0f)
        // Streak counts weeks with enough full days.
        val lastWeek = mon.minusWeeks(1)
        val minutes = (0L..2L).associate { lastWeek.plusDays(it) to 8.0 }
        assertEquals(1, Stats.weekStreakByGoal(minutes, mon.plusDays(3), daysTarget = 3, dayGoal = 8.0))
    }

    @Test
    fun weekStartsOnMonday() {
        assertEquals(LocalDate.of(2026, 11, 2), Stats.weekStart(LocalDate.of(2026, 11, 8)))
        assertEquals(LocalDate.of(2026, 11, 9), Stats.weekStart(LocalDate.of(2026, 11, 9)))
        val zone = ZoneId.of("UTC")
        val s = SessionEntity(startedAt = Stats.weekStartMillis(LocalDate.of(2026, 11, 8), zone), endedAt = 0, routineTitle = "x", completed = true)
        assertEquals(setOf(LocalDate.of(2026, 11, 2)), Stats.activeDays(listOf(s), zone))
    }

    @Test
    fun weekStreakCountsWeeksThatHitTheTarget() {
        val today = LocalDate.of(2026, 11, 11) // Wednesday
        fun d(day: Int, month: Int = 11) = LocalDate.of(2026, month, day)
        // This week: 1 day (target 2 not yet met). Last week: 2 days. Week before: 3. Before that: 1.
        val days = setOf(d(9), d(3), d(5), d(26, 10), d(28, 10), d(30, 10), d(20, 10))
        assertEquals(2, Stats.weekStreak(days, today, daysTarget = 2))
        assertEquals(3, Stats.weekStreak(days + d(10), today, daysTarget = 2))
        assertEquals(0, Stats.weekStreak(emptySet(), today, daysTarget = 2))
    }
}
