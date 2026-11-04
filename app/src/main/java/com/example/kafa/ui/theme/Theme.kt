package com.example.kafa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = with(LightKafaColors) {
    lightColorScheme(
        primary = accent,
        onPrimary = White,
        primaryContainer = accentBackground,
        onPrimaryContainer = textPrimary,
        secondary = textSecondary,
        onSecondary = White,
        secondaryContainer = warmHighlight,
        onSecondaryContainer = textPrimary,
        tertiary = textSecondary,
        onTertiary = White,
        tertiaryContainer = coolHighlight,
        onTertiaryContainer = textPrimary,
        background = screenBackground,
        onBackground = textPrimary,
        surface = cardBackground,
        onSurface = textPrimary,
        surfaceVariant = cardBackground,
        onSurfaceVariant = textSecondary,
        surfaceBright = screenBackground,
        surfaceDim = border,
        surfaceContainerLowest = screenBackground,
        surfaceContainerLow = cardBackground,
        surfaceContainer = cardBackground,
        surfaceContainerHigh = cardBackground,
        surfaceContainerHighest = border,
        outline = textSecondary,
        outlineVariant = border,
        inverseSurface = textPrimary,
        inverseOnSurface = cardBackground,
        inversePrimary = accentBackground,
    )
}

object KafaTheme {
    val colors: KafaColors
        @Composable
        get() = LocalKafaColors.current
}

@Composable
fun KafaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalKafaColors provides LightKafaColors) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
