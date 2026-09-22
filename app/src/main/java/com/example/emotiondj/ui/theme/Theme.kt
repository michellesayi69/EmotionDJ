package com.example.emotiondj.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class EmotionDJColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val moodHappy: Color,
    val moodSad: Color,
    val moodTired: Color,
    val moodNeutral: Color,
    val moodFocused: Color,
    val moodCalm: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

val DarkColorPalette = EmotionDJColors(
    background = DarkBackground,
    surface = DarkSurface,
    primary = NeonCyan,
    secondary = NeonPurple,
    moodHappy = HappyColor,
    moodSad = SadColor,
    moodTired = TiredColor,
    moodNeutral = NeutralColor,
    moodFocused = FocusedColor,
    moodCalm = CalmColor,
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.7f)
)

val LightColorPalette = EmotionDJColors(
    background = LightBackground,
    surface = LightSurface,
    primary = NeonCyan.copy(alpha = 0.8f),
    secondary = NeonPurple.copy(alpha = 0.8f),
    moodHappy = HappyColor,
    moodSad = SadColor,
    moodTired = TiredColor,
    moodNeutral = NeutralColor,
    moodFocused = FocusedColor,
    moodCalm = CalmColor,
    textPrimary = LightText,
    textSecondary = LightText.copy(alpha = 0.6f)
)

val LocalEmotionDJColors = staticCompositionLocalOf { DarkColorPalette }

@Composable
fun EmotionDJTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    CompositionLocalProvider(LocalEmotionDJColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            typography = Typography,
            content = content
        )
    }
}
