package dev.clean.stretch.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.figure.Pose
import dev.clean.stretch.figure.PoseParam
import dev.clean.stretch.ui.figure.FigureView
import dev.clean.stretch.ui.figure.figureStage
import dev.clean.stretch.figure.View
import dev.clean.stretch.ui.figure.figureColors
import dev.clean.stretch.ui.figure.rememberFigureClock
import kotlin.math.roundToInt

private val frameNames = listOf("Start", "Stretch", "Deep")

/**
 * Developer tool: adjust any keyframe with sliders and copy the result as Kotlin
 * to paste into SampleCatalog.kt. Edits are not saved on the phone.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoseStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var key by remember { mutableStateOf(SampleCatalog.all.first().key) }
    var frameIndex by remember { mutableIntStateOf(1) }
    var playing by remember { mutableStateOf(false) }
    var mirrored by remember { mutableStateOf(false) }
    var view by remember { mutableStateOf(View.Side) }
    val edits = remember { mutableStateMapOf<String, Pose>() }

    val stretch = SampleCatalog.byKey(key) ?: SampleCatalog.all.first()
    val original = MotionPlayer.keyframes(stretch.motion)
    val frameCount = original.size
    val safeFrame = frameIndex.coerceAtMost(frameCount - 1)
    fun editKey(i: Int) = "${stretch.key}#$i"
    fun poseAt(i: Int): Pose = edits[editKey(i)] ?: original[i]
    val edited = stretch.motion.copy(start = poseAt(0), stretch = poseAt(1), deep = if (frameCount > 2) poseAt(2) else null, view = view)
    val pose = poseAt(safeFrame)
    val camera = rememberViewCamera(view, reducedMotion = false)
    val figureStage = figureStage(MotionPlayer.keyframes(edited), camera, stretch.prop)
    val time by rememberFigureClock(running = playing)
    val stage = MaterialTheme.colorScheme.surfaceContainerLow

    fun copy(text: String, what: String) {
        val clipboard = context.getSystemService(ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("pose", text))
        Toast.makeText(context, "$what copied", Toast.LENGTH_SHORT).show()
    }

    Column(Modifier.fillMaxSize()) {
        BackBar("Pose Studio", onBack)
        Surface(color = stage, shape = RoundedCornerShape(28.dp), modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(260.dp)) {
            FigureView(
                pose = if (playing) MotionPlayer.demoPose(edited, time, reducedMotion = false) else pose,
                stage = figureStage,
                highlight = stretch.highlight,
                mirrored = mirrored,
                colors = figureColors(stage),
                modifier = Modifier.fillMaxSize().padding(12.dp),
                description = "Pose preview",
            )
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SampleCatalog.all, key = { it.key }) { s ->
                FilterChip(selected = s.key == key, onClick = { key = s.key; frameIndex = 1; view = s.motion.view }, label = { Text(s.name) })
            }
        }
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            (0 until frameCount).forEach { i ->
                SegmentedButton(
                    selected = i == safeFrame,
                    onClick = { frameIndex = i; playing = false },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = frameCount),
                    label = { Text(frameNames[i]) },
                )
            }
        }
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            View.entries.forEach { v ->
                SegmentedButton(
                    selected = v == view,
                    onClick = { view = v },
                    shape = SegmentedButtonDefaults.itemShape(index = v.ordinal, count = View.entries.size),
                    label = { Text(v.label) },
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = { playing = !playing }) { Text(if (playing) "Stop" else "Play") }
            OutlinedButton(onClick = { mirrored = !mirrored }) { Text("Mirror") }
            OutlinedButton(onClick = { edits.remove(editKey(safeFrame)) }) { Text("Reset") }
            FilledTonalButton(onClick = { copy(pose.toKotlin(), "Pose") }) { Text("Copy") }
        }
        LazyColumn(
            Modifier.weight(1f).navigationBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        ) {
            items(PoseParam.entries) { param ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(param.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(112.dp))
                    Slider(
                        value = pose[param].coerceIn(param.min, param.max),
                        onValueChange = { v ->
                            playing = false
                            edits[editKey(safeFrame)] = pose.with(param, (v * 2f).roundToInt() / 2f)
                        },
                        valueRange = param.min..param.max,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${pose[param].roundToInt()}°",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(48.dp).padding(start = 8.dp),
                    )
                }
            }
            item {
                OutlinedButton(
                    onClick = {
                        val text = buildString {
                            append("motion = Motion(\n    kind = MotionKind.${edited.kind.name},\n")
                            append("    start = ${edited.start.toKotlin()},\n")
                            append("    stretch = ${edited.stretch.toKotlin()},\n")
                            edited.deep?.let { append("    deep = ${it.toKotlin()},\n") }
                            if (edited.pins.isNotEmpty()) append("    pins = setOf(${edited.pins.joinToString { "Pin.${it.name}" }}),\n")
                            if (edited.view != View.Side) append("    view = View.${edited.view.name},\n")
                            append("),")
                        }
                        copy(text, "Motion")
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Copy whole motion as Kotlin") }
            }
        }
    }
}
