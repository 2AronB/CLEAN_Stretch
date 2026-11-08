package dev.clean.stretch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.plan.Generator
import dev.clean.stretch.plan.Profile
import kotlin.random.Random

/**
 * Review a recommended routine before starting (D-029): reorder, swap, remove, shuffle or change
 * the length. Only stretches allowed by the user's limits are ever offered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinePreviewScreen(route: Route.Preview, profile: Profile, onBack: () -> Unit, onStart: (Route) -> Unit) {
    var keysText by rememberSaveable { mutableStateOf(route.keys.joinToString(",")) }
    var minutes by rememberSaveable { mutableIntStateOf(profile.minutes) }
    var seed by rememberSaveable { mutableIntStateOf(0) }
    val keys = keysText.split(",").filter { it.isNotBlank() }
    fun set(newKeys: List<String>) { keysText = newKeys.joinToString(",") }
    fun regenerate() { set(Generator.generate(profile, route.kind, minutes = minutes, seed = seed.toLong()).keys) }
    val notes = Generator.generate(profile, route.kind, minutes = minutes).notes
    val estimate = Generator.estimateMinutes(keys, route.holdSeconds)

    Column(Modifier.fillMaxSize()) {
        BackBar(route.kind.title, onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text(
                "${keys.size} stretches · about $estimate min · ${route.holdSeconds} s holds",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                listOf(5, 10, 15).forEachIndexed { i, m ->
                    SegmentedButton(
                        selected = minutes == m,
                        onClick = { minutes = m; regenerate() },
                        shape = SegmentedButtonDefaults.itemShape(i, 3),
                        label = { Text("$m min") },
                    )
                }
            }
            if (notes.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer) }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            keys.forEachIndexed { i, key ->
                val s = SampleCatalog.byKey(key) ?: return@forEachIndexed
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Column(Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 4.dp)) {
                        Text("${i + 1}. ${s.name}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            s.primary.joinToString { it.label } + if (s.sided) " · each side" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = {
                                val alts = Generator.alternatives(profile, route.kind, key, keys)
                                if (alts.isNotEmpty()) {
                                    seed++
                                    set(keys.toMutableList().also { it[i] = alts.random(Random(seed)).key })
                                }
                            }) { Text("Swap") }
                            TextButton(onClick = { set(keys - key) }, enabled = keys.size > 1) { Text("Remove") }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { set(keys.toMutableList().also { it.add(i - 1, it.removeAt(i)) }) }, enabled = i > 0) { Text("Up") }
                            TextButton(onClick = { set(keys.toMutableList().also { it.add(i + 1, it.removeAt(i)) }) }, enabled = i < keys.lastIndex) { Text("Down") }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(onClick = { seed++; regenerate() }, modifier = Modifier.weight(1f).height(52.dp)) { Text("Shuffle") }
            Button(
                onClick = { onStart(Route.Player(keys, route.kind.title, holdSeconds = route.holdSeconds)) },
                enabled = keys.isNotEmpty(),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Icon(painterResource(R.drawable.ic_play), contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.padding(4.dp))
                Text("Start")
            }
        }
    }
}
