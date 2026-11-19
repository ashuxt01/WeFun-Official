package com.example.ui.viewmodel

import com.example.data.model.ActiveGiftEffect
import com.example.data.model.AuctionBidEntry
import com.example.data.model.AuctionSessionState
import com.example.data.model.BiddingMode
import com.example.data.model.MicRequest
import com.example.data.model.MusicTrack
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomChatMessage
import com.example.data.model.RoomLevelUpEvent
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomMessageType
import com.example.data.model.RoomMode
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.RoomSeat
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SongRequestItem
import com.example.data.model.StartingToken
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.repository.VipCatalog
import com.example.data.repository.VoiceRoomSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class LiveRoomSessionState(
    val room: VoiceRoom? = null,
    val isMinimized: Boolean = false,
    val ownerInfo: RoomOwnerInfo? = null,
    val partnerInfo: RoomPartnerInfo? = null,
    val memberRoles: List<RoomMemberRoleEntry> = emptyList(),
    val currentUserRole: RoomRole = RoomRole.NORMAL_MEMBER,
    val seats: List<RoomSeat> = emptyList(),
    val messages: List<RoomChatMessage> = emptyList(),
    val micRequests: List<MicRequest> = emptyList(),
    val activeGiftEffect: ActiveGiftEffect? = null,
    val levelUpEvent: RoomLevelUpEvent? = null,
    val vipEntryBanner: String? = null,
    val vipEntryLevel: Int = 0,
    val vipEntryUserName: String = "",
    val vipEntryEffectTitle: String = "",
    val muteProtectedSeatIndices: Map<Int, Long> = emptyMap(),
    val currentUserSeatIndex: Int? = null, // -1 = Owner slot, -2 = Partner slot, >=0 = regular mic seat
    val isUserMuted: Boolean = true,
    val isUserHost: Boolean = false,
    val isUserCoHost: Boolean = false,
    val pendingMoveFromSeat: Int? = null,
    val currentTrack: MusicTrack? = VoiceRoomSeedData.licensedTracks.firstOrNull(),
    val musicQueue: List<SongRequestItem> = emptyList(),
    val bannedUserIds: Set<String> = emptySet(),
    val auctionState: AuctionSessionState = AuctionSessionState(),
)

class LiveRoomManager(private val scope: CoroutineScope) {

    private val _session = MutableStateFlow(LiveRoomSessionState())
    val session: StateFlow<LiveRoomSessionState> = _session.asStateFlow()

    private var pkTimerJob: Job? = null
    private var giftDismissJob: Job? = null
    private var levelUpDismissJob: Job? = null

