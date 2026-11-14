package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Region
import dev.clean.stretch.figure.Ik
import dev.clean.stretch.figure.Pin
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.Vec
import dev.clean.stretch.figure.View
import kotlin.math.acos

/**
 * Range-of-motion self-checks (Slice S9, R-012, D-033). Each answer is a picture drawn with the
 * app's figure: "which looks most like you?". No numbers or tools. These are for tracking your
 * own progress, not medical tests (Editorial). Regular stretching increases range of motion
 * [S1][S2][S3] (Tier A).
 *
 * Keys are permanent: saved results refer to them. Retired checks (shoulder_reach,
 * knee_to_wall, deep_squat) keep their saved rows but are no longer shown.
 */
data class RomTest(
    val key: String,
    val name: String,
    val region: Region,
    val sided: Boolean,
    val howTo: List<String>,
    /** From least to most range. A result stores the index. */
    val levels: List<String>,
    /** One picture per level, same order. */
    val pictures: List<Pose>,
    val highlight: Set<Segment>,
    /** Camera for the pictures. */
    val view: View = View.Side,
)

object RomTests {
    private val legsStraight = Pose(nearElbow = 4f, farElbow = 4f)

    /** Standing forward fold with both hands reaching [hand] (body units, hip at 0,0). */
    private fun fold(torso: Float, hand: Vec): Pose {
        val base = legsStraight.copy(torso = torso, head = -15f)
        val targets = mapOf(Pin.NearHand to hand, Pin.FarHand to hand + Vec(-2f, 0f))
        return Ik.pin(base, base, targets)
    }

    /** Forward fold with both arms hanging straight down. */
    private fun hang(torso: Float): Pose =
        legsStraight.copy(torso = torso, head = -15f, nearShoulder = 180f - torso, farShoulder = 178f - torso)

    val toeReach = RomTest(
        key = "toe_reach",
        name = "Toe reach",
        region = Region.Legs,
        sided = false,
        howTo = listOf(
            "Stand with your feet together and knees straight but not locked.",
            "Breathe out and slowly reach down toward your toes. Don't bounce.",
            "Pick the picture closest to how far you reach without strain.",
        ),
        levels = listOf("Above the knees", "Knees", "Mid-shin", "Ankles", "Toes", "Past the toes", "Palms flat on the floor"),
        // Arms hang straight down until the hands reach the floor; then they reach to the toes or past.
        pictures = listOf(
            hang(120f),
            hang(103f),
            hang(79f),
            hang(55f),
            fold(42f, Vec(18f, 88.5f)),
            hang(49f),
            fold(30f, Vec(38f, 88.5f)),
        ),
        highlight = setOf(Segment.NearThigh),
    )

    val overheadReach = RomTest(
        key = "overhead_reach",
        name = "Overhead reach",
        region = Region.Shoulders,
        sided = false,
        howTo = listOf(
            "Stand tall with your arms by your sides.",
            "Keeping your elbows straight and your back from arching, lift both arms forward and up.",
            "Pick the picture closest to how high your arms go.",
        ),
        levels = listOf("Shoulder height", "Halfway up", "Nearly overhead", "Fully overhead"),
        pictures = listOf(90f, 125f, 155f, 180f).map { a -> Pose(nearShoulder = a, farShoulder = a - 2f, nearElbow = 2f, farElbow = 2f) },
        highlight = setOf(Segment.NearUpperArm),
    )

    /** Front knee drives forward over the toes with the heel down; [shin] = shin angle from vertical. */
    private fun lunge(shin: Float): Pose {
        val thigh = 40f
        val frontAnkleY = 44f * kotlin.math.cos(Math.toRadians(thigh.toDouble())).toFloat() + 43f * kotlin.math.cos(Math.toRadians(shin.toDouble())).toFloat()
        val back = -Math.toDegrees(acos((frontAnkleY / 87f).toDouble().coerceIn(-1.0, 1.0))).toFloat()
        return Pose(
            torso = 178f, nearShoulder = 75f, nearElbow = 10f, farShoulder = 72f, farElbow = 12f,
            nearHip = thigh, nearKnee = thigh + shin, nearAnkle = shin,
            farHip = back, farAnkle = 60f - (back + 90f),
        )
    }

