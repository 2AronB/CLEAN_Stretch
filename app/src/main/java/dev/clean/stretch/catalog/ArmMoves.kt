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
    // past the forearm (Pose.nearWrist); the thumb (Pose.nearPalm) shows palm up vs palm down.
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
            stretch = armForward.copy(nearWrist = -62f, nearPalm = -90f, farWrist = -20f),
            deep = armForward.copy(nearWrist = -74f, nearPalm = -90f, farWrist = -24f),
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
            stretch = armForward.copy(nearWrist = -70f, nearPalm = 90f, farWrist = -30f),
            deep = armForward.copy(nearWrist = -82f, nearPalm = 90f, farWrist = -34f),
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

    val all: List<Stretch> = listOf(overheadTriceps, wristFlexor, wristExtensor, prayerStretch)
}
