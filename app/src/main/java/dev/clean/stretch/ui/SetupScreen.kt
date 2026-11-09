package dev.clean.stretch.ui

import androidx.activity.compose.BackHandler
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.platform.LocalContext
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.settings.PlanCue
import dev.clean.stretch.settings.Reminders
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.plan.Condition
import dev.clean.stretch.plan.Goal
import dev.clean.stretch.plan.Profile

/** Draft profile survives rotation as text. */
private val profileSaver = Saver<Profile, String>(
    save = { p ->
        listOf(
            p.goal.name, p.olderAdult, p.focus.joinToString(",") { it.name }, p.avoid.joinToString(",") { it.name },
            p.noFloor, p.noKneeling, p.hasWall, p.conditions.joinToString(",") { it.name }, p.minutes, p.setupDone,
            p.hasRoller, p.strengthen?.toString() ?: "auto",
        ).joinToString("|")
    },
    restore = { text ->
        val f = text.split("|")
        fun regions(s: String) = s.split(",").mapNotNull { n -> Region.entries.firstOrNull { it.name == n } }.toSet()
        Profile(
            goal = Goal.valueOf(f[0]), olderAdult = f[1].toBoolean(), focus = regions(f[2]), avoid = regions(f[3]),
            noFloor = f[4].toBoolean(), noKneeling = f[5].toBoolean(), hasWall = f[6].toBoolean(),
            conditions = f[7].split(",").mapNotNull { n -> Condition.entries.firstOrNull { it.name == n } }.toSet(),
            minutes = f[8].toInt(), setupDone = f[9].toBoolean(),
            hasRoller = f.getOrNull(10).toBoolean(), strengthen = f.getOrNull(11)?.takeIf { it != "auto" }?.toBoolean(),
        )
    },
)

private const val PAGES = 6

