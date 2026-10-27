package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.data.ChatConversation
import com.example.data.ChatMessage
import com.example.data.ChatRepository
import com.example.data.VaultState
import com.example.ui.theme.BubbleStyle
import com.example.ui.theme.ThemePresetId
import com.example.ui.theme.ThemePresets
import com.example.ui.theme.WallpaperStyle
import kotlinx.coroutines.flow.StateFlow

sealed class Screen {
    data object ChatList : Screen()
    data class ChatDetail(val conversationId: String) : Screen()
    data object HiddenVault : Screen()
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)

    val conversations: StateFlow<List<ChatConversation>> = repository.conversations
    val messages: StateFlow<Map<String, List<ChatMessage>>> = repository.messages
    val vaultState: StateFlow<VaultState> = repository.vaultState

    val currentTheme = repository.currentTheme
    val currentBubbleStyle = repository.currentBubbleStyle
    val currentWallpaperStyle = repository.currentWallpaperStyle
    val currentFontSizeScale = repository.currentFontSizeScale

    fun sendMessage(conversationId: String, text: String) {
        repository.sendMessage(conversationId, text)
    }

    fun sendPhotoMessage(conversationId: String, uri: Uri, caption: String = "") {
        repository.sendPhotoMessage(conversationId, uri, caption)
    }

    fun sendVoiceMessage(conversationId: String) {
        repository.sendVoiceMessage(conversationId)
    }

    fun sendSampleAuroraPhoto(conversationId: String) {
        val sampleUri = Uri.parse("android.resource://com.aistudio.cipherchat.vxkmrq/drawable/sample_photo_aurora")
        repository.sendPhotoMessage(conversationId, sampleUri, "Aurora night reflection 🌌")
    }

    fun unlockVault(pin: String): Boolean {
        return repository.unlockVault(pin)
    }

    fun lockVault() {
        repository.lockVault()
    }

    fun toggleHideChat(conversationId: String) {
        repository.toggleChatHidden(conversationId)
    }

    fun toggleSafetyVerified(conversationId: String) {
        repository.toggleSafetyVerified(conversationId)
    }

    fun setDisappearingDuration(conversationId: String, seconds: Int) {
        repository.setDisappearingDuration(conversationId, seconds)
    }

    fun addReaction(conversationId: String, messageId: String, emoji: String) {
        repository.addReaction(conversationId, messageId, emoji)
    }

    fun createNewChat(name: String, handle: String, isHidden: Boolean): String {
        return repository.createNewChat(name, handle, isHidden)
    }

    fun setTheme(presetId: ThemePresetId) {
        currentTheme.value = presetId
    }

    fun setBubbleStyle(style: BubbleStyle) {
        currentBubbleStyle.value = style
    }

    fun setWallpaperStyle(style: WallpaperStyle) {
        currentWallpaperStyle.value = style
    }

    fun setFontSizeScale(scale: Float) {
        currentFontSizeScale.value = scale
    }
}
