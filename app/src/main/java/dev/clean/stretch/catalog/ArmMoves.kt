package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Arms & wrists moves (biceps, triceps, forearms and wrists): every move whose first main muscle
 * is in this area. Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object ArmMoves {
    val overheadTriceps = Stretch(
        key = "overhead_triceps",
        name = "Overhead triceps stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Triceps),
        secondary = listOf(Muscle.Lats, Muscle.Shoulders),
        position = Position.Standing,
        highlight = setOf(Segment.NearUpperArm),
        summary = "Reach one hand down behind your head and ease that elbow back with the other hand.",
        cues = listOf(
            "Stand or sit tall.",
            "Raise one arm, bend the elbow and let your hand drop behind your head.",
            "With the other hand, ease that elbow gently back and in.",
            "Feel the back of your upper arm and under your arm. Keep your head up.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(nearShoulder = 172f, nearElbow = 145f, nearArmOut = -8f, farShoulder = 150f, farElbow = 120f, farArmOut = -30f),
            deep = Pose(nearShoulder = 178f, nearElbow = 150f, nearArmOut = -10f, farShoulder = 154f, farElbow = 124f, farArmOut = -32f),
            view = View.Angled,
        ),
    )

    // Forearms & wrists (owner, 2026-11-13). The figure shows the bent wrist with the hand drawn
    // past the forearm (Pose.nearWrist). Palm up vs palm down can't be seen on the figure; the
    // cues say which.
    private val armForward = Pose(nearShoulder = 82f, nearElbow = 6f, nearArmOut = -18f, farShoulder = 82f, farElbow = 12f, farArmOut = -16f)

    val wristFlexor = Stretch(
        key = "wrist_flexor",
        name = "Wrist flexor stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Forearms),
        position = Position.Standing,
        highlight = setOf(Segment.NearForearm),
        summary = "Reach one arm forward, palm up, and gently draw the fingers back to feel the inside of your forearm.",
        cues = listOf(
            "Stand or sit tall and reach one arm out in front, palm up.",
            "With your other hand, gently draw your fingers down and back toward you.",
            "Keep the elbow straight but not locked.",
            "Feel the inside of your forearm. Keep it gentle.",
        ),
        cautions = listOf("Ease off if you feel tingling or numbness in your hand."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = armForward.copy(nearWrist = -62f, farWrist = -20f),
            deep = armForward.copy(nearWrist = -74f, farWrist = -24f),
            view = View.Angled,
        ),
    )

    val wristExtensor = Stretch(
        key = "wrist_extensor",
        name = "Wrist extensor stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Forearms),
        position = Position.Standing,
        highlight = setOf(Segment.NearForearm),
        summary = "Reach one arm forward, palm down, let the hand drop and gently press it toward you to feel the top of your forearm.",
        cues = listOf(
            "Stand or sit tall and reach one arm out in front, palm down.",
            "Let your hand drop so your fingers point to the floor.",
            "With your other hand, gently press the back of that hand toward you.",
            "Feel the top of your forearm. Keep the elbow straight but not locked.",
        ),
        cautions = listOf("Ease off if you feel tingling or numbness in your hand."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = armForward.copy(nearWrist = -70f, farWrist = -30f),
            deep = armForward.copy(nearWrist = -82f, farWrist = -34f),
            view = View.Angled,
        ),
    )

    val prayerStretch = Stretch(
        key = "prayer_stretch",
        name = "Prayer stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Forearms),
        position = Position.Standing,
        highlight = setOf(Segment.NearForearm, Segment.FarForearm),
        summary = "Press your palms together in front of your chest, then lower your hands until you feel the inside of your wrists.",
        cues = listOf(
            "Press your palms together in front of your chest, fingers pointing up.",
            "Keeping your palms together, slowly lower your hands toward your waist.",
            "Stop when you feel a gentle stretch along the inside of your wrists and forearms.",
            "Keep your shoulders relaxed and down.",
        ),
        cautions = listOf("Ease off if you feel tingling or numbness in your hands."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(
                nearShoulder = 36f, nearElbow = 86f, nearArmOut = 10f, nearArmTurn = -53f, nearWrist = 86f,
                farShoulder = 36f, farElbow = 86f, farArmOut = 10f, farArmTurn = -53f, farWrist = 86f,
            ),
            deep = Pose(
                nearShoulder = 12f, nearElbow = 88f, nearArmOut = 12f, nearArmTurn = -53f, nearWrist = 92f,
                farShoulder = 12f, farElbow = 88f, farArmOut = 12f, farArmTurn = -53f, farWrist = 92f,
            ),
            view = View.Front,
        ),
    )

    // Biceps (PLAN §8.2, 2026-11-14). All three reach straight arms behind the body, which is how
    // the biceps is stretched. Hands that rest on the wall or floor were placed numerically.
    val wallBiceps = Stretch(
        key = "wall_biceps",
        name = "Wall biceps stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Biceps),
        secondary = listOf(Muscle.Shoulders, Muscle.Chest),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.NearUpperArm, Segment.FarUpperArm),
        summary = "With your back to a wall, rest both palms on it behind you and slowly bend your knees.",
        cues = listOf(
            "Stand with your back to a wall, a small step away from it.",
            "Reach both arms straight back and rest your palms on the wall at about hip height, fingers pointing down.",
            "Keep your chest lifted and slowly bend your knees, leaving your hands where they are.",
            "Feel the front of your upper arms and shoulders. Keep it gentle.",
        ),
        cautions = listOf("Ease off if the front of your shoulders pinches or you feel tingling down your arms."),
        sourceIds = listOf("S3", "S2"),
        prop = Prop(Equipment.Wall, x = -44f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = -20f, nearElbow = 6f, farShoulder = -20f, farElbow = 6f),
            stretch = Pose(nearShoulder = -45f, nearElbow = 2f, nearWrist = -45f, farShoulder = -45f, farElbow = 2f, farWrist = -45f, nearHip = 8f, nearKnee = 16f, nearAnkle = 8f, farHip = 8f, farKnee = 16f, farAnkle = 8f),
            deep = Pose(shiftX = 4f, nearShoulder = -51.3f, nearElbow = 2f, nearWrist = -51f, farShoulder = -51.3f, farElbow = 2f, farWrist = -51f, nearHip = 17f, nearKnee = 40f, nearAnkle = 23f, farHip = 17f, farKnee = 40f, farAnkle = 23f),
            view = View.Angled,
        ),
    )

    val standingBiceps = Stretch(
        key = "standing_biceps",
        name = "Standing biceps stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Biceps),
        secondary = listOf(Muscle.Shoulders, Muscle.Chest),
        position = Position.Standing,
        highlight = setOf(Segment.NearUpperArm, Segment.FarUpperArm),
        summary = "Clasp your hands behind you with straight arms, tip forward a little from your hips and let your arms lift.",
        cues = listOf(
            "Stand tall with your knees soft and clasp your hands behind your back.",
            "Straighten your arms and turn your palms to face down.",
            "Tip forward a little from your hips, back long, and let your arms lift away from your body.",
            "Feel the front of your upper arms and shoulders. Keep breathing.",
        ),
        cautions = listOf(
            "Hold a towel between your hands if they don't reach each other.",
            "Come up slowly, and stay upright instead if tipping forward makes you light-headed.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = -12f, nearElbow = 10f, farShoulder = -12f, farElbow = 10f),
            stretch = Pose(torso = 165f, nearShoulder = -42f, nearElbow = 3f, nearArmOut = -21f, farShoulder = -42f, farElbow = 3f, farArmOut = -21f, nearHip = 4f, nearKnee = 8f, nearAnkle = 4f, farHip = 4f, farKnee = 8f, farAnkle = 4f),
            deep = Pose(torso = 157f, nearShoulder = -52f, nearElbow = 3f, nearArmOut = -21f, farShoulder = -52f, farElbow = 3f, farArmOut = -21f, nearHip = 4f, nearKnee = 8f, nearAnkle = 4f, farHip = 4f, farKnee = 8f, farAnkle = 4f),
        ),
    )

    val seatedBiceps = Stretch(
        key = "seated_biceps",
        name = "Seated biceps stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Biceps),
        secondary = listOf(Muscle.Shoulders, Muscle.Chest),
        position = Position.Seated,
        level = 2,
        highlight = setOf(Segment.NearUpperArm, Segment.FarUpperArm),
        summary = "Sit with your knees bent and your hands on the floor behind you, then slide your hips toward your feet.",
        cues = listOf(
            "Sit with your knees bent and your feet flat on the floor.",
            "Place your hands on the floor behind you, fingers pointing away from you.",
            "Keeping your hands still, slowly slide your hips forward toward your feet.",
            "Feel the front of your upper arms and shoulders. Keep your chest lifted.",
        ),
        cautions = listOf("Stop sliding as soon as you feel the stretch. Ease off if the front of your shoulders pinches."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 190f, nearShoulder = -20.1f, nearElbow = 3f, farShoulder = -20.1f, farElbow = 3f, nearHip = 137f, nearKnee = 106f, nearAnkle = -31f, farHip = 137f, farKnee = 106f, farAnkle = -31f),
            stretch = Pose(torso = 205f, nearShoulder = -46.9f, nearElbow = 3f, nearWrist = 68f, farShoulder = -46.9f, farElbow = 3f, farWrist = 68f, nearHip = 137f, nearKnee = 106f, nearAnkle = -31f, farHip = 137f, farKnee = 106f, farAnkle = -31f),
            deep = Pose(torso = 212f, shiftX = 11f, nearShoulder = -61.3f, nearElbow = 3f, nearWrist = 61f, farShoulder = -61.3f, farElbow = 3f, farWrist = 61f, nearHip = 144f, nearKnee = 123.5f, nearAnkle = -20.5f, farHip = 144f, farKnee = 123.5f, farAnkle = -20.5f),
        ),
    )

    val all: List<Stretch> = listOf(overheadTriceps, wristFlexor, wristExtensor, prayerStretch, wallBiceps, standingBiceps, seatedBiceps)
}