    fun enterRoom(
        room: VoiceRoom,
        currentUser: UserProfile,
        ownerInfo: RoomOwnerInfo?,
        partnerInfo: RoomPartnerInfo?,
        memberRoles: List<RoomMemberRoleEntry>,
    ) {
        pkTimerJob?.cancel()
        giftDismissJob?.cancel()
        levelUpDismissJob?.cancel()
        val isOwner = (ownerInfo?.userId ?: room.hostId) == currentUser.uid
        val isPartner = partnerInfo?.userId == currentUser.uid
        val role = when {
            isOwner -> RoomRole.OWNER
            isPartner -> RoomRole.PARTNER
            else -> memberRoles.firstOrNull { it.userId == currentUser.uid }?.role
                ?: if (currentUser.vipLevel > 0) RoomRole.VIP_MEMBER else RoomRole.NORMAL_MEMBER
        }
        val resolvedOwner = ownerInfo ?: RoomOwnerInfo(
            roomId = room.id,
            userId = room.hostId,
            displayName = if (isOwner) currentUser.nickname else room.hostName,
            initials = initialsOf(if (isOwner) currentUser.nickname else room.hostName),
            avatarColorHex = 0xFFFFD700,
            vipLevel = room.hostVip,
            userLevel = if (isOwner) currentUser.userLevel else 26,
            isSpeaking = true,
            isMuted = false,
            rmsLevel = 0.45f,
            charmCount = 1480L,
        )
        val resolvedPartner = partnerInfo ?: RoomPartnerInfo(
            roomId = room.id,
            userId = null,
            displayName = null,
            isLocked = false,
            isEnabled = room.isPartnerSeatEnabled,
        )

        val seatCount = seatCountForLevel(room.roomLevel, room.maxSeats)
        val seats = buildRegularSeats(seatCount)
        val welcomeMsgs = listOf(
            RoomChatMessage(
                id = "sys_rules",
                senderId = "system",
                senderName = "system",
                senderVip = 0,
                text = "Welcome to WEFUN Voice Room. Erotic, abusive, or illegal content is strictly forbidden. Minors should use this feature with guardian knowledge and protect personal privacy.",
                type = RoomMessageType.SYSTEM
            ),
            RoomChatMessage(
                id = "entry_${UUID.randomUUID()}",
                senderId = currentUser.uid,
                senderName = currentUser.nickname,
                senderVip = currentUser.vipLevel,
                text = if (currentUser.vipLevel >= 2) {
                    "${currentUser.nickname} arrived with ${currentUser.avatarConfig.frameName} aura!"
                } else {
                    "${currentUser.nickname} joined the room"
                },
                type = RoomMessageType.ENTRY
            ),
        )

        val sampleRequests = listOf(
            MicRequest("req_1", "Sanya Star", 3, 19),
            MicRequest("req_2", "Rohan Vibe", 1, 8),
        )
        val initialQueue = listOf(
            SongRequestItem("sq_1", VoiceRoomSeedData.licensedTracks[1], "Sanya Star", "Approved"),
            SongRequestItem("sq_2", VoiceRoomSeedData.licensedTracks[2], "Kabir Beats", "Pending"),
        )

        val initialAuctionState = if (room.roomMode == RoomMode.AUCTION) {
            AuctionSessionState(
                isConfigured = true,
                biddingMode = BiddingMode.BFF,
                selectedTag = RelationshipTagOption("tag_friend", "FRIEND", BiddingMode.BFF, "clover"),
                selectedToken = StartingToken.SCOOTER,
                startingPricePoints = 200L,
                isBiddingActive = true,
            )
        } else {
            AuctionSessionState()
        }
        val effectiveRoom = room.copy(
            maxSeats = seatCount,
            backdropTheme = if (room.roomMode == RoomMode.AUCTION) VoiceRoomBackdropTheme.AUCTION_ROYAL else room.backdropTheme,
        )

        val entranceEffectTitle = VipCatalog.defaultTiers
            .firstOrNull { it.level == currentUser.vipLevel }
            ?.entranceEffectTitle
            ?: "VIP Royal Arrival"

        _session.value = LiveRoomSessionState(
            room = effectiveRoom,
            isMinimized = false,
            ownerInfo = resolvedOwner,
            partnerInfo = resolvedPartner,
            memberRoles = memberRoles,
            currentUserRole = role,
            seats = seats,
            messages = welcomeMsgs,
            micRequests = sampleRequests,
            vipEntryBanner = if (currentUser.vipLevel >= 1) {
                "VIP ${currentUser.vipLevel} ${currentUser.nickname} entered!"
            } else null,
            vipEntryLevel = currentUser.vipLevel,
            vipEntryUserName = currentUser.nickname,
            vipEntryEffectTitle = entranceEffectTitle,
            currentUserSeatIndex = when {
                isOwner -> -1
                isPartner -> -2
                else -> null
            },
            isUserMuted = !isOwner,
            isUserHost = isOwner,
            isUserCoHost = isPartner || role.canModerate(),
            currentTrack = VoiceRoomSeedData.licensedTracks.firstOrNull(),
            musicQueue = initialQueue,
            auctionState = initialAuctionState,
        )

        if (currentUser.vipLevel >= 1) {
            scope.launch {
                delay(3200)
                _session.update {
                    it.copy(
                        vipEntryBanner = null,
                        vipEntryLevel = 0,
                        vipEntryUserName = "",
                        vipEntryEffectTitle = "",
                    )
                }
            }
        }
        startPkLoopIfActive()
    }

    fun minimizeRoom() {
        _session.update { it.copy(isMinimized = true) }
    }

    fun restoreRoom() {
        _session.update { it.copy(isMinimized = false) }
    }

    fun leaveRoom() {
        pkTimerJob?.cancel()
        giftDismissJob?.cancel()
        levelUpDismissJob?.cancel()
        _session.value = LiveRoomSessionState()
    }

    fun dismissLevelUpBanner() {
        _session.update { it.copy(levelUpEvent = null) }
    }

    fun updateFavoriteState(isFavorited: Boolean) {
        _session.update { it.copy(room = it.room?.copy(isFavorited = isFavorited)) }
    }

    fun updateRoomMetadata(
        title: String? = null,
        announcement: String? = null,
        theme: RoomThemeStyle? = null,
        decorationStyle: String? = null,
        musicAdminsOnly: Boolean? = null,
        backdropTheme: VoiceRoomBackdropTheme? = null,
    ) {
        _session.update { state ->
            val rm = state.room ?: return@update state
            state.copy(
                room = rm.copy(
                    title = title?.ifBlank { rm.title } ?: rm.title,
                    announcement = announcement?.ifBlank { rm.announcement } ?: rm.announcement,
                    themeStyle = theme ?: rm.themeStyle,
                    decorationStyle = decorationStyle ?: rm.decorationStyle,
                    musicAdminsOnly = musicAdminsOnly ?: rm.musicAdminsOnly,
                    backdropTheme = backdropTheme ?: rm.backdropTheme,
                )
            )
        }
    }

