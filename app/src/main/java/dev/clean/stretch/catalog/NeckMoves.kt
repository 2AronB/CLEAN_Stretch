package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Neck moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object NeckMoves {
    val neckFlexion = Stretch(
        key = "neck_flexion",
        spinalFlexion = true,
        name = "Chin-to-chest neck stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Neck),
        secondary = listOf(Muscle.UpperBack),
        position = Position.Standing,
        highlight = setOf(Segment.Neck),
        summary = "Stand or sit tall and slowly lower your chin toward your chest.",
        cues = listOf(
            "Stand or sit tall with your shoulders relaxed.",
            "Slowly lower your chin toward your chest.",
            "Let the weight of your head do the work. Feel the back of your neck.",
            "Breathe slowly and keep your shoulders soft.",
        ),
        cautions = listOf("Move gently. Skip it if bending your neck causes pain or tingling in your arms."),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(spine = 4f, head = -36f, nearElbow = 6f, farElbow = 6f),
            deep = Pose(spine = 6f, head = -44f, nearElbow = 6f, farElbow = 6f),
        ),
    )

    val chinTuck = Stretch(
        key = "chin_tuck",
        name = "Chin tuck hold",
        kind = StretchKind.Strengthen,
        sided = false,
        primary = listOf(Muscle.Neck),
        secondary = listOf(Muscle.UpperBack),
        position = Position.Standing,
        highlight = setOf(Segment.Neck),
        summary = "Glide your head straight back, making a gentle double chin, and hold.",
        cues = listOf(
            "Stand or sit tall and look straight ahead.",
            "Glide your head straight back, as if making a double chin.",
            "Keep your eyes level. Don't tip your head down.",
            "Hold gently and keep breathing.",
        ),
        sourceIds = listOf("S19", "S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(head = -8f, spine = -2f, nearElbow = 6f, farElbow = 6f),
            deep = Pose(head = -10f, spine = -3f, nearElbow = 6f, farElbow = 6f),
        ),
    )

    val neckNods = Stretch(
        key = "neck_nods",
        name = "Slow neck nods",
        kind = StretchKind.Dynamic,
        sided = false,
        primary = listOf(Muscle.Neck),
        position = Position.Standing,
        highlight = setOf(Segment.Neck),
        summary = "Slowly nod your chin down toward your chest and back up to level.",
        cues = listOf(
            "Stand or sit tall with your shoulders relaxed.",
            "Slowly lower your chin toward your chest.",
            "Lift it back to looking straight ahead. Don't tip your head far back.",
            "Keep the movement slow and easy.",
        ),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(head = -30f, spine = 3f, nearElbow = 6f, farElbow = 6f),
            deep = Pose(head = 5f, nearElbow = 6f, farElbow = 6f),
        ),
    )

    val neckSideBend = Stretch(
        key = "neck_side_bend",
        name = "Ear-to-shoulder neck stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Neck),
        secondary = listOf(Muscle.Shoulders),
        position = Position.Standing,
        highlight = setOf(Segment.Neck),
        summary = "Tip one ear toward your shoulder and let the other shoulder stay heavy.",
        cues = listOf(
            "Stand or sit tall with your shoulders relaxed.",
            "Slowly tip one ear toward the shoulder on that side. Keep your nose facing forward.",
            "Let the other shoulder stay low, its hand reaching toward the floor.",
            "Feel the side of your neck. Breathe slowly.",
        ),
        cautions = listOf("Keep it gentle. Don't pull on your head with your hand."),
        sourceIds = listOf("S19", "S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(headTilt = -32f, nearElbow = 4f, farElbow = 6f, nearArmOut = 6f),
            deep = Pose(headTilt = -38f, nearElbow = 2f, farElbow = 6f, nearArmOut = 8f),
            view = View.Front,
        ),
    )

    val all: List<Stretch> = listOf(neckFlexion, chinTuck, neckNods, neckSideBend)
}
