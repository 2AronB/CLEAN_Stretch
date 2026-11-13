package dev.clean.stretch.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.clean.stretch.settings.Palette

/*
 * Seasonal palettes (owner, 2026-11-13). Each has a light and a dark set covering every colour
 * role the app uses, including the surface-container layers behind cards and the player stage.
 * Accent colours on their containers are chosen for readable contrast (dark text on pale fills,
 * white on the strong colours).
 */

private val FallLight = lightColorScheme(
    primary = Color(0xFFA0461C), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCB), onPrimaryContainer = Color(0xFF380D00),
    secondary = Color(0xFF7A5B00), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE08A), onSecondaryContainer = Color(0xFF261A00),
    tertiary = Color(0xFF4E6A3A), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCFEDB4), onTertiaryContainer = Color(0xFF0C2000),
    background = Color(0xFFFFF8F5), onBackground = Color(0xFF221A16),
    surface = Color(0xFFFFF8F5), onSurface = Color(0xFF221A16),
    surfaceVariant = Color(0xFFF4DED5), onSurfaceVariant = Color(0xFF53433D),
    outline = Color(0xFF85736B), outlineVariant = Color(0xFFD8C2B9),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFFEF1EB),
    surfaceContainer = Color(0xFFF8EBE5), surfaceContainerHigh = Color(0xFFF3E5DF),
    surfaceContainerHighest = Color(0xFFEDE0DA), surfaceDim = Color(0xFFE7D7D0), surfaceBright = Color(0xFFFFF8F5),
    inverseSurface = Color(0xFF382E2A), inverseOnSurface = Color(0xFFFFEDE6), inversePrimary = Color(0xFFFFB690),
)

private val FallDark = darkColorScheme(
    primary = Color(0xFFFFB690), onPrimary = Color(0xFF571F00),
    primaryContainer = Color(0xFF7C3410), onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = Color(0xFFE9C349), onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5C4300), onSecondaryContainer = Color(0xFFFFE08A),
    tertiary = Color(0xFFB3D19A), onTertiary = Color(0xFF203610),
    tertiaryContainer = Color(0xFF374F25), onTertiaryContainer = Color(0xFFCFEDB4),
    background = Color(0xFF1A120E), onBackground = Color(0xFFF1DFD8),
    surface = Color(0xFF1A120E), onSurface = Color(0xFFF1DFD8),
    surfaceVariant = Color(0xFF53433D), onSurfaceVariant = Color(0xFFD8C2B9),
    outline = Color(0xFFA08D85), outlineVariant = Color(0xFF53433D),
    surfaceContainerLowest = Color(0xFF140C09), surfaceContainerLow = Color(0xFF221A16),
    surfaceContainer = Color(0xFF271E1A), surfaceContainerHigh = Color(0xFF322824),
    surfaceContainerHighest = Color(0xFF3D332E), surfaceDim = Color(0xFF1A120E), surfaceBright = Color(0xFF42372F),
    inverseSurface = Color(0xFFF1DFD8), inverseOnSurface = Color(0xFF382E2A), inversePrimary = Color(0xFFA0461C),
)

private val SpringLight = lightColorScheme(
    primary = Color(0xFF7454A0), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDDCFF), onPrimaryContainer = Color(0xFF2B0A52),
    secondary = Color(0xFF9C4A63), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E2), onSecondaryContainer = Color(0xFF3E0020),
    tertiary = Color(0xFF3A6A52), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC0EFD5), onTertiaryContainer = Color(0xFF002113),
    background = Color(0xFFFFF7FD), onBackground = Color(0xFF1E1A22),
    surface = Color(0xFFFFF7FD), onSurface = Color(0xFF1E1A22),
    surfaceVariant = Color(0xFFE8E0EC), onSurfaceVariant = Color(0xFF4A4453),
    outline = Color(0xFF7B7484), outlineVariant = Color(0xFFCCC3D3),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFF9F1FB),
    surfaceContainer = Color(0xFFF3EBF5), surfaceContainerHigh = Color(0xFFEEE6EF),
    surfaceContainerHighest = Color(0xFFE8E0EA), surfaceDim = Color(0xFFDFD8E1), surfaceBright = Color(0xFFFFF7FD),
    inverseSurface = Color(0xFF332F37), inverseOnSurface = Color(0xFFF6EEF8), inversePrimary = Color(0xFFD7BAFF),
)

