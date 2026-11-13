package dev.clean.stretch.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.clean.stretch.settings.Palette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteTest {
    @Test
    fun burntFallIsTheDefault() {
        assertEquals(Palette.Fall, Palette.fromName(null))
        assertEquals(Palette.Fall, Palette.fromName("no such palette"))
        assertEquals(Palette.Winter, Palette.fromName("Winter"))
    }

    private fun contrast(a: Color, b: Color): Double {
        val (hi, lo) = listOf(a.luminance(), b.luminance()).sortedDescending()
        return (hi + 0.05) / (lo + 0.05)
    }

    /** Text on every colour pair the app uses reads clearly: at least 4.5:1 (WCAG AA for body text). */
    @Test
    fun everyPaletteKeepsTextReadable() {
        for (palette in Palette.entries.filter { it != Palette.Wallpaper }) for (dark in listOf(false, true)) {
            val s = seasonalScheme(palette, dark)
            val pairs = mapOf(
                "primary" to (s.onPrimary to s.primary),
                "primaryContainer" to (s.onPrimaryContainer to s.primaryContainer),
                "secondary" to (s.onSecondary to s.secondary),
                "secondaryContainer" to (s.onSecondaryContainer to s.secondaryContainer),
                "tertiary" to (s.onTertiary to s.tertiary),
                "tertiaryContainer" to (s.onTertiaryContainer to s.tertiaryContainer),
                "surface" to (s.onSurface to s.surface),
                "surfaceVariant text on card" to (s.onSurfaceVariant to s.surfaceContainerLow),
                "primary text on surface" to (s.primary to s.surface),
            )
            pairs.forEach { (name, pair) ->
                val ratio = contrast(pair.first, pair.second)
                assertTrue("${palette.label} ${if (dark) "dark" else "light"} $name: ${"%.2f".format(ratio)}", ratio >= 4.5)
            }
        }
    }
}
