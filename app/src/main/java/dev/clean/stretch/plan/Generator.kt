package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Equipment
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.Stretch
import dev.clean.stretch.catalog.StretchKind
import kotlin.random.Random

/** A recommended routine. The user can change it before starting (D-029). */
data class Routine(
    val kind: RoutineKind,
    val title: String,
    val keys: List<String>,
    val holdSeconds: Int,
    /** Plain-language notes: what was left out and why. */
    val notes: List<String>,
)

/** Pure-Kotlin routine builder (Slice S5). Deterministic for a given seed. */
object Generator {
    /** Rough seconds of setup per side, used only to fit the time budget. */
    private const val SETUP_ESTIMATE_S = 10

    private val positionOrder = listOf(Position.Standing, Position.Kneeling, Position.HandsAndKnees, Position.Seated, Position.Lying)

    /** Hold length: 30 s inside warm-ups and quick breaks (P-3), 60 s for 65+ (P-2), else 40 s. */
    fun holdSeconds(profile: Profile, kind: RoutineKind): Int = when {
        kind == RoutineKind.WarmUp || kind == RoutineKind.Quick -> 30
        profile.olderAdult -> 60
        else -> 40
    }

    /** Why a stretch is left out for this profile, or null if it's allowed. Safety rules: EVIDENCE §9. */
    fun exclusionReason(s: Stretch, profile: Profile, kind: RoutineKind): String? {
        val regions = s.muscles.map { it.region }.toSet()
        return when {
            regions.any { it in profile.avoid } -> "an area you're avoiding"
            profile.noFloor && s.position != Position.Standing -> "no floor work"
            profile.noKneeling && (s.position == Position.Kneeling || s.position == Position.HandsAndKnees) -> "no kneeling"
            !profile.hasWall && s.equipment == Equipment.Wall -> "needs a wall"
            s.equipment == Equipment.FoamRoller && !profile.hasRoller -> "needs a foam roller"
            Condition.Osteoporosis in profile.conditions && s.kind == StretchKind.Roll && s.primary.any { it.region == Region.Back } -> "rolling on the spine (osteoporosis)"
            Condition.Pregnancy in profile.conditions && s.position == Position.Lying -> "lying down (pregnancy)"
            Condition.Osteoporosis in profile.conditions && s.spinalFlexion -> "rounds the spine (osteoporosis)"
            Condition.Osteoporosis in profile.conditions && s.spinalTwist -> "twists the spine (osteoporosis)"
            (Condition.Hypermobility in profile.conditions || Condition.Pregnancy in profile.conditions) && s.level > 1 -> "a harder stretch"
            kind == RoutineKind.Quick && s.position != Position.Standing -> "quick breaks stay standing"
            else -> null
        }
    }

    fun allowed(profile: Profile, kind: RoutineKind): List<Stretch> =
        SampleCatalog.all.filter { exclusionReason(it, profile, kind) == null }

    /** Rolling is 60 s per area and Strengthen holds 30 s (P-5, P-10); stretches use the routine's hold. */
    fun secondsFor(s: Stretch, hold: Int): Int = when (s.kind) {
        StretchKind.Roll -> ROLL_SECONDS
        StretchKind.Strengthen -> STRENGTHEN_SECONDS
        else -> hold
    }

    const val ROLL_SECONDS = 60
    const val STRENGTHEN_SECONDS = 30

    private fun costSeconds(s: Stretch, hold: Int) = (if (s.sided) 2 else 1) * (SETUP_ESTIMATE_S + secondsFor(s, hold))

    /** Estimated length in whole minutes (at least 1). */
    fun estimateMinutes(keys: List<String>, hold: Int): Int {
        val seconds = keys.mapNotNull { SampleCatalog.byKey(it) }.sumOf { costSeconds(it, hold) }
        return ((seconds + 59) / 60).coerceAtLeast(1)
    }

    /** P-1: never more than 4 minutes for one muscle on one side in a single session [S2]. */
    const val SESSION_CAP_MINUTES = 4.0

    /**
     * Minutes this move gives each of its main muscles, per side. A two-sided move is held once on
     * each side, so each side gets one hold; a move that stretches both sides at once gives both.
     */
    fun muscleMinutes(s: Stretch, hold: Int): Double = if (s.kind.isStretch) secondsFor(s, hold) / 60.0 else 0.0

