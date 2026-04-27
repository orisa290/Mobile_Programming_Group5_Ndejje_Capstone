package com.ndejje.lostfoundhub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = White,
    secondary = BrandGold,
    background = LightGrey,
    surface = White,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGold,
    onPrimary = NavyBlueDark,
    secondary = NavyBlue,
    background = DarkBackground,
    surface = DarkSurface,
    error = ErrorRed
)

@Composable
fun LostFoundHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
