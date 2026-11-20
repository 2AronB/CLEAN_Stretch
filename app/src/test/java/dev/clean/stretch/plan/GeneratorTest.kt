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
        assertEquals(45, Generator.generate(Profile(), RoutineKind.CoolDown).holdSeconds)
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
        val legs = Profile(focus = setOf(Region.Legs), minutes = 15, weeklyDose = 10) // primary, 10 min
        // Monday, 5 planned days: 10 / 5 = 2 min.
        assertEquals(2.0, Generator.sessionTargets(legs, emptyMap(), 5)[Muscle.Hamstrings]!!, 1e-9)
        // Wednesday after 4 min, 3 days left: (10 - 4) / 3 = 2 min.
        assertEquals(2.0, Generator.sessionTargets(legs, mapOf(Muscle.Hamstrings to 4.0), 3)[Muscle.Hamstrings]!!, 1e-9)
        // Last day with nothing done: capped at 4.
        assertEquals(4.0, Generator.sessionTargets(legs, emptyMap(), 1)[Muscle.Hamstrings]!!, 1e-9)
        // Shins & ankles has moves (2026-11-14); as a smaller muscle it aims for half (D-045): 5 / 5 = 1 min.
        assertEquals(1.0, Generator.sessionTargets(legs, emptyMap(), 5)[Muscle.Shins]!!, 1e-9)
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
    fun movesThatStretchBothSidesAtOnceArePreferred() {
        // Hamstrings: a both-sides move (e.g. a forward fold) beats a one-leg move for the same goal.
        val legs = Profile(focus = setOf(Region.Legs), weeklyDose = 10)
        val pool = Generator.allowed(legs, RoutineKind.Full).filter { it.kind.isStretch && Muscle.Hamstrings in it.primary }
        assertTrue("need both kinds of hamstring move", pool.any { it.sided } && pool.any { !it.sided })
        val r = Generator.generate(legs, RoutineKind.Full, minutes = 5, daysLeft = 5)
        val firstHamstring = r.keys.mapNotNull { SampleCatalog.byKey(it) }.first { Muscle.Hamstrings in it.primary }
        assertTrue(firstHamstring.key, !firstHamstring.sided)
        assertEquals(2, Generator.bothSidesBonus(firstHamstring))
    }

    @Test
    fun noAreasPickedAimsAtCommonlyStiffMuscles() {
        val p = Profile() // whole-body goal, nothing picked
        assertTrue(p.usesCommonlyStiff)
        assertEquals(7.0, p.weeklyTarget(Muscle.Hamstrings)!!, 1e-9)
        assertEquals(7.0, p.weeklyTarget(Muscle.Neck)!!, 1e-9)
        assertEquals(null, p.weeklyTarget(Muscle.Shins))
        assertEquals(null, p.weeklyTarget(Muscle.Biceps))
        assertEquals(null, p.copy(avoid = setOf(Region.Neck)).weeklyTarget(Muscle.Neck))
        // A goal with its own areas, or any area picked, uses those instead.
        assertTrue(!Profile(goal = Goal.Runner).usesCommonlyStiff)
        assertTrue(!Profile(secondary = setOf(Region.Arms)).usesCommonlyStiff)
        assertEquals(null, Profile(secondary = setOf(Region.Arms)).weeklyTarget(Muscle.Hamstrings))
        // About an hour of holding a week (was about 137 with the whole body as secondary).
        assertTrue(Generator.weeklyPlanMinutes(p) <= 60)
        // On the default 5 days it needs about 16 min a day (routines stop at 15).
        val hold = Generator.holdSeconds(p, RoutineKind.Full)
        assertTrue(Generator.neededMinutes(p, dev.clean.stretch.settings.DEFAULT_DAYS_PER_WEEK, hold) <= 16)
    }

    @Test
    fun doseAndSecondaryAreasSetEachMusclesGoal() {
        val p = Profile(focus = setOf(Region.Hips), secondary = setOf(Region.Neck, Region.Hips), weeklyDose = 15)
        assertEquals(15.0, p.weeklyTarget(Muscle.Glutes)!!, 1e-9)
        assertEquals(5.0, p.weeklyTarget(Muscle.Neck)!!, 1e-9)
        assertEquals(null, p.weeklyTarget(Muscle.Hamstrings))
        // An area to avoid never gets a goal.
        assertEquals(null, p.copy(avoid = setOf(Region.Neck)).weeklyTarget(Muscle.Neck))
        // Smaller, supporting muscles get half their area's goal (D-045).
        assertEquals(7.5, p.weeklyTarget(Muscle.DeepHip)!!, 1e-9)
        assertEquals(2.5, Profile(secondary = setOf(Region.Legs)).weeklyTarget(Muscle.Shins)!!, 1e-9)
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
    fun stretchThisNowPicksStretchesForTheMuscle() {
        // Every muscle has at least three main moves (owner, D-043), so each gets a routine.
        for (m in dev.clean.stretch.catalog.Muscle.entries) {
            val r = Generator.forMuscle(Profile(hasWall = true), m)
            assertTrue("$m has no moves", r.keys.isNotEmpty())
            assertTrue(r.keys.size <= Generator.MUSCLE_NOW_MOVES)
            r.keys.map { SampleCatalog.byKey(it)!! }.forEach { s ->
                assertTrue("${s.key} is not a stretch", s.kind.isStretch)
                assertTrue("${s.key} doesn't mainly target $m", m in s.primary)
            }
        }
        val chest = dev.clean.stretch.catalog.Muscle.Chest
        val rows = SampleCatalog.all.filter { it.kind.isStretch && chest in it.primary }.size
        assertTrue("chest should have three main stretches, has $rows", rows >= 3)
        // Limits still apply.
        assertTrue(Generator.forMuscle(Profile(avoid = setOf(Region.Shoulders)), chest).keys.isEmpty())
        val easy = Generator.forMuscle(Profile(conditions = setOf(Condition.Hypermobility)), dev.clean.stretch.catalog.Muscle.Triceps)
        assertTrue(easy.keys.isNotEmpty() && easy.keys.all { SampleCatalog.byKey(it)!!.level == 1 })
        // Everyone has a wall (owner, D-044): the old "no wall" answer no longer leaves wall moves out.
        assertTrue(Generator.exclusionReason(SampleCatalog.byKey("wall_triceps")!!, Profile(hasWall = false), RoutineKind.Full) == null)
    }

    @Test
    fun holdLengthFollowsTheTodayChipExceptInWarmUps() {
        assertEquals(45, Generator.holdSeconds(Profile(), RoutineKind.Full))
        assertEquals(60, Generator.holdSeconds(Profile(olderAdult = true), RoutineKind.Full))
        assertEquals(30, Generator.holdSeconds(Profile(), RoutineKind.Full, chosen = 30))
        assertEquals(60, Generator.holdSeconds(Profile(), RoutineKind.Quick, chosen = 60))
        assertEquals(30, Generator.holdSeconds(Profile(olderAdult = true), RoutineKind.WarmUp, chosen = 60))
        assertEquals(45, Generator.holdSeconds(Profile(), RoutineKind.CoolDown, chosen = 17)) // not a choice
    }

    @Test
    fun routineLengthFollowsTheWeeklyGoals() {
        val light = Generator.routineMinutes(Profile(weeklyDose = 5), 7, RoutineKind.Full, 45)
        val heavy = Generator.routineMinutes(Profile(weeklyDose = 15, focus = Region.entries.toSet()), 3, RoutineKind.Full, 45)
        assertTrue("$light <= $heavy", light <= heavy)
        assertTrue(light in 5..15 && heavy in 5..15)
        assertEquals(Generator.MAX_ROUTINE_MINUTES, heavy) // capped; Setup explains
        assertTrue(Generator.neededMinutes(Profile(weeklyDose = 15, focus = Region.entries.toSet()), 3, 45) > heavy)
        assertEquals(Generator.QUICK_MINUTES, Generator.routineMinutes(Profile(), 3, RoutineKind.Quick, 45))
        assertTrue(Generator.routineMinutes(Profile(weeklyDose = 15, focus = Region.entries.toSet()), 2, RoutineKind.WarmUp, 30) <= 8)
        // The generated routine fits the length it was given.
        val r = Generator.generate(Profile(), RoutineKind.Full, minutes = light, chosenHold = 45)
        assertTrue(Generator.estimateMinutes(r.keys, r.holdSeconds) <= light)
    }

    @Test
    fun aReminderCanKeepARoutineToSomeAreas() {
        val areas = setOf(Region.Neck, Region.Shoulders)
        val r = Generator.generate(Profile(), RoutineKind.Quick, minutes = 4, onlyAreas = areas)
        assertTrue(r.keys.isNotEmpty())
        r.keys.map { SampleCatalog.byKey(it)!! }.forEach { s -> assertTrue(s.key, s.primary.any { it.region in areas }) }
    }

    @Test
    fun newUsersStartOnSevenMinutes() {
        assertEquals(7, Profile().weeklyDose)
        assertEquals(listOf(5, 7, 10, 15), Dose.entries.map { it.minutes })
        assertEquals(Dose.Starter, Dose.of(99))
    }

    @Test
    fun everyMuscleHasThreeMainMoves() {
        for (m in dev.clean.stretch.catalog.Muscle.entries) {
            val n = SampleCatalog.all.count { m in it.primary }
            assertTrue("${m.label} has $n main moves (owner asked for at least 3, D-043)", n >= 3)
        }
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
