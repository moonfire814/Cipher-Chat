package com.example.data

import com.example.ui.theme.ThemePresetId

enum class MediaType {
    NONE,
    IMAGE,
    AUDIO
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val isMe: Boolean,
    val decryptedText: String,
    val ciphertext: String,
    val iv: String,
    val checksum: String,
    val mediaUri: String? = null,
    val mediaType: MediaType = MediaType.NONE,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.READ,
    val isEncrypted: Boolean = true,
    val disappearingDurationSeconds: Int = 0,
    val expiresAt: Long? = null,
    val reactions: List<String> = emptyList()
)

data class ChatConversation(
    val id: String,
    val name: String,
    val handle: String,
    val avatarColorHex: Long,
    val statusText: String,
    val isOnline: Boolean,
    val isPinned: Boolean,
    val isHidden: Boolean, // Vault hidden chat
    val safetyNumber: String,
    val isSafetyVerified: Boolean = false,
    val lastMessageSnippet: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0,
    val disappearingSeconds: Int = 0,
    val themePresetId: ThemePresetId = ThemePresetId.CYBERPUNK,
    val isTyping: Boolean = false
)

data class VaultState(
    val isUnlocked: Boolean = false,
    val pinHash: String,
    val stealthModeEnabled: Boolean = true,
    val failedAttempts: Int = 0
)
