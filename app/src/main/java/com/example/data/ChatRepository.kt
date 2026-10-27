package com.example.data

import android.content.Context
import android.net.Uri
import com.example.crypto.CryptoManager
import com.example.ui.theme.BubbleStyle
import com.example.ui.theme.ThemePresetId
import com.example.ui.theme.WallpaperStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ChatRepository(private val context: Context? = null) {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Global customization settings
    val currentTheme = MutableStateFlow(ThemePresetId.CYBERPUNK)
    val currentBubbleStyle = MutableStateFlow(BubbleStyle.SMOOTH)
    val currentWallpaperStyle = MutableStateFlow(WallpaperStyle.CYBER_CONSTELLATION)
    val currentFontSizeScale = MutableStateFlow(1.0f)

    // Vault Security State (Default PIN is "1234")
    private val _vaultState = MutableStateFlow(
        VaultState(
            isUnlocked = false,
            pinHash = CryptoManager.hashPin("1234"),
            stealthModeEnabled = true
        )
    )
    val vaultState: StateFlow<VaultState> = _vaultState.asStateFlow()

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    init {
        seedInitialData()
        startDisappearingMessagesTicker()
    }

    private fun seedInitialData() {
        val now = System.currentTimeMillis()

        val elenaId = "conv_elena"
        val ariaId = "conv_aria"
        val kaiId = "conv_kai"
        val marcusSecretId = "conv_marcus_vault"
        val ghostSecretId = "conv_ghost_vault"

        val initialConversations = listOf(
            ChatConversation(
                id = elenaId,
                name = "Elena Rostova",
                handle = "@elena_k",
                avatarColorHex = 0xFF00E5FF,
                statusText = "Zero-knowledge protocols researcher",
                isOnline = true,
                isPinned = true,
                isHidden = false,
                safetyNumber = CryptoManager.generateSafetyNumber("elena_k"),
                isSafetyVerified = true,
                lastMessageSnippet = "Double ratchet keys re-synced successfully. All channels secure.",
                lastMessageTimestamp = now - 1000 * 60 * 3,
                unreadCount = 1,
                disappearingSeconds = 0,
                themePresetId = ThemePresetId.CYBERPUNK
            ),
            ChatConversation(
                id = ariaId,
                name = "Aria Thorne",
                handle = "@aria_design",
                avatarColorHex = 0xFFFB7185,
                statusText = "Aesthetic neon palette architect",
                isOnline = true,
                isPinned = false,
                isHidden = false,
                safetyNumber = CryptoManager.generateSafetyNumber("aria_design"),
                isSafetyVerified = false,
                lastMessageSnippet = "Look at this aurora capture from our northern expedition! 🌌",
                lastMessageTimestamp = now - 1000 * 60 * 25,
                unreadCount = 0,
                disappearingSeconds = 0,
                themePresetId = ThemePresetId.ROSE_PASTEL
            ),
            ChatConversation(
                id = kaiId,
                name = "Kai Chen",
                handle = "@kaidev",
                avatarColorHex = 0xFF38BDF8,
                statusText = "Offline-first distributed systems",
                isOnline = false,
                isPinned = false,
                isHidden = false,
                safetyNumber = CryptoManager.generateSafetyNumber("kaidev"),
                isSafetyVerified = true,
                lastMessageSnippet = "The AES-GCM verification tests completed with zero packet loss.",
                lastMessageTimestamp = now - 1000 * 60 * 120,
                unreadCount = 0,
                disappearingSeconds = 0,
                themePresetId = ThemePresetId.NORDIC_SLATE
            ),
            // HIDDEN VAULT CHATS:
            ChatConversation(
                id = marcusSecretId,
                name = "Marcus Vance (Vault)",
                handle = "@vance_stealth",
                avatarColorHex = 0xFFA855F7,
                statusText = "Stealth Ops | Vault Protected",
                isOnline = true,
                isPinned = true,
                isHidden = true, // HIDDEN CHAT
                safetyNumber = CryptoManager.generateSafetyNumber("vance_stealth"),
                isSafetyVerified = true,
                lastMessageSnippet = "Operational data encrypted under quantum-resistant cipher.",
                lastMessageTimestamp = now - 1000 * 60 * 10,
                unreadCount = 2,
                disappearingSeconds = 300,
                themePresetId = ThemePresetId.MIDNIGHT_VELVET
            ),
            ChatConversation(
                id = ghostSecretId,
                name = "Ghost_09 (Self-Destruct)",
                handle = "@anon_phantom",
                avatarColorHex = 0xFF10B981,
                statusText = "Burn after reading enabled",
                isOnline = true,
                isPinned = false,
                isHidden = true, // HIDDEN CHAT
                safetyNumber = CryptoManager.generateSafetyNumber("anon_phantom"),
                isSafetyVerified = false,
                lastMessageSnippet = "This channel self-destructs messages automatically.",
                lastMessageTimestamp = now - 1000 * 60 * 45,
                unreadCount = 0,
                disappearingSeconds = 60,
                themePresetId = ThemePresetId.EMERALD_VAULT
            )
        )

        // Seed initial encrypted messages
        val initialMessages = mutableMapOf<String, List<ChatMessage>>()

        // Elena messages
        val elenaMsg1 = createEncryptedMessage(elenaId, false, "Hey! Initiated the key exchange for our channel.", now - 1000 * 60 * 15)
        val elenaMsg2 = createEncryptedMessage(elenaId, true, "Confirmed. My safety number matches your fingerprint.", now - 1000 * 60 * 10)
        val elenaMsg3 = createEncryptedMessage(elenaId, false, "Double ratchet keys re-synced successfully. All channels secure.", now - 1000 * 60 * 3)
        initialMessages[elenaId] = listOf(elenaMsg1, elenaMsg2, elenaMsg3)

        // Aria messages (with sample photo!)
        val ariaMsg1 = createEncryptedMessage(ariaId, true, "Did you finish the new aesthetic themes?", now - 1000 * 60 * 35)
        val ariaMsg2 = createEncryptedMessage(ariaId, false, "Yes! Cyberpunk, Midnight Velvet, and Emerald Vault are ready.", now - 1000 * 60 * 30)
        val ariaMsg3 = createEncryptedPhotoMessage(
            conversationId = ariaId,
            isMe = false,
            caption = "Look at this aurora capture from our northern expedition! 🌌",
            sampleResName = "sample_photo_aurora",
            timestamp = now - 1000 * 60 * 25
        )
        initialMessages[ariaId] = listOf(ariaMsg1, ariaMsg2, ariaMsg3)

        // Kai messages
        val kaiMsg1 = createEncryptedMessage(kaiId, true, "Running local cipher benchmark right now.", now - 1000 * 60 * 150)
        val kaiMsg2 = createEncryptedMessage(kaiId, false, "The AES-GCM verification tests completed with zero packet loss.", now - 1000 * 60 * 120)
        initialMessages[kaiId] = listOf(kaiMsg1, kaiMsg2)

        // Marcus (Vault) messages
        val marcusMsg1 = createEncryptedMessage(marcusSecretId, false, "Welcome to the isolated vault node.", now - 1000 * 60 * 40)
        val marcusMsg2 = createEncryptedMessage(marcusSecretId, true, "Only accessible when the secret vault PIN is unlocked.", now - 1000 * 60 * 20)
        val marcusMsg3 = createEncryptedMessage(marcusSecretId, false, "Operational data encrypted under quantum-resistant cipher.", now - 1000 * 60 * 10)
        initialMessages[marcusSecretId] = listOf(marcusMsg1, marcusMsg2, marcusMsg3)

        // Ghost (Vault) messages with disappearing timer
        val ghostMsg1 = createEncryptedMessage(
            conversationId = ghostSecretId,
            isMe = false,
            text = "This channel self-destructs messages automatically.",
            timestamp = now - 1000 * 60 * 45,
            disappearingDuration = 60
        )
        initialMessages[ghostSecretId] = listOf(ghostMsg1)

        _conversations.value = initialConversations
        _messages.value = initialMessages
    }

    private fun createEncryptedMessage(
        conversationId: String,
        isMe: Boolean,
        text: String,
        timestamp: Long,
        disappearingDuration: Int = 0
    ): ChatMessage {
        val payload = CryptoManager.encrypt(text)
        val expiresAt = if (disappearingDuration > 0) timestamp + disappearingDuration * 1000L else null
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            isMe = isMe,
            decryptedText = text,
            ciphertext = payload.ciphertext,
            iv = payload.iv,
            checksum = payload.checksum,
            mediaType = MediaType.NONE,
            timestamp = timestamp,
            status = MessageStatus.READ,
            isEncrypted = true,
            disappearingDurationSeconds = disappearingDuration,
            expiresAt = expiresAt
        )
    }

    private fun createEncryptedPhotoMessage(
        conversationId: String,
        isMe: Boolean,
        caption: String,
        sampleResName: String? = null,
        uriString: String? = null,
        timestamp: Long
    ): ChatMessage {
        val payload = CryptoManager.encrypt(caption.ifEmpty { "[Encrypted Photo Media]" })
        val photoUri = uriString ?: "android.resource://com.aistudio.cipherchat.vxkmrq/drawable/$sampleResName"
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            isMe = isMe,
            decryptedText = caption,
            ciphertext = payload.ciphertext,
            iv = payload.iv,
            checksum = payload.checksum,
            mediaUri = photoUri,
            mediaType = MediaType.IMAGE,
            timestamp = timestamp,
            status = MessageStatus.READ,
            isEncrypted = true
        )
    }

    /**
     * Sends an encrypted text message from the current user.
     */
    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val conv = _conversations.value.firstOrNull { it.id == conversationId } ?: return
        val now = System.currentTimeMillis()
        val disappearingDuration = conv.disappearingSeconds
        val expiresAt = if (disappearingDuration > 0) now + disappearingDuration * 1000L else null

        val payload = CryptoManager.encrypt(text)
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            isMe = true,
            decryptedText = text,
            ciphertext = payload.ciphertext,
            iv = payload.iv,
            checksum = payload.checksum,
            mediaType = MediaType.NONE,
            timestamp = now,
            status = MessageStatus.SENT,
            isEncrypted = true,
            disappearingDurationSeconds = disappearingDuration,
            expiresAt = expiresAt
        )

        // Append to message list
        _messages.update { map ->
            val list = map[conversationId].orEmpty() + newMsg
            map + (conversationId to list)
        }

        // Update conversation last message snippet
        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) {
                    c.copy(
                        lastMessageSnippet = text,
                        lastMessageTimestamp = now
                    )
                } else c
            }
        }

        // Trigger simulated intelligent encrypted peer reply
        schedulePeerReply(conversationId, text)
    }

    /**
     * Sends a photo message with encrypted metadata.
     */
    fun sendPhotoMessage(conversationId: String, uri: Uri, caption: String = "") {
        val conv = _conversations.value.firstOrNull { it.id == conversationId } ?: return
        val now = System.currentTimeMillis()
        val textSnippet = caption.ifEmpty { "📷 Encrypted Photo" }

        // Copy uri to app internal files if context is available
        val savedUriString = copyPhotoToInternalStorage(uri) ?: uri.toString()

        val payload = CryptoManager.encrypt(textSnippet)
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            isMe = true,
            decryptedText = caption,
            ciphertext = payload.ciphertext,
            iv = payload.iv,
            checksum = payload.checksum,
            mediaUri = savedUriString,
            mediaType = MediaType.IMAGE,
            timestamp = now,
            status = MessageStatus.SENT,
            isEncrypted = true,
            disappearingDurationSeconds = conv.disappearingSeconds,
            expiresAt = if (conv.disappearingSeconds > 0) now + conv.disappearingSeconds * 1000L else null
        )

        _messages.update { map ->
            val list = map[conversationId].orEmpty() + newMsg
            map + (conversationId to list)
        }

        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) {
                    c.copy(
                        lastMessageSnippet = "📷 Photo",
                        lastMessageTimestamp = now
                    )
                } else c
            }
        }

        schedulePeerReply(conversationId, "[Sent a photo]")
    }

    /**
     * Sends a simulated voice note message.
     */
    fun sendVoiceMessage(conversationId: String, durationSeconds: Int = 4) {
        val conv = _conversations.value.firstOrNull { it.id == conversationId } ?: return
        val now = System.currentTimeMillis()
        val label = "🎙️ Voice note (${durationSeconds}s)"
        val payload = CryptoManager.encrypt(label)

        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            isMe = true,
            decryptedText = label,
            ciphertext = payload.ciphertext,
            iv = payload.iv,
            checksum = payload.checksum,
            mediaType = MediaType.AUDIO,
            timestamp = now,
            status = MessageStatus.SENT,
            isEncrypted = true,
            disappearingDurationSeconds = conv.disappearingSeconds,
            expiresAt = if (conv.disappearingSeconds > 0) now + conv.disappearingSeconds * 1000L else null
        )

        _messages.update { map ->
            val list = map[conversationId].orEmpty() + newMsg
            map + (conversationId to list)
        }

        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) {
                    c.copy(lastMessageSnippet = label, lastMessageTimestamp = now)
                } else c
            }
        }

        schedulePeerReply(conversationId, "[Voice message]")
    }

    private fun copyPhotoToInternalStorage(uri: Uri): String? {
        val ctx = context ?: return null
        return try {
            val fileName = "encrypted_img_${System.currentTimeMillis()}.jpg"
            val file = File(ctx.filesDir, fileName)
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun schedulePeerReply(conversationId: String, userPrompt: String) {
        scope.launch {
            delay(800)
            // Show typing indicator
            _conversations.update { list ->
                list.map { c -> if (c.id == conversationId) c.copy(isTyping = true) else c }
            }

            delay(1600)
            // Generate peer reply
            val conv = _conversations.value.firstOrNull { it.id == conversationId } ?: return@launch
            val replyText = generatePersonaReply(conv.handle, userPrompt)
            val now = System.currentTimeMillis()
            val payload = CryptoManager.encrypt(replyText)

            val replyMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                isMe = false,
                decryptedText = replyText,
                ciphertext = payload.ciphertext,
                iv = payload.iv,
                checksum = payload.checksum,
                mediaType = MediaType.NONE,
                timestamp = now,
                status = MessageStatus.READ,
                isEncrypted = true,
                disappearingDurationSeconds = conv.disappearingSeconds,
                expiresAt = if (conv.disappearingSeconds > 0) now + conv.disappearingSeconds * 1000L else null
            )

            _messages.update { map ->
                val list = map[conversationId].orEmpty() + replyMsg
                map + (conversationId to list)
            }

            _conversations.update { list ->
                list.map { c ->
                    if (c.id == conversationId) {
                        c.copy(
                            lastMessageSnippet = replyText,
                            lastMessageTimestamp = now,
                            isTyping = false
                        )
                    } else c
                }
            }
        }
    }

    private fun generatePersonaReply(handle: String, input: String): String {
        val lower = input.lowercase()
        return when {
            handle.contains("elena") -> {
                when {
                    "key" in lower || "crypto" in lower || "safe" in lower ->
                        "Our 256-bit ephemeral keys verify perfectly against the master fingerprint. No leakage detected."
                    "photo" in lower || "image" in lower ->
                        "Received your encrypted photo asset! Decrypted cleanly in local memory buffer."
                    else ->
                        "Understood. Packet validated and appended to the encrypted ledger."
                }
            }
            handle.contains("aria") -> {
                when {
                    "photo" in lower || "image" in lower ->
                        "Stunning aesthetic! The color saturation and contrast look magnificent in this theme."
                    "theme" in lower || "color" in lower ->
                        "Try switching between Cyberpunk and Midnight Velvet in the theme sheet!"
                    else ->
                        "Love the aesthetic of this conversation! Seamless encryption and style combined."
                }
            }
            handle.contains("vance") -> {
                "Vault node received payload. Message isolated behind biometric barrier."
            }
            handle.contains("phantom") || handle.contains("ghost") -> {
                "Burn counter initiated. Message will vanish when the timer expires."
            }
            else -> {
                "E2EE packet acknowledged: verified with AES-GCM cipher."
            }
        }
    }

    // Vault Security Actions
    fun unlockVault(pin: String): Boolean {
        val storedHash = _vaultState.value.pinHash
        val success = CryptoManager.verifyPin(pin, storedHash)
        if (success) {
            _vaultState.update { it.copy(isUnlocked = true, failedAttempts = 0) }
            return true
        } else {
            _vaultState.update { it.copy(failedAttempts = it.failedAttempts + 1) }
            return false
        }
    }

    fun lockVault() {
        _vaultState.update { it.copy(isUnlocked = false) }
    }

    fun changeVaultPin(newPin: String) {
        val newHash = CryptoManager.hashPin(newPin)
        _vaultState.update { it.copy(pinHash = newHash) }
    }

    fun toggleChatHidden(conversationId: String) {
        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) c.copy(isHidden = !c.isHidden) else c
            }
        }
    }

    fun toggleSafetyVerified(conversationId: String) {
        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) c.copy(isSafetyVerified = !c.isSafetyVerified) else c
            }
        }
    }

    fun setDisappearingDuration(conversationId: String, seconds: Int) {
        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) c.copy(disappearingSeconds = seconds) else c
            }
        }
    }

    fun addReaction(conversationId: String, messageId: String, emoji: String) {
        _messages.update { map ->
            val list = map[conversationId].orEmpty().map { msg ->
                if (msg.id == messageId) {
                    val updatedReactions = if (msg.reactions.contains(emoji)) {
                        msg.reactions - emoji
                    } else {
                        msg.reactions + emoji
                    }
                    msg.copy(reactions = updatedReactions)
                } else msg
            }
            map + (conversationId to list)
        }
    }

    fun deleteMessage(conversationId: String, messageId: String) {
        _messages.update { map ->
            val list = map[conversationId].orEmpty().filterNot { it.id == messageId }
            map + (conversationId to list)
        }
    }

    fun createNewChat(name: String, handle: String, isHidden: Boolean): String {
        val id = "conv_${UUID.randomUUID()}"
        val colors = listOf(0xFF00E5FF, 0xFFA855F7, 0xFF10B981, 0xFFFB7185, 0xFF38BDF8, 0xFFF59E0B)
        val randomColor = colors.random()
        val newConv = ChatConversation(
            id = id,
            name = name,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            avatarColorHex = randomColor,
            statusText = if (isHidden) "Vault Encrypted Contact" else "Online | E2EE Active",
            isOnline = true,
            isPinned = false,
            isHidden = isHidden,
            safetyNumber = CryptoManager.generateSafetyNumber(handle),
            isSafetyVerified = false,
            lastMessageSnippet = "Encrypted session initiated.",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            disappearingSeconds = if (isHidden) 60 else 0,
            themePresetId = currentTheme.value
        )

        _conversations.update { listOf(newConv) + it }
        val intro = if (isHidden) "Vault session initialized under AES-256-GCM cipher." else "Encrypted chat channel established."
        val introMsg = createEncryptedMessage(id, false, intro, System.currentTimeMillis())
        _messages.update { it + (id to listOf(introMsg)) }
        return id
    }

    private fun startDisappearingMessagesTicker() {
        scope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                _messages.update { map ->
                    var changed = false
                    val newMap = map.mapValues { (_, msgs) ->
                        val filtered = msgs.filter { msg ->
                            val keep = msg.expiresAt == null || msg.expiresAt > now
                            if (!keep) changed = true
                            keep
                        }
                        filtered
                    }
                    if (changed) newMap else map
                }
            }
        }
    }
}
