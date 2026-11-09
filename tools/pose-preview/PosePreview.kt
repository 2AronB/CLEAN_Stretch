// Renders every catalog stretch's keyframes to PNG with the app's own figure code, from every
// camera view. The shapes come from FigureDrawing, the same code the app paints, so a preview
// matches the app (only the colors are fixed light-theme stand-ins). See README.md.
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.toStageProp
import dev.clean.stretch.figure.FigureDrawing
import dev.clean.stretch.figure.Ink
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.figure.Shape
import dev.clean.stretch.figure.Stage
import dev.clean.stretch.figure.View
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val paper = Color(0xF1, 0xF4, 0xF2)
private val inks = mapOf(
    Ink.Floor to Color(0xE6, 0xEA, 0xE8),
    Ink.Grid to Color(0xD3, 0xD9, 0xD6),
    Ink.Mat to Color(0xD6, 0xDC, 0xD9),
    Ink.MatEdge to Color(0xB9, 0xC2, 0xBE),
    Ink.Shadow to Color(0xA9, 0xB3, 0xAF),
    Ink.Prop to Color(0xB4, 0xBE, 0xBA),
    Ink.Body to Color(0x3E, 0x5C, 0x55),
    Ink.Highlight to Color(0x8A, 0x4F, 0x7D),
    Ink.Outline to Color(0x12, 0x18, 0x16),
)

private fun mix(a: Color, b: Color, t: Float, alpha: Float): Color {
    fun ch(x: Int, y: Int) = (x + (y - x) * t).toInt().coerceIn(0, 255)
    return Color(ch(a.red, b.red), ch(a.green, b.green), ch(a.blue, b.blue), (alpha * 255).toInt().coerceIn(0, 255))
}

private fun Graphics2D.paint(s: Shape) {
    color = mix(inks.getValue(s.ink), paper, s.fade, s.alpha)
    when (s) {
        is Shape.Poly -> {
            val p = Path2D.Float().apply { s.points.forEachIndexed { i, v -> if (i == 0) moveTo(v.x, v.y) else lineTo(v.x, v.y) }; closePath() }
            if (s.fill) fill(p)
            if (s.stroke > 0f) { stroke = BasicStroke(s.stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); draw(p) }
        }
        is Shape.Line -> {
            stroke = BasicStroke(s.width, if (s.round) BasicStroke.CAP_ROUND else BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
            draw(Line2D.Float(s.a.x, s.a.y, s.b.x, s.b.y))
        }
        is Shape.Dot -> fill(Ellipse2D.Float(s.center.x - s.radius, s.center.y - s.radius, 2 * s.radius, 2 * s.radius))
        is Shape.Torso -> {
            val p = Path2D.Float().apply {
                moveTo(s.hipNear.x, s.hipNear.y)
                quadTo(s.curveNear.x, s.curveNear.y, s.shoulderNear.x, s.shoulderNear.y)
                lineTo(s.shoulderFar.x, s.shoulderFar.y)
                quadTo(s.curveFar.x, s.curveFar.y, s.hipFar.x, s.hipFar.y)
                closePath()
            }
            fill(p)
            stroke = BasicStroke(s.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); draw(p)
        }
    }
}

/**
 * Args: [outDir] [key ...]. With keys, only those stretches are drawn.
 * Each PNG: one row per view (the stretch's starting view first, marked *), one column per keyframe.
 */
fun main(args: Array<String>) {
    val outDir = File(args.getOrElse(0) { "tools/pose-preview/out" }).apply { mkdirs() }
    val only = args.drop(1).toSet()
    val stretches = SampleCatalog.all.filter { only.isEmpty() || it.key in only }
    val cell = 240
    val names = listOf("start", "stretch", "deep")
    for (s in stretches) {
        val poses = MotionPlayer.keyframes(s.motion)
        val views = listOf(s.motion.view) + View.entries.filter { it != s.motion.view }
        val img = BufferedImage(cell * poses.size, cell * views.size + 28, BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g.color = paper; g.fillRect(0, 0, img.width, img.height)
        g.color = Color.DARK_GRAY; g.font = Font(Font.SANS_SERIF, Font.BOLD, 14)
        g.drawString("${s.name}  (${s.key})", 8, 19)
        views.forEachIndexed { row, view ->
            val stage = Stage.of(poses, view.camera, s.prop?.toStageProp())
            poses.forEachIndexed { col, pose ->
                val sub = g.create(col * cell, 28 + row * cell, cell, cell) as Graphics2D
                FigureDrawing.shapes(pose, stage, s.highlight, cell.toFloat(), cell.toFloat()).forEach { sub.paint(it) }
                sub.color = Color.DARK_GRAY; sub.font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
                sub.drawString("${view.label}${if (row == 0) " *" else ""} · ${names[col]}", 6, 14)
                sub.dispose()
            }
        }
        g.dispose()
        ImageIO.write(img, "png", File(outDir, "${s.key}.png"))
    }
    println("Wrote ${stretches.size} previews to ${outDir.path}")
}
