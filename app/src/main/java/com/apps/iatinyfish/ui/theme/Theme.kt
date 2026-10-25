package com.apps.iatinyfish.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Ocean,
    onPrimary = Color.White,
    primaryContainer = SeaFoam,
    onPrimaryContainer = OceanDark,
    secondary = Color(0xFF166582),
    secondaryContainer = Color(0xFFE0F5FC),
    tertiary = Color(0xFF0F766E),
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEAF4F7),
    onSurfaceVariant = Color(0xFF52616B),
    outlineVariant = Color(0xFFC9D9DE),
)

private val DarkColorScheme = darkColorScheme(
    primary = Aqua,
    onPrimary = Night,
    primaryContainer = OceanDark,
    onPrimaryContainer = SeaFoam,
    secondary = Color(0xFF7DD3FC),
    secondaryContainer = Color(0xFF12394D),
    tertiary = Color(0xFF5EEAD4),
    background = Night,
    onBackground = Color(0xFFE6F1F5),
    surface = NightSurface,
    onSurface = Color(0xFFE6F1F5),
    surfaceVariant = Color(0xFF163648),
    onSurfaceVariant = Color(0xFFA9BDC6),
    outlineVariant = Color(0xFF2D4A5A),
)

@Composable
fun IATinyFishTheme(content: @Composable () -> Unit) {
    val scheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
}
