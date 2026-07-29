package com.example.thecodecup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Monogatari night palette.
val DarkBackground = Color(0xFF121214)
val DarkSurface = Color(0xFF1E1E22)
val DarkPrimary = Color(0xFFE60023)
val DarkSecondary = Color(0xFFFFCC00)
val DarkTertiary = Color(0xFF6B46C1)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFA0A0AB)

// Monogatari golden-sunset palette.
val LightBackground = Color(0xFFFDF8F2)
val LightSurface = Color(0xFFFFFFFF)
val LightPrimary = Color(0xFFD62828)
val LightSecondary = Color(0xFFFFAA00)
val LightTertiary = Color(0xFF8A5EA2)
val LightTextPrimary = Color(0xFF1F1A24)
val LightTextSecondary = Color(0xFF6E6278)

// Compatibility names used by existing composables now resolve semantically,
// so they remain legible in either color scheme.
val CoffeeBlue: Color
    @Composable get() = MaterialTheme.colorScheme.primary
val CoffeeNavy: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground
val CoffeeCard: Color
    @Composable get() = MaterialTheme.colorScheme.surface
val LightBrown: Color
    @Composable get() = MaterialTheme.colorScheme.primary
val DarkBrown: Color
    @Composable get() = MaterialTheme.colorScheme.tertiary
val Gray: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val White: Color
    @Composable get() = MaterialTheme.colorScheme.onPrimary
