package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Motion
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.Pin
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.View

/**
 * Legs (quadriceps, hamstrings, calves) moves: every move whose first main muscle is in this area.
 * Library order is set by [SampleCatalog.all]; see docs/ADDING_STRETCHES.md.
 */
object LegMoves {
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
            start = Pose(roll = 90f, torso = 90f, head = 8f, nearShoulder = 0f, nearElbow = 6f, farShoulder = 178f, farElbow = 4f, nearHip = -90f, nearAnkle = -90f, farHip = -90f, farAnkle = -90f),
            stretch = Pose(roll = 90f, torso = 90f, head = 8f, nearShoulder = -17f, nearElbow = 10f, farShoulder = 178f, farElbow = 4f, nearHip = -94f, nearKnee = 158f, nearAnkle = -35f, farHip = -90f, farAnkle = -90f),
            deep = Pose(roll = 90f, torso = 90f, head = 8f, nearShoulder = -20f, nearElbow = 8f, farShoulder = 178f, farElbow = 4f, nearHip = -100f, nearKnee = 160f, nearAnkle = -38f, farHip = -90f, farAnkle = -90f),
            view = View.Above,
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
            view = View.Angled,
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
            view = View.Angled,
        ),
    )

    val legSwings = Stretch(
        key = "leg_swings",
        name = "Leg swings",
        kind = StretchKind.Dynamic,
        sided = true,
        primary = listOf(Muscle.Hamstrings),
        secondary = listOf(Muscle.HipFlexors, Muscle.Glutes),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh),
        summary = "Hold something steady and swing one leg forward and back, a little higher each time.",
        cues = listOf(
            "Stand side-on to a wall or chair and hold it lightly.",
            "Swing the outside leg forward and back, keeping it relaxed.",
            "Let the swings grow a little bigger as you warm up.",
            "Keep your body tall. Don't force the end of the swing.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearElbow = 6f, farShoulder = 80f, farElbow = 10f),
            stretch = Pose(nearShoulder = -20f, nearElbow = 6f, farShoulder = 80f, farElbow = 10f, nearHip = 50f, nearKnee = 6f),
            deep = Pose(nearShoulder = 20f, nearElbow = 6f, farShoulder = 80f, farElbow = 10f, nearHip = -28f, nearKnee = 4f),
        ),
    )

    val hipHinge = Stretch(
        key = "hip_hinge",
        name = "Hip hinge",
        kind = StretchKind.Dynamic,
        sided = false,
        primary = listOf(Muscle.Hamstrings),
        secondary = listOf(Muscle.Glutes, Muscle.LowerBack),
        position = Position.Standing,
        highlight = setOf(Segment.NearThigh, Segment.FarThigh),
        summary = "Hands on hips, push your hips back and tip forward with a long back, then stand tall.",
        cues = listOf(
            "Stand tall with your hands on your hips and knees soft.",
            "Push your hips back and let your chest tip forward, back long.",
            "Go until you feel the backs of your thighs, then stand back up.",
            "Move slowly, breathing out as you tip forward.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearShoulder = -30f, nearElbow = 100f, farShoulder = -28f, farElbow = 100f),
            stretch = Pose(nearShoulder = -30f, nearElbow = 100f, farShoulder = -28f, farElbow = 100f, nearHip = 3f, nearKnee = 6f, farHip = 3f, farKnee = 6f),
            deep = Pose(torso = 112f, head = -8f, nearShoulder = -30f, nearElbow = 100f, farShoulder = -28f, farElbow = 100f, nearHip = 5f, nearKnee = 10f, farHip = 5f, farKnee = 10f),
        ),
    )

    val ankleRocks = Stretch(
        key = "ankle_rocks",
        name = "Ankle rocks",
        kind = StretchKind.Dynamic,
        sided = true,
        primary = listOf(Muscle.Calves),
        position = Position.Standing,
        highlight = setOf(Segment.NearShin),
        summary = "In a short lunge, rock your front knee forward over your toes and back, heel down.",
        cues = listOf(
            "Stand in a short lunge, holding a wall or chair if you like.",
            "Keeping the front heel down, rock that knee forward over your toes.",
            "Rock back again. Keep it smooth and rhythmic.",
            "Feel the front ankle and calf loosen as you go.",
        ),
        sourceIds = listOf("S3", "S4", "S5"),
        motion = Motion(
            kind = MotionKind.Flow,
            start = Pose(nearShoulder = 75f, nearElbow = 10f, farShoulder = 72f, farElbow = 12f, nearHip = 40f, nearKnee = 50f, nearAnkle = 10f, farHip = -29f, farAnkle = -1f),
            stretch = Pose(torso = 178f, nearShoulder = 75f, nearElbow = 10f, farShoulder = 72f, farElbow = 12f, nearHip = 40f, nearKnee = 50f, nearAnkle = 10f, farHip = -29f, farAnkle = -1f),
            deep = Pose(torso = 178f, nearShoulder = 75f, nearElbow = 10f, farShoulder = 72f, farElbow = 12f, nearHip = 40f, nearKnee = 78f, nearAnkle = 38f, farHip = -39f, farAnkle = 9f),
        ),
    )

    val all: List<Stretch> = listOf(seatedForwardFold, standingQuad, wallCalf, bentKneeCalf, standingHamstring, lyingHamstring, downwardDog, sideLyingQuad, kneelingQuad, towelCalf, calfRaiseHold, rollCalves, rollQuads, legSwings, hipHinge, ankleRocks)
}