private val SpringDark = darkColorScheme(
    primary = Color(0xFFD7BAFF), onPrimary = Color(0xFF43246E),
    primaryContainer = Color(0xFF5B3C87), onPrimaryContainer = Color(0xFFEDDCFF),
    secondary = Color(0xFFFFB0C8), onSecondary = Color(0xFF5F1135),
    secondaryContainer = Color(0xFF7D334B), onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFFA4D3B9), onTertiary = Color(0xFF063826),
    tertiaryContainer = Color(0xFF22513C), onTertiaryContainer = Color(0xFFC0EFD5),
    background = Color(0xFF16121A), onBackground = Color(0xFFE9E0EA),
    surface = Color(0xFF16121A), onSurface = Color(0xFFE9E0EA),
    surfaceVariant = Color(0xFF4A4453), onSurfaceVariant = Color(0xFFCCC3D3),
    outline = Color(0xFF958E9D), outlineVariant = Color(0xFF4A4453),
    surfaceContainerLowest = Color(0xFF110D14), surfaceContainerLow = Color(0xFF1E1A22),
    surfaceContainer = Color(0xFF231E26), surfaceContainerHigh = Color(0xFF2D2831),
    surfaceContainerHighest = Color(0xFF38333C), surfaceDim = Color(0xFF16121A), surfaceBright = Color(0xFF3C3740),
    inverseSurface = Color(0xFFE9E0EA), inverseOnSurface = Color(0xFF332F37), inversePrimary = Color(0xFF7454A0),
)

private val WinterLight = lightColorScheme(
    primary = Color(0xFF3B5F84), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD2E4FF), onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF535F70), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7), onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF36666F), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCEAF5), onTertiaryContainer = Color(0xFF001F25),
    background = Color(0xFFF8F9FF), onBackground = Color(0xFF191C20),
    surface = Color(0xFFF8F9FF), onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFDFE2EB), onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F), outlineVariant = Color(0xFFC3C6CF),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFF2F3FA),
    surfaceContainer = Color(0xFFECEEF4), surfaceContainerHigh = Color(0xFFE6E8EE),
    surfaceContainerHighest = Color(0xFFE1E2E8), surfaceDim = Color(0xFFD8DAE0), surfaceBright = Color(0xFFF8F9FF),
    inverseSurface = Color(0xFF2E3135), inverseOnSurface = Color(0xFFEFF0F7), inversePrimary = Color(0xFFA5C8F2),
)

private val WinterDark = darkColorScheme(
    primary = Color(0xFFA5C8F2), onPrimary = Color(0xFF02314F),
    primaryContainer = Color(0xFF21486A), onPrimaryContainer = Color(0xFFD2E4FF),
    secondary = Color(0xFFBBC7DB), onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858), onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFA0CED9), onTertiary = Color(0xFF00363E),
    tertiaryContainer = Color(0xFF1C4E57), onTertiaryContainer = Color(0xFFBCEAF5),
    background = Color(0xFF111418), onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF111418), onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF43474E), onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199), outlineVariant = Color(0xFF43474E),
    surfaceContainerLowest = Color(0xFF0C0E12), surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024), surfaceContainerHigh = Color(0xFF282A2F),
    surfaceContainerHighest = Color(0xFF33353A), surfaceDim = Color(0xFF111418), surfaceBright = Color(0xFF37393E),
    inverseSurface = Color(0xFFE1E2E8), inverseOnSurface = Color(0xFF2E3135), inversePrimary = Color(0xFF3B5F84),
)

