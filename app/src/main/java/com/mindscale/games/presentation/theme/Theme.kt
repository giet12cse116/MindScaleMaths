package com.mindscale.games.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentMint,
    secondary = AccentPurple,
    tertiary = AccentAmber,
    background = BackgroundDeep,
    surface = SurfaceCard,
    onPrimary = BackgroundDeep,
    onSecondary = TextPrimary,
    onTertiary = BackgroundDeep,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = SurfaceCardBorder,
    error = AccentRed,
    onError = TextPrimary
)

@Composable
fun MindScaleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
