package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Equipment
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.Stretch
import dev.clean.stretch.catalog.StretchKind

/** Kinds of move, as the library shows them. */
enum class MoveType(val label: String, val kind: StretchKind) {
    Holds("Holds", StretchKind.Static),
    Flowing("Flowing", StretchKind.Dynamic),
    Strengthen("Strengthen", StretchKind.Strengthen),
    Rolling("Foam rolling", StretchKind.Roll),
}

enum class LevelFilter(val label: String, val matches: (Int) -> Boolean) {
    Easier("Easier", { it <= 1 }),
    Harder("Harder", { it >= 2 }),
}

/**
 * Stretch library filter (pure Kotlin, unit-tested). Empty sets mean "any". Within a group the
 * choices are OR (e.g. Standing or Seated); between groups they are AND.
 */
data class LibraryFilter(
    val query: String = "",
    val types: Set<MoveType> = emptySet(),
    val regions: Set<Region> = emptySet(),
    val muscles: Set<Muscle> = emptySet(),
    val positions: Set<Position> = emptySet(),
    val equipment: Set<Equipment> = emptySet(),
    val levels: Set<LevelFilter> = emptySet(),
    /** Only moves the user's own limits allow (Setup → avoid areas, floor, conditions…). */
    val fitsMe: Boolean = false,
) {
    val activeCount: Int get() =
        listOf(types, regions, muscles, positions, equipment, levels).count { it.isNotEmpty() } +
            (if (fitsMe) 1 else 0) + (if (query.isNotBlank()) 1 else 0)

    private fun targets(s: Stretch, main: Boolean): List<Muscle> = if (main) s.primary else s.muscles

    fun matches(s: Stretch, profile: Profile?): Boolean {
        return SearchWords.matches(s, query) &&
            (types.isEmpty() || types.any { it.kind == s.kind }) &&
            (regions.isEmpty() || s.muscles.any { it.region in regions }) &&
            (muscles.isEmpty() || targets(s, main = false).any { it in muscles }) &&
            (positions.isEmpty() || s.position in positions) &&
            (equipment.isEmpty() || s.equipment in equipment) &&
            (levels.isEmpty() || levels.any { it.matches(s.level) }) &&
            (!fitsMe || profile == null || Generator.exclusionReason(s, profile, RoutineKind.Full) == null)
    }

    /**
     * Matching moves, easiest first. When muscles or areas are chosen, moves that *mainly*
     * target them come before ones that only touch them; ties keep the library order.
     */
    fun apply(all: List<Stretch>, profile: Profile?): List<Stretch> {
        val mainHit: (Stretch) -> Int = { s ->
            when {
                muscles.isNotEmpty() -> if (s.primary.any { it in muscles }) 0 else 1
                regions.isNotEmpty() -> if (s.primary.any { it.region in regions }) 0 else 1
                else -> 0
            }
        }
        return all.withIndex()
            .filter { matches(it.value, profile) }
            .sortedWith(compareBy({ mainHit(it.value) }, { it.value.level }, { it.index }))
            .map { it.value }
    }

    /** Text form so the filter survives rotation and leaving the tab. */
    fun encode(): String = listOf(
        query.replace("|", " "),
        types.joinToString(",") { it.name }, regions.joinToString(",") { it.name }, muscles.joinToString(",") { it.name },
        positions.joinToString(",") { it.name }, equipment.joinToString(",") { it.name }, levels.joinToString(",") { it.name },
        fitsMe.toString(),
    ).joinToString("|")

    companion object {
        private inline fun <reified E : Enum<E>> set(text: String?): Set<E> =
            text.orEmpty().split(",").mapNotNull { n -> enumValues<E>().firstOrNull { it.name == n } }.toSet()

        fun decode(text: String): LibraryFilter {
            val f = text.split("|")
            return LibraryFilter(
                query = f.getOrElse(0) { "" },
                types = set(f.getOrNull(1)), regions = set(f.getOrNull(2)), muscles = set(f.getOrNull(3)),
                positions = set(f.getOrNull(4)), equipment = set(f.getOrNull(5)), levels = set(f.getOrNull(6)),
                fitsMe = f.getOrNull(7).toBoolean(),
            )
        }
    }
}


/**
 * Search box: every word must match something about the move (AND), so "easy hamstring",
 * "roller calves" or "standing back" narrow results like filters. Common everyday words and
 * plurals are understood ("hammies", "roller", "gentle", "floor", "stretches"...).
 */
