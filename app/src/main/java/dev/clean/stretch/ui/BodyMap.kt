package dev.clean.stretch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.data.BodySide
import dev.clean.stretch.data.SideMinutes
import dev.clean.stretch.plan.Profile

/*
 * Body view for weekly goals (owner request, 2026-11-13; D-039). A front and a back figure; every
 * muscle has a bar on each side of the body that fills from the bottom as that side reaches its
 * weekly goal. Colour shows the goal: primary, secondary or none.
 *
 * Coordinates are in a 100 × 220 box per figure. Each rect is the muscle on the viewer's left;
 * the other side is mirrored. On the front figure the viewer's left is the person's right side.
 */
private data class Zone(val muscle: Muscle, val back: Boolean, val rect: Rect)

private fun r(x: Float, y: Float, w: Float, h: Float) = Rect(x, y, x + w, y + h)

private val zones = listOf(
    // Front
    Zone(Muscle.Neck, false, r(44.5f, 27f, 5f, 9f)),
    Zone(Muscle.Shoulders, false, r(24f, 37f, 9f, 12f)),
    Zone(Muscle.Chest, false, r(34.5f, 39f, 14.5f, 14f)),
    Zone(Muscle.Biceps, false, r(15f, 48f, 10f, 22f)),
    Zone(Muscle.Forearms, false, r(11f, 84f, 10f, 26f)),
    Zone(Muscle.Obliques, false, r(31.5f, 60f, 8f, 26f)),
    Zone(Muscle.Abdominals, false, r(41f, 57f, 8.5f, 30f)),
    Zone(Muscle.HipFlexors, false, r(34f, 98f, 13f, 13f)),
    Zone(Muscle.Quadriceps, false, r(30.5f, 116f, 11f, 44f)),
    Zone(Muscle.Adductors, false, r(43f, 116f, 5.5f, 30f)),
    Zone(Muscle.Shins, false, r(34f, 170f, 9f, 34f)),
    // Back
    Zone(Muscle.UpperBack, true, r(34.5f, 37f, 14.5f, 16f)),
    Zone(Muscle.Triceps, true, r(15f, 46f, 10f, 24f)),
    Zone(Muscle.Lats, true, r(30.5f, 55f, 11f, 22f)),
    Zone(Muscle.LowerBack, true, r(42f, 76f, 7f, 18f)),
    Zone(Muscle.OuterHip, true, r(27.5f, 98f, 5f, 16f)),
    Zone(Muscle.Glutes, true, r(33.5f, 96f, 15.5f, 12f)),
    Zone(Muscle.DeepHip, true, r(36f, 109f, 13f, 6f)),
    Zone(Muscle.Hamstrings, true, r(31f, 117f, 15f, 44f)),
    Zone(Muscle.Calves, true, r(33f, 168f, 12f, 32f)),
)

private const val FIG_W = 100f
private const val FIG_H = 220f
private const val GAP = 12f

/** Which side of the person a zone drawn on the viewer's left (or right, [mirrored]) shows. */
private fun sideOf(back: Boolean, mirrored: Boolean): BodySide {
    val viewerLeftIsPersonLeft = back
    return if (viewerLeftIsPersonLeft != mirrored) BodySide.Left else BodySide.Right
}

private fun Rect.mirror(): Rect = Rect(FIG_W - right, top, FIG_W - left, bottom)

