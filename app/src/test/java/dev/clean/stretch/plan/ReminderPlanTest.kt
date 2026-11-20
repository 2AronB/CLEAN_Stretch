package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Region
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderPlanTest {
    private val noon = ReminderPlan(id = 2, time = 12 * 60, kind = RoutineKind.Quick, areas = setOf(Region.Shoulders, Region.Neck), roller = false, strengthen = null)

    @Test
    fun savedTextReadsBackTheSame() {
        val evening = ReminderPlan(id = 1, time = 19 * 60 + 30, kind = RoutineKind.CoolDown, roller = true, strengthen = false)
        val text = ReminderPlan.encodeAll(listOf(noon, evening))
        assertEquals(listOf(noon, evening), ReminderPlan.decodeAll(text)) // sorted by time
    }

    @Test
    fun unreadableLinesAreDroppedNotGuessed() {
        val text = listOf("garbage", "4;600;Full;;;", "1;9999;Full;;;", noon.encode(), "2;300;Full;;;").joinToString("\n")
        assertEquals(listOf(noon), ReminderPlan.decodeAll(text)) // bad id, bad time, repeated id
        assertTrue(ReminderPlan.decodeAll(null).isEmpty())
    }

    @Test
    fun atMostThreeAndIdsAreReused() {
        val three = (1..3).map { ReminderPlan(it, it * 60) }
        assertNull(ReminderPlan.nextId(three))
        assertEquals(2, ReminderPlan.nextId(three.filter { it.id != 2 }))
    }

    @Test
    fun labelsAndChoices() {
        assertEquals("Quick break · neck and shoulders & chest", noon.label)
        assertEquals("Quick break: neck and shoulders & chest", noon.title)
        val p = Profile(hasRoller = true, strengthen = true)
        assertEquals(false, noon.applyTo(p).hasRoller)
        assertEquals(true, noon.applyTo(p).strengthen) // null follows the Today card
    }
}
