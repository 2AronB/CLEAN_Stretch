package dev.clean.stretch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.clean.stretch.catalog.Muscle
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.Stretch
import dev.clean.stretch.plan.Generator
import dev.clean.stretch.plan.Profile
import dev.clean.stretch.plan.RoutineKind
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.SavedRoutineDao
import dev.clean.stretch.data.SavedRoutineEntity
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun TodayScreen(
    prefs: AppPreferences,
    profile: Profile,
    saver: SessionSaver,
    history: HistoryDao,
    onStart: (Route) -> Unit,
    onOpenStretch: (String) -> Unit,
    onOpenSources: () -> Unit,
    onOpenSetup: () -> Unit,
    savedRoutines: SavedRoutineDao,
    onOpenTender: () -> Unit,
    onUpdateProfile: (Profile) -> Unit,
) {
    val saved by remember { savedRoutines.observe() }.collectAsState(initial = emptyList())
    var deleting by remember { mutableStateOf<SavedRoutineEntity?>(null) }
    val scope = rememberCoroutineScope()
    val reduced = rememberReducedMotion(prefs.stillFigures)
    var checkpoint by remember { mutableStateOf(saver.checkpoints.load()) }
    val zone = remember { ZoneId.systemDefault() }
    val weekFrom = remember { Stats.weekStartMillis(LocalDate.now(), zone) }
    val recent by remember { history.recentSessions(50) }.collectAsState(initial = emptyList())
    val activeDays = remember(recent) { Stats.activeDays(recent.filter { it.startedAt >= weekFrom }, zone) }
    // Day circles (owner, 2026-11-14): each day fills toward its share of the weekly goal.
    val allSessions by remember { history.allSessions() }.collectAsState(initial = emptyList())
    val allSteps by remember { history.allSteps() }.collectAsState(initial = emptyList())
    val dayMinutes = remember(allSessions, allSteps) { Stats.minutesByDay(allSessions, allSteps, zone) }
    val dayGoal = remember(profile, prefs.daysPerWeek) { Generator.dayGoalMinutes(profile, prefs.daysPerWeek) }
    val streak = remember(dayMinutes, dayGoal, prefs.daysPerWeek) { Stats.weekStreakByGoal(dayMinutes, LocalDate.now(), prefs.daysPerWeek, dayGoal) }
    val weekSteps by remember { history.stepsSince(weekFrom) }.collectAsState(initial = emptyList())
    val weekMinutes = remember(weekSteps) { Stats.minutesByMuscle(weekSteps).mapValues { it.value.weaker } }
    var kindName by rememberSaveable { mutableStateOf(RoutineKind.Full.name) }
    val kind = RoutineKind.valueOf(kindName)
    // The recommendation (D-029): the user reviews and changes it before starting.
    // Generator v2 inputs: this week's minutes per area, planned days still to come, how often
    // each move was used this week, and today's date so the suggestion changes day to day.
    val weekCounts = remember(weekSteps) { weekSteps.groupingBy { it.stretchKey }.eachCount() }
    val todayDate = remember { LocalDate.now() }
    val daysLeft = (prefs.daysPerWeek - activeDays.count { it != todayDate }).coerceAtLeast(1)
    val routine = remember(profile, kind, weekMinutes, weekCounts, daysLeft) {
        Generator.generate(profile, kind, weekMinutes = weekMinutes, seed = todayDate.toEpochDay() * 7 + kind.ordinal, daysLeft = daysLeft, weekCounts = weekCounts)
    }
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
        if (!profile.setupDone) {
            SetupCard(onOpenSetup)
            Spacer(Modifier.height(12.dp))
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RoutineKind.entries.forEach { k ->
                FilterChip(selected = k == kind, onClick = { kindName = k.name }, label = { Text(k.label) })
            }
        }
        Spacer(Modifier.height(8.dp))
        val stretches = routine.keys.mapNotNull { SampleCatalog.byKey(it) }
        if (stretches.isEmpty()) {
            NoticeCard(title = "Nothing fits yet", body = routine.notes.joinToString(" "))
        } else {
            HeroRoutineCard(
                title = routine.title,
                stretches = stretches,
                minutes = Generator.estimateMinutes(routine.keys, routine.holdSeconds),
                notes = routine.notes,
                onOpenStretch = onOpenStretch,
                onStart = { onStart(Route.Player(routine.keys, routine.title, holdSeconds = routine.holdSeconds)) },
                onEdit = { onStart(Route.Preview(kind, routine.holdSeconds, routine.keys)) },
                profile = profile,
                onUpdateProfile = onUpdateProfile,
                works = remember(routine) { Generator.minutesByMuscle(routine.keys, routine.holdSeconds) },
                weekMinutes = weekMinutes,
            )
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable(onClick = onOpenTender),
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Sore spot?", style = MaterialTheme.typography.titleMedium)
                    Text("Build a short, gentle routine for it", style = MaterialTheme.typography.bodyMedium)
                }
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null)
            }
        }
        Spacer(Modifier.height(20.dp))
        if (saved.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            SectionLabel("Your routines")
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                saved.forEach { r ->
                    val keys = r.keys.split(",").filter { SampleCatalog.byKey(it) != null }
                    val kindOf = RoutineKind.entries.firstOrNull { it.name == r.kind } ?: RoutineKind.Full
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(r.name, style = MaterialTheme.typography.titleMedium)
                                Text("${keys.size} stretches · about ${Generator.estimateMinutes(keys, r.holdSeconds)} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { deleting = r }) { Text("Delete") }
                            Button(onClick = { onStart(Route.Preview(kindOf, r.holdSeconds, keys)) }, enabled = keys.isNotEmpty()) { Text("Open") }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        SectionLabel("This week")
        WeekCard(today, Stats.weekFill(Stats.weekStart(today), dayMinutes, dayGoal), prefs.daysPerWeek, streak, dayGoal, onOpenSources)
        Spacer(Modifier.height(32.dp))
    }

    deleting?.let { r ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete \"${r.name}\"?") },
            text = { Text("The routine is removed. Your history of past sessions stays as it is.") },
            confirmButton = { TextButton(onClick = { scope.launch { savedRoutines.delete(r.id) }; deleting = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } },
        )
    }
}

