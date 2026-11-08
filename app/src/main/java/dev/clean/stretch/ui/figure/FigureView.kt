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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.clean.stretch.catalog.Prop
import dev.clean.stretch.figure.Body
import dev.clean.stretch.figure.Box
import dev.clean.stretch.figure.Figure
import dev.clean.stretch.figure.Joint
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.Vec
import kotlin.math.max
import kotlin.math.min

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
 * Draws the figure in a side view.
 * [frame] should cover every pose the figure will take so the drawing doesn't jump
 * (see MotionPlayer.frameFor). [mirrored] shows the other side of the body.
 * [glow] (0..1) gently pulses the highlighted muscles, e.g. with the breath.
 */
@Composable
fun FigureView(
    pose: Pose,
    frame: Box,
    highlight: Set<Segment>,
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    prop: Prop? = null,
    glow: Float = 0f,
    colors: FigureColors = figureColors(),
    description: String = "Animated figure",
) {
    Canvas(modifier.semantics { contentDescription = description }) {
        drawFigure(Figure.solve(pose), frame, highlight, mirrored, prop, glow, colors)
    }
}

private fun DrawScope.drawFigure(
    figure: Figure,
    frame: Box,
    highlight: Set<Segment>,
    mirrored: Boolean,
    prop: Prop?,
    glow: Float,
    c: FigureColors,
) {
    // Fit the frame (plus room for a prop and a little air) into the canvas.
    val pad = 10f
    val left = min(frame.left, prop?.x?.minus(6f) ?: frame.left) - pad
    val right = max(frame.right, prop?.x?.plus(6f) ?: frame.right) + pad
    val top = frame.top - pad
    val bottom = 6f
    val scale = min(size.width / (right - left), size.height / (bottom - top))
    val usedW = (right - left) * scale
    val usedH = (bottom - top) * scale
    val ox = (size.width - usedW) / 2f
    val oy = (size.height - usedH) / 2f

    fun map(v: Vec): Offset {
        val x = ox + (v.x - left) * scale
        val y = oy + (v.y - top) * scale
        return Offset(if (mirrored) size.width - x else x, y)
    }

    val floorY = map(Vec(0f, 0f)).y
    drawLine(c.floor, Offset(size.width * 0.06f, floorY), Offset(size.width * 0.94f, floorY), strokeWidth = 2.5f * scale / 2f, cap = StrokeCap.Round)

    prop?.let {
        val p = map(Vec(it.x, 0f))
        val w = 5f * scale
        drawRoundRect(
            color = c.prop,
            topLeft = Offset(p.x - w / 2f, map(Vec(0f, frame.top - 4f)).y),
            size = Size(w, floorY - map(Vec(0f, frame.top - 4f)).y),
            cornerRadius = CornerRadius(w / 2f),
        )
    }

    // Soft contact shadow under the figure.
    val b = figure.bounds()
    val sLeft = map(Vec(b.left + 6f, 0f)).x
    val sRight = map(Vec(b.right - 6f, 0f)).x
    val shadowLeft = min(sLeft, sRight)
    val shadowWidth = kotlin.math.abs(sRight - sLeft)
    drawOval(c.floor.copy(alpha = 0.45f), topLeft = Offset(shadowLeft, floorY - 3f * scale), size = Size(shadowWidth, 6f * scale))

    fun stroke(seg: Segment, color: Color, extra: Float = 0f) {
        val w = (Body.width(seg) + extra) * scale
        if (seg == Segment.Torso) {
            val a = map(figure[seg.from]); val ctrl = map(figure.spineControl); val e = map(figure[seg.to])
            val path = Path().apply { moveTo(a.x, a.y); quadraticTo(ctrl.x, ctrl.y, e.x, e.y) }
            drawPath(path, color, style = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        } else {
            drawLine(color, map(figure[seg.from]), map(figure[seg.to]), strokeWidth = w, cap = StrokeCap.Round)
        }
    }

    fun colorFor(seg: Segment, base: Color): Color =
        if (seg in highlight) (if (seg.near == false) lerp(c.highlight, c.halo, 0.35f) else c.highlight) else base

    fun glowFor(segments: List<Segment>) {
        if (glow <= 0f) return
        segments.filter { it in highlight }.forEach { stroke(it, c.highlight.copy(alpha = 0.18f * glow), extra = 10f) }
    }

    fun group(segments: List<Segment>, base: Color, halo: Boolean) {
        glowFor(segments)
        if (halo) segments.forEach { stroke(it, c.halo, extra = 5f) }
        segments.forEach { stroke(it, colorFor(it, base)) }
    }

    group(listOf(Segment.FarUpperArm, Segment.FarForearm), c.far, halo = false)
    group(listOf(Segment.FarThigh, Segment.FarShin, Segment.FarFoot), c.far, halo = false)
    group(listOf(Segment.Torso, Segment.Neck), c.body, halo = false)
    drawCircle(c.body, radius = Body.HEAD_RADIUS * scale, center = map(figure[Joint.Head]))
    group(listOf(Segment.NearThigh, Segment.NearShin, Segment.NearFoot), c.body, halo = true)
    group(listOf(Segment.NearUpperArm, Segment.NearForearm), c.body, halo = true)
}
