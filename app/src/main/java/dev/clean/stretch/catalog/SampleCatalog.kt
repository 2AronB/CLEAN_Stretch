package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pin
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment

/**
 * Sample catalog: the six S1 stretches that exercise every part of the figure engine
 * (floor, kneeling, standing, a wall prop, holds and a flow), plus ten more covering neck,
 * chest, lats, glutes, inner thigh, soleus, core, and lying/hands-and-feet hamstring options. Slice S3 grows this to ~30.
 * Poses were tuned with tools/pose-preview and Pose Studio.
 */
object SampleCatalog {

    val seatedForwardFold = Stretch(
        key = "seated_forward_fold",
        spinalFlexion = true,
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
        spinalFlexion = true,
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
        spinalFlexion = true,
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

    // ---- Added 2026-11-08 (round 2): neck, chest, lats, glutes, inner thigh, soleus, standing hamstring.
    // Instructions are Editorial (standard exercise technique); hold defaults come from [S2][S3].

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

    val wallLat = Stretch(
        key = "wall_lat",
        name = "Wall lat stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Lats),
        secondary = listOf(Muscle.Shoulders, Muscle.Hamstrings),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "Hands on a wall, walk back and hinge until your back and arms make one long line.",
        cues = listOf(
            "Face a wall and place your hands on it a little above hip height.",
            "Walk your feet back and hinge at your hips.",
            "Let your chest sink toward the floor between your arms.",
            "Feel the sides of your back and under your arms. Breathe slowly.",
        ),
        cautions = listOf("Bend your knees a little if you feel it mostly behind your thighs."),
        sourceIds = listOf("S3", "S2"),
        prop = Prop(Equipment.Wall, x = 108f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 140f, nearShoulder = 130f, nearElbow = 10f, farShoulder = 128f, farElbow = 12f),
            stretch = Pose(torso = 95f, spine = -3f, nearShoulder = 175f, nearElbow = 2f, farShoulder = 173f, farElbow = 4f),
            deep = Pose(torso = 90f, spine = -5f, head = 4f, nearShoulder = 182f, nearElbow = 0f, farShoulder = 180f, farElbow = 2f),
        ),
    )

