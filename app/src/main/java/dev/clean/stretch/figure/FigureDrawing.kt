package dev.clean.stretch.figure

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * What the figure picture is made of, as plain shapes in canvas pixels. The app (Compose) and
 * tools/pose-preview (Java2D) both paint this same list, so a preview image matches the app.
 */
enum class Ink { Floor, Grid, Mat, MatEdge, Shadow, Prop, Body, Highlight, Outline }

sealed interface Shape {
    val ink: Ink
    /** 0 = full color, 1 = background color (parts farther away fade a little). */
    val fade: Float
    val alpha: Float

    /** Filled polygon; with [stroke] > 0 also outlined with round joins at that width. */
    data class Poly(val points: List<Vec>, override val ink: Ink, val stroke: Float = 0f, val fill: Boolean = true, override val fade: Float = 0f, override val alpha: Float = 1f) : Shape

    /** Thick line with round ends (a limb, a roller seen from the side...). */
    data class Line(val a: Vec, val b: Vec, val width: Float, override val ink: Ink, override val fade: Float = 0f, override val alpha: Float = 1f, val round: Boolean = true) : Shape

    data class Dot(val center: Vec, val radius: Float, override val ink: Ink, override val fade: Float = 0f, override val alpha: Float = 1f) : Shape

    /** The torso: near hip → (curve) → near shoulder → far shoulder → (curve) → far hip, filled and stroked. */
    data class Torso(val hipNear: Vec, val curveNear: Vec, val shoulderNear: Vec, val shoulderFar: Vec, val curveFar: Vec, val hipFar: Vec, val width: Float, override val ink: Ink, override val fade: Float = 0f, override val alpha: Float = 1f) : Shape
}

/** A wall or foam roller drawn with the figure (x in body units from the hip). */
data class StageProp(val kind: Kind, val x: Float) {
    enum class Kind { Wall, Roller }
}

/**
 * Everything one stretch needs to be drawn from one camera: the [scene] (projection, mat and a
 * frame covering every keyframe, so the figure doesn't jump) plus its prop.
 */
class Stage(val scene: Scene, val prop: StageProp?, val wallTop: Float) {
    companion object {
        const val ROLLER_RADIUS = 8f
        const val ROLLER_HALF_LENGTH = 24f
        const val WALL_HALF_WIDTH = 40f
        /** How far a drawn wall reaches above the tallest keyframe. */
        const val WALL_MARGIN = 6f

        /** A stage that keeps every pose in [poses] (and the prop) in view from [camera]. */
        fun of(poses: List<Pose>, camera: Camera, prop: StageProp?): Stage {
            val wallTop = poses.minOf { Figure3d.solve(it).minY() } - WALL_MARGIN
            val extra = when (prop?.kind) {
                StageProp.Kind.Roller -> listOf(
                    Vec3(prop.x - ROLLER_RADIUS, -2f * ROLLER_RADIUS, -ROLLER_HALF_LENGTH),
                    Vec3(prop.x + ROLLER_RADIUS, 0f, ROLLER_HALF_LENGTH),
                )
                StageProp.Kind.Wall -> listOf(Vec3(prop.x, wallTop, 0f), Vec3(prop.x, 0f, 0f))
                null -> emptyList()
            }
            return Stage(Scene.of(poses, camera, extra), prop, wallTop)
        }
    }
}

object FigureDrawing {
    /** Width of the dark outline around every body part, in body units (each side). */
    const val OUTLINE = 1.6f

    private class Group(val segments: List<Segment>, val withHead: Boolean = false)

    /** Drawing groups in side-view order (far arm first); re-sorted by depth for each camera. */
    private val groups = listOf(
        Group(listOf(Segment.FarUpperArm, Segment.FarForearm)),
        Group(listOf(Segment.FarThigh, Segment.FarShin, Segment.FarFoot)),
        Group(listOf(Segment.Torso, Segment.Neck), withHead = true),
        Group(listOf(Segment.NearThigh, Segment.NearShin, Segment.NearFoot)),
        Group(listOf(Segment.NearUpperArm, Segment.NearForearm)),
    )

