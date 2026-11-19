package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.DiscoverCategory
import com.example.data.model.GiftWallSummary
import com.example.data.model.PartyGame
import com.example.data.model.ProfileStatistics
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.UserProfile
import com.example.data.repository.CharmLevelCatalog
import com.example.ui.discover.DiscoverCategoryScreen
import com.example.ui.discover.MomentsScreen
import com.example.ui.games.GamePlaceholderScreen
import com.example.ui.navigation.AppNavigation
import com.example.ui.navigation.BottomNavigationBar
import com.example.ui.navigation.WefunDestination
import com.example.ui.navigation.WefunNavigationRail
import com.example.ui.navigation.navigateToWefunDestination
import com.example.ui.profile.AdminPanelScreen
import com.example.ui.profile.AdvancedRoomListScreen
import com.example.ui.profile.AppSettingsScreen
import com.example.ui.profile.AvatarAndProfileEditSheet
import com.example.ui.profile.CharmLevelScreen
import com.example.ui.profile.EventsAndRankingsSheet
import com.example.ui.profile.FullProfileScreen
import com.example.ui.profile.GiftWallCollectionScreen
import com.example.ui.profile.GuardListScreen
import com.example.ui.profile.HelpCenterScreen
import com.example.ui.profile.MyHomeScreen
import com.example.ui.profile.NotificationsSheet
import com.example.ui.profile.PlayShowScreen
import com.example.ui.profile.ProfileEditScreen
import com.example.ui.profile.ProfileStatsScreen
import com.example.ui.profile.SecurityCenterScreen
import com.example.ui.profile.ShopScreen
import com.example.ui.profile.VipCenterScreen
import com.example.ui.profile.WalletSheet
import com.example.ui.rooms.LiveVoiceRoomScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.WefunViewModel

private enum class ActiveSheet {
    NONE, WALLET, AVATAR_EDIT, EVENTS_RANKINGS, NOTIFICATIONS
}

private sealed interface ActiveOverlayScreen {
    data class GamePlaceholder(val game: PartyGame) : ActiveOverlayScreen
    data object Moments : ActiveOverlayScreen
    data class DiscoverCat(val category: DiscoverCategory) : ActiveOverlayScreen
    data class ProfileView(val user: UserProfile, val isOwn: Boolean) : ActiveOverlayScreen
    data class ProfileEdit(val user: UserProfile, val isOwn: Boolean) : ActiveOverlayScreen
    data class GiftWall(val ownerUid: String, val ownerName: String) : ActiveOverlayScreen
    data class CharmSystem(val user: UserProfile, val initialTab: Int = 0) : ActiveOverlayScreen
    data class GuardList(val ownerUid: String, val ownerName: String) : ActiveOverlayScreen
    data class AdvancedRooms(val ownerUid: String, val ownerName: String) : ActiveOverlayScreen
    data class ProfileStats(val user: UserProfile) : ActiveOverlayScreen
    data object VipCenter : ActiveOverlayScreen
    data object Shop : ActiveOverlayScreen
    data object PlayShow : ActiveOverlayScreen
    data object MyHome : ActiveOverlayScreen
    data object HelpCenter : ActiveOverlayScreen
    data object SecurityCenter : ActiveOverlayScreen
    data object AppSettings : ActiveOverlayScreen
    data object AdminPanel : ActiveOverlayScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WefunViewModel = viewModel()
            WEFUNTheme(darkTheme = false) {
                WefunAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WefunAppRoot(viewModel: WefunViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val rooms by viewModel.rooms.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val activeThreadId by viewModel.activeThreadId.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val followedUserIds by viewModel.followedUserIds.collectAsStateWithLifecycle()
    val dailyTasks by viewModel.dailyTasks.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val liveRoomState by viewModel.liveRoomState.collectAsStateWithLifecycle()
    val activeRoomContribution by viewModel.activeRoomContribution.collectAsStateWithLifecycle()
    val activeRoomContributors by viewModel.activeRoomContributors.collectAsStateWithLifecycle()
    val activeRoomGiftLedger by viewModel.activeRoomGiftLedger.collectAsStateWithLifecycle()
    val activeRoomActivityLogs by viewModel.activeRoomActivityLogs.collectAsStateWithLifecycle()
    val roomLevelConfigs by viewModel.roomLevelConfigs.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val packageInventory by viewModel.packageInventory.collectAsStateWithLifecycle()
    val relationshipRequests by viewModel.relationshipRequests.collectAsStateWithLifecycle()
    val acceptedRelationships by viewModel.acceptedRelationships.collectAsStateWithLifecycle()
    val customRelationshipTags by viewModel.customRelationshipTags.collectAsStateWithLifecycle()
    val isJoiningRoom by viewModel.isJoiningRoom.collectAsStateWithLifecycle()
    val roomJoinError by viewModel.roomJoinError.collectAsStateWithLifecycle()
    val publicProfileOverrides by viewModel.publicProfileOverrides.collectAsStateWithLifecycle()
    val profileGiftAnimation by viewModel.profileGiftAnimation.collectAsStateWithLifecycle()
    val vipConfigs by viewModel.vipConfigs.collectAsStateWithLifecycle()
    val vipGifts by viewModel.vipGifts.collectAsStateWithLifecycle()
    val vipAuditLogs by viewModel.vipAuditLogs.collectAsStateWithLifecycle()
    val vipMembershipState by viewModel.vipMembershipState.collectAsStateWithLifecycle()
    val vipGrowthHistory by viewModel.vipGrowthHistory.collectAsStateWithLifecycle()
    val vipPaymentHistory by viewModel.vipPaymentHistory.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            snackbarHostState.showSnackbar(toastMessage!!)
            viewModel.consumeToast()
        }
    }

