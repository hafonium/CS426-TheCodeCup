package com.example.thecodecup.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class ThemeController(
    val isDarkMode: Boolean,
    val setDarkMode: (Boolean) -> Unit
)

val LocalThemeController = staticCompositionLocalOf {
    ThemeController(isDarkMode = false, setDarkMode = {})
}

private val ColorWhite = androidx.compose.ui.graphics.Color.White

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkTextPrimary,
    primaryContainer = DarkPrimary,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkBackground,
    tertiary = DarkTertiary,
    onTertiary = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkTextSecondary.copy(alpha = 0.55f),
    outlineVariant = DarkTextSecondary.copy(alpha = 0.35f),
    error = DarkPrimary,
    errorContainer = DarkPrimary.copy(alpha = 0.18f)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = ColorWhite,
    primaryContainer = LightPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = LightTextPrimary,
    secondary = LightSecondary,
    onSecondary = LightTextPrimary,
    secondaryContainer = LightSecondary.copy(alpha = 0.18f),
    onSecondaryContainer = LightTextPrimary,
    tertiary = LightTertiary,
    onTertiary = ColorWhite,
    tertiaryContainer = LightTertiary.copy(alpha = 0.14f),
    onTertiaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextSecondary.copy(alpha = 0.55f),
    outlineVariant = LightTextSecondary.copy(alpha = 0.25f),
    error = LightPrimary,
    errorContainer = LightPrimary.copy(alpha = 0.12f)
)

@Composable
fun TheCodeCupTheme(
    darkTheme: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalThemeController provides ThemeController(darkTheme, onDarkThemeChange)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
