package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChatConversation
import com.example.data.ChatMessage
import com.example.ui.components.ChatBubble
import com.example.ui.components.EncryptionInspectorDialog
import com.example.ui.components.PhotoViewerDialog
import com.example.ui.components.SafetyVerificationDialog
import com.example.ui.theme.LocalChatTheme
import com.example.ui.theme.LocalWallpaperStyle
import com.example.ui.theme.WallpaperStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversation: ChatConversation,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onSendPhoto: (Uri, String) -> Unit,
    onSendVoiceNote: () -> Unit,
    onSendSampleAuroraPhoto: () -> Unit,
    onToggleSafetyVerified: () -> Unit,
    onSetDisappearingDuration: (Int) -> Unit,
    onReactionClick: (String, String) -> Unit,
    onOpenThemeStudio: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalChatTheme.current
    val wallpaperStyle = LocalWallpaperStyle.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var selectedPhotoViewer by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedInspectMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showSafetyDialog by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showDisappearingMenu by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSendPhoto(uri, "")
        }
    }

    BackHandler {
        onBack()
    }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(theme.background)) {
        // ATMOSPHERIC WALLPAPER BACKGROUND
        when (wallpaperStyle) {
            WallpaperStyle.CYBER_CONSTELLATION -> {
                Image(
                    painter = painterResource(id = R.drawable.chat_bg_cyber),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f
                )
            }
            WallpaperStyle.AURORA_GRADIENT -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F2027),
                                    Color(0xFF203A43),
                                    Color(0xFF2C5364)
                                )
                            )
                        )
                )
            }
            WallpaperStyle.DEEP_SLATE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        )
                )
            }
            WallpaperStyle.PURE_AMOLED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.surface.copy(alpha = 0.94f))
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("chat_detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Avatar
                        Surface(
                            shape = CircleShape,
                            color = Color(conversation.avatarColorHex),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = conversation.name.take(1).uppercase(),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Name & Status / Safety
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conversation.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                // Safety verification icon (click to verify)
                                Icon(
                                    imageVector = if (conversation.isSafetyVerified) Icons.Default.Verified else Icons.Default.Shield,
                                    contentDescription = "Verify Safety",
                                    tint = if (conversation.isSafetyVerified) Color(0xFF34D399) else theme.primary.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .clickable { showSafetyDialog = true }
                                )
                            }

                            if (conversation.isTyping) {
                                Text(
                                    text = "typing securely...",
                                    fontSize = 11.sp,
                                    color = theme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "AES-256 E2EE Verified",
                                        fontSize = 11.sp,
                                        color = theme.primary
                                    )
                                }
                            }
                        }

                        // Action Buttons: Disappearing Timer, Theme Studio
                        Box {
                            IconButton(
                                onClick = { showDisappearingMenu = true },
                                modifier = Modifier.testTag("disappearing_timer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Disappearing Messages",
                                    tint = if (conversation.disappearingSeconds > 0) Color(0xFFF87171) else Color.White.copy(alpha = 0.7f)
                                )
                            }

                            DropdownMenu(
                                expanded = showDisappearingMenu,
                                onDismissRequest = { showDisappearingMenu = false },
                                modifier = Modifier.background(theme.surface)
                            ) {
                                Text(
                                    text = "Disappearing Timer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.primary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                                val durations = listOf(
                                    0 to "Off",
                                    30 to "30 Seconds",
                                    300 to "5 Minutes",
                                    3600 to "1 Hour",
                                    86400 to "24 Hours"
                                )
                                durations.forEach { (sec, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = if (conversation.disappearingSeconds == sec) theme.primary else Color.White
                                                )
                                                if (conversation.disappearingSeconds == sec) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = theme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            showDisappearingMenu = false
                                            onSetDisappearingDuration(sec)
                                        }
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onOpenThemeStudio,
                            modifier = Modifier.testTag("chat_theme_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Themes",
                                tint = theme.primary
                            )
                        }
                    }

                    // Disappearing message active banner
                    if (conversation.disappearingSeconds > 0) {
                        Surface(
                            color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Disappearing messages: Burns ${conversation.disappearingSeconds}s after being sent",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFCA5A5),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Frosted message input bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.surface.copy(alpha = 0.95f))
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    // Attachment options drawer (if opened)
                    AnimatedVisibility(visible = showAttachmentMenu) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Launch Photo Picker
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = theme.primary.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showAttachmentMenu = false
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("pick_photo_from_gallery_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Photo Picker",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Quick Aesthetic Aurora Sample Photo
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showAttachmentMenu = false
                                        onSendSampleAuroraPhoto()
                                    }
                                    .testTag("send_sample_aurora_photo_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Aurora Photo",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Main input row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Attachment Toggle Button
                        IconButton(
                            onClick = { showAttachmentMenu = !showAttachmentMenu },
                            modifier = Modifier.testTag("toggle_attachment_menu_button")
                        ) {
                            Icon(
                                imageVector = if (showAttachmentMenu) Icons.Default.Close else Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Media",
                                tint = theme.primary
                            )
                        }

                        // Voice message simulation button
                        IconButton(
                            onClick = onSendVoiceNote,
                            modifier = Modifier.testTag("send_voice_note_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Send Encrypted Voice Note",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        // Message Text Field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = "Encrypted message...",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = theme.background.copy(alpha = 0.7f),
                                unfocusedContainerColor = theme.background.copy(alpha = 0.7f),
                                focusedBorderColor = theme.primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send Button
                        Surface(
                            shape = CircleShape,
                            color = if (inputText.isNotBlank()) theme.primary else theme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable(enabled = inputText.isNotBlank()) {
                                    val toSend = inputText.trim()
                                    inputText = ""
                                    onSendMessage(toSend)
                                }
                                .testTag("send_message_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (inputText.isNotBlank()) theme.userBubbleText else Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // Channel security encryption intro
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = theme.surface.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "End-to-End Encrypted Session",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Messages and media are encrypted with AES-256-GCM. Tap any message bubble to inspect cryptographic integrity.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.65f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onInspectEncryption = { selectedInspectMessage = it },
                        onPhotoClick = { uri, caption ->
                            selectedPhotoViewer = Pair(uri, caption)
                        },
                        onReactionClick = { emoji ->
                            onReactionClick(msg.id, emoji)
                        }
                    )
                }
            }
        }
    }

    // INSPECTION DIALOG
    selectedInspectMessage?.let { msg ->
        EncryptionInspectorDialog(
            message = msg,
            onDismiss = { selectedInspectMessage = null }
        )
    }

    // PHOTO VIEWER
    selectedPhotoViewer?.let { (uri, caption) ->
        PhotoViewerDialog(
            imageUri = uri,
            caption = caption,
            onDismiss = { selectedPhotoViewer = null }
        )
    }

    // SAFETY VERIFICATION DIALOG
    if (showSafetyDialog) {
        SafetyVerificationDialog(
            conversation = conversation,
            onToggleVerified = onToggleSafetyVerified,
            onDismiss = { showSafetyDialog = false }
        )
    }
}
