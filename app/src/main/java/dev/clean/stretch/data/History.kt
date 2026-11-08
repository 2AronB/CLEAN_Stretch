package dev.clean.stretch.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow

/**
 * One finished or ended-early routine. **Immutable snapshot** (docs/PLAN.md §2): rows are only
 * ever inserted, never updated, so later catalog edits can't change history. Weekly totals are
 * recomputed from these rows every time ([Stats]); nothing derived is stored.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Epoch milliseconds. A session counts on the local date it started. */
    val startedAt: Long,
    val endedAt: Long,
    val routineTitle: String,
    /** False when the user ended early or it was saved from an interrupted run. */
    val completed: Boolean,
)

/** One hold inside a session, with the stretch's name and target areas copied at the time. */
@Entity(
    tableName = "session_steps",
    foreignKeys = [ForeignKey(entity = SessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sessionId")],
)
data class SessionStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long = 0,
    val position: Int,
    val stretchKey: String,
    /** Name snapshot. */
    val stretchName: String,
    /** "both", "left" or "right". */
    val side: String,
    /** Comma-separated Region names of the stretch's primary muscles, snapshot. */
    val primaryRegions: String,
    /** Comma-separated Muscle names (primary then secondary), snapshot. */
    val muscles: String,
    val plannedSeconds: Int,
    val actualSeconds: Int,
    val skipped: Boolean,
)

@Dao
abstract class HistoryDao {
    @Insert protected abstract suspend fun insertSession(session: SessionEntity): Long
    @Insert protected abstract suspend fun insertSteps(steps: List<SessionStepEntity>)

    /** Saves a session and its steps together, or not at all. */
    @Transaction
    open suspend fun save(session: SessionEntity, steps: List<SessionStepEntity>): Long {
        val id = insertSession(session)
        insertSteps(steps.map { it.copy(sessionId = id) })
        return id
    }

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC LIMIT :limit")
    abstract fun recentSessions(limit: Int): Flow<List<SessionEntity>>

    @Query("SELECT * FROM session_steps WHERE sessionId IN (SELECT id FROM sessions WHERE startedAt >= :fromMs)")
    abstract fun stepsSince(fromMs: Long): Flow<List<SessionStepEntity>>

    @Query("SELECT * FROM sessions ORDER BY id")
    abstract suspend fun allSessionsNow(): List<SessionEntity>

    @Query("SELECT * FROM session_steps ORDER BY id")
    abstract suspend fun allStepsNow(): List<SessionStepEntity>

    @Query("DELETE FROM sessions")
    protected abstract suspend fun deleteAllSessions()

    @Insert protected abstract suspend fun insertSessionsExact(sessions: List<SessionEntity>)
    @Insert protected abstract suspend fun insertStepsExact(steps: List<SessionStepEntity>)

    /**
     * Restore only (S7): replaces all history with a validated backup in one transaction, so a
     * failure leaves the old history untouched. Steps go with their sessions (cascade).
     */
    @Transaction
    open suspend fun replaceAll(sessions: List<SessionEntity>, steps: List<SessionStepEntity>) {
        deleteAllSessions()
        insertSessionsExact(sessions)
        insertStepsExact(steps)
    }

    @Query("SELECT startedAt FROM sessions")
    abstract fun startTimes(): Flow<List<Long>>

    @Query("SELECT * FROM session_steps")
    abstract fun allSteps(): Flow<List<SessionStepEntity>>
}

/** v1: sessions. v2: adds the profile table (automatic migration; no existing data changes). */
@Database(
    entities = [SessionEntity::class, SessionStepEntity::class, ProfileEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
abstract class StretchDatabase : RoomDatabase() {
    abstract fun history(): HistoryDao
    abstract fun profile(): ProfileDao

    companion object {
        @Volatile private var instance: StretchDatabase? = null

        // No destructive fallback: a future schema change must ship a real migration (never reset user data).
        fun get(context: Context): StretchDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, StretchDatabase::class.java, "stretch.db")
                .build()
                .also { instance = it }
        }
    }
}

/** Outlives screens, so a save started while leaving the player still finishes. */
object AppScope : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO)
