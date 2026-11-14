package dev.clean.stretch.figure

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D point in body units. Axes extend the 2D side view:
 * x = forward (the way the figure faces), y = down (floor at 0, above is negative),
 * z = toward the viewer of the side view (the "near" side of the body).
 * With every sideways pose value at 0, x and y match the old 2D figure exactly.
 */
data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    operator fun unaryMinus() = Vec3(-x, -y, -z)
    fun dot(o: Vec3) = x * o.x + y * o.y + z * o.z
    fun cross(o: Vec3) = Vec3(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x)
    fun length() = sqrt(dot(this))
    fun xy() = Vec(x, y)

    /** Rotates this vector about the unit [axis] by [degrees] (right-hand rule). */
    fun rotate(axis: Vec3, degrees: Float): Vec3 {
        val r = degrees * PI.toFloat() / 180f
        val c = cos(r); val s = sin(r)
        return this * c + axis.cross(this) * s + axis * (axis.dot(this) * (1f - c))
    }

    companion object {
        val Side = Vec3(0f, 0f, 1f)

        /** Pose-convention angle in the side (x, y) plane: 0 = down, 90 = forward. */
        fun dir(degrees: Float): Vec3 { val v = Vec.dir(degrees); return Vec3(v.x, v.y, 0f) }
    }
}

private fun cosd(d: Float) = cos(d * PI.toFloat() / 180f)
private fun sind(d: Float) = sin(d * PI.toFloat() / 180f)

/** Half-widths of the body seen from the front (body units, ~cm). */
object Width3d {
    /** Torso slab half-width at the shoulders and at the hips (the stroke adds its own thickness). */
    const val CHEST_HALF = 8f
    const val PELVIS_HALF = 6f
    /** Where the arms and legs attach, measured out from the centre line. */
    const val ARM_ROOT = 20f
    const val LEG_ROOT = 8.5f
}

/**
 * Solved 3D figure. [points] uses the same [Joint]s as the 2D figure; arms and legs start from
 * their own roots (left/right of the centre line), returned by [root].
 */
