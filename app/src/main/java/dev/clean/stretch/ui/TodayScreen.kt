package dev.clean.stretch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.Stats
import dev.clean.stretch.figure.Easing
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.figure.Timing
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.figure.FigureView
import dev.clean.stretch.ui.figure.figureColors
import dev.clean.stretch.ui.figure.rememberFigureClock
import dev.clean.stretch.ui.figure.rememberReducedMotion
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** The S1 sample routine. The generator (Slice S5) replaces this with a personal one. */
val SampleRoutine = Route.Player(SampleCatalog.fullBodyReset, "Full-body reset")

private val routineStretches get() = SampleRoutine.keys.mapNotNull { SampleCatalog.byKey(it) }

@Composable
fun TodayScreen(
    prefs: AppPreferences,
    saver: SessionSaver,
    history: HistoryDao,
    onStart: (Route) -> Unit,
    onOpenStretch: (String) -> Unit,
    onOpenSources: () -> Unit,
) {
    val reduced = rememberReducedMotion(prefs.stillFigures)
    var checkpoint by remember { mutableStateOf(saver.checkpoints.load()) }
    val zone = remember { ZoneId.systemDefault() }
    val weekFrom = remember { Stats.weekStartMillis(LocalDate.now(), zone) }
    val recent by remember { history.recentSessions(50) }.collectAsState(initial = emptyList())
    val activeDays = remember(recent) { Stats.activeDays(recent.filter { it.startedAt >= weekFrom }, zone) }
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val today = remember { LocalDate.now() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle(title = greeting, subtitle = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")))
        saver.lastError?.let { error ->
            NoticeCard(title = "Not saved", body = error)
            Spacer(Modifier.height(12.dp))
        }
        checkpoint?.let { c ->
            val route = Route.decode(c.route) as? Route.Player
            if (route != null) {
                UnfinishedCard(
                    title = route.title,
                    onResume = { saver.checkpoints.resumeNext = true; onStart(route) },
                    onSave = { saver.saveCheckpoint(c); checkpoint = null },
                )
                Spacer(Modifier.height(12.dp))
            }
        }
        HeroRoutineCard(reduced = reduced, onStart = { onStart(SampleRoutine) })
        Spacer(Modifier.height(20.dp))
        SectionLabel("In this routine")
        RoutineStrip(onOpenStretch)
        Spacer(Modifier.height(20.dp))
        SectionLabel("This week")
        WeekCard(today, activeDays)
        Spacer(Modifier.height(20.dp))
        EvidenceCard(onOpenSources)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HeroRoutineCard(reduced: Boolean, onStart: () -> Unit) {
    val stretches = routineStretches
    val time by rememberFigureClock(running = !reduced)
    val index = ((time / Timing.DEMO_LOOP_MS) % stretches.size).toInt()
    val current = stretches[index]
    val frame = remember(current.key) { MotionPlayer.frameFor(current.motion) }
    val container = MaterialTheme.colorScheme.primaryContainer
    val minutes = estimateMinutes(SampleRoutine)

    Surface(
        color = container,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("Suggested for you", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(SampleRoutine.title, style = MaterialTheme.typography.headlineMedium)
            Text("${stretches.size} stretches · about $minutes min · hands-free", style = MaterialTheme.typography.bodyMedium)
            FigureView(
                pose = MotionPlayer.demoPose(current.motion, time % Timing.DEMO_LOOP_MS, reduced),
                frame = frame,
                highlight = current.highlight,
                prop = current.prop,
                glow = if (reduced) 0f else Easing.breath(time),
                colors = figureColors(container),
                modifier = Modifier.fillMaxWidth().aspectRatio(1.5f).padding(vertical = 8.dp),
                description = "${current.name} demonstration",
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(current.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Button(
                    onClick = onStart,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    modifier = Modifier.height(52.dp),
                ) {
                    Icon(painterResource(R.drawable.ic_play), contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start")
                }
            }
        }
    }
}

@Composable
private fun RoutineStrip(onOpenStretch: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        routineStretches.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { s ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).clickable { onOpenStretch(s.key) },
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            StretchThumbnail(
                                s,
                                background = MaterialTheme.colorScheme.surfaceContainerLow,
                                modifier = Modifier.fillMaxWidth().height(84.dp),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(s.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                            Text(s.primary.joinToString { it.label }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WeekCard(today: LocalDate, activeDays: Set<LocalDate>) {
    val monday = today.with(DayOfWeek.MONDAY)
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                (0 until 7).forEach { i ->
                    val day = monday.plusDays(i.toLong())
                    val isToday = day == today
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier.size(34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            val active = day in activeDays
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isToday -> MaterialTheme.colorScheme.primary
                                    active -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                                border = if (isToday && active) BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary) else null,
                                modifier = Modifier.size(if (isToday) 34.dp else 28.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        if (active && !isToday) "✓" else "${day.dayOfMonth}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when {
                                            isToday -> MaterialTheme.colorScheme.onPrimary
                                            active -> MaterialTheme.colorScheme.onTertiaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                if (activeDays.isEmpty()) {
                    "Finish a routine to start your week. Missed days never break your streak — weeks that reach your goal do the counting."
                } else {
                    "${activeDays.size} ${if (activeDays.size == 1) "day" else "days"} of stretching this week. Missed days never break your streak."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun UnfinishedCard(title: String, onResume: () -> Unit, onSave: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Unfinished routine", style = MaterialTheme.typography.labelLarge)
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onResume) { Text("Resume") }
                OutlinedButton(onClick = onSave) { Text("Save what I did") }
            }
        }
    }
}

@Composable
private fun NoticeCard(title: String, body: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun EvidenceCard(onOpenSources: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable(onClick = onOpenSources),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Why it works", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(
                "Consistency matters most. About 10 minutes per muscle each week is where flexibility gains level off.",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text("Ingram et al. 2024 · 189 studies · See sources", style = MaterialTheme.typography.bodySmall)
        }
    }
}
