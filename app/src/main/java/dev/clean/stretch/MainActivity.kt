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
        val checkpoints = CheckpointStore(applicationContext)
        val saver = SessionSaver(history, checkpoints)
        setContent { StretchRoot(prefs = prefs, history = history, profiles = db.profile(), routines = db.routines(), romChecks = db.romChecks(), saver = saver, backup = BackupManager(db, prefs, checkpoints), activity = this) }
    }
}
