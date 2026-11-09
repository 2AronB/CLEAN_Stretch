package dev.clean.stretch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import dev.clean.stretch.catalog.Equipment
import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Position
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.plan.LevelFilter
import dev.clean.stretch.plan.LibraryFilter
import dev.clean.stretch.plan.MoveType
import dev.clean.stretch.plan.Profile

/**
 * Stretch library with search and filters (plan/LibraryFilter.kt). Search words all have to match
 * ("easy hamstring", "roller calves"); quick chips cover common needs; "More filters" opens every
 * group. Results are easiest first, moves that mainly target a chosen muscle before others.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(onOpen: (String) -> Unit, profile: Profile?) {
    var filterText by rememberSaveable { mutableStateOf(LibraryFilter().encode()) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val filter = LibraryFilter.decode(filterText)
    fun update(f: LibraryFilter) { filterText = f.encode() }
    fun <T> Set<T>.toggle(item: T) = if (item in this) this - item else this + item

    val shown = filter.apply(SampleCatalog.all, profile)
    val moreCount = listOf(filter.regions, filter.muscles, filter.positions, filter.equipment).count { it.isNotEmpty() } +
        (if (LevelFilter.Harder in filter.levels) 1 else 0)

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            ScreenTitle(
                "Stretches",
                subtitle = if (filter.activeCount == 0) "${SampleCatalog.all.size} in the library" else "${shown.size} of ${SampleCatalog.all.size} shown",
            )
        }
        item {
            OutlinedTextField(
                value = filter.query,
                onValueChange = { update(filter.copy(query = it.take(60))) },
                singleLine = true,
                placeholder = { Text("Search, e.g. easy hamstring, roller") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 4.dp),
            ) {
                items(MoveType.entries) { t ->
                    FilterChip(selected = t in filter.types, onClick = { update(filter.copy(types = filter.types.toggle(t))) }, label = { Text(t.label) })
                }
                item {
                    FilterChip(
                        selected = LevelFilter.Easier in filter.levels,
                        onClick = { update(filter.copy(levels = filter.levels.toggle(LevelFilter.Easier))) },
                        label = { Text("Easier") },
                    )
                }
                if (profile != null) {
                    item { FilterChip(selected = filter.fitsMe, onClick = { update(filter.copy(fitsMe = !filter.fitsMe)) }, label = { Text("Fits my limits") }) }
                }
                item {
                    FilterChip(
                        selected = expanded || moreCount > 0,
                        onClick = { expanded = !expanded },
                        label = { Text(if (moreCount > 0) "More filters ($moreCount)" else "More filters") },
                    )
                }
            }
        }
        if (expanded) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        ChipGroup("Body area", Region.entries, { it.label }, filter.regions) { update(filter.copy(regions = filter.regions.toggle(it))) }
                        ChipGroup("Muscle", Muscle.entries, { it.label }, filter.muscles) { update(filter.copy(muscles = filter.muscles.toggle(it))) }
                        ChipGroup("Position", Position.entries, { it.label }, filter.positions) { update(filter.copy(positions = filter.positions.toggle(it))) }
                        ChipGroup("Equipment", Equipment.entries.filter { e -> SampleCatalog.all.any { it.equipment == e } }, { it.label }, filter.equipment) {
                            update(filter.copy(equipment = filter.equipment.toggle(it)))
                        }
                        ChipGroup("Level", LevelFilter.entries, { it.label }, filter.levels) { update(filter.copy(levels = filter.levels.toggle(it))) }
                    }
                }
            }
        }
        if (filter.activeCount > 0) {
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { update(LibraryFilter()) }) { Text("Clear all") }
                }
            }
        }
        items(shown, key = { it.key }) { s ->
            val bg = MaterialTheme.colorScheme.surfaceContainerLow
            Surface(
                color = bg,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth().clickable { onOpen(s.key) },
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(18.dp)) {
                        StretchThumbnail(s, background = MaterialTheme.colorScheme.surfaceContainerHighest, modifier = Modifier.size(width = 104.dp, height = 84.dp).padding(6.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(s.primary.joinToString { it.label }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        Text(
                            s.metaLine() + (if (s.level >= 2) " · Harder" else "") + (if (s.equipment != Equipment.None) " · ${s.equipment.label}" else ""),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (shown.isEmpty()) {
            item {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "Nothing matches all of these. Try removing a filter or a word.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { update(LibraryFilter()) }) { Text("Clear all") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun <T> ChipGroup(title: String, options: List<T>, label: (T) -> String, selected: Set<T>, onToggle: (T) -> Unit) {
    Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { o -> FilterChip(selected = o in selected, onClick = { onToggle(o) }, label = { Text(label(o)) }) }
    }
}