    fun updateRoomFullState(updatedRoom: VoiceRoom) {
        _session.update { state ->
            val resolvedBackdrop = if (updatedRoom.roomMode == RoomMode.AUCTION) {
                VoiceRoomBackdropTheme.AUCTION_ROYAL
            } else {
                updatedRoom.backdropTheme
            }
            val nextAuction = if (updatedRoom.roomMode == RoomMode.AUCTION && !state.auctionState.isConfigured) {
                state.auctionState.copy(
                    isConfigured = true,
                    selectedTag = state.auctionState.selectedTag
                        ?: RelationshipTagOption("tag_friend", "FRIEND", BiddingMode.BFF, "clover"),
                    isBiddingActive = true,
                )
            } else state.auctionState
            state.copy(
                room = updatedRoom.copy(backdropTheme = resolvedBackdrop),
                auctionState = nextAuction,
            )
        }
    }

    fun switchRoomMode(mode: RoomMode) {
        _session.update { state ->
            val rm = state.room ?: return@update state
            val nextBackdrop = if (mode == RoomMode.AUCTION) {
                VoiceRoomBackdropTheme.AUCTION_ROYAL
            } else if (rm.backdropTheme == VoiceRoomBackdropTheme.AUCTION_ROYAL) {
                VoiceRoomBackdropTheme.ROYAL_BLUE
            } else {
                rm.backdropTheme
            }
            val nextAuction = if (mode == RoomMode.AUCTION) {
                state.auctionState.copy(
                    isConfigured = true,
                    selectedTag = state.auctionState.selectedTag
                        ?: RelationshipTagOption("tag_friend", "FRIEND", BiddingMode.BFF, "clover"),
                    isBiddingActive = true,
                )
            } else state.auctionState
            state.copy(
                room = rm.copy(roomMode = mode, backdropTheme = nextBackdrop),
                auctionState = nextAuction,
            )
        }
        appendSystemMessage("Room Mode switched to ${mode.label}.")
    }

    fun configureAuction(
        mode: BiddingMode,
        tag: RelationshipTagOption?,
        customLabel: String,
        token: StartingToken?,
        targetUserId: String?,
        targetUserName: String?,
        startingPrice: Long,
    ) {
        val resolvedTag = tag ?: RelationshipTagOption(
            id = "tag_custom",
            label = customLabel.ifBlank { "BFF" },
            mode = mode,
            iconKey = "star",
            isCustom = customLabel.isNotBlank(),
        )
        _session.update { state ->
            state.copy(
                auctionState = state.auctionState.copy(
                    isConfigured = true,
                    biddingMode = mode,
                    selectedTag = resolvedTag,
                    customTagLabel = customLabel,
                    selectedToken = token,
                    targetUserId = targetUserId,
                    targetUserName = targetUserName,
                    startingPricePoints = startingPrice.coerceAtLeast(50L),
                    isBiddingActive = true,
                )
            )
        }
        val tokenLabel = token?.title ?: "Custom Reward"
        val targetSuffix = targetUserName?.let { " with $it" } ?: ""
        appendSystemMessage("Auction updated: ${mode.label} • ${resolvedTag.label}$targetSuffix ($tokenLabel)")
    }

    fun placeAuctionBid(
        bidderId: String,
        bidderName: String,
        points: Long,
    ): Result<AuctionBidEntry> {
        val state = _session.value
        val auction = state.auctionState
        val minRequired = maxOf(auction.startingPricePoints, auction.currentTopBidPoints + 50L)
        if (points < minRequired) {
            return Result.failure(IllegalArgumentException("Bid must be at least $minRequired points."))
        }
        val entry = AuctionBidEntry(
            id = "bid_${UUID.randomUUID().toString().take(8)}",
            bidderId = bidderId,
            bidderName = bidderName,
            tagLabel = auction.selectedTag?.label ?: "BFF",
            tokenTitle = auction.selectedToken?.title ?: "Scooter Token",
            points = points,
            targetUserId = auction.targetUserId ?: "host",
            targetUserName = auction.targetUserName ?: state.room?.hostName ?: "Host",
        )
        _session.update { s ->
            s.copy(
                auctionState = s.auctionState.copy(
                    currentTopBidderName = bidderName,
                    currentTopBidPoints = points,
                    history = listOf(entry) + s.auctionState.history,
                )
            )
        }
        appendSystemMessage("🎉 $bidderName placed ${points} pts for ${entry.tagLabel}!")
        return Result.success(entry)
    }

