package dev.clean.stretch.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.clean.stretch.data.BackupCodec
import dev.clean.stretch.data.BackupData
import dev.clean.stretch.data.BackupException
import dev.clean.stretch.data.BackupManager
import dev.clean.stretch.settings.Reminders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import dev.clean.stretch.settings.Palette
import dev.clean.stretch.ui.theme.colorSchemeFor
import dev.clean.stretch.ui.theme.wallpaperColorsAvailable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.settings.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(prefs: AppPreferences, backup: BackupManager, onOpenStudio: () -> Unit, onOpenSources: () -> Unit, onOpenSetup: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backupStatus by remember { mutableStateOf<String?>(null) }
    var pendingRestore by remember { mutableStateOf<BackupData?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    val createFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            backupStatus = try {
                val text = backup.export(System.currentTimeMillis())
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) } ?: error("Couldn't open the file")
                }
                "Backup saved."
            } catch (e: Exception) {
                Log.e("StretchBackup", "Backup failed", e)
                "Backup failed: ${e.message ?: e.javaClass.simpleName}"
            }
        }
    }
    val openFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val text = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() } ?: error("Couldn't open the file")
                }
                pendingRestore = BackupCodec.decode(text)
                backupStatus = null
            } catch (e: BackupException) {
                backupStatus = "Not restored. ${e.message} Your current data is unchanged."
            } catch (e: Exception) {
                Log.e("StretchBackup", "Reading backup failed", e)
                backupStatus = "Not restored: ${e.message ?: e.javaClass.simpleName}. Your current data is unchanged."
            }
        }
    }
    val version = remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: ""
    }
    // Settings is a short menu; each cluster opens on its own page (owner, 2026-11-14).
    var section by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(enabled = section != null) { section = null }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        when (section) {
            null -> {
                ScreenTitle("Settings")
                Group {
                    NavRow("Goals, focus and limits", "Weekly goals, areas, health, length, schedule", onOpenSetup)
                    NavRow("Appearance", "Light or dark, colors, still figures", { section = "appearance" })
                    NavRow("Routine settings", "Voice, chimes, vibration", { section = "routine" })
                    NavRow("Your data", "Back up, restore, delete", { section = "data" })
                    NavRow("About", "Sources, version", { section = "about" })
                }
            }
            "appearance" -> {
                BackBar("Appearance", onBack = { section = null })
        Group {
            Column(Modifier.padding(16.dp)) {
                Text("Light or dark", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { i, mode ->
                        SegmentedButton(
                            selected = prefs.themeMode == mode,
                            onClick = { prefs.updateThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = i, count = ThemeMode.entries.size),
                            label = { Text(mode.label) },
                        )
                    }
                }
            }
            Text("Colors", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp))
            PalettePicker(selected = prefs.palette, onSelect = prefs::updatePalette)
            SwitchRow(
                title = "Still figures",
                subtitle = "Show each stretch as a still pose. Also on when Android's animations are off.",
                checked = prefs.stillFigures,
                onChange = prefs::updateStillFigures,
            )
        }

            }
            "routine" -> {
                BackBar("Routine settings", onBack = { section = null })
        Group {
            SwitchRow(
                title = "Voice",
                subtitle = "Announce each stretch and what's next, in a recorded natural voice.",
                checked = prefs.voiceCues,
                onChange = prefs::updateVoiceCues,
            )
            SwitchRow(
                title = "Chimes",
                subtitle = "3-2-1 ticks and a chime when each hold ends",
                checked = prefs.chimeCues,
                onChange = prefs::updateChimeCues,
            )
            SwitchRow(
                title = "Vibration",
                subtitle = "A short buzz between steps",
                checked = prefs.vibrationCues,
                onChange = prefs::updateVibrationCues,
            )
        }

            }
            "data" -> {
                BackBar("Your data", onBack = { section = null })
        Group {
            NavRow("Back up to a file", "History, goals and settings in one file you keep", { createFile.launch("stretch-backup-${java.time.LocalDate.now()}.json") })
            NavRow("Restore from a backup", "Replaces what's on this phone, after you confirm", { openFile.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) })
            Row(
                Modifier.fillMaxWidth().clickable { confirmDelete = true }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Delete my data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Text("Erase everything Stretch has saved on this phone", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            backupStatus?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp))
            }
        }

            }
            else -> {
                BackBar("About", onBack = { section = null })
        Group {
            NavRow("Sources & credits", "The research behind the defaults", onOpenSources)
            NavRow("Pose Studio", "For developers: tune the figure's poses", onOpenStudio)
            Column(Modifier.padding(16.dp)) {
                Text("Stretch is a CLEAN app", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "No ads, trackers, accounts, subscriptions or paywalls. Works fully offline. Not medical advice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text("Version $version", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (confirmDelete) {
        DeleteDialog(
            onCancel = { confirmDelete = false },
            onConfirm = {
                confirmDelete = false
                scope.launch {
                    backupStatus = try {
                        backup.deleteEverything()
                        Reminders.sync(context, prefs)
                        "All your Stretch data has been deleted."
                    } catch (e: Exception) {
                        Log.e("StretchBackup", "Delete failed", e)
                        "Couldn't delete everything: ${e.message ?: e.javaClass.simpleName}. Nothing was partly removed from your history."
                    }
                }
            },
        )
    }

    pendingRestore?.let { data ->
        RestoreDialog(
            data = data,
            onCancel = { pendingRestore = null },
            onConfirm = {
                pendingRestore = null
                scope.launch {
                    backupStatus = try {
                        backup.restore(data)
                        Reminders.sync(context, prefs)
                        "Restored ${data.sessions.size} sessions."
                    } catch (e: Exception) {
                        Log.e("StretchBackup", "Restore failed", e)
                        "Restore failed: ${e.message ?: e.javaClass.simpleName}. Your history was not changed."
                    }
                }
            },
        )
    }
}

@Composable
private fun Group(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
    ) { Column { content() } }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onChange(!checked) }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun NavRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RestoreDialog(data: BackupData, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Replace your data?") },
        text = {
            Text(
                "This backup has ${data.sessions.size} sessions${if (data.profile != null) ", your goals" else ""} and settings.\n\n" +
                    "Restoring replaces the history, goals and settings on this phone. This can't be undone, so back up first if you want to keep them.",
            )
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Replace") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
    )
}

@Composable
private fun DeleteDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    var understood by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Delete all your data?") },
        text = {
            Column {
                Text(
                    "This erases your stretching history, goals and limits (including health answers), saved routines, " +
                        "range-of-motion check-ins, settings, your reminder and any unfinished routine. " +
                        "Stretch goes back to how it was when first installed.\n\n" +
                        "If you might want them later, use \"Back up to a file\" first.",
                )
                Row(Modifier.fillMaxWidth().clickable { understood = !understood }.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = understood, onCheckedChange = { understood = it })
                    Text("I understand this can't be undone")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = understood) {
                Text("Delete everything", color = if (understood) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
    )
}

/** One row per palette: three colour dots, its name and a radio button (owner, 2026-11-13). */
@Composable
private fun PalettePicker(selected: Palette, onSelect: (Palette) -> Unit) {
    val context = LocalContext.current
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Column(Modifier.selectableGroup().padding(vertical = 4.dp)) {
        Palette.entries.filter { it != Palette.Wallpaper || wallpaperColorsAvailable }.forEach { palette ->
            val scheme = colorSchemeFor(context, palette, dark)
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(selected = palette == selected, role = Role.RadioButton, onClick = { onSelect(palette) })
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                    listOf(scheme.primary, scheme.secondaryContainer, scheme.tertiary).forEach { color ->
                        Box(
                            Modifier.size(26.dp).clip(CircleShape).background(color)
                                .border(2.dp, MaterialTheme.colorScheme.surfaceContainerLow, CircleShape),
                        )
                    }
                }
                Column(Modifier.weight(1f).padding(start = 16.dp)) {
                    Text(palette.label, style = MaterialTheme.typography.titleMedium)
                    Text(palette.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RadioButton(selected = palette == selected, onClick = null)
            }
        }
    }
}
