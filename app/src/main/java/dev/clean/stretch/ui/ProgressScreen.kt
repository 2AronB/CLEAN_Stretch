package dev.clean.stretch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import dev.clean.stretch.plan.Dose
import dev.clean.stretch.plan.Profile
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

/** This week's minutes per muscle and side (body view), range of motion and recent sessions, recomputed from saved history. */
@Composable
fun ProgressScreen(history: HistoryDao, profile: Profile, daysTarget: Int, romChecks: RomCheckDao, onCheck: (String?) -> Unit) {
    val romRows by remember { romChecks.observe() }.collectAsState(initial = emptyList())
    val zone = remember { ZoneId.systemDefault() }
    val weekFrom = remember { Stats.weekStartMillis(LocalDate.now(), zone) }
    val weekSteps by remember { history.stepsSince(weekFrom) }.collectAsState(initial = emptyList())
    val sessions by remember { history.recentSessions(20) }.collectAsState(initial = emptyList())
    val minutes = remember(weekSteps) { Stats.minutesByMuscle(weekSteps) }
    val starts by remember { history.startTimes() }.collectAsState(initial = emptyList())
    val days = remember(starts) { Stats.daysFromStarts(starts, zone) }
    val today = remember { LocalDate.now() }
    val streak = remember(days, daysTarget) { Stats.weekStreak(days, today, daysTarget) }
    val daysThisWeek = days.count { !it.isBefore(Stats.weekStart(today)) }

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
                    "Each side fills toward this week's goal: " +
                        "${profile.dose.minutes} min for primary areas, ${Dose.SECONDARY.minutes} min for secondary. " +
                        "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                BodyMap(minutes, profile)
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

@Composable
private fun RomHistory(levels: List<Int>, max: Int) {
    val color = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    Canvas(Modifier.padding(vertical = 4.dp).size(width = 140.dp, height = 24.dp)) {
        val r = 3.dp.toPx()
        drawLine(track, Offset(0f, size.height - r), Offset(size.width, size.height - r), strokeWidth = 1.dp.toPx())
        val pts = levels.takeLast(10).let { l ->
            l.mapIndexed { i, v ->
                val x = if (l.size == 1) size.width / 2 else r + (size.width - 2 * r) * i / (l.size - 1)
                Offset(x, size.height - r - (size.height - 2 * r) * v / max.coerceAtLeast(1))
            }
        }
        pts.zipWithNext().forEach { (a, b) -> drawLine(color, a, b, strokeWidth = 2.dp.toPx()) }
        pts.forEach { drawCircle(color, r, it) }
    }
}

/** Range-of-motion check results (S9): latest answer and change since the first check. */
@Composable
private fun RomCard(entries: List<RomEntry>, onCheck: (String?) -> Unit) {
    val trends = remember(entries) { RomTrends.of(entries) }
    val days = remember(entries) { RomTrends.daysSinceLast(entries, System.currentTimeMillis()) }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Range of motion", style = MaterialTheme.typography.titleMedium)
            Text(
                when {
                    days == null -> "See how your range changes over time."
                    days >= RomTests.RECHECK_DAYS -> "Last checked $days days ago. A good time to check again."
                    days == 0L -> "Checked today."
                    else -> "Last checked $days ${if (days == 1L) "day" else "days"} ago."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // One row per check; left and right share it (owner: as few words as needed).
            RomTests.all.forEach { test ->
                val sides = if (test.sided) listOf("left" to "L", "right" to "R") else listOf("both" to "")
                fun level(side: String) = trends[test.key to side]?.let { test.levels.getOrElse(it.latest.level) { "?" } } ?: "—"
                fun change(side: String) = trends[test.key to side]?.takeIf { it.count > 1 }?.change
                Row(
                    Modifier.fillMaxWidth().clickable(onClickLabel = "Update ${test.name}") { onCheck(test.key) }.padding(top = 10.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(test.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            if (test.sided) sides.joinToString(" · ") { (side, l) -> "$l: ${level(side)}" } else level("both").let { if (it == "—") "Not measured yet" else it },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        // Flexibility history: one dot per check, higher = more range.
                        sides.forEach { (side, _) ->
                            val series = entries.filter { it.testKey == test.key && it.side == side }.sortedBy { it.takenAt }
                            if (series.size > 1) RomHistory(series.map { it.level }, test.levels.lastIndex)
                        }
                    }
                    val changes = sides.mapNotNull { (side, l) -> change(side)?.let { c -> (if (l.isEmpty()) "" else "$l ") + (if (c > 0) "+$c" else "$c") } }
                    if (changes.isNotEmpty()) {
                        Text(
                            changes.joinToString(" · "),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (sides.any { (change(it.first) ?: 0) > 0 }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Tap one to update it. +/- = steps since your first check.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onCheck(null) }) { Text(if (days == null) "Check all now" else "Check all again") }
        }
    }
}