class Figure3d(
    val points: Map<Joint, Vec3>,
    val spineControl: Vec3,
    /** Unit vector across the chest, toward the near side. */
    val chestSide: Vec3,
    val roots: Map<Segment, Vec3>,
    val torsoAngle: Float,
    val neckAngle: Float,
    /** Unit vector across the hips, toward the near side (changes only when the body rolls). */
    val pelvisSide: Vec3 = Vec3.Side,
    /** Fingertip points for hands bent at the wrist, keyed by [Joint.NearHand] / [Joint.FarHand]. Drawing only. */
    val handTips: Map<Joint, Vec3> = emptyMap(),
    /** Thumb tips for hands with a palm direction set, same keys as [handTips]. Drawing only. */
    val thumbTips: Map<Joint, Vec3> = emptyMap(),
) {
    operator fun get(j: Joint): Vec3 = points.getValue(j)

    /** Start point of [seg]: limb roots sit out to the side; everything else starts at its joint. */
    fun root(seg: Segment): Vec3 = roots[seg] ?: get(seg.from)

    fun end(seg: Segment): Vec3 = get(seg.to)

    /** Corners of the torso slab: near hip, near shoulder, far shoulder, far hip, plus the two back-curve controls. */
    fun torsoOutline(): List<Vec3> {
        val hip = get(Joint.Hip); val sh = get(Joint.Shoulder)
        val pelvis = pelvisSide * Width3d.PELVIS_HALF
        val chest = chestSide * Width3d.CHEST_HALF
        val mid = (pelvis + chest) * 0.5f
        return listOf(hip + pelvis, sh + chest, sh - chest, hip - pelvis, spineControl + mid, spineControl - mid)
    }

    fun minY(): Float = points.minOf { (j, p) -> p.y - Body.radius(j) }

    /** The same figure flattened to the classic 2D side view. */
    fun sideView(): Figure = Figure(points.mapValues { it.value.xy() }, spineControl.xy(), torsoAngle, neckAngle)

    companion object {
        /** Forward kinematics in 3D, then drop the figure so its lowest part rests on the floor. */
        fun solve(pose: Pose, grounded: Boolean = true): Figure3d {
            val hip = Vec3(pose.shiftX, 0f, 0f)

            // Torso frame: up (hip -> shoulders), forward (chest direction) and side (toward near side).
            var up = Vec3.dir(pose.torso)
            var fwd = Vec3.dir(pose.torso - 90f)
            var side = Vec3.Side
            // Side bend: tip the torso toward the near side (+) or far side (-), about the chest direction.
            if (pose.sideBend != 0f) {
                up = up.rotate(fwd, -pose.sideBend)
                side = side.rotate(fwd, -pose.sideBend)
            }
            // Twist: turn the chest toward the near side (+) or far side (-), about the spine.
            if (pose.twist != 0f) {
                fwd = fwd.rotate(up, pose.twist)
                side = side.rotate(up, pose.twist)
            }

            val shoulder = hip + up * Body.TORSO
            val neckAngle = pose.torso + pose.head
            val neckDir = (up * cosd(pose.head) - fwd * sind(pose.head)).let { if (pose.headTilt == 0f) it else it.rotate(fwd, -pose.headTilt) }
            val neck = shoulder + neckDir * Body.NECK
            val head = neck + neckDir * Body.HEAD_RADIUS

            // [turn] rotates the arm about the upper arm (shoulder rotation), which changes the
            // direction the elbow bends: with the arm out to the side, +90 bends the forearm up.
            fun arm(root: Vec3, raise: Float, bend: Float, out: Float, turn: Float, sign: Float, outward: Vec3): Pair<Vec3, Vec3> {
                val sagittal = -up * cosd(raise) + fwd * sind(raise)
                val upper = sagittal * cosd(out) + outward * sind(out)
                val bendDir = (up * sind(raise) + fwd * cosd(raise)).let { if (turn == 0f) it else it.rotate(upper, -turn * sign) }
                val fore = upper * cosd(bend) + bendDir * sind(bend)
                val elbow = root + upper * Body.UPPER_ARM
                return elbow to elbow + fore * Body.FOREARM
            }

            // [turn] rotates the leg about the thigh (hip rotation): positive turns the knee out, so a
            // bent knee swings the foot in toward the middle (as in the butterfly stretch).
            fun leg(root: Vec3, hipAngle: Float, knee: Float, ankle: Float, out: Float, turn: Float, outward: Vec3): Triple<Vec3, Vec3, Vec3> {
                val t = Vec3.dir(hipAngle)
                val thigh = t * cosd(out) + outward * sind(out)
                val tPerp = Vec3.dir(hipAngle + 90f).let { if (turn == 0f) it else it.rotate(thigh, -turn * outward.z) }
                val shin = thigh * cosd(knee) - tPerp * sind(knee)
                val shinPerp = thigh * sind(knee) + tPerp * cosd(knee)
                val foot = -shin * sind(ankle) + shinPerp * cosd(ankle)
                val kneeP = root + thigh * Body.THIGH
                val ankleP = kneeP + shin * Body.SHIN
                return Triple(kneeP, ankleP, ankleP + foot * Body.FOOT)
            }

            val nearArmRoot = shoulder + side * Width3d.ARM_ROOT
            val farArmRoot = shoulder - side * Width3d.ARM_ROOT
            val (nearElbow, nearHand) = arm(nearArmRoot, pose.nearShoulder, pose.nearElbow, pose.nearArmOut, pose.nearArmTurn, 1f, side)
            val (farElbow, farHand) = arm(farArmRoot, pose.farShoulder, pose.farElbow, pose.farArmOut, pose.farArmTurn, -1f, -side)

            // A bent wrist adds a hand past the forearm, turned toward the torso's up (+) or down (-).
            fun handTip(elbow: Vec3, hand: Vec3, wrist: Float): Vec3? {
                if (wrist == 0f) return null
                val fore = (hand - elbow) * (1f / Body.FOREARM)
                var perp = up - fore * up.dot(fore)
                if (perp.length() < 0.05f) perp = fwd - fore * fwd.dot(fore)
                perp = perp * (1f / perp.length())
                return hand + (fore * cosd(wrist) + perp * sind(wrist)) * Body.HAND
            }
            // Thumb: starts at the wrist and points toward the body's middle when the palm faces
            // down (palm 90), out to the side when it faces up (palm -90).
            fun thumbTip(elbow: Vec3, hand: Vec3, wrist: Float, palm: Float, outward: Vec3): Vec3? {
                if (wrist == 0f || palm == 0f) return null
                val fore = (hand - elbow) * (1f / Body.FOREARM)
                var medial = -outward - fore * (-outward).dot(fore)
                if (medial.length() < 0.05f) medial = fwd - fore * fwd.dot(fore)
                medial = medial * (1f / medial.length())
                val side = medial.rotate(fore, palm - 90f)
                val tip = handTip(elbow, hand, wrist) ?: return null
                val handDir = (tip - hand) * (1f / Body.HAND)
                return hand + (side * 0.85f + handDir * 0.5f) * Body.THUMB
            }
            val rawThumbs = listOfNotNull(
                thumbTip(nearElbow, nearHand, pose.nearWrist, pose.nearPalm, side)?.let { Joint.NearHand to it },
                thumbTip(farElbow, farHand, pose.farWrist, pose.farPalm, -side)?.let { Joint.FarHand to it },
            ).toMap()
            val rawTips = listOfNotNull(
                handTip(nearElbow, nearHand, pose.nearWrist)?.let { Joint.NearHand to it },
                handTip(farElbow, farHand, pose.farWrist)?.let { Joint.FarHand to it },
            ).toMap()

            val nearLegRoot = hip + Vec3.Side * Width3d.LEG_ROOT
            val farLegRoot = hip - Vec3.Side * Width3d.LEG_ROOT
            val (nearKnee, nearAnkle, nearToe) = leg(nearLegRoot, pose.nearHip, pose.nearKnee, pose.nearAnkle, pose.nearLegOut, pose.nearLegTurn, Vec3.Side)
            val (farKnee, farAnkle, farToe) = leg(farLegRoot, pose.farHip, pose.farKnee, pose.farAnkle, pose.farLegOut, pose.farLegTurn, -Vec3.Side)

            val spineControl = (hip + shoulder) * 0.5f - fwd * pose.spine

            val rawPoints = mapOf(
                Joint.Hip to hip, Joint.Shoulder to shoulder, Joint.Neck to neck, Joint.Head to head,
                Joint.NearElbow to nearElbow, Joint.NearHand to nearHand,
                Joint.FarElbow to farElbow, Joint.FarHand to farHand,
                Joint.NearKnee to nearKnee, Joint.NearAnkle to nearAnkle, Joint.NearToe to nearToe,
                Joint.FarKnee to farKnee, Joint.FarAnkle to farAnkle, Joint.FarToe to farToe,
            )
            val rawRoots = mapOf(
                Segment.NearUpperArm to nearArmRoot, Segment.FarUpperArm to farArmRoot,
                Segment.NearThigh to nearLegRoot, Segment.FarThigh to farLegRoot,
            )
            // Roll: turn the whole body about the forward (x) axis through the hip, e.g. onto its
            // side for side-lying stretches. + lifts the near side up.
            val axis = Vec3(1f, 0f, 0f)
            fun rolled(p: Vec3) = if (pose.roll == 0f) p else hip + (p - hip).rotate(axis, pose.roll)
            fun rolledDir(v: Vec3) = if (pose.roll == 0f) v else v.rotate(axis, pose.roll)
            val raw = rawPoints.mapValues { rolled(it.value) }
            val roots = rawRoots.mapValues { rolled(it.value) }
            val tips = rawTips.mapValues { rolled(it.value) }
            val thumbs = rawThumbs.mapValues { rolled(it.value) }
            val spine = rolled(spineControl)
            val chest = rolledDir(side)
            val pelvisSide = rolledDir(Vec3.Side)
            val rolledShoulder = raw.getValue(Joint.Shoulder)
            if (!grounded) return Figure3d(raw, spine, chest, roots, pose.torso, neckAngle, pelvisSide, tips, thumbs)
            // A curved spine bulges past the hip-shoulder line; include its outer edge.
            val spineBottom = (hip * 0.25f + spine * 0.5f + rolledShoulder * 0.25f).y + Body.width(Segment.Torso) / 2f
            val lowest = max(raw.maxOf { (j, p) -> p.y + Body.radius(j) }, spineBottom)
            val drop = Vec3(0f, -lowest, 0f)
            return Figure3d(
                raw.mapValues { it.value + drop }, spine + drop, chest,
                roots.mapValues { it.value + drop }, pose.torso, neckAngle, pelvisSide,
                tips.mapValues { it.value + drop },
                thumbs.mapValues { it.value + drop },
            )
        }
    }
}

