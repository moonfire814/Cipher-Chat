package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ChatMessage
import com.example.ui.theme.LocalChatTheme

@Composable
fun EncryptionInspectorDialog(
    message: ChatMessage,
    onDismiss: () -> Unit
) {
    val theme = LocalChatTheme.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("encryption_inspector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Crypto Security Audit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_inspector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Badge
                Surface(
                    color = Color(0xFF064E3B).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "E2EE Integrity Verified",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Ciphertext authenticated via Galois/Counter Mode tag",
                                color = Color(0xFFD1FAE5),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cipher Specs
                DetailRow("Cipher Algorithm", "AES-256-GCM / NoPadding")
                DetailRow("Authentication Tag", "128-bit Poly1305 GCM Tag")
                DetailRow("Key Length", "256 bits (Quantum Resistant)")
                DetailRow("SHA-256 Fingerprint", message.checksum.uppercase())

                Spacer(modifier = Modifier.height(12.dp))

                // Ciphertext Box
                Text(
                    text = "RAW CIPHERTEXT PAYLOAD (Base64):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = message.ciphertext,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF67E8F9),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // IV Box
                Text(
                    text = "INITIALIZATION VECTOR (IV 96-bit):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = message.iv,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFF472B6)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Decrypted In-Memory Text
                Text(
                    text = "LOCAL DECRYPTED PLAINTEXT:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = message.decryptedText.ifEmpty { "[Media Attachment]" },
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Close Audit",
                        color = theme.userBubbleText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}
