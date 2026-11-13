package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Region

/**
 * Weekly stretching dose per muscle, per side (P-1 revised, D-039; EVIDENCE §1, Ingram 2024 [S2],
 * Thomas 2018 [S1]). The user picks one for their primary areas; secondary areas always get the
 * minimum.
 */
enum class Dose(val minutes: Int, val label: String, val info: String) {
    Minimum(5, "Minimum · 5 min", "The smallest weekly amount linked with clear gains in studies."),
    Recommended(10, "Recommended · 10 min", "Where gains peaked in the largest review. More time added no measurable benefit."),
    More(15, "More · 15 min", "For people who enjoy extra stretching. Studies measured no further gain beyond about 10 minutes."),
    ;

    companion object {
        /** Secondary areas keep this much each week. */
        val SECONDARY = Minimum
        fun of(minutes: Int): Dose = entries.firstOrNull { it.minutes == minutes } ?: Recommended
    }
}

/** Goal profiles (EVIDENCE §7, P-8). Specific-target programs come later (S10). */
enum class Goal(val label: String, val blurb: String, val defaultFocus: Set<Region>) {
    General("General flexibility", "Feel looser all over", emptySet()),
    Runner("Runner", "Calves, hips and legs", setOf(Region.Legs, Region.Hips)),
    Lifter("Lifter", "Hips, shoulders and back", setOf(Region.Hips, Region.Shoulders, Region.Back)),
    Desk("Desk / stiffness", "Neck, chest, back and hips", setOf(Region.Neck, Region.Shoulders, Region.Back, Region.Hips)),
}

/**
 * Health conditions asked about at setup (D-029). A "yes" shows the doctor advice and turns on
 * that condition's limits. These are cautions, not medical advice; rules need a current
 * clinical check before Play release (EVIDENCE §9).
 */
enum class Condition(val label: String, val limits: String) {
    Pregnancy("Pregnancy", "Leaves out stretches lying on your back or front, and harder stretches."),
    Osteoporosis("Osteoporosis or low bone density", "Leaves out stretches that round or twist your spine."),
    Hypermobility("Hypermobility (very flexible joints)", "Keeps to easier stretches and gentle holds."),
    RecentSurgery("Recent surgery or joint replacement", "Leaves out the areas you mark to avoid. Follow your surgeon's advice."),
}

/** What kind of routine to build. */
enum class RoutineKind(val label: String, val title: String) {
    Full("Full stretch", "Full-body stretch"),
    WarmUp("Warm-up", "Warm-up"),
    CoolDown("Cool-down", "Cool-down"),
    Quick("Quick break", "Quick break"),
}

/** The user's answers from setup. Stored on the device only. */
data class Profile(
    val goal: Goal = Goal.General,
    val olderAdult: Boolean = false,
    /** Primary areas: their muscles aim for [weeklyDose] each week, per side. */
    val focus: Set<Region> = emptySet(),
    /** Secondary areas: their muscles aim for the minimum dose. */
    val secondary: Set<Region> = emptySet(),
    /** Minutes per muscle per side each week for primary areas (5, 10 or 15). */
    val weeklyDose: Int = Dose.Recommended.minutes,
    val avoid: Set<Region> = emptySet(),
    val noFloor: Boolean = false,
    val noKneeling: Boolean = false,
    val hasWall: Boolean = true,
    val conditions: Set<Condition> = emptySet(),
    val minutes: Int = 10,
    val setupDone: Boolean = false,
    val hasRoller: Boolean = false,
    /** Strengthen block: null = automatic (on for Desk or hypermobility, P-5). */
    val strengthen: Boolean? = null,
) {
    val strengthenOn: Boolean get() = strengthen ?: (goal == Goal.Desk || Condition.Hypermobility in conditions)

    /** Focus areas the user picked, or the goal's defaults. Empty means whole body. */
    val effectiveFocus: Set<Region> get() = focus.ifEmpty { goal.defaultFocus }

    /** Secondary areas, never overlapping the primary ones. With no areas chosen at all, the whole body is secondary. */
    val effectiveSecondary: Set<Region> get() =
        if (effectiveFocus.isEmpty() && secondary.isEmpty()) Region.entries.toSet() else secondary - effectiveFocus

    val dose: Dose get() = Dose.of(weeklyDose)

    /** This muscle's weekly goal in minutes per side, or null if it has none. Areas to avoid never get one. */
    fun weeklyTarget(m: Muscle): Double? = when (m.region) {
        in avoid -> null
        in effectiveFocus -> dose.minutes.toDouble()
        in effectiveSecondary -> Dose.SECONDARY.minutes.toDouble()
        else -> null
    }
}
