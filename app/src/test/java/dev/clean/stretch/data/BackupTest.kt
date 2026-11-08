package dev.clean.stretch.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BackupTest {
    private val sample = BackupData(
        sessions = listOf(
            SessionEntity(id = 1, startedAt = 1_000L, endedAt = 61_000L, routineTitle = "Full \"body\"\nstretch ✓", completed = true),
            SessionEntity(id = 2, startedAt = 2_000L, endedAt = 3_000L, routineTitle = "Quick", completed = false),
        ),
        steps = listOf(
            SessionStepEntity(id = 10, sessionId = 1, position = 0, stretchKey = "cat_cow", stretchName = "Cat–cow", side = "both",
                primaryRegions = "Back", muscles = "UpperBack,LowerBack", plannedSeconds = 40, actualSeconds = 40, skipped = false),
        ),
        profile = ProfileEntity(goal = "Desk", olderAdult = true, focus = "Neck,Back", avoid = "", noFloor = true, noKneeling = false,
            hasWall = true, conditions = "Pregnancy", minutes = 10, setupDone = true, updatedAt = 5L),
        settings = mapOf("theme_mode" to "2", "cue_voice" to "false"),
    )

    @Test
    fun roundTripKeepsEverything() {
        assertEquals(sample, BackupCodec.decode(BackupCodec.encode(sample, exportedAt = 9L)))
    }

    @Test
    fun emptyBackupRoundTrips() {
        val empty = BackupData(emptyList(), emptyList(), null, emptyMap())
        assertEquals(empty, BackupCodec.decode(BackupCodec.encode(empty, 0L)))
    }

    private fun refused(text: String, containing: String) {
        try {
            BackupCodec.decode(text)
            fail("should refuse")
        } catch (e: BackupException) {
            assertTrue(e.message!!, e.message!!.contains(containing))
        }
    }

    @Test
    fun newerCorruptAndForeignFilesAreRefused() {
        val good = BackupCodec.encode(sample, 0L)
        refused(good.replace("\"version\":1", "\"version\":2"), "newer version")
        refused(good.dropLast(10), "readable")
        refused("{\"format\":\"something-else\"}", "isn't a Stretch backup")
        refused(good.replace("\"sessionId\":1", "\"sessionId\":99"), "missing session")
        refused(good.replace("\"routineTitle\":\"Quick\",", ""), "routineTitle")
    }
}
