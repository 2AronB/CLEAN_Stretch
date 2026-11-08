package dev.clean.stretch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.Stretch
import dev.clean.stretch.data.Checkpoint
import dev.clean.stretch.data.StepRecord
import dev.clean.stretch.data.StepRecords
import dev.clean.stretch.figure.Easing
import dev.clean.stretch.figure.MotionKind
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.figure.Timing
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.figure.FigureView
import dev.clean.stretch.ui.figure.figureColors
import dev.clean.stretch.ui.figure.rememberReducedMotion
import kotlin.math.ceil

/** Evidence default P-2: 40 s holds for adults, with a 5 s "get into position" step that isn't counted. */
const val SETUP_MS = 5_000L
const val HOLD_MS = 40_000L

enum class Side(val label: String) { Both(""), Right("Right side"), Left("Left side") }

data class Step(val stretch: Stretch, val side: Side, val setup: Boolean) {
    val baseMs get() = if (setup) SETUP_MS else HOLD_MS
}

fun buildSteps(keys: List<String>, firstSide: Side = Side.Right): List<Step> = keys.mapNotNull { SampleCatalog.byKey(it) }.flatMap { s ->
    val sides = when {
        !s.sided -> listOf(Side.Both)
        firstSide == Side.Left -> listOf(Side.Left, Side.Right)
        else -> listOf(Side.Right, Side.Left)
    }
    sides.flatMap { side -> listOf(Step(s, side, setup = true), Step(s, side, setup = false)) }
}

fun estimateMinutes(route: Route.Player): Int =
    ceil(buildSteps(route.keys).sumOf { it.baseMs } / 60_000.0).toInt().coerceAtLeast(1)

private val recordsSaver = Saver<SnapshotStateMap<Int, StepRecord>, String>(
    save = { StepRecords.encode(it) },
    restore = { text -> mutableStateMapOf<Int, StepRecord>().apply { putAll(StepRecords.decode(text)) } },
)

/**
 * The hands-free runner (Slice S2). It advances on its own, keeps the screen on, speaks and chimes
 * (each switchable in Settings), writes a checkpoint at every step and saves an immutable session
 * when the routine finishes or the user ends it early.
 */
