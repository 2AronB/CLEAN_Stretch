package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment

/**
 * Slice S1 sample catalog: six stretches that exercise every part of the figure engine
 * (floor, kneeling, standing, a wall prop, holds and a flow). Slice S3 grows this to ~30.
 * Poses were tuned with tools/pose-preview and Pose Studio.
 */
object SampleCatalog {

    val seatedForwardFold = Stretch(
        key = "seated_forward_fold",
        name = "Seated forward fold",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Hamstrings),
        secondary = listOf(Muscle.Calves, Muscle.LowerBack),
        position = Position.Seated,
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        summary = "Sit tall with legs long, then hinge forward from the hips toward your shins.",
        cues = listOf(
            "Sit tall with your legs straight out in front.",
            "Hinge forward from your hips, reaching toward your shins or feet.",
            "Ease in until you feel mild tension behind your thighs — never pain.",
            "Breathe slowly. Let each exhale take you a little deeper.",
        ),
        cautions = listOf("Bend your knees slightly if you feel it in your lower back."),
        sourceIds = listOf("S12", "S13", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 38f, nearElbow = 28f, farShoulder = 34f, farElbow = 30f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = 126f, spine = 11f, head = -12f, nearShoulder = 124f, nearElbow = 8f, farShoulder = 122f, farElbow = 10f, nearHip = 90f, nearAnkle = 8f, farHip = 90f, farAnkle = 8f),
            deep = Pose(torso = 118f, spine = 14f, head = -15f, nearShoulder = 131f, nearElbow = 6f, farShoulder = 130f, farElbow = 8f, nearHip = 90f, nearAnkle = 12f, farHip = 90f, farAnkle = 12f),
        ),
    )

    val standingQuad = Stretch(
        key = "standing_quad",
        name = "Standing quad stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Quadriceps),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Stand tall, bend one knee and hold your ankle behind you.",
        cues = listOf(
            "Stand tall. Hold a wall or chair for balance if you like.",
            "Bend one knee and hold that ankle behind you.",
            "Keep your knees close together and gently tuck your hips under.",
            "You should feel the front of your thigh. Keep breathing.",
        ),
        cautions = listOf("If you can't reach your ankle comfortably, loop a towel around it."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(torso = 179f, nearShoulder = -17f, nearElbow = 10f, farShoulder = 70f, farElbow = 18f, nearHip = -4f, nearKnee = 158f, nearAnkle = -35f, farKnee = 3f),
            deep = Pose(torso = 177f, nearShoulder = -22f, nearElbow = 8f, farShoulder = 72f, farElbow = 18f, nearHip = -11f, nearKnee = 160f, nearAnkle = -38f, farKnee = 3f),
        ),
    )

    val halfKneelingHipFlexor = Stretch(
        key = "half_kneeling_hip_flexor",
        name = "Half-kneeling hip flexor",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.HipFlexors),
        secondary = listOf(Muscle.Quadriceps),
        position = Position.Kneeling,
        highlight = setOf(Segment.NearThigh, Segment.Torso),
        summary = "Kneel on one knee, tuck your hips under and shift gently forward.",
        cues = listOf(
            "Kneel on one knee with the other foot flat in front of you.",
            "Tuck your hips under, as if zipping up tight jeans.",
            "Shift forward until you feel the front of your back hip.",
            "Reach the same-side arm overhead to feel a little more.",
        ),
        cautions = listOf("Put a folded towel under your knee if the floor is hard."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 40f, nearElbow = 30f, farShoulder = 46f, farElbow = 34f, nearHip = -5f, nearKnee = 85f, nearAnkle = -95f, farHip = 88f, farKnee = 88f),
            stretch = Pose(torso = 182f, nearShoulder = 165f, nearElbow = 10f, farShoulder = 50f, farElbow = 40f, nearHip = -30f, nearKnee = 60f, nearAnkle = -95f, farHip = 92f, farKnee = 117f, farAnkle = 25f),
            deep = Pose(torso = 185f, nearShoulder = 172f, nearElbow = 8f, farShoulder = 52f, farElbow = 42f, nearHip = -36f, nearKnee = 54f, nearAnkle = -95f, farHip = 95f, farKnee = 123f, farAnkle = 28f),
        ),
    )

    val childsPose = Stretch(
        key = "childs_pose",
        name = "Child's pose",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Lats, Muscle.LowerBack),
        secondary = listOf(Muscle.Shoulders, Muscle.Glutes),
        position = Position.Kneeling,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "Sit back toward your heels and reach your arms long on the floor.",
        cues = listOf(
            "Start kneeling, then sit your hips back toward your heels.",
            "Walk your hands forward and rest your forehead down.",
            "Reach long through your fingertips.",
            "Let your breathing slow down.",
        ),
        cautions = listOf("Place a cushion between your heels and hips if your knees complain."),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f, nearElbow = 6f, farElbow = 6f),
            stretch = Pose(torso = 88f, spine = 8f, head = -6f, nearShoulder = 176f, nearElbow = 4f, farShoulder = 174f, farElbow = 4f, nearHip = 70f, nearKnee = 160f, nearAnkle = -90f, farHip = 70f, farKnee = 160f, farAnkle = -90f),
            deep = Pose(torso = 90f, spine = 10f, head = -8f, nearShoulder = 182f, nearElbow = 2f, farShoulder = 180f, farElbow = 2f, nearHip = 72f, nearKnee = 162f, nearAnkle = -90f, farHip = 72f, farKnee = 162f, farAnkle = -90f),
        ),
    )

    val wallCalf = Stretch(
        key = "wall_calf",
        name = "Wall calf stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Calves),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.NearShin),
        summary = "Hands on a wall, one leg back with the heel pressing down.",
        cues = listOf(
            "Face a wall with your hands on it at shoulder height.",
            "Step one foot back and keep that leg straight.",
            "Press the back heel into the floor and lean toward the wall.",
            "Bend the back knee slightly to move the stretch lower in the calf.",
        ),
        sourceIds = listOf("S3", "S2"),
        prop = Prop(Equipment.Wall, x = 74f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 174f, nearShoulder = 95f, nearElbow = 25f, farShoulder = 92f, farElbow = 28f),
            stretch = Pose(torso = 160f, nearShoulder = 112f, nearElbow = 16f, farShoulder = 110f, farElbow = 18f, nearHip = -20f, nearAnkle = 20f, farHip = 18f, farKnee = 30f, farAnkle = 12f),
            deep = Pose(torso = 156f, nearShoulder = 114f, nearElbow = 20f, farShoulder = 112f, farElbow = 22f, nearHip = -24f, nearAnkle = 24f, farHip = 20f, farKnee = 36f, farAnkle = 16f),
        ),
    )

    val catCow = Stretch(
        key = "cat_cow",
        name = "Cat–cow",
        kind = StretchKind.Dynamic,
        sided = false,
        primary = listOf(Muscle.UpperBack, Muscle.LowerBack),
        secondary = listOf(Muscle.Abdominals, Muscle.Neck),
        position = Position.HandsAndKnees,
        highlight = setOf(Segment.Torso),
        summary = "On hands and knees, slowly round and then arch your back.",
        cues = listOf(
            "Start on hands and knees, hands under shoulders.",
            "Breathe in as you let your belly drop and lift your gaze.",
            "Breathe out as you round your back toward the ceiling.",
            "Move slowly and stay in a comfortable range.",
        ),
        sourceIds = listOf("S3"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 103f, spine = -14f, head = 22f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            deep = Pose(torso = 103f, spine = 16f, head = -30f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
        ),
    )

    val all: List<Stretch> = listOf(seatedForwardFold, standingQuad, halfKneelingHipFlexor, childsPose, wallCalf, catCow)

    fun byKey(key: String): Stretch? = all.firstOrNull { it.key == key }
}
