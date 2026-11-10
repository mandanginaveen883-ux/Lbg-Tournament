package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GamingDarkColorScheme = darkColorScheme(
    primary = EsportsRed,
    onPrimary = Color.White,
    primaryContainer = EsportsRedContainer,
    onPrimaryContainer = Color(0xFFFFD8DC),
    secondary = TournamentGold,
    onSecondary = Color.Black,
    secondaryContainer = TournamentGoldContainer,
    onSecondaryContainer = Color(0xFFFFEEBA),
    tertiary = CyberCyan,
    onTertiary = Color.Black,
    tertiaryContainer = CyberCyanContainer,
    onTertiaryContainer = Color(0xFFBAF7FF),
    background = GamingBlack,
    onBackground = TextPrimary,
    surface = GamingDarkCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = GamingCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = GamingBorder,
    outlineVariant = GamingBorderGlow,
    error = EsportsRed,
    onError = Color.White
)

@Composable
fun LbgArmyTournamentTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GamingDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LbgArmyTournamentTheme(content = content)
}
