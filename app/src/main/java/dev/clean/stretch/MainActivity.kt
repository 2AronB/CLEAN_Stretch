package dev.clean.stretch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.clean.stretch.data.BackupManager
import dev.clean.stretch.data.CheckpointStore
import dev.clean.stretch.data.StretchDatabase
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.settings.Reminders
import dev.clean.stretch.ui.SessionSaver
import dev.clean.stretch.ui.StretchRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(applicationContext)
        // Re-arm the daily reminder each launch (covers phone restarts without a boot permission).
        Reminders.sync(applicationContext, prefs)
        val db = StretchDatabase.get(applicationContext)
        val history = db.history()
        val saver = SessionSaver(history, CheckpointStore(applicationContext))
        setContent { StretchRoot(prefs = prefs, history = history, profiles = db.profile(), saver = saver, backup = BackupManager(db, prefs), activity = this) }
    }
}
