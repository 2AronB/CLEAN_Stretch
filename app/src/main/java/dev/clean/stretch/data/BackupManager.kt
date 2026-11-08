package dev.clean.stretch.data

import androidx.room.withTransaction
import dev.clean.stretch.settings.AppPreferences

/** Reads everything into a backup, and restores a validated backup (S7, R-009). */
class BackupManager(private val db: StretchDatabase, private val prefs: AppPreferences) {
    suspend fun export(now: Long): String = BackupCodec.encode(
        BackupData(
            sessions = db.history().allSessionsNow(),
            steps = db.history().allStepsNow(),
            profile = db.profile().getNow(),
            settings = prefs.exportSettings(),
        ),
        exportedAt = now,
    )

    /** Replaces history and profile in one database transaction, then settings. */
    suspend fun restore(data: BackupData) {
        db.withTransaction {
            db.history().replaceAll(data.sessions, data.steps)
            db.profile().clear()
            data.profile?.let { db.profile().save(it) }
        }
        prefs.importSettings(data.settings)
    }
}
