package dev.clean.stretch.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.clean.stretch.plan.RomEntry
import kotlinx.coroutines.flow.Flow

/**
 * One self-check answer (S9). Insert-only, like session history. A check the user skipped has
 * no row, so "not measured" never looks like a low score.
 */
@Entity(tableName = "rom_checks", indices = [Index("testKey")])
data class RomCheckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testKey: String,
    /** "both", "left" or "right". */
    val side: String,
    /** Index into the test's levels, least range first. */
    val level: Int,
    val takenAt: Long,
) {
    fun toEntry() = RomEntry(testKey, side, level, takenAt)
}

@Dao
interface RomCheckDao {
    @Query("SELECT * FROM rom_checks ORDER BY takenAt")
    fun observe(): Flow<List<RomCheckEntity>>

    @Query("SELECT * FROM rom_checks ORDER BY id")
    suspend fun allNow(): List<RomCheckEntity>

    @Insert
    suspend fun insertAll(checks: List<RomCheckEntity>)

    @Query("DELETE FROM rom_checks")
    suspend fun clear()
}
