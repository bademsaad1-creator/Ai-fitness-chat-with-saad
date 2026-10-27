package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitPulseDarkColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = Obsidian,
    primaryContainer = NeonEmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = Obsidian,
    secondaryContainer = SurfaceCardHighlight,
    onSecondaryContainer = ElectricCyan,
    tertiary = VividFlame,
    onTertiary = Obsidian,
    background = Obsidian,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = Crimson
)

private val FitPulseLightColorScheme = lightColorScheme(
    primary = NeonEmeraldDark,
    onPrimary = TextPrimary,
    primaryContainer = NeonEmerald,
    onPrimaryContainer = Obsidian,
    secondary = ElectricCyan,
    onSecondary = Obsidian,
    secondaryContainer = ChipBackground,
    onSecondaryContainer = TextPrimary,
    tertiary = VividFlame,
    onTertiary = TextPrimary,
    background = Obsidian, // Maintain dark athletic vibe
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = Crimson
)

@Composable
fun FitPulseTheme(
    darkTheme: Boolean = true, // Default to sleek dark fitness aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitPulseDarkColorScheme else FitPulseLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Obsidian.toArgb()
                window.navigationBarColor = Obsidian.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
