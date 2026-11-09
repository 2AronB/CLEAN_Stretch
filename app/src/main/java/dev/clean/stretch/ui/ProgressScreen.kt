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
import androidx.compose.material3.Button
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.RomCheckDao
import dev.clean.stretch.plan.RomEntry
import dev.clean.stretch.plan.RomTests
import dev.clean.stretch.plan.RomTrends
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
fun ProgressScreen(history: HistoryDao, daysTarget: Int, romChecks: RomCheckDao, onCheck: () -> Unit) {
    val romRows by remember { romChecks.observe() }.collectAsState(initial = emptyList())
    val zone = remember { ZoneId.systemDefault() }
    val weekFrom = remember { Stats.weekStartMillis(LocalDate.now(), zone) }
    val weekSteps by remember { history.stepsSince(weekFrom) }.collectAsState(initial = emptyList())
    val sessions by remember { history.recentSessions(20) }.collectAsState(initial = emptyList())
    val minutes = remember(weekSteps) { Stats.minutesByRegion(weekSteps) }
    val starts by remember { history.startTimes() }.collectAsState(initial = emptyList())
    val days = remember(starts) { Stats.daysFromStarts(starts, zone) }
    val today = remember { LocalDate.now() }
    val streak = remember(days, daysTarget) { Stats.weekStreak(days, today, daysTarget) }
    val daysThisWeek = days.count { !it.isBefore(Stats.weekStart(today)) }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.secondary)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Progress", subtitle = "This week")
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        when (streak) { 0 -> "Building your first week"; 1 -> "1 week"; else -> "$streak weeks" },
                        style = if (streak == 0) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                    )
                    Text("Week streak · weeks with $daysTarget or more days of stretching", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$daysThisWeek/$daysTarget", style = MaterialTheme.typography.titleLarge)
                    Text("days this week", style = MaterialTheme.typography.labelSmall)
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
        RomCard(romRows.map { it.toEntry() }, onCheck)
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

/** Range-of-motion check results (S9): latest answer and change since the first check. */
@Composable
private fun RomCard(entries: List<RomEntry>, onCheck: () -> Unit) {
    val trends = remember(entries) { RomTrends.of(entries) }
    val days = remember(entries) { RomTrends.daysSinceLast(entries, System.currentTimeMillis()) }
    val dateFormat = remember { DateTimeFormatter.ofPattern("d MMM") }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Range of motion", style = MaterialTheme.typography.titleMedium)
            Text(
                when {
                    days == null -> "Check how far you can reach today, then again in a few weeks to see what's changed."
                    days >= RomTests.RECHECK_DAYS -> "Last checked $days days ago. A good time to check again."
                    days == 0L -> "Checked today. Check again in about 4 weeks."
                    else -> "Last checked $days ${if (days == 1L) "day" else "days"} ago. Check again in about 4 weeks."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            RomTests.all.forEach { test ->
                val sides = if (test.sided) listOf("left" to " (left)", "right" to " (right)") else listOf("both" to "")
                sides.forEach { (side, label) ->
                    val trend = trends[test.key to side]
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(test.name + label, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                trend?.let { test.levels.getOrElse(it.latest.level) { "?" } } ?: "Not measured yet",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (trend != null && trend.count > 1) {
                            val since = Instant.ofEpochMilli(trend.first.takenAt).atZone(ZoneId.systemDefault()).format(dateFormat)
                            Text(
                                when {
                                    trend.change > 0 -> "+${trend.change} ${if (trend.change == 1) "step" else "steps"} since $since"
                                    trend.change < 0 -> "${trend.change} ${if (trend.change == -1) "step" else "steps"} since $since"
                                    else -> "Same as $since"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (trend.change > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onCheck) { Text(if (days == null) "Check now" else "Check again") }
        }
    }
}
