package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.Muscle
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
        stretches(bones).forEach { assertTrue(it.key, !it.spinalFlexion && !it.spinalTwist) }
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

    @Test
    fun rollingOnlyWithARollerAndNotOnTheSpineForOsteoporosis() {
        fun keys(p: Profile, k: RoutineKind) = Generator.generate(p, k, minutes = 15).keys
        assertTrue(keys(Profile(), RoutineKind.CoolDown).none { it.startsWith("roll_") })
        val withRoller = keys(Profile(hasRoller = true, focus = setOf(Region.Back)), RoutineKind.CoolDown)
        assertEquals("roll_upper_back", withRoller.first())
        val bones = Profile(hasRoller = true, focus = setOf(Region.Back), conditions = setOf(Condition.Osteoporosis))
        assertTrue(keys(bones, RoutineKind.CoolDown).none { it == "roll_upper_back" })
    }

    @Test
    fun strengthenBlockFollowsTheGoalAndSwitch() {
        fun strengths(p: Profile) = Generator.generate(p, RoutineKind.Full).keys.mapNotNull { SampleCatalog.byKey(it) }.filter { it.kind == dev.clean.stretch.catalog.StretchKind.Strengthen }
        assertTrue(strengths(Profile()).isEmpty())
        assertTrue(strengths(Profile(goal = Goal.Desk)).isNotEmpty())
        assertTrue(strengths(Profile(conditions = setOf(Condition.Hypermobility))).isNotEmpty())
        assertTrue(strengths(Profile(goal = Goal.Desk, strengthen = false)).isEmpty())
        assertTrue(strengths(Profile(strengthen = true)).isNotEmpty())
        // The block comes last and still fits the time budget.
        val r = Generator.generate(Profile(goal = Goal.Desk, minutes = 10), RoutineKind.Full)
        assertTrue(Generator.estimateMinutes(r.keys, r.holdSeconds) <= 10)
        assertEquals(dev.clean.stretch.catalog.StretchKind.Strengthen, SampleCatalog.byKey(r.keys.last())!!.kind)
    }

    // PLAN §5 acceptance "Weekly target", per muscle per side (D-039): the goal is spread over the
    // planned days left and never more than 4 min for one muscle on one side in a session.
    @Test
    fun weeklyTargetIsSpreadOverPlannedDaysAndCapped() {
        val legs = Profile(focus = setOf(Region.Legs), minutes = 15) // primary, 10 min default
        // Monday, 5 planned days: 10 / 5 = 2 min.
        assertEquals(2.0, Generator.sessionTargets(legs, emptyMap(), 5)[Muscle.Hamstrings]!!, 1e-9)
        // Wednesday after 4 min, 3 days left: (10 - 4) / 3 = 2 min.
        assertEquals(2.0, Generator.sessionTargets(legs, mapOf(Muscle.Hamstrings to 4.0), 3)[Muscle.Hamstrings]!!, 1e-9)
        // Last day with nothing done: capped at 4.
        assertEquals(4.0, Generator.sessionTargets(legs, emptyMap(), 1)[Muscle.Hamstrings]!!, 1e-9)
        // A muscle with no moves yet gets no target.
        assertEquals(null, Generator.sessionTargets(legs, emptyMap(), 5)[Muscle.Shins])
        val r = Generator.generate(legs, RoutineKind.Full, daysLeft = 5)
        assertTrue((Generator.minutesByMuscle(r.keys, r.holdSeconds)[Muscle.Hamstrings] ?: 0.0) > 0.0)
        // No muscle side ever gets more than 4 min in one routine, whatever the length.
        listOf(5, 10, 15).forEach { m ->
            RoutineKind.entries.forEach { k ->
                val rr = Generator.generate(Profile(minutes = m), k, minutes = m, daysLeft = 1)
                Generator.minutesByMuscle(rr.keys, rr.holdSeconds).values.forEach { assertTrue("$k $m min: $it", it <= 4.0 + 1e-9) }
            }
        }
    }

    @Test
    fun metWeeklyGoalShiftsTimeToOtherAreasWithANote() {
        val p = Profile(focus = setOf(Region.Legs), minutes = 10)
        val done = Muscle.entries.filter { it.region == Region.Legs }.associateWith { 12.0 }
        val r = Generator.generate(p, RoutineKind.Full, weekMinutes = done, daysLeft = 2)
        val byMuscle = Generator.minutesByMuscle(r.keys, r.holdSeconds)
        assertTrue(r.notes.any { it.contains("legs goal") })
        assertTrue(byMuscle.filterKeys { it.region != Region.Legs }.values.sum() > byMuscle.filterKeys { it.region == Region.Legs }.values.sum())
    }

    @Test
    fun doseAndSecondaryAreasSetEachMusclesGoal() {
        val p = Profile(focus = setOf(Region.Hips), secondary = setOf(Region.Neck, Region.Hips), weeklyDose = 15)
        assertEquals(15.0, p.weeklyTarget(Muscle.Glutes)!!, 1e-9)
        assertEquals(5.0, p.weeklyTarget(Muscle.Neck)!!, 1e-9)
        assertEquals(null, p.weeklyTarget(Muscle.Hamstrings))
        // An area to avoid never gets a goal.
        assertEquals(null, p.copy(avoid = setOf(Region.Neck)).weeklyTarget(Muscle.Neck))
        // Nothing chosen and a whole-body goal: everything is secondary.
        assertEquals(5.0, Profile().weeklyTarget(Muscle.Calves)!!, 1e-9)
        // The setup page's weekly total grows with the dose.
        val legs = Profile(focus = setOf(Region.Legs))
        assertTrue(Generator.weeklyPlanMinutes(legs.copy(weeklyDose = 5)) < Generator.weeklyPlanMinutes(legs.copy(weeklyDose = 15)))
        assertTrue(Generator.weeklyPlanMinutes(legs.copy(weeklyDose = 5)) > 0)
    }

    @Test
    fun suggestionsVaryDayToDayAndAvoidOverusedMoves() {
        val p = Profile(minutes = 10)
        val days = (0L until 7L).map { Generator.generate(p, RoutineKind.Full, seed = it * 7).keys.toSet() }
        assertTrue("same routine every day", days.toSet().size > 1)
        val first = Generator.generate(p, RoutineKind.Full, seed = 3).keys
        val counts = first.associateWith { 5 }
        val next = Generator.generate(p, RoutineKind.Full, seed = 3, weekCounts = counts).keys
        assertTrue("overused moves not pushed back", next.count { it in first } < first.size)
    }

    @Test
    fun warmUpsAreMostlyFlowingMoves() {
        val r = Generator.generate(Profile(minutes = 10), RoutineKind.WarmUp)
        val kinds = r.keys.mapNotNull { SampleCatalog.byKey(it) }.filter { it.kind.isStretch }
        assertTrue(kinds.isNotEmpty())
        assertTrue(kinds.count { it.kind == dev.clean.stretch.catalog.StretchKind.Dynamic } * 2 >= kinds.size)
        val full = Generator.generate(Profile(minutes = 10), RoutineKind.Full).keys.mapNotNull { SampleCatalog.byKey(it) }
        assertTrue(full.count { it.kind == dev.clean.stretch.catalog.StretchKind.Static } > full.count { it.kind == dev.clean.stretch.catalog.StretchKind.Dynamic })
    }

    @Test
    fun tenderSpotRollsStretchesAndStrengthensTheArea() {
        val calves = dev.clean.stretch.catalog.Muscle.Calves
        val withRoller = Generator.tenderSpot(Profile(hasRoller = true), calves)
        assertEquals("roll_calves", withRoller.keys.first())
        val moves = withRoller.keys.map { SampleCatalog.byKey(it)!! }
        assertTrue(moves.any { it.kind.isStretch && calves in it.primary })
        assertTrue(Generator.estimateMinutes(withRoller.keys, withRoller.holdSeconds) <= 6)
        // No roller: no rolling, and a hint about it.
        val noRoller = Generator.tenderSpot(Profile(), calves)
        assertTrue(noRoller.keys.none { it.startsWith("roll_") })
        assertTrue(noRoller.notes.any { it.contains("foam roller") })
        // Easiest stretches first.
        val ham = Generator.tenderSpot(Profile(), dev.clean.stretch.catalog.Muscle.Hamstrings)
        assertTrue(ham.keys.mapNotNull { SampleCatalog.byKey(it) }.filter { it.kind.isStretch }.all { it.level == 1 })
    }

    @Test
    fun tenderSpotRespectsLimits() {
        val back = dev.clean.stretch.catalog.Muscle.UpperBack
        val avoided = Generator.tenderSpot(Profile(avoid = setOf(Region.Back)), back)
        assertTrue(avoided.keys.isEmpty())
        assertTrue(avoided.notes.any { it.contains("avoid") })
        val bones = Generator.tenderSpot(Profile(hasRoller = true, conditions = setOf(Condition.Osteoporosis)), back)
        assertTrue(bones.keys.none { it == "roll_upper_back" })
        val noFloor = Generator.tenderSpot(Profile(noFloor = true, hasRoller = true), dev.clean.stretch.catalog.Muscle.Quadriceps)
        assertTrue(noFloor.keys.all { SampleCatalog.byKey(it)!!.position == dev.clean.stretch.catalog.Position.Standing })
    }
}
