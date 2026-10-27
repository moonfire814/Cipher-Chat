package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalChatTheme = staticCompositionLocalOf { ThemePresets.Cyberpunk }
val LocalBubbleStyle = staticCompositionLocalOf { BubbleStyle.SMOOTH }
val LocalWallpaperStyle = staticCompositionLocalOf { WallpaperStyle.CYBER_CONSTELLATION }
val LocalFontSizeScale = staticCompositionLocalOf { 1.0f }

@Composable
fun CipherChatTheme(
    activeTheme: ChatThemeConfig = ThemePresets.Cyberpunk,
    bubbleStyle: BubbleStyle = BubbleStyle.SMOOTH,
    wallpaperStyle: WallpaperStyle = WallpaperStyle.CYBER_CONSTELLATION,
    fontSizeScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = activeTheme.primary,
        onPrimary = activeTheme.userBubbleText,
        primaryContainer = activeTheme.primaryContainer,
        secondary = activeTheme.secondary,
        background = activeTheme.background,
        onBackground = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
        surface = activeTheme.surface,
        onSurface = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
        surfaceVariant = activeTheme.surfaceVariant,
        onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF94A3B8)
    )

    CompositionLocalProvider(
        LocalChatTheme provides activeTheme,
        LocalBubbleStyle provides bubbleStyle,
        LocalWallpaperStyle provides wallpaperStyle,
        LocalFontSizeScale provides fontSizeScale
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
