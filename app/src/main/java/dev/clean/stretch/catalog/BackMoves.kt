package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pin
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Back (upper back, lower back, lats) moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object BackMoves {
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
            start = Pose(nearElbow = 90f, nearArmOut = 90f, nearArmTurn = 90f, farElbow = 90f, farArmOut = 90f, farArmTurn = 90f),
            stretch = Pose(nearElbow = 45f, nearArmOut = 128f, nearArmTurn = 90f, farElbow = 45f, farArmOut = 128f, farArmTurn = 90f),
            deep = Pose(nearElbow = 38f, nearArmOut = 134f, nearArmTurn = 90f, farElbow = 38f, farArmOut = 134f, farArmTurn = 90f),
            view = View.Front,
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
            view = View.Angled,
        ),
    )

    val standingSideBend = Stretch(
        key = "standing_side_bend",
        name = "Standing side bend",
        kind = StretchKind.Static,
        sided = true,
        // Counts for side body as well as lats (owner, 2026-11-14). Lats stays first so the move stays in this file.
        primary = listOf(Muscle.Lats, Muscle.Obliques),
        secondary = listOf(Muscle.Abdominals),
        position = Position.Standing,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "Reach one arm overhead and lean gently to the other side.",
        cues = listOf(
            "Stand tall with your feet hip-width apart.",
            "Reach one arm up overhead. Rest the other hand on your hip or thigh.",
            "Lean gently to the side, reaching up and over.",
            "Feel along your side from hip to fingertips. Keep both feet on the floor.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(sideBend = -20f, nearShoulder = 168f, nearElbow = 10f, nearArmOut = -12f, farElbow = 30f, farArmOut = 24f),
            deep = Pose(sideBend = -26f, nearShoulder = 170f, nearElbow = 8f, nearArmOut = -16f, farElbow = 30f, farArmOut = 24f),
            view = View.Front,
        ),
    )

    val kneeDropTwist = Stretch(
        key = "knee_drop_twist",
        name = "Lying knee-drop twist",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.LowerBack),
        secondary = listOf(Muscle.Obliques, Muscle.Glutes),
        position = Position.Lying,
        spinalTwist = true,
        highlight = setOf(Segment.Torso),
        summary = "On your back with knees bent, let both knees fall slowly to one side.",
        cues = listOf(
            "Lie on your back with your knees bent and feet flat.",
            "Stretch your arms out to the sides, palms down.",
            "Let both knees fall slowly to one side. Keep both shoulders on the floor.",
            "Feel your lower back and the side of your hip. Breathe slowly.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 75f, farShoulder = 4f, farElbow = 4f, farArmOut = 75f, nearHip = 135f, nearKnee = 100f, farHip = 135f, farKnee = 100f),
            stretch = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 75f, farShoulder = 4f, farElbow = 4f, farArmOut = 75f, nearHip = 135f, nearKnee = 100f, nearLegOut = 45f, farHip = 135f, farKnee = 100f, farLegOut = -40f),
            deep = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 75f, farShoulder = 4f, farElbow = 4f, farArmOut = 75f, nearHip = 135f, nearKnee = 100f, nearLegOut = 55f, farHip = 135f, farKnee = 100f, farLegOut = -50f),
            view = View.Angled,
        ),
    )

    val seatedTwist = Stretch(
        key = "seated_twist",
        name = "Seated twist",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.LowerBack),
        secondary = listOf(Muscle.Obliques, Muscle.UpperBack),
        position = Position.Seated,
        level = 2,
        spinalTwist = true,
        highlight = setOf(Segment.Torso),
        summary = "Sit tall with your legs out and turn your chest to one side.",
        cues = listOf(
            "Sit tall with your legs out in front of you.",
            "Place one hand on the floor behind you and the other on the outside of your knee.",
            "Grow tall, then turn your chest and head toward the hand behind you.",
            "Feel your back and sides. Stay tall rather than turning further.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 4f, nearElbow = 8f, farShoulder = 4f, farElbow = 8f, nearHip = 90f, farHip = 90f),
            stretch = Pose(twist = 32f, nearShoulder = -30f, nearElbow = 4f, nearArmOut = 18f, farShoulder = 50f, farElbow = 30f, farArmOut = -35f, nearHip = 90f, farHip = 90f),
            deep = Pose(twist = 40f, nearShoulder = -34f, nearElbow = 4f, nearArmOut = 18f, farShoulder = 52f, farElbow = 30f, farArmOut = -40f, nearHip = 90f, farHip = 90f),
            view = View.Angled,
        ),
    )

    val threadNeedle = Stretch(
        key = "thread_the_needle",
        name = "Thread the needle",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.UpperBack),
        secondary = listOf(Muscle.Shoulders),
        position = Position.HandsAndKnees,
        spinalTwist = true,
        highlight = setOf(Segment.Torso, Segment.NearUpperArm),
        summary = "On hands and knees, slide one arm under your body and rest that shoulder down.",
        cues = listOf(
            "Start on your hands and knees, hands under your shoulders.",
            "Slide one arm along the floor under your body, palm up.",
            "Lower that shoulder and the side of your head toward the floor.",
            "Feel between your shoulder blades. Keep your hips over your knees.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 112f, twist = -35f, head = -10f, nearShoulder = 80f, nearElbow = 4f, nearArmOut = -62f, farShoulder = 68f, farElbow = 50f, nearHip = -8f, nearKnee = 90f, nearAnkle = -90f, farHip = -8f, farKnee = 90f, farAnkle = -90f),
            deep = Pose(torso = 116f, twist = -42f, head = -12f, nearShoulder = 82f, nearElbow = 4f, nearArmOut = -70f, farShoulder = 66f, farElbow = 58f, nearHip = -10f, nearKnee = 90f, nearAnkle = -90f, farHip = -10f, farKnee = 90f, farAnkle = -90f),
            view = View.Front,
        ),
    )

    val all: List<Stretch> = listOf(childsPose, catCow, wallLat, upperBack, overheadReach, puppy, wallAngel, rollUpperBack, standingSideBend, kneeDropTwist, seatedTwist, threadNeedle)
}
