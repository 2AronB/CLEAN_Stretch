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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.clean.stretch.catalog.Prop
import dev.clean.stretch.catalog.toStageProp
import dev.clean.stretch.figure.Camera
import dev.clean.stretch.figure.FigureDrawing
import dev.clean.stretch.figure.Ink
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import dev.clean.stretch.figure.Shape
import dev.clean.stretch.figure.Stage
import dev.clean.stretch.figure.Vec

/** Colors for one drawing of the figure. Defaults come from the current Material theme. */
data class FigureColors(
    val body: Color,
    val highlight: Color,
    val background: Color,
    val floor: Color,
    val prop: Color,
    val outline: Color,
)

@Composable
fun figureColors(background: Color = MaterialTheme.colorScheme.surface): FigureColors {
    val scheme = MaterialTheme.colorScheme
    return FigureColors(
        body = lerp(scheme.onSurface, scheme.primary, 0.35f),
        highlight = scheme.tertiary,
        background = background,
        floor = lerp(scheme.outlineVariant, background, 0.2f),
        prop = lerp(scheme.outlineVariant, background, 0.35f),
        // Near-black ink (owner request): the same outline around every body part.
        outline = lerp(Color.Black, scheme.primary, 0.1f),
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

/** A stage that keeps every pose in [poses] (and the prop) in view from [camera]. */
fun figureStage(poses: List<Pose>, camera: Camera, prop: Prop?): Stage = Stage.of(poses, camera, prop?.toStageProp())

@Composable
fun rememberFigureStage(key: Any, poses: List<Pose>, camera: Camera, prop: Prop?): Stage =
    remember(key, camera, prop) { figureStage(poses, camera, prop) }

/**
 * Draws the figure in 3D on a floor with an exercise mat, seen from the stage's camera
 * (shapes come from [FigureDrawing], shared with tools/pose-preview).
 * [mirrored] shows the other side of the body. [glow] (0..1) gently pulses the highlighted
 * muscles, e.g. with the breath. [showGrid] adds floor tiles (skip it for tiny thumbnails).
 */
@Composable
fun FigureView(
    pose: Pose,
    stage: Stage,
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
            FigureDrawing.shapes(pose, stage, highlight, size.width, size.height, mirrored, glow, showGrid)
                .forEach { paint(it, colors) }
        }
    }
}

private fun FigureColors.of(ink: Ink): Color = when (ink) {
    Ink.Floor -> lerp(background, floor, 0.14f)
    Ink.Grid -> lerp(background, floor, 0.38f)
    Ink.Mat -> lerp(background, floor, 0.34f)
    Ink.MatEdge -> lerp(background, floor, 0.62f)
    Ink.Shadow -> lerp(background, floor, 0.78f)
    Ink.Prop -> prop
    Ink.Body -> body
    Ink.Highlight -> highlight
    Ink.Outline -> outline
}

private fun Vec.o() = Offset(x, y)

private fun DrawScope.paint(s: Shape, c: FigureColors) {
    val base = c.of(s.ink)
    val color = lerp(base, c.background, s.fade).copy(alpha = base.alpha * s.alpha)
    when (s) {
        is Shape.Poly -> {
            val path = Path().apply {
                s.points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
                close()
            }
            if (s.fill) drawPath(path, color)
            if (s.stroke > 0f) drawPath(path, color, style = Stroke(width = s.stroke, join = StrokeJoin.Round))
        }
        is Shape.Line -> {
            if (s.round && (s.a - s.b).let { it.x * it.x + it.y * it.y } < 0.25f) drawCircle(color, s.width / 2f, s.a.o())
            else drawLine(color, s.a.o(), s.b.o(), strokeWidth = s.width, cap = if (s.round) StrokeCap.Round else StrokeCap.Butt)
        }
        is Shape.Dot -> drawCircle(color, s.radius, s.center.o())
        is Shape.Torso -> {
            val path = Path().apply {
                moveTo(s.hipNear.x, s.hipNear.y)
                quadraticTo(s.curveNear.x, s.curveNear.y, s.shoulderNear.x, s.shoulderNear.y)
                lineTo(s.shoulderFar.x, s.shoulderFar.y)
                quadraticTo(s.curveFar.x, s.curveFar.y, s.hipFar.x, s.hipFar.y)
                close()
            }
            drawPath(path, color)
            drawPath(path, color, style = Stroke(width = s.width, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}
