package dev.clean.stretch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.clean.stretch.data.CheckpointStore
import dev.clean.stretch.data.StretchDatabase
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.SessionSaver
import dev.clean.stretch.ui.StretchRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(applicationContext)
        val history = StretchDatabase.get(applicationContext).history()
        val saver = SessionSaver(history, CheckpointStore(applicationContext))
        setContent { StretchRoot(prefs = prefs, history = history, saver = saver, activity = this) }
    }
}
