package dev.clean.stretch.ui

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.clean.stretch.R
import dev.clean.stretch.data.AppScope
import dev.clean.stretch.data.HistoryDao
import dev.clean.stretch.data.ProfileDao
import dev.clean.stretch.data.ProfileMapper
import dev.clean.stretch.data.profile
import dev.clean.stretch.plan.RoutineKind
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.settings.ThemeMode
import dev.clean.stretch.ui.theme.StretchTheme

/** Every place the app can show. Encoded as text so the back stack survives rotation. */
sealed interface Route {
    data object Today : Route
    data object Library : Route
    data object Progress : Route
    data object Settings : Route
    data class Detail(val key: String) : Route
    /** [firstSide] is the side two-sided stretches start on (the one picked on the detail screen). */
    data class Player(val keys: List<String>, val title: String, val firstSide: Side = Side.Right, val holdSeconds: Int = (HOLD_MS / 1000).toInt()) : Route
    data object Studio : Route
    data object Sources : Route
    data object Setup : Route
    /** Review a recommended routine before starting it (D-029). */
    data class Preview(val kind: RoutineKind, val holdSeconds: Int, val keys: List<String>) : Route

    val isTab get() = this == Today || this == Library || this == Progress || this == Settings

    companion object {
        fun encode(r: Route): String = when (r) {
            Today -> "today"
            Library -> "library"
            Progress -> "progress"
            Settings -> "settings"
            Studio -> "studio"
            Sources -> "sources"
            Setup -> "setup"
            is Preview -> "preview|${r.kind.name}|${r.holdSeconds}|${r.keys.joinToString(",")}"
            is Detail -> "detail|${r.key}"
            is Player -> "player|${r.title}|${r.keys.joinToString(",")}|${if (r.firstSide == Side.Left) "L" else "R"}|${r.holdSeconds}"
        }

        fun decode(s: String): Route {
            val parts = s.split("|")
            return when (parts[0]) {
                "library" -> Library
                "progress" -> Progress
                "settings" -> Settings
                "studio" -> Studio
                "sources" -> Sources
                "detail" -> Detail(parts.getOrElse(1) { "" })
                "player" -> Player(
                    keys = parts.getOrElse(2) { "" }.split(",").filter { it.isNotBlank() },
                    title = parts.getOrElse(1) { "Routine" },
                    firstSide = if (parts.getOrNull(3) == "L") Side.Left else Side.Right,
                    holdSeconds = parts.getOrNull(4)?.toIntOrNull() ?: (HOLD_MS / 1000).toInt(),
                )
                "setup" -> Setup
                "preview" -> Preview(
                    kind = RoutineKind.entries.firstOrNull { it.name == parts.getOrNull(1) } ?: RoutineKind.Full,
                    holdSeconds = parts.getOrNull(2)?.toIntOrNull() ?: (HOLD_MS / 1000).toInt(),
                    keys = parts.getOrElse(3) { "" }.split(",").filter { it.isNotBlank() },
                )
                else -> Today
            }
        }
    }
}

private data class Tab(val route: Route, val label: String, val icon: Int)

private val tabs = listOf(
    Tab(Route.Today, "Today", R.drawable.ic_today),
    Tab(Route.Library, "Stretches", R.drawable.ic_library),
    Tab(Route.Progress, "Progress", R.drawable.ic_progress),
    Tab(Route.Settings, "Settings", R.drawable.ic_settings),
)

