package dev.clean.stretch.figure

/**
 * A side-view pose of the figure. All angles are in degrees.
 *
 * Direction convention (screen space, y grows downward): an absolute angle `a` points
 * along (sin a, cos a). So 0 = straight down, 90 = forward (+x, the way the figure faces),
 * 180 = straight up and -90 = backward.
 *
 * - [torso]: absolute direction from hip to shoulders (180 = upright, 90 = leaning fully forward).
 * - [spine]: visual curve of the back. Positive rounds the back, negative arches it.
 * - [head]: nod relative to the torso. Negative tips the chin down/forward.
 * - Shoulders: arm raise relative to the torso. 0 = arm along the body toward the hips,
 *   90 = reaching forward, 180 = overhead, negative = reaching behind.
 * - Elbows: forearm bend relative to the upper arm (positive bends forward).
 * - Hips: absolute thigh direction (0 = straight down, 90 = forward, negative = behind).
 * - Knees: flexion. The shin direction is thigh - knee.
 * - Ankles: foot direction is shin + 90 + ankle. 0 = neutral, positive = toes up.
 * - [shiftX]: horizontal offset of the hip in body units.
 *
 * Sideways values (3D; all 0 keeps the classic side view exactly):
 * - [sideBend]: torso leans toward the near side (+) or far side (-).
 * - [twist]: chest turns toward the near side (+) or far side (-) around the spine.
 * - Arm/leg "out": swings the limb out to its own side (+) or across the body (-).
 * - Arm "turn": rotates the arm at the shoulder; with the arm out to the side, +90 bends the forearm up.
 * - [roll]: turns the whole body about its long axis through the hip; +90 lies on the far side, near side up.
 * - [headTilt]: ear toward the near shoulder (+) or far shoulder (-).
 * - Leg "turn": rotates the leg at the hip; + turns the knee out (a bent knee then brings the foot inward).
 * - Wrists: 0 = no separate hand is drawn (the arm ends in a round hand, as in every older pose).
 *   Any other value draws a hand bent from the forearm toward the body's up (+) or down (-), e.g.
 *   -70 with the arm reaching forward = fingers pointing to the floor.
 * - Palms (only with a bent wrist): 0 = no thumb drawn; 90 = palm down (thumb toward the body's
 *   middle), -90 = palm up (thumb pointing out to the side). Shows which way the palm faces.
 *
 * "Near" limbs face the viewer and are drawn in front; "far" limbs are drawn behind.
 */
data class Pose(
    val torso: Float = 180f,
    val spine: Float = 0f,
    val head: Float = 0f,
    val nearShoulder: Float = 0f,
    val nearElbow: Float = 0f,
    val farShoulder: Float = 0f,
    val farElbow: Float = 0f,
    val nearHip: Float = 0f,
    val nearKnee: Float = 0f,
    val nearAnkle: Float = 0f,
    val farHip: Float = 0f,
    val farKnee: Float = 0f,
    val farAnkle: Float = 0f,
    val shiftX: Float = 0f,
    val sideBend: Float = 0f,
    val twist: Float = 0f,
    val nearArmOut: Float = 0f,
    val farArmOut: Float = 0f,
    val nearLegOut: Float = 0f,
    val farLegOut: Float = 0f,
    val nearLegTurn: Float = 0f,
    val farLegTurn: Float = 0f,
    val headTilt: Float = 0f,
    val nearArmTurn: Float = 0f,
    val farArmTurn: Float = 0f,
    val roll: Float = 0f,
    val nearWrist: Float = 0f,
    val farWrist: Float = 0f,
    val nearPalm: Float = 0f,
    val farPalm: Float = 0f,
) {
    fun lerp(to: Pose, t: Float): Pose = Pose(
        torso = mix(torso, to.torso, t),
        spine = mix(spine, to.spine, t),
        head = mix(head, to.head, t),
        nearShoulder = mix(nearShoulder, to.nearShoulder, t),
        nearElbow = mix(nearElbow, to.nearElbow, t),
        farShoulder = mix(farShoulder, to.farShoulder, t),
        farElbow = mix(farElbow, to.farElbow, t),
        nearHip = mix(nearHip, to.nearHip, t),
        nearKnee = mix(nearKnee, to.nearKnee, t),
        nearAnkle = mix(nearAnkle, to.nearAnkle, t),
        farHip = mix(farHip, to.farHip, t),
        farKnee = mix(farKnee, to.farKnee, t),
        farAnkle = mix(farAnkle, to.farAnkle, t),
        shiftX = mix(shiftX, to.shiftX, t),
        sideBend = mix(sideBend, to.sideBend, t),
        twist = mix(twist, to.twist, t),
        nearArmOut = mix(nearArmOut, to.nearArmOut, t),
        farArmOut = mix(farArmOut, to.farArmOut, t),
        nearLegOut = mix(nearLegOut, to.nearLegOut, t),
        farLegOut = mix(farLegOut, to.farLegOut, t),
        nearLegTurn = mix(nearLegTurn, to.nearLegTurn, t),
        farLegTurn = mix(farLegTurn, to.farLegTurn, t),
        headTilt = mix(headTilt, to.headTilt, t),
        nearArmTurn = mix(nearArmTurn, to.nearArmTurn, t),
        farArmTurn = mix(farArmTurn, to.farArmTurn, t),
        roll = mix(roll, to.roll, t),
        nearWrist = mix(nearWrist, to.nearWrist, t),
        farWrist = mix(farWrist, to.farWrist, t),
        nearPalm = mix(nearPalm, to.nearPalm, t),
        farPalm = mix(farPalm, to.farPalm, t),
    )

    operator fun get(param: PoseParam): Float = param.read(this)

    fun with(param: PoseParam, value: Float): Pose = param.write(this, value)

    /** Kotlin source for this pose, used by Pose Studio's "Copy" button. */
    fun toKotlin(): String {
        val defaults = Pose()
        val parts = PoseParam.entries
            .filter { this[it] != defaults[it] }
            .joinToString(", ") { "${it.property} = ${format(this[it])}f" }
        return "Pose($parts)"
    }

    companion object {
        val Standing = Pose()

        private fun mix(a: Float, b: Float, t: Float) = a + (b - a) * t

        private fun format(v: Float): String {
            val rounded = Math.round(v * 10f) / 10f
            return if (rounded == rounded.toInt().toFloat()) rounded.toInt().toString() else rounded.toString()
        }
    }
}

