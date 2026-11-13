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

    val all: List<Stretch> = listOf(overheadTriceps)
}
