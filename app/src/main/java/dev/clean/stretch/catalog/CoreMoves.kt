package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Core moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object CoreMoves {
    val sphinx = Stretch(
        key = "sphinx",
        name = "Sphinx stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Abdominals),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Lying,
        highlight = setOf(Segment.Torso),
        summary = "Lie on your front and prop yourself up on your forearms.",
        cues = listOf(
            "Lie on your front with your legs long and the tops of your feet down.",
            "Bring your elbows under your shoulders, forearms on the floor.",
            "Lift your chest gently and let your belly stay heavy.",
            "Feel the front of your body. Keep your shoulders away from your ears.",
        ),
        cautions = listOf("Lower down a little if you feel pinching in your lower back."),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 100f, head = 10f, nearShoulder = 130f, nearElbow = 40f, farShoulder = 130f, farElbow = 40f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            stretch = Pose(torso = 112f, spine = -4f, head = 22f, nearShoulder = 68f, nearElbow = 90f, farShoulder = 68f, farElbow = 90f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            deep = Pose(torso = 118f, spine = -6f, head = 26f, nearShoulder = 62f, nearElbow = 90f, farShoulder = 62f, farElbow = 90f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
        ),
    )

    val standingBackBend = Stretch(
        key = "standing_back_bend",
        name = "Standing back bend",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Abdominals),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Standing,
        highlight = setOf(Segment.Torso),
        summary = "Hands on your lower back, lift your chest and lean back a little.",
        cues = listOf(
            "Stand tall and place your hands on your lower back.",
            "Lift your chest toward the ceiling.",
            "Lean back only a little, keeping your neck long.",
            "Feel the front of your body. Breathe slowly.",
        ),
        cautions = listOf("Keep it small. Skip it if leaning back hurts your back."),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(torso = 190f, spine = -9f, head = 8f, nearShoulder = -40f, nearElbow = 50f, farShoulder = -38f, farElbow = 48f),
            deep = Pose(torso = 194f, spine = -11f, head = 10f, nearShoulder = -42f, nearElbow = 52f, farShoulder = -40f, farElbow = 50f),
        ),
    )

    val cobra = Stretch(
        key = "cobra",
        name = "Cobra stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Abdominals),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Lying,
        level = 2,
        highlight = setOf(Segment.Torso),
        summary = "Like the sphinx, but press up on your hands for a stronger stretch.",
        cues = listOf(
            "Lie on your front with your hands under your shoulders.",
            "Press up slowly, keeping your hips on the floor.",
            "Straighten your arms only as far as feels comfortable.",
            "Feel the front of your body. Keep your shoulders down.",
        ),
        cautions = listOf("Stay in the sphinx instead if you feel pinching in your lower back."),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 100f, head = 10f, nearShoulder = 130f, nearElbow = 40f, farShoulder = 130f, farElbow = 40f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            stretch = Pose(torso = 140f, spine = -12f, head = 25f, nearShoulder = 73f, farShoulder = 73f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            deep = Pose(torso = 145f, spine = -14f, head = 28f, nearShoulder = 62f, farShoulder = 62f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
        ),
    )

    val plank = Stretch(
        key = "plank",
        name = "Forearm plank",
        kind = StretchKind.Strengthen,
        sided = false,
        primary = listOf(Muscle.Abdominals),
        secondary = listOf(Muscle.Shoulders, Muscle.Glutes),
        position = Position.HandsAndKnees,
        level = 2,
        highlight = setOf(Segment.Torso),
        summary = "On your forearms and toes, hold your body in one straight line.",
        cues = listOf(
            "Start on your forearms and knees, elbows under your shoulders.",
            "Step your feet back so your body is one straight line from head to heels.",
            "Gently tighten your stomach and buttocks. Don't let your hips sag.",
            "Hold steady and keep breathing. Drop to your knees if you need to.",
        ),
        sourceIds = listOf("S9", "S30"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 96f, nearShoulder = 84f, nearElbow = 90f, farShoulder = 84f, farElbow = 90f, nearHip = -84f, nearAnkle = -6f, farHip = -84f, farAnkle = -6f),
            deep = Pose(torso = 96f, spine = -1f, nearShoulder = 84f, nearElbow = 90f, farShoulder = 84f, farElbow = 90f, nearHip = -84f, nearAnkle = -6f, farHip = -84f, farAnkle = -6f),
        ),
    )

    val torsoTurns = Stretch(
        key = "torso_turns",
        name = "Standing torso turns",
        kind = StretchKind.Dynamic,
        sided = false,
        primary = listOf(Muscle.Abdominals),
        secondary = listOf(Muscle.LowerBack, Muscle.UpperBack),
        position = Position.Standing,
        spinalTwist = true,
        highlight = setOf(Segment.Torso),
        summary = "Stand tall and turn your chest slowly from side to side.",
        cues = listOf(
            "Stand tall with your feet hip-width apart and knees soft.",
            "Bring your hands together in front of your chest.",
            "Turn your chest slowly to one side, then the other. Let your head follow.",
            "Keep your hips facing forward. Make the turns a little bigger as you warm up.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearShoulder = 80f, nearElbow = 100f, nearArmOut = -15f, farShoulder = 80f, farElbow = 100f, farArmOut = -15f, nearKnee = 6f, farKnee = 6f),
            stretch = Pose(twist = 35f, nearShoulder = 80f, nearElbow = 100f, nearArmOut = -15f, farShoulder = 80f, farElbow = 100f, farArmOut = -15f, nearKnee = 6f, farKnee = 6f),
            deep = Pose(twist = -35f, nearShoulder = 80f, nearElbow = 100f, nearArmOut = -15f, farShoulder = 80f, farElbow = 100f, farArmOut = -15f, nearKnee = 6f, farKnee = 6f),
            view = View.Front,
        ),
    )

    val all: List<Stretch> = listOf(sphinx, standingBackBend, cobra, plank, torsoTurns)
}