    fun refreshSnapshotRoles(ownerInfo: RoomOwnerInfo?, partnerInfo: RoomPartnerInfo?, memberRoles: List<RoomMemberRoleEntry>, currentUserId: String) {
        _session.update { state ->
            val isOwner = ownerInfo?.userId == currentUserId
            val isPartner = partnerInfo?.userId == currentUserId
            val role = when {
                isOwner -> RoomRole.OWNER
                isPartner -> RoomRole.PARTNER
                else -> memberRoles.firstOrNull { it.userId == currentUserId }?.role ?: state.currentUserRole
            }
            state.copy(
                ownerInfo = ownerInfo ?: state.ownerInfo,
                partnerInfo = partnerInfo ?: state.partnerInfo,
                memberRoles = memberRoles,
                currentUserRole = role,
                isUserHost = isOwner,
                isUserCoHost = isPartner || role.canModerate(),
            )
        }
    }

    fun updatePartnerSeatEnabled(enabled: Boolean) {
        _session.update { state ->
            state.copy(
                room = state.room?.copy(isPartnerSeatEnabled = enabled),
                partnerInfo = state.partnerInfo?.copy(isEnabled = enabled, isLocked = false)
            )
        }
    }

    fun updateUserMicTelemetry(rmsLevel: Float, isSpeaking: Boolean) {
        val seatIdx = _session.value.currentUserSeatIndex ?: return
        val muted = _session.value.isUserMuted
        _session.update { state ->
            when (seatIdx) {
                -1 -> state.copy(
                    ownerInfo = state.ownerInfo?.copy(
                        isMuted = muted,
                        isSpeaking = !muted && isSpeaking,
                        rmsLevel = if (muted) 0f else rmsLevel,
                    )
                )
                -2 -> state.copy(
                    partnerInfo = state.partnerInfo?.copy(
                        isMuted = muted,
                        isSpeaking = !muted && isSpeaking,
                        rmsLevel = if (muted) 0f else rmsLevel,
                    )
                )
                else -> state.copy(
                    seats = state.seats.map { seat ->
                        if (seat.seatIndex == seatIdx) {
                            seat.copy(
                                isMuted = muted,
                                isSpeaking = !muted && isSpeaking,
                                rmsLevel = if (muted) 0f else rmsLevel
                            )
                        } else seat
                    }
                )
            }
        }
    }

    fun toggleUserMute(): Boolean {
        val current = _session.value
        val seatIdx = current.currentUserSeatIndex ?: return true
        if (seatIdx >= 0) {
            val seat = current.seats.getOrNull(seatIdx) ?: return true
            if (seat.isMicLocked && !current.currentUserRole.canModerate()) {
                appendSystemMessage("Admin has locked microphone on Seat #${seatIdx + 1}.")
                return true
            }
        }
        val nextMuted = !current.isUserMuted
        _session.update { state ->
            when (seatIdx) {
                -1 -> state.copy(
                    isUserMuted = nextMuted,
                    ownerInfo = state.ownerInfo?.copy(isMuted = nextMuted, isSpeaking = false, rmsLevel = 0f)
                )
                -2 -> state.copy(
                    isUserMuted = nextMuted,
                    partnerInfo = state.partnerInfo?.copy(isMuted = nextMuted, isSpeaking = false, rmsLevel = 0f)
                )
                else -> state.copy(
                    isUserMuted = nextMuted,
                    seats = state.seats.map {
                        if (it.seatIndex == seatIdx) {
                            it.copy(isMuted = nextMuted, isSpeaking = false, rmsLevel = 0f)
                        } else it
                    }
                )
            }
        }
        return nextMuted
    }

    fun takeOrRequestSeat(seatIndex: Int, user: UserProfile) {
        val state = _session.value
        val target = state.seats.getOrNull(seatIndex) ?: return
        if (target.isSeatLocked && !state.currentUserRole.canModerate()) {
            appendSystemMessage("Seat #${seatIndex + 1} is locked by room management.")
            return
        }
        if (target.reservedForUserId != null && target.reservedForUserId != user.uid && !state.currentUserRole.canModerate()) {
            appendSystemMessage("Seat #${seatIndex + 1} is reserved for ${target.reservedForName ?: "a guest"}.")
            return
        }
        if (target.occupantId != null) return

        if (state.currentUserRole.canModerate() || state.room?.category?.name != "OFFICIAL") {
            seatUserAt(seatIndex, user)
        } else {
            val already = state.micRequests.any { it.userId == user.uid }
            if (!already) {
                _session.update {
                    it.copy(micRequests = it.micRequests + MicRequest(user.uid, user.nickname, user.vipLevel, user.userLevel))
                }
                appendSystemMessage("Requested Seat #${seatIndex + 1}. Host/Admins reviewing...")
            }
        }
    }

    fun leaveMicSeat() {
        val state = _session.value
        val seatIdx = state.currentUserSeatIndex ?: return
        if (seatIdx < 0) return // Owner/Partner stay on their dedicated throne slots unless transferred
        _session.update { s ->
            s.copy(
                currentUserSeatIndex = null,
                isUserMuted = true,
                seats = s.seats.map {
                    if (it.seatIndex == seatIdx) {
                        it.copy(
                            occupantId = null,
                            occupantName = null,
                            occupantInitials = "",
                            isCoHost = false,
                            isMuted = false,
                            isSpeaking = false,
                            rmsLevel = 0f,
                            vipLevel = 0,
                        )
                    } else it
                }
            )
        }
        appendSystemMessage("You stepped down to the listener floor.")
    }

