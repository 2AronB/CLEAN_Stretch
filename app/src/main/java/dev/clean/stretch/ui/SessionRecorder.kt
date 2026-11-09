package dev.clean.stretch.ui

import android.util.Log
import dev.clean.stretch.data.AppScope
import dev.clean.stretch.data.Checkpoint
import dev.clean.stretch.data.CheckpointStore
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.SessionEntity
import dev.clean.stretch.data.SessionStepEntity
import dev.clean.stretch.data.StepRecord
import dev.clean.stretch.data.StepRecords
import kotlinx.coroutines.launch

/** Pure conversion from a run's steps and records into the immutable rows that get saved. */
object SessionRecorder {
    fun stepRows(steps: List<Step>, records: Map<Int, StepRecord>): List<SessionStepEntity> =
        steps.withIndex()
            .filter { (i, step) -> !step.setup && records.containsKey(i) }
            .mapIndexed { position, (i, step) ->
                val r = records.getValue(i)
                SessionStepEntity(
                    position = position,
                    stretchKey = step.stretch.key,
                    stretchName = step.stretch.name,
                    side = when (step.side) { Side.Both -> "both"; Side.Left -> "left"; Side.Right -> "right" },
                    // Strengthen and rolling time counts toward minutes and streaks, not per-area stretch targets (P-5, P-10).
                    primaryRegions = if (step.stretch.kind.isStretch) step.stretch.primary.map { it.region.name }.distinct().joinToString(",") else "",
                    muscles = step.stretch.muscles.joinToString(",") { it.name },
                    plannedSeconds = (step.holdMs / 1000).toInt(),
                    actualSeconds = (r.heldMs / 1000).toInt(),
                    skipped = r.skipped,
                )
            }
}

/** Saves runs and unfinished checkpoints. Errors are logged and reported, never swallowed silently. */
class SessionSaver(private val history: HistoryDao, val checkpoints: CheckpointStore) {
    /** Last save problem, shown on Today so a failure is never hidden. */
    var lastError: String? = null
        private set

    fun save(title: String, steps: List<Step>, records: Map<Int, StepRecord>, startedAt: Long, endedAt: Long, completed: Boolean) {
        val rows = SessionRecorder.stepRows(steps, records)
        if (rows.none { it.actualSeconds > 0 }) {
            checkpoints.clear()
            return // nothing held: nothing to record
        }
        val session = SessionEntity(startedAt = startedAt, endedAt = endedAt, routineTitle = title, completed = completed)
        AppScope.launch {
            runCatching { history.save(session, rows) }
                .onSuccess { checkpoints.clear(); lastError = null }
                .onFailure {
                    // Keep the checkpoint so the run can be saved again later.
                    Log.e("StretchHistory", "Could not save session", it)
                    lastError = "Couldn't save your last routine (${it.javaClass.simpleName}). It's kept so you can try again."
                }
        }
    }

    /** Saves an interrupted run from its checkpoint as "ended early". */
    fun saveCheckpoint(c: Checkpoint) {
        val route = Route.decode(c.route) as? Route.Player ?: run { checkpoints.clear(); return }
        val steps = buildSteps(route.keys, route.firstSide, route.holdSeconds * 1000L)
        save(route.title, steps, StepRecords.decode(c.records), c.startedAt, c.savedAt, completed = false)
    }
}
