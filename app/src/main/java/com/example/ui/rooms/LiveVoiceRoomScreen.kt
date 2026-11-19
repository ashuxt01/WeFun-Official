package com.example.ui.rooms

import android.Manifest
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioEngineState
import com.example.data.model.AnnouncementCategory
import com.example.data.model.BiddingMode
import com.example.data.model.ContributorRankEntry
import com.example.data.model.MicRequest
import com.example.data.model.MusicTrack
import com.example.data.model.RelationshipRequest
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomGiftLedgerEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomMessageType
import com.example.data.model.RoomMode
import com.example.data.model.RoomRole
import com.example.data.model.RoomSeat
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SongRequestItem
import com.example.data.model.StartingToken
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.features.voice_room.ui.components.OwnerAndPartnerStage
import com.example.features.voice_room.ui.components.OwnerPartnerModalSheet
import com.example.features.voice_room.ui.components.RoomBackdropThemePickerSheet
import com.example.features.voice_room.ui.components.VoiceRoomAnnouncementCard
import com.example.features.voice_room.ui.components.VoiceRoomBackdrop
import com.example.features.voice_room.ui.components.VoiceRoomHeaderBar
import com.example.features.voice_room.ui.components.VoiceRoomSeatingGrid
import com.example.features.voice_room.ui.components.VoiceRoomTopNav
import com.example.features.voice_room.ui.components.VoiceSeatActionSheet
import com.example.ui.components.VipBadge
import com.example.ui.components.VipEntranceBannerOverlay
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.viewmodel.LiveRoomSessionState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveVoiceRoomScreen(
    session: LiveRoomSessionState,
    currentUser: UserProfile,
    audioState: AudioEngineState,
    levelConfigs: List<RoomLevelConfig>,
    contributionSummary: RoomContributionSummary?,
    contributors: List<ContributorRankEntry>,
    giftLedger: List<RoomGiftLedgerEntry>,
    activityLogs: List<RoomActivityLogEntry>,
    packageInventory: Map<String, Int> = emptyMap(),
    relationshipRequests: List<RelationshipRequest> = emptyList(),
    customRelationshipTags: List<RelationshipTagOption> = emptyList(),
    onRefreshMicPermission: () -> Unit,
    onMinimize: () -> Unit,
    onLeave: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleMute: () -> Unit,
    onTapSeat: (Int) -> Unit,
    onSendChat: (String, Boolean) -> Unit,
    onSendBulletComment: (String) -> Unit = { text -> onSendChat(text, false) },
    onPlayDiamondBingo: () -> Unit = {},
    onSendGift: (VirtualGift, Int, List<Int>, String, Boolean) -> Unit,
    onAcceptMicRequest: (MicRequest) -> Unit,
    onRejectMicRequest: (String) -> Unit,
    onToggleSeatLock: (Int) -> Unit,
    onToggleMicLock: (Int) -> Unit,
    onToggleOccupantMute: (Int) -> Unit,
    onReserveSeat: (Int, String?) -> Unit,
    onAssignUserToSeat: (Int, String, String, Int) -> Unit,
    onToggleCoHost: (Int) -> Unit,
    onRemoveFromSeat: (Int) -> Unit,
    onBanUser: (String, String) -> Unit,
    onStartMoveSeat: (Int) -> Unit,
    onTransferHost: (Int) -> Unit,
    onAssignMemberRole: (String, String, RoomRole) -> Unit,
    onAssignPartner: (String, String) -> Unit,
    onRemovePartner: () -> Unit,
    onTogglePartnerSeatEnabled: (Boolean) -> Unit = {},
    onChangeBackdropTheme: (VoiceRoomBackdropTheme) -> Unit,
    onPublishAnnouncement: (AnnouncementCategory, String, String) -> Unit,
    onUpdateAppearance: (String, RoomThemeStyle, String, Boolean) -> Unit,
    onUpdateAdminLevelConfig: (Map<Int, Int>, Double, Double) -> Unit,
    onSwitchRoomMode: (RoomMode) -> Unit = {},
    onSaveFullRoomSettings: (
        String, String, String?, RoomMode, List<String>, VoiceRoomBackdropTheme,
        Boolean, Boolean, Boolean, Boolean, Long, Boolean
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onCreateCustomTag: (String, BiddingMode) -> Result<RelationshipTagOption> = { label, mode ->
        Result.success(RelationshipTagOption("custom", label, mode, "clover", true))
    },
    onConfirmAuctionRelationshipSetup: (
        BiddingMode, RelationshipTagOption?, String, StartingToken?, String?, String?, Long
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    onPlaceAuctionBid: (Long) -> Unit = {},
    onAcceptRelationshipRequest: (String) -> Unit = {},
    onDeclineRelationshipRequest: (String) -> Unit = {},
    onStartPk: (String, String) -> Unit,
    onEndPk: () -> Unit,
    onToggleMusic: () -> Unit,
    onSelectOrRequestTrack: (MusicTrack) -> Unit,
    onApproveSongRequest: (SongRequestItem) -> Unit,
    onReportRoomOrUser: (String, String, String) -> Unit,
    onBlockUser: (String, String) -> Unit,
    onOpenWallet: () -> Unit,
    onInviteFriendsToast: (String) -> Unit,
    onDismissLevelUp: () -> Unit,
) {
    val room = session.room ?: return
    val context = LocalContext.current
    val isDarkStage = room.backdropTheme.isDarkStage

    BackHandler { onMinimize() }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onRefreshMicPermission()
        if (granted) onToggleMute()
    }

    var chatInput by remember { mutableStateOf("") }
    var bulletCommentMode by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var selectedSeatForSheet by remember { mutableStateOf<RoomSeat?>(null) }
    var showOwnerPartnerModal by remember { mutableStateOf<String?>(null) } // "OWNER" or "PARTNER"
    var showMoreFeaturesSheet by remember { mutableStateOf(false) }
    var showRoomInfoSheet by remember { mutableStateOf(false) }
    var showContributionSheet by remember { mutableStateOf(false) }
    var showManagementSheet by remember { mutableStateOf(false) }
    var showRoomSettingsScreen by remember { mutableStateOf(false) }
    var showRelationshipSettingsSheet by remember { mutableStateOf(false) }
    var showMusicSheet by remember { mutableStateOf(false) }
    var showThemePickerSheet by remember { mutableStateOf(false) }
    var showPkDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    val listState = rememberLazyListState()
    LaunchedEffect(session.messages.size) {
        if (session.messages.isNotEmpty()) {
            listState.animateScrollToItem(session.messages.lastIndex)
        }
    }

    val shareRoomAction: () -> Unit = {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Join me live on WEFUN in '${room.title}' (ID: ${room.id}, Lv.${room.roomLevel})!")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share WEFUN Voice Room"))
    }

    val currentLevel = contributionSummary?.currentLevel ?: room.roomLevel
    val stageBarColor = if (isDarkStage) Color.Black.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.94f)
    val stageBarBorder = if (isDarkStage) Color.White.copy(alpha = 0.14f) else BorderColor
    val stageTextColor = if (isDarkStage) Color.White else PrimaryText

    VoiceRoomBackdrop(theme = room.backdropTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // A. TOP NAVIGATION AREA
            VoiceRoomTopNav(
                room = room,
                roomLevel = currentLevel,
                owner = session.ownerInfo,
                partner = session.partnerInfo,
                memberRoles = session.memberRoles,
                isDarkStage = isDarkStage,
                onBackMinimize = onMinimize,
                onToggleFavorite = onToggleFavorite,
                onCopyRoomId = { id -> onInviteFriendsToast("Copied Room ID $id to clipboard!") },
                onClickOwner = { showOwnerPartnerModal = "OWNER" },
                onClickCoOwner = { showOwnerPartnerModal = "PARTNER" },
                onOpenRoomInfo = { showRoomInfoSheet = true },
                onOpenContribution = { showContributionSheet = true },
                onOpenManagement = { showManagementSheet = true },
                onOpenRoomSettings = { showRoomSettingsScreen = true },
                onOpenThemePicker = { showThemePickerSheet = true },
                onShareRoom = shareRoomAction,
                onReportRoom = { showReportDialog = "Room" to room.title },
                onExitRoom = onLeave,
            )

            Spacer(modifier = Modifier.height(6.dp))

            // B. ROOM HEADER BAR (Audio status, Selectable Backdrop Theme, Level + Exp Progress)
            VoiceRoomHeaderBar(
                room = room,
                audioState = audioState,
                contributionSummary = contributionSummary,
                isDarkStage = isDarkStage,
                onOpenThemePicker = { showThemePickerSheet = true },
                onOpenContribution = { showContributionSheet = true },
            )

            VipEntranceBannerOverlay(
                visible = session.vipEntryBanner != null && session.vipEntryLevel > 0,
                vipLevel = session.vipEntryLevel,
                nickname = session.vipEntryUserName.ifBlank { currentUser.nickname },
                effectTitle = session.vipEntryEffectTitle.ifBlank { "VIP Stage Arrival" },
                modifier = Modifier.padding(top = 6.dp)
            )

            if (room.isPkActive) {
                Spacer(modifier = Modifier.height(6.dp))
                PkBattleBar(
                    leftName = room.hostName,
                    rightName = room.pkOpponentName,
                    leftScore = room.pkScoreLeft,
                    rightScore = room.pkScoreRight,
                    secondsLeft = room.pkSecondsLeft,
                    modeLabel = room.pkMode
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (room.roomMode == RoomMode.AUCTION) {
                AuctionRoomScreen(
                    session = session,
                    currentUserId = currentUser.uid,
                    pendingRequests = relationshipRequests,
                    onSeatClick = { seat ->
                        if (session.pendingMoveFromSeat != null && seat.occupantId == null && !seat.isSeatLocked) {
                            onTapSeat(seat.seatIndex)
                        } else {
                            selectedSeatForSheet = seat
                        }
                    },
                    onOpenRelationshipSettings = { showRelationshipSettingsSheet = true },
                    onPlaceBid = onPlaceAuctionBid,
                    onAcceptRequest = onAcceptRelationshipRequest,
                    onDeclineRequest = onDeclineRelationshipRequest,
                )
            } else {
                // D. DEDICATED OWNER & PARTNER STAGE + MUSIC PLAYER
                OwnerAndPartnerStage(
                    owner = session.ownerInfo,
                    partner = session.partnerInfo,
                    isMusicPlaying = room.isMusicPlaying,
                    currentTrack = session.currentTrack,
                    isDarkStage = isDarkStage,
                    onClickOwner = { showOwnerPartnerModal = "OWNER" },
                    onClickPartner = { showOwnerPartnerModal = "PARTNER" },
                    onClickMusic = { showMusicSheet = true },
                )

                Spacer(modifier = Modifier.height(10.dp))

                // C. VOICE SEATING AREA (8 seats Level 1..26 seats Level 10, 4 states & badges)
                VoiceRoomSeatingGrid(
                    seats = session.seats,
                    currentUserSeatIndex = session.currentUserSeatIndex,
                    pendingMoveFromSeat = session.pendingMoveFromSeat,
                    isDarkStage = isDarkStage,
                    muteProtectedSeatIndices = session.muteProtectedSeatIndices,
                    onSeatClick = { seat ->
                        if (session.pendingMoveFromSeat != null && seat.occupantId == null && !seat.isSeatLocked) {
                            onTapSeat(seat.seatIndex)
                        } else {
                            selectedSeatForSheet = seat
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ANNOUNCEMENT BANNER
            VoiceRoomAnnouncementCard(
                announcement = room.announcement,
                isDarkStage = isDarkStage,
                onClick = { if (session.currentUserRole.canModerate()) showManagementSheet = true else showRoomInfoSheet = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SYSTEM & LIVE CHAT FEED
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(stageBarColor)
                    .border(1.dp, stageBarBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(session.messages, key = { it.id }) { msg ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (msg.type) {
                            RoomMessageType.SYSTEM -> {
                                Text(
                                    text = "🔔 system: ${msg.text}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkStage) PremiumGold else PrimaryPurple
                                )
                            }
                            RoomMessageType.ENTRY -> {
                                Text(
                                    text = "✨ ${msg.text}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyanPulse
                                )
                            }
                            RoomMessageType.GIFT -> {
                                Text(
                                    text = "🎁 ${msg.senderName} ${msg.text}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PinkAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            else -> {
                                if (msg.isBulletComment) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(0xFF4C1D95).copy(alpha = 0.88f),
                                        border = BorderStroke(1.2.dp, PremiumGold),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("room_bullet_comment_item")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            VipLevelBadge3D(vipLevel = msg.senderVip, compact = true)
                                            VipNicknameText(
                                                name = "${msg.senderName}:",
                                                vipLevel = msg.senderVip,
                                                style = MaterialTheme.typography.labelMedium,
                                                onDarkBackground = true,
                                            )
                                            Text(
                                                text = msg.text,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = PremiumGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    if (msg.senderVip > 0) {
                                        VipLevelBadge3D(
                                            vipLevel = msg.senderVip,
                                            compact = true,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        VipNicknameText(
                                            name = msg.senderName,
                                            vipLevel = msg.senderVip,
                                            style = MaterialTheme.typography.bodySmall,
                                            onDarkBackground = isDarkStage,
                                        )
                                        Text(
                                            text = ": ${msg.text}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = stageTextColor
                                        )
                                    } else {
                                        Text(
                                            text = "${msg.senderName}: ${msg.text}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = stageTextColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Emoji Reactions
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(listOf("🔥", "👏", "💜", "😂", "👑", "🎶", "🙏")) { emoji ->
                    Surface(
                        onClick = { onSendChat(emoji, true) },
                        shape = CircleShape,
                        color = stageBarColor,
                        border = BorderStroke(0.8.dp, stageBarBorder)
                    ) {
                        Text(emoji, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // BOTTOM CONTROLS (Speaker/Mic, Type..., Gift, Wallet, More Grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (session.currentUserSeatIndex == null) {
                            val firstOpen = session.seats.firstOrNull { it.occupantId == null && !it.isSeatLocked }?.seatIndex ?: 0
                            onTapSeat(firstOpen)
                        } else if (!audioState.hasRecordPermission && session.isUserMuted) {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            onToggleMute()
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (session.isUserMuted || session.currentUserSeatIndex == null) stageBarColor
                            else PrimaryPurple
                        )
                        .border(1.dp, PremiumGold.copy(alpha = 0.55f), CircleShape)
                        .testTag("room_mic_toggle_button")
                ) {
                    Icon(
                        imageVector = when {
                            session.currentUserSeatIndex == null -> Icons.AutoMirrored.Rounded.VolumeUp
                            session.isUserMuted -> Icons.Rounded.MicOff
                            else -> Icons.Rounded.Mic
                        },
                        contentDescription = "Voice speaker / mic toggle",
                        tint = if (session.isUserMuted || session.currentUserSeatIndex == null) stageTextColor else PremiumGold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = stageBarColor,
                    border = BorderStroke(1.dp, stageBarBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (chatInput.isEmpty()) {
                                Text(
                                    text = if (room.banTextChatting && !session.currentUserRole.canModerate()) {
                                        "Text chatting paused by room admin"
                                    } else {
                                        "Type..."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = stageTextColor.copy(alpha = 0.5f)
                                )
                            }
                            BasicTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                singleLine = true,
                                textStyle = TextStyle(color = stageTextColor, fontSize = 14.sp),
                                cursorBrush = SolidColor(PrimaryPurple),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("room_chat_input")
                            )
                        }
                        if (currentUser.vipLevel >= 9) {
                            Surface(
                                onClick = { bulletCommentMode = !bulletCommentMode },
                                shape = RoundedCornerShape(50),
                                color = if (bulletCommentMode) PremiumGold else PrimaryPurple.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, if (bulletCommentMode) PremiumGold else BorderColor),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .testTag("room_bullet_comment_toggle")
                            ) {
                                Text(
                                    text = "Bullet",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (bulletCommentMode) PrimaryText else stageTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        if (chatInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    if (bulletCommentMode && currentUser.vipLevel >= 9) {
                                        onSendBulletComment(chatInput)
                                    } else {
                                        onSendChat(chatInput, false)
                                    }
                                    chatInput = ""
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("room_send_chat_button")
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.Send, "Send room message", tint = PrimaryPurple, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { showGiftSheet = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PrimaryPurple, DeepPurple)))
                        .border(1.2.dp, PremiumGold, CircleShape)
                        .testTag("room_open_gifts_button")
                ) {
                    Icon(Icons.Rounded.CardGiftcard, "Send virtual gift", tint = PremiumGold)
                }

                IconButton(
                    onClick = {
                        onMinimize()
                        onOpenWallet()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(stageBarColor)
                        .border(1.dp, stageBarBorder, CircleShape)
                ) {
                    Icon(Icons.Rounded.AccountBalanceWallet, "Wallet & Bag", tint = stageTextColor)
                }

                IconButton(
                    onClick = { showMoreFeaturesSheet = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(stageBarColor)
                        .border(1.dp, stageBarBorder, CircleShape)
                ) {
                    Icon(Icons.Rounded.GridView, "More room features", tint = stageTextColor)
                }
            }
        }

        // Level-Up Celebration Overlay & Gift Animation Engine
        RoomLevelUpOverlay(event = session.levelUpEvent, onDismiss = onDismissLevelUp)
        GiftAnimationOverlay(effect = session.activeGiftEffect)
    }

    // Sub-Sheets & Modals (Clean Premium Light Theme sheets matching reference screenshots)
    if (showThemePickerSheet) {
        RoomBackdropThemePickerSheet(
            selectedTheme = room.backdropTheme,
            onSelectTheme = onChangeBackdropTheme,
            onDismiss = { showThemePickerSheet = false }
        )
    }

    if (showGiftSheet) {
        VirtualGiftDrawerSheet(
            userCoins = currentUser.coins,
            occupiedSeats = session.seats.filter { it.occupantId != null },
            packageInventory = packageInventory,
            onDismiss = { showGiftSheet = false },
            onSendGift = { gift, combo, seats, label, fromPkg ->
                if (room.banRedPacket && gift.id == "g_giftbox") {
                    onInviteFriendsToast("Red Packet gifts are restricted by Room Settings.")
                } else {
                    onSendGift(gift, combo, seats, label, fromPkg)
                }
            },
            onOpenWallet = {
                showGiftSheet = false
                onMinimize()
                onOpenWallet()
            }
        )
    }

    if (showRoomInfoSheet) {
        RoomInfoAndLevelsSheet(
            room = room,
            owner = session.ownerInfo,
            partner = session.partnerInfo,
            memberRoles = session.memberRoles,
            levelConfigs = levelConfigs,
            contributionSummary = contributionSummary,
            activityLogs = activityLogs,
            onOpenContributions = {
                showRoomInfoSheet = false
                showContributionSheet = true
            },
            onDismiss = { showRoomInfoSheet = false }
        )
    }

    if (showContributionSheet) {
        RoomContributionSheet(
            room = room,
            summary = contributionSummary,
            rankings = contributors,
            giftLedger = giftLedger,
            onDismiss = { showContributionSheet = false }
        )
    }

    if (showManagementSheet) {
        RoomManagementPanelSheet(
            session = session,
            levelConfigs = levelConfigs,
            activityLogs = activityLogs,
            onAcceptMicRequest = onAcceptMicRequest,
            onRejectMicRequest = onRejectMicRequest,
            onToggleSeatLock = onToggleSeatLock,
            onToggleMicLock = onToggleMicLock,
            onToggleMuteOccupant = onToggleOccupantMute,
            onReserveSeat = onReserveSeat,
            onAssignUserToSeat = onAssignUserToSeat,
            onRemoveFromSeat = onRemoveFromSeat,
            onBanUser = onBanUser,
            onAssignRole = onAssignMemberRole,
            onPublishAnnouncement = onPublishAnnouncement,
            onUpdateAppearance = onUpdateAppearance,
            onUpdateAdminLevelConfig = onUpdateAdminLevelConfig,
            onTogglePartnerSeatEnabled = onTogglePartnerSeatEnabled,
            onDismiss = { showManagementSheet = false }
        )
    }

    if (showMusicSheet) {
        MusicQueueSheet(
            session = session,
            onTogglePlayPause = onToggleMusic,
            onSelectOrRequestTrack = onSelectOrRequestTrack,
            onApproveQueueItem = onApproveSongRequest,
            onDismiss = { showMusicSheet = false }
        )
    }

    if (showMoreFeaturesSheet) {
        MoreFeaturesSheet(
            canManageRoom = session.currentUserRole.canModerate(),
            isPkActive = room.isPkActive,
            onShareRoom = shareRoomAction,
            onInviteFriends = { onInviteFriendsToast("Invite link copied for WEFUN Room ID ${room.id}!") },
            onOpenRoomInfo = { showRoomInfoSheet = true },
            onOpenContributions = { showContributionSheet = true },
            onOpenManagement = { showManagementSheet = true },
            onOpenRoomSettings = { showRoomSettingsScreen = true },
            onOpenMusic = { showMusicSheet = true },
            onTogglePk = { if (room.isPkActive) onEndPk() else showPkDialog = true },
            onPlayDiamondBingo = onPlayDiamondBingo,
            onReportRoom = { showReportDialog = "Room" to room.title },
            onExitRoom = onLeave,
            onDismiss = { showMoreFeaturesSheet = false }
        )
    }

    if (showRoomSettingsScreen) {
        RoomSettingsScreen(
            room = room,
            currentUserRole = session.currentUserRole,
            memberRoles = session.memberRoles,
            activityLogs = activityLogs,
            onBack = { showRoomSettingsScreen = false },
            onSwitchRoomMode = onSwitchRoomMode,
            onOpenAuctionRelationshipSettings = {
                showRoomSettingsScreen = false
                showRelationshipSettingsSheet = true
            },
            onTogglePartnerSeat = onTogglePartnerSeatEnabled,
            onAssignMemberRole = onAssignMemberRole,
            onSaveFullSettings = onSaveFullRoomSettings,
            onShowToast = onInviteFriendsToast,
        )
    }

    if (showRelationshipSettingsSheet) {
        RelationshipSettingsSheet(
            auctionState = session.auctionState,
            customTags = customRelationshipTags,
            occupiedSeats = session.seats,
            onCreateCustomTag = onCreateCustomTag,
            onConfirm = onConfirmAuctionRelationshipSetup,
            onDismiss = { showRelationshipSettingsSheet = false },
        )
    }

    if (showOwnerPartnerModal != null) {
        OwnerPartnerModalSheet(
            mode = showOwnerPartnerModal!!,
            owner = session.ownerInfo,
            partner = session.partnerInfo,
            memberRoles = session.memberRoles,
            currentUserRole = session.currentUserRole,
            onAssignPartner = onAssignPartner,
            onRemovePartner = onRemovePartner,
            onSendGift = { showGiftSheet = true },
            onOpenManagement = { showManagementSheet = true },
            onDismiss = { showOwnerPartnerModal = null }
        )
    }

    if (selectedSeatForSheet != null) {
        VoiceSeatActionSheet(
            seat = selectedSeatForSheet!!,
            currentUserId = currentUser.uid,
            currentUserSeatIndex = session.currentUserSeatIndex,
            currentUserRole = session.currentUserRole,
            memberRoles = session.memberRoles,
            onJoinSeat = onTapSeat,
            onLeaveSeat = onTapSeat,
            onSendGift = { showGiftSheet = true },
            onAssignUserToSeat = onAssignUserToSeat,
            onReserveSeat = onReserveSeat,
            onToggleOccupantMute = onToggleOccupantMute,
            onToggleMicLock = onToggleMicLock,
            onToggleSeatLock = onToggleSeatLock,
            onStartMoveSeat = onStartMoveSeat,
            onToggleCoHost = onToggleCoHost,
            onRemoveFromSeat = onRemoveFromSeat,
            onTransferHost = onTransferHost,
            onAssignPartner = onAssignPartner,
            onBlockUser = onBlockUser,
            onReportUser = { name -> showReportDialog = "User" to name },
            onDismiss = { selectedSeatForSheet = null }
        )
    }

    if (showPkDialog) {
        var pkMode by remember { mutableStateOf("1v1 PK") }
        var opponent by remember { mutableStateOf("DJ Rhea • Pulse") }
        AlertDialog(
            onDismissRequest = { showPkDialog = false },
            title = { Text("Start PK Battle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("1v1 PK", "Team vs Team", "Host vs Host").forEach { mode ->
                            FilterChip(selected = pkMode == mode, onClick = { pkMode = mode }, label = { Text(mode) })
                        }
                    }
                    OutlinedTextField(value = opponent, onValueChange = { opponent = it }, label = { Text("Invite Room / Host") })
                }
            },
            confirmButton = {
                TextButton(onClick = { onStartPk(pkMode, opponent); showPkDialog = false }) { Text("Start 3m PK") }
            },
            dismissButton = {
                TextButton(onClick = { showPkDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showReportDialog != null) {
        val (type, name) = showReportDialog!!
        var reason by remember { mutableStateOf("Spam or inappropriate audio") }
        AlertDialog(
            onDismissRequest = { showReportDialog = null },
            title = { Text("Report $type") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Submitting safety report for $name")
                    OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onReportRoomOrUser(type, name, reason)
                    showReportDialog = null
                }) { Text("Submit Report", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PkBattleBar(
    leftName: String,
    rightName: String,
    leftScore: Long,
    rightScore: Long,
    secondsLeft: Int,
    modeLabel: String,
) {
    val total = (leftScore + rightScore).coerceAtLeast(1L)
    val leftWeight = (leftScore.toFloat() / total.toFloat()).coerceIn(0.15f, 0.85f)
    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, PremiumGold.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$leftName ($leftScore)", style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                Text(
                    text = "$modeLabel • ${String.format("%02d:%02d", mins, secs)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = PremiumGold,
                    fontWeight = FontWeight.Bold
                )
                Text("$rightName ($rightScore)", style = MaterialTheme.typography.labelSmall, color = PinkAccent)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .weight(leftWeight)
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(PrimaryPurple, CyanPulse)))
                )
                Box(
                    modifier = Modifier
                        .weight(1f - leftWeight)
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(PinkAccent, PremiumGold)))
                )
            }
        }
    }
}