    fun acceptMicRequest(req: MicRequest) {
        val state = _session.value
        val openSeat = state.seats.firstOrNull { it.occupantId == null && !it.isSeatLocked } ?: return
        _session.update { s ->
            s.copy(
                micRequests = s.micRequests.filterNot { it.userId == req.userId },
                seats = s.seats.map {
                    if (it.seatIndex == openSeat.seatIndex) {
                        it.copy(
                            occupantId = req.userId,
                            occupantName = req.userName,
                            occupantInitials = initialsOf(req.userName),
                            accentColorHex = 0xFF6200EE,
                            isMuted = false,
                            vipLevel = req.vipLevel,
                        )
                    } else it
                }
            )
        }
        appendSystemMessage("${req.userName} assigned to Seat #${openSeat.seatIndex + 1}.")
    }

    fun rejectMicRequest(userId: String) {
        _session.update { it.copy(micRequests = it.micRequests.filterNot { r -> r.userId == userId }) }
    }

    fun assignUserToSeat(seatIndex: Int, userId: String, displayName: String, vipLevel: Int) {
        _session.update { state ->
            state.copy(
                seats = state.seats.map { seat ->
                    if (seat.seatIndex == seatIndex) {
                        seat.copy(
                            occupantId = userId,
                            occupantName = displayName,
                            occupantInitials = initialsOf(displayName),
                            accentColorHex = 0xFF6200EE,
                            isSeatLocked = false,
                            isMuted = false,
                            vipLevel = vipLevel,
                            reservedForUserId = null,
                            reservedForName = null,
                        )
                    } else seat
                }
            )
        }
        appendSystemMessage("Admin seated $displayName on Seat #${seatIndex + 1}.")
    }

    fun reserveSeat(seatIndex: Int, targetName: String?) {
        _session.update { state ->
            state.copy(
                seats = state.seats.map { seat ->
                    if (seat.seatIndex == seatIndex) {
                        if (targetName.isNullOrBlank()) {
                            seat.copy(reservedForUserId = null, reservedForName = null)
                        } else {
                            seat.copy(reservedForUserId = "res_${targetName.lowercase()}", reservedForName = targetName)
                        }
                    } else seat
                }
            )
        }
        if (!targetName.isNullOrBlank()) {
            appendSystemMessage("Seat #${seatIndex + 1} reserved for $targetName.")
        }
    }

    fun toggleSeatLock(seatIndex: Int) {
        _session.update { state ->
            state.copy(
                seats = state.seats.map {
                    if (it.seatIndex == seatIndex) it.copy(isSeatLocked = !it.isSeatLocked) else it
                }
            )
        }
    }

    fun toggleSeatMicLock(seatIndex: Int) {
        _session.update { state ->
            state.copy(
                seats = state.seats.map {
                    if (it.seatIndex == seatIndex) {
                        val nextLock = !it.isMicLocked
                        it.copy(isMicLocked = nextLock, isMuted = if (nextLock) true else it.isMuted, isSpeaking = false)
                    } else it
                }
            )
        }
    }

    fun activateMuteProtection(seatIndex: Int, durationMs: Long = 600_000L) {
        val until = System.currentTimeMillis() + durationMs
        _session.update { state ->
            state.copy(
                muteProtectedSeatIndices = state.muteProtectedSeatIndices + (seatIndex to until),
                seats = state.seats.map { seat ->
                    if (seat.seatIndex == seatIndex) seat.copy(isMuted = false) else seat
                }
            )
        }
        appendSystemMessage("🛡️ Mute Protection VIP Gift aura activated on Seat #${seatIndex + 1}!")
    }

    fun toggleOccupantMuteByAdmin(seatIndex: Int) {
        val stateNow = _session.value
        val target = stateNow.seats.getOrNull(seatIndex) ?: return
        val protectedUntil = stateNow.muteProtectedSeatIndices[seatIndex] ?: 0L
        if (protectedUntil > System.currentTimeMillis() && !target.isMuted) {
            appendSystemMessage("🛡️ Mute Protection VIP Gift aura blocked mute on ${target.occupantName ?: "Speaker"}.")
            return
        }
        _session.update { state ->
            state.copy(
                seats = state.seats.map {
                    if (it.seatIndex == seatIndex && it.occupantId != null) {
                        val nextMute = !it.isMuted
                        it.copy(isMuted = nextMute, isSpeaking = if (nextMute) false else it.isSpeaking)
                    } else it
                }
            )
        }
    }