@Composable
fun BodyMap(minutes: Map<Muscle, SideMinutes>, profile: Profile, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val silhouette = scheme.surfaceContainerHighest
    val track = scheme.onSurface.copy(alpha = 0.06f)
    val primaryColor = scheme.primary
    val secondaryColor = scheme.tertiary
    val noGoalColor = scheme.outline
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = Muscle.entries.firstOrNull { it.name == selectedName }
    val hasMoves = SampleCatalog.musclesWithMoves.toSet()

    fun goalColor(m: Muscle): Color {
        val goal = profile.weeklyTarget(m)
        return when {
            goal == null -> noGoalColor
            m.region in profile.effectiveFocus -> primaryColor
            else -> secondaryColor
        }
    }
    fun fraction(m: Muscle, side: BodySide): Float {
        val goal = profile.weeklyTarget(m) ?: 10.0
        return ((minutes[m]?.get(side) ?: 0.0) / goal).toFloat().coerceIn(0f, 1f)
    }

    val summary = zones.joinToString("; ") { z ->
        val t = minutes[z.muscle] ?: SideMinutes()
        "${z.muscle.label}: left ${formatMin(t.left)}, right ${formatMin(t.right)} min"
    }.let { "Body map of this week's stretching. $it" }

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Text("Front", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("Back", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
        Canvas(
            Modifier
                .fillMaxWidth()
                .aspectRatio((FIG_W * 2 + GAP) / FIG_H)
                .semantics { contentDescription = summary }
                .pointerInput(profile) {
                    detectTapGestures { tap ->
                        val scale = size.width / (FIG_W * 2 + GAP)
                        val x = tap.x / scale
                        val y = tap.y / scale
                        val back = x > FIG_W + GAP / 2
                        val fx = if (back) x - FIG_W - GAP else x
                        // Nearest muscle on that figure (small muscles are easier to hit this way).
                        val hit = zones.filter { it.back == back }.flatMap { z -> listOf(z.rect, z.rect.mirror()).map { z.muscle to it } }
                            .minByOrNull { (_, rect) ->
                                val dx = maxOf(rect.left - fx, 0f, fx - rect.right)
                                val dy = maxOf(rect.top - y, 0f, y - rect.bottom)
                                dx * dx + dy * dy
                            }
                        selectedName = hit?.first?.name
                    }
                },
        ) {
            val scale = size.width / (FIG_W * 2 + GAP)
            for (back in listOf(false, true)) {
                val ox = if (back) FIG_W + GAP else 0f
                fun Rect.place() = Rect((left + ox) * scale, top * scale, (right + ox) * scale, bottom * scale)
                drawSilhouette(ox, scale, silhouette)
                zones.filter { it.back == back }.forEach { z ->
                    listOf(false, true).forEach { mirrored ->
                        val rect = (if (mirrored) z.rect.mirror() else z.rect).place()
                        val side = sideOf(back, mirrored)
                        val color = goalColor(z.muscle)
                        // Capsule-shaped bar: a faint wash of its goal colour, filled from the bottom.
                        val radius = CornerRadius(minOf(rect.width, rect.height) / 2f)
                        val moves = z.muscle in hasMoves
                        drawRoundRect(if (moves) color.copy(alpha = 0.22f) else track, rect.topLeft, rect.size, radius)
                        if (moves) {
                            val f = fraction(z.muscle, side)
                            // Any progress shows at least a sliver, so a short hold is visible.
                            val shown = if (f > 0f) maxOf(f, 0.08f) else 0f
                            if (shown > 0f) {
                                clipRect(rect.left, rect.bottom - rect.height * shown, rect.right, rect.bottom) {
                                    drawRoundRect(color, rect.topLeft, rect.size, radius)
                                }
                            }
                        }
                        if (z.muscle == selected) {
                            drawRoundRect(scheme.onSurface, rect.topLeft, rect.size, radius, style = Stroke(width = 2.dp.toPx()))
                        }
                    }
                }
            }
        }
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
                    Text("Tap a muscle to see its minutes.", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
                } else {
                    val goal = profile.weeklyTarget(selected)
                    val t = minutes[selected] ?: SideMinutes()
                    Text(selected.label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        when {
                            selected !in hasMoves -> "No moves for this muscle yet."
                            goal == null -> "No weekly goal. Left ${formatMin(t.left)} min · right ${formatMin(t.right)} min."
                            else -> "Left ${formatMin(t.left)} of ${formatMin(goal)} min · right ${formatMin(t.right)} of ${formatMin(goal)} min" +
                                if (selected.region in profile.effectiveFocus) " (primary)" else " (secondary)"
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
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 6.dp))
    }
}

/** A plain figure: head, neck, torso, arms, pelvis and legs, as rounded shapes. */
private fun DrawScope.drawSilhouette(ox: Float, scale: Float, color: Color) {
    fun part(x: Float, y: Float, w: Float, h: Float, rad: Float) {
        drawRoundRect(color, Offset((x + ox) * scale, y * scale), Size(w * scale, h * scale), CornerRadius(rad * scale))
        if (x + w / 2 != 50f) drawRoundRect(color, Offset((FIG_W - x - w + ox) * scale, y * scale), Size(w * scale, h * scale), CornerRadius(rad * scale))
    }
    drawCircle(color, 11f * scale, Offset((50f + ox) * scale, 15f * scale))
    part(43f, 24f, 14f, 14f, 4f) // neck
    part(26f, 35f, 48f, 62f, 12f) // torso
    part(13f, 38f, 14f, 42f, 7f) // upper arm
    part(9.5f, 80f, 13f, 38f, 6f) // forearm
    part(10f, 116f, 11f, 12f, 6f) // hand
    part(29f, 92f, 42f, 24f, 10f) // pelvis
    part(29.5f, 110f, 19.5f, 56f, 9f) // thigh
    part(32f, 164f, 15f, 44f, 7f) // lower leg
    part(30f, 206f, 18f, 8f, 4f) // foot
}

private fun formatMin(m: Double): String = if (m < 10 && m % 1.0 != 0.0) "%.1f".format(m) else m.toInt().toString()
