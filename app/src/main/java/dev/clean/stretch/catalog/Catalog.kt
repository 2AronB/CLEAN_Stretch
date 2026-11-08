package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.Segment

enum class Region(val label: String) { Neck("Neck"), Shoulders("Shoulders & chest"), Back("Back"), Core("Core"), Hips("Hips"), Legs("Legs") }

enum class Muscle(val label: String, val region: Region) {
    Neck("Neck", Region.Neck),
    Chest("Chest", Region.Shoulders),
    Shoulders("Shoulders", Region.Shoulders),
    Lats("Lats", Region.Back),
    UpperBack("Upper back", Region.Back),
    LowerBack("Lower back", Region.Back),
    Abdominals("Abdominals", Region.Core),
    HipFlexors("Hip flexors", Region.Hips),
    Glutes("Glutes", Region.Hips),
    Adductors("Inner thigh", Region.Hips),
    Quadriceps("Quadriceps", Region.Legs),
    Hamstrings("Hamstrings", Region.Legs),
    Calves("Calves", Region.Legs),
}

enum class Position(val label: String) {
    Standing("Standing"), Seated("Seated"), Kneeling("Kneeling"), HandsAndKnees("Hands & knees"), Lying("Lying")
}

enum class Equipment(val label: String) { None("No equipment"), Wall("Wall"), Chair("Chair"), Strap("Strap or towel"), FoamRoller("Foam roller") }

enum class StretchKind(val label: String) { Static("Hold"), Dynamic("Flow") }

/** Where props are drawn, in body units relative to the hip (only walls for now). */
data class Prop(val equipment: Equipment, val x: Float)

/**
 * One catalog entry. Health wording must follow docs/EVIDENCE.md §11 (claims tiers).
 * Keys are permanent: history refers to them, so never rename or reuse a key.
 */
data class Stretch(
    val key: String,
    val name: String,
    val kind: StretchKind,
    val sided: Boolean,
    val primary: List<Muscle>,
    val secondary: List<Muscle> = emptyList(),
    val position: Position,
    val equipment: Equipment = Equipment.None,
    val level: Int = 1,
    val highlight: Set<Segment>,
    val summary: String,
    val cues: List<String>,
    val cautions: List<String> = emptyList(),
    val sourceIds: List<String>,
    val motion: Motion,
    val prop: Prop? = null,
) {
    val muscles get() = primary + secondary
}

data class Source(val id: String, val short: String, val citation: String, val url: String)