/**
 * Camera angle. [yaw] turns around the figure: 0 = the classic side view, 90 = facing the figure.
 * [pitch] raises the camera: 0 = eye level with the floor, 90 = looking straight down.
 */
data class Camera(val yaw: Float, val pitch: Float) {
    fun lerp(o: Camera, t: Float) = Camera(yaw + (o.yaw - yaw) * t, pitch + (o.pitch - pitch) * t)
}

/** Named viewpoints the app offers. Side keeps a little height so the floor reads as a floor. */
enum class View(val label: String, val camera: Camera) {
    Side("Side", Camera(0f, 12f)),
    Angled("Angled", Camera(38f, 24f)),
    Front("Front", Camera(90f, 10f)),
    Above("Above", Camera(0f, 72f)),
    ;

    fun next(): View = entries[(ordinal + 1) % entries.size]
}

/**
 * A point on screen (body units before fitting to the canvas) and its perspective scale.
 * [depth] is distance toward the camera (higher parts are nearer when looking down);
 * [level] ignores height: how far toward the camera the point is across the floor.
 */
data class Projected(val x: Float, val y: Float, val depth: Float, val scale: Float, val level: Float) {
    val xy get() = Vec(x, y)
}

/** Gentle pinhole perspective around [center], viewed by [camera]. */
class Projection(val camera: Camera, val center: Vec3) {
    private val cy = cosd(camera.yaw); private val sy = sind(camera.yaw)
    private val cp = cosd(camera.pitch); private val sp = sind(camera.pitch)