    /**
     * Today's share of each muscle's weekly goal (D-039): what's left of its goal ([Profile.weeklyTarget],
     * per side, judged on the weaker side), spread over the planned days still to come, capped at
     * [SESSION_CAP_MINUTES]. Muscles no allowed move stretches are left out.
     */
    fun sessionTargets(profile: Profile, weekMinutes: Map<Muscle, Double>, daysLeft: Int, pool: List<Stretch> = SampleCatalog.all): Map<Muscle, Double> =
        Muscle.entries.mapNotNull { m ->
            val goal = profile.weeklyTarget(m) ?: return@mapNotNull null
            if (pool.none { it.kind.isStretch && m in it.primary }) return@mapNotNull null
            m to ((goal - (weekMinutes[m] ?: 0.0)) / daysLeft.coerceAtLeast(1)).coerceIn(0.0, SESSION_CAP_MINUTES)
        }.toMap()

    /**
     * Roughly how many minutes of holding a week it takes to meet every weekly goal (both sides),
     * for the setup page's live total. Greedy: keep picking the allowed move that helps the most
     * unmet muscles, one standard hold per side at a time. Moves that stretch several muscles
     * make the total smaller than adding up each muscle's goal.
     */
    /** Minutes a day should reach to count as done: the weekly plan spread over the planned days. */
    fun dayGoalMinutes(profile: Profile, daysPerWeek: Int): Double =
        (weeklyPlanMinutes(profile).toDouble() / daysPerWeek.coerceAtLeast(1)).coerceAtLeast(3.0)

    fun weeklyPlanMinutes(profile: Profile): Int {
        val hold = holdSeconds(profile, RoutineKind.Full)
        val moves = allowed(profile, RoutineKind.Full).filter { it.kind.isStretch }
        val left = sessionTargets(profile, emptyMap(), 1, moves).keys
            .associateWith { profile.weeklyTarget(it) ?: 0.0 }.toMutableMap()
        var seconds = 0
        while (true) {
            val best = moves.maxByOrNull { s -> s.primary.count { (left[it] ?: 0.0) > 1e-9 } } ?: break
            if (best.primary.none { (left[it] ?: 0.0) > 1e-9 }) break
            best.primary.forEach { m -> left[m]?.let { left[m] = it - hold / 60.0 } }
            seconds += hold * (if (best.sided) 2 else 1) // a two-sided move is held on each side
        }
        return (seconds + 59) / 60
    }

