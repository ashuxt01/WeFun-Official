package com.example.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.DailyTaskItem
import com.example.data.model.EventItem
import com.example.data.model.PartyGame
import com.example.data.model.UserProfile
import com.example.data.repository.WefunSeedData
import com.example.ui.home.WefunHomeGamesScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun HomeScreen(
    user: UserProfile = defaultPreviewUser(),
    events: List<EventItem> = WefunSeedData.events,
    dailyTasks: List<DailyTaskItem> = WefunSeedData.dailyTasks,
    followedUserIds: Set<String> = emptySet(),
    onOpenWallet: () -> Unit = {},
    onOpenEventsRankings: () -> Unit = {},
    onOpenGamePlaceholder: (PartyGame) -> Unit = {},
    onOpenGameRooms: () -> Unit = {},
    onOpenOwnFullProfile: () -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    onOpenChatWithPeer: (peerId: String, peerName: String) -> Unit = { _, _ -> },
    onToggleFollow: (String) -> Unit = {},
    onClaimDailyTask: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        WefunHomeGamesScreen(
            user = user,
            events = events,
            dailyTasks = dailyTasks,
            followedUserIds = followedUserIds,
            onOpenWallet = onOpenWallet,
            onOpenEventsRankings = onOpenEventsRankings,
            onOpenGamePlaceholder = onOpenGamePlaceholder,
            onOpenGameRooms = onOpenGameRooms,
            onOpenOwnFullProfile = onOpenOwnFullProfile,
            onOpenUserProfile = onOpenUserProfile,
            onOpenChatWithPeer = onOpenChatWithPeer,
            onToggleFollow = onToggleFollow,
            onClaimDailyTask = onClaimDailyTask,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun defaultPreviewUser() = UserProfile(
    uid = "usr_nova_01",
    wefunId = "WF-804921",
    username = "ariavance",
    nickname = "Aria Vance",
    bio = "Music host, late-night storyteller & Ludo strategist."
)
