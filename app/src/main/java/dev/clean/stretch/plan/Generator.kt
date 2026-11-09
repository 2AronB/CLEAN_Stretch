package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Equipment
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

    /**
     * Builds a routine. Focus areas come first, least-stretched this week first ([weekMinutes]),
     * then the rest of the body fills any time left. Warm-ups start with flowing moves.
     */
    fun generate(
        profile: Profile,
        kind: RoutineKind,
        minutes: Int = profile.minutes,
        weekMinutes: Map<Region, Double> = emptyMap(),
        seed: Long = 0L,
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
        val pool = everything.filter { it.kind.isStretch }.toMutableList()
        if (kind == RoutineKind.WarmUp) pool.sortBy { if (it.kind == StretchKind.Dynamic) 0 else 1 }

        val focus = profile.effectiveFocus
        val byNeed = compareBy<Region> { weekMinutes[it] ?: 0.0 }
        val regions = focus.sortedWith(byNeed) + (Region.entries - focus).sortedWith(byNeed)
        val chosen = mutableListOf<Stretch>()
        var used = (rolls + strengths).sumOf { costSeconds(it, hold) }
        var added = true
        while (added) {
            added = false
            for (region in regions) {
                // Only spill into non-focus areas once every focus area has had its turn.
                if (focus.isNotEmpty() && region !in focus && chosen.isNotEmpty() && pool.any { s -> s.primary.any { it.region in focus } && fits(s, hold, used, budget) }) continue
                val pick = pool.firstOrNull { s -> s.primary.any { it.region == region } && fits(s, hold, used, budget) } ?: continue
                pool.remove(pick)
                chosen += pick
                used += costSeconds(pick, hold)
                added = true
            }
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
        if (rolls.isNotEmpty()) notes += "Starts with a minute of foam rolling."
        if (strengths.isNotEmpty()) notes += "Ends with a short Strengthen block. Strength through a full range builds flexibility too."
        focus.filter { r -> allowed(profile, kind).none { s -> s.primary.any { it.region == r } } }
            .forEach { notes += "No ${it.label.lowercase()} stretch fits your settings, so it's left out." }
        if (chosen.isEmpty()) notes += "Nothing in the library fits your settings yet. Try changing your limits in Settings."
        profile.conditions.forEach { notes += "${it.label}: ${it.limits}" }

        return Routine(kind, kind.title, (rolls + ordered + strengths).map { it.key }, hold, notes)
    }

    private fun fits(s: Stretch, hold: Int, used: Int, budget: Int) = used + costSeconds(s, hold) <= budget

    /** Other allowed stretches for the same body area, for the Swap button. */
    fun alternatives(profile: Profile, kind: RoutineKind, key: String, inRoutine: List<String>): List<Stretch> {
        val current = SampleCatalog.byKey(key) ?: return emptyList()
        val regions = current.primary.map { it.region }.toSet()
        val allowed = allowed(profile, kind).filter { it.key !in inRoutine && (it.kind.isStretch == current.kind.isStretch) && (it.kind.isStretch || it.kind == current.kind) }
        val sameArea = allowed.filter { s -> s.primary.any { it.region in regions } }
        return sameArea.ifEmpty { allowed }
    }
}