    /**
     * Builds a routine (Generator v2, D-032: right on generation).
     * 1. Each muscle with a goal first gets its share of the weekly goal ([sessionTargets]), neediest first.
     * 2. Leftover time fills from the least-covered muscles, never past 4 min per muscle per side.
     * 3. Variety: the order changes day to day ([seed] = date) and moves done often this week
     *    ([weekCounts]) go to the back of the queue.
     * Warm-ups use flowing moves first; full routines and cool-downs use holds first.
     */
    fun generate(
        profile: Profile,
        kind: RoutineKind,
        minutes: Int = profile.minutes,
        /** This week's minutes per muscle on its weaker side ([dev.clean.stretch.data.SideMinutes.weaker]). */
        weekMinutes: Map<Muscle, Double> = emptyMap(),
        seed: Long = 0L,
        daysLeft: Int = 3,
        weekCounts: Map<String, Int> = emptyMap(),
    ): Routine {
        val hold = holdSeconds(profile, kind)
        val budget = (if (kind == RoutineKind.Quick) minutes.coerceAtMost(4) else minutes) * 60
        val everything = allowed(profile, kind).shuffled(Random(seed))
        val focusFirst = compareBy<Stretch> { s -> if (s.primary.any { it.region in profile.effectiveFocus }) 0 else 1 }
        // Blocks (P-5, P-10): rolling opens warm-ups and cool-downs if the user has a roller;
        // a short Strengthen block closes full routines, cool-downs and quick breaks when it's on.
        val rolls = if (kind == RoutineKind.WarmUp || kind == RoutineKind.CoolDown) everything.filter { it.kind == StretchKind.Roll }.sortedWith(focusFirst).take(1) else emptyList()
        val strengthCount = if (!profile.strengthenOn || kind == RoutineKind.WarmUp) 0 else if (kind == RoutineKind.Quick || minutes <= 5) 1 else 2
        val strengths = everything.filter { it.kind == StretchKind.Strengthen }.sortedWith(focusFirst).take(strengthCount)

        // Preferred move type first, then the least-used this week; the date-seeded shuffle breaks ties.
        val typeRank: (Stretch) -> Int = { s ->
            val dynamic = s.kind == StretchKind.Dynamic
            if (kind == RoutineKind.WarmUp) (if (dynamic) 0 else 1) else (if (dynamic) 1 else 0)
        }
        val pool = everything.filter { it.kind.isStretch }
            .sortedWith(compareBy<Stretch>({ typeRank(it) }, { weekCounts[it.key] ?: 0 }))
            .toMutableList()

        val targets = sessionTargets(profile, weekMinutes, daysLeft, pool)
        val given = mutableMapOf<Muscle, Double>()
        val chosen = mutableListOf<Stretch>()
        var used = (rolls + strengths).sumOf { costSeconds(it, hold) }

        fun canAdd(s: Stretch): Boolean =
            fits(s, hold, used, budget) &&
                s.primary.all { (given[it] ?: 0.0) + muscleMinutes(s, hold) <= SESSION_CAP_MINUTES + 1e-9 }

        fun take(s: Stretch) {
            pool.remove(s); chosen += s; used += costSeconds(s, hold)
            s.primary.forEach { given[it] = (given[it] ?: 0.0) + muscleMinutes(s, hold) }
        }

        // 1. Weekly-goal shares, neediest muscle first. A move that also stretches other needy
        //    muscles is preferred, since its time counts for each of them.
        //    Warm-ups chase goals with flowing moves only, so they stay warm-ups (P-3); holds can
        //    still fill spare time below.
        val forGoals = { s: Stretch -> kind != RoutineKind.WarmUp || s.kind == StretchKind.Dynamic }
        while (true) {
            val muscle = targets.filter { (m, goal) -> (given[m] ?: 0.0) < goal - 1e-9 && pool.any { s -> m in s.primary && forGoals(s) && canAdd(s) } }
                .maxByOrNull { (m, goal) -> goal - (given[m] ?: 0.0) }?.key ?: break
            val needy = { s: Stretch -> s.primary.count { m -> (targets[m] ?: 0.0) - (given[m] ?: 0.0) > 1e-9 } }
            // Preferred move type first (pool order), then the move that helps the most needy muscles.
            take(pool.filter { s -> muscle in s.primary && forGoals(s) && canAdd(s) }.minWith(compareBy({ typeRank(it) }, { -needy(it) })))
        }
        // 2. Fill leftover time from the least-covered muscles; muscles whose weekly goal is met come last.
        val metThisWeek = targets.filter { (m, share) -> share <= 1e-9 && (weekMinutes[m] ?: 0.0) > 0 }.keys
        while (true) {
            val pick = pool.filter(::canAdd).minWithOrNull(
                compareBy<Stretch>(
                    { s -> typeRank(s) },
                    { s -> if (s.primary.all { it in metThisWeek }) 1 else 0 },
                    { s -> s.primary.minOf { given[it] ?: 0.0 } },
                ),
            ) ?: break
            take(pick)
        }
        if (chosen.isEmpty()) {
            // Always offer something if anything is allowed, even if it runs a little long.
            allowed(profile, kind).firstOrNull { it.kind.isStretch }?.let { chosen += it }
        }

        val ordered = if (kind == RoutineKind.WarmUp) {
            chosen.sortedWith(compareBy({ if (it.kind == StretchKind.Dynamic) 0 else 1 }, { positionOrder.indexOf(it.position) }))
        } else {
            chosen.sortedBy { positionOrder.indexOf(it.position) }
        }

        val notes = mutableListOf<String>()
        // Foam rolling and the Strengthen block are shown as toggles on Today, not as notes.
        val focus = profile.effectiveFocus
        focus.filter { r -> val ms = targets.keys.filter { it.region == r }; ms.isNotEmpty() && ms.all { it in metThisWeek } }
            .forEach { notes += "You've reached this week's ${it.label.lowercase()} goal, so today leans on other areas." }
        focus.filter { r -> allowed(profile, kind).none { s -> s.primary.any { it.region == r } } }
            .forEach { notes += "No ${it.label.lowercase()} stretch fits your settings, so it's left out." }
        if (chosen.isEmpty()) notes += "Nothing in the library fits your settings yet. Try changing your limits in Settings."
        profile.conditions.forEach { notes += "${it.label}: ${it.limits}" }

        return Routine(kind, kind.title, (rolls + ordered + strengths).map { it.key }, hold, notes)
    }

