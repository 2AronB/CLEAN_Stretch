package dev.clean.stretch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.StretchRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(applicationContext)
        setContent { StretchRoot(prefs = prefs, activity = this) }
    }
}
