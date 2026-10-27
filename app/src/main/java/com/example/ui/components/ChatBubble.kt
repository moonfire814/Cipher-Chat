package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ChatMessage
import com.example.data.MediaType
import com.example.data.MessageStatus
import com.example.ui.theme.LocalBubbleStyle
import com.example.ui.theme.LocalChatTheme
import com.example.ui.theme.LocalFontSizeScale
import com.example.ui.theme.ThemePresets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(
    message: ChatMessage,
    onInspectEncryption: (ChatMessage) -> Unit,
    onPhotoClick: (String, String) -> Unit,
    onReactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatTheme.current
    val bubbleStyle = LocalBubbleStyle.current
    val fontScale = LocalFontSizeScale.current
    val isMe = message.isMe

    val bubbleShape = remember(bubbleStyle, isMe) {
        ThemePresets.getBubbleShape(bubbleStyle, isMe)
    }

    val bubbleColor = if (isMe) theme.userBubble else theme.peerBubble
    val textColor = if (isMe) theme.userBubbleText else theme.peerBubbleText

    var showActionMenu by remember { mutableStateOf(false) }

    // Live disappearing countdown
    var remainingSeconds by remember(message.expiresAt) {
        mutableStateOf(
            message.expiresAt?.let { exp ->
                val diff = (exp - System.currentTimeMillis()) / 1000
                if (diff > 0) diff else 0
            }
        )
    }

    LaunchedEffect(message.expiresAt) {
        if (message.expiresAt != null) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val diff = (message.expiresAt - System.currentTimeMillis()) / 1000
                remainingSeconds = if (diff > 0) diff else 0
                if (remainingSeconds == 0L) break
            }
        }
    }

    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .border(
                    width = 1.dp,
                    color = if (isMe) theme.primary.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                    shape = bubbleShape
                )
                .clickable { showActionMenu = !showActionMenu }
                .testTag("chat_bubble_${message.id}")
                .padding(if (message.mediaType == MediaType.IMAGE) 6.dp else 12.dp)
        ) {
            Column {
                // PHOTO ATTACHMENT
                if (message.mediaType == MediaType.IMAGE && message.mediaUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onPhotoClick(message.mediaUri, message.decryptedText)
                            }
                    ) {
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Encrypted Shared Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Encrypted media badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "E2EE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (message.decryptedText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.decryptedText,
                            color = textColor,
                            fontSize = (14 * fontScale).sp,
                            lineHeight = (18 * fontScale).sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // VOICE NOTE ATTACHMENT
                if (message.mediaType == MediaType.AUDIO) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isMe) Color.White.copy(alpha = 0.2f) else theme.primary.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Voice Note",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            // Simulated audio waveform
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 16, 12, 18, 6, 14, 20, 10, 15, 8, 16, 12)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .background(
                                                color = textColor.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Encrypted Audio (0:04)",
                                fontSize = 11.sp,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // TEXT MESSAGE
                if (message.mediaType == MediaType.NONE) {
                    Text(
                        text = message.decryptedText,
                        color = textColor,
                        fontSize = (15 * fontScale).sp,
                        lineHeight = (21 * fontScale).sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // FOOTER: E2EE Lock Icon + Disappearing Timer + Timestamp + Delivery status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock icon
                    if (message.isEncrypted) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "AES-256 Encrypted",
                            tint = if (isMe) textColor.copy(alpha = 0.7f) else theme.primary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }

                    // Disappearing countdown
                    if (remainingSeconds != null && remainingSeconds!! > 0) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Self-Destructing Message",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${remainingSeconds}s",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Timestamp
                    Text(
                        text = timeFormatted,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.65f)
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        val statusIcon = when (message.status) {
                            MessageStatus.SENDING -> Icons.Default.Done
                            MessageStatus.SENT -> Icons.Default.Done
                            MessageStatus.DELIVERED, MessageStatus.READ -> Icons.Default.DoneAll
                        }
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = "Delivered",
                            tint = if (message.status == MessageStatus.READ) Color(0xFF67E8F9) else textColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Reaction chips below bubble
        if (message.reactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                message.reactions.distinct().forEach { emoji ->
                    Surface(
                        color = theme.surfaceVariant.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable { onReactionClick(emoji) }
                    ) {
                        Text(
                            text = "$emoji ${message.reactions.count { it == emoji }}",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Interactive Context Actions (Inspect Cipher / Quick Reactions)
        AnimatedVisibility(
            visible = showActionMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surface.copy(alpha = 0.95f))
                    .border(1.dp, theme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
            ) {
                // Quick emoji row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("❤️", "🔥", "👍", "🔒", "😂").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onReactionClick(emoji)
                                    showActionMenu = false
                                }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Inspect Cipher action
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            showActionMenu = false
                            onInspectEncryption(message)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = theme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Inspect AES-256 Cipher Payload",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = theme.primary
                    )
                }
            }
        }
    }
}
