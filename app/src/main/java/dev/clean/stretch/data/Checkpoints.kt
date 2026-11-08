package dev.clean.stretch.data

import android.content.Context
import androidx.core.content.edit

/**
 * Where an unfinished routine got to, written at every step boundary (docs/PLAN.md §3.1).
 * If Android closes the app mid-routine, Today offers Resume or "Save what I did".
 *
 * [records] maps step index to the time held and whether it was skipped; see [StepRecords].
 */
data class Checkpoint(
    val route: String,
    val index: Int,
    val startedAt: Long,
    val savedAt: Long,
    val records: String,
)

class CheckpointStore(context: Context) {
    private val prefs = context.getSharedPreferences("runner", Context.MODE_PRIVATE)

    /** Set by Today's Resume button; read once by the player. Memory only. */
    var resumeNext: Boolean = false

    fun load(): Checkpoint? {
        val route = prefs.getString(KEY_ROUTE, null) ?: return null
        return Checkpoint(
            route = route,
            index = prefs.getInt(KEY_INDEX, 0),
            startedAt = prefs.getLong(KEY_STARTED, 0L),
            savedAt = prefs.getLong(KEY_SAVED, 0L),
            records = prefs.getString(KEY_RECORDS, "") ?: "",
        )
    }

    fun save(c: Checkpoint) {
        prefs.edit {
            putString(KEY_ROUTE, c.route)
            putInt(KEY_INDEX, c.index)
            putLong(KEY_STARTED, c.startedAt)
            putLong(KEY_SAVED, c.savedAt)
            putString(KEY_RECORDS, c.records)
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    private companion object {
        const val KEY_ROUTE = "route"
        const val KEY_INDEX = "index"
        const val KEY_STARTED = "started_at"
        const val KEY_SAVED = "saved_at"
        const val KEY_RECORDS = "records"
    }
}

/** What happened to one hold step. */
data class StepRecord(val heldMs: Long, val skipped: Boolean)

/** Text form of step records ("index:ms:skipped;…") for checkpoints and saved UI state. */
object StepRecords {
    fun encode(records: Map<Int, StepRecord>): String =
        records.entries.sortedBy { it.key }.joinToString(";") { (i, r) -> "$i:${r.heldMs}:${if (r.skipped) 1 else 0}" }

    fun decode(text: String): Map<Int, StepRecord> = text.split(";").mapNotNull { part ->
        val bits = part.split(":")
        if (bits.size != 3) return@mapNotNull null
        val i = bits[0].toIntOrNull() ?: return@mapNotNull null
        val ms = bits[1].toLongOrNull() ?: return@mapNotNull null
        i to StepRecord(ms.coerceAtLeast(0L), bits[2] == "1")
    }.toMap()
}
