package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatConversation
import com.example.ui.theme.LocalChatTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    conversations: List<ChatConversation>,
    isVaultUnlocked: Boolean,
    onOpenChat: (ChatConversation) -> Unit,
    onOpenVault: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    onNewChatClick: () -> Unit,
    onToggleHideChat: (String) -> Unit
) {
    val theme = LocalChatTheme.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, VERIFIED, UNREAD

    // Filter out hidden chats (they belong strictly in the vault!)
    val visibleConversations = remember(conversations, searchQuery, selectedFilter) {
        conversations
            .filterNot { it.isHidden }
            .filter {
                if (searchQuery.isBlank()) true
                else it.name.contains(searchQuery, ignoreCase = true) || it.handle.contains(searchQuery, ignoreCase = true)
            }
            .filter {
                when (selectedFilter) {
                    "VERIFIED" -> it.isSafetyVerified
                    "UNREAD" -> it.unreadCount > 0
                    else -> true
                }
            }
    }

    val hiddenChatsCount = remember(conversations) {
        conversations.count { it.isHidden }
    }

    Scaffold(
        containerColor = theme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(theme.surface, theme.background)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = theme.primary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CipherChat",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AES-256 E2EE Active",
                                    fontSize = 11.sp,
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Theme Studio Button
                        IconButton(
                            onClick = onOpenThemeStudio,
                            modifier = Modifier.testTag("open_theme_studio_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Studio",
                                tint = theme.primary
                            )
                        }

                        // Secret Vault Access Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isVaultUnlocked) Color(0xFFA855F7).copy(alpha = 0.25f) else theme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVaultUnlocked) Color(0xFFA855F7) else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenVault() }
                                .testTag("vault_entry_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Outlined.Lock,
                                    contentDescription = "Hidden Vault",
                                    tint = if (isVaultUnlocked) Color(0xFFA855F7) else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVaultUnlocked) "Vault ($hiddenChatsCount)" else "Vault",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isVaultUnlocked) Color(0xFFA855F7) else Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search encrypted messages or contacts...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = theme.surface,
                        unfocusedContainerColor = theme.surface,
                        focusedBorderColor = theme.primary.copy(alpha = 0.6f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("chat_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        title = "All Encrypted",
                        isSelected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" }
                    )
                    FilterChipItem(
                        title = "Verified Keys",
                        isSelected = selectedFilter == "VERIFIED",
                        onClick = { selectedFilter = "VERIFIED" }
                    )
                    FilterChipItem(
                        title = "Unread",
                        isSelected = selectedFilter == "UNREAD",
                        onClick = { selectedFilter = "UNREAD" }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewChatClick,
                containerColor = theme.primary,
                contentColor = theme.userBubbleText,
                shape = CircleShape,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("new_chat_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Start New Chat",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Active Online Contacts Row
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "SECURE PEERS ONLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.45f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(conversations.filter { it.isOnline }) { conv ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onOpenChat(conv) }
                                    .padding(4.dp)
                            ) {
                                Box {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(conv.avatarColorHex),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = conv.name.take(1).uppercase(),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    // Online pulse dot
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                            .border(2.dp, theme.background, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = conv.name.substringBefore(" "),
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.06f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Visible Chat Conversations
            if (visibleConversations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No encrypted conversations found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button to initiate a new session",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                items(visibleConversations, key = { it.id }) { conv ->
                    ConversationListItem(
                        conversation = conv,
                        onClick = { onOpenChat(conv) },
                        onHideToVault = { onToggleHideChat(conv.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationListItem(
    conversation: ChatConversation,
    onClick: () -> Unit,
    onHideToVault: () -> Unit
) {
    val theme = LocalChatTheme.current
    val timeFormatted = remember(conversation.lastMessageTimestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(conversation.lastMessageTimestamp))
    }

    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conversation_item_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Surface(
                    shape = CircleShape,
                    color = Color(conversation.avatarColorHex),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = conversation.name.take(1).uppercase(),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399))
                            .border(2.dp, theme.background, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Name, snippet, status
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = conversation.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (conversation.isSafetyVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Safety Key Verified",
                                tint = theme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = timeFormatted,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (conversation.isTyping) {
                        Text(
                            text = "typing securely...",
                            fontSize = 13.sp,
                            color = theme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = conversation.lastMessageSnippet,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (conversation.disappearingSeconds > 0) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Disappearing",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        if (conversation.unreadCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = theme.primary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = conversation.unreadCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.userBubbleText
                                    )
                                }
                            }
                        }

                        // More options button (e.g. Move to Secret Vault)
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(theme.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Move to Hidden Vault", color = Color(0xFFA855F7)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFA855F7),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        onHideToVault()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalChatTheme.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) theme.primary else theme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) theme.userBubbleText else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
