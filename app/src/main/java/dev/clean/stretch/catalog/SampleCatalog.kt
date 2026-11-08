package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
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
            start = Pose(nearShoulder = 4f, nearElbow = 8f, farShoulder = 4f, farElbow = 8f, nearHip = 110f, nearKnee = 175f, nearAnkle = 65f, farHip = 108f, farKnee = 172f, farAnkle = 62f),
            stretch = Pose(torso = 165f, spine = 3f, head = -6f, nearShoulder = 3f, nearElbow = 10f, farShoulder = 2f, farElbow = 12f, nearHip = 110f, nearKnee = 175f, nearAnkle = 65f, farHip = 108f, farKnee = 172f, farAnkle = 62f),
            deep = Pose(torso = 155f, spine = 5f, head = -8f, nearShoulder = 4f, nearElbow = 15f, farShoulder = 3f, farElbow = 17f, nearHip = 110f, nearKnee = 175f, nearAnkle = 65f, farHip = 108f, farKnee = 172f, farAnkle = 62f),
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

    val all: List<Stretch> = listOf(
        seatedForwardFold, standingQuad, halfKneelingHipFlexor, childsPose, wallCalf, catCow,
        neckFlexion, chestOpener, wallLat, kneeToChest, butterfly, bentKneeCalf, standingHamstring,
        sphinx, lyingHamstring, downwardDog,
        upperBack, standingLunge, overheadReach, standingBackBend, standingKneeHug,
    )


    fun byKey(key: String): Stretch? = all.firstOrNull { it.key == key }
}
