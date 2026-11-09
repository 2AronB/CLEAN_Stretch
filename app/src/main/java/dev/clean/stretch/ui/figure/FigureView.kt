package dev.clean.stretch.ui.figure

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.clean.stretch.catalog.Equipment
import dev.clean.stretch.catalog.Prop
import dev.clean.stretch.figure.Body
import dev.clean.stretch.figure.Camera
import dev.clean.stretch.figure.Figure3d
import dev.clean.stretch.figure.Joint
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Projected
import dev.clean.stretch.figure.Scene
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.Vec3
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/** Foam roller radius in body units (about a 16 cm roller). */
const val ROLLER_RADIUS = 8f
private const val ROLLER_HALF_LENGTH = 24f
private const val WALL_HALF_WIDTH = 40f
/** How far a drawn wall reaches above the tallest keyframe. */
private const val WALL_MARGIN = 6f
private const val PI_F = Math.PI.toFloat()

/** Colors for one drawing of the figure. Defaults come from the current Material theme. */
data class FigureColors(val body: Color, val far: Color, val highlight: Color, val halo: Color, val floor: Color, val prop: Color)

@Composable
fun figureColors(background: Color = MaterialTheme.colorScheme.surface): FigureColors {
    val scheme = MaterialTheme.colorScheme
    val body = lerp(scheme.onSurface, scheme.primary, 0.35f)
    return FigureColors(
        body = body,
        far = lerp(body, background, 0.55f),
        highlight = scheme.tertiary,
        halo = background,
        floor = lerp(scheme.outlineVariant, background, 0.2f),
        prop = lerp(scheme.outlineVariant, background, 0.35f),
    )
}

/** Milliseconds since this composable started, advancing every frame while [running]. */
@Composable
fun rememberFigureClock(running: Boolean = true): State<Long> {
    val time = remember { mutableLongStateOf(0L) }
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        val offset = time.longValue
        val start = withFrameMillis { it }
        while (true) {
            withFrameMillis { now -> time.longValue = offset + (now - start) }
        }
    }
    return time
}

/** True when the user picked still figures or turned Android's animations off. */
@Composable
fun rememberReducedMotion(stillFiguresSetting: Boolean): Boolean {
    val context = LocalContext.current
    val systemOff = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    return stillFiguresSetting || systemOff
}

/**
 * Everything one stretch needs to be drawn from one camera: the [scene] (projection, mat and a
 * frame covering every keyframe, so the figure doesn't jump) plus its prop.
 */
class FigureStage(val scene: Scene, val prop: Prop?, val wallTop: Float)

/** Builds a stage that keeps every pose in [poses] (and the prop) in view from [camera]. */
fun figureStage(poses: List<Pose>, camera: Camera, prop: Prop?): FigureStage {
    val wallTop = poses.minOf { Figure3d.solve(it).minY() } - WALL_MARGIN
    val extra = when (prop?.equipment) {
        Equipment.FoamRoller -> listOf(
            Vec3(prop.x - ROLLER_RADIUS, -2f * ROLLER_RADIUS, -ROLLER_HALF_LENGTH),
            Vec3(prop.x + ROLLER_RADIUS, 0f, ROLLER_HALF_LENGTH),
        )
        Equipment.Wall -> listOf(Vec3(prop.x, wallTop, 0f), Vec3(prop.x, 0f, 0f))
        else -> emptyList()
    }
    return FigureStage(Scene.of(poses, camera, extra), prop, wallTop)
}

@Composable
fun rememberFigureStage(key: Any, poses: List<Pose>, camera: Camera, prop: Prop?): FigureStage =
    remember(key, camera, prop) { figureStage(poses, camera, prop) }

/**
 * Draws the figure in 3D on a floor with an exercise mat, seen from the stage's camera.
 * [mirrored] shows the other side of the body. [glow] (0..1) gently pulses the highlighted
 * muscles, e.g. with the breath. [showGrid] adds floor tiles (skip it for tiny thumbnails).
 */
@Composable
fun FigureView(
    pose: Pose,
    stage: FigureStage,
    highlight: Set<Segment>,
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    glow: Float = 0f,
    colors: FigureColors = figureColors(),
    showGrid: Boolean = true,
    description: String = "Animated figure",
) {
    Canvas(modifier.semantics { contentDescription = description }) {
        clipRect {
            drawFigure(Figure3d.solve(pose), stage, highlight, mirrored, glow, colors, showGrid)
        }
    }
}

private class Group(val segments: List<Segment>, val withHead: Boolean = false)