object SearchWords {
    /** Everyday words mapped to what they mean. */
    private val synonyms: Map<String, List<String>> = mapOf(
        "hammies" to listOf("hamstrings"), "hamstring" to listOf("hamstrings"),
        "quad" to listOf("quadriceps"), "quads" to listOf("quadriceps"), "thigh" to listOf("quadriceps", "hamstrings", "inner thigh"),
        "thighs" to listOf("quadriceps", "hamstrings", "inner thigh"), "groin" to listOf("inner thigh"), "adductor" to listOf("inner thigh"),
        "adductors" to listOf("inner thigh"), "calf" to listOf("calves"), "ankle" to listOf("calves", "shins & ankles"), "ankles" to listOf("calves", "shins & ankles"), "shin" to listOf("shins & ankles"),
        "foot" to listOf("shins & ankles"), "feet" to listOf("shins & ankles"),
        "glute" to listOf("glutes"), "butt" to listOf("glutes"), "bum" to listOf("glutes"), "hip" to listOf("hips", "hip flexors", "glutes"),
        "pecs" to listOf("chest"), "pec" to listOf("chest"), "shoulder" to listOf("shoulders"), "lat" to listOf("lats"),
        "abs" to listOf("abdominals"), "stomach" to listOf("abdominals"), "spine" to listOf("back", "upper back", "lower back"),
        "roller" to listOf("foam roller", "roll"), "foam" to listOf("foam roller"), "rolling" to listOf("roll"),
        "towel" to listOf("strap or towel"), "strap" to listOf("strap or towel"), "band" to listOf("strap or towel"),
        "floor" to listOf("lying", "seated", "kneeling", "hands & knees"), "ground" to listOf("lying", "seated", "kneeling", "hands & knees"),
        "laying" to listOf("lying"), "sitting" to listOf("seated"), "sit" to listOf("seated"), "stand" to listOf("standing"),
        "kneel" to listOf("kneeling"), "dynamic" to listOf("flow"), "flowing" to listOf("flow"), "moving" to listOf("flow"),
        "twisting" to listOf("twist"), "rotation" to listOf("twist"), "rotate" to listOf("twist"), "obliques" to listOf("side body"), "oblique" to listOf("side body"),
        "wrist" to listOf("forearms & wrists"), "wrists" to listOf("forearms & wrists"), "forearm" to listOf("forearms & wrists"),
        "forearms" to listOf("forearms & wrists"), "hand" to listOf("forearms & wrists"), "hands" to listOf("forearms & wrists"),
        "tricep" to listOf("triceps"), "bicep" to listOf("biceps"), "arm" to listOf("arms"), "arms" to listOf("arms"),
        "piriformis" to listOf("deep hip rotators"), "rotators" to listOf("deep hip rotators"),
        "warmup" to listOf("flow"), "strength" to listOf("strengthen"), "strong" to listOf("strengthen"), "hold" to listOf("hold"),
    )
    private val easyWords = setOf("easy", "easier", "easiest", "gentle", "beginner", "simple")
    private val hardWords = setOf("hard", "harder", "hardest", "advanced", "challenging", "deep", "deeper")
    private val ignored = setOf("stretch", "stretches", "stretching", "for", "my", "the", "a", "an", "and", "with", "of", "to", "move", "moves", "exercise", "exercises")

    /** Everything a word can match on for one move. */
    private fun haystack(s: Stretch): String = buildList {
        add(s.name); add(s.summary); add(s.kind.label); add(s.equipment.label); add(s.position.label)
        s.muscles.forEach { add(it.label); add(it.region.label) }
        if (s.sided) add("each side") else add("both sides")
        // Twisting moves (thread the needle, torso turns) don't all say "twist" in their names.
        if (s.spinalTwist) add("twist")
    }.joinToString(" ").lowercase()

    fun matches(s: Stretch, query: String): Boolean {
        val words = query.lowercase().split(Regex("[^a-z0-9&]+")).filter { it.isNotBlank() && it !in ignored }
        if (words.isEmpty()) return true
        val text = haystack(s)
        return words.all { w ->
            when (w) {
                in easyWords -> s.level <= 1
                in hardWords -> s.level >= 2
                else -> {
                    val options = listOf(w) + (synonyms[w] ?: emptyList()) + listOfNotNull(w.removeSuffix("s").takeIf { it.length > 2 && it != w })
                    options.any { text.contains(it) }
                }
            }
        }
    }
}
