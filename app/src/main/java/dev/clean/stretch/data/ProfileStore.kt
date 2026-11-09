package dev.clean.stretch.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.plan.Condition
import dev.clean.stretch.plan.Goal
import dev.clean.stretch.plan.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** The single saved profile row (id = 1). Sets are stored as comma-separated enum names. */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val goal: String,
    val olderAdult: Boolean,
    val focus: String,
    val avoid: String,
    val noFloor: Boolean,
    val noKneeling: Boolean,
    val hasWall: Boolean,
    val conditions: String,
    val minutes: Int,
    val setupDone: Boolean,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "0") val hasRoller: Boolean = false,
    /** 1 = on, 0 = off, null = automatic. */
    val strengthen: Boolean? = null,
)

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    fun observe(): Flow<ProfileEntity?>

    @Upsert
    suspend fun save(profile: ProfileEntity)

    @Query("SELECT * FROM profile WHERE id = 1")
    suspend fun getNow(): ProfileEntity?

    @Query("DELETE FROM profile")
    suspend fun clear()
}

object ProfileMapper {
    private inline fun <reified E : Enum<E>> decodeSet(text: String): Set<E> =
        text.split(",").mapNotNull { name -> enumValues<E>().firstOrNull { it.name == name } }.toSet()

    fun toProfile(e: ProfileEntity) = Profile(
        goal = Goal.entries.firstOrNull { it.name == e.goal } ?: Goal.General,
        olderAdult = e.olderAdult,
        focus = decodeSet<Region>(e.focus),
        avoid = decodeSet<Region>(e.avoid),
        noFloor = e.noFloor,
        noKneeling = e.noKneeling,
        hasWall = e.hasWall,
        conditions = decodeSet<Condition>(e.conditions),
        minutes = e.minutes,
        setupDone = e.setupDone,
        hasRoller = e.hasRoller,
        strengthen = e.strengthen,
    )

    fun toEntity(p: Profile, now: Long) = ProfileEntity(
        goal = p.goal.name,
        olderAdult = p.olderAdult,
        focus = p.focus.joinToString(",") { it.name },
        avoid = p.avoid.joinToString(",") { it.name },
        noFloor = p.noFloor,
        noKneeling = p.noKneeling,
        hasWall = p.hasWall,
        conditions = p.conditions.joinToString(",") { it.name },
        minutes = p.minutes,
        setupDone = p.setupDone,
        updatedAt = now,
        hasRoller = p.hasRoller,
        strengthen = p.strengthen,
    )
}

fun ProfileDao.profile(): Flow<Profile> = observe().map { it?.let(ProfileMapper::toProfile) ?: Profile() }
