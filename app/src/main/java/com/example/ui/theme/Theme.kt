package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShieldPrimary,
    onPrimary = Color.White,
    primaryContainer = ShieldCardSurface,
    onPrimaryContainer = Color.White,
    secondary = ShieldSecondary,
    onSecondary = Color.White,
    secondaryContainer = ShieldCardSurface,
    onSecondaryContainer = Color.White,
    tertiary = ShieldTertiary,
    onTertiary = Color.Black,
    background = ShieldDarkBg,
    onBackground = TextPrimary,
    surface = ShieldCardBg,
    onSurface = TextPrimary,
    surfaceVariant = ShieldCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = ShieldCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ShieldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFECEF),
    onPrimaryContainer = ShieldPrimary,
    secondary = ShieldSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0EFFF),
    onSecondaryContainer = ShieldSecondary,
    tertiary = ShieldTertiary,
    onTertiary = Color.Black,
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF131722),
    surface = Color.White,
    onSurface = Color(0xFF131722),
    surfaceVariant = Color(0xFFEFF2F8),
    onSurfaceVariant = Color(0xFF56627A),
    outline = Color(0xFFDDE2ED)
)

@Composable
fun ReelShieldTheme(
    darkTheme: Boolean = true, // Default to sleek focus dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
