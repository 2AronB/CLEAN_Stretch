package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.SampleCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratorTest {
    private fun stretches(r: Routine) = r.keys.map { SampleCatalog.byKey(it)!! }

    @Test
    fun fitsTheTimeBudgetAndIsDeterministic() {
        val p = Profile(minutes = 10)
        val a = Generator.generate(p, RoutineKind.Full, seed = 7)
        val b = Generator.generate(p, RoutineKind.Full, seed = 7)
        assertEquals(a, b)
        assertTrue(a.keys.isNotEmpty())
        assertTrue(Generator.estimateMinutes(a.keys, a.holdSeconds) <= 10)
        assertEquals(a.keys.size, a.keys.toSet().size)
    }

    @Test
    fun avoidedAreasNeverAppear() {
        val p = Profile(avoid = setOf(Region.Back, Region.Legs))
        repeat(5) { seed ->
            stretches(Generator.generate(p, RoutineKind.Full, seed = seed.toLong())).forEach { s ->
                assertTrue(s.key, s.muscles.none { it.region == Region.Back || it.region == Region.Legs })
            }
        }
    }

    @Test
    fun noFloorMeansStandingOnly() {
        val r = Generator.generate(Profile(noFloor = true, minutes = 15), RoutineKind.Full)
        assertTrue(r.keys.isNotEmpty())
        stretches(r).forEach { assertEquals(it.key, Position.Standing, it.position) }
    }

    @Test
    fun conditionsApplyTheirLimits() {
        val pregnant = Generator.generate(Profile(conditions = setOf(Condition.Pregnancy), minutes = 15), RoutineKind.Full)
        stretches(pregnant).forEach { assertTrue(it.key, it.position != Position.Lying && it.level == 1) }
        val bones = Generator.generate(Profile(conditions = setOf(Condition.Osteoporosis), minutes = 15), RoutineKind.Full)
        stretches(bones).forEach { assertTrue(it.key, !it.spinalFlexion) }
        assertTrue(pregnant.notes.any { it.startsWith("Pregnancy") })
    }

    @Test
    fun warmUpsUseShortHoldsAndOlderAdultsLongHolds() {
        assertEquals(30, Generator.generate(Profile(), RoutineKind.WarmUp).holdSeconds)
        assertEquals(60, Generator.generate(Profile(olderAdult = true), RoutineKind.Full).holdSeconds)
        assertEquals(40, Generator.generate(Profile(), RoutineKind.CoolDown).holdSeconds)
    }

    @Test
    fun focusAreasComeFirst() {
        val r = Generator.generate(Profile(focus = setOf(Region.Neck), minutes = 5), RoutineKind.Full)
        assertTrue(stretches(r).any { s -> s.primary.any { it.region == Region.Neck } })
    }

    @Test
    fun uncoveredFocusIsExplained() {
        val r = Generator.generate(Profile(focus = setOf(Region.Neck), avoid = setOf(Region.Neck)), RoutineKind.Full)
        assertTrue(r.notes.any { it.contains("neck") })
    }

    @Test
    fun swapOffersAllowedSameAreaStretches() {
        val p = Profile()
        val alts = Generator.alternatives(p, RoutineKind.Full, "standing_quad", listOf("standing_quad"))
        assertTrue(alts.isNotEmpty())
        assertTrue(alts.none { it.key == "standing_quad" })
    }
}
