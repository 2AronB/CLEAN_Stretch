package dev.clean.stretch.settings

import dev.clean.stretch.plan.ReminderPlan
import dev.clean.stretch.plan.RoutineKind
import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val label: String) { System("System"), Light("Light"), Dark("Dark") }

/** App colours (owner, 2026-11-13). Burnt fall is the default; Wallpaper follows the phone (Android 12+). */
enum class Palette(val label: String, val description: String) {
    Fall("Burnt fall", "Rust, mustard and forest green"),
    Spring("Spring pastels", "Lilac, blush and mint"),
    Winter("Winter calm", "Slate blue, frost and icy teal"),
    Summer("Summer energy", "Ocean teal, sunshine and coral"),
    Wallpaper("Wallpaper", "Match your phone's colors"),
    ;

    companion object {
        val DEFAULT = Fall
        fun fromName(name: String?): Palette = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}


/** Small on-device settings. User data (session history) lives in Room (data/History.kt). */
/** New users plan 5 days a week (owner, D-046) so the default goals fit 15-min routines. */
const val DEFAULT_DAYS_PER_WEEK = 5

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(ThemeMode.entries.getOrElse(prefs.getInt(KEY_THEME, 0)) { ThemeMode.System })
        private set
    // Replaces the old "wallpaper colors" switch (on by default), whose key is no longer read.
    var palette by mutableStateOf(Palette.fromName(prefs.getString(KEY_PALETTE, null)))
        private set
    var stillFigures by mutableStateOf(prefs.getBoolean(KEY_STILL, false))
        private set

    var voiceCues by mutableStateOf(prefs.getBoolean(KEY_VOICE, true))
        private set
    var chimeCues by mutableStateOf(prefs.getBoolean(KEY_CHIMES, true))
        private set
    var vibrationCues by mutableStateOf(prefs.getBoolean(KEY_VIBRATION, true))
        private set

    var daysPerWeek by mutableIntStateOf(prefs.getInt(KEY_DAYS, DEFAULT_DAYS_PER_WEEK))
        private set
    /** Up to three reminders, each with its own routine (D-044). Replaces the single reminder and if-then cue. */
    var reminders by mutableStateOf(loadReminders())
        private set
    /** Hold length picked on Today's card (30 / 45 / 60 s, D-044); 0 = not picked yet (45 s, or 60 s for 65+). */
    var holdSeconds by mutableIntStateOf(prefs.getInt(KEY_HOLD, 0))
        private set
    /** The routine type last picked on Today (D-044). */
    var routineKind by mutableStateOf(RoutineKind.entries.firstOrNull { it.name == prefs.getString(KEY_KIND, null) } ?: RoutineKind.Full)
        private set

    /** Reminders as saved; a phone that only had the old single daily reminder gets it as reminder 1. */
    private fun loadReminders(): List<ReminderPlan> = if (prefs.contains(KEY_REMINDERS)) {
        ReminderPlan.decodeAll(prefs.getString(KEY_REMINDERS, null))
    } else if (prefs.getBoolean(KEY_REMINDER, false)) {
        listOf(ReminderPlan(id = 1, time = prefs.getInt(KEY_REMINDER_TIME, 19 * 60)))
    } else {
        emptyList()
    }

    /** Monday (epoch day) of the week the weekly flexibility check was last offered. Not backed up. */
    var romPromptWeek: Long
        get() = prefs.getLong(KEY_ROM_PROMPT, 0L)
        set(value) { prefs.edit { putLong(KEY_ROM_PROMPT, value) } }

    fun updateDaysPerWeek(days: Int) { daysPerWeek = days; prefs.edit { putInt(KEY_DAYS, days) } }
    fun updateReminders(list: List<ReminderPlan>) {
        reminders = ReminderPlan.decodeAll(ReminderPlan.encodeAll(list))
        prefs.edit { putString(KEY_REMINDERS, ReminderPlan.encodeAll(reminders)) }
    }
    fun updateHoldSeconds(seconds: Int) { holdSeconds = seconds; prefs.edit { putInt(KEY_HOLD, seconds) } }
    fun updateRoutineKind(kind: RoutineKind) { routineKind = kind; prefs.edit { putString(KEY_KIND, kind.name) } }

