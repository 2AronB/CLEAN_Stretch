package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Region

/**
 * Range-of-motion self-checks (Slice S9, R-012). Each check is answered by picking the closest
 * level, so no tape measure is needed. These are for tracking your own progress, not medical
 * tests (Editorial). Regular stretching increases range of motion [S1][S2][S3] (Tier A).
 *
 * Keys are permanent: saved results refer to them.
 */
data class RomTest(
    val key: String,
    val name: String,
    val region: Region,
    val sided: Boolean,
    val howTo: List<String>,
    /** From least to most range. A result stores the index. */
    val levels: List<String>,
)

object RomTests {
    val toeReach = RomTest(
        key = "toe_reach",
        name = "Toe reach",
        region = Region.Legs,
        sided = false,
        howTo = listOf(
            "Stand with your feet together and knees straight but not locked.",
            "Breathe out and slowly reach down toward your toes. Don't bounce.",
            "Note how far your fingertips reach without strain.",
        ),
        levels = listOf("Above the knees", "Knees", "Mid-shin", "Ankles", "Toes", "Past the toes", "Palms flat on the floor"),
    )

    val shoulderReach = RomTest(
        key = "shoulder_reach",
        name = "Shoulder reach",
        region = Region.Shoulders,
        sided = true,
        howTo = listOf(
            "Reach one hand over that shoulder and down your back.",
            "Reach the other hand up your back from below.",
            "Note how close your fingertips get. The side named is the arm on top.",
        ),
        levels = listOf("More than a hand apart", "A few fingers apart", "Fingertips touch", "Fingers overlap"),
    )

    val kneeToWall = RomTest(
        key = "knee_to_wall",
        name = "Knee to wall",
        region = Region.Legs,
        sided = true,
        howTo = listOf(
            "Face a wall with one foot in front, toes pointing at the wall.",
            "Keeping that heel down, bend the knee forward to touch the wall.",
            "Slide the foot back until the knee only just reaches. Note the toe-to-wall gap.",
        ),
        levels = listOf("Knee can't reach with toes touching the wall", "Under 5 cm (2 in)", "5–9 cm (2–3½ in)", "10–14 cm (4–5½ in)", "15 cm (6 in) or more"),
    )

    val deepSquat = RomTest(
        key = "deep_squat",
        name = "Deep squat",
        region = Region.Hips,
        sided = false,
        howTo = listOf(
            "Stand with your feet a little wider than your hips. Hold something steady if you like.",
            "Slowly squat as low as is comfortable, heels on the floor.",
            "Note how low you can go without your heels lifting.",
        ),
        levels = listOf("A quarter of the way", "Halfway", "Thighs level with the floor", "Below level, heels up", "All the way down, heels flat"),
    )

    val all: List<RomTest> = listOf(toeReach, shoulderReach, kneeToWall, deepSquat)

    fun byKey(key: String): RomTest? = all.firstOrNull { it.key == key }

    /** Suggested days between checks. */
    const val RECHECK_DAYS = 28
}

/** One saved answer, as plain values (no Android types) so trend math is unit-tested. */
data class RomEntry(val testKey: String, val side: String, val level: Int, val takenAt: Long)

/** How one check (and side) has changed. Null levels mean "not measured". */
data class RomTrend(val first: RomEntry, val latest: RomEntry, val count: Int) {
    /** Steps up (positive) or down since the first check. */
    val change: Int get() = latest.level - first.level
}

object RomTrends {
    /** Trend per test and side; tests never measured are simply absent. */
    fun of(entries: List<RomEntry>): Map<Pair<String, String>, RomTrend> =
        entries.groupBy { it.testKey to it.side }.mapValues { (_, list) ->
            val sorted = list.sortedBy { it.takenAt }
            RomTrend(first = sorted.first(), latest = sorted.last(), count = sorted.size)
        }

    /** Days since the most recent check of any test, or null if never checked. */
    fun daysSinceLast(entries: List<RomEntry>, now: Long): Long? =
        entries.maxOfOrNull { it.takenAt }?.let { (now - it) / 86_400_000L }
}
