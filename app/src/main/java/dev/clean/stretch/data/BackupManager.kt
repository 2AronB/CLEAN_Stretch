package dev.clean.stretch.data

import androidx.room.withTransaction
import dev.clean.stretch.settings.AppPreferences

/** Reads everything into a backup, and restores a validated backup (S7, R-009). */
class BackupManager(private val db: StretchDatabase, private val prefs: AppPreferences, private val checkpoints: CheckpointStore) {
    suspend fun export(now: Long): String = BackupCodec.encode(
        BackupData(
            sessions = db.history().allSessionsNow(),
            steps = db.history().allStepsNow(),
            profile = db.profile().getNow(),
            settings = prefs.exportSettings(),
            routines = db.routines().allNow(),
            romChecks = db.romChecks().allNow(),
        ),
        exportedAt = now,
    )

    /**
     * "Delete my data" (Play readiness): erases history, profile, saved routines and check-ins in
     * one transaction, then settings and any unfinished routine. Only ever called after the user
     * confirms in Settings; the app returns to its first-run state.
     */
    suspend fun deleteEverything() {
        db.withTransaction {
            db.history().replaceAll(emptyList(), emptyList())
            db.profile().clear()
            db.routines().clear()
            db.romChecks().clear()
        }
        checkpoints.clear()
        prefs.clearAll()
    }

    /** Replaces history and profile in one database transaction, then settings. */
    suspend fun restore(data: BackupData) {
        db.withTransaction {
            db.history().replaceAll(data.sessions, data.steps)
            db.profile().clear()
            data.profile?.let { db.profile().save(it) }
            db.routines().clear()
            db.routines().insertAll(data.routines)
            db.romChecks().clear()
            db.romChecks().insertAll(data.romChecks)
        }
        prefs.importSettings(data.settings)
    }
}