    fun project(w: Vec3): Projected {
        val p = w - center
        val x1 = p.x * cy - p.z * sy
        val z1 = p.x * sy + p.z * cy
        val y2 = p.y * cp + z1 * sp
        val depth = -p.y * sp + z1 * cp
        val f = DISTANCE / max(DISTANCE - depth, DISTANCE * 0.3f)
        return Projected(center.x + x1 * f, center.y + y2 * f, depth, f, z1)
    }

    companion object {
        /** Camera distance in body units; large enough that perspective stays subtle. */
        const val DISTANCE = 800f
    }
}

/**
 * Everything needed to draw one stretch from one camera without the picture jumping around:
 * the projection, the exercise mat under the figure, and a [frame] covering every keyframe.
 */
class Scene(val projection: Projection, val matX: ClosedFloatingPointRange<Float>, val frame: Box) {
    val matZ = -MAT_HALF_WIDTH..MAT_HALF_WIDTH

    fun matCorners(): List<Vec3> = listOf(
        Vec3(matX.start, 0f, matZ.start), Vec3(matX.endInclusive, 0f, matZ.start),
        Vec3(matX.endInclusive, 0f, matZ.endInclusive), Vec3(matX.start, 0f, matZ.endInclusive),
    )

    companion object {
        const val MAT_HALF_WIDTH = 30f

        /**
         * [poses] are every keyframe; [extra] are world points (props) that must stay in view.
         */
        fun of(poses: List<Pose>, camera: Camera, extra: List<Vec3> = emptyList()): Scene {
            val figures = poses.map { Figure3d.solve(it) }
            val all = figures.flatMap { f -> f.points.values + f.roots.values }
            val minX = min(all.minOf { it.x }, extra.minOfOrNull { it.x } ?: Float.MAX_VALUE)
            val maxX = max(all.maxOf { it.x }, extra.maxOfOrNull { it.x } ?: -Float.MAX_VALUE)
            val minY = figures.minOf { it.minY() }
            val center = Vec3((minX + maxX) / 2f, minY / 2f, 0f)
            val projection = Projection(camera, center)
            val matX = (minX - 12f)..(maxX + 12f)

            var box: Box? = null
            fun add(p: Vec3, r: Float) {
                val s = projection.project(p)
                val rr = r * s.scale
                val b = Box(s.x - rr, s.y - rr, s.x + rr, s.y + rr)
                box = box?.union(b) ?: b
            }
            figures.forEach { f ->
                f.points.forEach { (j, p) -> add(p, Body.radius(j)) }
                f.roots.values.forEach { add(it, 8f) }
                f.torsoOutline().take(4).forEach { add(it, Body.width(Segment.Torso) / 2f) }
            }
            extra.forEach { add(it, 2f) }
            val scene = Scene(projection, matX, Box(0f, 0f, 0f, 0f))
            scene.matCorners().forEach { add(it, 0f) }
            return Scene(projection, matX, box!!)
        }
    }
}
