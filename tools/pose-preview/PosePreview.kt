// Renders every catalog stretch's keyframes to PNG using the app's own figure math.
// Lets anyone (or an agent without an emulator) check poses quickly. See tools/pose-preview/README.md.
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.figure.Body
import dev.clean.stretch.figure.Figure
import dev.clean.stretch.figure.Joint
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.Segment
import java.awt.BasicStroke
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.QuadCurve2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val outDir = File(args.getOrElse(0) { "tools/pose-preview/out" }).apply { mkdirs() }
    val cell = 260
    for (s in SampleCatalog.all) {
        val poses = MotionPlayer.keyframes(s.motion)
        val img = BufferedImage(cell * poses.size, cell, BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.color = Color(0xF4, 0xF7, 0xF5); g.fillRect(0, 0, img.width, img.height)
        val frame = MotionPlayer.frameFor(s.motion)
        val propRight = s.prop?.let { maxOf(frame.right, it.x + 4f) } ?: frame.right
        val w = propRight - frame.left; val h = -frame.top
        val scale = (cell - 40) / maxOf(w, h)
        poses.forEachIndexed { i, pose ->
            val ox = i * cell + (cell - w * scale) / 2f - frame.left * scale
            val oy = cell - 24f
            fun X(x: Float) = (ox + x * scale).toDouble()
            fun Y(y: Float) = (oy + y * scale).toDouble()
            g.color = Color(0xCF, 0xD8, 0xD4); g.stroke = BasicStroke(2f)
            g.draw(Line2D.Double((i * cell + 8).toDouble(), Y(0f), ((i + 1) * cell - 8).toDouble(), Y(0f)))
            s.prop?.let { g.stroke = BasicStroke(6f); g.draw(Line2D.Double(X(it.x), Y(0f), X(it.x), Y(frame.top))) }
            val f = Figure.solve(pose)
            fun seg(sg: Segment, c: Color, halo: Boolean = false) {
                if (halo) { g.color = Color(0xF4, 0xF7, 0xF5); g.stroke = BasicStroke((Body.width(sg) + 5f) * scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                    val a0 = f[sg.from]; val b0 = f[sg.to]; g.draw(Line2D.Double(X(a0.x), Y(a0.y), X(b0.x), Y(b0.y))) }
                g.color = c; g.stroke = BasicStroke(Body.width(sg) * scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                val a = f[sg.from]; val b = f[sg.to]
                if (sg == Segment.Torso) g.draw(QuadCurve2D.Double(X(a.x), Y(a.y), X(f.spineControl.x), Y(f.spineControl.y), X(b.x), Y(b.y)))
                else g.draw(Line2D.Double(X(a.x), Y(a.y), X(b.x), Y(b.y)))
            }
            val far = Color(0x8F, 0xA3, 0x9C); val body = Color(0x3E, 0x5C, 0x55); val hi = Color(0x2E, 0xA0, 0x84)
            fun col(sg: Segment, base: Color) = if (sg in s.highlight) hi else base
            listOf(Segment.FarUpperArm, Segment.FarForearm, Segment.FarThigh, Segment.FarShin, Segment.FarFoot).forEach { seg(it, if (it in s.highlight) hi.darker() else far) }
            listOf(Segment.Torso, Segment.Neck).forEach { seg(it, col(it, body)) }
            val hd = f[Joint.Head]; val r = Body.HEAD_RADIUS * scale
            g.color = body; g.fill(Ellipse2D.Double(X(hd.x) - r, Y(hd.y) - r, 2.0 * r, 2.0 * r))
            fun group(parts: List<Segment>) {
                parts.forEach { sg -> g.color = Color(0xF4, 0xF7, 0xF5); g.stroke = BasicStroke((Body.width(sg) + 5f) * scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                    val a0 = f[sg.from]; val b0 = f[sg.to]; g.draw(Line2D.Double(X(a0.x), Y(a0.y), X(b0.x), Y(b0.y))) }
                parts.forEach { seg(it, col(it, body)) }
            }
            group(listOf(Segment.NearThigh, Segment.NearShin, Segment.NearFoot))
            group(listOf(Segment.NearUpperArm, Segment.NearForearm))
            g.color = Color.DARK_GRAY; g.drawString(listOf("start", "stretch", "deep")[i], i * cell + 8, 16)
        }
        g.drawString(s.name, 8, 32)
        ImageIO.write(img, "png", File(outDir, "${s.key}.png"))
    }
    println("Wrote ${SampleCatalog.all.size} previews to ${outDir.path}")
}