    fun toggleSeatCoHost(seatIndex: Int) {
        _session.update { state ->
            state.copy(
                seats = state.seats.map {
                    if (it.seatIndex == seatIndex && it.occupantId != null) it.copy(isCoHost = !it.isCoHost)
                    else it
                }
            )
        }
    }

    fun removeUserFromSeat(seatIndex: Int, force: Boolean = false) {
        val stateNow = _session.value
        val target = stateNow.seats.getOrNull(seatIndex) ?: return
        val removedName = target.occupantName ?: return
        if (!force && target.vipLevel >= 11 && target.occupantId != stateNow.ownerInfo?.userId) {
            appendSystemMessage("🛡️ VIP 11+ Anti-KickOut & Anti-Leave-Mic protection prevented removing $removedName.")
            return
        }
        _session.update { state ->
            val wasMe = state.currentUserSeatIndex == seatIndex
            state.copy(
                currentUserSeatIndex = if (wasMe) null else state.currentUserSeatIndex,
                isUserMuted = if (wasMe) true else state.isUserMuted,
                seats = state.seats.map {
                    if (it.seatIndex == seatIndex) {
                        it.copy(
                            occupantId = null,
                            occupantName = null,
                            occupantInitials = "",
                            isCoHost = false,
                            isMuted = false,
                            isSpeaking = false,
                            vipLevel = 0
                        )
                    } else it
                }
            )
        }
        appendSystemMessage("$removedName was removed from Seat #${seatIndex + 1}.")
    }

    fun banUserFromRoom(userId: String, displayName: String) {
        val stateNow = _session.value
        val protectedSeat = stateNow.seats.firstOrNull {
            it.occupantId == userId && it.vipLevel >= 11 && it.occupantId != stateNow.ownerInfo?.userId
        }
        if (protectedSeat != null) {
            appendSystemMessage("🛡️ VIP 11+ Anti-KickOut & Anti-Leave-Mic protection shielded $displayName from kick/ban.")
            return
        }
        _session.update { state ->
            state.copy(
                bannedUserIds = state.bannedUserIds + userId,
                seats = state.seats.map { seat ->
                    if (seat.occupantId == userId) {
                        seat.copy(occupantId = null, occupantName = null, occupantInitials = "", isSpeaking = false, vipLevel = 0)
                    } else seat
                }
            )
        }
        appendSystemMessage("$displayName was banned from the room.")
    }

    fun startMoveSeat(fromIndex: Int) {
        _session.update { it.copy(pendingMoveFromSeat = fromIndex) }
        appendSystemMessage("Tap an open seat to move speaker from #${fromIndex + 1}.")
    }

    fun completeMoveSeat(toIndex: Int) {
        val state = _session.value
        val fromIndex = state.pendingMoveFromSeat ?: return
        val fromSeat = state.seats.getOrNull(fromIndex)
        val toSeat = state.seats.getOrNull(toIndex)
        if (fromSeat == null || toSeat == null || toSeat.occupantId != null || toSeat.isSeatLocked) {
            _session.update { it.copy(pendingMoveFromSeat = null) }
            return
        }
        _session.update { s ->
            val newMySeat = if (s.currentUserSeatIndex == fromIndex) toIndex else s.currentUserSeatIndex
            s.copy(
                pendingMoveFromSeat = null,
                currentUserSeatIndex = newMySeat,
                seats = s.seats.map { seat ->
                    when (seat.seatIndex) {
                        toIndex -> seat.copy(
                            occupantId = fromSeat.occupantId,
                            occupantName = fromSeat.occupantName,
                            occupantInitials = fromSeat.occupantInitials,
                            accentColorHex = fromSeat.accentColorHex,
                            isCoHost = fromSeat.isCoHost,
                            isMuted = fromSeat.isMuted,
                            vipLevel = fromSeat.vipLevel,
                            charmCount = fromSeat.charmCount,
                        )
                        fromIndex -> seat.copy(
                            occupantId = null,
                            occupantName = null,
                            occupantInitials = "",
                            isCoHost = false,
                            isMuted = false,
                            isSpeaking = false,
                            vipLevel = 0,
                            charmCount = 0,
                        )
                        else -> seat
                    }
                }
            )
        }
        appendSystemMessage("Moved speaker from Seat #${fromIndex + 1} to #${toIndex + 1}.")
    }

    fun requestOrSelectTrack(track: MusicTrack, requesterName: String, isAdmin: Boolean) {
        _session.update { state ->
            if (isAdmin) {
                state.copy(
                    currentTrack = track,
                    room = state.room?.copy(currentTrack = "${track.title} — ${track.artist}", isMusicPlaying = true),
                    musicQueue = listOf(SongRequestItem("sq_${UUID.randomUUID()}", track, requesterName, "Playing")) +
                        state.musicQueue.filterNot { it.track.id == track.id }
                )
            } else {
                val req = SongRequestItem("sq_${UUID.randomUUID()}", track, requesterName, "Pending")
                state.copy(musicQueue = state.musicQueue + req)
            }
        }
        appendSystemMessage(if (isAdmin) "Now playing: ${track.title}" else "$requesterName requested '${track.title}'")
    }

