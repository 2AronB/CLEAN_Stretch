package dev.clean.stretch

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.mutableIntStateOf
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
    /** Reminder whose notification opened the app (D-044); Today shows its routine. 0 = none. */
    private val openPlan = mutableIntStateOf(0)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openPlan.intValue = intent.getIntExtra(Reminders.EXTRA_PLAN, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) openPlan.intValue = intent.getIntExtra(Reminders.EXTRA_PLAN, 0)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(applicationContext)
        // Re-arm the reminders each launch (a backstop; BootReceiver covers phone restarts).
        Reminders.sync(applicationContext, prefs)
        val db = StretchDatabase.get(applicationContext)
        val history = db.history()
        val checkpoints = CheckpointStore(applicationContext)
        val saver = SessionSaver(history, checkpoints)
        setContent { StretchRoot(prefs = prefs, history = history, profiles = db.profile(), routines = db.routines(), romChecks = db.romChecks(), saver = saver, backup = BackupManager(db, prefs, checkpoints), activity = this, openPlan = openPlan) }
    }
}