@Composable
fun StretchRoot(prefs: AppPreferences, history: HistoryDao, profiles: ProfileDao, saver: SessionSaver, activity: ComponentActivity) {
    val dark = when (prefs.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    // Keep status/navigation bar icons readable when the in-app theme differs from the system's.
    DisposableEffect(dark) {
        val transparent = android.graphics.Color.TRANSPARENT
        val style = if (dark) SystemBarStyle.dark(transparent) else SystemBarStyle.light(transparent, transparent)
        activity.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        onDispose { }
    }
    StretchTheme(dark = dark, wallpaperColors = prefs.wallpaperColors) {
        StretchApp(prefs, history, profiles, saver)
    }
}

@Composable
fun StretchApp(prefs: AppPreferences, history: HistoryDao, profiles: ProfileDao, saver: SessionSaver) {
    // null until the saved profile has loaded, so first-run cards don't flash.
    val profile by remember { profiles.profile() }.collectAsState(initial = null)
    val stack: SnapshotStateList<Route> = rememberSaveable(
        saver = listSaver<SnapshotStateList<Route>, String>(
            save = { list -> list.map(Route::encode) },
            restore = { saved -> saved.map(Route::decode).toMutableStateList() },
        ),
    ) { listOf<Route>(Route.Today).toMutableStateList() }

    // Keeps each screen's saved state (side picked, scroll position…) while it is on the back stack,
    // so it is still there when the user comes back to it. State is dropped once a screen leaves the stack.
    val screenStates = rememberSaveableStateHolder()
    val current = stack.last()
    fun forget(routes: List<Route>) { routes.forEach { screenStates.removeState(Route.encode(it)) } }
    fun push(r: Route) { stack.add(r) }
    fun pop() {
        if (stack.size > 1) forget(listOf(stack.removeAt(stack.lastIndex)))
        else if (current != Route.Today) { forget(stack.toList()); stack.clear(); stack.add(Route.Today) }
    }
    /** Replaces the current screen (e.g. preview -> player, so finishing returns to Today). */
    fun replace(r: Route) { forget(listOf(stack.removeAt(stack.lastIndex))); stack.add(r) }
    fun switchTab(r: Route) { forget(stack.toList()); stack.clear(); stack.add(r) }

    BackHandler(enabled = stack.size > 1 || current != Route.Today) { pop() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (current.isTab) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = current == tab.route,
                            onClick = { if (current != tab.route) switchTab(tab.route) },
                            icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        AnimatedContent(
            targetState = current,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
            modifier = Modifier.fillMaxSize().padding(padding),
            label = "screen",
        ) { route ->
            screenStates.SaveableStateProvider(Route.encode(route)) {
                when (route) {
                    Route.Today -> profile?.let { p ->
                        TodayScreen(prefs = prefs, profile = p, saver = saver, history = history, onStart = ::push, onOpenStretch = { push(Route.Detail(it)) }, onOpenSources = { push(Route.Sources) }, onOpenSetup = { push(Route.Setup) })
                    }
                    Route.Library -> LibraryScreen(onOpen = { push(Route.Detail(it)) })
                    Route.Progress -> ProgressScreen(history)
                    Route.Settings -> SettingsScreen(prefs = prefs, onOpenStudio = { push(Route.Studio) }, onOpenSources = { push(Route.Sources) }, onOpenSetup = { push(Route.Setup) })
                    is Route.Detail -> StretchDetailScreen(key = route.key, prefs = prefs, onBack = ::pop, onTry = ::push, onOpenSources = { push(Route.Sources) })
                    is Route.Player -> PlayerScreen(route = route, prefs = prefs, saver = saver, onClose = ::pop)
                    Route.Studio -> PoseStudioScreen(onBack = ::pop)
                    Route.Sources -> SourcesScreen(onBack = ::pop)
                    Route.Setup -> profile?.let { p ->
                        SetupScreen(
                            initial = p,
                            onSave = { edited ->
                                AppScope.launch { profiles.save(ProfileMapper.toEntity(edited.copy(setupDone = true), System.currentTimeMillis())) }
                                pop()
                            },
                            onBack = ::pop,
                        )
                    }
                    is Route.Preview -> profile?.let { p ->
                        RoutinePreviewScreen(route = route, profile = p, onBack = ::pop, onStart = ::replace)
                    }
                }
            }
        }
    }
}
