package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Equipment
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.SampleCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryFilterTest {
    private val all = SampleCatalog.all
    private fun keys(f: LibraryFilter, p: Profile? = null) = f.apply(all, p).map { it.key }

    @Test
    fun discoverFoamRollerMoves() {
        val byEquipment = keys(LibraryFilter(equipment = setOf(Equipment.FoamRoller)))
        val byType = keys(LibraryFilter(types = setOf(MoveType.Rolling)))
        assertEquals(setOf("roll_upper_back", "roll_calves", "roll_quads"), byEquipment.toSet())
        assertEquals(byEquipment.toSet(), byType.toSet())
    }

    @Test
    fun easierHamstringStretchesMainTargetsFirst() {
        val f = LibraryFilter(muscles = setOf(Muscle.Hamstrings), levels = setOf(LevelFilter.Easier))
        val result = f.apply(all, null)
        val k = result.map { it.key }
        assertTrue(k.containsAll(listOf("lying_hamstring", "standing_hamstring", "seated_forward_fold")))
        assertTrue("level 2 excluded", "downward_dog" !in k)
        // Moves that mainly target hamstrings come before ones that only touch them.
        val firstSecondary = result.indexOfFirst { Muscle.Hamstrings !in it.primary }
        if (firstSecondary >= 0) assertTrue(result.drop(firstSecondary).none { Muscle.Hamstrings in it.primary })
    }

    @Test
    fun groupsCombineAndSearchWorks() {
        val standingHolds = LibraryFilter(types = setOf(MoveType.Holds), positions = setOf(Position.Standing)).apply(all, null)
        assertTrue(standingHolds.isNotEmpty())
        assertTrue(standingHolds.all { it.position == Position.Standing && it.kind.label == "Hold" })
        assertEquals(listOf("butterfly", "reclining_butterfly"), keys(LibraryFilter(query = "butterfly")))
        assertEquals(all.filter { it.spinalTwist }.map { it.key }.toSet(), keys(LibraryFilter(query = "twist")).toSet())
        assertTrue(keys(LibraryFilter(query = "calves")).contains("wall_calf"))
    }

    @Test
    fun fitsMyLimitsUsesTheProfile() {
        val noFloor = Profile(noFloor = true)
        assertTrue(LibraryFilter(fitsMe = true).apply(all, noFloor).all { it.position == Position.Standing })
        assertTrue(keys(LibraryFilter(fitsMe = true), Profile()).none { it.startsWith("roll_") }) // no roller
    }

    @Test
    fun encodeRoundTrips() {
        val f = LibraryFilter("hip", setOf(MoveType.Holds), emptySet(), setOf(Muscle.Glutes), setOf(Position.Lying), setOf(Equipment.Wall), setOf(LevelFilter.Easier), true)
        assertEquals(f, LibraryFilter.decode(f.encode()))
        assertEquals(LibraryFilter(), LibraryFilter.decode(LibraryFilter().encode()))
        assertEquals(7, f.activeCount)
    }

    @Test
    fun searchUnderstandsEverydayWords() {
        // Every word must match.
        val easyHam = keys(LibraryFilter(query = "easy hamstring stretches"))
        assertTrue(easyHam.containsAll(listOf("lying_hamstring", "standing_hamstring")))
        assertTrue("downward_dog" !in easyHam)
        assertEquals(setOf("roll_upper_back", "roll_calves", "roll_quads"), keys(LibraryFilter(query = "roller")).toSet())
        assertEquals(listOf("roll_calves"), keys(LibraryFilter(query = "roller calves")))
        assertTrue(keys(LibraryFilter(query = "hammies")).contains("lying_hamstring"))
        assertTrue(keys(LibraryFilter(query = "standing back")).all { SampleCatalog.byKey(it)!!.position == Position.Standing })
        assertTrue(keys(LibraryFilter(query = "floor glutes")).all { SampleCatalog.byKey(it)!!.position != Position.Standing })
        assertTrue(keys(LibraryFilter(query = "towel")).contains("towel_calf"))
        assertTrue(keys(LibraryFilter(query = "warmup legs")).all { SampleCatalog.byKey(it)!!.kind == dev.clean.stretch.catalog.StretchKind.Dynamic })
        assertTrue(keys(LibraryFilter(query = "zzzz")).isEmpty())
    }
}