    fun approveAndPlayQueueItem(item: SongRequestItem) {
        _session.update { state ->
            state.copy(
                currentTrack = item.track,
                room = state.room?.copy(currentTrack = "${item.track.title} — ${item.track.artist}", isMusicPlaying = true),
                musicQueue = state.musicQueue.map {
                    if (it.id == item.id) it.copy(status = "Playing") else it
                }
            )
        }
        appendSystemMessage("Approved & playing: ${item.track.title} (requested by ${item.requestedBy})")
    }

    fun sendRoomChat(text: String, user: UserProfile, isEmoji: Boolean = false, asBulletComment: Boolean = false) {
        if (text.isBlank()) return
        val current = _session.value
        if (current.room?.banTextChatting == true && !current.currentUserRole.canModerate()) {
            appendSystemMessage("Text chatting is currently disabled by room moderators.")
            return
        }
        val bulletAllowed = asBulletComment && user.vipLevel >= 9
        val msg = RoomChatMessage(
            id = "rc_${UUID.randomUUID()}",
            senderId = user.uid,
            senderName = user.nickname,
            senderVip = user.vipLevel,
            text = text,
            type = if (isEmoji) RoomMessageType.EMOJI else RoomMessageType.TEXT,
            isBulletComment = bulletAllowed,
        )
        _session.update { it.copy(messages = (it.messages + msg).takeLast(60)) }
    }

    fun triggerGiftAndContributionUpdate(
        gift: VirtualGift,
        senderName: String,
        receiverName: String,
        comboCount: Int,
        totalCoins: Long,
        targetSeatIndices: List<Int>,
        updatedRoomLevel: Int,
        updatedTotalContribution: Long,
        updatedSeatCount: Int,
        contributionPointsAdded: Long,
        levelUpEvent: RoomLevelUpEvent?,
    ) {
        val effect = ActiveGiftEffect(
            id = UUID.randomUUID().toString(),
            gift = gift,
            senderName = senderName,
            receiverName = receiverName,
            comboCount = comboCount,
            totalCoins = totalCoins
        )
        _session.update { state ->
            val currentRoom = state.room
            val targetCapacity = (if (updatedRoomLevel < 7) 8 else maxOf(state.seats.size, updatedSeatCount)).coerceIn(8, 14)
            val resizedSeats = (0 until targetCapacity).map { idx ->
                val existing = state.seats.getOrNull(idx) ?: RoomSeat(seatIndex = idx)
                if (idx in targetSeatIndices && existing.occupantId != null) {
                    existing.copy(charmCount = existing.charmCount + totalCoins)
                } else existing
            }
            val updatedOwner = if (-1 in targetSeatIndices || targetSeatIndices.isEmpty()) {
                state.ownerInfo?.copy(charmCount = state.ownerInfo.charmCount + totalCoins)
            } else state.ownerInfo

            val updatedRoom = currentRoom?.copy(
                roomLevel = updatedRoomLevel,
                totalContribution = updatedTotalContribution,
                maxSeats = targetCapacity,
                popularity = currentRoom.popularity + totalCoins,
                pkScoreLeft = if (currentRoom.isPkActive) currentRoom.pkScoreLeft + totalCoins else currentRoom.pkScoreLeft,
                decorationStyle = levelUpEvent?.unlockedDecorations?.firstOrNull() ?: currentRoom.decorationStyle
            )
            val giftMsg = RoomChatMessage(
                id = "gf_${UUID.randomUUID()}",
                senderId = "gift",
                senderName = senderName,
                senderVip = 2,
                text = "sent ${gift.emoji} ${gift.name} x$comboCount to $receiverName (+$contributionPointsAdded pts)!",
                type = RoomMessageType.GIFT
            )
            state.copy(
                room = updatedRoom,
                ownerInfo = updatedOwner,
                seats = resizedSeats,
                activeGiftEffect = effect,
                levelUpEvent = levelUpEvent ?: state.levelUpEvent,
                messages = (state.messages + giftMsg).takeLast(60)
            )
        }
        giftDismissJob?.cancel()
        giftDismissJob = scope.launch {
            delay(gift.durationMs)
            _session.update { it.copy(activeGiftEffect = null) }
        }
        if (levelUpEvent != null) {
            appendSystemMessage("🎉 LEVEL UP! Room reached Level ${levelUpEvent.newLevel} — ${levelUpEvent.newSeatCount} golden mic seats unlocked!")
            levelUpDismissJob?.cancel()
            levelUpDismissJob = scope.launch {
                delay(4800)
                _session.update { it.copy(levelUpEvent = null) }
            }
        }
    }

