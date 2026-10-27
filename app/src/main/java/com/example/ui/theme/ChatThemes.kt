package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.R

enum class ThemePresetId {
    CYBERPUNK,
    MIDNIGHT_VELVET,
    EMERALD_VAULT,
    ROSE_PASTEL,
    NORDIC_SLATE,
    SOLAR_FLARE
}

enum class BubbleStyle {
    SMOOTH,
    SHARP_CORNER,
    MINIMAL_ROUNDED,
    CYBER_ANGULAR
}

enum class WallpaperStyle {
    CYBER_CONSTELLATION,
    PURE_AMOLED,
    AURORA_GRADIENT,
    DEEP_SLATE
}

data class ChatThemeConfig(
    val id: ThemePresetId,
    val name: String,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val userBubble: Color,
    val userBubbleText: Color,
    val peerBubble: Color,
    val peerBubbleText: Color,
    val accentGlow: Color,
    val wallpaperDrawable: Int = R.drawable.chat_bg_cyber
)

object ThemePresets {
    val Cyberpunk = ChatThemeConfig(
        id = ThemePresetId.CYBERPUNK,
        name = "Cyberpunk Neon",
        primary = Color(0xFF00F0FF),
        primaryContainer = Color(0xFF003840),
        secondary = Color(0xFFFF007F),
        background = Color(0xFF090D16),
        surface = Color(0xFF111827),
        surfaceVariant = Color(0xFF1E293B),
        userBubble = Color(0xFF00E5FF),
        userBubbleText = Color(0xFF031019),
        peerBubble = Color(0xFF1F293D),
        peerBubbleText = Color(0xFFF1F5F9),
        accentGlow = Color(0xFF00F0FF)
    )

    val MidnightVelvet = ChatThemeConfig(
        id = ThemePresetId.MIDNIGHT_VELVET,
        name = "Midnight Velvet",
        primary = Color(0xFFA855F7),
        primaryContainer = Color(0xFF3B0764),
        secondary = Color(0xFFC084FC),
        background = Color(0xFF0E0B1F),
        surface = Color(0xFF171330),
        surfaceVariant = Color(0xFF231C4A),
        userBubble = Color(0xFF9333EA),
        userBubbleText = Color(0xFFFAF5FF),
        peerBubble = Color(0xFF221A45),
        peerBubbleText = Color(0xFFE9D5FF),
        accentGlow = Color(0xFFA855F7)
    )

    val EmeraldVault = ChatThemeConfig(
        id = ThemePresetId.EMERALD_VAULT,
        name = "Emerald Vault",
        primary = Color(0xFF10B981),
        primaryContainer = Color(0xFF064E3B),
        secondary = Color(0xFF34D399),
        background = Color(0xFF09140E),
        surface = Color(0xFF102318),
        surfaceVariant = Color(0xFF1B3828),
        userBubble = Color(0xFF059669),
        userBubbleText = Color(0xFFECFDF5),
        peerBubble = Color(0xFF152C1E),
        peerBubbleText = Color(0xFFD1FAE5),
        accentGlow = Color(0xFF10B981)
    )

    val RosePastel = ChatThemeConfig(
        id = ThemePresetId.ROSE_PASTEL,
        name = "Rose Quartz",
        primary = Color(0xFFFB7185),
        primaryContainer = Color(0xFF4C0519),
        secondary = Color(0xFFFDA4AF),
        background = Color(0xFF160D12),
        surface = Color(0xFF24141E),
        surfaceVariant = Color(0xFF3B1E2E),
        userBubble = Color(0xFFE11D48),
        userBubbleText = Color(0xFFFFF1F2),
        peerBubble = Color(0xFF2E1926),
        peerBubbleText = Color(0xFFFFE4E6),
        accentGlow = Color(0xFFFB7185)
    )

    val NordicSlate = ChatThemeConfig(
        id = ThemePresetId.NORDIC_SLATE,
        name = "Nordic Slate",
        primary = Color(0xFF38BDF8),
        primaryContainer = Color(0xFF0C4A6E),
        secondary = Color(0xFF94A3B8),
        background = Color(0xFF0B0F17),
        surface = Color(0xFF141C2B),
        surfaceVariant = Color(0xFF1E293B),
        userBubble = Color(0xFF0284C7),
        userBubbleText = Color(0xFFF0F9FF),
        peerBubble = Color(0xFF1E293B),
        peerBubbleText = Color(0xFFF8FAFC),
        accentGlow = Color(0xFF38BDF8)
    )

    val SolarFlare = ChatThemeConfig(
        id = ThemePresetId.SOLAR_FLARE,
        name = "Solar Flare",
        primary = Color(0xFFF59E0B),
        primaryContainer = Color(0xFF78350F),
        secondary = Color(0xFFFBBF24),
        background = Color(0xFF170E07),
        surface = Color(0xFF26160A),
        surfaceVariant = Color(0xFF3D2310),
        userBubble = Color(0xFFD97706),
        userBubbleText = Color(0xFFFFFBEB),
        peerBubble = Color(0xFF2C190D),
        peerBubbleText = Color(0xFFFEF3C7),
        accentGlow = Color(0xFFF59E0B)
    )

    val all = listOf(
        Cyberpunk,
        MidnightVelvet,
        EmeraldVault,
        RosePastel,
        NordicSlate,
        SolarFlare
    )

    fun getById(id: ThemePresetId): ChatThemeConfig {
        return all.firstOrNull { it.id == id } ?: Cyberpunk
    }

    fun getBubbleShape(style: BubbleStyle, isMe: Boolean): RoundedCornerShape {
        return when (style) {
            BubbleStyle.SMOOTH -> {
                if (isMe) RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
                else RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
            }
            BubbleStyle.SHARP_CORNER -> {
                if (isMe) RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                else RoundedCornerShape(topStart = 2.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
            }
            BubbleStyle.MINIMAL_ROUNDED -> RoundedCornerShape(12.dp)
            BubbleStyle.CYBER_ANGULAR -> {
                if (isMe) RoundedCornerShape(topStart = 14.dp, topEnd = 0.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
                else RoundedCornerShape(topStart = 0.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
            }
        }
    }
}
