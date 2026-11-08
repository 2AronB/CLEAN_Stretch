package dev.clean.stretch.ui

import androidx.activity.compose.BackHandler
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
        )
    },
)

private const val PAGES = 5

/**
 * First-run setup, also opened from Settings (Slice S4). Five short pages: goal, focus,
 * limits, health conditions (D-029) and routine length. Nothing leaves the phone.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(initial: Profile, onSave: (Profile) -> Unit, onBack: () -> Unit) {
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
                else -> {
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
            }
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (page > 0) OutlinedButton(onClick = ::back, modifier = Modifier.weight(1f).height(52.dp)) { Text("Back") }
            Button(
                onClick = { if (page < PAGES - 1) page++ else onSave(draft) },
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
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
