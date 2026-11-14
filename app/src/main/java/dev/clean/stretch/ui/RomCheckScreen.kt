package dev.clean.stretch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.clean.stretch.data.RomCheckDao
import dev.clean.stretch.data.RomCheckEntity
import dev.clean.stretch.plan.RomTest
import dev.clean.stretch.plan.RomTests
import dev.clean.stretch.ui.figure.FigureView
import dev.clean.stretch.ui.figure.figureStage
import dev.clean.stretch.figure.View
import dev.clean.stretch.ui.figure.figureColors
import kotlinx.coroutines.launch

/**
 * Range-of-motion check-in (S9). Every check is optional: anything left on "Skip" is not saved,
 * so it shows as "not measured" rather than a low score.
 */
@Composable
fun RomCheckScreen(checks: RomCheckDao, only: String? = null, onDone: () -> Unit) {
    val tests = RomTests.all.filter { only == null || it.key == only }.ifEmpty { RomTests.all }
    // "testKey|side" -> level, saved as text so it survives rotation.
    var answersText by rememberSaveable { mutableStateOf("") }
    val answers = answersText.split(";").filter { it.isNotBlank() }.associate { it.substringBeforeLast("=") to it.substringAfterLast("=").toInt() }
    fun set(id: String, level: Int?) {
        val next = answers.toMutableMap().also { if (level == null) it.remove(id) else it[id] = level }
        answersText = next.entries.joinToString(";") { "${it.key}=${it.value}" }
    }
    val scope = rememberCoroutineScope()
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        BackBar(if (tests.size == 1) tests.single().name else "Range-of-motion check", onDone)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text(
                "Warm up gently first. Go only to mild tension, never pain, and pick the closest answer. These are for tracking your own progress, not medical tests.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            tests.forEach { test ->
                Spacer(Modifier.height(16.dp))
                Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(test.name, style = MaterialTheme.typography.titleLarge)
                        test.howTo.forEachIndexed { i, step ->
                            Text("${i + 1}. $step", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        }
                        val sides = if (test.sided) listOf("left" to "Left", "right" to "Right") else listOf("both" to "")
                        sides.forEach { (side, label) ->
                            val id = "${test.key}|$side"
                            if (label.isNotEmpty()) Text("$label side", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 12.dp))
                            Spacer(Modifier.height(4.dp))
                            Option("Skip (not measured)", selected = answers[id] == null) { set(id, null) }
                            PictureChoices(test, mirrored = side == "left", selected = answers[id]) { set(id, it) }
                        }
                    }
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
            Spacer(Modifier.height(16.dp))
        }
        Button(
            onClick = {
                val now = System.currentTimeMillis()
                val rows = answers.map { (id, level) ->
                    RomCheckEntity(testKey = id.substringBefore("|"), side = id.substringAfter("|"), level = level, takenAt = now)
                }
                scope.launch {
                    try {
                        checks.insertAll(rows)
                        onDone()
                    } catch (e: Exception) {
                        error = "Couldn't save your check: ${e.message ?: e.javaClass.simpleName}"
                    }
                }
            },
            enabled = answers.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp),
        ) { Text(if (answers.isEmpty()) "Pick at least one answer" else "Save ${answers.size} ${if (answers.size == 1) "answer" else "answers"}") }
    }
}

@Composable
private fun Option(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

/** "Which looks most like you?" — one figure per level, two per row (D-033). */
@Composable
private fun PictureChoices(test: RomTest, mirrored: Boolean, selected: Int?, onPick: (Int) -> Unit) {
    val stage = remember(test.key) { figureStage(test.pictures, test.view.camera, prop = null) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        test.levels.indices.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { level ->
                    val on = selected == level
                    val bg = if (on) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest
                    Surface(
                        color = bg,
                        shape = RoundedCornerShape(16.dp),
                        border = if (on) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.weight(1f).clickable { onPick(level) },
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            FigureView(
                                pose = test.pictures[level],
                                stage = stage,
                                showGrid = false,
                                highlight = test.highlight,
                                mirrored = mirrored,
                                colors = figureColors(bg),
                                modifier = Modifier.fillMaxWidth().height(96.dp),
                                description = test.levels[level],
                            )
                            Text(test.levels[level], style = MaterialTheme.typography.labelMedium, minLines = 2, maxLines = 2)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