/** Every editable pose value, with a slider range for Pose Studio. */
enum class PoseParam(
    val label: String,
    val property: String,
    val min: Float,
    val max: Float,
    val read: (Pose) -> Float,
    val write: (Pose, Float) -> Pose,
) {
    Torso("Torso", "torso", 60f, 270f, { it.torso }, { p, v -> p.copy(torso = v) }),
    Spine("Spine curve", "spine", -30f, 30f, { it.spine }, { p, v -> p.copy(spine = v) }),
    Head("Head", "head", -60f, 60f, { it.head }, { p, v -> p.copy(head = v) }),
    NearShoulder("Near shoulder", "nearShoulder", -90f, 230f, { it.nearShoulder }, { p, v -> p.copy(nearShoulder = v) }),
    NearElbow("Near elbow", "nearElbow", 0f, 160f, { it.nearElbow }, { p, v -> p.copy(nearElbow = v) }),
    FarShoulder("Far shoulder", "farShoulder", -90f, 230f, { it.farShoulder }, { p, v -> p.copy(farShoulder = v) }),
    FarElbow("Far elbow", "farElbow", 0f, 160f, { it.farElbow }, { p, v -> p.copy(farElbow = v) }),
    NearHip("Near hip", "nearHip", -120f, 160f, { it.nearHip }, { p, v -> p.copy(nearHip = v) }),
    NearKnee("Near knee", "nearKnee", 0f, 170f, { it.nearKnee }, { p, v -> p.copy(nearKnee = v) }),
    NearAnkle("Near ankle", "nearAnkle", -110f, 40f, { it.nearAnkle }, { p, v -> p.copy(nearAnkle = v) }),
    FarHip("Far hip", "farHip", -120f, 160f, { it.farHip }, { p, v -> p.copy(farHip = v) }),
    FarKnee("Far knee", "farKnee", 0f, 170f, { it.farKnee }, { p, v -> p.copy(farKnee = v) }),
    FarAnkle("Far ankle", "farAnkle", -110f, 40f, { it.farAnkle }, { p, v -> p.copy(farAnkle = v) }),
    ShiftX("Shift", "shiftX", -60f, 60f, { it.shiftX }, { p, v -> p.copy(shiftX = v) }),
    SideBend("Side bend", "sideBend", -45f, 45f, { it.sideBend }, { p, v -> p.copy(sideBend = v) }),
    Twist("Twist", "twist", -60f, 60f, { it.twist }, { p, v -> p.copy(twist = v) }),
    NearArmOut("Near arm out", "nearArmOut", -60f, 120f, { it.nearArmOut }, { p, v -> p.copy(nearArmOut = v) }),
    FarArmOut("Far arm out", "farArmOut", -60f, 120f, { it.farArmOut }, { p, v -> p.copy(farArmOut = v) }),
    NearLegOut("Near leg out", "nearLegOut", -30f, 80f, { it.nearLegOut }, { p, v -> p.copy(nearLegOut = v) }),
    FarLegOut("Far leg out", "farLegOut", -30f, 80f, { it.farLegOut }, { p, v -> p.copy(farLegOut = v) }),
    NearLegTurn("Near leg turn", "nearLegTurn", -60f, 80f, { it.nearLegTurn }, { p, v -> p.copy(nearLegTurn = v) }),
    FarLegTurn("Far leg turn", "farLegTurn", -60f, 80f, { it.farLegTurn }, { p, v -> p.copy(farLegTurn = v) }),
    HeadTilt("Head tilt", "headTilt", -50f, 50f, { it.headTilt }, { p, v -> p.copy(headTilt = v) }),
    NearArmTurn("Near arm turn", "nearArmTurn", -120f, 120f, { it.nearArmTurn }, { p, v -> p.copy(nearArmTurn = v) }),
    FarArmTurn("Far arm turn", "farArmTurn", -120f, 120f, { it.farArmTurn }, { p, v -> p.copy(farArmTurn = v) }),
    Roll("Body roll", "roll", -100f, 100f, { it.roll }, { p, v -> p.copy(roll = v) }),
    NearWrist("Near wrist", "nearWrist", -90f, 90f, { it.nearWrist }, { p, v -> p.copy(nearWrist = v) }),
    FarWrist("Far wrist", "farWrist", -90f, 90f, { it.farWrist }, { p, v -> p.copy(farWrist = v) }),
    NearPalm("Near palm", "nearPalm", -90f, 90f, { it.nearPalm }, { p, v -> p.copy(nearPalm = v) }),
    FarPalm("Far palm", "farPalm", -90f, 90f, { it.farPalm }, { p, v -> p.copy(farPalm = v) }),
}