/**
 * First-run setup, also opened from Settings (Slice S4). Five short pages: goal, focus,
 * limits, health conditions (D-029) and routine length. Nothing leaves the phone.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(initial: Profile, prefs: AppPreferences, onSave: (Profile) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var draft by rememberSaveable(stateSaver = profileSaver) { mutableStateOf(initial) }
    var page by rememberSaveable { mutableIntStateOf(0) }
    var askingAbout by rememberSaveable { mutableStateOf<String?>(null) }

    fun back() { if (page > 0) page-- else onBack() }
    BackHandler { back() }

    Column(Modifier.fillMaxSize()) {
        BackBar(if (initial.setupDone) "Goal, focus and limits" else "Set up", ::back)
        LinearProgressIndicator(progress = { (page + 1f) / PAGES }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            when (page) {
                0 -> {
                    Heading("What's your main goal?", "This picks the areas routines start with. You can change it any time.")
                    Goal.entries.forEach { g ->
                        ChoiceRow(g.label, g.blurb, selected = draft.goal == g) { draft = draft.copy(goal = g) }
                    }
                    Spacer(Modifier.height(8.dp))
                    ToggleRow("I'm 65 or older", "Uses longer, 60-second holds.", draft.olderAdult) { draft = draft.copy(olderAdult = it) }
                }
                1 -> {
                    Heading("Any areas to focus on?", "Leave empty to follow your goal (${draft.goal.defaultFocus.joinToString { it.label }.ifEmpty { "whole body" }}).")
                    RegionChips(draft.focus) { draft = draft.copy(focus = it) }
                }
                2 -> {
                    Heading("Anything to avoid?", "Areas with pain, an injury or recent surgery are left out of routines.")
                    RegionChips(draft.avoid) { draft = draft.copy(avoid = it) }
                    Spacer(Modifier.height(12.dp))
                    ToggleRow("No floor exercises", "Standing stretches only.", draft.noFloor) { draft = draft.copy(noFloor = it) }
                    ToggleRow("No kneeling", "Leaves out kneeling and hands-and-knees.", draft.noKneeling) { draft = draft.copy(noKneeling = it) }
                    ToggleRow("I have a wall to use", "Some stretches lean on a wall.", draft.hasWall) { draft = draft.copy(hasWall = it) }
                    ToggleRow("I have a foam roller", "Adds a minute of rolling to warm-ups and cool-downs.", draft.hasRoller) { draft = draft.copy(hasRoller = it) }
                    ToggleRow(
                        "Strengthen block",
                        "Short strength holds at the end of routines. On by default for Desk and hypermobility.",
                        draft.strengthenOn,
                    ) { draft = draft.copy(strengthen = it) }
                }
                3 -> {
                    Heading("Do any of these apply to you?", "Your answers stay on this phone. Stretch is not medical advice.")
                    Condition.entries.forEach { c ->
                        val yes = c in draft.conditions
                        Text(c.label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                            SegmentedButton(
                                selected = !yes,
                                onClick = { draft = draft.copy(conditions = draft.conditions - c) },
                                shape = SegmentedButtonDefaults.itemShape(0, 2),
                                label = { Text("No") },
                            )
                            SegmentedButton(
                                selected = yes,
                                onClick = { if (!yes) askingAbout = c.name },
                                shape = SegmentedButtonDefaults.itemShape(1, 2),
                                label = { Text("Yes") },
                            )
                        }
                        if (yes) Text(c.limits, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                4 -> {
                    Heading("How long is a usual routine?", "Each routine is a suggestion. You can change it before you start.")
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        listOf(5, 10, 15).forEachIndexed { i, m ->
                            SegmentedButton(
                                selected = draft.minutes == m,
                                onClick = { draft = draft.copy(minutes = m) },
                                shape = SegmentedButtonDefaults.itemShape(i, 3),
                                label = { Text("$m min") },
                            )
                        }
                    }
                }
                else -> PlanPage(prefs)
            }
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (page > 0) OutlinedButton(onClick = ::back, modifier = Modifier.weight(1f).height(52.dp)) { Text("Back") }
            Button(
                onClick = { if (page < PAGES - 1) page++ else { Reminders.sync(context, prefs); onSave(draft) } },
                modifier = Modifier.weight(1f).height(52.dp),
            ) { Text(if (page < PAGES - 1) "Next" else "Save") }
        }
    }

    askingAbout?.let { name ->
        val c = Condition.valueOf(name)
        AlertDialog(
            onDismissRequest = { askingAbout = null },
            title = { Text("Before you begin") },
            text = {
                Text("We highly suggest talking to your doctor before beginning any new routine.\n\nStretch will adjust your routines: ${c.limits}")
            },
            confirmButton = {
                TextButton(onClick = { draft = draft.copy(conditions = draft.conditions + c); askingAbout = null }) { Text("I understand") }
            },
            dismissButton = { TextButton(onClick = { askingAbout = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun Heading(title: String, body: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(4.dp))
    Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ChoiceRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick)
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onChange(!checked) }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionChips(selected: Set<Region>, onChange: (Set<Region>) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Region.entries.forEach { r ->
            val on = r in selected
            FilterChip(selected = on, onClick = { onChange(if (on) selected - r else selected + r) }, label = { Text(r.label) })
        }
    }
}

/** If-then plan (P-7, [S22]), days per week for the week streak, and an optional daily reminder (R-007). */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PlanPage(prefs: AppPreferences) {
    val context = LocalContext.current
    var denied by rememberSaveable { mutableStateOf(false) }
    var pickingTime by rememberSaveable { mutableStateOf(false) }
    val askPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        denied = !granted
        prefs.updateReminder(granted)
        Reminders.sync(context, prefs)
    }

    Heading("When will you stretch?", "People who pick a moment, like \"after I brush my teeth\", find it easier to stick with.")
    PlanCue.entries.forEach { cue ->
        ChoiceRow(cue.label, "", selected = prefs.planCue == cue) { prefs.updatePlanCue(cue) }
    }
    Spacer(Modifier.height(16.dp))
    Text("Days a week", style = MaterialTheme.typography.titleMedium)
    Text("A week counts toward your streak when you stretch on this many days. Missed days never break it.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        (2..7).forEach { d -> FilterChip(selected = prefs.daysPerWeek == d, onClick = { prefs.updateDaysPerWeek(d) }, label = { Text("$d") }) }
    }
    Spacer(Modifier.height(12.dp))
    ToggleRow("Daily reminder", "A gentle nudge at ${formatTime(prefs.reminderTime)}.", prefs.reminderOn) { on ->
        if (on && !Reminders.canNotify(context)) { // only possible on Android 13+
            askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            denied = false
            prefs.updateReminder(on)
            Reminders.sync(context, prefs)
        }
    }
    if (prefs.reminderOn) TextButton(onClick = { pickingTime = true }) { Text("Change time") }
    if (denied) {
        Text(
            "Notifications are off, so reminders can't show. Everything else works. You can allow notifications for Stretch in Android settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
    if (pickingTime) {
        val state = rememberTimePickerState(initialHour = prefs.reminderTime / 60, initialMinute = prefs.reminderTime % 60)
        AlertDialog(
            onDismissRequest = { pickingTime = false },
            confirmButton = { TextButton(onClick = { prefs.updateReminder(true, state.hour * 60 + state.minute); Reminders.sync(context, prefs); pickingTime = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { pickingTime = false }) { Text("Cancel") } },
            text = { TimePicker(state = state) },
        )
    }
}

private fun formatTime(minutes: Int): String =
    java.time.LocalTime.of(minutes / 60, minutes % 60).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
