package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Shoulders & chest moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object ShoulderMoves {
    val chestOpener = Stretch(
        key = "chest_opener",
        name = "Behind-the-back chest opener",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Chest),
        secondary = listOf(Muscle.Shoulders),
        position = Position.Standing,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "Clasp your hands behind you and gently lift them as your chest opens.",
        cues = listOf(
            "Stand tall and clasp your hands behind your back.",
            "Draw your shoulders back and down.",
            "Lift your hands slightly away from your back until you feel your chest.",
            "Keep your chin level and breathe.",
        ),
        cautions = listOf("Hold a towel between your hands if they don't reach each other."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = -12f, nearElbow = 10f, farShoulder = -10f, farElbow = 10f),
            stretch = Pose(spine = -5f, head = 6f, nearShoulder = -42f, nearElbow = 4f, farShoulder = -40f, farElbow = 4f),
            deep = Pose(spine = -7f, head = 8f, nearShoulder = -52f, nearElbow = 3f, farShoulder = -50f, farElbow = 3f),
        ),
    )

    val wallChest = Stretch(
        key = "wall_chest",
        name = "Wall chest stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Chest),
        secondary = listOf(Muscle.Shoulders, Muscle.Biceps),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.NearUpperArm, Segment.Torso),
        summary = "Rest one arm on a wall behind you and turn your chest gently away.",
        cues = listOf(
            "Stand beside a wall and place one palm on it, a little behind you at shoulder height.",
            "Keep the arm long and your shoulder down.",
            "Turn your chest slowly away from the wall.",
            "Feel the front of your chest and shoulder. Breathe slowly.",
        ),
        cautions = listOf("Lower your arm if you feel tingling down the arm."),
        sourceIds = listOf("S3", "S2"),
        prop = Prop(Equipment.Wall, x = -60f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = -40f, nearElbow = 10f, farElbow = 6f),
            stretch = Pose(torso = 176f, head = 4f, nearShoulder = -85f, nearElbow = 4f, farShoulder = 10f, farElbow = 10f),
            deep = Pose(torso = 173f, head = 5f, nearShoulder = -95f, nearElbow = 2f, farShoulder = 12f, farElbow = 10f),
        ),
    )

    val crossBodyShoulder = Stretch(
        key = "cross_body_shoulder",
        name = "Cross-body shoulder stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Shoulders),
        secondary = listOf(Muscle.UpperBack),
        position = Position.Standing,
        highlight = setOf(Segment.NearUpperArm),
        summary = "Bring one arm across your chest and hug it in with the other.",
        cues = listOf(
            "Stand or sit tall with your shoulders relaxed.",
            "Bring one arm straight across your chest.",
            "Hold it above the elbow with your other hand and draw it in.",
            "Feel the back of that shoulder. Keep the shoulder down.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(nearShoulder = 90f, nearElbow = 2f, farShoulder = 45f, farElbow = 95f, nearArmOut = -55f, farArmOut = -10f),
            deep = Pose(nearShoulder = 92f, nearElbow = 2f, farShoulder = 48f, farElbow = 100f, nearArmOut = -62f, farArmOut = -12f),
            view = View.Front,
        ),
    )

    val armSwings = Stretch(
        key = "arm_swings",
        name = "Arm swings",
        kind = StretchKind.Dynamic,
        sided = false,
        primary = listOf(Muscle.Shoulders),
        secondary = listOf(Muscle.Chest, Muscle.UpperBack),
        position = Position.Standing,
        highlight = setOf(Segment.NearUpperArm),
        summary = "Swing both arms up overhead and back down behind you, smoothly.",
        cues = listOf(
            "Stand tall with your knees soft.",
            "Swing both arms forward and up overhead.",
            "Let them swing down and a little behind you.",
            "Keep it smooth and easy, growing the range as you warm up.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(spine = -3f, nearShoulder = 170f, nearElbow = 4f, farShoulder = 168f, farElbow = 4f),
            deep = Pose(nearShoulder = -35f, nearElbow = 6f, farShoulder = -33f, farElbow = 6f),
        ),
    )

    val all: List<Stretch> = listOf(chestOpener, wallChest, crossBodyShoulder, armSwings)
}
