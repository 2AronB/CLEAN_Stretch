package dev.clean.stretch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.data.BodySide
import dev.clean.stretch.data.SideMinutes
import dev.clean.stretch.plan.Profile
import kotlin.math.abs

/*
 * Body view for weekly goals (D-039; anatomy chart, owner 2026-11-14). A simple anatomy-chart
 * figure seen from the front, right side, back or left side: swipe or tap to turn it. Every
 * muscle is drawn as its own shape and fills from the bottom as that side reaches its weekly
 * goal. Colour shows the goal: primary, secondary or none.
 *
 * Shapes are hand-drawn outlines in a 100 × 220 box, smoothed into curves. Front and back list
 * the muscles on the viewer's left; the other side is mirrored. On the front the viewer's left
 * is the person's right; on the back it's the person's left. Side views show one side.
 */
private enum class BodyView(val label: String) { Front("Front"), Right("Right"), Back("Back"), Left("Left") }

private class Shape(val muscle: Muscle, val points: List<Offset>)

private fun pts(vararg xy: Float): List<Offset> = xy.toList().chunked(2).map { Offset(it[0], it[1]) }

private val frontOutline = pts(
    44f, 25f, 44f, 31f, 36f, 34f, 25f, 37f, 19f, 42f, 15f, 52f, 13f, 66f, 12f, 80f, 10f, 98f, 9f, 113f,
    7.5f, 121f, 10f, 128f, 15f, 124f, 16f, 113f, 18f, 98f, 20f, 82f, 23f, 68f, 28f, 58f, 30f, 72f, 32f, 88f,
    29f, 102f, 28f, 122f, 30f, 158f, 31f, 176f, 34f, 202f, 29f, 210f, 31f, 214f, 46f, 214f, 46f, 204f,
    46.5f, 176f, 45.5f, 160f, 47f, 126f, 50f, 117f,
)
private val sideOutline = pts(
    48f, 24f, 56f, 24f, 57f, 34f, 62f, 40f, 65f, 49f, 63f, 63f, 62f, 80f, 61f, 96f, 61f, 110f, 60f, 132f,
    58f, 158f, 57f, 168f, 56f, 200f, 68f, 205f, 69f, 212f, 45f, 213f, 46f, 200f, 46f, 178f, 44f, 165f,
    46f, 157f, 41f, 132f, 37f, 113f, 35f, 101f, 38f, 88f, 38f, 70f, 40f, 50f, 45f, 36f,
)
private val sideArm = pts(45f, 38f, 52f, 36f, 57f, 40f, 56f, 78f, 55f, 110f, 54f, 120f, 49f, 122f, 47f, 110f, 46f, 78f, 44f, 50f)

