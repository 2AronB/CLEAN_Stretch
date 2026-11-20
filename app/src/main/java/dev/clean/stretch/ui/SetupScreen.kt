package dev.clean.stretch.ui

import androidx.compose.runtime.key
import androidx.activity.compose.BackHandler
import dev.clean.stretch.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.platform.LocalContext
import dev.clean.stretch.settings.AppPreferences
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.plan.Condition
import dev.clean.stretch.plan.Dose
import dev.clean.stretch.plan.Generator
import dev.clean.stretch.plan.Goal
import dev.clean.stretch.plan.Profile
import dev.clean.stretch.plan.ReminderPlan
import dev.clean.stretch.plan.RoutineKind

/** Draft profile survives rotation as text. */
private val profileSaver = Saver<Profile, String>(
    save = { p ->
        listOf(
            p.goal.name, p.olderAdult, p.focus.joinToString(",") { it.name }, p.avoid.joinToString(",") { it.name },
            p.noFloor, p.noKneeling, p.hasWall, p.conditions.joinToString(",") { it.name }, p.minutes, p.setupDone,
            p.hasRoller, p.strengthen?.toString() ?: "auto", p.secondary.joinToString(",") { it.name }, p.weeklyDose,
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
            secondary = regions(f.getOrElse(12) { "" }), weeklyDose = f.getOrNull(13)?.toIntOrNull() ?: Dose.DEFAULT.minutes,
        )
    },
)

private const val PAGES = 3
private const val MENU = -1
/**
 * Three sections (owner, D-044): goal and weekly goals together; 65+, areas to avoid and health
 * conditions together; days a week and reminders. Equipment and floor/kneeling choices live on
 * Today's suggested card, and routine length now follows the weekly goals.
 */
private val SECTIONS = listOf("Goal and weekly goals", "Health and limits", "Days and reminders")

