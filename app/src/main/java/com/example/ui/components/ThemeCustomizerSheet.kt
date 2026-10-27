package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeCustomizerSheet(
    activeTheme: ThemePresetId,
    onSelectTheme: (ThemePresetId) -> Unit,
    activeBubbleStyle: BubbleStyle,
    onSelectBubbleStyle: (BubbleStyle) -> Unit,
    activeWallpaperStyle: WallpaperStyle,
    onSelectWallpaperStyle: (WallpaperStyle) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val currentThemeConfig = ThemePresets.getById(activeTheme)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = currentThemeConfig.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) },
        modifier = Modifier.testTag("theme_customizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = currentThemeConfig.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aesthetic Theme Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 1: THEME PRESETS
            Text(
                text = "COLOR PALETTES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = currentThemeConfig.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ThemePresets.all) { preset ->
                    val isSelected = preset.id == activeTheme
                    Surface(
                        color = preset.surface,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) preset.primary else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .width(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectTheme(preset.id) }
                            .testTag("theme_preset_${preset.id.name}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Swatches
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(preset.primary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(preset.secondary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(preset.userBubble)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = preset.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) preset.primary else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: BUBBLE STYLES
            Text(
                text = "CHAT BUBBLE GEOMETRY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = currentThemeConfig.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val styles = listOf(
                    BubbleStyle.SMOOTH to "Smooth",
                    BubbleStyle.SHARP_CORNER to "Sharp",
                    BubbleStyle.MINIMAL_ROUNDED to "Minimal",
                    BubbleStyle.CYBER_ANGULAR to "Cyber"
                )

                styles.forEach { (style, label) ->
                    val isSelected = style == activeBubbleStyle
                    Surface(
                        color = if (isSelected) currentThemeConfig.primary.copy(alpha = 0.2f) else currentThemeConfig.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) currentThemeConfig.primary else Color.White.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectBubbleStyle(style) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) currentThemeConfig.primary else Color.White,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: WALLPAPER TEXTURE
            Text(
                text = "BACKGROUND ATMOSPHERE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = currentThemeConfig.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val wallpapers = listOf(
                    WallpaperStyle.CYBER_CONSTELLATION to "Constellation",
                    WallpaperStyle.AURORA_GRADIENT to "Aurora Glow",
                    WallpaperStyle.DEEP_SLATE to "Slate Mesh",
                    WallpaperStyle.PURE_AMOLED to "AMOLED Pure"
                )

                wallpapers.forEach { (wp, label) ->
                    val isSelected = wp == activeWallpaperStyle
                    Surface(
                        color = if (isSelected) currentThemeConfig.primary.copy(alpha = 0.2f) else currentThemeConfig.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) currentThemeConfig.primary else Color.White.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectWallpaperStyle(wp) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) currentThemeConfig.primary else Color.White,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 4: FONT SCALING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = currentThemeConfig.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Message Font Scale",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Text(
                    text = "${(fontScale * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentThemeConfig.primary
                )
            }

            Slider(
                value = fontScale,
                onValueChange = onFontScaleChange,
                valueRange = 0.85f..1.3f,
                steps = 3,
                colors = SliderDefaults.colors(
                    thumbColor = currentThemeConfig.primary,
                    activeTrackColor = currentThemeConfig.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
