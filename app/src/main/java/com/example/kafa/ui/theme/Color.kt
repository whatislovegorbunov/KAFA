package com.example.kafa.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class KafaColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accent: Color,
    val accentBackground: Color,
    val warmHighlight: Color,
    val coolHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
)

internal val LightKafaColors = KafaColors(
    screenBackground = White,
    cardBackground = OffWhite,
    accent = Teal,
    accentBackground = LightTeal,
    warmHighlight = Sand,
    coolHighlight = Lavender,
    textPrimary = DarkGreen,
    textSecondary = MutedGreen,
    border = PaleGreen,
)

internal val LocalKafaColors = staticCompositionLocalOf { LightKafaColors }
