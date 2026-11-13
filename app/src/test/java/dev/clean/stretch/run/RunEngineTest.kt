package dev.clean.stretch.run

import dev.clean.stretch.data.StepRecord
import dev.clean.stretch.ui.HOLD_MS
import dev.clean.stretch.ui.SETUP_FIRST_MS
import dev.clean.stretch.ui.SETUP_NEW_POSITION_MS
import dev.clean.stretch.ui.buildSteps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunEngineTest {
    // 0 child's pose setup, 1 hold, 2 standing quad (right) setup, 3 hold, 4 left setup, 5 hold
    private val steps = buildSteps(listOf("childs_pose", "standing_quad"))
    private fun engine(now: Long = 0L) = RunEngine(steps, 0, emptyMap(), now)

    @Test
    fun setupThenHoldAdvanceByTheClock() {
        val e = engine()
        assertEquals(listOf(RunEvent.StepStarted(0, buzz = false)), e.start())
        assertTrue(e.tick(SETUP_FIRST_MS - 1).isEmpty())
        assertEquals(listOf(RunEvent.StepStarted(1, buzz = true)), e.tick(SETUP_FIRST_MS))
        val end = SETUP_FIRST_MS + HOLD_MS
        assertEquals(listOf(RunEvent.HoldDone, RunEvent.StepStarted(2, buzz = true)), e.tick(end))
        assertEquals(StepRecord(HOLD_MS, skipped = false), e.records[1])
    }

    @Test
    fun aLateTickNeverCountsMoreThanTheHold() {
        // Screen off and the phone slow to wake: the next tick comes 5 minutes late.
        val e = engine()
        e.ready(0L)
        e.tick(300_000L)
        assertEquals(HOLD_MS, e.records.getValue(1).heldMs)
        assertEquals(2, e.index)
    }

    @Test
    fun pausedTimeIsNotCounted() {
        val e = engine()
        e.ready(0L)
        e.togglePause(10_000L)
        assertEquals(10_000L, e.elapsed(500_000L))
        assertTrue(e.tick(500_000L).isEmpty())
        e.togglePause(500_000L)
        assertEquals(15_000L, e.elapsed(505_000L))
        e.tick(500_000L + HOLD_MS - 10_000L)
        assertEquals(2, e.index)
    }

    @Test
    fun countdownAndNextAreAnnouncedOnce() {
        val e = engine()
        e.ready(0L)
        val events = (0L..HOLD_MS - 1 step 100L).flatMap { e.tick(it) }
        assertEquals(
            listOf(RunEvent.AnnounceNext(2), RunEvent.Countdown(3), RunEvent.Countdown(2), RunEvent.Countdown(1)),
            events,
        )
    }

    @Test
    fun skipFromSetupSkipsTheWholeStretchWithoutARecord() {
        val e = engine()
        e.skip(1_000L)
        assertEquals(2, e.index)
        assertFalse(e.records.containsKey(1))
    }

    @Test
    fun skipFromHoldRecordsItAsSkipped() {
        val e = engine()
        e.ready(0L)
        e.skip(12_000L)
        assertEquals(StepRecord(12_000L, skipped = true), e.records[1])
        assertEquals(2, e.index)
    }

    @Test
    fun backRestartsOrGoesToThePreviousStretch() {
        val e = engine()
        e.skip(0L) // to standing quad setup (2)
        e.ready(0L) // hold (3)
        e.back(5_000L)
        assertEquals("after 3 s, back restarts the step", 3, e.index)
        assertEquals(0L, e.elapsed(5_000L))
        e.back(6_000L)
        assertEquals("within 3 s, back goes to the previous setup", 1, e.index)
    }

    @Test
    fun extraTimeExtendsTheHold() {
        val e = engine()
        e.ready(0L)
        e.addTime()
        assertTrue(e.tick(HOLD_MS).isEmpty())
        assertEquals(2, e.tick(HOLD_MS + 15_000L).filterIsInstance<RunEvent.StepStarted>().single().index)
        assertEquals(HOLD_MS + 15_000L, e.records.getValue(1).heldMs)
    }

    @Test
    fun lastHoldFinishesTheRun() {
        val e = RunEngine(steps, 5, emptyMap(), 0L)
        assertEquals(listOf(RunEvent.HoldDone, RunEvent.Finished), e.tick(HOLD_MS))
        assertTrue(e.finished)
        assertTrue(e.tick(HOLD_MS * 2).isEmpty())
    }

    @Test
    fun endingEarlyKeepsAPartialHold() {
        val e = engine()
        e.ready(0L)
        e.endEarly(7_000L)
        assertEquals(StepRecord(7_000L, skipped = true), e.records[1])
    }

    @Test
    fun resumeStartsAtTheSavedStepWithItsRecords() {
        val saved = mapOf(1 to StepRecord(HOLD_MS, false))
        val e = RunEngine(steps, 2, saved, 0L)
        assertEquals(2, e.index)
        assertEquals(SETUP_NEW_POSITION_MS, e.durationMs)
        assertEquals(saved, e.records)
    }
}
