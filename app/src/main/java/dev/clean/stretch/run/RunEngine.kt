package dev.clean.stretch.run

import dev.clean.stretch.data.StepRecord
import dev.clean.stretch.ui.Step
import kotlin.math.ceil

/** Something the runner should say, play or do. The engine decides; [RunSession] performs it. */
sealed interface RunEvent {
    /** A step began. [buzz] is false only for the very first step of a run. */
    data class StepStarted(val index: Int, val buzz: Boolean) : RunEvent
    /** A hold ran to its end (chime). */
    data object HoldDone : RunEvent
    /** 3-2-1 tick near the end of a hold. */
    data class Countdown(val secondsLeft: Int) : RunEvent
    /** Six seconds left: say what's next ([index] is the next setup step). */
    data class AnnounceNext(val index: Int) : RunEvent
    data object Finished : RunEvent
}

/**
 * The routine timer as a plain state machine (docs/PLAN.md §3.1). Time comes from a clock the
 * caller passes in (`SystemClock.elapsedRealtime()` in the app), never from screen frames, so a run
 * keeps correct time with the screen off and pausing is the only thing that stops the clock.
 *
 * [records] maps hold-step index to how long it was held; only hold steps are recorded.
 */
class RunEngine(val steps: List<Step>, startIndex: Int, records: Map<Int, StepRecord>, now: Long) {
    init { require(steps.isNotEmpty()) { "A run needs at least one step" } }

    var index = startIndex.coerceIn(0, steps.lastIndex)
        private set
    private val held = records.toMutableMap()
    val records: Map<Int, StepRecord> get() = held
    var extraMs = 0L
        private set
    var paused = false
        private set
    var finished = false
        private set

    /** Time already counted in this step, plus the running stretch since [runningSince]. */
    private var accumulated = 0L
    private var runningSince = now
    private var lastSecondsLeft = secondsLeft(now)

    val step: Step get() = steps[index]
    val durationMs: Long get() = step.baseMs + extraMs

    fun elapsed(now: Long): Long =
        if (paused || finished) accumulated else accumulated + (now - runningSince).coerceAtLeast(0L)

    fun secondsLeft(now: Long): Int = ceil((durationMs - elapsed(now)).coerceAtLeast(0L) / 1000.0).toInt()

    /** Events for the first step. Call once when the run starts. */
    fun start(): List<RunEvent> = listOf(RunEvent.StepStarted(index, buzz = false))

    /** Call often (a few times a second). Moves on when a step's time is up. */
    fun tick(now: Long): List<RunEvent> {
        if (paused || finished) return emptyList()
        // Recorded time is capped at the step length, so a late tick never over-counts.
        if (elapsed(now) >= durationMs) return advance(index + 1, now, heldMs = durationMs)
        if (step.setup) return emptyList()
        val left = secondsLeft(now)
        if (left == lastSecondsLeft) return emptyList()
        lastSecondsLeft = left
        val events = mutableListOf<RunEvent>()
        if (left in 1..3) events += RunEvent.Countdown(left)
        if (left == 6) nextSetupIndex()?.let { events += RunEvent.AnnounceNext(it) }
        return events
    }

    /** Index of the next "get into position" step, i.e. the next new stretch or side. */
    fun nextSetupIndex(): Int? = (index + 1..steps.lastIndex).firstOrNull { steps[it].setup }

    /** "I'm ready": start the hold now. */
    fun ready(now: Long): List<RunEvent> = if (step.setup && !finished) advance(index + 1, now) else emptyList()

    /** Skip: from setup, skip the whole stretch-and-side; from a hold, record it as skipped. */
    fun skip(now: Long): List<RunEvent> = when {
        finished -> emptyList()
        step.setup -> advance(index + 2, now)
        else -> advance(index + 1, now, skipped = true)
    }

    /** Back: restart this step, or within its first 3 s go to the previous stretch's setup. */
    fun back(now: Long): List<RunEvent> = when {
        finished -> emptyList()
        elapsed(now) > 3_000L || index == 0 -> advance(index, now)
        else -> advance(index - 2, now)
    }

    /** "+15 s" on a hold. */
    fun addTime() {
        if (!step.setup && !finished) extraMs += 15_000L
    }

    fun togglePause(now: Long) {
        if (finished) return
        if (paused) {
            runningSince = now
            paused = false
        } else {
            accumulated = elapsed(now)
            paused = true
        }
    }

    /** Ending early: a hold in progress counts as partly done (skipped). */
    fun endEarly(now: Long) {
        if (finished) return
        val t = elapsed(now)
        if (!step.setup && t > 0L) held[index] = StepRecord(t, skipped = true)
    }

    private fun advance(next: Int, now: Long, skipped: Boolean = false, heldMs: Long? = null): List<RunEvent> {
        val events = mutableListOf<RunEvent>()
        if (next > index && !step.setup) {
            held[index] = StepRecord(heldMs ?: elapsed(now), skipped)
            if (!skipped) events += RunEvent.HoldDone
        }
        if (next >= steps.size) {
            accumulated = elapsed(now)
            finished = true
            events += RunEvent.Finished
            return events
        }
        index = next.coerceAtLeast(0)
        accumulated = 0L
        runningSince = now
        extraMs = 0L
        lastSecondsLeft = secondsLeft(now)
        events += RunEvent.StepStarted(index, buzz = true)
        return events
    }
}
