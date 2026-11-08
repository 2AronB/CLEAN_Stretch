package dev.clean.stretch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.clean.stretch.R
import dev.clean.stretch.catalog.Stretch
import dev.clean.stretch.figure.Easing
import dev.clean.stretch.figure.MotionPlayer
import dev.clean.stretch.ui.figure.FigureView
import dev.clean.stretch.ui.figure.figureColors
import dev.clean.stretch.ui.figure.rememberFigureClock

/** Large title used at the top of each tab, like stock Android apps. */
@Composable
fun ScreenTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.fillMaxWidth().statusBarsPadding().padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 12.dp)) {
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
        }
        Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}

/** Compact top bar with a back arrow for pushed screens. */
@Composable
fun BackBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier, actions: @Composable () -> Unit = {}) {
    Row(
        modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) { Icon(painterResource(R.drawable.ic_back), contentDescription = "Back") }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(start = 4.dp),
        )
        actions()
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

@Composable
fun Pill(text: String, container: Color = MaterialTheme.colorScheme.secondaryContainer, content: Color = MaterialTheme.colorScheme.onSecondaryContainer) {
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(50)) {
        Text(text, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PillRow(items: List<String>, highlightFirst: Int = 0) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { i, text ->
            if (i < highlightFirst) {
                Pill(text, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
            } else {
                Pill(text)
            }
        }
    }
}

/** A stretch's looping demo animation (enter, hold with breathing, return). */
@Composable
fun AnimatedStretch(
    stretch: Stretch,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    background: Color = MaterialTheme.colorScheme.surface,
) {
    val time by rememberFigureClock(running = !reducedMotion)
    val frame = remember(stretch.key) { MotionPlayer.frameFor(stretch.motion) }
    FigureView(
        pose = MotionPlayer.demoPose(stretch.motion, time, reducedMotion),
        frame = frame,
        highlight = stretch.highlight,
        mirrored = mirrored,
        prop = stretch.prop,
        glow = if (reducedMotion) 0f else Easing.breath(time),
        colors = figureColors(background),
        modifier = modifier,
        description = "${stretch.name} demonstration",
    )
}

/** Still thumbnail of the stretch position, for lists. */
@Composable
fun StretchThumbnail(stretch: Stretch, modifier: Modifier = Modifier, background: Color = MaterialTheme.colorScheme.surface) {
    val frame = remember(stretch.key) { MotionPlayer.frameFor(stretch.motion) }
    FigureView(
        pose = stretch.motion.stretch,
        frame = frame,
        highlight = stretch.highlight,
        prop = stretch.prop,
        colors = figureColors(background),
        modifier = modifier,
        description = stretch.name,
    )
}

fun Stretch.metaLine(): String = buildList {
    add(position.label)
    add(kind.label)
    add(if (sided) "Each side" else "Both sides")
}.joinToString(" · ")