/** First-run setup, also opened from Settings (Slice S4). Nothing leaves the phone. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(initial: Profile, prefs: AppPreferences, onSave: (Profile) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var draft by rememberSaveable(stateSaver = profileSaver) { mutableStateOf(initial) }
    // After first setup, editing opens a menu of the six sections instead of walking through all of them.
    val editing = initial.setupDone
    var page by rememberSaveable { mutableIntStateOf(if (editing) MENU else 0) }
    var askingAbout by rememberSaveable { mutableStateOf<String?>(null) }

    fun saveAndClose() { Reminders.sync(context, prefs); onSave(draft) }
    fun back() {
        when {
            editing && page == MENU -> saveAndClose()
            editing -> page = MENU
            page > 0 -> page--
            else -> onBack()
        }
    }
    BackHandler { back() }

    Column(Modifier.fillMaxSize()) {
        BackBar(if (!editing) "Set up" else if (page == MENU) "Goals, health and reminders" else SECTIONS[page], ::back)
        if (!editing) LinearProgressIndicator(progress = { (page + 1f) / PAGES }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        // Each section opens at its top (one scroll position per section).
        val scroll = key(page) { rememberScrollState() }
        Column(Modifier.weight(1f).verticalScroll(scroll).padding(16.dp)) {
            when (page) {
                MENU -> SectionMenu(sectionSummaries(draft, prefs)) { page = it }
                0 -> {
                    Heading("What's your main goal?", "This picks the areas routines start with. You can change it any time.")
                    Goal.entries.forEach { g ->
                        ChoiceRow(g.label, g.blurb, selected = draft.goal == g) { draft = draft.copy(goal = g) }
                    }
                    Spacer(Modifier.height(20.dp))
                    GoalsPage(draft, prefs.daysPerWeek) { draft = it }
                }
                1 -> {
                    Heading("Health and limits", "Your answers stay on this phone. Stretch is not medical advice.")
                    ToggleRow("I'm 65 or older", "Uses longer, 60-second holds unless you pick another length on Today.", draft.olderAdult) { draft = draft.copy(olderAdult = it) }
                    Text("Anything to avoid?", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                    Text("Areas with pain, an injury or recent surgery are left out of routines.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                    RegionChips(draft.avoid) { draft = draft.copy(avoid = it) }
                    Text("Do any of these apply to you?", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                    Condition.entries.forEach { c ->
                        val yes = c in draft.conditions
                        Text(c.label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 12.dp))
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
                else -> RemindersPage(prefs, draft)
            }
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (editing) {
                Button(
                    onClick = { if (page == MENU) saveAndClose() else page = MENU },
                    modifier = Modifier.weight(1f).height(52.dp),
                ) { Text(if (page == MENU) "Save and close" else "Done") }
            } else {
                if (page > 0) OutlinedButton(onClick = ::back, modifier = Modifier.weight(1f).height(52.dp)) { Text("Back") }
                Button(
                    onClick = { if (page < PAGES - 1) page++ else saveAndClose() },
                    modifier = Modifier.weight(1f).height(52.dp),
                ) { Text(if (page < PAGES - 1) "Next" else "Save") }
            }
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

/**
 * Weekly goals (D-039). Primary areas get the chosen dose per muscle per side; secondary areas get
 * the minimum; other areas only fill spare time. A live total shows what the choices add up to,
 * so nobody signs up for two hours a week by accident.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsPage(draft: Profile, daysPerWeek: Int, onChange: (Profile) -> Unit) {
    Heading(
        "Weekly goals",
        "Primary areas get your weekly goal for each muscle, on each side. Secondary areas get a smaller amount to keep what you have. " +
            "Leave all off to follow your goal (${draft.goal.defaultFocus.joinToString { it.label.lowercase() }.ifEmpty { "commonly stiff muscles" }}).",
    )
    val levels = listOf("Off", "Secondary", "Primary")
    Region.entries.filter { it !in draft.avoid }.forEach { r ->
        val level = when (r) { in draft.focus -> 2; in draft.secondary -> 1; else -> 0 }
        Text(r.label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            levels.forEachIndexed { i, label ->
                SegmentedButton(
                    selected = level == i,
                    onClick = {
                        onChange(
                            draft.copy(
                                focus = if (i == 2) draft.focus + r else draft.focus - r,
                                secondary = if (i == 1) draft.secondary + r else draft.secondary - r,
                            ),
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = levels.size),
                    label = { Text(label) },
                )
            }
        }
    }
    if (draft.usesCommonlyStiff) {
        Text(
            "No areas picked, so goals cover commonly stiff muscles: " + commonlyStiffText() + ".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
    Spacer(Modifier.height(20.dp))
    Text("Weekly goal for primary areas", style = MaterialTheme.typography.titleMedium)
    Text(
        "Minutes for each large muscle, on each side, per week. Smaller muscles like shins, forearms and side body get half. " +
            "Secondary areas get ${Dose.SECONDARY.minutes} minutes.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Dose.entries.forEach { d ->
        ChoiceRow(d.label, d.info, selected = draft.dose == d) { onChange(draft.copy(weeklyDose = d.minutes)) }
    }
    Spacer(Modifier.height(12.dp))
    val weekly = remember(draft) { Generator.weeklyPlanMinutes(draft) }
    val perDay = (weekly + daysPerWeek - 1) / daysPerWeek.coerceAtLeast(1)
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("About $weekly min of stretching a week", style = MaterialTheme.typography.titleMedium)
            Text(
                run {
                    val hold = Generator.holdSeconds(draft, RoutineKind.Full)
                    val needed = Generator.neededMinutes(draft, daysPerWeek, hold)
                    "About $perDay min of holding on each of your $daysPerWeek days" +
                        if (needed > Generator.MAX_ROUTINE_MINUTES) {
                            ", which needs about $needed min a day. Suggested routines stop at ${Generator.MAX_ROUTINE_MINUTES} min, " +
                                "so try " + (if (draft.usesCommonlyStiff) "picking a few areas, " else "fewer primary or secondary areas, ") + "a smaller goal, or more days a week."
                        } else {
                            ", so each day's suggested routine is about ${Generator.routineMinutes(draft, daysPerWeek, RoutineKind.Full, hold)} min." +
                                " Moves that stretch several muscles at once count for each of them."
                        }
                },
                style = MaterialTheme.typography.bodySmall,
            )
        }
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

/**
 * Days a week for the week streak, and up to three reminders (R-007, D-044), each with its own
 * time and routine: type, optional areas, and foam roller / strengthen on or off. Replaces the
 * if-then cue: the app can't know when someone brushes their teeth, but it can remind at a time.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun RemindersPage(prefs: AppPreferences, draft: Profile) {
    val context = LocalContext.current
    var denied by rememberSaveable { mutableStateOf(false) }
    var editingTime by rememberSaveable { mutableIntStateOf(0) } // reminder id whose time is being picked
    var explainAround by rememberSaveable { mutableStateOf(false) }
    fun save(list: List<ReminderPlan>) { prefs.updateReminders(list); Reminders.sync(context, prefs) }
    fun update(plan: ReminderPlan) = save(prefs.reminders.map { if (it.id == plan.id) plan else it })
    fun addReminder() {
        val id = ReminderPlan.nextId(prefs.reminders) ?: return
        // Morning, midday, evening: the first of these not already taken.
        val time = listOf(8 * 60, 12 * 60, 19 * 60).firstOrNull { t -> prefs.reminders.none { it.time == t } } ?: (19 * 60)
        save(prefs.reminders + ReminderPlan(id = id, time = time, roller = draft.hasRoller, strengthen = draft.strengthenOn))
        editingTime = id
    }
    val askPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        denied = !granted
        if (granted) addReminder()
    }

    Heading("Days and reminders", "Pick how many days a week you'd like to stretch, and when you'd like a nudge.")
    Text("Days a week", style = MaterialTheme.typography.titleMedium)
    Text("A week counts toward your streak when you stretch on this many days. Missed days never break it.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        (2..7).forEach { d -> FilterChip(selected = prefs.daysPerWeek == d, onClick = { prefs.updateDaysPerWeek(d) }, label = { Text("$d") }) }
    }
    Spacer(Modifier.height(20.dp))
    Text("Reminders", style = MaterialTheme.typography.titleMedium)
    // "around" links to why the time isn't exact (minimal permissions).
    val linkColor = MaterialTheme.colorScheme.primary
    Text(
        buildAnnotatedString {
            append("Up to ${ReminderPlan.MAX} a day, each with its own routine, for example a morning warm-up and an evening cool-down. Each arrives ")
            withLink(LinkAnnotation.Clickable("around", TextLinkStyles(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))) { explainAround = true }) {
                append("around")
            }
            append(" its time.")
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    prefs.reminders.forEach { plan ->
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { editingTime = plan.id }) { Text(formatTime(plan.time), style = MaterialTheme.typography.titleLarge) }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { save(prefs.reminders.filter { it.id != plan.id }) }) { Text("Remove") }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoutineKind.entries.forEach { k -> FilterChip(selected = plan.kind == k, onClick = { update(plan.copy(kind = k)) }, label = { Text(k.label) }) }
                }
                Text("Areas (leave all off to follow your weekly goals)", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                RegionChips(plan.areas) { update(plan.copy(areas = it)) }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    val roller = plan.roller ?: draft.hasRoller
                    val strengthen = plan.strengthen ?: draft.strengthenOn
                    FilterChip(selected = roller, onClick = { update(plan.copy(roller = !roller)) }, label = { Text("Foam roller") })
                    FilterChip(selected = strengthen, onClick = { update(plan.copy(strengthen = !strengthen)) }, label = { Text("Strengthen") })
                }
            }
        }
    }
    if (prefs.reminders.size < ReminderPlan.MAX) {
        OutlinedButton(
            onClick = {
                if (!Reminders.canNotify(context)) askPermission.launch(Manifest.permission.POST_NOTIFICATIONS) // only on Android 13+
                else addReminder()
            },
            modifier = Modifier.padding(top = 12.dp),
        ) { Text(if (prefs.reminders.isEmpty()) "Add a reminder" else "Add another reminder") }
    }
    if (denied || (prefs.reminders.isNotEmpty() && !Reminders.canNotify(context))) {
        Text(
            "Notifications are off, so reminders can't show. Everything else works. You can allow notifications for Stretch in Android settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
    if (explainAround) {
        AlertDialog(
            onDismissRequest = { explainAround = false },
            title = { Text("Why \"around\"?") },
            text = {
                Text(
                    "Stretch asks for as few permissions as possible. Without the \"exact alarm\" permission, " +
                        "Android may deliver a reminder up to about 10 minutes after the time you pick, " +
                        "so it can save battery. Your routine is ready whenever the nudge arrives.",
                )
            },
            confirmButton = { TextButton(onClick = { explainAround = false }) { Text("Got it") } },
        )
    }
    prefs.reminders.firstOrNull { it.id == editingTime }?.let { plan ->
        val state = rememberTimePickerState(initialHour = plan.time / 60, initialMinute = plan.time % 60)
        AlertDialog(
            onDismissRequest = { editingTime = 0 },
            confirmButton = { TextButton(onClick = { update(plan.copy(time = state.hour * 60 + state.minute)); editingTime = 0 }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { editingTime = 0 }) { Text("Cancel") } },
            text = { TimePicker(state = state) },
        )
    }
}

private fun formatTime(minutes: Int): String =
    java.time.LocalTime.of(minutes / 60, minutes % 60).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))

/** One line per section showing what's set now, so the user can jump straight to it. */
private fun sectionSummaries(d: Profile, prefs: AppPreferences): List<String> = listOf(
    buildList {
        add(d.goal.label)
        add(if (d.usesCommonlyStiff) "Commonly stiff muscles · ${d.dose.minutes} min" else d.effectiveFocus.joinToString { it.label }.ifEmpty { "No primary areas" } + " · ${d.dose.minutes} min")
        if (d.effectiveSecondary.isNotEmpty()) add("secondary: " + d.effectiveSecondary.joinToString { it.label.lowercase() })
    }.joinToString(" · "),
    buildList {
        if (d.olderAdult) add("65 or older")
        if (d.avoid.isNotEmpty()) add("Avoid " + d.avoid.joinToString { it.label.lowercase() })
        addAll(d.conditions.map { it.label })
    }.joinToString(" · ").ifEmpty { "Nothing set" },
    "${prefs.daysPerWeek} days a week · " + when (prefs.reminders.size) {
        0 -> "no reminders"
        1 -> "reminder around ${formatTime(prefs.reminders[0].time)}"
        else -> "${prefs.reminders.size} reminders"
    },
)

@Composable
private fun SectionMenu(summaries: List<String>, onOpen: (Int) -> Unit) {
    Text("Tap a section to change it.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))
    SECTIONS.forEachIndexed { i, name ->
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onOpen(i) },
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium)
                    Text(summaries[i], style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null)
            }
        }
    }
}

/** "neck, chest, upper back, ... and calves". */
private fun commonlyStiffText(): String {
    val names = Profile.COMMONLY_STIFF.sortedBy { it.ordinal }.map { it.label.lowercase() }
    return names.dropLast(1).joinToString() + " and " + names.last()
}
