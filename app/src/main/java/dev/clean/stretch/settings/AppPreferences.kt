package dev.clean.stretch.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val label: String) { System("System"), Light("Light"), Dark("Dark") }

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

    fun updateVoiceCues(enabled: Boolean) {
        voiceCues = enabled
        prefs.edit().putBoolean(KEY_VOICE, enabled).apply()
    }

    fun updateChimeCues(enabled: Boolean) {
        chimeCues = enabled
        prefs.edit().putBoolean(KEY_CHIMES, enabled).apply()
    }

    fun updateVibrationCues(enabled: Boolean) {
        vibrationCues = enabled
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }

    fun updateThemeMode(mode: ThemeMode) {
        themeMode = mode
        prefs.edit().putInt(KEY_THEME, mode.ordinal).apply()
    }

    fun updateWallpaperColors(enabled: Boolean) {
        wallpaperColors = enabled
        prefs.edit().putBoolean(KEY_WALLPAPER, enabled).apply()
    }

    fun updateStillFigures(enabled: Boolean) {
        stillFigures = enabled
        prefs.edit().putBoolean(KEY_STILL, enabled).apply()
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_WALLPAPER = "wallpaper_colors"
        const val KEY_STILL = "still_figures"
        const val KEY_VOICE = "cue_voice"
        const val KEY_CHIMES = "cue_chimes"
        const val KEY_VIBRATION = "cue_vibration"
    }
}
