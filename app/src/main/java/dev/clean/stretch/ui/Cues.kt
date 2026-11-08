package dev.clean.stretch.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import dev.clean.stretch.settings.AppPreferences
import java.util.Locale

/**
 * Hands-free cues: Android's built-in offline text-to-speech, short chimes and a vibration
 * (docs/PLAN.md §3.1). Each one follows its own switch in Settings. If no voice is installed,
 * chimes and vibration still work.
 */
class Cues(context: Context, private val prefs: AppPreferences) {
    private var ttsReady = false
    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ttsReady = status == TextToSpeech.SUCCESS
        if (ttsReady) {
            tts.language = Locale.getDefault()
        } else {
            Log.w(TAG, "Text-to-speech unavailable (status $status); using chimes only")
        }
    }
    private val tone: ToneGenerator? = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 70) }
        .onFailure { Log.w(TAG, "Chimes unavailable", it) }
        .getOrNull()
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun say(text: String) {
        if (prefs.voiceCues && ttsReady) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    /** Short tick for the 3-2-1 countdown. */
    fun tick() {
        if (prefs.chimeCues) tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 90)
    }

    /** Longer chime at the end of a hold. */
    fun chime() {
        if (prefs.chimeCues) tone?.startTone(ToneGenerator.TONE_PROP_ACK, 220)
    }

    fun buzz() {
        if (prefs.vibrationCues && vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun release() {
        tts.stop()
        tts.shutdown()
        tone?.release()
    }

    private companion object { const val TAG = "StretchCues" }
}