/** Drawing groups in side-view order (far arm first); re-sorted by depth for each camera. */
private val groups = listOf(
    Group(listOf(Segment.FarUpperArm, Segment.FarForearm)),
    Group(listOf(Segment.FarThigh, Segment.FarShin, Segment.FarFoot)),
    Group(listOf(Segment.Torso, Segment.Neck), withHead = true),
    Group(listOf(Segment.NearThigh, Segment.NearShin, Segment.NearFoot)),
    Group(listOf(Segment.NearUpperArm, Segment.NearForearm)),
)

private fun DrawScope.drawFigure(
    figure: Figure3d,
    stage: FigureStage,
    highlight: Set<Segment>,
    mirrored: Boolean,
    glow: Float,
    c: FigureColors,
    showGrid: Boolean,
) {
    val scene = stage.scene
    val proj = scene.projection
    val frame = scene.frame

    // Fit the frame (plus a little air) into the canvas.
    val pad = 10f
    val left = frame.left - pad
    val top = frame.top - pad
    val fw = frame.width + 2 * pad
    val fh = frame.height + 2 * pad
    val px = min(size.width / fw, size.height / fh)
    val ox = (size.width - fw * px) / 2f
    val oy = (size.height - fh * px) / 2f

    fun map(p: Projected): Offset {
        val x = ox + (p.x - left) * px
        return Offset(if (mirrored) size.width - x else x, oy + (p.y - top) * px)
    }
    fun at(w: Vec3) = map(proj.project(w))
    fun polygon(points: List<Vec3>) = Path().apply {
        points.forEachIndexed { i, w -> val o = at(w); if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) }
        close()
    }

    val floorFill = lerp(c.halo, c.floor, 0.14f)
    val grid = lerp(c.halo, c.floor, 0.38f)
    val mat = lerp(c.halo, c.floor, 0.34f)
    val matEdge = lerp(c.halo, c.floor, 0.62f)
    val shadow = lerp(c.halo, c.floor, 0.78f)

    // Floor: a large tiled plane whose back edge reads as the horizon, then the mat.
    val cx = proj.center.x
    val fx0 = cx - 260f
    val fx1 = cx + 260f
    val fz0 = -260f
    val fz1 = 260f
    drawPath(polygon(listOf(Vec3(fx0, 0f, fz0), Vec3(fx1, 0f, fz0), Vec3(fx1, 0f, fz1), Vec3(fx0, 0f, fz1))), floorFill)
    if (showGrid) {
        val gw = 0.7f * px
        var gx = (fx0 / 20f).toInt() * 20f
        while (gx <= fx1) { drawLine(grid, at(Vec3(gx, 0f, fz0)), at(Vec3(gx, 0f, fz1)), strokeWidth = gw); gx += 20f }
        var gz = fz0
        while (gz <= fz1) { drawLine(grid, at(Vec3(fx0, 0f, gz)), at(Vec3(fx1, 0f, gz)), strokeWidth = gw); gz += 20f }
    }
    val matPath = polygon(scene.matCorners())
    drawPath(matPath, mat)
    drawPath(matPath, matEdge, style = Stroke(width = 1.4f * px, join = StrokeJoin.Round))

    // Shadows straight below every part, flattened onto the floor (a light overhead).
    fun floorCapsule(a: Vec3, b: Vec3, r: Float): Path {
        var dx = b.x - a.x
        var dz = b.z - a.z
        val len = sqrt(dx * dx + dz * dz)
        if (len < 1e-3f) { dx = 1f; dz = 0f } else { dx /= len; dz /= len }
        val pts = ArrayList<Vec3>(18)
        fun arc(cx: Float, cz: Float, from: Float) {
            for (i in 0..8) {
                val t = (from + i * 22.5f) * PI_F / 180f
                pts += Vec3(cx + r * (dx * cos(t) - dz * sin(t)), 0f, cz + r * (dz * cos(t) + dx * sin(t)))
            }
        }
        arc(b.x, b.z, -90f)
        arc(a.x, a.z, 90f)
        return polygon(pts)
    }
    fun ground(v: Vec3) = Vec3(v.x, 0f, v.z)
    val outline = figure.torsoOutline()
    val torsoR = Body.width(Segment.Torso) / 2f
    drawPath(floorCapsule(ground(outline[0]), ground(outline[1]), torsoR), shadow)
    drawPath(floorCapsule(ground(outline[3]), ground(outline[2]), torsoR), shadow)
    drawPath(polygon(outline.take(4).map(::ground)), shadow)
    Segment.entries.filter { it != Segment.Torso }.forEach { seg ->
        drawPath(floorCapsule(ground(figure.root(seg)), ground(figure.end(seg)), Body.width(seg) / 2f), shadow)
    }
    val headGround = ground(figure[Joint.Head])
    drawPath(floorCapsule(headGround, headGround, Body.HEAD_RADIUS), shadow)

    // Props.
    val prop = stage.prop
    val hipDepth = proj.project(figure[Joint.Hip]).depth
    var wallInFront: (() -> Unit)? = null
    if (prop?.equipment == Equipment.FoamRoller) {
        val a = Vec3(prop.x, -ROLLER_RADIUS, -ROLLER_HALF_LENGTH)
        val b = Vec3(prop.x, -ROLLER_RADIUS, ROLLER_HALF_LENGTH)
        drawPath(floorCapsule(Vec3(prop.x, 0f, a.z), Vec3(prop.x, 0f, b.z), ROLLER_RADIUS), shadow)
        val pa = proj.project(a)
        val pb = proj.project(b)
        val w = ROLLER_RADIUS * (pa.scale + pb.scale) * px
        val oa = map(pa)
        val ob = map(pb)
        if ((oa - ob).getDistance() < 0.5f) drawCircle(c.prop, w / 2f, oa)
        else drawLine(c.prop, oa, ob, strokeWidth = w, cap = StrokeCap.Round)
    }
    if (prop?.equipment == Equipment.Wall) {
        val wall = polygon(
            listOf(
                Vec3(prop.x, 0f, -WALL_HALF_WIDTH), Vec3(prop.x, stage.wallTop, -WALL_HALF_WIDTH),
                Vec3(prop.x, stage.wallTop, WALL_HALF_WIDTH), Vec3(prop.x, 0f, WALL_HALF_WIDTH),
            ),
        )
        val draw = { alpha: Float ->
            drawPath(wall, c.prop.copy(alpha = alpha))
            drawPath(wall, c.prop.copy(alpha = alpha), style = Stroke(width = 5f * px, join = StrokeJoin.Round))
        }
        // A wall between the camera and the figure is drawn last and see-through.
        if (proj.project(Vec3(prop.x, stage.wallTop / 2f, 0f)).depth > hipDepth + 10f) wallInFront = { draw(0.35f) } else draw(1f)
    }

    // Body: whole limbs sorted far-to-near so nearer parts cover farther ones.
    fun width(seg: Segment, extra: Float): Float {
        val s = (proj.project(figure.root(seg)).scale + proj.project(figure.end(seg)).scale) / 2f
        return (Body.width(seg) + extra) * s * px
    }
    fun stroke(seg: Segment, color: Color, extra: Float = 0f) {
        if (seg == Segment.Torso) {
            val a = at(outline[0]); val b = at(outline[1]); val d = at(outline[2]); val e = at(outline[3])
            val qn = at(outline[4]); val qf = at(outline[5])
            val path = Path().apply {
                moveTo(a.x, a.y); quadraticTo(qn.x, qn.y, b.x, b.y); lineTo(d.x, d.y); quadraticTo(qf.x, qf.y, e.x, e.y); close()
            }
            drawPath(path, color, style = Fill)
            drawPath(path, color, style = Stroke(width = width(seg, extra), cap = StrokeCap.Round, join = StrokeJoin.Round))
        } else {
            drawLine(color, at(figure.root(seg)), at(figure.end(seg)), strokeWidth = width(seg, extra), cap = StrokeCap.Round)
        }
    }
    fun points(g: Group) = g.segments.flatMap { listOf(figure.root(it), figure.end(it)) }.map { proj.project(it) }
    fun depthOf(g: Group): Float = points(g).map { it.depth }.average().toFloat()
    fun levelOf(g: Group): Float = points(g).map { it.level }.average().toFloat()

    val torsoGroup = groups[2]
    val torsoLevel = levelOf(torsoGroup)
    groups.sortedBy { depthOf(it) }.forEach { g ->
        // Parts behind the torso (across the floor, ignoring height) fade toward the background,
        // like the old "far" limbs.
        val back = ((torsoLevel - levelOf(g)) / 14f).coerceIn(0f, 1f)
        val base = if (g === torsoGroup) c.body else lerp(c.body, c.far, back)
        val hl = lerp(c.highlight, lerp(c.highlight, c.halo, 0.35f), back)
        if (glow > 0f) g.segments.filter { it in highlight }.forEach { stroke(it, c.highlight.copy(alpha = 0.18f * glow), extra = 10f) }
        if (g !== torsoGroup && back < 0.3f) g.segments.forEach { stroke(it, c.halo, extra = 5f) }
        g.segments.forEach { stroke(it, if (it in highlight) hl else base) }
        if (g.withHead) {
            val h = proj.project(figure[Joint.Head])
            drawCircle(c.body, radius = Body.HEAD_RADIUS * h.scale * px, center = map(h))
        }
    }
    wallInFront?.invoke()
}
