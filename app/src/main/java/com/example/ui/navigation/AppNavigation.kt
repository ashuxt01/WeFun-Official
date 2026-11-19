package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.local.WefunAppSettings
import com.example.data.model.ChatThread
import com.example.data.model.DailyTaskItem
import com.example.data.model.DirectMessage
import com.example.data.model.DiscoverCategory
import com.example.data.model.EventItem
import com.example.data.model.PartyGame
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.repository.WefunSeedData
import com.example.ui.screens.chats.ChatsScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.party.PartyScreen
import com.example.ui.screens.profile.ProfileScreen

sealed class WefunDestination(
    val route: String,
    val label: String,
    val title: String,
) {
    data object Home : WefunDestination(route = "home", label = "WeFun", title = "WeFun")
    data object Party : WefunDestination(route = "party", label = "Voice Room", title = "Voice Room")
    data object Chats : WefunDestination(route = "chats", label = "Chats", title = "Chats")
    data object Discover : WefunDestination(route = "discover", label = "Discover", title = "Discover")
    data object Profile : WefunDestination(route = "profile", label = "Me", title = "Me")

    companion object {
        val entries: List<WefunDestination>
            get() = listOf(Home, Party, Chats, Discover, Profile)

        fun fromRoute(route: String?): WefunDestination =
            entries.firstOrNull { it.route == route } ?: Home
    }
}

fun NavHostController.navigateToWefunDestination(destination: WefunDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    user: UserProfile = UserProfile(
        uid = "usr_nova_01",
        wefunId = "WF-804921",
        username = "ariavance",
        nickname = "Aria Vance",
        bio = "Music host, late-night storyteller & Ludo strategist."
    ),
    settings: WefunAppSettings = WefunAppSettings(),
    rooms: List<VoiceRoom> = emptyList(),
    events: List<EventItem> = WefunSeedData.events,
    dailyTasks: List<DailyTaskItem> = WefunSeedData.dailyTasks,
    posts: List<SocialPost> = emptyList(),
    threads: List<ChatThread> = WefunSeedData.initialThreads,
    activeThreadId: String? = null,
    activeMessages: List<DirectMessage> = emptyList(),
    followedUserIds: Set<String> = emptySet(),
    initialVoiceRoomCategory: RoomCategory = RoomCategory.ALL,
    onOpenWallet: () -> Unit = {},
    onOpenEventsRankings: () -> Unit = {},
    onOpenGamePlaceholder: (PartyGame) -> Unit = {},
    onOpenGameRooms: () -> Unit = {},
    onOpenOwnFullProfile: () -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    onOpenMoments: () -> Unit = {},
    onOpenDiscoverCategory: (DiscoverCategory) -> Unit = {},
    onOpenVipCenter: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenPlayShow: () -> Unit = {},
    onOpenMyHome: () -> Unit = {},
    onOpenAvatarEditor: () -> Unit = {},
    onOpenHelpCenter: () -> Unit = {},
    onOpenSecurityCenter: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onEnterRoom: (VoiceRoom) -> Unit = {},
    onEnterRoomById: (String) -> Unit = {},
    onCreateRoomListing: (String, String, RoomCategory, String?) -> Unit = { _, _, _, _ -> },
    onCreateRoomConfigured: ((String, String, RoomCategory, String?, RoomMode, VoiceRoomBackdropTheme) -> Unit)? = null,
    onStartOrOpenChat: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onOpenThread: (String?) -> Unit = {},
    onSendDirectMessage: (String, String, VirtualGift?, Int?) -> Unit = { _, _, _, _ -> },
    onDeleteMessage: (String) -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onDeclineRequest: (String) -> Unit = {},
    onToggleFollow: (String) -> Unit = {},
    onClaimDailyTask: (String) -> Unit = {},
    onToggleParentalControl: (Boolean) -> Unit = {},
    onSelectLanguage: (String) -> Unit = {},
    onShowToast: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = WefunDestination.Home.route,
        modifier = modifier
    ) {
        composable(WefunDestination.Home.route) {
            HomeScreen(
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
                onOpenChatWithPeer = { uid, name ->
                    onStartOrOpenChat(uid, name, false)
                    navController.navigateToWefunDestination(WefunDestination.Chats)
                },
                onToggleFollow = onToggleFollow,
                onClaimDailyTask = onClaimDailyTask
            )
        }

        composable(WefunDestination.Party.route) {
            PartyScreen(
                rooms = rooms,
                currentUserId = user.uid,
                onEnterRoom = onEnterRoom,
                onEnterRoomById = onEnterRoomById,
                onCreateRoomListing = onCreateRoomListing,
                onCreateRoomConfigured = onCreateRoomConfigured,
                onOpenEventsRankings = onOpenEventsRankings,
                onOpenUserProfile = onOpenUserProfile,
                onShowToast = onShowToast,
                initialCategoryFilter = initialVoiceRoomCategory
            )
        }

        composable(WefunDestination.Chats.route) {
            ChatsScreen(
                currentUser = user,
                threads = threads,
                activeThreadId = activeThreadId,
                activeMessages = activeMessages,
                blockedUserIds = settings.blockedUserIds,
                onOpenThread = onOpenThread,
                onStartOrOpenChat = onStartOrOpenChat,
                onSendDirectMessage = onSendDirectMessage,
                onDeleteMessage = onDeleteMessage,
                onAcceptRequest = onAcceptRequest,
                onDeclineRequest = onDeclineRequest,
                onOpenUserProfile = onOpenUserProfile
            )
        }

        composable(WefunDestination.Discover.route) {
            DiscoverScreen(
                onOpenMoments = onOpenMoments,
                onOpenDiscoverCategory = onOpenDiscoverCategory
            )
        }

        composable(WefunDestination.Profile.route) {
            ProfileScreen(
                user = user,
                settings = settings,
                posts = posts,
                followedUserIds = followedUserIds,
                onToggleParentalControl = onToggleParentalControl,
                onSelectLanguage = onSelectLanguage,
                onToggleFollow = onToggleFollow,
                onOpenOwnFullProfile = onOpenOwnFullProfile,
                onOpenUserProfile = onOpenUserProfile,
                onOpenAvatarEditor = onOpenAvatarEditor,
                onOpenMoments = onOpenMoments,
                onOpenVipCenter = onOpenVipCenter,
                onOpenShop = onOpenShop,
                onOpenPlayShow = onOpenPlayShow,
                onOpenMyHome = onOpenMyHome,
                onOpenHelpCenter = onOpenHelpCenter,
                onOpenSecurityCenter = onOpenSecurityCenter,
                onOpenAppSettings = onOpenAppSettings,
                onShowToast = onShowToast
            )
        }
    }
}
