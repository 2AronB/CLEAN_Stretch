package dev.clean.stretch.run

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import dev.clean.stretch.catalog.StretchKind
import dev.clean.stretch.data.Checkpoint
import dev.clean.stretch.data.StepRecord
import dev.clean.stretch.data.StepRecords
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.Cues
import dev.clean.stretch.ui.Route
import dev.clean.stretch.ui.SessionSaver
import dev.clean.stretch.ui.Step
import dev.clean.stretch.ui.VoiceLines
import dev.clean.stretch.ui.buildSteps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * One routine in progress. It lives outside the screen (D-037), so it keeps going when the phone
 * is locked or the app is in the background: [RunService] keeps the app alive, a partial wake
 * lock keeps the timer ticking while it runs, and cues play from here. The player screen only
 * shows this state and forwards button presses.
 */
class RunSession private constructor(
    context: Context,
    val route: Route.Player,
    val steps: List<Step>,
    private val saver: SessionSaver,
    prefs: AppPreferences,
    val startedAt: Long,
    startIndex: Int,
    records: Map<Int, StepRecord>,
) {
    private val app = context.applicationContext
    private val cues = Cues(app, prefs)
    val engine = RunEngine(steps, startIndex, records, now())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val wakeLock = app.getSystemService(PowerManager::class.java)
        ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Stretch:routine")
        ?.apply { setReferenceCounted(false) }
    private var saved = false
    var closed = false
        private set

    /** Bumped on every change, so the screen redraws. */
    var revision by mutableIntStateOf(0)
        private set

    /** Starts the clock, cues and background service. Register the session in [ActiveRun] first. */
    internal fun start() {
        perform(engine.start())
        RunService.start(app)
        scope.launch {
            while (isActive) {
                val events = engine.tick(now())
                if (events.isNotEmpty()) perform(events)
                delay(TICK_MS)
            }
        }
    }

    fun togglePause() = act { engine.togglePause(now()); emptyList() }
    fun ready() = act { engine.ready(now()) }
    fun skip() = act { engine.skip(now()) }
    fun back() = act { engine.back(now()) }
    fun addTime() = act { engine.addTime(); emptyList() }

    /** End button and system Back: keep what was done as an "ended early" session, then close. */
    fun end() {
        if (closed) return
        if (!engine.finished) {
            engine.endEarly(now())
            saveOnce(completed = false)
        }
        close()
    }

    /** Stops everything without saving; the last checkpoint stays for Today's Resume card. */
    fun close() {
        if (closed) return
        closed = true
        scope.cancel()
        cues.release()
        releaseWakeLock()
        RunService.stop(app)
        ActiveRun.forget(this)
        revision++
    }

    private inline fun act(change: () -> List<RunEvent>) {
        if (closed) return
        perform(change())
        RunService.update(app, this)
        revision++
    }

    private fun perform(events: List<RunEvent>) {
        for (event in events) when (event) {
            is RunEvent.StepStarted -> {
                if (event.buzz) {
                    cues.buzz()
                    saver.checkpoints.save(Checkpoint(Route.encode(route), engine.index, startedAt, System.currentTimeMillis(), StepRecords.encode(engine.records)))
                }
                cues.say(voiceFor(steps[event.index]))
            }
            RunEvent.HoldDone -> cues.chime()
            is RunEvent.Countdown -> cues.tick()
            is RunEvent.AnnounceNext -> steps[event.index].let { cues.say(VoiceLines.next(it.stretch, it.side)) }
            RunEvent.Finished -> {
                saveOnce(completed = true)
                cues.say(VoiceLines.COMPLETE)
                // Let the closing line play, then stop holding the phone awake.
                scope.launch {
                    delay(FINISH_GRACE_MS)
                    releaseWakeLock()
                    RunService.stop(app)
                }
            }
        }
        if (engine.paused || engine.finished) releaseWakeLock() else holdWakeLock()
        if (events.isNotEmpty()) {
            RunService.update(app, this)
            revision++
        }
    }

    private fun voiceFor(s: Step): String = when {
        s.setup -> VoiceLines.setup(s.stretch, s.side)
        s.stretch.kind == StretchKind.Roll -> VoiceLines.roll(s.baseMs / 1000)
        s.stretch.kind == StretchKind.Dynamic -> VoiceLines.flow(s.baseMs / 1000)
        else -> VoiceLines.hold(s.baseMs / 1000)
    }

    private fun saveOnce(completed: Boolean) {
        if (saved) return
        saved = true
        saver.save(route.title, steps, engine.records, startedAt, System.currentTimeMillis(), completed)
    }

    /** Re-armed at every step with a timeout, so a bug can never keep the phone awake for long. */
    private fun holdWakeLock() {
        runCatching { wakeLock?.acquire(WAKE_TIMEOUT_MS) }
    }

    private fun releaseWakeLock() {
        runCatching { if (wakeLock?.isHeld == true) wakeLock.release() }
    }

    companion object {
        private const val TICK_MS = 100L
        private const val FINISH_GRACE_MS = 6_000L
        private const val WAKE_TIMEOUT_MS = 10 * 60 * 1000L

        fun now(): Long = SystemClock.elapsedRealtime()

        /** Makes a run (not started yet), resuming the saved checkpoint if Today's Resume asked for it or [resumeFrom] matches it. */
        internal fun begin(context: Context, route: Route.Player, saver: SessionSaver, prefs: AppPreferences, resumeFrom: Long?): RunSession {
            val steps = buildSteps(route.keys, route.firstSide, route.holdSeconds * 1000L)
            val store = saver.checkpoints
            val c = store.load()
            val wanted = store.resumeNext
            store.resumeNext = false
            val session = if (c != null && c.route == Route.encode(route) && (wanted || (resumeFrom != null && c.startedAt == resumeFrom))) {
                RunSession(context, route, steps, saver, prefs, c.startedAt, c.index, StepRecords.decode(c.records))
            } else {
                // An older unfinished run is saved as "ended early" so it is never lost.
                if (c != null) saver.saveCheckpoint(c)
                RunSession(context, route, steps, saver, prefs, System.currentTimeMillis(), 0, emptyMap())
            }
            return session
        }
    }
}

/** The one routine that may be running. Lives as long as the app's process. */
object ActiveRun {
    // Holds only the application context (never an Activity), which lives as long as the process.
    @SuppressLint("StaticFieldLeak")
    var current: RunSession? = null
        private set

    /**
     * The live run for [route] (after rotation, or coming back from the notification), or a new
     * one. [resumeFrom] is the start time the screen remembered, used after Android restarts the app.
     */
    fun obtain(context: Context, route: Route.Player, saver: SessionSaver, prefs: AppPreferences, resumeFrom: Long?): RunSession {
        current?.let { live ->
            if (!live.closed && live.route == route) return live
            live.end()
        }
        val session = RunSession.begin(context, route, saver, prefs, resumeFrom)
        current = session // before start(), so the service finds it
        session.start()
        return session
    }

    internal fun forget(session: RunSession) {
        if (current === session) current = null
    }
}
