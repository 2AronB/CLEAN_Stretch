package dev.clean.stretch.settings

import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val label: String) { System("System"), Light("Light"), Dark("Dark") }

/** The "if" in "If [cue], then I stretch" (P-7, [S22]). */
enum class PlanCue(val label: String, val reminderText: String) {
    Wake("After I wake up", "after you wake up"),
    Teeth("After I brush my teeth", "after you brush your teeth"),
    Workout("After a workout", "after your workout"),
    Bed("Before bed", "before bed"),
    SetTime("At a set time", "now"),
}

/** Small on-device settings. User data (session history) lives in Room (data/History.kt). */
class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(ThemeMode.entries.getOrElse(prefs.getInt(KEY_THEME, 0)) { ThemeMode.System })
        private set
    var wallpaperColors by mutableStateOf(prefs.getBoolean(KEY_WALLPAPER, true))
        private set
    var stillFigures by mutableStateOf(prefs.getBoolean(KEY_STILL, false))
        private set

    var voiceCues by mutableStateOf(prefs.getBoolean(KEY_VOICE, true))
        private set
    var chimeCues by mutableStateOf(prefs.getBoolean(KEY_CHIMES, true))
        private set
    var vibrationCues by mutableStateOf(prefs.getBoolean(KEY_VIBRATION, true))
        private set

    // If-then plan and reminder (P-7, R-007).
    var planCue by mutableStateOf(PlanCue.entries.firstOrNull { it.name == prefs.getString(KEY_PLAN_CUE, null) } ?: PlanCue.Teeth)
        private set
    var daysPerWeek by mutableIntStateOf(prefs.getInt(KEY_DAYS, 3))
        private set
    var reminderOn by mutableStateOf(prefs.getBoolean(KEY_REMINDER, false))
        private set
    /** Minutes after midnight. */
    var reminderTime by mutableIntStateOf(prefs.getInt(KEY_REMINDER_TIME, 19 * 60))
        private set

    fun updatePlanCue(cue: PlanCue) { planCue = cue; prefs.edit { putString(KEY_PLAN_CUE, cue.name) } }
    fun updateDaysPerWeek(days: Int) { daysPerWeek = days; prefs.edit { putInt(KEY_DAYS, days) } }
    fun updateReminder(on: Boolean, time: Int = reminderTime) {
        reminderOn = on
        reminderTime = time
        prefs.edit { putBoolean(KEY_REMINDER, on); putInt(KEY_REMINDER_TIME, time) }
    }

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

    fun updateWallpaperColors(enabled: Boolean) {
        wallpaperColors = enabled
        prefs.edit { putBoolean(KEY_WALLPAPER, enabled) }
    }

    fun updateStillFigures(enabled: Boolean) {
        stillFigures = enabled
        prefs.edit { putBoolean(KEY_STILL, enabled) }
    }

    /** Settings for a backup file (S7): every saved key as text. */
    fun exportSettings(): Map<String, String> = prefs.all.mapValues { it.value.toString() }

    /** Restores backed-up settings, ignoring unknown keys, then reloads the visible values. */
    fun importSettings(values: Map<String, String>) {
        val ints = setOf(KEY_THEME, KEY_DAYS, KEY_REMINDER_TIME)
        val bools = setOf(KEY_WALLPAPER, KEY_STILL, KEY_VOICE, KEY_CHIMES, KEY_VIBRATION, KEY_REMINDER)
        prefs.edit {
            values.forEach { (k, v) ->
                when (k) {
                    in ints -> v.toIntOrNull()?.let { putInt(k, it) }
                    in bools -> putBoolean(k, v.toBoolean())
                    KEY_PLAN_CUE -> putString(k, v)
                }
            }
        }
        themeMode = ThemeMode.entries.getOrElse(prefs.getInt(KEY_THEME, 0)) { ThemeMode.System }
        wallpaperColors = prefs.getBoolean(KEY_WALLPAPER, true)
        stillFigures = prefs.getBoolean(KEY_STILL, false)
        voiceCues = prefs.getBoolean(KEY_VOICE, true)
        chimeCues = prefs.getBoolean(KEY_CHIMES, true)
        vibrationCues = prefs.getBoolean(KEY_VIBRATION, true)
        planCue = PlanCue.entries.firstOrNull { it.name == prefs.getString(KEY_PLAN_CUE, null) } ?: PlanCue.Teeth
        daysPerWeek = prefs.getInt(KEY_DAYS, 3)
        reminderOn = prefs.getBoolean(KEY_REMINDER, false)
        reminderTime = prefs.getInt(KEY_REMINDER_TIME, 19 * 60)
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_WALLPAPER = "wallpaper_colors"
        const val KEY_STILL = "still_figures"
        const val KEY_PLAN_CUE = "plan_cue"
        const val KEY_DAYS = "plan_days_per_week"
        const val KEY_REMINDER = "reminder_on"
        const val KEY_REMINDER_TIME = "reminder_time"
        const val KEY_VOICE = "cue_voice"
        const val KEY_CHIMES = "cue_chimes"
        const val KEY_VIBRATION = "cue_vibration"
    }
}