    /**
     * Shapes for [pose] on [stage], fitted into a [width] × [height] canvas.
     * [mirrored] shows the other side of the body; [glow] (0..1) pulses the highlighted parts;
     * [showGrid] adds floor tiles (skip it for tiny thumbnails).
     */
    fun shapes(
        pose: Pose,
        stage: Stage,
        highlight: Set<Segment>,
        width: Float,
        height: Float,
        mirrored: Boolean = false,
        glow: Float = 0f,
        showGrid: Boolean = true,
    ): List<Shape> {
        val figure = Figure3d.solve(pose)
        val scene = stage.scene
        val proj = scene.projection
        val frame = scene.frame
        val out = ArrayList<Shape>(160)

        // Fit the frame (plus a little air) into the canvas.
        val pad = 10f
        val left = frame.left - pad
        val top = frame.top - pad
        val fw = frame.width + 2 * pad
        val fh = frame.height + 2 * pad
        val px = min(width / fw, height / fh)
        val ox = (width - fw * px) / 2f
        val oy = (height - fh * px) / 2f

        fun map(p: Projected): Vec {
            val x = ox + (p.x - left) * px
            return Vec(if (mirrored) width - x else x, oy + (p.y - top) * px)
        }
        fun at(w: Vec3) = map(proj.project(w))

        // Floor: a large tiled plane whose back edge reads as the horizon, then the mat.
        val cx = proj.center.x
        val fx0 = cx - 260f
        val fx1 = cx + 260f
        val fz0 = -260f
        val fz1 = 260f
        out += Shape.Poly(listOf(Vec3(fx0, 0f, fz0), Vec3(fx1, 0f, fz0), Vec3(fx1, 0f, fz1), Vec3(fx0, 0f, fz1)).map(::at), Ink.Floor)
        if (showGrid) {
            val gw = 0.7f * px
            var gx = (fx0 / 20f).toInt() * 20f
            while (gx <= fx1) { out += Shape.Line(at(Vec3(gx, 0f, fz0)), at(Vec3(gx, 0f, fz1)), gw, Ink.Grid, round = false); gx += 20f }
            var gz = fz0
            while (gz <= fz1) { out += Shape.Line(at(Vec3(fx0, 0f, gz)), at(Vec3(fx1, 0f, gz)), gw, Ink.Grid, round = false); gz += 20f }
        }
        val mat = scene.matCorners().map(::at)
        out += Shape.Poly(mat, Ink.Mat)
        out += Shape.Poly(mat, Ink.MatEdge, stroke = 1.4f * px, fill = false)

        // Shadows straight below every part, flattened onto the floor (a light overhead).
        fun floorCapsule(a: Vec3, b: Vec3, r: Float): List<Vec> {
            var dx = b.x - a.x
            var dz = b.z - a.z
            val len = sqrt(dx * dx + dz * dz)
            if (len < 1e-3f) { dx = 1f; dz = 0f } else { dx /= len; dz /= len }
            val pts = ArrayList<Vec>(18)
            fun arc(cx0: Float, cz: Float, from: Float) {
                for (i in 0..8) {
                    val t = (from + i * 22.5f) * PI_F / 180f
                    pts += at(Vec3(cx0 + r * (dx * cos(t) - dz * sin(t)), 0f, cz + r * (dz * cos(t) + dx * sin(t))))
                }
            }
            arc(b.x, b.z, -90f)
            arc(a.x, a.z, 90f)
            return pts
        }
        fun ground(v: Vec3) = Vec3(v.x, 0f, v.z)
        val outline = figure.torsoOutline()
        val torsoR = Body.width(Segment.Torso) / 2f
        out += Shape.Poly(floorCapsule(ground(outline[0]), ground(outline[1]), torsoR), Ink.Shadow)
        out += Shape.Poly(floorCapsule(ground(outline[3]), ground(outline[2]), torsoR), Ink.Shadow)
        out += Shape.Poly(outline.take(4).map { at(ground(it)) }, Ink.Shadow)
        Segment.entries.filter { it != Segment.Torso }.forEach { seg ->
            out += Shape.Poly(floorCapsule(ground(figure.root(seg)), ground(figure.end(seg)), Body.width(seg) / 2f), Ink.Shadow)
        }
        val headGround = ground(figure[Joint.Head])
        out += Shape.Poly(floorCapsule(headGround, headGround, Body.HEAD_RADIUS), Ink.Shadow)

        // Props.
        val prop = stage.prop
        val hipDepth = proj.project(figure[Joint.Hip]).depth
        var wallInFront: List<Shape> = emptyList()
        if (prop?.kind == StageProp.Kind.Roller) {
            val r = Stage.ROLLER_RADIUS
            val a = Vec3(prop.x, -r, -Stage.ROLLER_HALF_LENGTH)
            val b = Vec3(prop.x, -r, Stage.ROLLER_HALF_LENGTH)
            out += Shape.Poly(floorCapsule(Vec3(prop.x, 0f, a.z), Vec3(prop.x, 0f, b.z), r), Ink.Shadow)
            val pa = proj.project(a)
            val pb = proj.project(b)
            val w = r * (pa.scale + pb.scale) * px
            out += Shape.Line(map(pa), map(pb), w + 2 * OUTLINE * px, Ink.Outline)
            out += Shape.Line(map(pa), map(pb), w, Ink.Prop)
        }
        if (prop?.kind == StageProp.Kind.Wall) {
            val hw = Stage.WALL_HALF_WIDTH
            val wall = listOf(
                Vec3(prop.x, 0f, -hw), Vec3(prop.x, stage.wallTop, -hw),
                Vec3(prop.x, stage.wallTop, hw), Vec3(prop.x, 0f, hw),
            ).map(::at)
            // A wall between the camera and the figure is drawn last and see-through.
            val inFront = proj.project(Vec3(prop.x, stage.wallTop / 2f, 0f)).depth > hipDepth + 10f
            val alpha = if (inFront) 0.35f else 1f
            val shapes = listOf(Shape.Poly(wall, Ink.Prop, stroke = 5f * px, alpha = alpha))
            if (inFront) wallInFront = shapes else out += shapes
        }

        // Body: whole limbs sorted far-to-near so nearer parts cover farther ones. Every part gets
        // the same dark outline, so outlines don't come and go as the figure moves.
        fun widthOf(seg: Segment, extra: Float): Float {
            val s = (proj.project(figure.root(seg)).scale + proj.project(figure.end(seg)).scale) / 2f
            return (Body.width(seg) + extra) * s * px
        }
        fun part(seg: Segment, ink: Ink, extra: Float, fade: Float, alpha: Float = 1f): Shape =
            if (seg == Segment.Torso) {
                Shape.Torso(at(outline[0]), at(outline[4]), at(outline[1]), at(outline[2]), at(outline[5]), at(outline[3]), widthOf(seg, extra), ink, fade, alpha)
            } else {
                Shape.Line(at(figure.root(seg)), at(figure.end(seg)), widthOf(seg, extra), ink, fade, alpha)
            }
        fun points(g: Group) = g.segments.flatMap { listOf(figure.root(it), figure.end(it)) }.map { proj.project(it) }
        fun depthOf(g: Group): Float = points(g).map { it.depth }.average().toFloat()
        fun levelOf(g: Group): Float = points(g).map { it.level }.average().toFloat()

        val torsoGroup = groups[2]
        val torsoLevel = levelOf(torsoGroup)
        val head = proj.project(figure[Joint.Head])
        val headR = Body.HEAD_RADIUS * head.scale * px
        groups.sortedBy { depthOf(it) }.forEach { g ->
            // Parts behind the torso (across the floor, ignoring height) fade a little toward the background.
            val back = if (g === torsoGroup) 0f else ((torsoLevel - levelOf(g)) / 14f).coerceIn(0f, 1f)
            if (glow > 0f) g.segments.filter { it in highlight }.forEach { out += part(it, Ink.Highlight, 10f, 0f, 0.18f * glow) }
            // A hand bent at the wrist belongs to its forearm: same outline, ink and highlight.
            val hands = g.segments.mapNotNull { seg ->
                val joint = if (seg == Segment.NearForearm) Joint.NearHand else if (seg == Segment.FarForearm) Joint.FarHand else null
                joint?.let { j -> figure.handTips[j]?.let { tip -> Triple(seg, figure[j], tip) } }
            }
            fun hand(seg: Segment, from: Vec3, tip: Vec3, ink: Ink, extra: Float, fade: Float, alpha: Float = 1f, width: Float = Body.HAND_WIDTH): Shape {
                val s = (proj.project(from).scale + proj.project(tip).scale) / 2f
                return Shape.Line(at(from), at(tip), (width + extra) * s * px, ink, fade, alpha)
            }
            val thumbs = hands.mapNotNull { (seg, from, _) ->
                val j = if (seg == Segment.NearForearm) Joint.NearHand else Joint.FarHand
                figure.thumbTips[j]?.let { Triple(seg, from, it) }
            }
            if (glow > 0f) hands.filter { it.first in highlight }.forEach { (seg, a, b) -> out += hand(seg, a, b, Ink.Highlight, 10f, 0f, 0.18f * glow) }
            thumbs.forEach { (seg, a, b) -> out += hand(seg, a, b, Ink.Outline, 2 * OUTLINE, 0f, width = Body.THUMB_WIDTH) }
            hands.forEach { (seg, a, b) -> out += hand(seg, a, b, Ink.Outline, 2 * OUTLINE, 0f) }
            g.segments.forEach { out += part(it, Ink.Outline, 2 * OUTLINE, 0f) }
            if (g.withHead) out += Shape.Dot(map(head), headR + OUTLINE * head.scale * px, Ink.Outline)
            thumbs.forEach { (seg, a, b) ->
                out += if (seg in highlight) hand(seg, a, b, Ink.Highlight, 0f, back * 0.35f, width = Body.THUMB_WIDTH) else hand(seg, a, b, Ink.Body, 0f, back * 0.55f, width = Body.THUMB_WIDTH)
            }
            hands.forEach { (seg, a, b) ->
                out += if (seg in highlight) hand(seg, a, b, Ink.Highlight, 0f, back * 0.35f) else hand(seg, a, b, Ink.Body, 0f, back * 0.55f)
            }
            g.segments.forEach { seg ->
                out += if (seg in highlight) part(seg, Ink.Highlight, 0f, back * 0.35f) else part(seg, Ink.Body, 0f, back * 0.55f)
            }
            if (g.withHead) out += Shape.Dot(map(head), headR, Ink.Body)
        }
        out += wallInFront
        return out
    }

    private const val PI_F = Math.PI.toFloat()
}
