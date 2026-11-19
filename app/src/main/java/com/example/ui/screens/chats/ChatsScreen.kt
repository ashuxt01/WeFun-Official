package com.example.ui.screens.chats

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ChatThread
import com.example.data.model.DirectMessage
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.repository.WefunSeedData
import com.example.ui.messages.MessagesAndSocialScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun ChatsScreen(
    currentUser: UserProfile = UserProfile(
        uid = "usr_nova_01",
        wefunId = "WF-804921",
        username = "ariavance",
        nickname = "Aria Vance",
        bio = "Music host, late-night storyteller & Ludo strategist."
    ),
    threads: List<ChatThread> = WefunSeedData.initialThreads,
    activeThreadId: String? = null,
    activeMessages: List<DirectMessage> = emptyList(),
    blockedUserIds: Set<String> = emptySet(),
    onOpenThread: (String?) -> Unit = {},
    onStartOrOpenChat: (peerId: String, peerName: String, isGroup: Boolean) -> Unit = { _, _, _ -> },
    onSendDirectMessage: (threadId: String, text: String, gift: VirtualGift?, voiceSeconds: Int?) -> Unit = { _, _, _, _ -> },
    onDeleteMessage: (String) -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onDeclineRequest: (String) -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        MessagesAndSocialScreen(
            currentUser = currentUser,
            threads = threads,
            activeThreadId = activeThreadId,
            activeMessages = activeMessages,
            blockedUserIds = blockedUserIds,
            onOpenThread = onOpenThread,
            onStartOrOpenChat = onStartOrOpenChat,
            onSendDirectMessage = onSendDirectMessage,
            onDeleteMessage = onDeleteMessage,
            onAcceptRequest = onAcceptRequest,
            onDeclineRequest = onDeclineRequest,
            onOpenUserProfile = onOpenUserProfile
        )
    }
}
