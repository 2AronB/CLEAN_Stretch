package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Hips (hip flexors, glutes, inner thigh, outer hip, deep hip rotators) moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object HipMoves {
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
            start = Pose(nearShoulder = 4f, nearElbow = 8f, farShoulder = 4f, farElbow = 8f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f, farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = 30f, farLegOut = 30f, nearLegTurn = 60f, farLegTurn = 60f),
            stretch = Pose(torso = 165f, spine = 3f, head = -6f, nearShoulder = 25f, nearElbow = 10f, farShoulder = 25f, farElbow = 12f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f, farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = 55f, farLegOut = 55f, nearLegTurn = 60f, farLegTurn = 60f),
            deep = Pose(torso = 155f, spine = 5f, head = -8f, nearShoulder = 30f, nearElbow = 15f, farShoulder = 30f, farElbow = 17f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f, farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = 60f, farLegOut = 60f, nearLegTurn = 60f, farLegTurn = 60f),
            view = View.Angled,
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
            view = View.Angled,
        ),
    )

    val lungeReach = Stretch(
        key = "lunge_reach",
        name = "Lunge with reach",
        kind = StretchKind.Dynamic,
        sided = true,
        primary = listOf(Muscle.HipFlexors),
        secondary = listOf(Muscle.Quadriceps, Muscle.Lats),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Step into a lunge and reach the back-leg arm overhead, then return.",
        cues = listOf(
            "Stand tall, then take a long step back with one foot.",
            "Bend the front knee a little and reach the back-leg arm overhead.",
            "Feel the front of the back hip, then step back to standing.",
            "Repeat slowly and smoothly.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(nearElbow = 6f, farElbow = 6f),
            deep = Pose(torso = 182f, spine = -3f, nearShoulder = 160f, nearElbow = 8f, farShoulder = 30f, farElbow = 20f, nearHip = -25f, nearKnee = 15f, farHip = 25f, farKnee = 30f, farAnkle = 5f),
        ),
    )

    val figureFour = Stretch(
        key = "figure_four",
        name = "Lying figure-four stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Glutes, Muscle.DeepHip),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "On your back, cross one ankle over the other knee and draw both legs toward you.",
        cues = listOf(
            "Lie on your back with your knees bent and feet flat.",
            "Cross one ankle over the other knee, letting that knee open out.",
            "Hold behind the lower thigh and draw both legs gently toward your chest.",
            "Feel the back and outside of your crossed-leg hip. Keep your head down.",
        ),
        cautions = listOf("Leave your foot on the floor instead if that is already enough."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 135f, nearKnee = 100f, farHip = 135f, farKnee = 100f),
            stretch = Pose(torso = -90f, nearShoulder = 40f, nearElbow = 30f, nearArmOut = -10f, farShoulder = 40f, farElbow = 30f, farArmOut = -10f, nearHip = 150f, nearKnee = 95f, nearLegOut = 30f, nearLegTurn = 70f, farHip = 165f, farKnee = 95f),
            deep = Pose(torso = -90f, nearShoulder = 46f, nearElbow = 30f, nearArmOut = -10f, farShoulder = 46f, farElbow = 30f, farArmOut = -10f, nearHip = 160f, nearKnee = 95f, nearLegOut = 32f, nearLegTurn = 70f, farHip = 178f, farKnee = 95f),
            view = View.Angled,
        ),
    )

    val wideLegFold = Stretch(
        key = "wide_leg_fold",
        name = "Wide-leg seated stretch",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Adductors),
        secondary = listOf(Muscle.Hamstrings),
        position = Position.Seated,
        level = 2,
        spinalFlexion = true,
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        summary = "Sit with your legs wide and lean forward from your hips.",
        cues = listOf(
            "Sit tall and open your legs wide, as far as is comfortable.",
            "Keep your knees and toes pointing up.",
            "Walk your hands forward and lean from your hips, keeping your back long.",
            "Feel your inner thighs. Ease off if your knees complain.",
        ),
        cautions = listOf("Sit on a folded towel if you can't sit tall."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 20f, nearElbow = 8f, farShoulder = 20f, farElbow = 8f, nearHip = 90f, nearLegOut = 38f, farHip = 90f, farLegOut = 38f),
            stretch = Pose(torso = 140f, spine = 6f, head = -8f, nearShoulder = 85f, nearElbow = 8f, farShoulder = 85f, farElbow = 8f, nearHip = 90f, nearLegOut = 40f, farHip = 90f, farLegOut = 40f),
            deep = Pose(torso = 132f, spine = 8f, head = -10f, nearShoulder = 92f, nearElbow = 6f, farShoulder = 92f, farElbow = 6f, nearHip = 90f, nearLegOut = 42f, farHip = 90f, farLegOut = 42f),
            view = View.Angled,
        ),
    )

    val sideLunge = Stretch(
        key = "side_lunge",
        name = "Side lunge stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.Adductors),
        secondary = listOf(Muscle.Glutes),
        position = Position.Standing,
        level = 2,
        highlight = setOf(Segment.NearThigh),
        summary = "Stand wide, bend one knee and sit your hips toward it to feel the straight leg's inner thigh.",
        cues = listOf(
            "Stand with your feet wide apart, toes forward.",
            "Bend one knee and shift your hips toward it. Keep the other leg straight.",
            "Keep your chest up and your bent knee over your toes.",
            "Feel the inner thigh of the straight leg. Rest your hands on the bent thigh.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f, nearLegOut = 22f, farLegOut = 22f),
            stretch = Pose(torso = 165f, nearShoulder = 30f, nearElbow = 30f, nearArmOut = -20f, farShoulder = 30f, farElbow = 30f, farArmOut = -10f, nearLegOut = 38f, farHip = 55f, farKnee = 85f, farLegOut = 12f, farAnkle = 30f),
            deep = Pose(torso = 160f, nearShoulder = 34f, nearElbow = 30f, nearArmOut = -20f, farShoulder = 34f, farElbow = 30f, farArmOut = -10f, nearLegOut = 42f, farHip = 62f, farKnee = 95f, farLegOut = 10f, farAnkle = 33f),
            view = View.Angled,
        ),
    )

    val sideLegRaise = Stretch(
        key = "side_leg_raise",
        name = "Side leg raise hold",
        kind = StretchKind.Strengthen,
        sided = true,
        primary = listOf(Muscle.OuterHip),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Stand tall and hold one straight leg out to the side.",
        cues = listOf(
            "Stand tall. Hold a wall or chair lightly for balance if you like.",
            "Lift one straight leg out to the side, toes facing forward.",
            "Keep your body upright. Don't lean away from the leg.",
            "Hold steady and keep breathing. Feel the side of your hip working.",
        ),
        sourceIds = listOf("S9", "S3"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(sideBend = -3f, nearElbow = 6f, nearArmOut = 10f, farElbow = 6f, farArmOut = 30f, nearLegOut = 28f, nearKnee = 2f),
            deep = Pose(sideBend = -3f, nearElbow = 6f, nearArmOut = 10f, farElbow = 6f, farArmOut = 30f, nearLegOut = 31f, nearKnee = 2f),
            view = View.Front,
        ),
    )

    val recliningButterfly = Stretch(
        key = "reclining_butterfly",
        name = "Reclining butterfly",
        kind = StretchKind.Static,
        sided = false,
        primary = listOf(Muscle.Adductors),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        summary = "Lie on your back with the soles of your feet together and let your knees fall open.",
        cues = listOf(
            "Lie on your back with your knees bent and feet flat.",
            "Bring the soles of your feet together.",
            "Let your knees fall open to the sides. Let gravity do the work.",
            "Feel your inner thighs. Rest your arms out to the sides and breathe.",
        ),
        cautions = listOf("Put a cushion under each knee if the stretch is too strong."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 30f, farShoulder = 4f, farElbow = 4f, farArmOut = 30f, nearHip = 135f, nearKnee = 100f, farHip = 135f, farKnee = 100f),
            stretch = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 30f, farShoulder = 4f, farElbow = 4f, farArmOut = 30f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f, farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = 50f, farLegOut = 50f, nearLegTurn = 60f, farLegTurn = 60f),
            deep = Pose(torso = -90f, nearShoulder = 4f, nearElbow = 4f, nearArmOut = 30f, farShoulder = 4f, farElbow = 4f, farArmOut = 30f, nearHip = 115f, nearKnee = 140f, nearAnkle = 20f, farHip = 115f, farKnee = 140f, farAnkle = 20f, nearLegOut = 58f, farLegOut = 58f, nearLegTurn = 60f, farLegTurn = 60f),
            view = View.Front,
        ),
    )

    // Outer hip (PLAN §8.2, 2026-11-14). Feet that stay on the floor were placed numerically.
    val standingOuterHip = Stretch(
        key = "standing_outer_hip",
        name = "Standing outer-hip stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.OuterHip),
        secondary = listOf(Muscle.Lats),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Cross one foot behind the other and lean your upper body away, letting that hip push out to the side.",
        cues = listOf(
            "Stand tall. Hold a wall or chair for balance if you like.",
            "Cross one foot behind the other and rest it flat on the floor.",
            "Reach the same-side arm overhead and lean gently away from the back leg, letting that hip push out to the side.",
            "Feel the outside of your hip and thigh. Keep both feet on the floor.",
        ),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearElbow = 6f, farElbow = 6f),
            stretch = Pose(sideBend = -15f, nearShoulder = 168f, nearElbow = 10f, nearArmOut = -10f, farElbow = 30f, farArmOut = 24f, nearHip = -6f, nearLegOut = -12f, nearAnkle = 6f, farHip = 13.5f, farKnee = 27f, farAnkle = 13.5f),
            deep = Pose(sideBend = -21f, nearShoulder = 170f, nearElbow = 8f, nearArmOut = -14f, farElbow = 30f, farArmOut = 24f, nearHip = -6f, nearLegOut = -12f, nearAnkle = 6f, farHip = 13.5f, farKnee = 27f, farAnkle = 13.5f),
            view = View.Front,
        ),
    )

    val seatedOuterHip = Stretch(
        key = "seated_outer_hip",
        name = "Seated outer-hip stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.OuterHip),
        secondary = listOf(Muscle.Glutes),
        position = Position.Seated,
        highlight = setOf(Segment.NearThigh),
        summary = "Sit with one leg long, cross the other foot over it and hug that knee toward your opposite shoulder.",
        cues = listOf(
            "Sit tall with both legs out in front of you.",
            "Bend one knee and place that foot flat on the floor, on the outside of your other knee.",
            "Hug the bent knee with both arms and draw it gently toward your opposite shoulder.",
            "Feel the outside of your hip. Sit tall and keep your chest facing forward.",
        ),
        cautions = listOf("Sit on a folded towel if you can't sit tall."),
        sourceIds = listOf("S3", "S2"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(nearShoulder = 4f, nearElbow = 8f, farShoulder = 4f, farElbow = 8f, nearHip = 87f, farHip = 87f),
            stretch = Pose(nearShoulder = 20f, nearElbow = 55f, nearArmOut = -40f, farShoulder = 20f, farElbow = 80f, farArmOut = -10f, nearHip = 146f, nearKnee = 116f, nearAnkle = -25f, nearLegOut = -21f, nearLegTurn = 35f, farHip = 87f),
            deep = Pose(nearShoulder = 20f, nearElbow = 50f, nearArmOut = -45f, farShoulder = 20f, farElbow = 80f, farArmOut = -5f, nearHip = 150f, nearKnee = 116f, nearAnkle = -25f, nearLegOut = -27f, nearLegTurn = 30f, farHip = 87f),
            view = View.Angled,
        ),
    )

    val sideLegSwings = Stretch(
        key = "side_leg_swings",
        name = "Side leg swings",
        kind = StretchKind.Dynamic,
        sided = true,
        primary = listOf(Muscle.OuterHip),
        secondary = listOf(Muscle.Adductors),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Hold something steady and swing one leg out to the side, then across in front of you.",
        cues = listOf(
            "Face a wall or chair and hold it lightly with both hands.",
            "Swing one leg out to the side, then across in front of your standing leg.",
            "Keep the leg relaxed and your toes pointing forward.",
            "Stay tall. Let the swings grow a little as you warm up, without forcing them.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearShoulder = 70f, nearElbow = 20f, farShoulder = 70f, farElbow = 20f),
            stretch = Pose(nearShoulder = 70f, nearElbow = 20f, farShoulder = 70f, farElbow = 20f, nearHip = 4f, nearKnee = 4f, nearLegOut = 32f),
            deep = Pose(nearShoulder = 70f, nearElbow = 20f, farShoulder = 70f, farElbow = 20f, nearHip = 16f, nearKnee = 4f, nearLegOut = -20f),
            view = View.Front,
        ),
    )

    // Deep hip rotators (PLAN §8.2, 2026-11-14). Hands, knees and feet on the floor were placed numerically.
    val seatedFigureFour = Stretch(
        key = "seated_figure_four",
        name = "Seated figure-four stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.DeepHip),
        secondary = listOf(Muscle.Glutes),
        position = Position.Seated,
        highlight = setOf(Segment.NearThigh),
        summary = "Sit propped on your hands with your knees bent, cross one ankle over the other knee and sit up taller.",
        cues = listOf(
            "Sit with your knees bent and feet flat. Rest your hands on the floor behind you.",
            "Cross one ankle over the other knee and let that knee open out to the side.",
            "Walk your hands in and sit up taller, bringing your chest toward your legs.",
            "Feel deep in the back of your crossed-leg hip. Keep your back long.",
        ),
        cautions = listOf("Move your supporting foot further away from you if the crossed knee feels strained."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 210f, nearShoulder = -55.8f, farShoulder = -55.8f, nearHip = 137f, nearKnee = 106f, nearAnkle = -31f, farHip = 137f, farKnee = 106f, farAnkle = -31f),
            stretch = Pose(torso = 200f, nearShoulder = -34.7f, farShoulder = -34.7f, nearHip = 173f, nearKnee = 112f, nearLegOut = 28f, nearLegTurn = 60f, farHip = 137f, farKnee = 106f, farAnkle = -31f),
            deep = Pose(torso = 200f, nearShoulder = -34.7f, farShoulder = -34.7f, nearHip = 150f, nearKnee = 114f, nearLegOut = 36f, nearLegTurn = 86f, farHip = 137f, farKnee = 106f, farAnkle = -31f),
            view = View.Angled,
        ),
    )

    val pigeon = Stretch(
        key = "pigeon",
        name = "Pigeon stretch",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.DeepHip),
        secondary = listOf(Muscle.Glutes, Muscle.HipFlexors),
        position = Position.HandsAndKnees,
        level = 2,
        highlight = setOf(Segment.NearThigh),
        summary = "From hands and knees, bring one knee forward with the shin across the floor and slide the other leg straight back.",
        cues = listOf(
            "Start on your hands and knees.",
            "Bring one knee forward toward the same-side hand, and let that foot slide across toward your other hip.",
            "Slide your other leg straight back and lower your hips toward the floor, keeping them level.",
            "Stay propped on your hands. Feel deep in the hip of your front leg, never in the knee.",
        ),
        cautions = listOf(
            "Stop if you feel pain or twisting in your front knee, and try the lying figure-four instead.",
            "Put a cushion under the hip of your front leg if it doesn't reach the floor.",
        ),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = 103f, nearShoulder = 77f, farShoulder = 77f, nearKnee = 90f, nearAnkle = -90f, farKnee = 90f, farAnkle = -90f),
            stretch = Pose(torso = 160f, nearShoulder = 20f, nearElbow = 3f, nearArmOut = 10f, farShoulder = 20f, farElbow = 3f, farArmOut = 10f, nearHip = 84f, nearKnee = 88f, nearAnkle = -60f, nearLegOut = 24f, nearLegTurn = 90f, farHip = -84f, farKnee = 3.7f, farAnkle = -84.3f),
            deep = Pose(torso = 140f, nearShoulder = 36f, nearElbow = 20f, nearArmOut = 10f, farShoulder = 36f, farElbow = 20f, farArmOut = 10f, nearHip = 84f, nearKnee = 88f, nearAnkle = -60f, nearLegOut = 24f, nearLegTurn = 90f, farHip = -84f, farKnee = 3.7f, farAnkle = -84.3f),
            view = View.Angled,
        ),
    )

    val kneeToOppositeShoulder = Stretch(
        key = "knee_to_opposite_shoulder",
        spinalFlexion = true,
        name = "Knee to opposite shoulder",
        kind = StretchKind.Static,
        sided = true,
        primary = listOf(Muscle.DeepHip),
        secondary = listOf(Muscle.Glutes, Muscle.OuterHip),
        position = Position.Lying,
        highlight = setOf(Segment.NearThigh),
        summary = "Lie on your back and draw one bent knee across your body toward the opposite shoulder.",
        cues = listOf(
            "Lie on your back with both legs long.",
            "Bend one knee and hold it with both hands.",
            "Draw the knee gently across your body, toward your opposite shoulder.",
            "Feel deep in the back of that hip. Keep both shoulders resting on the floor.",
        ),
        cautions = listOf("Ease off if you feel pinching at the front of your hip."),
        sourceIds = listOf("S3", "S15"),
        motion = Motion(
            kind = MotionKind.Hold,
            start = Pose(torso = -90f, head = -6f, nearShoulder = 4f, nearElbow = 4f, farShoulder = 4f, farElbow = 4f, nearHip = 87f, farHip = 87f),
            stretch = Pose(torso = -90f, head = -6f, nearShoulder = 48f, nearArmOut = -20f, farShoulder = 48f, farArmOut = -10f, nearHip = 205f, nearKnee = 120f, nearLegOut = -20f, farHip = 87f),
            deep = Pose(torso = -90f, head = -6f, nearShoulder = 48f, nearArmOut = -25f, farShoulder = 33f, farElbow = 30f, farArmOut = -5f, nearHip = 212f, nearKnee = 120f, nearLegOut = -26f, farHip = 87f),
            view = View.Angled,
        ),
    )

    val all: List<Stretch> = listOf(halfKneelingHipFlexor, kneeToChest, butterfly, standingLunge, standingKneeHug, activeLegRaise, gluteBridge, lungeReach, figureFour, wideLegFold, sideLunge, sideLegRaise, recliningButterfly, standingOuterHip, seatedOuterHip, sideLegSwings, seatedFigureFour, pigeon, kneeToOppositeShoulder)
}