    fun updateVoiceCues(enabled: Boolean) {
        voiceCues = enabled
        prefs.edit { putBoolean(KEY_VOICE, enabled) }
    }

    fun updateChimeCues(enabled: Boolean) {
        chimeCues = enabled
        prefs.edit { putBoolean(KEY_CHIMES, enabled) }
    }

    fun updateVibrationCues(enabled: Boolean) {
        vibrationCues = enabled
        prefs.edit { putBoolean(KEY_VIBRATION, enabled) }
    }

    fun updateThemeMode(mode: ThemeMode) {
        themeMode = mode
        prefs.edit { putInt(KEY_THEME, mode.ordinal) }
    }

    fun updatePalette(choice: Palette) {
        palette = choice
        prefs.edit { putString(KEY_PALETTE, choice.name) }
    }

    fun updateStillFigures(enabled: Boolean) {
        stillFigures = enabled
        prefs.edit { putBoolean(KEY_STILL, enabled) }
    }

    /** "Delete my data": removes every saved setting and goes back to the defaults. */
    fun clearAll() {
        prefs.edit { clear() }
        importSettings(emptyMap())
    }

    /** Settings for a backup file (S7): every saved key as text. */
    fun exportSettings(): Map<String, String> = prefs.all.mapValues { it.value.toString() }

    /** Restores backed-up settings, ignoring unknown keys, then reloads the visible values. */
    fun importSettings(values: Map<String, String>) {
        val ints = setOf(KEY_THEME, KEY_DAYS, KEY_REMINDER_TIME, KEY_HOLD)
        val bools = setOf(KEY_STILL, KEY_VOICE, KEY_CHIMES, KEY_VIBRATION, KEY_REMINDER)
        prefs.edit {
            values.forEach { (k, v) ->
                when (k) {
                    in ints -> v.toIntOrNull()?.let { putInt(k, it) }
                    in bools -> putBoolean(k, v.toBoolean())
                    KEY_PALETTE, KEY_KIND -> putString(k, v)
                    // Backups store newlines as-is; anything unreadable is dropped by decodeAll.
                    KEY_REMINDERS -> putString(k, v)
                }
            }
        }
        themeMode = ThemeMode.entries.getOrElse(prefs.getInt(KEY_THEME, 0)) { ThemeMode.System }
        palette = Palette.fromName(prefs.getString(KEY_PALETTE, null))
        stillFigures = prefs.getBoolean(KEY_STILL, false)
        voiceCues = prefs.getBoolean(KEY_VOICE, true)
        chimeCues = prefs.getBoolean(KEY_CHIMES, true)
        vibrationCues = prefs.getBoolean(KEY_VIBRATION, true)
        daysPerWeek = prefs.getInt(KEY_DAYS, DEFAULT_DAYS_PER_WEEK)
        reminders = loadReminders()
        holdSeconds = prefs.getInt(KEY_HOLD, 0)
        routineKind = RoutineKind.entries.firstOrNull { it.name == prefs.getString(KEY_KIND, null) } ?: RoutineKind.Full
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_PALETTE = "palette"
        const val KEY_ROM_PROMPT = "rom_prompt_week"
        const val KEY_STILL = "still_figures"
        // "plan_cue" (the if-then cue) is no longer read (D-044).
        const val KEY_REMINDERS = "reminders"
        const val KEY_HOLD = "hold_seconds"
        const val KEY_KIND = "routine_kind"
        const val KEY_DAYS = "plan_days_per_week"
        const val KEY_REMINDER = "reminder_on"
        const val KEY_REMINDER_TIME = "reminder_time"
        const val KEY_VOICE = "cue_voice"
        const val KEY_CHIMES = "cue_chimes"
        const val KEY_VIBRATION = "cue_vibration"
    }
}