private val SummerLight = lightColorScheme(
    primary = Color(0xFF00696B), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9CF1F0), onPrimaryContainer = Color(0xFF002020),
    secondary = Color(0xFF7A5900), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDEA3), onSecondaryContainer = Color(0xFF261900),
    tertiary = Color(0xFFB02F4A), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9DC), onTertiaryContainer = Color(0xFF400011),
    background = Color(0xFFF4FBFA), onBackground = Color(0xFF161D1D),
    surface = Color(0xFFF4FBFA), onSurface = Color(0xFF161D1D),
    surfaceVariant = Color(0xFFDAE5E3), onSurfaceVariant = Color(0xFF3F4948),
    outline = Color(0xFF6F7979), outlineVariant = Color(0xFFBEC9C8),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFEEF5F4),
    surfaceContainer = Color(0xFFE9EFEE), surfaceContainerHigh = Color(0xFFE3E9E9),
    surfaceContainerHighest = Color(0xFFDDE4E3), surfaceDim = Color(0xFFD5DBDA), surfaceBright = Color(0xFFF4FBFA),
    inverseSurface = Color(0xFF2B3231), inverseOnSurface = Color(0xFFECF2F1), inversePrimary = Color(0xFF5DD8D7),
)

private val SummerDark = darkColorScheme(
    primary = Color(0xFF5DD8D7), onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF004F50), onPrimaryContainer = Color(0xFF9CF1F0),
    secondary = Color(0xFFF5BF48), onSecondary = Color(0xFF402D00),
    secondaryContainer = Color(0xFF5C4200), onSecondaryContainer = Color(0xFFFFDEA3),
    tertiary = Color(0xFFFFB2B9), onTertiary = Color(0xFF67001F),
    tertiaryContainer = Color(0xFF8F1534), onTertiaryContainer = Color(0xFFFFD9DC),
    background = Color(0xFF0E1514), onBackground = Color(0xFFDDE4E3),
    surface = Color(0xFF0E1514), onSurface = Color(0xFFDDE4E3),
    surfaceVariant = Color(0xFF3F4948), onSurfaceVariant = Color(0xFFBEC9C8),
    outline = Color(0xFF889392), outlineVariant = Color(0xFF3F4948),
    surfaceContainerLowest = Color(0xFF090F0F), surfaceContainerLow = Color(0xFF161D1D),
    surfaceContainer = Color(0xFF1A2121), surfaceContainerHigh = Color(0xFF252B2B),
    surfaceContainerHighest = Color(0xFF303636), surfaceDim = Color(0xFF0E1514), surfaceBright = Color(0xFF343A3A),
    inverseSurface = Color(0xFFDDE4E3), inverseOnSurface = Color(0xFF2B3231), inversePrimary = Color(0xFF00696B),
)

private val StretchTypography = Typography().let { base ->
    base.copy(
        displaySmall = base.displaySmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
        headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
        headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

/** Wallpaper colours need Android 12; older phones get the default palette instead. */
val wallpaperColorsAvailable: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/** The colours for [palette] in light or [dark] mode. */
fun colorSchemeFor(context: Context, palette: Palette, dark: Boolean): ColorScheme =
    if (palette == Palette.Wallpaper && wallpaperColorsAvailable) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        seasonalScheme(palette, dark)
    }

/** A built-in palette's colours ([Palette.Wallpaper] falls back to the default). */
internal fun seasonalScheme(palette: Palette, dark: Boolean): ColorScheme = when (palette) {
    Palette.Wallpaper -> seasonalScheme(Palette.DEFAULT, dark)
    Palette.Fall -> if (dark) FallDark else FallLight
    Palette.Spring -> if (dark) SpringDark else SpringLight
    Palette.Winter -> if (dark) WinterDark else WinterLight
    Palette.Summer -> if (dark) SummerDark else SummerLight
}

@Composable
fun StretchTheme(dark: Boolean, palette: Palette, content: @Composable () -> Unit) {
    val scheme = colorSchemeFor(LocalContext.current, palette, dark)
    MaterialTheme(colorScheme = scheme, typography = StretchTypography, content = content)
}