    /** Stretch minutes per muscle, per side, for a list of moves (used by tests). */
    fun minutesByMuscle(keys: List<String>, hold: Int): Map<Muscle, Double> {
        val out = mutableMapOf<Muscle, Double>()
        keys.mapNotNull { SampleCatalog.byKey(it) }.forEach { s ->
            s.primary.forEach { m -> out[m] = (out[m] ?: 0.0) + muscleMinutes(s, hold) }
        }
        return out
    }

    private fun fits(s: Stretch, hold: Int, used: Int, budget: Int) = used + costSeconds(s, hold) <= budget

    /** Hold length for tender-spot routines: gentle 30 s holds. */
    const val TENDER_HOLD_SECONDS = 30

    /**
     * "Tender spot" mini-routine (P-10 item 4): roll the spot (if the user has a roller and it's
     * allowed), then the easiest stretches for it, then a strengthen move for the area if one
     * fits. About 3–6 minutes. Every limit and safety rule applies; an area the user avoids gets a
     * note and no moves. The screen asks the "see a clinician" questions before calling this.
     */
    fun tenderSpot(profile: Profile, muscle: Muscle): Routine {
        val hold = TENDER_HOLD_SECONDS
        val title = "Tender spot: ${muscle.label.lowercase()}"
        if (muscle.region in profile.avoid) {
            return Routine(RoutineKind.CoolDown, title, emptyList(), hold,
                listOf("You've marked ${muscle.region.label.lowercase()} as an area to avoid, so Stretch won't suggest moves for it. Follow your clinician's advice."))
        }
        val allowed = allowed(profile, RoutineKind.CoolDown)
        fun forSpot(list: List<Stretch>) = list.filter { muscle in it.primary }.ifEmpty { list.filter { muscle in it.muscles } }
        val roll = forSpot(allowed.filter { it.kind == StretchKind.Roll }).take(1)
        val stretches = forSpot(allowed.filter { it.kind.isStretch })
            .sortedWith(compareBy({ it.level }, { if (it.kind == StretchKind.Static) 0 else 1 }))
            .take(2)
        val strengthen = allowed.filter { it.kind == StretchKind.Strengthen }
            .sortedWith(compareBy { if (muscle in it.primary) 0 else 1 })
            .filter { s -> s.primary.any { it.region == muscle.region } }
            .take(1)
        var picked = roll + stretches + strengthen
        // Keep it short: drop the second stretch, then the strengthen move, if it runs past 6 min.
        while (estimateMinutes(picked.map { it.key }, hold) > 6 && picked.size > 1) {
            picked = if (stretches.size > 1 && stretches[1] in picked) picked - stretches[1] else picked.dropLast(1)
        }
        val notes = mutableListOf<String>()
        if (picked.isEmpty()) notes += "Nothing in the library fits this spot with your settings yet."
        if (roll.isEmpty() && !profile.hasRoller && allowedRollExists(muscle)) notes += "Have a foam roller? Turn it on in Settings and this will start with a minute of rolling."
        notes += "Gentle pressure and mild tension only. Stop for sharp pain, numbness or tingling."
        return Routine(RoutineKind.CoolDown, title, picked.map { it.key }, hold, notes)
    }

    private fun allowedRollExists(muscle: Muscle) = SampleCatalog.all.any { it.kind == StretchKind.Roll && muscle in it.muscles }

    /** Other allowed stretches for the same body area, for the Swap button. */
    fun alternatives(profile: Profile, kind: RoutineKind, key: String, inRoutine: List<String>): List<Stretch> {
        val current = SampleCatalog.byKey(key) ?: return emptyList()
        val regions = current.primary.map { it.region }.toSet()
        val allowed = allowed(profile, kind).filter { it.key !in inRoutine && (it.kind.isStretch == current.kind.isStretch) && (it.kind.isStretch || it.kind == current.kind) }
        val sameArea = allowed.filter { s -> s.primary.any { it.region in regions } }
        return sameArea.ifEmpty { allowed }
    }
}
