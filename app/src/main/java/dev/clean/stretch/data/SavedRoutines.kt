package dev.clean.stretch.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A routine the user kept (PLAN §2 SavedRoutine). Deleting one never touches history,
 * because sessions are separate snapshots.
 */
@Entity(tableName = "saved_routines")
data class SavedRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** RoutineKind name. */
    val kind: String,
    val holdSeconds: Int,
    /** Comma-separated stretch keys, in order. */
    val keys: String,
    val createdAt: Long,
)

@Dao
interface SavedRoutineDao {
    @Query("SELECT * FROM saved_routines ORDER BY createdAt DESC")
    fun observe(): Flow<List<SavedRoutineEntity>>

    @Query("SELECT * FROM saved_routines ORDER BY id")
    suspend fun allNow(): List<SavedRoutineEntity>

    @Insert
    suspend fun insert(routine: SavedRoutineEntity): Long

    @Query("DELETE FROM saved_routines WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM saved_routines")
    suspend fun clear()

    @Insert
    suspend fun insertAll(routines: List<SavedRoutineEntity>)
}