    val kneeToChest = Stretch(
        key = "knee_to_chest",
        spinalFlexion = true,
        name = "Knee-to-chest stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Glutes),
        secondary = listOf(Muscle.LowerBack),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "Lie on your back and hug one knee gently toward your chest.",
        cues = listOf(
            "Lie on your back with both legs long.",
            "Bring one knee up and hold it with both hands.",
            "Draw the knee gently toward your chest. Keep the other leg relaxed.",
            "Feel the back of your hip and lower back. Breathe slowly.",
        ),
        cautions = listOf("Hold behind your thigh instead of your shin if your knee feels uncomfortable."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, head = -6f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = -90f, head = -8f, nearShoulder = 46f, nearElbow = 12f, farShoulder = 42f, farElbow = 14f, nearHip = 195f, nearKnee = 115f, farHip = 90f),
            deep = Pose(torso = -90f, head = -10f, nearShoulder = 52f, nearElbow = 16f, farShoulder = 48f, farElbow = 18f, nearHip = 205f, nearKnee = 115f, farHip = 90f),
        ),
    )

    val butterfly = Stretch(
        key = "butterfly",
        name = "Butterfly stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Adductors),
        secondary = listOf(Muscle.Glutes),
        position = Position.Seated,
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        summary = "Sit with the soles of your feet together and let your knees fall open.",
        cues = listOf(
            "Sit tall and bring the soles of your feet together.",
            "Hold your ankles and let your knees drop out to the sides.",
            "Hinge forward a little from your hips, keeping your back long.",
            "Feel your inner thighs. Let them relax with each breath out.",
        ),
        cautions = listOf("Move your feet further from your body if your knees or hips complain."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 4f, nearElbow = 8f, farShoulder = 4f, farElbow = 8f, nearHip = 118f, nearKnee = 168f, nearAnkle = 50f, farHip = 116f, farKnee = 166f, farAnkle = 50f),
            stretch = Pose(torso = 165f, spine = 3f, head = -6f, nearShoulder = 3f, nearElbow = 10f, farShoulder = 2f, farElbow = 12f, nearHip = 118f, nearKnee = 168f, nearAnkle = 50f, farHip = 116f, farKnee = 166f, farAnkle = 50f),
            deep = Pose(torso = 155f, spine = 5f, head = -8f, nearShoulder = 4f, nearElbow = 15f, farShoulder = 3f, farElbow = 17f, nearHip = 118f, nearKnee = 168f, nearAnkle = 50f, farHip = 116f, farKnee = 166f, farAnkle = 50f),
        ),
    )

    val bentKneeCalf = Stretch(
        key = "bent_knee_calf",
        name = "Bent-knee calf stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Calves),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.NearShin),
        summary = "Like the wall calf stretch, but bend the back knee to reach the lower calf.",
        cues = listOf(
            "Face a wall with your hands on it at shoulder height.",
            "Step one foot back, a little shorter than for the straight-leg version.",
            "Keep the back heel down and bend that knee.",
            "Feel it low in the calf, above the heel. Breathe slowly.",
        ),
        sourceIds = listOf("S3", "S2"),
        prop = Prop(Equipment.Wall, x = 72f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 174f, nearShoulder = 95f, nearElbow = 25f, farShoulder = 92f, farElbow = 28f),
            stretch = Pose(torso = 166f, nearShoulder = 108f, nearElbow = 14f, farShoulder = 106f, farElbow = 16f, nearHip = -10f, nearKnee = 30f, nearAnkle = 40f, farHip = 35f, farKnee = 40f, farAnkle = 5f),
            deep = Pose(torso = 162f, nearShoulder = 110f, nearElbow = 16f, farShoulder = 108f, farElbow = 18f, nearHip = -10f, nearKnee = 34f, nearAnkle = 44f, farHip = 40f, farKnee = 50f, farAnkle = 10f),
        ),
    )

    val standingHamstring = Stretch(
        key = "standing_hamstring",
        name = "Standing hamstring hinge",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Hamstrings),
        secondary = listOf(Muscle.Calves),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Heel forward, toes up, then hinge from your hips with a long back.",
        cues = listOf(
            "Stand tall and place one heel a small step in front, toes up.",
            "Bend the back knee slightly and rest your hands on your thighs.",
            "Hinge forward from your hips, keeping your back long rather than rounded.",
            "Feel the back of the front thigh. Breathe slowly.",
        ),
        cautions = listOf("Hold a wall or chair for balance if you need to."),
        sourceIds = listOf("S13", "S12", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f, nearHip = 25f, nearAnkle = 25f, farHip = -5f, farKnee = 30f, farAnkle = 35f),
            stretch = Pose(torso = 112f, head = -4f, nearShoulder = 45f, nearElbow = 6f, farShoulder = 42f, farElbow = 8f, nearHip = 25f, nearAnkle = 25f, farHip = -5f, farKnee = 30f, farAnkle = 35f),
            deep = Pose(torso = 104f, spine = 2f, head = -6f, nearShoulder = 40f, nearElbow = 6f, farShoulder = 38f, farElbow = 8f, nearHip = 25f, nearAnkle = 28f, farHip = -5f, farKnee = 30f, farAnkle = 35f),
        ),
    )

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

    val lyingHamstring = Stretch(
        key = "lying_hamstring",
        name = "Lying hamstring stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Hamstrings),
        secondary = listOf(Muscle.Calves),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "Lie on your back and raise one straight leg, holding behind the thigh.",
        cues = listOf(
            "Lie on your back with both legs long.",
            "Raise one leg and hold behind the thigh with both hands.",
            "Keep that knee almost straight and draw the leg gently toward you.",
            "Feel the back of the thigh. Let your head and shoulders rest.",
        ),
        cautions = listOf("Loop a towel around your foot if you can't reach your thigh comfortably."),
        sourceIds = listOf("S13", "S12", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = -90f, head = -4f, nearShoulder = 23f, nearElbow = 8f, farShoulder = 21f, farElbow = 10f, nearHip = 178f, nearKnee = 4f, farHip = 90f),
            deep = Pose(torso = -90f, head = -6f, nearShoulder = 26f, nearElbow = 10f, farShoulder = 24f, farElbow = 12f, nearHip = 188f, nearKnee = 4f, farHip = 90f),
        ),
    )

    val downwardDog = Stretch(
        key = "downward_dog",
        name = "Downward dog",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Hamstrings, Muscle.Calves),
        secondary = listOf(Muscle.Shoulders, Muscle.Lats),
        position = Position.HandsAndKnees,
        level = 2,
        highlight = setOf(Segment.NearThigh, Segment.NearShin),
        summary = "From hands and knees, lift your hips high so your body makes an upside-down V.",
        cues = listOf(
            "Start on hands and knees, hands a little in front of your shoulders.",
            "Tuck your toes and lift your hips up and back.",
            "Press the floor away and let your heels sink toward it.",
            "Feel the backs of your legs and under your arms. Breathe slowly.",
        ),
        cautions = listOf("Bend your knees as much as you need to keep your back long."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 40f, spine = -3f, nearShoulder = 180f, farShoulder = 178f, nearHip = -25f, nearAnkle = 25f, farHip = -25f, farAnkle = 25f),
            deep = Pose(torso = 41f, spine = -4f, nearShoulder = 182f, farShoulder = 180f, nearHip = -22f, nearAnkle = 22f, farHip = -22f, farAnkle = 22f),
        ),
    )

    // ---- Round 3 (S3): standing options so "no floor" and Desk routines have more to choose from.

    val upperBack = Stretch(
        key = "standing_upper_back",
        spinalFlexion = true,
        name = "Standing upper-back stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.UpperBack),
        secondary = listOf(Muscle.Shoulders),
        position = Position.Standing,
        highlight = setOf(Segment.Torso),
        summary = "Reach your arms forward and round your upper back, like hugging a big ball.",
        cues = listOf(
            "Stand tall with your knees soft.",
            "Reach both arms forward at shoulder height and clasp your hands.",
            "Round your upper back and let your head drop a little.",
            "Feel between your shoulder blades. Breathe into your back.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(torso = 176f, spine = 14f, head = -20f, nearShoulder = 90f, nearElbow = 10f, farShoulder = 88f, farElbow = 12f),
            deep = Pose(torso = 174f, spine = 18f, head = -25f, nearShoulder = 95f, nearElbow = 8f, farShoulder = 93f, farElbow = 10f),
        ),
    )

    val standingLunge = Stretch(
        key = "standing_lunge_hip_flexor",
        name = "Standing lunge hip flexor",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.HipFlexors),
        secondary = listOf(Muscle.Quadriceps),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "A long step back, tuck your hips under and feel the front of the back hip.",
        cues = listOf(
            "Stand tall and take a long step back with one foot, heel lifted.",
            "Bend your front knee a little and keep your back leg long.",
            "Tuck your hips under and squeeze the back-leg buttock.",
            "Reach the back-leg arm overhead. Feel the front of that hip.",
        ),
        cautions = listOf("Hold a wall or chair for balance if you need to."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(torso = 182f, spine = -3f, nearShoulder = 160f, nearElbow = 8f, farShoulder = 30f, farElbow = 20f, nearHip = -25f, nearKnee = 15f, farHip = 25f, farKnee = 30f, farAnkle = 5f),
            deep = Pose(torso = 184f, spine = -4f, nearShoulder = 168f, nearElbow = 6f, farShoulder = 32f, farElbow = 22f, nearHip = -30f, nearKnee = 18f, nearAnkle = -10f, farHip = 30f, farKnee = 45f, farAnkle = 15f),
        ),
    )

    val overheadReach = Stretch(
        key = "overhead_reach",
        name = "Overhead reach",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Lats),
        secondary = listOf(Muscle.Shoulders),
        position = Position.Standing,
        highlight = setOf(Segment.NearUpperArm, Segment.Torso),
        summary = "Reach both arms up and grow tall through your sides.",
        cues = listOf(
            "Stand tall with your feet hip-width apart.",
            "Reach both arms overhead, hands shoulder-width or clasped.",
            "Grow tall from your ribs to your fingertips without arching your lower back.",
            "Feel your sides and under your arms. Breathe slowly.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(spine = -3f, head = 6f, nearShoulder = 175f, nearElbow = 4f, farShoulder = 172f, farElbow = 6f),
            deep = Pose(torso = 182f, spine = -5f, head = 8f, nearShoulder = 182f, nearElbow = 2f, farShoulder = 179f, farElbow = 4f),
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

    val standingKneeHug = Stretch(
        key = "standing_knee_hug",
        name = "Standing knee hug",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Glutes),
        secondary = listOf(Muscle.LowerBack),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Stand tall and hug one knee up toward your chest.",
        cues = listOf(
            "Stand tall, near a wall if you'd like support.",
            "Lift one knee and hold it just below the knee with both hands.",
            "Draw it gently toward your chest while standing tall.",
            "Feel the back of that hip. Breathe slowly.",
        ),
        cautions = listOf("Hold a wall with one hand if you feel wobbly."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(nearShoulder = 53f, nearElbow = 30f, farShoulder = 50f, farElbow = 34f, nearHip = 120f, nearKnee = 120f, nearAnkle = -10f),
            deep = Pose(nearShoulder = 58f, nearElbow = 34f, farShoulder = 55f, farElbow = 38f, nearHip = 130f, nearKnee = 125f, nearAnkle = -10f),
        ),
    )

    // ---- Round 4 (S3): easier/harder versions and remaining gaps (quads, chest, shoulders, core).

    val sideLyingQuad = Stretch(
        key = "side_lying_quad",
        name = "Side-lying quad stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Quadriceps),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "Lie on your side and pull your top heel toward your buttock. No balancing needed.",
        cues = listOf(
            "Lie on your side with your head resting on your lower arm.",
            "Bend your top knee and hold that ankle behind you.",
            "Keep your knees close together and gently push your hips forward.",
            "Feel the front of your thigh. Breathe slowly.",
        ),
        cautions = listOf("Loop a towel around your ankle if you can't reach it."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            // Drawn as the standing quad stretch laid down: thigh pivots back, heel toward buttock,
            // hand reaches back for the ankle. Knee stays within a real range (no >170°).
            start = Pose(torso = 90f, head = 8f, nearShoulder = 0f, nearElbow = 6f, farShoulder = 178f, farElbow = 4f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            stretch = Pose(torso = 90f, head = 8f, nearShoulder = -17f, nearElbow = 10f, farShoulder = 178f, farElbow = 4f, nearHip = -94f, nearKnee = 158f, nearAnkle = -35f, farHip = -90f, farAnkle = -90f),
            deep = Pose(torso = 90f, head = 8f, nearShoulder = -20f, nearElbow = 8f, farShoulder = 178f, farElbow = 4f, nearHip = -100f, nearKnee = 160f, nearAnkle = -38f, farHip = -90f, farAnkle = -90f),
        ),
    )

    val kneelingQuad = Stretch(
        key = "kneeling_quad",
        name = "Kneeling quad stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Quadriceps),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Kneeling,
        level = 2,
        highlight = setOf(Segment.NearThigh),
        summary = "From a half-kneel, reach back and draw your back foot up toward your buttock.",
        cues = listOf(
            "Kneel on one knee with the other foot flat in front of you.",
            "Tuck your hips under, as if zipping up tight jeans.",
            "Reach back with the same-side hand and lift your back foot.",
            "Feel the front of the back thigh. Ease off if your knee complains.",
        ),
        cautions = listOf("Put a folded towel under your knee. Use a towel around the foot if you can't reach it."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 40f, nearElbow = 30f, farShoulder = 46f, farElbow = 34f, nearHip = -5f, nearKnee = 85f, nearAnkle = -95f, farHip = 88f, farKnee = 88f),
            stretch = Pose(torso = 182f, nearShoulder = -26f, nearElbow = 5f, farShoulder = 50f, farElbow = 40f, nearHip = -30f, nearKnee = 150f, nearAnkle = -20f, farHip = 92f, farKnee = 117f, farAnkle = 25f),
            deep = Pose(torso = 184f, nearShoulder = -22f, nearElbow = 5f, farShoulder = 52f, farElbow = 42f, nearHip = -35f, nearKnee = 155f, nearAnkle = -20f, farHip = 95f, farKnee = 123f, farAnkle = 28f),
        ),
    )

    val towelCalf = Stretch(
        key = "towel_calf",
        name = "Seated towel calf stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Calves),
        secondary = listOf(Muscle.Hamstrings),
        position = Position.Seated,
        equipment = Equipment.Strap,
        highlight = setOf(Segment.NearShin),
        summary = "Sit with legs long, loop a towel around your feet and draw your toes toward you.",
        cues = listOf(
            "Sit tall with your legs straight out in front.",
            "Loop a towel or strap around the balls of your feet.",
            "Gently pull the towel so your toes come toward you.",
            "Keep your back tall. Feel your calves.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 38f, nearElbow = 28f, farShoulder = 34f, farElbow = 30f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = 168f, spine = 2f, head = -6f, nearShoulder = 80f, nearElbow = 10f, farShoulder = 78f, farElbow = 12f, nearHip = 90f, nearAnkle = 25f, farHip = 90f, farAnkle = 25f),
            deep = Pose(torso = 162f, spine = 3f, head = -8f, nearShoulder = 84f, nearElbow = 8f, farShoulder = 82f, farElbow = 10f, nearHip = 90f, nearAnkle = 30f, farHip = 90f, farAnkle = 30f),
        ),
    )

    val wallChest = Stretch(
        key = "wall_chest",
        name = "Wall chest stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Chest),
        secondary = listOf(Muscle.Shoulders),
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
            stretch = Pose(nearShoulder = 90f, nearElbow = 2f, farShoulder = 70f, farElbow = 100f),
            deep = Pose(nearShoulder = 95f, nearElbow = 2f, farShoulder = 72f, farElbow = 110f),
        ),
    )

    val puppy = Stretch(
        key = "puppy",
        name = "Puppy stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Lats),
        secondary = listOf(Muscle.Shoulders, Muscle.UpperBack),
        position = Position.HandsAndKnees,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "From hands and knees, walk your hands forward and let your chest sink.",
        cues = listOf(
            "Start on hands and knees, hips over your knees.",
            "Walk your hands forward, keeping your hips where they are.",
            "Let your chest sink toward the floor and rest your forehead down.",
            "Feel under your arms and along your back. Breathe slowly.",
        ),
        cautions = listOf("Put a folded towel under your knees if the floor is hard."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 60f, spine = -6f, head = -10f, nearShoulder = 186f, farShoulder = 184f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            deep = Pose(torso = 55f, spine = -8f, head = -12f, nearShoulder = 195f, farShoulder = 193f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
        ),
    )

    // ---- S8: Strengthen holds (P-5, timed, no rep counting) and foam rolling (P-10).

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

    val wallAngel = Stretch(
        key = "wall_angel",
        name = "Wall angel hold",
        kind = StretchKind.Strengthen,
        sided = false,
        primary = listOf(Muscle.UpperBack),
        secondary = listOf(Muscle.Shoulders),
        position = Position.Standing,
        equipment = Equipment.Wall,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "Back against a wall, slide your arms up the wall and hold them there.",
        cues = listOf(
            "Stand with your back, head and bottom against a wall.",
            "Bend your elbows and rest your arms on the wall, like goalposts.",
            "Slide your arms up as far as you can while keeping them touching the wall.",
            "Hold there and breathe. Keep your lower back gently against the wall.",
        ),
        sourceIds = listOf("S19", "S9"),
        prop = Prop(Equipment.Wall, x = -14f),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 90f, nearElbow = 90f, farShoulder = 88f, farElbow = 92f),
            stretch = Pose(nearShoulder = 168f, nearElbow = 30f, farShoulder = 166f, farElbow = 32f),
            deep = Pose(nearShoulder = 174f, nearElbow = 24f, farShoulder = 172f, farElbow = 26f),
        ),
    )

    val activeLegRaise = Stretch(
        key = "active_leg_raise",
        name = "Active leg raise",
        kind = StretchKind.Strengthen,
        sided = true,
        primary = listOf(Muscle.HipFlexors),
        secondary = listOf(Muscle.Hamstrings, Muscle.Abdominals),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "Lying on your back, lift one straight leg using your own muscles and hold it.",
        cues = listOf(
            "Lie on your back with both legs long.",
            "Tighten your stomach gently and lift one straight leg as high as you can without help.",
            "Keep the other leg relaxed on the floor.",
            "Hold steady and keep breathing.",
        ),
        sourceIds = listOf("S9", "S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 145f, nearKnee = 3f, farHip = 90f),
            deep = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 150f, nearKnee = 3f, farHip = 90f),
        ),
    )

    val gluteBridge = Stretch(
        key = "glute_bridge",
        name = "Glute bridge hold",
        kind = StretchKind.Strengthen,
        sided = false,
        primary = listOf(Muscle.Glutes),
        secondary = listOf(Muscle.Hamstrings, Muscle.LowerBack),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh, Segment.Torso),
        summary = "Lying on your back with knees bent, lift your hips and hold.",
        cues = listOf(
            "Lie on your back with your knees bent and feet flat.",
            "Squeeze your buttocks and lift your hips until your body is a straight line from shoulders to knees.",
            "Don't arch your lower back.",
            "Hold steady and keep breathing.",
        ),
        sourceIds = listOf("S9", "S30"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 130f, nearKnee = 90f, nearAnkle = -40f, farHip = 130f, farKnee = 90f, farAnkle = -40f),
            stretch = Pose(torso = -60f, head = -30f, nearShoulder = -35f, nearElbow = 2f, farShoulder = -35f, farElbow = 2f, nearHip = 100f, nearKnee = 129f, nearAnkle = 29f, farHip = 100f, farKnee = 129f, farAnkle = 29f),
            deep = Pose(torso = -62f, head = -28f, nearShoulder = -37f, nearElbow = 2f, farShoulder = -37f, farElbow = 2f, nearHip = 101f, nearKnee = 130f, nearAnkle = 29f, farHip = 101f, farKnee = 130f, farAnkle = 29f),
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

    val calfRaiseHold = Stretch(
        key = "calf_raise_hold",
        name = "Calf raise hold",
        kind = StretchKind.Strengthen,
        sided = false,
        primary = listOf(Muscle.Calves),
        position = Position.Standing,
        highlight = setOf(Segment.NearShin, Segment.FarShin),
        summary = "Rise onto the balls of your feet and hold.",
        cues = listOf(
            "Stand tall, lightly holding a wall or chair for balance.",
            "Rise slowly onto the balls of your feet.",
            "Hold at the top, keeping your ankles straight rather than rolling out.",
            "Breathe steadily. Lower slowly when the time is up.",
        ),
        sourceIds = listOf("S9", "S10"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(nearElbow = 6f, farElbow = 6f, nearAnkle = -50f, farAnkle = -50f),
            deep = Pose(nearElbow = 6f, farElbow = 6f, nearAnkle = -55f, farAnkle = -55f),
        ),
    )

    val rollUpperBack = Stretch(
        key = "roll_upper_back",
        name = "Roll: upper back",
        kind = StretchKind.Roll,
        sided = false,
        primary = listOf(Muscle.UpperBack),
        position = Position.Lying,
        equipment = Equipment.FoamRoller,
        highlight = setOf(Segment.Torso),
        summary = "Lie with the roller across your upper back and roll slowly between your shoulder blades.",
        cues = listOf(
            "Sit in front of the roller, then lie back so it's across your upper back.",
            "Support your head with your hands, knees bent and feet flat.",
            "Lift your hips a little and roll slowly, about 3 seconds each way.",
            "Use firm pressure where you can still breathe slowly. Ease off if it feels sharp.",
        ),
        cautions = listOf("Don't roll over joints, bones or your lower spine. Stop for numbness, tingling or sharp pain.", "Keep the roller between your shoulder blades, not on your neck or lower back."),
        sourceIds = listOf("S34", "S36", "S44"),
        prop = Prop(Equipment.FoamRoller, x = -35f),
        motion = Motion(
            kind = MotionKind.Flow,
            pins = setOf(Pin.NearFoot, Pin.FarFoot),
            start = Pose(torso = -115f, nearShoulder = 160f, nearElbow = 140f, farShoulder = 158f, farElbow = 140f, nearHip = 130f, nearKnee = 90f, nearAnkle = -40f, farHip = 130f, farKnee = 90f, farAnkle = -40f),
            stretch = Pose(torso = -115f, nearShoulder = 160f, nearElbow = 140f, farShoulder = 158f, farElbow = 140f, nearHip = 130f, nearKnee = 90f, nearAnkle = -40f, farHip = 130f, farKnee = 90f, farAnkle = -40f),
            deep = Pose(torso = -115f, nearShoulder = 160f, nearElbow = 140f, farShoulder = 158f, farElbow = 140f, nearHip = 130f, nearKnee = 90f, nearAnkle = -40f, farHip = 130f, farKnee = 90f, farAnkle = -40f, shiftX = 6f),
        ),
    )

    val rollCalves = Stretch(
        key = "roll_calves",
        name = "Roll: calves",
        kind = StretchKind.Roll,
        sided = false,
        primary = listOf(Muscle.Calves),
        position = Position.Seated,
        equipment = Equipment.FoamRoller,
        highlight = setOf(Segment.NearShin),
        summary = "Sit with the roller under your calves, lift your hips and roll from ankle to knee.",
        cues = listOf(
            "Sit with your legs long and the roller under your calves.",
            "Put your hands on the floor behind you and lift your hips.",
            "Roll slowly from just above the ankles to just below the knees, about 3 seconds each way.",
            "Use firm pressure where you can still breathe slowly. Ease off if it feels sharp.",
        ),
        cautions = listOf("Don't roll over joints, bones or your lower spine. Stop for numbness, tingling or sharp pain."),
        sourceIds = listOf("S34", "S36", "S44"),
        prop = Prop(Equipment.FoamRoller, x = 60f),
        motion = Motion(
            kind = MotionKind.Flow,
            pins = setOf(Pin.NearHand, Pin.FarHand),
            start = Pose(torso = 230f, head = -45f, nearShoulder = -71f, nearElbow = 10f, farShoulder = -71f, farElbow = 10f, nearHip = 90f, farHip = 90f),
            stretch = Pose(torso = 230f, head = -45f, nearShoulder = -71f, farShoulder = -71f, nearHip = 90f, farHip = 90f),
            deep = Pose(torso = 230f, head = -45f, nearShoulder = -71f, farShoulder = -71f, nearHip = 90f, farHip = 90f, shiftX = -15f),
        ),
    )

    val rollQuads = Stretch(
        key = "roll_quads",
        name = "Roll: front of thighs",
        kind = StretchKind.Roll,
        sided = false,
        primary = listOf(Muscle.Quadriceps),
        secondary = listOf(Muscle.HipFlexors),
        position = Position.Lying,
        equipment = Equipment.FoamRoller,
        highlight = setOf(Segment.NearThigh),
        summary = "Face down on your forearms with the roller under your thighs, and roll from hip to knee.",
        cues = listOf(
            "Lie face down on your forearms with the roller under the front of your thighs.",
            "Keep your stomach gently tight so your back doesn't sag.",
            "Roll slowly from just below the hips to just above the knees, about 3 seconds each way.",
            "Use firm pressure where you can still breathe slowly. Ease off if it feels sharp.",
        ),
        cautions = listOf("Don't roll over joints, bones or your lower spine. Stop for numbness, tingling or sharp pain."),
        sourceIds = listOf("S34", "S36", "S44"),
        prop = Prop(Equipment.FoamRoller, x = -22f),
        motion = Motion(
            kind = MotionKind.Flow,
            pins = setOf(Pin.NearHand, Pin.FarHand),
            start = Pose(torso = 96f, nearShoulder = 84f, nearElbow = 90f, farShoulder = 84f, farElbow = 90f, nearHip = -84f, nearAnkle = -90f, farHip = -84f, farAnkle = -90f),
            stretch = Pose(torso = 96f, nearShoulder = 84f, nearElbow = 90f, farShoulder = 84f, farElbow = 90f, nearHip = -84f, nearAnkle = -90f, farHip = -84f, farAnkle = -90f),
            deep = Pose(torso = 96f, nearShoulder = 84f, nearElbow = 90f, farShoulder = 84f, farElbow = 90f, nearHip = -84f, nearAnkle = -90f, farHip = -84f, farAnkle = -90f, shiftX = 12f),
        ),
    )

    val all: List<Stretch> = listOf(
        seatedForwardFold, standingQuad, halfKneelingHipFlexor, childsPose, wallCalf, catCow,
        neckFlexion, chestOpener, wallLat, kneeToChest, butterfly, bentKneeCalf, standingHamstring,
        sphinx, lyingHamstring, downwardDog,
        upperBack, standingLunge, overheadReach, standingBackBend, standingKneeHug,
        sideLyingQuad, kneelingQuad, towelCalf, wallChest, cobra, crossBodyShoulder, puppy,
        chinTuck, wallAngel, activeLegRaise, gluteBridge, plank, calfRaiseHold,
        rollUpperBack, rollCalves, rollQuads,
    )


    fun byKey(key: String): Stretch? = all.firstOrNull { it.key == key }


    /**
     * Easier → harder pairs for the same body area (EVIDENCE §5.4). Used for the Easier/Harder
     * buttons; CatalogTest checks every pair resolves and shares a main area.
     */
    val variantPairs: List<Pair<String, String>> = listOf(
        "lying_hamstring" to "standing_hamstring",
        "standing_hamstring" to "seated_forward_fold",
        "seated_forward_fold" to "downward_dog",
        "side_lying_quad" to "standing_quad",
        "standing_quad" to "kneeling_quad",
        "standing_lunge_hip_flexor" to "half_kneeling_hip_flexor",
        "chest_opener" to "wall_chest",
        "overhead_reach" to "wall_lat",
        "wall_lat" to "puppy",
        "standing_back_bend" to "sphinx",
        "sphinx" to "cobra",
        "towel_calf" to "wall_calf",
    )

    fun easier(key: String): Stretch? = variantPairs.firstOrNull { it.second == key }?.first?.let(::byKey)

    fun harder(key: String): Stretch? = variantPairs.firstOrNull { it.first == key }?.second?.let(::byKey)
}
