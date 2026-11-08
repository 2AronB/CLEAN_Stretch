package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Region

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
    Osteoporosis("Osteoporosis or low bone density", "Leaves out stretches that round your spine."),
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
    val focus: Set<Region> = emptySet(),
    val avoid: Set<Region> = emptySet(),
    val noFloor: Boolean = false,
    val noKneeling: Boolean = false,
    val hasWall: Boolean = true,
    val conditions: Set<Condition> = emptySet(),
    val minutes: Int = 10,
    val setupDone: Boolean = false,
) {
    /** Focus areas the user picked, or the goal's defaults. Empty means whole body. */
    val effectiveFocus: Set<Region> get() = focus.ifEmpty { goal.defaultFocus }
}