private val frontShapes = listOf(
    Shape(Muscle.Neck, pts(44.5f, 27f, 48.5f, 27f, 48f, 35f, 45f, 34f)),
    Shape(Muscle.Shoulders, pts(24f, 38f, 31f, 36f, 33f, 42f, 28f, 52f, 21f, 52f, 19f, 45f)),
    Shape(Muscle.Chest, pts(33.5f, 40f, 49f, 40f, 49f, 54f, 40f, 57f, 33f, 52f, 31.5f, 46f)),
    Shape(Muscle.Biceps, pts(17f, 53f, 24.5f, 54f, 24.5f, 66f, 22f, 75f, 16.5f, 74f, 15.5f, 62f)),
    Shape(Muscle.Forearms, pts(12.5f, 83f, 18.5f, 83f, 17.5f, 97f, 15f, 110f, 11f, 110f, 10.5f, 97f)),
    Shape(Muscle.Obliques, pts(31.5f, 59f, 39.5f, 59f, 40.5f, 80f, 41f, 95f, 35.5f, 95f, 32.5f, 84f, 31f, 70f)),
    Shape(Muscle.Abdominals, pts(41.5f, 58f, 49f, 58f, 49f, 96f, 43f, 96f, 42f, 80f)),
    Shape(Muscle.HipFlexors, pts(34f, 99f, 45.5f, 101f, 45.5f, 110f, 38f, 114f, 31.5f, 107f)),
    Shape(Muscle.Quadriceps, pts(29.5f, 114f, 38f, 116f, 43.5f, 123f, 42f, 155f, 36f, 159f, 30.5f, 150f, 28.5f, 129f)),
    Shape(Muscle.Adductors, pts(44.5f, 115f, 49f, 118f, 48f, 136f, 45f, 145f)),
    Shape(Muscle.Shins, pts(35f, 167f, 41f, 167f, 41f, 199f, 37.5f, 201f, 34.5f, 186f)),
)
private val backShapes = listOf(
    Shape(Muscle.UpperBack, pts(34f, 37f, 49f, 31f, 49f, 56f, 42f, 56f, 36f, 48f)),
    Shape(Muscle.Triceps, pts(16f, 48f, 23.5f, 47f, 25.5f, 60f, 23.5f, 74f, 17f, 74f, 14.5f, 62f)),
    Shape(Muscle.Forearms, pts(12.5f, 83f, 18.5f, 83f, 17.5f, 97f, 15f, 110f, 11f, 110f, 10.5f, 97f)),
    Shape(Muscle.Lats, pts(29.5f, 57f, 41f, 57f, 43f, 76f, 37.5f, 88f, 32.5f, 82f, 30.5f, 68f)),
    Shape(Muscle.LowerBack, pts(44f, 72f, 49f, 72f, 49f, 95f, 43f, 95f)),
    Shape(Muscle.OuterHip, pts(28.5f, 101f, 33f, 99f, 33.5f, 114f, 29.5f, 118f)),
    Shape(Muscle.Glutes, pts(34f, 98f, 49f, 100f, 49f, 116f, 40f, 120f, 33.5f, 113f)),
    Shape(Muscle.DeepHip, pts(37.5f, 104f, 46.5f, 105f, 46f, 110f, 38f, 110f)),
    Shape(Muscle.Hamstrings, pts(29.5f, 121f, 46f, 121f, 45f, 155f, 39f, 160f, 31f, 154f)),
    Shape(Muscle.Calves, pts(31.5f, 165f, 45f, 165f, 44f, 186f, 39f, 194f, 33f, 186f)),
)
private val sideShapes = listOf(
    Shape(Muscle.Neck, pts(49.5f, 27f, 55f, 27f, 55f, 34f, 49.5f, 34f)),
    Shape(Muscle.Lats, pts(39.5f, 46f, 44.5f, 48f, 44.5f, 72f, 39f, 74f)),
    Shape(Muscle.Obliques, pts(57f, 58f, 62f, 60f, 61f, 90f, 57f, 92f)),
    Shape(Muscle.Glutes, pts(36.5f, 92f, 46f, 92f, 46f, 110f, 37.5f, 110f)),
    Shape(Muscle.OuterHip, pts(47f, 94f, 59f, 95f, 59f, 108f, 47f, 108f)),
    Shape(Muscle.Quadriceps, pts(50f, 113f, 60f, 111f, 58f, 154f, 51f, 156f)),
    Shape(Muscle.Hamstrings, pts(39.5f, 115f, 49f, 115f, 48f, 154f, 42.5f, 156f)),
    Shape(Muscle.Calves, pts(43.5f, 165f, 50f, 165f, 49f, 192f, 45f, 194f)),
    Shape(Muscle.Shins, pts(51.5f, 167f, 56.5f, 167f, 55f, 199f, 52f, 199f)),
    // The arm hangs in front of the torso, so its muscles are drawn last.
    Shape(Muscle.Shoulders, pts(44.5f, 38f, 57f, 39f, 56f, 52f, 46f, 52f)),
    Shape(Muscle.Triceps, pts(45.5f, 54f, 50f, 54f, 50f, 76f, 46.5f, 76f)),
    Shape(Muscle.Biceps, pts(51f, 54f, 56f, 54f, 55.5f, 76f, 51f, 76f)),
    Shape(Muscle.Forearms, pts(47.5f, 81f, 55f, 81f, 54.5f, 108f, 48.5f, 108f)),
)

private const val W = 100f
private const val H = 220f