    fun startPkBattle(mode: String, opponentName: String, durationSec: Int = 180) {
        _session.update { state ->
            state.copy(
                room = state.room?.copy(
                    isPkActive = true,
                    pkMode = mode,
                    pkOpponentName = opponentName,
                    pkScoreLeft = 450,
                    pkScoreRight = 420,
                    pkSecondsLeft = durationSec
                )
            )
        }
        appendSystemMessage("PK Battle started ($mode vs $opponentName)! Send gifts to boost score!")
        startPkLoopIfActive()
    }

    fun endPkBattle() {
        pkTimerJob?.cancel()
        val rm = _session.value.room ?: return
        val winner = if (rm.pkScoreLeft >= rm.pkScoreRight) rm.hostName else rm.pkOpponentName
        _session.update { it.copy(room = rm.copy(isPkActive = false)) }
        appendSystemMessage("PK Battle ended! Winner: $winner (${rm.pkScoreLeft} vs ${rm.pkScoreRight})")
    }

    fun toggleMusicState(playing: Boolean) {
        _session.update { state ->
            state.copy(room = state.room?.copy(isMusicPlaying = playing))
        }
    }

    private fun startPkLoopIfActive() {
        pkTimerJob?.cancel()
        if (_session.value.room?.isPkActive != true) return
        pkTimerJob = scope.launch {
            while (isActive) {
                delay(1000)
                val rm = _session.value.room ?: break
                if (!rm.isPkActive) break
                if (rm.pkSecondsLeft <= 1) {
                    endPkBattle()
                    break
                } else {
                    val opponentTick = if ((0..5).random() == 0) (20L..90L).random() else 0L
                    _session.update {
                        it.copy(
                            room = rm.copy(
                                pkSecondsLeft = rm.pkSecondsLeft - 1,
                                pkScoreRight = rm.pkScoreRight + opponentTick
                            )
                        )
                    }
                }
            }
        }
    }

    private fun seatUserAt(seatIndex: Int, user: UserProfile) {
        _session.update { state ->
            val prevIdx = state.currentUserSeatIndex
            state.copy(
                currentUserSeatIndex = seatIndex,
                isUserMuted = false,
                seats = state.seats.map { seat ->
                    when (seat.seatIndex) {
                        seatIndex -> seat.copy(
                            occupantId = user.uid,
                            occupantName = user.nickname,
                            occupantInitials = initialsOf(user.nickname),
                            accentColorHex = user.accentColorHex,
                            isCoHost = state.currentUserRole.canModerate(),
                            isMuted = false,
                            vipLevel = user.vipLevel,
                            userLevel = user.userLevel,
                            role = state.currentUserRole,
                        )
                        prevIdx -> seat.copy(
                            occupantId = null,
                            occupantName = null,
                            occupantInitials = "",
                            isCoHost = false,
                            isMuted = false,
                            isSpeaking = false,
                            vipLevel = 0,
                            userLevel = 12,
                            role = RoomRole.NORMAL_MEMBER,
                        )
                        else -> seat
                    }
                }
            )
        }
        appendSystemMessage("You joined Seat #${seatIndex + 1}.")
    }

    fun appendSystemMessage(text: String) {
        val msg = RoomChatMessage(
            id = "sys_${UUID.randomUUID()}",
            senderId = "system",
            senderName = "system",
            senderVip = 0,
            text = text,
            type = RoomMessageType.SYSTEM
        )
        _session.update { it.copy(messages = (it.messages + msg).takeLast(60)) }
    }

    private fun buildRegularSeats(count: Int): List<RoomSeat> {
        // Level 1 starts with 8 symmetrical seats + sample speaker on seat 5 (index 4)
        return (0 until count).map { idx ->
            when (idx) {
                4 -> RoomSeat(
                    seatIndex = idx,
                    occupantId = "sp_2",
                    occupantName = "Jishan Bhati",
                    occupantInitials = "JB",
                    accentColorHex = 0xFF7B2CFF,
                    isCoHost = true,
                    isMuted = false,
                    isSpeaking = true,
                    rmsLevel = 0.36f,
                    vipLevel = 3,
                    userLevel = 21,
                    role = RoomRole.ROOM_ADMIN,
                    charmCount = 620
                )
                else -> RoomSeat(seatIndex = idx)
            }
        }
    }

    private fun seatCountForLevel(level: Int, fallback: Int): Int {
        val fromDefault = VoiceRoomSeedData.defaultLevelConfigs.firstOrNull { it.level == level }?.seatCount
            ?: if (level >= 7) 14 else 8
        return if (level < 7) 8 else maxOf(fromDefault, fallback).coerceIn(8, 14)
    }

    private fun initialsOf(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "WF"
        }
    }
}