@Composable
fun PlayerScreen(route: Route.Player, prefs: AppPreferences, saver: SessionSaver, onClose: () -> Unit) {
    val steps = remember(route) { buildSteps(route.keys, route.firstSide) }
    if (steps.isEmpty()) {
        Column(Modifier.fillMaxSize()) {
            BackBar("Routine", onClose)
            Text("Nothing to play.", modifier = Modifier.padding(24.dp))
        }
        return
    }
    val reduced = rememberReducedMotion(prefs.stillFigures)
    val context = LocalContext.current
    val cues = remember { Cues(context, prefs) }
    DisposableEffect(Unit) { onDispose { cues.release() } }

    // Runs once per routine (not again after rotation): resume a checkpoint if Today asked for it,
    // otherwise save any older unfinished run as "ended early" so it is never lost.
    val start = rememberSaveable {
        val store = saver.checkpoints
        val c = store.load()
        val resume = store.resumeNext && c != null && c.route == Route.encode(route)
        store.resumeNext = false
        if (resume) {
            "${c.startedAt}|${c.index.coerceIn(0, steps.lastIndex)}|${c.records}"
        } else {
            if (c != null) saver.saveCheckpoint(c)
            "${System.currentTimeMillis()}|0|"
        }
    }
    val startParts = start.split("|", limit = 3)
    val startedAt = startParts[0].toLong()

    var index by rememberSaveable { mutableIntStateOf(startParts[1].toInt()) }
    val records = rememberSaveable(saver = recordsSaver) {
        mutableStateMapOf<Int, StepRecord>().apply { putAll(StepRecords.decode(startParts[2])) }
    }
    var paused by rememberSaveable { mutableStateOf(false) }
    var extraMs by rememberSaveable { mutableLongStateOf(0L) }
    var finished by rememberSaveable { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    val elapsed = remember { mutableLongStateOf(0L) }

    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    fun saveOnce(completed: Boolean) {
        if (saved) return
        saved = true
        saver.save(route.title, steps, records.toMap(), startedAt, System.currentTimeMillis(), completed)
    }

    /** Moves to [next]. Leaving a hold forward records how long it was held. */
    fun goTo(next: Int, skipped: Boolean = false) {
        val current = steps[index]
        if (next > index && !current.setup) {
            records[index] = StepRecord(elapsed.longValue, skipped)
            if (!skipped) cues.chime()
        }
        if (next >= steps.size) {
            finished = true
            saveOnce(completed = true)
            return
        }
        index = next.coerceAtLeast(0)
        elapsed.longValue = 0L
        extraMs = 0L
        cues.buzz()
        saver.checkpoints.save(Checkpoint(Route.encode(route), index, startedAt, System.currentTimeMillis(), StepRecords.encode(records)))
    }

    /** End button and system Back: keep what was done as an "ended early" session. */
    fun endEarly() {
        if (!finished) {
            val current = steps[index]
            if (!current.setup && elapsed.longValue > 0L) records[index] = StepRecord(elapsed.longValue, skipped = true)
            saveOnce(completed = false)
        }
        onClose()
    }
    BackHandler { endEarly() }

    val step = steps[index]
    val duration = step.baseMs + extraMs

    LaunchedEffect(paused, finished, index) {
        if (paused || finished) return@LaunchedEffect
        var last = withFrameMillis { it }
        while (true) {
            withFrameMillis { now ->
                val dt = now - last
                last = now
                elapsed.longValue += dt
            }
            if (elapsed.longValue >= steps[index].baseMs + extraMs) {
                goTo(index + 1)
                break
            }
        }
    }

    // Voice line at the start of each step.
    LaunchedEffect(index, finished) {
        if (finished) { cues.say("Routine complete. Nice work."); return@LaunchedEffect }
        val s = steps[index]
        val side = if (s.side == Side.Both) "" else ", ${s.side.label.lowercase()}"
        if (s.setup) cues.say("${s.stretch.name}$side. Get into position.")
        else cues.say("Hold for ${(s.baseMs / 1000)} seconds.")
    }

    if (finished) {
        FinishedView(route.title, records.values.sumOf { it.heldMs }, onClose)
        return
    }

    val t = elapsed.longValue
    val holdSteps = steps.filter { !it.setup }
    val holdIndex = steps.take(index + 1).count { !it.setup } - if (step.setup) 0 else 1
    val stage = MaterialTheme.colorScheme.surfaceContainerLow
    val pose = if (step.setup) step.stretch.motion.start else MotionPlayer.sessionPose(step.stretch.motion, t, reduced)
    val frame = remember(step.stretch.key) { MotionPlayer.frameFor(step.stretch.motion) }
    // Every stretch-and-side begins with a setup step, so the next setup step is the next new thing
    // (not the hold of the side that is being set up right now).
    val nextStretch = steps.drop(index + 1).firstOrNull { it.setup }

    // 3-2-1 ticks and "what's next" near the end of each hold.
    val remainingSec = ceil((duration - t).coerceAtLeast(0L) / 1000.0).toInt()
    LaunchedEffect(index, remainingSec, paused) {
        if (paused || step.setup) return@LaunchedEffect
        if (remainingSec in 1..3) cues.tick()
        if (remainingSec == 6 && nextStretch != null) {
            val side = if (nextStretch.side == Side.Both) "" else ", ${nextStretch.side.label.lowercase()}"
            cues.say("Next: ${nextStretch.stretch.name}$side.")
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = ::endEarly) { Icon(painterResource(R.drawable.ic_close), contentDescription = "End routine and save what you did") }
            Text(route.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(
                "${(holdIndex + 1).coerceAtMost(holdSteps.size)} of ${holdSteps.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp),
            )
        }
        SegmentedProgress(total = holdSteps.size, done = holdIndex, current = if (step.setup) 0f else (t.toFloat() / duration).coerceIn(0f, 1f))

        Surface(
            color = stage,
            shape = RoundedCornerShape(36.dp),
            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
        ) {
            Box {
                FigureView(
                    pose = pose,
                    frame = frame,
                    highlight = step.stretch.highlight,
                    mirrored = step.side == Side.Left,
                    prop = step.stretch.prop,
                    glow = if (reduced || step.setup) 0f else Easing.breath((t - Timing.ENTER_MS).coerceAtLeast(0L)),
                    colors = figureColors(stage),
                    // Extra top room keeps the "Get into position" label clear of the figure's head.
                    modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 64.dp),
                    description = step.stretch.name,
                )
                if (step.setup) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
                    ) {
                        Text("Get into position", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }
        }

        AnimatedContent(
            targetState = step.stretch.name to step.side,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
            label = "title",
            modifier = Modifier.fillMaxWidth(),
        ) { (name, side) ->
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                if (side != Side.Both) {
                    Text(side.label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Text(
            cueFor(step, t, reduced),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            minLines = 2,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
        )

        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FilledTonalIconButton(onClick = { if (t > 3_000L || index == 0) goTo(index) else goTo(index - 2) }, modifier = Modifier.size(56.dp)) {
                Icon(painterResource(R.drawable.ic_skip_previous), contentDescription = "Back")
            }
            Box(contentAlignment = Alignment.Center) {
                CountdownRing(fraction = 1f - (t.toFloat() / duration).coerceIn(0f, 1f), modifier = Modifier.size(112.dp))
                FilledIconButton(
                    onClick = { paused = !paused },
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(),
                ) {
                    if (paused) {
                        Icon(painterResource(R.drawable.ic_play), contentDescription = "Resume", modifier = Modifier.size(32.dp))
                    } else {
                        Text(
                            "${ceil((duration - t).coerceAtLeast(0L) / 1000.0).toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
            FilledTonalIconButton(
                onClick = { if (step.setup) goTo(index + 2) else goTo(index + 1, skipped = true) },
                modifier = Modifier.size(56.dp),
            ) {
                Icon(painterResource(R.drawable.ic_skip_next), contentDescription = "Skip")
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (!step.setup) {
                TextButton(onClick = { extraMs += 15_000L }) { Text("+15 s") }
            }
            if (nextStretch != null && (step.setup || duration - t < 8_000L)) {
                Text(
                    "Next: ${nextStretch.stretch.name}${if (nextStretch.side != Side.Both) " · ${nextStretch.side.label.lowercase()}" else ""}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }
        }
    }
}

private fun cueFor(step: Step, t: Long, reduced: Boolean): String {
    val s = step.stretch
    if (step.setup) return s.cues.firstOrNull() ?: s.summary
    if (t < Timing.ENTER_MS) return s.cues.getOrElse(1) { s.summary }
    if (s.motion.kind == MotionKind.Flow) return "Move slowly with your breath."
    if (reduced) return "Hold at mild tension. Breathe slowly."
    val inBreath = (t - Timing.ENTER_MS) % Timing.BREATH_MS
    return if (inBreath < Timing.BREATH_MS / 2) "Breathe out… ease a little deeper." else "Breathe in… stay tall and relaxed."
}

@Composable
private fun SegmentedProgress(total: Int, done: Int, current: Float) {
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val fill = MaterialTheme.colorScheme.primary
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(total) { i ->
            val amount = when {
                i < done -> 1f
                i == done -> current
                else -> 0f
            }
            Canvas(Modifier.weight(1f).fillMaxSize()) {
                drawLine(track, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = size.height, cap = StrokeCap.Round)
                if (amount > 0f) {
                    drawLine(fill, Offset(0f, size.height / 2), Offset(size.width * amount, size.height / 2), strokeWidth = size.height, cap = StrokeCap.Round)
                }
            }
        }
    }
}

@Composable
private fun CountdownRing(fraction: Float, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val fill = MaterialTheme.colorScheme.primary
    Canvas(modifier) {
        val stroke = 6.dp.toPx()
        val inset = stroke / 2
        val arcSize = Size(size.width - stroke, size.height - stroke)
        drawArc(track, 0f, 360f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = Stroke(stroke))
        drawArc(fill, -90f, 360f * fraction, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
private fun FinishedView(title: String, heldMs: Long, onClose: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Nice work", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(8.dp))
        val minutes = heldMs / 60_000
        val seconds = (heldMs / 1000) % 60
        Text("$title · ${minutes} min ${seconds} s of stretching", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text(
            "Saved to your history. See this week's minutes on Progress.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onClose, modifier = Modifier.height(52.dp).fillMaxWidth()) { Text("Done") }
    }
}
