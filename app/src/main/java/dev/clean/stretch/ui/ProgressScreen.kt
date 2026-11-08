package dev.clean.stretch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.Stats
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** This week's minutes per body area and recent sessions, recomputed from saved history. */
@Composable
fun ProgressScreen(history: HistoryDao) {
    val zone = remember { ZoneId.systemDefault() }
    val weekFrom = remember { Stats.weekStartMillis(LocalDate.now(), zone) }
    val weekSteps by remember { history.stepsSince(weekFrom) }.collectAsState(initial = emptyList())
    val sessions by remember { history.recentSessions(20) }.collectAsState(initial = emptyList())
    val minutes = remember(weekSteps) { Stats.minutesByRegion(weekSteps) }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.secondary)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Progress", subtitle = "This week")
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Weekly goals", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Each area aims for about 10 minutes a week. Minutes count the time you held stretches for that area.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Region.entries.chunked(3).forEachIndexed { row, regions ->
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        regions.forEachIndexed { i, region ->
                            val m = minutes[region] ?: 0.0
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Ring(fraction = (m / Stats.WEEKLY_GOAL_MINUTES).toFloat(), color = colors[(row * 3 + i) % colors.size], modifier = Modifier.size(64.dp))
                                    Text(formatMinutes(m), style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(region.label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                                Text("of 10 min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Your history", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                if (sessions.isEmpty()) {
                    Text(
                        "Finished routines will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                val dateFormat = remember { DateTimeFormatter.ofPattern("EEE d MMM, HH:mm") }
                sessions.forEach { s ->
                    val start = Instant.ofEpochMilli(s.startedAt).atZone(zone)
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(s.routineTitle, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                start.format(dateFormat) + if (s.completed) "" else " · ended early",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        val mins = ((s.endedAt - s.startedAt) / 60_000).coerceAtLeast(0)
                        Text(if (mins < 1) "<1 min" else "$mins min", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun formatMinutes(m: Double): String = when {
    m <= 0.0 -> "0"
    m < 1.0 -> "<1"
    else -> m.toInt().toString()
}

@Composable
fun Ring(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    Canvas(modifier) {
        val stroke = 7.dp.toPx()
        val inset = stroke / 2
        val arc = Size(size.width - stroke, size.height - stroke)
        drawArc(track, 0f, 360f, useCenter = false, topLeft = Offset(inset, inset), size = arc, style = Stroke(stroke))
        if (fraction > 0f) {
            drawArc(color, -90f, 360f * fraction.coerceIn(0f, 1f), useCenter = false, topLeft = Offset(inset, inset), size = arc, style = Stroke(stroke, cap = StrokeCap.Round))
        }
    }
}
