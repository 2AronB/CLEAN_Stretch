package dev.clean.stretch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.plan.Generator
import dev.clean.stretch.plan.Profile

/**
 * "Tender spot" (P-10 item 4): pick a sore spot, answer two safety questions, get a short gentle
 * routine (roll → stretch → strengthen). A spot that has been sore for weeks, hurts at rest or at
 * night, or follows an injury gets a "get it checked" note instead (EVIDENCE §13).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TenderSpotScreen(profile: Profile, onBack: () -> Unit, onStart: (Route) -> Unit) {
    var muscleName by rememberSaveable { mutableStateOf<String?>(null) }
    var longLasting by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var atRest by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val muscle = Muscle.entries.firstOrNull { it.name == muscleName }
    val seeClinician = longLasting == true || atRest == true
    val ready = muscle != null && longLasting == false && atRest == false
    val routine = if (ready) Generator.tenderSpot(profile, muscle!!) else null

    Column(Modifier.fillMaxSize()) {
        BackBar("Tender spot", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text(
                "Pick a sore or tight spot and Stretch builds a short, gentle routine for it. This isn't medical advice.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Text("Where is it?", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                Muscle.entries.forEach { m ->
                    FilterChip(selected = m == muscle, onClick = { muscleName = m.name }, label = { Text(m.label) })
                }
            }
            if (muscle != null) {
                Spacer(Modifier.height(20.dp))
                YesNo("Has it been sore for more than 2 weeks?", longLasting) { longLasting = it }
                YesNo("Does it hurt at rest, at night, or after an injury or fall?", atRest) { atRest = it }
            }
            if (seeClinician) {
                Spacer(Modifier.height(16.dp))
                Surface(color = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Best to get this checked", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Pain that lasts for weeks, hurts at rest or at night, or follows an injury is worth showing to a doctor or physiotherapist rather than rolling or stretching it.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            routine?.let { r ->
                Spacer(Modifier.height(20.dp))
                Text("Your routine", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${r.keys.size} moves · about ${Generator.estimateMinutes(r.keys, r.holdSeconds)} min · gentle ${r.holdSeconds} s holds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                r.keys.mapNotNull { SampleCatalog.byKey(it) }.forEachIndexed { i, s ->
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${i + 1}. ${s.name}", style = MaterialTheme.typography.titleSmall)
                            Text(s.kind.label + if (s.sided) " · each side" else "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                r.notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
            }
            Spacer(Modifier.height(16.dp))
        }
        Button(
            onClick = { routine?.let { onStart(Route.Player(it.keys, it.title, holdSeconds = it.holdSeconds)) } },
            enabled = routine != null && routine.keys.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp),
        ) { Text(if (muscle == null) "Pick a spot" else if (!ready) "Answer both questions" else "Start") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YesNo(question: String, value: Boolean?, onChange: (Boolean) -> Unit) {
    Text(question, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 6.dp)) {
        SegmentedButton(selected = value == false, onClick = { onChange(false) }, shape = SegmentedButtonDefaults.itemShape(0, 2), label = { Text("No") })
        SegmentedButton(selected = value == true, onClick = { onChange(true) }, shape = SegmentedButtonDefaults.itemShape(1, 2), label = { Text("Yes") })
    }
}
