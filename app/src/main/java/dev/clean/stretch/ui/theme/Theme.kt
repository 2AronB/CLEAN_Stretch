package dev.clean.stretch.ui.theme

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

private val SageLight = lightColorScheme(
    primary = Color(0xFF2B6A5C), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB4EFDD), onPrimaryContainer = Color(0xFF002019),
    secondary = Color(0xFF4A635C), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8DF), onSecondaryContainer = Color(0xFF06201A),
    tertiary = Color(0xFF9A4A33), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBD0), onTertiaryContainer = Color(0xFF3A0B00),
    background = Color(0xFFF7FAF8), onBackground = Color(0xFF171D1B),
    surface = Color(0xFFF7FAF8), onSurface = Color(0xFF171D1B),
    surfaceVariant = Color(0xFFDBE5E0), onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975), outlineVariant = Color(0xFFBFC9C4),
)

private val SageDark = darkColorScheme(
    primary = Color(0xFF98D3C2), onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF0B5144), onPrimaryContainer = Color(0xFFB4EFDD),
    secondary = Color(0xFFB1CCC3), onSecondary = Color(0xFF1C352F),
    secondaryContainer = Color(0xFF334B45), onSecondaryContainer = Color(0xFFCCE8DF),
    tertiary = Color(0xFFFFB59F), onTertiary = Color(0xFF5D1A08),
    tertiaryContainer = Color(0xFF7B331E), onTertiaryContainer = Color(0xFFFFDBD0),
    background = Color(0xFF0F1513), onBackground = Color(0xFFDEE4E1),
    surface = Color(0xFF0F1513), onSurface = Color(0xFFDEE4E1),
    surfaceVariant = Color(0xFF3F4945), onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF89938F), outlineVariant = Color(0xFF3F4945),
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

@Composable
fun StretchTheme(dark: Boolean, wallpaperColors: Boolean, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val scheme: ColorScheme = when {
        wallpaperColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> SageDark
        else -> SageLight
    }
    MaterialTheme(colorScheme = scheme, typography = StretchTypography, content = content)
}
