package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalChatTheme

@Composable
fun NewChatDialog(
    initialIsHidden: Boolean = false,
    onCreateChat: (name: String, handle: String, isHidden: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalChatTheme.current
    var name by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var isHidden by remember { mutableStateOf(initialIsHidden) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("new_chat_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHidden) "New Hidden Vault Chat" else "New Encrypted Chat",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Contact Name (e.g. Maya Lin)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = theme.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_chat_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it; errorMessage = null },
                    label = { Text("Contact Handle (e.g. @mayalin)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = theme.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_chat_handle_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Vault Hidden Switch
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isHidden) Color(0xFFA855F7) else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Secret Vault Chat",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Hidden from main list, locked behind PIN",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Switch(
                            checked = isHidden,
                            onCheckedChange = { isHidden = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFA855F7),
                                checkedTrackColor = Color(0xFFA855F7).copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("toggle_new_chat_hidden_switch")
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFF87171),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Please enter a contact name"
                        } else {
                            val finalHandle = if (handle.isBlank()) "@${name.lowercase().replace(" ", "_")}" else handle
                            onCreateChat(name, finalHandle, isHidden)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("create_chat_confirm_button")
                ) {
                    Text(
                        text = "Establish Encrypted Channel",
                        color = theme.userBubbleText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