    val ankleLunge = RomTest(
        key = "ankle_lunge",
        name = "Ankle lunge",
        region = Region.Legs,
        sided = true,
        howTo = listOf(
            "Stand in a short lunge, hands on a wall or chair if you like.",
            "Keeping your front heel on the floor, bend that knee forward as far as it goes.",
            "Pick the picture closest to where your knee ends up. The side named is the front leg.",
        ),
        levels = listOf("Knee stays behind the toes", "Knee over the toes", "Knee a little past the toes", "Knee well past the toes"),
        pictures = listOf(10f, 20f, 30f, 40f).map(::lunge),
        highlight = setOf(Segment.NearShin),
    )

    val legRaise = RomTest(
        key = "leg_raise",
        name = "Straight-leg raise",
        region = Region.Legs,
        sided = true,
        howTo = listOf(
            "Lie on your back with both legs long.",
            "Keeping the knee straight, lift one leg as high as you comfortably can. The other leg stays down.",
            "Pick the picture closest to how high the leg goes.",
        ),
        levels = listOf("Just off the floor", "A third of the way up", "Halfway up", "Most of the way up", "Straight up"),
        pictures = listOf(20f, 35f, 50f, 70f, 90f).map { a ->
            Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 90f + a, nearKnee = 2f, farHip = 90f)
        },
        highlight = setOf(Segment.NearThigh),
    )

    // Round 2 (owner, 2026-11-14). Back-scratch reach and neck turn need new figure abilities.
    val butterflyKnees = RomTest(
        key = "butterfly_knees",
        name = "Butterfly knees",
        region = Region.Hips,
        sided = false,
        howTo = listOf(
            "Sit tall with the soles of your feet together and heels in close.",
            "Let your knees drop toward the floor. Don't push them.",
            "Pick the picture closest to how low your knees rest.",
        ),
        levels = listOf("Knees well up", "Knees a little up", "Knees near the floor", "Knees on the floor"),
        pictures = listOf(20f, 32f, 44f, 58f).map { out ->
            Pose(torso = 178f, nearShoulder = 25f, nearElbow = 10f, farShoulder = 25f, farElbow = 12f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f,
                farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = out, farLegOut = out, nearLegTurn = 60f, farLegTurn = 60f)
        },
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        view = View.Front,
    )

    val seatedTwist = RomTest(
        key = "seated_twist",
        name = "Seated twist",
        region = Region.Core,
        sided = true,
        howTo = listOf(
            "Sit tall on the floor, legs out in front.",
            "Turn your chest toward the side named, keeping your hips still. Use your hands lightly.",
            "Pick the picture closest to how far your chest turns.",
        ),
        levels = listOf("A small turn", "A quarter turn", "Most of the way", "Shoulders sideways"),
        pictures = listOf(12f, 24f, 34f, 44f).map { t ->
            Pose(twist = t, nearShoulder = -30f * t / 40f, nearElbow = 4f, nearArmOut = 18f, farShoulder = 50f, farElbow = 30f, farArmOut = -35f, nearHip = 90f, farHip = 90f)
        },
        highlight = setOf(Segment.Torso),
        view = View.Above,
    )

    val kneeHug = RomTest(
        key = "knee_hug",
        name = "Knee hug",
        region = Region.Hips,
        sided = true,
        howTo = listOf(
            "Lie on your back on a firm surface, both legs long.",
            "Hug one knee to your chest. Let the other leg relax.",
            "Watch the straight leg. Pick the picture closest to it. The side named is the straight leg.",
        ),
        levels = listOf("Thigh lifts well off", "Thigh lifts a little", "Thigh stays down"),
        pictures = listOf(124f to 40f, 106f to 18f, 90f to 0f).map { (hip, knee) ->
            Pose(torso = -90f, head = -8f, nearShoulder = 46f, nearElbow = 12f, farShoulder = 42f, farElbow = 14f, nearHip = 200f, nearKnee = 115f, farHip = hip, farKnee = knee)
        },
        highlight = setOf(Segment.FarThigh),
    )

    val all: List<RomTest> = listOf(toeReach, overheadReach, ankleLunge, legRaise, butterflyKnees, seatedTwist, kneeHug)

    fun byKey(key: String): RomTest? = all.firstOrNull { it.key == key }

    /** Days between check-ins: Today offers one once a week (owner, 2026-11-14). */
    const val RECHECK_DAYS = 7
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
