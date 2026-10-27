package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChatViewModel
import com.example.ui.Screen
import com.example.ui.components.DisguiseCalculatorDialog
import com.example.ui.components.NewChatDialog
import com.example.ui.components.ThemeCustomizerSheet
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.HiddenVaultScreen
import com.example.ui.theme.CipherChatTheme
import com.example.ui.theme.ThemePresets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ChatViewModel = viewModel()

            val activeThemeId by viewModel.currentTheme.collectAsStateWithLifecycle()
            val bubbleStyle by viewModel.currentBubbleStyle.collectAsStateWithLifecycle()
            val wallpaperStyle by viewModel.currentWallpaperStyle.collectAsStateWithLifecycle()
            val fontScale by viewModel.currentFontSizeScale.collectAsStateWithLifecycle()

            val activeTheme = remember(activeThemeId) { ThemePresets.getById(activeThemeId) }

            CipherChatTheme(
                activeTheme = activeTheme,
                bubbleStyle = bubbleStyle,
                wallpaperStyle = wallpaperStyle,
                fontSizeScale = fontScale
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CipherChatApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CipherChatApp(viewModel: ChatViewModel) {
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val allMessages by viewModel.messages.collectAsStateWithLifecycle()
    val vaultState by viewModel.vaultState.collectAsStateWithLifecycle()

    val activeThemeId by viewModel.currentTheme.collectAsStateWithLifecycle()
    val bubbleStyle by viewModel.currentBubbleStyle.collectAsStateWithLifecycle()
    val wallpaperStyle by viewModel.currentWallpaperStyle.collectAsStateWithLifecycle()
    val fontScale by viewModel.currentFontSizeScale.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.ChatList) }
    var showDisguiseDialog by remember { mutableStateOf(false) }
    var showThemeStudio by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var isNewChatVaultOnly by remember { mutableStateOf(false) }

    when (val screen = currentScreen) {
        is Screen.ChatList -> {
            ChatListScreen(
                conversations = conversations,
                isVaultUnlocked = vaultState.isUnlocked,
                onOpenChat = { conv ->
                    currentScreen = Screen.ChatDetail(conv.id)
                },
                onOpenVault = {
                    if (vaultState.isUnlocked) {
                        currentScreen = Screen.HiddenVault
                    } else {
                        showDisguiseDialog = true
                    }
                },
                onOpenThemeStudio = { showThemeStudio = true },
                onNewChatClick = {
                    isNewChatVaultOnly = false
                    showNewChatDialog = true
                },
                onToggleHideChat = { convId ->
                    viewModel.toggleHideChat(convId)
                }
            )
        }

        is Screen.ChatDetail -> {
            val conversation = conversations.firstOrNull { it.id == screen.conversationId }
            if (conversation != null) {
                val messages = allMessages[conversation.id].orEmpty()
                ChatDetailScreen(
                    conversation = conversation,
                    messages = messages,
                    onSendMessage = { text ->
                        viewModel.sendMessage(conversation.id, text)
                    },
                    onSendPhoto = { uri, caption ->
                        viewModel.sendPhotoMessage(conversation.id, uri, caption)
                    },
                    onSendVoiceNote = {
                        viewModel.sendVoiceMessage(conversation.id)
                    },
                    onSendSampleAuroraPhoto = {
                        viewModel.sendSampleAuroraPhoto(conversation.id)
                    },
                    onToggleSafetyVerified = {
                        viewModel.toggleSafetyVerified(conversation.id)
                    },
                    onSetDisappearingDuration = { sec ->
                        viewModel.setDisappearingDuration(conversation.id, sec)
                    },
                    onReactionClick = { msgId, emoji ->
                        viewModel.addReaction(conversation.id, msgId, emoji)
                    },
                    onOpenThemeStudio = { showThemeStudio = true },
                    onBack = {
                        // If it's a hidden chat, return to Vault, otherwise return to ChatList
                        if (conversation.isHidden) {
                            currentScreen = Screen.HiddenVault
                        } else {
                            currentScreen = Screen.ChatList
                        }
                    }
                )
            } else {
                currentScreen = Screen.ChatList
            }
        }

        is Screen.HiddenVault -> {
            val hiddenChats = conversations.filter { it.isHidden }
            HiddenVaultScreen(
                hiddenConversations = hiddenChats,
                onOpenChat = { conv ->
                    currentScreen = Screen.ChatDetail(conv.id)
                },
                onLockVaultNow = {
                    viewModel.lockVault()
                    currentScreen = Screen.ChatList
                },
                onUnhideChat = { convId ->
                    viewModel.toggleHideChat(convId)
                },
                onNewHiddenChatClick = {
                    isNewChatVaultOnly = true
                    showNewChatDialog = true
                },
                onBack = {
                    viewModel.lockVault()
                    currentScreen = Screen.ChatList
                }
            )
        }
    }

    // MODAL: Disguise Calculator / Secret Vault PIN Dialog
    if (showDisguiseDialog) {
        DisguiseCalculatorDialog(
            onUnlockSuccess = {
                showDisguiseDialog = false
                currentScreen = Screen.HiddenVault
            },
            onVerifyPin = { enteredPin ->
                viewModel.unlockVault(enteredPin)
            },
            onDismiss = { showDisguiseDialog = false }
        )
    }

    // MODAL: Theme Studio Sheet
    if (showThemeStudio) {
        ThemeCustomizerSheet(
            activeTheme = activeThemeId,
            onSelectTheme = { viewModel.setTheme(it) },
            activeBubbleStyle = bubbleStyle,
            onSelectBubbleStyle = { viewModel.setBubbleStyle(it) },
            activeWallpaperStyle = wallpaperStyle,
            onSelectWallpaperStyle = { viewModel.setWallpaperStyle(it) },
            fontScale = fontScale,
            onFontScaleChange = { viewModel.setFontSizeScale(it) },
            onDismiss = { showThemeStudio = false }
        )
    }

    // MODAL: New Chat Dialog
    if (showNewChatDialog) {
        NewChatDialog(
            initialIsHidden = isNewChatVaultOnly,
            onCreateChat = { name, handle, isHidden ->
                showNewChatDialog = false
                val newId = viewModel.createNewChat(name, handle, isHidden)
                currentScreen = Screen.ChatDetail(newId)
            },
            onDismiss = { showNewChatDialog = false }
        )
    }
}
