package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalChatTheme

@Composable
fun DisguiseCalculatorDialog(
    onUnlockSuccess: () -> Unit,
    onVerifyPin: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val theme = LocalChatTheme.current
    var isStealthMode by remember { mutableStateOf(false) } // Default to sleek PIN keypad with disguise mode toggle
    var pinInput by remember { mutableStateOf("") }
    var calcDisplay by remember { mutableStateOf("0") }
    var calcHistory by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("disguise_vault_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top bar with mode switch & close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isStealthMode = !isStealthMode }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isStealthMode) Icons.Default.VisibilityOff else Icons.Default.Lock,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isStealthMode) "Stealth Calculator" else "Secret Vault Access",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_vault_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isStealthMode) {
                    // CALCULATOR DISGUISE MODE
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = calcHistory,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = calcDisplay,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calculator Keypad
                    val buttons = listOf(
                        listOf("C", "±", "%", "÷"),
                        listOf("7", "8", "9", "×"),
                        listOf("4", "5", "6", "-"),
                        listOf("1", "2", "3", "+"),
                        listOf("0", ".", "⌫", "=")
                    )

                    buttons.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row.forEach { key ->
                                val isOperator = key in listOf("÷", "×", "-", "+", "=")
                                val isSpecial = key in listOf("C", "±", "%", "⌫")
                                val btnBg = when {
                                    key == "=" -> theme.primary
                                    isOperator -> theme.surfaceVariant
                                    isSpecial -> Color.White.copy(alpha = 0.1f)
                                    else -> theme.surface.copy(alpha = 0.9f)
                                }
                                val textColor = when {
                                    key == "=" -> theme.userBubbleText
                                    isOperator -> theme.primary
                                    else -> Color.White
                                }

                                Surface(
                                    color = btnBg,
                                    shape = CircleShape,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            when (key) {
                                                "C" -> {
                                                    calcDisplay = "0"
                                                    calcHistory = ""
                                                }
                                                "⌫" -> {
                                                    calcDisplay = if (calcDisplay.length > 1) calcDisplay.dropLast(1) else "0"
                                                }
                                                "=" -> {
                                                    // Check if secret PIN matches
                                                    if (onVerifyPin(calcDisplay)) {
                                                        onUnlockSuccess()
                                                    } else {
                                                        // Fallback to simple calculation
                                                        calcHistory = "$calcDisplay ="
                                                        calcDisplay = "0"
                                                    }
                                                }
                                                "+", "-", "×", "÷" -> {
                                                    calcHistory = "$calcDisplay $key"
                                                    calcDisplay = "0"
                                                }
                                                else -> {
                                                    calcDisplay = if (calcDisplay == "0") key else calcDisplay + key
                                                }
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = key,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tip: Enter secret PIN (default: 1234) and tap '=' to unlock.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )

                } else {
                    // SLEEK PIN KEYPAD MODE
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = theme.primary,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter Vault PIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Access encrypted and hidden conversations",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // PIN Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 4) {
                            val filled = i < pinInput.length
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (filled) theme.primary else Color.White.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        1.dp,
                                        if (filled) theme.primary else Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            fontSize = 12.sp,
                            color = Color(0xFFF87171),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Keypad 1-9, 0, Backspace
                    val keyRows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("Stealth", "0", "DEL")
                    )

                    keyRows.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { k ->
                                Surface(
                                    color = if (k.isEmpty()) Color.Transparent else theme.surfaceVariant.copy(alpha = 0.7f),
                                    shape = CircleShape,
                                    border = if (k.isEmpty()) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .clickable(enabled = k.isNotEmpty()) {
                                            errorMessage = null
                                            when (k) {
                                                "DEL" -> {
                                                    if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                                }
                                                "Stealth" -> {
                                                    isStealthMode = true
                                                }
                                                else -> {
                                                    if (pinInput.length < 4) {
                                                        val next = pinInput + k
                                                        pinInput = next
                                                        if (next.length == 4) {
                                                            if (onVerifyPin(next)) {
                                                                onUnlockSuccess()
                                                            } else {
                                                                errorMessage = "Incorrect PIN. Default is 1234."
                                                                pinInput = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("pin_key_$k")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (k == "DEL") {
                                            Icon(
                                                imageVector = Icons.Default.Backspace,
                                                contentDescription = "Delete",
                                                tint = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (k == "Stealth") {
                                            Text(
                                                text = "Calc",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = theme.primary
                                            )
                                        } else {
                                            Text(
                                                text = k,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Default PIN: 1234",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}