    // Immediate non-null profile fallback — NO login/registration screen gate
    val baseUser = currentUser ?: remember { fallbackOwnUser() }
    val user = remember(baseUser, acceptedRelationships) {
        if (acceptedRelationships.isNotEmpty()) {
            baseUser.copy(activeRelationships = acceptedRelationships)
        } else {
            baseUser
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: WefunDestination.Home.route

    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }
    var initialVoiceRoomCategory by remember { mutableStateOf(RoomCategory.ALL) }
    var overlayStack by remember { mutableStateOf<List<ActiveOverlayScreen>>(emptyList()) }

    fun pushOverlay(screen: ActiveOverlayScreen) {
        overlayStack = overlayStack + screen
    }

    fun popOverlay() {
        if (overlayStack.isNotEmpty()) {
            overlayStack = overlayStack.dropLast(1)
        }
    }

    fun openUserProfileById(uid: String, name: String) {
        if (uid == user.uid) {
            pushOverlay(ActiveOverlayScreen.ProfileView(user, isOwn = true))
        } else {
            val target = viewModel.findPublicProfile(uid, name)
            pushOverlay(ActiveOverlayScreen.ProfileView(target, isOwn = false))
        }
    }

    val unreadChats = threads.sumOf { it.unreadCount }
    val unreadNotifs = notifications.count { !it.isRead }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!isWideScreen) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onSelectDestination = { destination ->
                            overlayStack = emptyList()
                            navController.navigateToWefunDestination(destination)
                        },
                        unreadChatsCount = unreadChats,
                        unreadNotificationsCount = unreadNotifs,
                        showVipGoldIndicator = user.vipLevel > 0,
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    WefunNavigationRail(
                        currentRoute = currentRoute,
                        onSelectDestination = { destination ->
                            overlayStack = emptyList()
                            navController.navigateToWefunDestination(destination)
                        },
                        unreadChatsCount = unreadChats,
                        unreadNotificationsCount = unreadNotifs,
                        showVipGoldIndicator = user.vipLevel > 0,
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AppNavigation(
                        navController = navController,
                        user = user,
                        settings = settings,
                        rooms = rooms,
                        events = events,
                        dailyTasks = dailyTasks,
                        posts = posts,
                        threads = threads,
                        activeThreadId = activeThreadId,
                        activeMessages = activeMessages,
                        followedUserIds = followedUserIds,
                        initialVoiceRoomCategory = initialVoiceRoomCategory,
                        onOpenWallet = { activeSheet = ActiveSheet.WALLET },
                        onOpenEventsRankings = { activeSheet = ActiveSheet.EVENTS_RANKINGS },
                        onOpenGamePlaceholder = { game ->
                            pushOverlay(ActiveOverlayScreen.GamePlaceholder(game))
                        },
                        onOpenGameRooms = {
                            initialVoiceRoomCategory = RoomCategory.GAME
                            navController.navigateToWefunDestination(WefunDestination.Party)
                        },
                        onOpenOwnFullProfile = {
                            pushOverlay(ActiveOverlayScreen.ProfileView(user, isOwn = true))
                        },
                        onOpenUserProfile = { uid, name ->
                            openUserProfileById(uid, name)
                        },
                        onOpenMoments = {
                            pushOverlay(ActiveOverlayScreen.Moments)
                        },
                        onOpenDiscoverCategory = { cat ->
                            pushOverlay(ActiveOverlayScreen.DiscoverCat(cat))
                        },
                        onOpenVipCenter = { pushOverlay(ActiveOverlayScreen.VipCenter) },
                        onOpenShop = { pushOverlay(ActiveOverlayScreen.Shop) },
                        onOpenPlayShow = { pushOverlay(ActiveOverlayScreen.PlayShow) },
                        onOpenMyHome = { pushOverlay(ActiveOverlayScreen.MyHome) },
                        onOpenAvatarEditor = { activeSheet = ActiveSheet.AVATAR_EDIT },
                        onOpenHelpCenter = { pushOverlay(ActiveOverlayScreen.HelpCenter) },
                        onOpenSecurityCenter = { pushOverlay(ActiveOverlayScreen.SecurityCenter) },
                        onOpenAppSettings = { pushOverlay(ActiveOverlayScreen.AppSettings) },
                        onEnterRoom = { room -> viewModel.joinRoom(room) },
                        onEnterRoomById = { roomId -> viewModel.joinRoomById(roomId) },
                        onCreateRoomListing = { title, desc, cat, pass ->
                            overlayStack = emptyList()
                            viewModel.createRoomListing(title, desc, cat, pass)
                        },
                        onCreateRoomConfigured = { title, desc, cat, pass, mode, backdrop ->
                            overlayStack = emptyList()
                            viewModel.createRoomListing(title, desc, cat, pass, mode, backdrop, autoEnter = true)
                        },
                        onStartOrOpenChat = { uid, name, isGrp ->
                            viewModel.startOrOpenChat(uid, name, isGrp)
                        },
                        onOpenThread = { id -> viewModel.openThread(id) },
                        onSendDirectMessage = { tid, txt, gift, voice ->
                            viewModel.sendDirectMessage(tid, txt, gift, voice)
                        },
                        onDeleteMessage = { id -> viewModel.deleteDirectMessage(id) },
                        onAcceptRequest = { tid -> viewModel.acceptFriendRequest(tid) },
                        onDeclineRequest = { tid -> viewModel.declineFriendRequest(tid) },
                        onToggleFollow = { uid -> viewModel.toggleFollow(uid) },
                        onClaimDailyTask = { taskId -> viewModel.claimDailyTask(taskId) },
                        onToggleParentalControl = { enabled -> viewModel.setParentalControl(enabled) },
                        onSelectLanguage = { lang -> viewModel.updateLanguage(lang) },
                        onShowToast = { msg -> viewModel.showToast(msg) },
                    )
                }
            }
        }

        // Minimized Live Voice Room Floating Pill Bar
        val minimizedRoom = liveRoomState.room
        if (minimizedRoom != null && liveRoomState.isMinimized) {
            Surface(
                shape = RoundedCornerShape(50),
                color = if (minimizedRoom.roomMode == RoomMode.AUCTION) PrimaryPurple else Color.White,
                border = BorderStroke(1.5.dp, TurquoiseActive),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 78.dp)
                    .clickable { viewModel.restoreVoiceRoom() }
                    .testTag("minimized_voice_room_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.GraphicEq,
                        contentDescription = "Live Voice Room",
                        tint = if (minimizedRoom.roomMode == RoomMode.AUCTION) Color.White else TurquoiseActive,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = minimizedRoom.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (minimizedRoom.roomMode == RoomMode.AUCTION) Color.White else PrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(
                        onClick = { viewModel.leaveVoiceRoom() },
                        modifier = Modifier.size(24.dp).testTag("minimized_room_leave_btn")
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Leave Voice Room",
                            tint = if (minimizedRoom.roomMode == RoomMode.AUCTION) Color.White else SecondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Full-Screen Secondary Destinations
        val topOverlay = overlayStack.lastOrNull()
        if (topOverlay != null) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                color = PrimaryBackground
            ) {
                when (topOverlay) {
                    is ActiveOverlayScreen.GamePlaceholder -> {
                        GamePlaceholderScreen(
                            game = topOverlay.game,
                            onBack = { popOverlay() },
                            onInviteFriends = { title ->
                                viewModel.showToast("Game invite link for $title copied!")
                            },
                            onOpenGameRooms = {
                                overlayStack = emptyList()
                                initialVoiceRoomCategory = RoomCategory.GAME
                                navController.navigateToWefunDestination(WefunDestination.Party)
                            }
                        )
                    }

                    ActiveOverlayScreen.Moments -> {
                        MomentsScreen(
                            posts = posts,
                            onBack = { popOverlay() },
                            onCreatePost = { content, tag, type, uri ->
                                viewModel.createSocialPost(content, tag, type, uri)
                            },
                            onToggleLikePost = { post -> viewModel.toggleLikePost(post) },
                            onCommentPost = { post -> viewModel.commentOnPost(post) },
                            onReportPost = { post ->
                                viewModel.submitReport("Moment", post.authorName, "Reported from Moments")
                            },
                            onOpenUserProfile = { uid, name -> openUserProfileById(uid, name) }
                        )
                    }

                    is ActiveOverlayScreen.DiscoverCat -> {
                        DiscoverCategoryScreen(
                            category = topOverlay.category,
                            followedUserIds = followedUserIds,
                            onBack = { popOverlay() },
                            onToggleFollow = { uid -> viewModel.toggleFollow(uid) },
                            onOpenUserProfile = { uid, name -> openUserProfileById(uid, name) },
                            onChatWithUser = { uid, name ->
                                viewModel.startOrOpenChat(uid, name, false)
                                overlayStack = emptyList()
                                navController.navigateToWefunDestination(WefunDestination.Chats)
                            }
                        )
                    }

                    is ActiveOverlayScreen.ProfileView -> {
                        val displayUser = if (topOverlay.isOwn) {
                            user
                        } else {
                            val livePublic = publicProfileOverrides[topOverlay.user.uid] ?: topOverlay.user
                            val syncedRels = acceptedRelationships.filter { it.partnerUserId == livePublic.uid }
                            if (syncedRels.isNotEmpty() || livePublic.activeRelationships.isEmpty()) {
                                livePublic.copy(activeRelationships = syncedRels)
                            } else {
                                livePublic
                            }
                        }
                        val giftWall by remember(displayUser.uid) {
                            viewModel.observeGiftWall(displayUser.uid)
                        }.collectAsStateWithLifecycle(
                            initialValue = GiftWallSummary(displayUser.uid, displayUser.giftsReceivedCount, 0L, emptyList(), emptyList())
                        )
                        val guards by remember(displayUser.uid) {
                            viewModel.observeAuthorizedGuards(displayUser.uid)
                        }.collectAsStateWithLifecycle(initialValue = emptyList())
                        val advancedRooms by remember(displayUser.uid) {
                            viewModel.observeAdvancedRooms(displayUser.uid)
                        }.collectAsStateWithLifecycle(initialValue = emptyList())

                        FullProfileScreen(
                            profileUser = displayUser,
                            isOwnProfile = topOverlay.isOwn,
                            isFollowing = followedUserIds.contains(displayUser.uid),
                            posts = posts,
                            onBack = { popOverlay() },
                            onEditProfile = {
                                pushOverlay(ActiveOverlayScreen.ProfileEdit(displayUser, topOverlay.isOwn))
                            },
                            onOpenSettings = {
                                if (topOverlay.isOwn) {
                                    pushOverlay(ActiveOverlayScreen.ProfileEdit(displayUser, true))
                                } else {
                                    pushOverlay(ActiveOverlayScreen.AppSettings)
                                }
                            },
                            onToggleFollow = { viewModel.toggleFollow(displayUser.uid) },
                            onMessageUser = {
                                viewModel.startOrOpenChat(displayUser.uid, displayUser.nickname, false)
                                overlayStack = emptyList()
                                navController.navigateToWefunDestination(WefunDestination.Chats)
                            },
                            onPickPhoto = { uri ->
                                viewModel.updateProfileAndAvatar(
                                    user.nickname,
                                    user.bio,
                                    user.country,
                                    user.language,
                                    user.showGender,
                                    user.showAge,
                                    uri,
                                    user.avatarConfig
                                )
                            },
                            onToggleLikePost = { post -> viewModel.toggleLikePost(post) },
                            onShowToast = { msg -> viewModel.showToast(msg) },
                            viewerCoins = user.coins,
                            packageInventory = packageInventory,
                            giftWallSummary = giftWall,
                            guards = guards,
                            advancedRooms = advancedRooms,
                            activeGiftAnimation = profileGiftAnimation,
                            onOpenCharmSystem = {
                                pushOverlay(ActiveOverlayScreen.CharmSystem(displayUser))
                            },
                            onOpenMoments = { pushOverlay(ActiveOverlayScreen.Moments) },
                            onOpenGiftWallCollection = {
                                pushOverlay(ActiveOverlayScreen.GiftWall(displayUser.uid, displayUser.nickname))
                            },
                            onUpdateSignature = { newSig ->
                                viewModel.updateProfileSignature(displayUser.uid, newSig)
                            },
                            onCancelRelationship = { relId ->
                                viewModel.cancelRelationship(relId)
                            },
                            onOpenGuardList = {
                                pushOverlay(ActiveOverlayScreen.GuardList(displayUser.uid, displayUser.nickname))
                            },
                            onOpenGuardProfile = { gUid, gName ->
                                openUserProfileById(gUid, gName)
                            },
                            onEnterRoomById = { roomId ->
                                viewModel.joinRoomById(roomId)
                            },
                            onOpenAdvancedRoomList = {
                                pushOverlay(ActiveOverlayScreen.AdvancedRooms(displayUser.uid, displayUser.nickname))
                            },
                            onOpenStats = {
                                pushOverlay(ActiveOverlayScreen.ProfileStats(displayUser))
                            },
                            onSendGiftToProfile = { gift, qty, fromPkg ->
                                viewModel.sendGiftFromProfile(displayUser, gift, qty, fromPkg)
                            },
                            onOpenWallet = { activeSheet = ActiveSheet.WALLET },
                        )
                    }

                    is ActiveOverlayScreen.ProfileEdit -> {
                        val liveTarget = if (topOverlay.isOwn) user else (publicProfileOverrides[topOverlay.user.uid] ?: topOverlay.user)
                        ProfileEditScreen(
                            user = liveTarget,
                            isOwner = topOverlay.isOwn && liveTarget.uid == user.uid,
                            onBack = { popOverlay() },
                            onSave = { tUid, nick, bio, country, lang, showG, showA, photo, cover, onDone ->
                                viewModel.saveFullProfileEdits(
                                    targetUid = tUid,
                                    nickname = nick,
                                    bio = bio,
                                    country = country,
                                    language = lang,
                                    showGender = showG,
                                    showAge = showA,
                                    photoUri = photo,
                                    coverPhotoUri = cover,
                                    onComplete = onDone,
                                )
                            },
                            onSaveVipPrivacy = { customTitle, hideGifts, hideGuard, privateVisit, friendProt, theme ->
                                viewModel.updateVipPrivacySettings(
                                    customTitle = customTitle,
                                    hideRecentGifts = hideGifts,
                                    hideGuardianBoard = hideGuard,
                                    privateProfileVisit = privateVisit,
                                    friendRequestProtection = friendProt,
                                    customNamecardTheme = theme,
                                )
                            }
                        )
                    }

                    is ActiveOverlayScreen.GiftWall -> {
                        val summary by remember(topOverlay.ownerUid) {
                            viewModel.observeGiftWall(topOverlay.ownerUid)
                        }.collectAsStateWithLifecycle(
                            initialValue = GiftWallSummary(topOverlay.ownerUid, 0, 0L, emptyList(), emptyList())
                        )
                        GiftWallCollectionScreen(
                            ownerName = topOverlay.ownerName,
                            summary = summary,
                            onBack = { popOverlay() },
                        )
                    }

                    is ActiveOverlayScreen.CharmSystem -> {
                        val liveTarget = if (topOverlay.user.uid == user.uid) user else (publicProfileOverrides[topOverlay.user.uid] ?: topOverlay.user)
                        val progress = remember(liveTarget.charmPoints) {
                            CharmLevelCatalog.calculateCharmProgress(liveTarget.charmPoints)
                        }
                        val activeLevel = (liveTarget.userLevel / 5).coerceAtLeast(1)
                        val effects by remember(liveTarget.uid, progress.currentLevel, activeLevel) {
                            viewModel.observeSpecialEffects(liveTarget.uid, progress.currentLevel, activeLevel)
                        }.collectAsStateWithLifecycle(initialValue = emptyList())

                        CharmLevelScreen(
                            userDisplayName = liveTarget.nickname,
                            userCharmPoints = liveTarget.charmPoints,
                            userActiveLevel = activeLevel,
                            specialEffects = effects,
                            initialTab = topOverlay.initialTab,
                            onBack = { popOverlay() },
                            onToggleSpecialEffect = { effectId ->
                                viewModel.toggleSpecialEffect(effectId)
                            }
                        )
                    }

                    is ActiveOverlayScreen.GuardList -> {
                        val guards by remember(topOverlay.ownerUid) {
                            viewModel.observeAuthorizedGuards(topOverlay.ownerUid)
                        }.collectAsStateWithLifecycle(initialValue = emptyList())
                        GuardListScreen(
                            ownerName = topOverlay.ownerName,
                            guards = guards,
                            onBack = { popOverlay() },
                            onOpenUserProfile = { uid, name -> openUserProfileById(uid, name) }
                        )
                    }

                    is ActiveOverlayScreen.AdvancedRooms -> {
                        val advRooms by remember(topOverlay.ownerUid) {
                            viewModel.observeAdvancedRooms(topOverlay.ownerUid)
                        }.collectAsStateWithLifecycle(initialValue = emptyList())
                        AdvancedRoomListScreen(
                            ownerName = topOverlay.ownerName,
                            rooms = advRooms,
                            onBack = { popOverlay() },
                            onEnterRoomById = { roomId -> viewModel.joinRoomById(roomId) }
                        )
                    }

                    is ActiveOverlayScreen.ProfileStats -> {
                        val liveTarget = if (topOverlay.user.uid == user.uid) user else (publicProfileOverrides[topOverlay.user.uid] ?: topOverlay.user)
                        val stats by remember(liveTarget.uid, liveTarget.charmPoints, liveTarget.giftsReceivedCount) {
                            viewModel.observeProfileStatistics(liveTarget)
                        }.collectAsStateWithLifecycle(
                            initialValue = ProfileStatistics(
                                userId = liveTarget.uid,
                                accountLevel = liveTarget.userLevel,
                                xp = liveTarget.xp,
                                nextLevelXp = liveTarget.nextLevelXp,
                                charmPoints = liveTarget.charmPoints,
                                charmLevel = CharmLevelCatalog.calculateCharmProgress(liveTarget.charmPoints).currentLevel,
                                followers = liveTarget.followersCount,
                                following = liveTarget.followingCount,
                                friends = liveTarget.friendsCount,
                                giftsReceived = liveTarget.giftsReceivedCount,
                                giftsSent = liveTarget.giftsSentCount,
                                totalStars = 0L,
                                totalMoments = 0,
                                roomsJoined = 0,
                                roomsHosted = 0,
                                gameStats = emptyList(),
                            )
                        )
                        ProfileStatsScreen(
                            ownerName = liveTarget.nickname,
                            stats = stats,
                            onBack = { popOverlay() },
                        )
                    }

                    ActiveOverlayScreen.VipCenter -> {
                        VipCenterScreen(
                            user = user,
                            vipConfigs = vipConfigs,
                            membershipState = vipMembershipState,
                            growthHistory = vipGrowthHistory,
                            paymentHistory = vipPaymentHistory,
                            vipGifts = vipGifts,
                            onBack = { popOverlay() },
                            onActivateVip = { lvl, cost, name ->
                                viewModel.purchaseVip(lvl, cost, name)
                            },
                            onClaimDailyDiamonds = { viewModel.claimVipDailyDiamonds() },
                            onToggleAutoRenewal = { enabled -> viewModel.toggleVipAutoRenewal(enabled) },
                            onRestorePurchases = { viewModel.restoreVipPurchases() },
                            onCheckRegulation = { viewModel.evaluateVipRegulation() },
                            onSpendDiamondsForGrowth = { cat, amt, desc ->
                                viewModel.spendDiamondsForGrowth(cat, amt, desc)
                            },
                            onSendVipGift = { gift ->
                                viewModel.sendVipExclusiveGift(gift, user.uid, user.nickname)
                            },
                            onOpenAdminPanel = { pushOverlay(ActiveOverlayScreen.AdminPanel) }
                        )
                    }

                    ActiveOverlayScreen.Shop -> {
                        ShopScreen(
                            user = user,
                            onBack = { popOverlay() },
                            onPreviewItem = { msg -> viewModel.showToast(msg) },
                            onShopLuckyDraw = {
                                viewModel.spendDiamondsForGrowth(
                                    category = DiamondSpendCategory.SHOP_LUCKY_DRAW,
                                    diamondsAmount = 100L,
                                    description = "Shop Diamond Lucky Draw"
                                )
                            }
                        )
                    }

                    ActiveOverlayScreen.PlayShow -> {
                        PlayShowScreen(
                            user = user,
                            onBack = { popOverlay() },
                            onSaveAvatarConfig = { cfg -> viewModel.saveAvatarConfig(cfg) }
                        )
                    }

                    ActiveOverlayScreen.MyHome -> {
                        MyHomeScreen(
                            user = user,
                            onBack = { popOverlay() },
                            onOpenPlayShow = { pushOverlay(ActiveOverlayScreen.PlayShow) }
                        )
                    }

                    ActiveOverlayScreen.HelpCenter -> {
                        HelpCenterScreen(
                            onBack = { popOverlay() },
                            onSubmitProblem = { type, name, reason ->
                                viewModel.submitReport(type, name, reason)
                            }
                        )
                    }

                    ActiveOverlayScreen.SecurityCenter -> {
                        SecurityCenterScreen(
                            user = user,
                            settings = settings,
                            onBack = { popOverlay() },
                            onToggleParentalControl = { enabled -> viewModel.setParentalControl(enabled) },
                            onUnblockUser = { uid, name -> viewModel.toggleBlockUser(uid, name) }
                        )
                    }

                    ActiveOverlayScreen.AppSettings -> {
                        AppSettingsScreen(
                            user = user,
                            settings = settings,
                            onBack = { popOverlay() },
                            onToggleParentalControl = { enabled -> viewModel.setParentalControl(enabled) },
                            onSelectLanguage = { lang -> viewModel.updateLanguage(lang) },
                            onUpdateNotificationPrefs = { g, r, p, m ->
                                viewModel.updateNotificationPrefs(g, r, p, m)
                            },
                            onOpenAdminPanel = { pushOverlay(ActiveOverlayScreen.AdminPanel) }
                        )
                    }

                    ActiveOverlayScreen.AdminPanel -> {
                        AdminPanelScreen(
                            user = user,
                            vipConfigs = vipConfigs,
                            vipGifts = vipGifts,
                            vipAuditLogs = vipAuditLogs,
                            onBack = { popOverlay() },
                            onUpdateVipConfig = { cfg -> viewModel.adminUpdateVipConfig(cfg) },
                            onGrantUserVip = { lvl, mo, active -> viewModel.adminGrantUserVip(lvl, mo, active) },
                            onAdjustGrowthPoints = { delta, reason -> viewModel.adminAdjustGrowthPoints(delta, reason) },
                            onSaveVipGift = { gift -> viewModel.adminSaveVipGift(gift) }
                        )
                    }
                }
            }
        }

        // Full-Screen Live Voice Room & Auction Room Stage
        if (liveRoomState.room != null && !liveRoomState.isMinimized) {
            LiveVoiceRoomScreen(
                session = liveRoomState,
                currentUser = user,
                audioState = audioState,
                levelConfigs = roomLevelConfigs,
                contributionSummary = activeRoomContribution,
                contributors = activeRoomContributors,
                giftLedger = activeRoomGiftLedger,
                activityLogs = activeRoomActivityLogs,
                packageInventory = packageInventory,
                relationshipRequests = relationshipRequests,
                customRelationshipTags = customRelationshipTags,
                onRefreshMicPermission = { viewModel.refreshMicPermission() },
                onMinimize = { viewModel.minimizeVoiceRoom() },
                onLeave = { viewModel.leaveVoiceRoom() },
                onToggleFavorite = { viewModel.toggleFavoriteRoom() },
                onToggleMute = { viewModel.toggleMicMute() },
                onTapSeat = { idx -> viewModel.tapSeat(idx) },
                onSendChat = { msg, emoji -> viewModel.sendRoomChat(msg, emoji) },
                onSendBulletComment = { text -> viewModel.sendRoomChat(text, isEmoji = false, asBulletComment = true) },
                onPlayDiamondBingo = { viewModel.playVoiceRoomBingo(100L) },
                onSendGift = { gift, combo, seats, label, fromPkg ->
                    viewModel.sendVirtualGiftInRoom(gift, combo, seats, label, fromPkg)
                },
                onAcceptMicRequest = { req -> viewModel.acceptMicRequest(req) },
                onRejectMicRequest = { uid -> viewModel.rejectMicRequest(uid) },
                onToggleSeatLock = { idx -> viewModel.toggleSeatLock(idx) },
                onToggleMicLock = { idx -> viewModel.toggleSeatMicLock(idx) },
                onToggleOccupantMute = { idx -> viewModel.toggleOccupantMuteByAdmin(idx) },
                onReserveSeat = { idx, name -> viewModel.reserveSeat(idx, name) },
                onAssignUserToSeat = { idx, uid, name, vip ->
                    viewModel.assignUserToSeat(idx, uid, name, vip)
                },
                onToggleCoHost = { idx -> viewModel.toggleSeatCoHost(idx) },
                onRemoveFromSeat = { idx -> viewModel.removeUserFromSeat(idx) },
                onBanUser = { uid, name -> viewModel.banUserFromRoom(uid, name) },
                onStartMoveSeat = { idx -> viewModel.startMoveSeat(idx) },
                onTransferHost = { idx -> viewModel.transferHostToSeat(idx) },
                onAssignMemberRole = { uid, name, role -> viewModel.assignMemberRole(uid, name, role) },
                onAssignPartner = { uid, name -> viewModel.assignPartner(uid, name) },
                onRemovePartner = { viewModel.removePartner() },
                onTogglePartnerSeatEnabled = { enabled -> viewModel.togglePartnerSeatEnabled(enabled) },
                onChangeBackdropTheme = { theme -> viewModel.updateRoomBackdropTheme(theme) },
                onPublishAnnouncement = { cat, title, content ->
                    viewModel.publishRoomAnnouncement(cat, title, content)
                },
                onUpdateAppearance = { title, style, decor, musicAdmin ->
                    viewModel.updateRoomAppearance(title, style, decor, musicAdmin)
                },
                onUpdateAdminLevelConfig = { counts, pts, dia ->
                    viewModel.updateAdminLevelSeatConfig(counts, pts, dia)
                },
                onSwitchRoomMode = { mode -> viewModel.switchRoomMode(mode) },
                onSaveFullRoomSettings = { t, a, p, m, tags, bg, hq, bTxt, bPho, bRed, fee, mus ->
                    viewModel.updateFullRoomSettings(t, a, p, m, tags, bg, hq, bTxt, bPho, bRed, fee, mus)
                },
                onCreateCustomTag = { lbl, mode -> viewModel.createCustomRelationshipTag(lbl, mode) },
                onConfirmAuctionRelationshipSetup = { mode, tag, customLbl, tok, tid, tname, price ->
                    viewModel.confirmAuctionRelationshipSetup(mode, tag, customLbl, tok, tid, tname, price)
                },
                onPlaceAuctionBid = { pts -> viewModel.placeAuctionBid(pts) },
                onAcceptRelationshipRequest = { reqId -> viewModel.acceptRelationshipRequest(reqId) },
                onDeclineRelationshipRequest = { reqId -> viewModel.declineRelationshipRequest(reqId) },
                onStartPk = { mode, opp -> viewModel.startPkBattle(mode, opp) },
                onEndPk = { viewModel.endPkBattle() },
                onToggleMusic = { viewModel.toggleRoomMusic() },
                onSelectOrRequestTrack = { track -> viewModel.selectOrRequestTrack(track) },
                onApproveSongRequest = { item -> viewModel.approveSongRequest(item) },
                onReportRoomOrUser = { type, target, reason ->
                    viewModel.submitReport(type, target, reason)
                },
                onBlockUser = { uid, name -> viewModel.toggleBlockUser(uid, name) },
                onOpenWallet = { activeSheet = ActiveSheet.WALLET },
                onInviteFriendsToast = { msg -> viewModel.showToast(msg) },
                onDismissLevelUp = { viewModel.dismissLevelUpBanner() },
            )
        }

        if (isJoiningRoom) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PrimaryBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = TurquoiseActive, modifier = Modifier.size(22.dp))
                        Text("Joining Voice Room...", fontWeight = FontWeight.SemiBold, color = PrimaryText)
                    }
                }
            }
        }
    }

    if (roomJoinError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.consumeRoomJoinError() },
            containerColor = PrimaryBackground,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Unable to Join Room", fontWeight = FontWeight.Bold, color = PrimaryText) },
            text = { Text(roomJoinError!!, color = SecondaryText) },
            confirmButton = {
                TextButton(onClick = { viewModel.consumeRoomJoinError() }) {
                    Text("OK", color = TurquoiseActive, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Ecosystem Modal Sheets
    when (activeSheet) {
        ActiveSheet.WALLET -> WalletSheet(
            user = user,
            transactions = transactions,
            onRecharge = { pkg -> viewModel.rechargeCoins(pkg) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.AVATAR_EDIT -> AvatarAndProfileEditSheet(
            user = user,
            onSave = { nick, bio, country, lang, showG, showA, cfg ->
                viewModel.updateProfileAndAvatar(nick, bio, country, lang, showG, showA, null, cfg)
            },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.EVENTS_RANKINGS -> EventsAndRankingsSheet(
            events = events,
            onToggleRegisterEvent = { id -> viewModel.toggleEventRegistration(id) },
            onEventLuckyDraw = {
                viewModel.spendDiamondsForGrowth(
                    category = DiamondSpendCategory.EVENT_LUCKY_DRAW,
                    diamondsAmount = 100L,
                    description = "Event Lucky Draw"
                )
            },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.NOTIFICATIONS -> NotificationsSheet(
            notifications = notifications,
            settings = settings,
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onUpdatePrefs = { g, r, p, m -> viewModel.updateNotificationPrefs(g, r, p, m) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.NONE -> {}
    }
}

private fun fallbackOwnUser() = UserProfile(
    uid = "usr_nova_01",
    wefunId = "WF-804921",
    username = "ariavance",
    nickname = "Aria Vance",
    bio = "Music host, late-night storyteller & Ludo strategist. Come grab a mic!",
    vipLevel = 2,
    userLevel = 14,
    xp = 3450,
    nextLevelXp = 5000,
    coins = 2800,
    diamonds = 640,
    bonusPoints = 320,
    followersCount = 1280,
    followingCount = 184,
    friendsCount = 72,
    visitorsCount = 10,
)
