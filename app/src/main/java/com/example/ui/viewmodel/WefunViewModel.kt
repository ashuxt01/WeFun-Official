package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngineState
import com.example.audio.VoiceAudioEngine
import com.example.core.database.FirestoreManager
import com.example.core.security.AuthRepository
import com.example.data.local.WefunAppSettings
import com.example.data.model.AcceptedRelationship
import com.example.data.model.ActiveGiftEffect
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.AnnouncementCategory
import com.example.data.model.AvatarConfig
import com.example.data.model.BiddingMode
import com.example.data.model.ChatThread
import com.example.data.model.ContributorRankEntry
import com.example.data.model.DailyTaskItem
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.DirectMessage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameMatchRecord
import com.example.data.model.GiftWallSummary
import com.example.data.model.GrowthPointHistoryEntry
import com.example.data.model.GuardRelationship
import com.example.data.model.MicRequest
import com.example.data.model.MusicTrack
import com.example.data.model.NotificationItem
import com.example.data.model.ProfileStatistics
import com.example.data.model.RechargePackage
import com.example.data.model.RelationshipRequest
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomAnnouncementEntry
import com.example.data.model.RoomCategory
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomGiftLedgerEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomMode
import com.example.data.model.RoomRole
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SafetyReport
import com.example.data.model.SocialPost
import com.example.data.model.SongRequestItem
import com.example.data.model.SpecialEffect
import com.example.data.model.StartingToken
import com.example.data.model.UserProfile
import com.example.data.model.VipAuditLogEntry
import com.example.data.model.VipGiftItem
import com.example.data.model.VipMembershipState
import com.example.data.model.VipPaymentHistoryEntry
import com.example.data.model.VipTierConfig
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.model.WalletTransaction
import com.example.data.repository.CharmLevelCatalog
import com.example.data.repository.VipCatalog
import com.example.data.repository.VipGrowthEngine
import com.example.data.repository.VoiceRoomSeedData
import com.example.data.repository.WefunRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WefunViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository? = if (FirebaseApp.getApps(application).isNotEmpty()) {
        runCatching { AuthRepository() }.getOrNull()
    } else {
        null
    }
    private val firestoreManager: FirestoreManager? = if (FirebaseApp.getApps(application).isNotEmpty()) {
        runCatching { FirestoreManager(application) }.getOrNull()
    } else {
        null
    }

    private val repository = WefunRepository(application, firestoreManager, authRepository)
    private val audioEngine = VoiceAudioEngine(application)
    private val roomManager = LiveRoomManager(viewModelScope)

    val firebaseUser: StateFlow<FirebaseUser?> = (authRepository?.authStateFlow() ?: flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository?.currentUser())

    private val _isGoogleAuthBusy = MutableStateFlow(false)
    val isGoogleAuthBusy: StateFlow<Boolean> = _isGoogleAuthBusy.asStateFlow()

    val settings: StateFlow<WefunAppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WefunAppSettings())

    val currentUser: StateFlow<UserProfile?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rooms: StateFlow<List<VoiceRoom>> = repository.roomsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<WalletTransaction>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<SocialPost>> = repository.postsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameMatches: StateFlow<List<GameMatchRecord>> = repository.gameMatchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<SafetyReport>> = repository.reportsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val threads: StateFlow<List<ChatThread>> = repository.threadsFlow
    val families: StateFlow<List<FamilyInfo>> = repository.familiesFlow
    val events: StateFlow<List<EventItem>> = repository.eventsFlow
    val notifications: StateFlow<List<NotificationItem>> = repository.notificationsFlow
    val followedUserIds: StateFlow<Set<String>> = repository.followedUserIdsFlow
    val dailyTasks: StateFlow<List<DailyTaskItem>> = repository.dailyTasksFlow
    val auditLogs: StateFlow<List<String>> = repository.auditLogsFlow
    val packageInventory: StateFlow<Map<String, Int>> = repository.packageInventoryFlow
    val relationshipRequests: StateFlow<List<RelationshipRequest>> = repository.relationshipRequestsFlow
    val acceptedRelationships: StateFlow<List<AcceptedRelationship>> = repository.acceptedRelationshipsFlow
    val customRelationshipTags: StateFlow<List<RelationshipTagOption>> = repository.customRelationshipTagsFlow

    val vipConfigs: StateFlow<List<VipTierConfig>> = repository.vipConfigsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VipCatalog.defaultTiers)

    val vipGifts: StateFlow<List<VipGiftItem>> = repository.vipGiftsFlow

    val vipAuditLogs: StateFlow<List<VipAuditLogEntry>> = repository.vipAuditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vipMembershipState: StateFlow<VipMembershipState?> = combine(currentUser, vipConfigs) { user, configs ->
        user?.let {
            val now = System.currentTimeMillis()
            val epochDay = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
            VipGrowthEngine.buildMembershipState(it, configs, now, epochDay)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val vipGrowthHistory: StateFlow<List<GrowthPointHistoryEntry>> = currentUser
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeVipGrowthHistory(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val vipPaymentHistory: StateFlow<List<VipPaymentHistoryEntry>> = currentUser
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeVipPaymentHistory(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isJoiningRoom = MutableStateFlow(false)
    val isJoiningRoom: StateFlow<Boolean> = _isJoiningRoom.asStateFlow()

    private val _roomJoinError = MutableStateFlow<String?>(null)
    val roomJoinError: StateFlow<String?> = _roomJoinError.asStateFlow()

    val audioState: StateFlow<AudioEngineState> = audioEngine.state
    val liveRoomState: StateFlow<LiveRoomSessionState> = roomManager.session

    val roomLevelConfigs: StateFlow<List<RoomLevelConfig>> = repository.roomBackend.observeLevelConfigs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VoiceRoomSeedData.defaultLevelConfigs)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val activeRoomIdFlow = liveRoomState.map { it.room?.id }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomContribution: StateFlow<RoomContributionSummary?> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.roomBackend.observeContributionSummary(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomContributors: StateFlow<List<ContributorRankEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeContributorRankings(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomGiftLedger: StateFlow<List<RoomGiftLedgerEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeRoomGiftTransactions(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomAnnouncements: StateFlow<List<RoomAnnouncementEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeAnnouncements(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomActivityLogs: StateFlow<List<RoomActivityLogEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeActivityLogs(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeThreadId = MutableStateFlow<String?>(null)
    val activeThreadId: StateFlow<String?> = _activeThreadId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<DirectMessage>> = _activeThreadId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeThreadMessages(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val publicProfileOverrides: StateFlow<Map<String, UserProfile>> = repository.publicProfileOverridesFlow

    private val _profileGiftAnimation = MutableStateFlow<ActiveGiftEffect?>(null)
    val profileGiftAnimation: StateFlow<ActiveGiftEffect?> = _profileGiftAnimation.asStateFlow()

    fun observeGiftWall(ownerUid: String) = repository.observeGiftWall(ownerUid)
    fun observeAuthorizedGuards(ownerUid: String) = repository.observeAuthorizedGuards(ownerUid)
    fun observeAdvancedRooms(ownerUid: String) = repository.observeAdvancedRooms(ownerUid)
    fun observeProfileStatistics(profile: UserProfile) = repository.observeProfileStatistics(profile)
    fun observeSpecialEffects(ownerUid: String, charmLevel: Int, activeLevel: Int) =
        repository.observeSpecialEffects(ownerUid, charmLevel, activeLevel)

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
        viewModelScope.launch {
            audioEngine.state.collect { audio ->
                roomManager.updateUserMicTelemetry(audio.micRmsLevel, audio.isVoiceDetected)
            }
        }
    }

    fun consumeToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun refreshMicPermission() {
        audioEngine.refreshPermission()
    }

    fun attemptSilentGoogleSignIn(context: Context) {
        val authRepo = authRepository ?: return
        viewModelScope.launch {
            authRepo.attemptAutoSignIn(context).onSuccess { fbUser ->
                if (fbUser != null && !settings.value.isLoggedIn) {
                    val displayName = fbUser.displayName?.takeIf { it.isNotBlank() } ?: "Aria Vance"
                    val handle = displayName.lowercase().filter { it.isLetterOrDigit() }.ifBlank { "wefunstar" }
                    repository.completeSignIn(
                        nickname = displayName,
                        username = handle,
                        phone = fbUser.phoneNumber ?: "",
                        email = fbUser.email ?: "",
                        dob = "2000-05-14",
                        gender = "Prefer not to say",
                        country = "India",
                        language = "English",
                        photoUri = fbUser.photoUrl?.toString()
                    )
                }
            }
        }
    }

    fun signInWithGoogle(activityContext: Context) {
        val authRepo = authRepository
        if (authRepo == null) {
            _toastMessage.value = "Firebase Auth is not initialized in this environment."
            return
        }
        viewModelScope.launch {
            _isGoogleAuthBusy.value = true
            authRepo.onGoogleSignInClicked(activityContext)
                .onSuccess { fbUser ->
                    val displayName = fbUser.displayName?.takeIf { it.isNotBlank() } ?: "Aria Vance"
                    val handle = displayName.lowercase().filter { it.isLetterOrDigit() }.ifBlank { "wefunstar" }
                    repository.completeSignIn(
                        nickname = displayName,
                        username = handle,
                        phone = fbUser.phoneNumber ?: "",
                        email = fbUser.email ?: "",
                        dob = "2000-05-14",
                        gender = "Prefer not to say",
                        country = "India",
                        language = "English",
                        photoUri = fbUser.photoUrl?.toString()
                    )
                    _toastMessage.value = "Signed in with Google as $displayName"
                }
                .onFailure { err ->
                    _toastMessage.value = err.message ?: "Google Sign-In did not complete."
                }
            _isGoogleAuthBusy.value = false
        }
    }

    fun completeSignIn(
        nickname: String,
        username: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        country: String,
        language: String,
        photoUri: String?,
    ) {
        viewModelScope.launch {
            repository.completeSignIn(
                nickname = nickname,
                username = username,
                phone = phone,
                email = email,
                dob = dob,
                gender = gender,
                country = country,
                language = language,
                photoUri = photoUri
            )
        }
    }

    fun signOut() {
        leaveVoiceRoom()
        viewModelScope.launch {
            runCatching { authRepository?.signOut(getApplication()) }
            repository.signOut()
        }
    }

    fun deleteAccount() {
        leaveVoiceRoom()
        viewModelScope.launch {
            runCatching { authRepository?.signOut(getApplication()) }
            repository.deleteAccountAndReset()
            _toastMessage.value = "Account reset & local data cleared."
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setParentalControl(enabled: Boolean) {
        viewModelScope.launch {
            repository.setParentalControl(enabled)
            _toastMessage.value = if (enabled) "Parental Control & Safe Mode enabled." else "Parental Control disabled."
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguage(language)
            _toastMessage.value = "App language set to $language."
        }
    }

    fun claimDailyTask(taskId: String) {
        viewModelScope.launch {
            repository.claimDailyTask(taskId)
                .onSuccess { task ->
                    _toastMessage.value = "Task claimed: +${task.rewardCoins} Coins & +${task.rewardXp} XP!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun updateNotificationPrefs(gifts: Boolean, rooms: Boolean, pk: Boolean, messages: Boolean) {
        viewModelScope.launch {
            repository.updateNotificationPrefs(gifts, rooms, pk, messages)
            _toastMessage.value = "Notification preferences saved."
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            repository.claimDailyCheckIn()
                .onSuccess { reward ->
                    _toastMessage.value = "Claimed +$reward Coins & +150 XP!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun rechargeCoins(pkg: RechargePackage) {
        viewModelScope.launch {
            val refId = "gp_sim_${pkg.id}_${System.currentTimeMillis()}"
            repository.rechargeCoins(pkg, refId)
                .onSuccess { total ->
                    _toastMessage.value = "Verified +$total Coins added to your Wallet!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun purchaseVip(level: Int, costCoins: Long, tierName: String) {
        purchaseVipSubscription(level = level, months = 1)
    }

    fun purchaseVipSubscription(level: Int, months: Int = 1, autoRenew: Boolean? = null) {
        viewModelScope.launch {
            repository.purchaseVipSubscription(level, months, autoRenew)
                .onSuccess { msg -> _toastMessage.value = msg }
                .onFailure { err -> _toastMessage.value = err.message ?: "Unable to activate VIP." }
        }
    }

    fun restoreVipPurchases() {
        viewModelScope.launch {
            repository.restoreVipPurchases()
                .onSuccess { restoredCount ->
                    _toastMessage.value = if (restoredCount > 0) {
                        "Restored $restoredCount verified VIP purchase(s)!"
                    } else {
                        "All verified VIP purchases are up to date."
                    }
                }
                .onFailure { err -> _toastMessage.value = err.message ?: "Restore failed." }
        }
    }

    fun spendDiamondsForGrowth(
        category: DiamondSpendCategory,
        diamondsAmount: Long,
        description: String,
    ) {
        viewModelScope.launch {
            repository.spendDiamondsForVipGrowth(category, diamondsAmount, description)
                .onSuccess { out ->
                    _toastMessage.value = if (out.growthPointsEarned > 0L) {
                        if (out.leveledUp) {
                            "Earned +${out.growthPointsEarned} Growth Pts • VIP ${out.levelAfter} Unlocked!"
                        } else {
                            "$description: +${out.growthPointsEarned} VIP Growth Pts!"
                        }
                    } else {
                        "$description completed (Active VIP required to earn Growth Pts)."
                    }
                }
                .onFailure { err -> _toastMessage.value = err.message ?: "Spend failed." }
        }
    }

    fun claimVipDailyDiamonds() {
        viewModelScope.launch {
            repository.claimDailyVipDiamonds()
                .onSuccess { bonus -> _toastMessage.value = "Claimed +$bonus VIP Daily Diamonds!" }
                .onFailure { err -> _toastMessage.value = err.message ?: "Unable to claim VIP Diamonds." }
        }
    }

    fun toggleVipAutoRenewal(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleVipAutoRenewal(enabled)
                .onSuccess {
                    _toastMessage.value = if (it) {
                        "VIP auto-renewal enabled (billed 24h prior to expiration)."
                    } else {
                        "VIP auto-renewal disabled."
                    }
                }
                .onFailure { err -> _toastMessage.value = err.message }
        }
    }

    fun evaluateVipRegulation(simulatedAdvanceDays: Int = 0) {
        viewModelScope.launch {
            repository.evaluateVipRegulation(simulatedAdvanceDays)
                .onSuccess { eval -> _toastMessage.value = eval.summaryMessage }
                .onFailure { err -> _toastMessage.value = err.message }
        }
    }

    fun updateVipPrivacySettings(
        customTitle: String,
        hideRecentGifts: Boolean,
        hideGuardianBoard: Boolean,
        privateProfileVisit: Boolean,
        friendRequestProtection: Boolean,
        customNamecardTheme: String,
    ) {
        viewModelScope.launch {
            repository.updateVipPrivacyAndCustomization(
                customTitle = customTitle,
                hideRecentGifts = hideRecentGifts,
                hideGuardianBoard = hideGuardianBoard,
                privateProfileVisit = privateProfileVisit,
                friendRequestProtection = friendRequestProtection,
                customNamecardTheme = customNamecardTheme,
            ).onSuccess {
                _toastMessage.value = "VIP privacy & customization saved!"
            }.onFailure { err ->
                _toastMessage.value = err.message
            }
        }
    }

    fun sendVipExclusiveGift(gift: VipGiftItem, recipientUid: String, recipientName: String) {
        viewModelScope.launch {
            repository.sendVipExclusiveGift(gift, recipientUid, recipientName)
                .onSuccess { out ->
                    if (gift.grantsMuteProtectionSeconds > 0) {
                        val session = liveRoomState.value
                        val targetSeat = session.seats.firstOrNull { it.occupantId == recipientUid }
                            ?: session.currentUserSeatIndex?.takeIf { it >= 0 }?.let { idx ->
                                session.seats.firstOrNull { it.seatIndex == idx }
                            }
                        if (targetSeat != null) {
                            roomManager.activateMuteProtection(
                                targetSeat.seatIndex,
                                gift.grantsMuteProtectionSeconds * 1000L
                            )
                        }
                    }
                    _toastMessage.value = "Sent ${gift.name} to $recipientName (+${out.growthPointsEarned} Growth Pts)!"
                }
                .onFailure { err -> _toastMessage.value = err.message ?: "Unable to send VIP gift." }
        }
    }

    fun adminUpdateVipConfig(config: VipTierConfig) {
        viewModelScope.launch {
            repository.adminUpdateVipConfig(config)
                .onSuccess { _toastMessage.value = "Admin saved ${config.name} rules." }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun adminGrantUserVip(level: Int, months: Int, active: Boolean) {
        viewModelScope.launch {
            repository.adminGrantOrUpdateUserVip(level, months, active)
                .onSuccess { _toastMessage.value = "Admin updated user VIP to VIP $level (active=$active)." }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun adminAdjustGrowthPoints(delta: Long, reason: String) {
        viewModelScope.launch {
            repository.adminAdjustGrowthPoints(delta, reason)
                .onSuccess { _toastMessage.value = "Admin adjusted Growth Pts by $delta." }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun adminSaveVipGift(gift: VipGiftItem) {
        repository.adminSaveVipGift(gift)
        _toastMessage.value = "Admin saved VIP Gift '${gift.name}'."
    }

    fun updateProfileAndAvatar(
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        avatarConfig: AvatarConfig,
    ) {
        viewModelScope.launch {
            repository.updateProfileAndAvatar(
                nickname, bio, country, language, showGender, showAge, photoUri, avatarConfig
            )
            _toastMessage.value = "Profile & 3D Avatar saved."
        }
    }

    fun createRoomListing(
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
        roomMode: RoomMode = RoomMode.NORMAL,
        backdropTheme: VoiceRoomBackdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE,
        autoEnter: Boolean = true,
    ) {
        viewModelScope.launch {
            _isJoiningRoom.value = autoEnter
            _roomJoinError.value = null
            runCatching {
                repository.ensureSeeded()
                val themeStyle = when {
                    roomMode == RoomMode.AUCTION -> RoomThemeStyle.ROYAL_GOLD
                    backdropTheme == VoiceRoomBackdropTheme.AURORA_VIOLET -> RoomThemeStyle.GALAXY
                    backdropTheme == VoiceRoomBackdropTheme.ROSE_VELVET -> RoomThemeStyle.SAKURA_NIGHT
                    backdropTheme == VoiceRoomBackdropTheme.AUCTION_ROYAL -> RoomThemeStyle.ROYAL_GOLD
                    else -> RoomThemeStyle.CYBER_NEON
                }
                val created = repository.createVoiceRoom(
                    title = title,
                    description = description,
                    category = category,
                    maxSeats = 8,
                    password = password,
                    theme = themeStyle,
                    roomMode = roomMode,
                    backdropTheme = backdropTheme,
                )
                if (autoEnter) {
                    if (liveRoomState.value.room != null) {
                        audioEngine.stopMicCapture()
                        audioEngine.toggleBackgroundMusic(false)
                    }
                    val user = currentUser.value ?: UserProfile(
                        uid = created.hostId,
                        wefunId = "WF-804921",
                        username = created.hostName.lowercase().replace(" ", ""),
                        nickname = created.hostName,
                        bio = "WeFun Room Host",
                        vipLevel = created.hostVip,
                    )
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(created.id)
                    roomManager.enterRoom(created, user, owner, partner, roles)
                    audioEngine.startMicCapture()
                    _toastMessage.value = "Room \"${created.title}\" created (ID: ${created.id})!"
                } else {
                    _toastMessage.value = "Room listing \"${created.title}\" published!"
                }
            }.onFailure { err ->
                val msg = err.message ?: "Unable to create voice room right now."
                _roomJoinError.value = msg
                _toastMessage.value = msg
            }
            _isJoiningRoom.value = false
        }
    }

    fun saveAvatarConfig(avatarConfig: AvatarConfig) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProfileAndAvatar(
                nickname = user.nickname,
                bio = user.bio,
                country = user.country,
                language = user.language,
                showGender = user.showGender,
                showAge = user.showAge,
                photoUri = user.photoUri,
                avatarConfig = avatarConfig
            )
            _toastMessage.value = "PLAY Show outfit saved!"
        }
    }

    fun createAndJoinRoom(
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
        roomMode: RoomMode = RoomMode.NORMAL,
        backdropTheme: VoiceRoomBackdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE,
    ) {
        viewModelScope.launch {
            _isJoiningRoom.value = true
            _roomJoinError.value = null
            runCatching {
                repository.ensureSeeded()
                if (liveRoomState.value.room != null) {
                    audioEngine.stopMicCapture()
                    audioEngine.toggleBackgroundMusic(false)
                }
                val created = repository.createVoiceRoom(
                    title = title,
                    description = description,
                    category = category,
                    maxSeats = maxSeats,
                    password = password,
                    theme = theme,
                    roomMode = roomMode,
                    backdropTheme = backdropTheme,
                )
                val user = currentUser.value ?: UserProfile(
                    uid = created.hostId,
                    wefunId = "WF-804921",
                    username = created.hostName.lowercase().replace(" ", ""),
                    nickname = created.hostName,
                    bio = "WeFun Room Host",
                    vipLevel = created.hostVip,
                )
                val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(created.id)
                roomManager.enterRoom(created, user, owner, partner, roles)
                audioEngine.startMicCapture()
                _toastMessage.value = "Room \"${created.title}\" created (ID: ${created.id})!"
            }.onFailure { err ->
                val msg = err.message ?: "Unable to create voice room."
                _roomJoinError.value = msg
                _toastMessage.value = msg
            }
            _isJoiningRoom.value = false
        }
    }

    fun joinRoom(room: VoiceRoom) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isJoiningRoom.value = true
            _roomJoinError.value = null
            if (liveRoomState.value.room != null && liveRoomState.value.room?.id != room.id) {
                audioEngine.stopMicCapture()
                audioEngine.toggleBackgroundMusic(false)
            }
            repository.markRoomJoined(room.id)
            val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
            roomManager.enterRoom(room, user, owner, partner, roles)
            _isJoiningRoom.value = false
        }
    }

    fun joinRoomById(roomId: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isJoiningRoom.value = true
            _roomJoinError.value = null
            repository.getRoomByIdValidated(roomId)
                .onSuccess { room ->
                    if (liveRoomState.value.room != null && liveRoomState.value.room?.id != room.id) {
                        audioEngine.stopMicCapture()
                        audioEngine.toggleBackgroundMusic(false)
                    }
                    repository.markRoomJoined(room.id)
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
                    roomManager.enterRoom(room, user, owner, partner, roles)
                }
                .onFailure { err ->
                    val msg = err.message ?: "Room ID '$roomId' was not found."
                    _roomJoinError.value = msg
                    _toastMessage.value = msg
                }
            _isJoiningRoom.value = false
        }
    }

    fun consumeRoomJoinError() {
        _roomJoinError.value = null
    }

    fun updateFullRoomSettings(
        title: String,
        announcement: String,
        password: String?,
        roomMode: RoomMode,
        tags: List<String>,
        backdropTheme: VoiceRoomBackdropTheme,
        highQualityAudio: Boolean,
        banTextChatting: Boolean,
        banPhotoChatting: Boolean,
        banRedPacket: Boolean,
        membershipFeeCoins: Long,
        musicAdminsOnly: Boolean,
    ) {
        val session = liveRoomState.value
        val room = session.room ?: return
        val actor = currentUser.value?.nickname ?: "Owner"
        viewModelScope.launch {
            repository.roomBackend.updateFullRoomSettings(
                roomId = room.id,
                actorName = actor,
                actorRole = session.currentUserRole,
                title = title,
                announcement = announcement,
                password = password,
                roomMode = roomMode,
                tags = tags,
                backdropTheme = backdropTheme,
                highQualityAudio = highQualityAudio,
                banTextChatting = banTextChatting,
                banPhotoChatting = banPhotoChatting,
                banRedPacket = banRedPacket,
                membershipFeeCoins = membershipFeeCoins,
                musicAdminsOnly = musicAdminsOnly,
            ).onSuccess { entity ->
                roomManager.updateRoomFullState(repository.domainRoomFromEntity(entity))
                _toastMessage.value = "Room settings saved!"
            }.onFailure {
                _toastMessage.value = it.message ?: "Unable to save room settings."
            }
        }
    }

    fun switchRoomMode(mode: RoomMode) {
        val session = liveRoomState.value
        val room = session.room ?: return
        val actor = currentUser.value?.nickname ?: "Owner"
        if (!session.currentUserRole.canManageRoom()) {
            _toastMessage.value = "Only Owner or Room Admins can switch Room Mode."
            return
        }
        viewModelScope.launch {
            repository.roomBackend.updateFullRoomSettings(
                roomId = room.id,
                actorName = actor,
                actorRole = session.currentUserRole,
                title = room.title,
                announcement = room.announcement,
                password = room.password,
                roomMode = mode,
                tags = room.tags,
                backdropTheme = if (mode == RoomMode.AUCTION) VoiceRoomBackdropTheme.AUCTION_ROYAL else room.backdropTheme,
                highQualityAudio = room.highQualityAudio,
                banTextChatting = room.banTextChatting,
                banPhotoChatting = room.banPhotoChatting,
                banRedPacket = room.banRedPacket,
                membershipFeeCoins = room.membershipFeeCoins,
                musicAdminsOnly = room.musicAdminsOnly,
            ).onSuccess {
                roomManager.switchRoomMode(mode)
                _toastMessage.value = "Switched to ${mode.label}"
            }.onFailure {
                _toastMessage.value = it.message
            }
        }
    }

    fun createCustomRelationshipTag(label: String, mode: BiddingMode): Result<RelationshipTagOption> {
        val res = repository.addCustomRelationshipTag(label, mode)
        res.onSuccess {
            _toastMessage.value = "Custom tag '${it.label}' created!"
        }
        return res
    }

    fun confirmAuctionRelationshipSetup(
        mode: BiddingMode,
        tag: RelationshipTagOption?,
        customLabel: String,
        token: StartingToken?,
        targetUserId: String?,
        targetUserName: String?,
        startingPrice: Long,
        sendRequestNow: Boolean = true,
    ) {
        val room = liveRoomState.value.room ?: return
        roomManager.configureAuction(
            mode = mode,
            tag = tag,
            customLabel = customLabel,
            token = token,
            targetUserId = targetUserId,
            targetUserName = targetUserName,
            startingPrice = startingPrice,
        )
        if (sendRequestNow && !targetUserId.isNullOrBlank() && !targetUserName.isNullOrBlank()) {
            viewModelScope.launch {
                val chosenTag = tag?.label ?: customLabel.ifBlank { "BFF" }
                repository.sendRelationshipRequest(
                    roomId = room.id,
                    targetUserId = targetUserId,
                    targetName = targetUserName,
                    tagLabel = chosenTag,
                    mode = mode,
                    tokenTitle = token?.title ?: "Scooter Token",
                    bidPoints = startingPrice,
                ).onSuccess {
                    _toastMessage.value = "Relationship request '$chosenTag' sent to $targetUserName!"
                }.onFailure {
                    _toastMessage.value = it.message
                }
            }
        } else {
            _toastMessage.value = "Auction relationship settings saved."
        }
    }

    fun acceptRelationshipRequest(requestId: String) {
        repository.respondToRelationshipRequest(requestId, accept = true)
            .onSuccess { req ->
                roomManager.appendSystemMessage("💞 Relationship formed: ${req.tagLabel} between ${req.senderName} & ${req.targetUserName}!")
                _toastMessage.value = "Accepted ${req.tagLabel} relationship!"
            }
            .onFailure { _toastMessage.value = it.message }
    }

    fun declineRelationshipRequest(requestId: String) {
        repository.respondToRelationshipRequest(requestId, accept = false)
            .onSuccess { req ->
                _toastMessage.value = "Declined ${req.tagLabel} request."
            }
            .onFailure { _toastMessage.value = it.message }
    }

    fun placeAuctionBid(points: Long) {
        val user = currentUser.value ?: return
        roomManager.placeAuctionBid(user.uid, user.nickname, points)
            .onSuccess {
                _toastMessage.value = "Bid ${points} virtual points placed!"
            }
            .onFailure { _toastMessage.value = it.message }
    }

    fun findPublicProfile(userId: String, fallbackName: String? = null): UserProfile =
        repository.findPublicProfileWithRelationships(userId, fallbackName)

    fun saveFullProfileEdits(
        targetUid: String,
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        coverPhotoUri: String?,
        onComplete: (Result<UserProfile>) -> Unit = {},
    ) {
        val actorUid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            val res = repository.updateProfileWithOwnershipCheck(
                actorUid = actorUid,
                targetUid = targetUid,
                nickname = nickname,
                bio = bio,
                country = country,
                language = language,
                showGender = showGender,
                showAge = showAge,
                photoUri = photoUri,
                coverPhotoUri = coverPhotoUri,
            )
            res.onSuccess {
                _toastMessage.value = "Profile changes saved!"
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Unable to update profile."
            }
            onComplete(res)
        }
    }

    fun updateProfileSignature(
        targetUid: String,
        rawSignature: String,
        onComplete: (Result<String>) -> Unit = {},
    ) {
        val actorUid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            val res = repository.updateUserSignature(actorUid, targetUid, rawSignature)
            res.onSuccess { updated ->
                _toastMessage.value = if (updated.isBlank()) "Signature cleared." else "Signature saved!"
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Signature update failed."
            }
            onComplete(res)
        }
    }

    fun sendGiftFromProfile(
        targetUser: UserProfile,
        gift: VirtualGift,
        quantity: Int,
        fromPackage: Boolean,
    ) {
        val sender = currentUser.value ?: return
        val refId = "profile_gift_${UUID.randomUUID()}"
        viewModelScope.launch {
            repository.sendProfileGift(
                targetUser = targetUser,
                gift = gift,
                quantity = quantity,
                fromPackage = fromPackage,
                referenceId = refId,
            ).onSuccess { result ->
                _profileGiftAnimation.value = ActiveGiftEffect(
                    id = refId,
                    gift = gift,
                    senderName = sender.nickname,
                    receiverName = targetUser.nickname,
                    comboCount = result.quantity,
                    totalCoins = result.totalCoins,
                )
                _toastMessage.value = "Sent ${gift.name} x${result.quantity} to ${targetUser.nickname} (+${result.starsAdded} Stars)!"
                kotlinx.coroutines.delay(gift.durationMs)
                if (_profileGiftAnimation.value?.id == refId) {
                    _profileGiftAnimation.value = null
                }
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Unable to send gift."
            }
        }
    }

    fun dismissProfileGiftAnimation() {
        _profileGiftAnimation.value = null
    }

    fun cancelRelationship(relationshipId: String) {
        val actorUid = currentUser.value?.uid ?: return
        repository.cancelAcceptedRelationship(actorUid, relationshipId)
            .onSuccess { rel ->
                _toastMessage.value = "Removed ${rel.tagLabel} relationship with ${rel.partnerName}."
            }
            .onFailure { err ->
                _toastMessage.value = err.message ?: "Unable to cancel relationship."
            }
    }

    fun toggleSpecialEffect(effectId: String) {
        val actorUid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            repository.toggleSpecialEffectActivation(actorUid, effectId)
                .onSuccess { fx ->
                    _toastMessage.value = if (fx.isActive) "${fx.title} activated!" else "${fx.title} deactivated."
                }
                .onFailure { err ->
                    _toastMessage.value = err.message ?: "Unable to update effect."
                }
        }
    }

    fun minimizeVoiceRoom() = roomManager.minimizeRoom()
    fun restoreVoiceRoom() = roomManager.restoreRoom()
    fun dismissLevelUpBanner() = roomManager.dismissLevelUpBanner()

    fun leaveVoiceRoom() {
        audioEngine.stopMicCapture()
        audioEngine.toggleBackgroundMusic(false)
        roomManager.leaveRoom()
    }

    fun toggleMicMute() {
        val muted = roomManager.toggleUserMute()
        if (muted) {
            audioEngine.stopMicCapture()
        } else {
            audioEngine.startMicCapture()
        }
    }

    fun tapSeat(seatIndex: Int) {
        val user = currentUser.value ?: return
        val session = liveRoomState.value
        if (session.pendingMoveFromSeat != null) {
            roomManager.completeMoveSeat(seatIndex)
            return
        }
        if (session.currentUserSeatIndex == seatIndex) {
            roomManager.leaveMicSeat()
            audioEngine.stopMicCapture()
            return
        }
        roomManager.takeOrRequestSeat(seatIndex, user)
        if (liveRoomState.value.currentUserSeatIndex == seatIndex) {
            audioEngine.startMicCapture()
        }
    }

    fun toggleFavoriteRoom() {
        val room = liveRoomState.value.room ?: return
        viewModelScope.launch {
            val fav = repository.roomBackend.toggleFavoriteRoom(room.id)
            roomManager.updateFavoriteState(fav)
            _toastMessage.value = if (fav) "Added room to favorites!" else "Removed from favorites."
        }
    }

    fun acceptMicRequest(req: MicRequest) = roomManager.acceptMicRequest(req)
    fun rejectMicRequest(userId: String) = roomManager.rejectMicRequest(userId)
    fun toggleSeatLock(seatIndex: Int) = roomManager.toggleSeatLock(seatIndex)
    fun toggleSeatMicLock(seatIndex: Int) = roomManager.toggleSeatMicLock(seatIndex)
    fun toggleOccupantMuteByAdmin(seatIndex: Int) = roomManager.toggleOccupantMuteByAdmin(seatIndex)
    fun toggleSeatCoHost(seatIndex: Int) = roomManager.toggleSeatCoHost(seatIndex)
    fun removeUserFromSeat(seatIndex: Int) = roomManager.removeUserFromSeat(seatIndex)
    fun startMoveSeat(seatIndex: Int) = roomManager.startMoveSeat(seatIndex)
    fun reserveSeat(seatIndex: Int, name: String?) = roomManager.reserveSeat(seatIndex, name)
    fun assignUserToSeat(seatIndex: Int, userId: String, name: String, vip: Int) =
        roomManager.assignUserToSeat(seatIndex, userId, name, vip)

    fun banUserFromRoom(userId: String, name: String) {
        val room = liveRoomState.value.room ?: return
        val actor = currentUser.value?.nickname ?: "Moderator"
        roomManager.banUserFromRoom(userId, name)
        viewModelScope.launch {
            repository.roomBackend.logActivity(room.id, actor, "User Banned", "$name banned from room")
        }
    }

    fun assignMemberRole(targetUserId: String, targetName: String, newRole: RoomRole) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.assignRole(room.id, actor.nickname, session.currentUserRole, targetUserId, targetName, newRole)
                .onSuccess {
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
                    roomManager.refreshSnapshotRoles(owner, partner, roles, actor.uid)
                    _toastMessage.value = "$targetName updated to ${newRole.label}."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun transferHostToSeat(seatIndex: Int) {
        val seat = liveRoomState.value.seats.getOrNull(seatIndex) ?: return
        val targetId = seat.occupantId ?: return
        val targetName = seat.occupantName ?: return
        assignMemberRole(targetId, targetName, RoomRole.OWNER)
        roomManager.removeUserFromSeat(seatIndex, force = true)
    }

    fun publishRoomAnnouncement(category: AnnouncementCategory, title: String, content: String) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.publishAnnouncement(room.id, actor.nickname, session.currentUserRole, category, title, content)
                .onSuccess {
                    roomManager.updateRoomMetadata(announcement = content)
                    _toastMessage.value = "Announcement published!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun assignPartner(targetUserId: String, targetName: String) {
        assignMemberRole(targetUserId, targetName, RoomRole.PARTNER)
    }

    fun removePartner() {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.removePartner(room.id, actor.nickname, session.currentUserRole)
                .onSuccess {
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
                    roomManager.refreshSnapshotRoles(owner, partner, roles, actor.uid)
                    _toastMessage.value = "Partner position cleared."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun togglePartnerSeatEnabled(enabled: Boolean) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.setPartnerSeatEnabled(room.id, actor.nickname, session.currentUserRole, enabled)
                .onSuccess {
                    roomManager.updatePartnerSeatEnabled(enabled)
                    repository.syncPartnerSeatToCloudIfAuthenticated(room.id, enabled)
                    _toastMessage.value = if (enabled) "Partner seat enabled beside Owner." else "Partner seat hidden."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun updateRoomBackdropTheme(backdropTheme: VoiceRoomBackdropTheme) {
        val roomId = liveRoomState.value.room?.id
        roomManager.updateRoomMetadata(backdropTheme = backdropTheme)
        if (roomId != null) {
            viewModelScope.launch {
                repository.updateRoomBackdrop(roomId, backdropTheme)
            }
        }
        _toastMessage.value = "Room backdrop changed to ${backdropTheme.label}."
    }

    fun updateRoomAppearance(title: String, theme: RoomThemeStyle, decoration: String, musicAdminsOnly: Boolean) {
        val room = liveRoomState.value.room ?: return
        val actor = currentUser.value?.nickname ?: "Owner"
        viewModelScope.launch {
            repository.roomBackend.updateRoomAppearanceAndPermissions(room.id, actor, title, theme, decoration, musicAdminsOnly)
            roomManager.updateRoomMetadata(title = title, theme = theme, decorationStyle = decoration, musicAdminsOnly = musicAdminsOnly)
            _toastMessage.value = "Room appearance & permissions saved."
        }
    }

    fun updateAdminLevelSeatConfig(seatCounts: Map<Int, Int>, pointsPerCoin: Double, diamondsPerCoin: Double) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.updateLevelSeatMappingAndRules(
                room.id, actor.nickname, session.currentUserRole, seatCounts, pointsPerCoin, diamondsPerCoin
            ).onSuccess {
                _toastMessage.value = "Level 1–10 seat capacities & conversion rules updated."
            }.onFailure { _toastMessage.value = it.message }
        }
    }

    fun sendRoomChat(text: String, isEmoji: Boolean = false, asBulletComment: Boolean = false) {
        val user = currentUser.value ?: return
        roomManager.sendRoomChat(text, user, isEmoji, asBulletComment)
    }

    fun playVoiceRoomBingo(diamondsAmount: Long = 100L) {
        spendDiamondsForGrowth(DiamondSpendCategory.VOICE_ROOM_BINGO, diamondsAmount, "Voice Room Bingo")
    }

    fun triggerRoomMuteProtection(seatIndex: Int) {
        val user = currentUser.value ?: return
        if (user.vipLevel < 12) {
            _toastMessage.value = "VIP 12 is required to activate Mute Protection VIP Gift."
            return
        }
        roomManager.activateMuteProtection(seatIndex)
        _toastMessage.value = "🛡️ Mute Protection aura active on Seat #${seatIndex + 1}!"
    }

    fun sendVirtualGiftInRoom(
        gift: VirtualGift,
        comboCount: Int,
        targetSeatIndices: List<Int>,
        receiverLabel: String,
        fromPackage: Boolean = false,
    ) {
        val user = currentUser.value ?: return
        val session = liveRoomState.value
        val room = session.room ?: return
        val countTargets = targetSeatIndices.size.coerceAtLeast(1)
        val firstSeat = targetSeatIndices.firstOrNull()?.let { idx ->
            session.seats.firstOrNull { it.seatIndex == idx }
        }
        val receiverId = firstSeat?.occupantId ?: room.hostId
        viewModelScope.launch {
            repository.consumePackageOrSpendCoinsForGift(
                gift = gift,
                comboCount = comboCount,
                receiverCount = countTargets,
                receiverLabel = receiverLabel,
                fromPackage = fromPackage,
            ).onSuccess { totalCoins ->
                    val process = repository.roomBackend.processRoomGiftContribution(
                        roomId = room.id,
                        sender = user,
                        receiverId = receiverId,
                        receiverName = receiverLabel,
                        gift = gift,
                        comboCount = comboCount,
                        totalCoins = totalCoins,
                    )
                    repository.creditReceivedGiftLedger(
                        recipientUid = receiverId,
                        gift = gift,
                        quantity = comboCount,
                        starsPerUnit = (gift.coinPrice / 20L).coerceAtLeast(1L),
                        charmAdded = totalCoins,
                    )
                    roomManager.triggerGiftAndContributionUpdate(
                        gift = gift,
                        senderName = user.nickname,
                        receiverName = receiverLabel,
                        comboCount = comboCount,
                        totalCoins = totalCoins,
                        targetSeatIndices = targetSeatIndices,
                        updatedRoomLevel = process.updatedRoomLevel,
                        updatedTotalContribution = process.updatedTotalContribution,
                        updatedSeatCount = process.updatedSeatCount,
                        contributionPointsAdded = process.contributionPointsAdded,
                        levelUpEvent = process.levelUpEvent,
                    )
                    if (process.levelUpEvent != null) {
                        repository.syncRoomCapacityToCloudIfAuthenticated(
                            roomId = room.id,
                            roomLevel = process.updatedRoomLevel,
                            maxSeats = process.updatedSeatCount,
                        )
                    }
                    repository.pushNotification(
                        category = "Gifts",
                        title = "Gift Delivered!",
                        body = "Sent ${gift.name} x$comboCount ($totalCoins Coins, +${process.contributionPointsAdded} Room Pts) to $receiverLabel."
                    )
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun startPkBattle(mode: String, opponentName: String) = roomManager.startPkBattle(mode, opponentName)
    fun endPkBattle() = roomManager.endPkBattle()

    fun toggleRoomMusic() {
        val session = liveRoomState.value
        val rm = session.room ?: return
        if (rm.musicAdminsOnly && !session.currentUserRole.canModerate()) {
            _toastMessage.value = "Music controls are restricted to Room Admins."
            return
        }
        val next = !rm.isMusicPlaying
        roomManager.toggleMusicState(next)
        audioEngine.toggleBackgroundMusic(next)
    }

    fun selectOrRequestTrack(track: MusicTrack) {
        val session = liveRoomState.value
        val user = currentUser.value ?: return
        val isAdmin = session.currentUserRole.canModerate() || !(session.room?.musicAdminsOnly ?: false)
        roomManager.requestOrSelectTrack(track, user.nickname, isAdmin)
        if (isAdmin) {
            audioEngine.toggleBackgroundMusic(true)
        } else {
            _toastMessage.value = "Song requested: ${track.title}"
        }
    }

    fun approveSongRequest(item: SongRequestItem) {
        if (!liveRoomState.value.currentUserRole.canModerate()) {
            _toastMessage.value = "Only Moderators/Admins can approve music queue."
            return
        }
        roomManager.approveAndPlayQueueItem(item)
        audioEngine.toggleBackgroundMusic(true)
    }

    fun createSocialPost(content: String, hashtag: String, mediaType: String, imageUri: String?) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.createSocialPost(content, hashtag, mediaType, imageUri)
            _toastMessage.value = "Moment published (+45 XP)!"
        }
    }

    fun toggleLikePost(post: SocialPost) {
        viewModelScope.launch { repository.toggleLikePost(post) }
    }

    fun commentOnPost(post: SocialPost) {
        viewModelScope.launch {
            repository.addCommentToPost(post)
            _toastMessage.value = "Comment added!"
        }
    }

    fun openThread(threadId: String?) {
        _activeThreadId.value = threadId
    }

    fun sendDirectMessage(threadId: String, text: String, gift: VirtualGift? = null, voiceSeconds: Int? = null) {
        viewModelScope.launch {
            if (gift != null) {
                val res = repository.spendCoinsForGift(gift, 1, 1, "Chat Peer")
                if (res.isFailure) {
                    _toastMessage.value = res.exceptionOrNull()?.message
                    return@launch
                }
            }
            repository.sendDirectMessage(threadId, text, gift?.name, voiceSeconds)
        }
    }

    fun deleteDirectMessage(id: String) {
        viewModelScope.launch { repository.deleteDirectMessage(id) }
    }

    fun reactToDirectMessage(message: DirectMessage, emoji: String) {
        viewModelScope.launch { repository.reactToDirectMessage(message, emoji) }
    }

    fun toggleMuteThread(threadId: String) {
        repository.toggleMuteThread(threadId)
        _toastMessage.value = "Conversation mute preference updated."
    }

    fun markAllThreadsRead() {
        repository.markAllThreadsRead()
        _toastMessage.value = "All chats marked as read."
    }

    fun acceptFriendRequest(threadId: String) {
        repository.acceptFriendRequest(threadId)
        _toastMessage.value = "Friend request accepted!"
    }

    fun declineFriendRequest(threadId: String) {
        repository.declineFriendRequest(threadId)
        _toastMessage.value = "Friend request removed."
    }

    fun startOrOpenChat(peerId: String, peerName: String, isGroup: Boolean = false) {
        val tid = repository.startOrOpenChat(peerId, peerName, isGroup)
        _activeThreadId.value = tid
    }

    fun recordGameMatch(gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) {
        viewModelScope.launch {
            repository.recordGameResult(gameTitle, outcome, score, coinsWon, xpEarned)
            _toastMessage.value = "$gameTitle: $outcome (+$coinsWon Coins, +$xpEarned XP)"
        }
    }

    fun toggleFamilyMembership(familyId: String) = repository.toggleFamilyMembership(familyId)
    fun createFamily(name: String, intro: String, emoji: String) {
        repository.createFamily(name, intro, emoji)
        _toastMessage.value = "Family '$name' created!"
    }

    fun toggleEventRegistration(eventId: String) {
        repository.toggleEventRegistration(eventId)
        _toastMessage.value = "Event registration updated."
    }

    fun toggleFollow(userId: String) = repository.toggleFollowUser(userId)

    fun toggleBlockUser(userId: String, name: String) {
        viewModelScope.launch {
            repository.toggleBlockUser(userId, name)
            _toastMessage.value = "Updated block list for $name."
        }
    }

    fun submitReport(targetType: String, targetName: String, reason: String) {
        viewModelScope.launch {
            repository.submitSafetyReport(targetType, targetName, reason)
            _toastMessage.value = "Report submitted to WEFUN Safety Queue."
        }
    }

    fun resolveReport(report: SafetyReport, status: String) {
        viewModelScope.launch {
            repository.resolveReport(report, status)
            _toastMessage.value = "Report marked $status."
        }
    }

    fun adminGrantCoins(amount: Long, reason: String) {
        viewModelScope.launch {
            repository.adminGrantCoins(amount, reason)
            _toastMessage.value = "Admin Ledger updated ($amount Coins)."
        }
    }

    fun markAllNotificationsRead() = repository.markAllNotificationsRead()

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
