package com.gromber05.peco.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),            // green 800
    onPrimary = Color(0xFFFFFFFF),

    primaryContainer = Color(0xFFA8DAB5),   // soft mint
    onPrimaryContainer = Color(0xFF0E2A13),

    secondary = Color(0xFF6D4C41),          // warm brown
    onSecondary = Color(0xFFFFFFFF),

    secondaryContainer = Color(0xFFFFE0B2), // warm sand
    onSecondaryContainer = Color(0xFF2B1B15),

    tertiary = Color(0xFF4E6E5D),           // muted green-gray
    onTertiary = Color(0xFFFFFFFF),

    tertiaryContainer = Color(0xFFCFE9D7),  // pale mint
    onTertiaryContainer = Color(0xFF10231A),

    background = Color(0xFFF6FBF7),         // very light green tint
    onBackground = Color(0xFF18211B),

    surface = Color(0xFFF6FBF7),
    onSurface = Color(0xFF18211B),

    surfaceVariant = Color(0xFFE2EEE6),
    onSurfaceVariant = Color(0xFF3B4A41),

    outline = Color(0xFF6F7F75),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

// --- Dark colors ---
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7EDC93),            // bright mint
    onPrimary = Color(0xFF0B2A13),

    primaryContainer = Color(0xFF1F5A30),   // deep forest
    onPrimaryContainer = Color(0xFFCFEFD8),

    secondary = Color(0xFFFFB59D),          // warm peach
    onSecondary = Color(0xFF3A1D14),

    secondaryContainer = Color(0xFF5A3A2D), // dark warm brown
    onSecondaryContainer = Color(0xFFFFE3DA),

    tertiary = Color(0xFFA9D6C0),           // soft teal-mint
    onTertiary = Color(0xFF103328),

    tertiaryContainer = Color(0xFF274C3E),
    onTertiaryContainer = Color(0xFFCFE9D7),

    background = Color(0xFF0F1511),         // very dark greenish
    onBackground = Color(0xFFE6F1EA),

    surface = Color(0xFF0F1511),
    onSurface = Color(0xFFE6F1EA),

    surfaceVariant = Color(0xFF223128),
    onSurfaceVariant = Color(0xFFB8C8BE),

    outline = Color(0xFF7F9287),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
)

@Composable
fun PecoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}