/**
 * The day's recommendation (D-032): the stretches as tiles, then Start (straight into the
 * routine) or Edit (optional changes). Getting it right on generation is the goal.
 */
@Composable
private fun HeroRoutineCard(
    title: String,
    stretches: List<Stretch>,
    minutes: Int,
    notes: List<String>,
    onOpenStretch: (String) -> Unit,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    profile: Profile,
    onUpdateProfile: (Profile) -> Unit,
    works: Map<Muscle, Double>,
    weekMinutes: Map<Muscle, Double>,
) {
    var showWorks by rememberSaveable { mutableStateOf(false) }
    val container = MaterialTheme.colorScheme.primaryContainer
    Surface(
        color = container,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Suggested for you", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            // The title is a link: it opens which muscles this routine works (kept off the card).
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium.copy(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable(onClickLabel = "See which muscles this works") { showWorks = true },
            )
            Text("${stretches.size} moves · about $minutes min · hands-free", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stretches.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { s ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).clickable { onOpenStretch(s.key) },
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    StretchThumbnail(
                                        s,
                                        background = MaterialTheme.colorScheme.surfaceContainerLowest,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(s.name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, minLines = 2)
                                }
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
            notes.filter { !it.contains(":") }.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onStart,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    modifier = Modifier.weight(1f).height(52.dp),
                ) {
                    Icon(painterResource(R.drawable.ic_play), contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start")
                }
                OutlinedButton(onClick = onEdit, modifier = Modifier.height(52.dp)) { Text("Edit") }
            }
            // Quick switches that rebuild the suggestion (owner, 2026-11-14).
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                FilterChip(selected = profile.hasRoller, onClick = { onUpdateProfile(profile.copy(hasRoller = !profile.hasRoller)) }, label = { Text("Foam roller") })
                FilterChip(selected = profile.strengthenOn, onClick = { onUpdateProfile(profile.copy(strengthen = !profile.strengthenOn)) }, label = { Text("Strengthen") })
            }
        }
    }
    if (showWorks) {
        AlertDialog(
            onDismissRequest = { showWorks = false },
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    works.entries.sortedBy { it.key.ordinal }.forEach { (m, min) ->
                        val goal = profile.weeklyTarget(m)
                        Row {
                            Text(m.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(
                                "+${fmt(min)} min" + (goal?.let { " · ${fmt(weekMinutes[m] ?: 0.0)}/${fmt(it)}" } ?: ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text("Per side · this week / goal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { showWorks = false }) { Text("Close") } },
        )
    }

}

@Composable
private fun WeekCard(today: LocalDate, fill: List<Float>, daysTarget: Int, streak: Int, dayGoal: Double, onOpenSources: () -> Unit) {
    val monday = today.with(DayOfWeek.MONDAY)
    val done = fill.count { it >= 0.999f }
    var why by rememberSaveable { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                (0 until 7).forEach { i ->
                    val day = monday.plusDays(i.toLong())
                    DayCircle(day, fill[i], isToday = day == today)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$done of $daysTarget days" + if (streak > 0) " · $streak-week streak" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "Why?",
                    style = MaterialTheme.typography.labelLarge.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { why = true }.padding(8.dp),
                )
            }
        }
    }
    if (why) {
        AlertDialog(
            onDismissRequest = { why = false },
            title = { Text("Steady weeks") },
            text = {
                Text(
                    "A day fills at about ${dayGoal.toInt()} min. Missed one? Extra minutes later in the week fill it.\n\n" +
                        "Gains level off at about 10 min per muscle a week.",
                )
            },
            confirmButton = { TextButton(onClick = { why = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { why = false; onOpenSources() }) { Text("Sources") } },
        )
    }
}

/** A day's circle: a ring that fills toward the day's goal; a full day gets a check. */
@Composable
private fun DayCircle(day: LocalDate, fill: Float, isToday: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val full = fill >= 0.999f
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            day.dayOfWeek.getDisplayName(TextStyle.NARROW, LocalConfiguration.current.locales[0]),
            style = MaterialTheme.typography.labelMedium,
            color = if (isToday) scheme.primary else scheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
            val track = scheme.surfaceContainerHighest
            val color = scheme.primary
            Canvas(Modifier.size(34.dp)) {
                val stroke = 4.dp.toPx()
                val inset = stroke / 2
                val arc = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
                val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
                if (full) {
                    drawCircle(color)
                } else {
                    drawArc(track, 0f, 360f, false, topLeft, arc, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                    if (fill > 0f) drawArc(color, -90f, 360f * fill, false, topLeft, arc, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                }
            }
            Text(
                if (full) "✓" else "${day.dayOfMonth}",
                style = MaterialTheme.typography.labelMedium,
                color = if (full) scheme.onPrimary else if (isToday) scheme.primary else scheme.onSurfaceVariant,
            )
        }
    }
}

private fun fmt(m: Double): String = if (m % 1.0 == 0.0) m.toInt().toString() else "%.1f".format(m)

@Composable
private fun SetupCard(onOpenSetup: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Make it yours", style = MaterialTheme.typography.labelLarge)
            Text("Tell Stretch your goal, focus areas and anything to avoid. About a minute.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onOpenSetup) { Text("Set up") }
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
