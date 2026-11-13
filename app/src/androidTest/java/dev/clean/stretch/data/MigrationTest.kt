package dev.clean.stretch.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.clean.stretch.catalog.Muscle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Database upgrades keep users' data (D-041). Builds a database exactly as an old app version
 * did (from the exported schema in app/schemas), adds real rows, then opens it with the current
 * app. Room runs the app's own migrations and checks every table matches the current schema
 * (it refuses to open otherwise), and the rows are read back through the app's DAOs.
 *
 * Uses its own database file ("migration-test"), never the app's "stretch.db".
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val name = "migration-test"

    private val target = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun clean() {
        target.deleteDatabase(name)
    }

    /** Creates the database as app version [version] left it, then runs [rows] on it. */
    private fun createAt(version: Int, rows: SQLiteDatabase.() -> Unit) {
        val json = InstrumentationRegistry.getInstrumentation().context.assets
            .open("dev.clean.stretch.data.StretchDatabase/$version.json").bufferedReader().use { it.readText() }
        val schema = JSONObject(json).getJSONObject("database")
        val db = SQLiteDatabase.openOrCreateDatabase(target.getDatabasePath(name).apply { parentFile?.mkdirs() }, null)
        val entities = schema.getJSONArray("entities")
        for (i in 0 until entities.length()) {
            val e = entities.getJSONObject(i)
            val table = e.getString("tableName")
            db.execSQL(e.getString("createSql").replace("\${TABLE_NAME}", table))
            val indices = e.optJSONArray("indices") ?: continue
            for (j in 0 until indices.length()) db.execSQL(indices.getJSONObject(j).getString("createSql").replace("\${TABLE_NAME}", table))
        }
        val setup = schema.getJSONArray("setupQueries")
        for (i in 0 until setup.length()) db.execSQL(setup.getString(i))
        db.version = version
        db.rows()
        db.close()
    }

    private fun open(): StretchDatabase =
        Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, StretchDatabase::class.java, name).build()

    @Test
    fun historyFromTheFirstVersionSurvivesEveryUpgrade() {
        createAt(1) {
            execSQL("INSERT INTO sessions (id, startedAt, endedAt, routineTitle, completed) VALUES (1, 1000, 2000, 'Full-body stretch', 1)")
            execSQL(
                "INSERT INTO session_steps (id, sessionId, position, stretchKey, stretchName, side, primaryRegions, muscles, plannedSeconds, actualSeconds, skipped) " +
                    "VALUES (1, 1, 0, 'standing_quad', 'Standing quad stretch', 'right', 'Legs', 'Quadriceps,HipFlexors', 40, 40, 0)",
            )
        }

        val db = open()
        try {
            val sessions = runBlocking { db.history().allSessionsNow() }
            val steps = runBlocking { db.history().allStepsNow() }
            assertEquals(listOf("Full-body stretch"), sessions.map { it.routineTitle })
            assertEquals(1, steps.size)
            assertEquals("", steps.single().primaryMuscles) // filled in later from the move's tags
            // The weekly numbers still count it, on the right side only.
            val quads = Stats.minutesByMuscle(steps).getValue(Muscle.Quadriceps)
            assertEquals(40 / 60.0, quads.right, 1e-9)
            assertEquals(0.0, quads.left, 1e-9)
        } finally {
            db.close()
        }
    }

    @Test
    fun profileAndChecksFromVersion5SurviveTheUpgrade() {
        createAt(5) {
            execSQL(
                "INSERT INTO profile (id, goal, olderAdult, focus, avoid, noFloor, noKneeling, hasWall, conditions, minutes, setupDone, updatedAt, hasRoller, strengthen) " +
                    "VALUES (1, 'Runner', 0, 'Legs', 'Neck', 0, 1, 1, '', 15, 1, 3000, 1, NULL)",
            )
            execSQL("INSERT INTO rom_checks (id, testKey, side, level, takenAt) VALUES (1, 'toe_reach', 'both', 3, 4000)")
            execSQL("INSERT INTO saved_routines (id, name, kind, holdSeconds, keys, createdAt) VALUES (1, 'Mine', 'Full', 40, 'cobra,butterfly', 5000)")
        }

        val db = open()
        try {
            val p = runBlocking { db.profile().profile().first() }
            assertEquals(setOf(dev.clean.stretch.catalog.Region.Legs), p.focus)
            assertEquals(setOf(dev.clean.stretch.catalog.Region.Neck), p.avoid)
            assertEquals(15, p.minutes)
            assertEquals(true, p.noKneeling && p.hasRoller && p.setupDone)
            // New in v6: no secondary areas yet, recommended dose.
            assertEquals(emptySet<dev.clean.stretch.catalog.Region>(), p.secondary)
            assertEquals(10, p.weeklyDose)
            assertEquals(3, runBlocking { db.romChecks().allNow() }.single().level)
            assertEquals("cobra,butterfly", runBlocking { db.routines().allNow() }.single().keys)
        } finally {
            db.close()
        }
    }
}
