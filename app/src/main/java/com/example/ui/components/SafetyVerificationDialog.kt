package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ChatConversation
import com.example.ui.theme.LocalChatTheme

@Composable
fun SafetyVerificationDialog(
    conversation: ChatConversation,
    onToggleVerified: () -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalChatTheme.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("safety_verification_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verify Safety Number",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_safety_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Compare the 60-digit fingerprint below with ${conversation.name}'s device to verify end-to-end encryption integrity.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated High-Density Cryptographic QR Matrix
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 15
                        val cellW = size.width / gridSize
                        val cellH = size.height / gridSize
                        val seed = conversation.handle.hashCode()

                        for (r in 0 until gridSize) {
                            for (c in 0 until gridSize) {
                                // Corner anchors
                                val isAnchor = (r < 4 && c < 4) || (r < 4 && c >= gridSize - 4) || (r >= gridSize - 4 && c < 4)
                                val filled = if (isAnchor) {
                                    val localR = if (r >= gridSize - 4) r - (gridSize - 4) else r
                                    val localC = if (c >= gridSize - 4) c - (gridSize - 4) else c
                                    localR == 0 || localR == 3 || localC == 0 || localC == 3 || (localR == 1 && localC == 1) || (localR == 2 && localC == 2)
                                } else {
                                    ((seed * 31 + r * 17 + c * 23) and 0x7F) % 2 == 0
                                }
                                if (filled) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = Offset(c * cellW, r * cellH),
                                        size = Size(cellW, cellH)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 60-digit number formatted in clean monospace chunks
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = conversation.safetyNumber,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        lineHeight = 22.sp,
                        color = theme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Switch Row
                Surface(
                    color = theme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (conversation.isSafetyVerified) Color(0xFF34D399) else Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mark as Verified",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Switch(
                            checked = conversation.isSafetyVerified,
                            onCheckedChange = { onToggleVerified() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = theme.primary,
                                checkedTrackColor = theme.primary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("toggle_verified_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Done",
                        color = theme.userBubbleText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
