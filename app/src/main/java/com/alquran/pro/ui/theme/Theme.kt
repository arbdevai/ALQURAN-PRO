package com.alquran.pro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OledBlack,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = TextHighContrast,
    secondary = GoldAccent,
    onSecondary = OledBlack,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = TextHighContrast,
    background = OledBlack,
    onBackground = TextHighContrast,
    surface = DarkSurface,
    onSurface = TextHighContrast,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    surfaceContainer = DarkSurfaceContainer,
    error = DangerRed,
    onError = TextHighContrast
)

@Composable
fun AlQuranProTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = OledBlack.toArgb()
                window.navigationBarColor = OledBlack.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
