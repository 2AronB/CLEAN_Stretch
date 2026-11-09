package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.StageProp

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

/**
 * Static and Dynamic are stretches. Strengthen = timed strength/control holds (P-5).
 * Roll = paced foam rolling (P-10). Neither counts toward per-area stretch targets.
 */
enum class StretchKind(val label: String) { Static("Hold"), Dynamic("Flow"), Strengthen("Strengthen"), Roll("Roll");
    val isStretch get() = this == Static || this == Dynamic
}

/** Where props are drawn, in body units relative to the hip: a wall (vertical) or a foam roller (circle on the floor). */
data class Prop(val equipment: Equipment, val x: Float)

/** How the figure drawing sees a prop (only walls and foam rollers are drawn). */
fun Prop.toStageProp(): StageProp? = when (equipment) {
    Equipment.Wall -> StageProp(StageProp.Kind.Wall, x)
    Equipment.FoamRoller -> StageProp(StageProp.Kind.Roller, x)
    else -> null
}

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
    /** Rounds (flexes) the spine, held or repeated. Left out for osteoporosis (EVIDENCE §9, [S32][S33]). */
    val spinalFlexion: Boolean = false,
    /** Twists the spine, held or repeated. Also left out for osteoporosis (EVIDENCE §9, [S32][S33]). */
    val spinalTwist: Boolean = false,
) {
    val muscles get() = primary + secondary
}

data class Source(val id: String, val short: String, val citation: String, val url: String)
