package dev.clean.stretch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.Sources
import dev.clean.stretch.settings.AppPreferences
import dev.clean.stretch.ui.figure.rememberReducedMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StretchDetailScreen(
    key: String,
    prefs: AppPreferences,
    onBack: () -> Unit,
    onTry: (Route) -> Unit,
    onOpenSources: () -> Unit,
) {
    val stretch = SampleCatalog.byKey(key)
    if (stretch == null) {
        Column(Modifier.fillMaxSize()) {
            BackBar("Stretch", onBack)
            Text("This stretch isn't in the library anymore.", modifier = Modifier.padding(24.dp))
        }
        return
    }
    val reduced = rememberReducedMotion(prefs.stillFigures)
    var rightSide by rememberSaveable(key) { mutableStateOf(true) }
    val card = MaterialTheme.colorScheme.surfaceContainerLow

    Column(Modifier.fillMaxSize()) {
        BackBar(stretch.name, onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        ) {
            Surface(color = card, shape = RoundedCornerShape(32.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    AnimatedStretch(
                        stretch,
                        reducedMotion = reduced,
                        mirrored = stretch.sided && !rightSide,
                        background = card,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.35f),
                    )
                    if (stretch.sided) {
                        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            SegmentedButton(
                                selected = !rightSide,
                                onClick = { rightSide = false },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                label = { Text("Left side") },
                            )
                            SegmentedButton(
                                selected = rightSide,
                                onClick = { rightSide = true },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                label = { Text("Right side") },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(stretch.summary, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(stretch.metaLine() + " · " + stretch.equipment.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(20.dp))
            Text("Targets", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            PillRow(stretch.muscles.map { it.label }, highlightFirst = stretch.primary.size)

            Spacer(Modifier.height(24.dp))
            Text("How to", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            stretch.cues.forEachIndexed { i, cue ->
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.Top) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${i + 1}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(cue, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 3.dp))
                }
            }

            if (stretch.cautions.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Good to know", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        stretch.cautions.forEach {
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(top = 4.dp))
                        }
                        Text(
                            "Stop if you feel sharp pain, numbness or tingling.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Sources: " + stretch.sourceIds.mapNotNull { Sources.byId(it)?.short }.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onOpenSources).padding(vertical = 8.dp),
            )
            Spacer(Modifier.height(16.dp))
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(onClick = { onTry(Route.Player(listOf(stretch.key), stretch.name, if (rightSide) Side.Right else Side.Left)) }, modifier = Modifier.height(52.dp).fillMaxWidth()) {
                Icon(painterResource(R.drawable.ic_play), contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (stretch.sided) "Try it · both sides" else "Try it")
            }
        }
    }
}
