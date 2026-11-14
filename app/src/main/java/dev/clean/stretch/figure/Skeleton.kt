package dev.clean.stretch.figure

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** Plain 2D point so the figure math stays testable on the JVM without Android. */
data class Vec(val x: Float, val y: Float) {
    operator fun plus(o: Vec) = Vec(x + o.x, y + o.y)
    operator fun minus(o: Vec) = Vec(x - o.x, y - o.y)
    operator fun times(s: Float) = Vec(x * s, y * s)

    companion object {
        /** Unit vector for an absolute angle (see [Pose] for the convention). */
        fun dir(degrees: Float): Vec {
            val r = degrees * PI.toFloat() / 180f
            return Vec(sin(r), cos(r))
        }
    }
}

data class Box(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    val width get() = right - left
    val height get() = bottom - top
    fun union(o: Box) = Box(min(left, o.left), min(top, o.top), max(right, o.right), max(bottom, o.bottom))
}

enum class Joint {
    Hip, Shoulder, Neck, Head,
    NearElbow, NearHand, FarElbow, FarHand,
    NearKnee, NearAnkle, NearToe, FarKnee, FarAnkle, FarToe,
}

/** Drawable body parts. Highlights for target muscles are expressed as segments. */
enum class Segment(val from: Joint, val to: Joint, val near: Boolean?) {
    Torso(Joint.Hip, Joint.Shoulder, null),
    Neck(Joint.Shoulder, Joint.Neck, null),
    NearUpperArm(Joint.Shoulder, Joint.NearElbow, true),
    NearForearm(Joint.NearElbow, Joint.NearHand, true),
    FarUpperArm(Joint.Shoulder, Joint.FarElbow, false),
    FarForearm(Joint.FarElbow, Joint.FarHand, false),
    NearThigh(Joint.Hip, Joint.NearKnee, true),
    NearShin(Joint.NearKnee, Joint.NearAnkle, true),
    NearFoot(Joint.NearAnkle, Joint.NearToe, true),
    FarThigh(Joint.Hip, Joint.FarKnee, false),
    FarShin(Joint.FarKnee, Joint.FarAnkle, false),
    FarFoot(Joint.FarAnkle, Joint.FarToe, false),
}

/** Proportions in body units (roughly centimetres for a 170 cm adult). */
object Body {
    const val TORSO = 50f
    const val NECK = 8f
    const val HEAD_RADIUS = 10.5f
    const val UPPER_ARM = 28f
    const val FOREARM = 27f
    const val THIGH = 44f
    const val SHIN = 43f
    const val FOOT = 15f
    /** Hand length and width, drawn only when a pose bends the wrist ([Pose.nearWrist]). */
    const val HAND = 14f
    const val HAND_WIDTH = 7.5f
    /** Thumb, drawn only when a pose sets a palm direction ([Pose.nearPalm]). */
    const val THUMB = 9.5f
    const val THUMB_WIDTH = 5f

    /** Stroke widths used both for drawing and for keeping thick parts above the floor. */
    fun width(segment: Segment): Float = when (segment) {
        Segment.Torso -> 21f
        Segment.Neck -> 9f
        Segment.NearUpperArm, Segment.FarUpperArm -> 10.5f
        Segment.NearForearm, Segment.FarForearm -> 8.5f
        Segment.NearThigh, Segment.FarThigh -> 15f
        Segment.NearShin, Segment.FarShin -> 11.5f
        Segment.NearFoot, Segment.FarFoot -> 7f
    }

    fun radius(joint: Joint): Float = when (joint) {
        Joint.Hip -> 10.5f
        Joint.Shoulder -> 10.5f
        Joint.Neck -> 4.5f
        Joint.Head -> HEAD_RADIUS
        Joint.NearElbow, Joint.FarElbow -> 5.3f
        Joint.NearHand, Joint.FarHand -> 4.3f
        Joint.NearKnee, Joint.FarKnee -> 7.5f
        Joint.NearAnkle, Joint.FarAnkle -> 5.8f
        Joint.NearToe, Joint.FarToe -> 3.5f
    }
}

/**
 * Solved figure: joint positions in body units, standing on the floor at y = 0
 * (everything above the floor has negative y).
 */
class Figure(val points: Map<Joint, Vec>, val spineControl: Vec, val torsoAngle: Float, val neckAngle: Float) {
    operator fun get(j: Joint): Vec = points.getValue(j)

    /** Bounds including the thickness of each part. */
    fun bounds(): Box {
        var box: Box? = null
        for ((joint, p) in points) {
            val r = Body.radius(joint)
            val b = Box(p.x - r, p.y - r, p.x + r, p.y + r)
            box = box?.union(b) ?: b
        }
        return box!!
    }

    companion object {
        /** Solves the 3D figure (see [Figure3d]) and flattens it to the classic side view. */
        fun solve(pose: Pose, grounded: Boolean = true): Figure = Figure3d.solve(pose, grounded).sideView()

        /** Midpoint of the quadratic curve drawn for the back. */
        fun spineMidpoint(hip: Vec, control: Vec, shoulder: Vec): Vec =
            hip * 0.25f + control * 0.5f + shoulder * 0.25f
    }
}