private fun List<Offset>.mirrored() = map { Offset(W - it.x, it.y) }

/** A closed, smooth curve through [p] (Catmull-Rom turned into cubic Béziers). */
private fun smooth(p: List<Offset>, map: (Offset) -> Offset): Path {
    val q = p.map(map)
    val path = Path()
    path.moveTo(q[0].x, q[0].y)
    for (i in q.indices) {
        val p0 = q[(i - 1 + q.size) % q.size]
        val p1 = q[i]
        val p2 = q[(i + 1) % q.size]
        val p3 = q[(i + 2) % q.size]
        val c1 = p1 + (p2 - p0) / 6f
        val c2 = p2 - (p3 - p1) / 6f
        path.cubicTo(c1.x, c1.y, c2.x, c2.y, p2.x, p2.y)
    }
    path.close()
    return path
}

private fun inside(p: Offset, poly: List<Offset>): Boolean {
    var c = false
    var j = poly.lastIndex
    for (i in poly.indices) {
        val a = poly[i]; val b = poly[j]
        if ((a.y > p.y) != (b.y > p.y) && p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x) c = !c
        j = i
    }
    return c
}

/** Each drawn piece: which muscle, which side of the person, and its outline in figure units. */
private fun pieces(view: BodyView): List<Triple<Muscle, BodySide, List<Offset>>> = when (view) {
    BodyView.Front -> frontShapes.flatMap { listOf(Triple(it.muscle, BodySide.Right, it.points), Triple(it.muscle, BodySide.Left, it.points.mirrored())) }
    BodyView.Back -> backShapes.flatMap { listOf(Triple(it.muscle, BodySide.Left, it.points), Triple(it.muscle, BodySide.Right, it.points.mirrored())) }
    BodyView.Right -> sideShapes.map { Triple(it.muscle, BodySide.Right, it.points) }
    BodyView.Left -> sideShapes.map { Triple(it.muscle, BodySide.Left, it.points.mirrored()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMap(minutes: Map<Muscle, SideMinutes>, profile: Profile, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val skin = scheme.surfaceContainerHighest
    val line = scheme.onSurface.copy(alpha = 0.18f)
    val noMoves = scheme.onSurface.copy(alpha = 0.07f)
    val primaryColor = scheme.primary
    val secondaryColor = scheme.tertiary
    val noGoalColor = scheme.outline
    var viewIndex by rememberSaveable { mutableIntStateOf(0) }
    val view = BodyView.entries[viewIndex]
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = Muscle.entries.firstOrNull { it.name == selectedName }
    val hasMoves = SampleCatalog.musclesWithMoves.toSet()

    fun goalColor(m: Muscle): Color = when {
        profile.weeklyTarget(m) == null -> noGoalColor
        m.region in profile.effectiveFocus -> primaryColor
        else -> secondaryColor
    }
    fun fraction(m: Muscle, side: BodySide): Float {
        val goal = profile.weeklyTarget(m) ?: 10.0
        return ((minutes[m]?.get(side) ?: 0.0) / goal).toFloat().coerceIn(0f, 1f)
    }

    val summary = "Body map of this week's stretching. " + Muscle.entries.filter { it in hasMoves }.joinToString("; ") { m ->
        val t = minutes[m] ?: SideMinutes()
        "${m.label}: left ${formatMin(t.left)}, right ${formatMin(t.right)} min"
    }

    Column(modifier) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            BodyView.entries.forEachIndexed { i, v ->
                SegmentedButton(
                    selected = i == viewIndex,
                    onClick = { viewIndex = i },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = BodyView.entries.size),
                    label = { Text(v.label) },
                    icon = {},
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        var drag by rememberSaveable { mutableStateOf(0f) }
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(380.dp)
                .semantics { contentDescription = summary }
                // Swipe to turn the figure.
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { drag = 0f },
                        onDragEnd = {
                            if (abs(drag) > 60f) {
                                val step = if (drag < 0) 1 else -1
                                viewIndex = (viewIndex + step + BodyView.entries.size) % BodyView.entries.size
                            }
                        },
                    ) { _, dx -> drag += dx }
                }
                .pointerInput(view, profile) {
                    detectTapGestures { tap ->
                        val s = minOf(size.width / W, size.height / H)
                        val ox = (size.width - W * s) / 2f
                        val p = Offset((tap.x - ox) / s, tap.y / s)
                        // Last drawn wins (it's on top); if nothing is hit, pick the nearest shape.
                        val all = pieces(view)
                        val hit = all.lastOrNull { inside(p, it.third) }
                            ?: all.minByOrNull { (_, _, poly) ->
                                val cx = poly.map { it.x }.average().toFloat(); val cy = poly.map { it.y }.average().toFloat()
                                (cx - p.x) * (cx - p.x) + (cy - p.y) * (cy - p.y)
                            }
                        selectedName = hit?.first?.name
                    }
                },
        ) {
            val s = minOf(size.width / W, size.height / H)
            val ox = (size.width - W * s) / 2f
            val map = { o: Offset -> Offset(ox + o.x * s, o.y * s) }
            val mirrorSide = view == BodyView.Left
            // Body: outline (front/back are symmetric; side views face the way they show).
            val outline = when (view) {
                BodyView.Front, BodyView.Back -> frontOutline + frontOutline.mirrored().reversed()
                BodyView.Right -> sideOutline
                BodyView.Left -> sideOutline.mirrored()
            }
            drawPath(smooth(outline, map), skin)
            val headX = if (view == BodyView.Right) 52f else if (view == BodyView.Left) 48f else 50f
            drawCircle(skin, 11f * s, map(Offset(headX, 15f)))
            if (view == BodyView.Right || view == BodyView.Left) {
                val arm = if (mirrorSide) sideArm.mirrored() else sideArm
                drawPath(smooth(arm, map), skin)
                drawPath(smooth(arm, map), line, style = Stroke(1.dp.toPx()))
            }
            pieces(view).forEach { (m, side, poly) ->
                val path = smooth(poly, map)
                val moves = m in hasMoves
                val color = goalColor(m)
                drawPath(path, if (moves) color.copy(alpha = 0.22f) else noMoves)
                if (moves) {
                    val f = fraction(m, side)
                    val shown = if (f > 0f) maxOf(f, 0.08f) else 0f
                    if (shown > 0f) {
                        val top = poly.minOf { it.y }; val bottom = poly.maxOf { it.y }
                        val cut = map(Offset(0f, bottom - (bottom - top) * shown)).y
                        clipPath(path) { drawRect(color, topLeft = Offset(0f, cut), size = androidx.compose.ui.geometry.Size(size.width, size.height - cut)) }
                    }
                }
                drawPath(path, if (m == selected) scheme.onSurface else line, style = Stroke((if (m == selected) 2.5f else 1f).dp.toPx()))
            }
        }
        Text(
            if (view == BodyView.Front) "Front · your right is on the left" else if (view == BodyView.Back) "Back · your left is on the left" else "${view.label} side",
            style = MaterialTheme.typography.labelSmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Legend(primaryColor, "Primary")
            Legend(secondaryColor, "Secondary")
            Legend(noGoalColor, "No goal")
        }
        Spacer(Modifier.height(12.dp))
        Surface(color = scheme.surfaceContainer, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                if (selected == null) {
                    Text("Tap a muscle. Swipe to turn.", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
                } else {
                    val goal = profile.weeklyTarget(selected)
                    val t = minutes[selected] ?: SideMinutes()
                    Text(selected.label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        when {
                            selected !in hasMoves -> "No moves yet."
                            goal == null -> "No goal · left ${formatMin(t.left)} · right ${formatMin(t.right)} min"
                            else -> "Left ${formatMin(t.left)}/${formatMin(goal)} · right ${formatMin(t.right)}/${formatMin(goal)} min" +
                                if (selected.region in profile.effectiveFocus) " · primary" else " · secondary"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun Legend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 6.dp))
    }
}

private fun formatMin(m: Double): String = if (m < 10 && m % 1.0 != 0.0) "%.1f".format(m) else m.toInt().toString()
