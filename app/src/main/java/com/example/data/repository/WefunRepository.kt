package com.example.data.repository

import android.content.Context
import com.example.core.database.FirestoreManager
import com.example.core.database.FirestoreUserProfile
import com.example.core.database.FirestoreVoiceRoom
import com.example.core.security.AuthRepository
import com.example.data.local.DirectMessageEntity
import com.example.data.local.GameMatchEntity
import com.example.data.local.GuardRelationshipEntity
import com.example.data.local.OwnedSpecialEffectEntity
import com.example.data.local.ReceivedGiftEntity
import com.example.data.local.SafetyReportEntity
import com.example.data.local.SocialPostEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.local.WefunAppSettings
import com.example.data.local.WefunDatabase
import com.example.data.local.WefunPreferences
import com.example.data.model.AcceptedRelationship
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.AvatarConfig
import com.example.data.model.BiddingMode
import com.example.data.model.ChatThread
import com.example.data.model.CurrencyKind
import com.example.data.model.DailyTaskItem
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.DirectMessage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameId
import com.example.data.model.GameMatchRecord
import com.example.data.model.GameStatSummary
import com.example.data.model.GiftCategory
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.NotificationItem
import com.example.data.model.ProfileGiftSendResult
import com.example.data.model.ProfileStatistics
import com.example.data.model.ReceivedGift
import com.example.data.model.RechargePackage
import com.example.data.model.RelationshipRequest
import com.example.data.model.RelationshipRequestStatus
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.RoomRelationType
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SafetyReport
import com.example.data.model.SocialPost
import com.example.data.model.SpecialEffect
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.data.model.VipAuditLogEntry
import com.example.data.model.VipGiftItem
import com.example.data.model.VipTierConfig
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class WefunRepository(
    context: Context,
    private val firestoreManager: FirestoreManager? = null,
    private val authRepository: AuthRepository? = null,
) : WefunProfileApiContract {

    private val db = WefunDatabase.getInstance(context)
    private val prefs = WefunPreferences(context)

    private val userDao = db.userDao()
    private val roomDao = db.voiceRoomDao()
    private val txDao = db.transactionDao()
    private val postDao = db.socialPostDao()
    private val msgDao = db.messageDao()
    private val matchDao = db.gameMatchDao()
    private val reportDao = db.reportDao()
    private val receivedGiftDao = db.receivedGiftDao()
    private val guardDao = db.guardDao()
    private val specialEffectDao = db.specialEffectDao()
    private val sysDao = db.voiceRoomSystemDao()

    val roomBackend = VoiceRoomBackendEngine(roomDao, sysDao)
    val vipBackend = VipBackendDelegate(userDao, db.vipEcosystemDao(), txDao) { appendAuditLog(it) }

    val vipConfigsFlow: Flow<List<VipTierConfig>> = vipBackend.vipConfigsFlow
    val vipGiftsFlow: StateFlow<List<VipGiftItem>> = vipBackend.vipGiftsFlow
    val vipAuditLogsFlow: Flow<List<VipAuditLogEntry>> = vipBackend.vipAuditLogsFlow

    fun observeVipGrowthHistory(userId: String) = vipBackend.observeGrowthHistory(userId)
    fun observeVipPaymentHistory(userId: String) = vipBackend.observePaymentHistory(userId)

    val settingsFlow: Flow<WefunAppSettings> = prefs.settingsFlow

    val currentUserFlow: Flow<UserProfile?> = userDao.observeCurrentUser().map { it?.toDomain() }

    val roomsFlow: Flow<List<VoiceRoom>> = roomDao.observeRooms().map { list ->
        list.map { it.toDomain() }
    }

    val transactionsFlow: Flow<List<WalletTransaction>> = txDao.observeTransactions().map { list ->
        list.map { it.toDomain() }
    }

    val postsFlow: Flow<List<SocialPost>> = postDao.observePosts().map { list ->
        list.map { it.toDomain() }
    }

    val gameMatchesFlow: Flow<List<GameMatchRecord>> = matchDao.observeMatches().map { list ->
        list.map { it.toDomain() }
    }

    val reportsFlow: Flow<List<SafetyReport>> = reportDao.observeReports().map { list ->
        list.map { it.toDomain() }
    }

    private val _threads = MutableStateFlow(WefunSeedData.initialThreads)
    val threadsFlow = _threads.asStateFlow()

    private val _families = MutableStateFlow(WefunSeedData.families)
    val familiesFlow = _families.asStateFlow()

    private val _events = MutableStateFlow(WefunSeedData.events)
    val eventsFlow = _events.asStateFlow()

    private val _notifications = MutableStateFlow(WefunSeedData.notifications)
    val notificationsFlow = _notifications.asStateFlow()

    private val _followedUserIds = MutableStateFlow(setOf("host_rhea", "host_kai"))
    val followedUserIdsFlow = _followedUserIds.asStateFlow()

    private val _dailyTasks = MutableStateFlow(WefunSeedData.dailyTasks)
    val dailyTasksFlow = _dailyTasks.asStateFlow()

    private val _auditLogs = MutableStateFlow(
        listOf(
            "Admin initialized local verified ledger & safety rules",
            "Commission tier set to 15% for Nova Star Agency",
            "Festival Banner #4 published to Home Discovery",
        )
    )
    val auditLogsFlow = _auditLogs.asStateFlow()

    private val _packageInventory = MutableStateFlow(WefunSeedData.initialPackageInventory)
    val packageInventoryFlow = _packageInventory.asStateFlow()

    private val _relationshipRequests = MutableStateFlow<List<RelationshipRequest>>(emptyList())
    val relationshipRequestsFlow = _relationshipRequests.asStateFlow()

    private val _acceptedRelationships = MutableStateFlow(
        listOf(
            AcceptedRelationship(
                id = "rel_seed_1",
                partnerUserId = "host_rhea",
                partnerName = "DJ Rhea",
                tagLabel = "Confidant",
                mode = BiddingMode.BFF,
                tokenTitle = "Luxury Home Token",
                daysTogether = 42,
                level = 2,
                tokenKey = "puzzle",
            ),
            AcceptedRelationship(
                id = "rel_seed_2",
                partnerUserId = "host_kai",
                partnerName = "Kai Storm",
                tagLabel = "Bro",
                mode = BiddingMode.BFF,
                tokenTitle = "Bicycle Token",
                daysTogether = 27,
                level = 2,
                tokenKey = "bro",
            ),
        )
    )
    val acceptedRelationshipsFlow = _acceptedRelationships.asStateFlow()

    private val _customRelationshipTags = MutableStateFlow<List<RelationshipTagOption>>(emptyList())
    val customRelationshipTagsFlow = _customRelationshipTags.asStateFlow()

    private val _joinedRoomIds = MutableStateFlow(setOf("1579056", "320252"))
    val joinedRoomIdsFlow = _joinedRoomIds.asStateFlow()

    private val _publicProfileOverrides = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val publicProfileOverridesFlow = _publicProfileOverrides.asStateFlow()

    suspend fun getRoomByIdValidated(roomId: String): Result<VoiceRoom> {
        ensureSeeded()
        return roomBackend.getRoomByIdValidated(roomId).map { it.toDomain() }
    }

    fun domainRoomFromEntity(entity: VoiceRoomEntity): VoiceRoom = entity.toDomain()

    fun addCustomRelationshipTag(label: String, mode: BiddingMode): Result<RelationshipTagOption> {
        val existingLabels = (WefunSeedData.ordinaryRelationshipTags + WefunSeedData.bffRelationshipTags + _customRelationshipTags.value)
            .map { it.label }
        val validated = roomBackend.validateCustomTag(label, existingLabels).getOrElse {
            return Result.failure(it)
        }
        val option = RelationshipTagOption(
            id = "tag_custom_${UUID.randomUUID().toString().take(6)}",
            label = validated,
            mode = mode,
            iconKey = "star",
            isCustom = true,
        )
        _customRelationshipTags.value = _customRelationshipTags.value + option
        return Result.success(option)
    }

    suspend fun sendRelationshipRequest(
        roomId: String,
        targetUserId: String,
        targetName: String,
        tagLabel: String,
        mode: BiddingMode,
        tokenTitle: String,
        bidPoints: Long,
    ): Result<RelationshipRequest> {
        val sender = userDao.getCurrentUser() ?: WefunSeedData.defaultUser()
        val duplicate = _relationshipRequests.value.any {
            it.senderId == sender.uid &&
                it.targetUserId == targetUserId &&
                it.tagLabel.equals(tagLabel, ignoreCase = true) &&
                it.status == RelationshipRequestStatus.PENDING
        }
        if (duplicate) {
            return Result.failure(IllegalStateException("A pending '$tagLabel' request for $targetName already exists."))
        }
        val request = RelationshipRequest(
            id = "rreq_${UUID.randomUUID().toString().take(8)}",
            roomId = roomId,
            senderId = sender.uid,
            senderName = sender.nickname,
            targetUserId = targetUserId,
            targetUserName = targetName,
            tagLabel = tagLabel,
            mode = mode,
            tokenTitle = tokenTitle,
            bidPoints = bidPoints,
            status = RelationshipRequestStatus.PENDING,
        )
        _relationshipRequests.value = listOf(request) + _relationshipRequests.value
        appendAuditLog("Relationship request sent: $tagLabel -> $targetName ($tokenTitle)")
        return Result.success(request)
    }

    fun respondToRelationshipRequest(requestId: String, accept: Boolean): Result<RelationshipRequest> {
        val existing = _relationshipRequests.value.firstOrNull { it.id == requestId }
            ?: return Result.failure(NoSuchElementException("Relationship request not found."))
        if (existing.status != RelationshipRequestStatus.PENDING) {
            return Result.failure(IllegalStateException("Request has already been ${existing.status.label}."))
        }
        val nextStatus = if (accept) RelationshipRequestStatus.ACCEPTED else RelationshipRequestStatus.DECLINED
        val updated = existing.copy(status = nextStatus)
        _relationshipRequests.value = _relationshipRequests.value.map {
            if (it.id == requestId) updated else it
        }
        if (accept) {
            val relation = AcceptedRelationship(
                id = "rel_${UUID.randomUUID().toString().take(8)}",
                partnerUserId = existing.targetUserId,
                partnerName = existing.targetUserName,
                tagLabel = existing.tagLabel,
                mode = existing.mode,
                tokenTitle = existing.tokenTitle,
                daysTogether = 1,
            )
            _acceptedRelationships.value = listOf(relation) + _acceptedRelationships.value.filterNot {
                it.partnerUserId == existing.targetUserId && it.tagLabel.equals(existing.tagLabel, ignoreCase = true)
            }
            appendAuditLog("Relationship accepted: ${existing.tagLabel} with ${existing.targetUserName}")
        } else {
            appendAuditLog("Relationship declined: ${existing.tagLabel} with ${existing.targetUserName}")
        }
        return Result.success(updated)
    }

    fun findPublicProfileWithRelationships(userId: String, fallbackName: String? = null): UserProfile {
        val base = _publicProfileOverrides.value[userId] ?: WefunSeedData.findPublicProfile(userId, fallbackName)
        val related = _acceptedRelationships.value.filter { it.partnerUserId == base.uid }
        return base.copy(activeRelationships = related)
    }

    fun markRoomJoined(roomId: String) {
        _joinedRoomIds.value = _joinedRoomIds.value + roomId
    }

    override fun observeGiftWall(ownerUid: String): Flow<GiftWallSummary> =
        receivedGiftDao.observeByOwner(ownerUid).map { entities ->
            val domainList = entities.map { it.toDomain() }
            val totalGifts = domainList.sumOf { it.quantity }
            val totalStars = domainList.sumOf { it.totalStarsEarned }
            val featured = domainList.sortedByDescending { it.totalStarsEarned }.take(3)
            GiftWallSummary(
                ownerUid = ownerUid,
                totalGifts = totalGifts,
                totalStars = totalStars,
                featuredGifts = featured,
                allGifts = domainList,
            )
        }

    override fun observeAuthorizedGuards(ownerUid: String): Flow<List<GuardRelationship>> =
        guardDao.observeAuthorizedGuards(ownerUid).map { entities ->
            entities.mapNotNull { entity ->
                if (!entity.isAuthorized || entity.isPrivateHidden) null else entity.toDomain()
            }
        }

    override fun observeSpecialEffects(
        ownerUid: String,
        userCharmLevel: Int,
        userActiveLevel: Int,
    ): Flow<List<SpecialEffect>> =
        specialEffectDao.observeByOwner(ownerUid).map { rows ->
            val rowById = rows.associateBy { it.effectId }
            CharmLevelCatalog.defaultEffectsCatalog.map { template ->
                val saved = rowById[template.id]
                template.copy(
                    isOwned = saved?.isOwned ?: false,
                    isActive = (saved?.isOwned == true && saved.isActive),
                )
            }
        }

    override fun observeAdvancedRooms(ownerUid: String): Flow<List<AdvancedRoomPreview>> =
        combine(roomsFlow, _joinedRoomIds) { allRooms, joinedIds ->
            allRooms.mapNotNull { room ->
                val relation = when {
                    room.hostId == ownerUid -> RoomRelationType.OWNED
                    room.id in joinedIds -> RoomRelationType.JOINED
                    room.isFavorited && ownerUid == "usr_nova_01" -> RoomRelationType.FOLLOWED
                    else -> null
                } ?: return@mapNotNull null

                AdvancedRoomPreview(
                    roomId = room.id,
                    title = room.title,
                    hostId = room.hostId,
                    hostName = room.hostName,
                    category = room.category,
                    roomMode = room.roomMode,
                    roomLevel = room.roomLevel,
                    onlineCount = room.onlineCount,
                    popularity = room.popularity,
                    relationshipType = relation,
                    themePrimaryHex = room.themeStyle.primaryHex,
                    themeSecondaryHex = room.themeStyle.secondaryHex,
                )
            }
        }

    override fun observeProfileStatistics(profile: UserProfile): Flow<ProfileStatistics> =
        combine(
            gameMatchesFlow,
            postsFlow,
            observeGiftWall(profile.uid),
            observeAdvancedRooms(profile.uid),
        ) { matches, allPosts, giftWall, advRooms ->
            val supportedGames = WefunSeedData.wefunFeaturedGames
            val gameStats = supportedGames.map { game ->
                // Game matches are tracked for the local player; other public profiles only get real match rows if attributed
                val relevant = if (profile.uid == "usr_nova_01") {
                    matches.filter { it.gameTitle.equals(game.title, ignoreCase = true) }
                } else {
                    emptyList()
                }
                GameStatSummary(
                    gameId = game.id,
                    gameTitle = game.title,
                    matchesPlayed = relevant.size,
                    wins = relevant.count {
                        it.outcome.contains("Win", ignoreCase = true) ||
                            it.outcome.contains("1st", ignoreCase = true)
                    },
                    bestScore = relevant.maxOfOrNull { it.score } ?: 0,
                    totalCoinsWon = relevant.sumOf { it.coinsWon },
                    accentHex = game.accentHex,
                )
            }
            val momentCount = allPosts.count {
                (it.authorId == profile.uid || it.authorName.equals(profile.nickname, ignoreCase = true)) &&
                    (profile.uid == "usr_nova_01" || it.isPublic)
            }
            val charmProgress = CharmLevelCatalog.calculateCharmProgress(profile.charmPoints)
            ProfileStatistics(
                userId = profile.uid,
                accountLevel = profile.userLevel,
                xp = profile.xp,
                nextLevelXp = profile.nextLevelXp,
                charmPoints = profile.charmPoints,
                charmLevel = charmProgress.currentLevel,
                followers = profile.followersCount,
                following = profile.followingCount,
                friends = profile.friendsCount,
                giftsReceived = maxOf(profile.giftsReceivedCount, giftWall.totalGifts),
                giftsSent = profile.giftsSentCount,
                totalStars = giftWall.totalStars,
                totalMoments = momentCount,
                roomsJoined = advRooms.count { it.relationshipType == RoomRelationType.JOINED },
                roomsHosted = advRooms.count { it.relationshipType == RoomRelationType.OWNED },
                gameStats = gameStats,
            )
        }

    override suspend fun updateProfileWithOwnershipCheck(
        actorUid: String,
        targetUid: String,
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        coverPhotoUri: String?,
    ): Result<UserProfile> {
        if (actorUid.isBlank() || actorUid != targetUid) {
            return Result.failure(SecurityException("Unauthorized: only the profile owner can edit this profile."))
        }
        val cleanNick = nickname.trim()
        if (cleanNick.length !in 2..28) {
            return Result.failure(IllegalArgumentException("Display name must be between 2 and 28 characters."))
        }
        val cleanBio = bio.trim()
        if (cleanBio.length > 160) {
            return Result.failure(IllegalArgumentException("Signature cannot exceed 160 characters."))
        }
        val user = userDao.getCurrentUser()
            ?: return Result.failure(IllegalStateException("Current user profile not found."))
        if (user.uid != actorUid) {
            return Result.failure(SecurityException("Ownership mismatch against persisted user record."))
        }
        val updated = user.copy(
            nickname = cleanNick,
            bio = cleanBio,
            country = country.trim().ifBlank { user.country },
            language = language.trim().ifBlank { user.language },
            showGender = showGender,
            showAge = showAge,
            photoUri = photoUri ?: user.photoUri,
            coverPhotoUri = coverPhotoUri ?: user.coverPhotoUri,
        )
        userDao.upsertUser(updated)
        val domain = updated.toDomain()
        syncUserProfileToCloudIfAuthenticated(domain)
        return Result.success(domain)
    }

    override suspend fun updateUserSignature(
        actorUid: String,
        targetUid: String,
        rawSignature: String,
    ): Result<String> {
        if (actorUid.isBlank() || actorUid != targetUid) {
            return Result.failure(SecurityException("Only the account owner may update this signature."))
        }
        val trimmed = rawSignature.trim()
        if (trimmed.length > 120) {
            return Result.failure(IllegalArgumentException("Signature must be 120 characters or fewer."))
        }
        val user = userDao.getCurrentUser()
            ?: return Result.failure(IllegalStateException("Profile not available."))
        if (user.uid != actorUid) {
            return Result.failure(SecurityException("Unauthorized signature edit attempt."))
        }
        val updated = user.copy(bio = trimmed)
        userDao.upsertUser(updated)
        syncUserProfileToCloudIfAuthenticated(updated.toDomain())
        return Result.success(trimmed)
    }

    override suspend fun sendProfileGift(
        targetUser: UserProfile,
        gift: VirtualGift,
        quantity: Int,
        fromPackage: Boolean,
        referenceId: String,
    ): Result<ProfileGiftSendResult> {
        val qty = quantity.coerceAtLeast(1)
        val spendRes = consumePackageOrSpendCoinsForGift(
            gift = gift,
            comboCount = qty,
            receiverCount = 1,
            receiverLabel = targetUser.nickname,
            fromPackage = fromPackage,
            referenceId = referenceId,
        )
        val totalCoins = spendRes.getOrElse { return Result.failure(it) }

        val starsPerUnit = (gift.coinPrice / 20L).coerceAtLeast(1L)
        val starsAdded = starsPerUnit * qty
        val charmAdded = totalCoins

        creditReceivedGiftLedger(
            recipientUid = targetUser.uid,
            gift = gift,
            quantity = qty,
            starsPerUnit = starsPerUnit,
            charmAdded = charmAdded,
        )
        appendAuditLog("Verified Gift Wall delivery: ${gift.name} x$qty -> ${targetUser.nickname} (+$starsAdded Stars)")
        return Result.success(
            ProfileGiftSendResult(
                gift = gift,
                quantity = qty,
                totalCoins = totalCoins,
                starsAdded = starsAdded,
                charmAdded = charmAdded,
            )
        )
    }

    suspend fun creditReceivedGiftLedger(
        recipientUid: String,
        gift: VirtualGift,
        quantity: Int,
        starsPerUnit: Long = (gift.coinPrice / 20L).coerceAtLeast(1L),
        charmAdded: Long = gift.coinPrice * quantity,
    ) {
        val now = System.currentTimeMillis()
        val existing = receivedGiftDao.getByOwnerAndGift(recipientUid, gift.id)
        val nextEntity = if (existing == null) {
            ReceivedGiftEntity(
                id = "${recipientUid}_${gift.id}",
                ownerUid = recipientUid,
                giftId = gift.id,
                giftName = gift.name,
                emoji = gift.emoji,
                coinValue = gift.coinPrice,
                starsPerUnit = starsPerUnit,
                quantity = quantity,
                categoryName = gift.category.name,
                accentHex = gift.accentHex,
                lastReceivedAt = now,
            )
        } else {
            existing.copy(
                quantity = existing.quantity + quantity,
                lastReceivedAt = now,
            )
        }
        receivedGiftDao.upsert(nextEntity)

        val currentOwn = userDao.getCurrentUser()
        if (currentOwn != null && currentOwn.uid == recipientUid) {
            userDao.upsertUser(
                currentOwn.copy(
                    giftsReceivedCount = currentOwn.giftsReceivedCount + quantity,
                    charmPoints = currentOwn.charmPoints + charmAdded,
                )
            )
        } else {
            val basePublic = _publicProfileOverrides.value[recipientUid]
                ?: WefunSeedData.findPublicProfile(recipientUid)
            _publicProfileOverrides.value = _publicProfileOverrides.value + (
                recipientUid to basePublic.copy(
                    giftsReceivedCount = basePublic.giftsReceivedCount + quantity,
                    charmPoints = basePublic.charmPoints + charmAdded,
                )
            )
        }
    }

    override fun cancelAcceptedRelationship(
        actorUid: String,
        relationshipId: String,
    ): Result<AcceptedRelationship> {
        val target = _acceptedRelationships.value.firstOrNull { it.id == relationshipId }
            ?: return Result.failure(NoSuchElementException("Accepted relationship not found."))
        val isParticipant = actorUid == "usr_nova_01" || actorUid == target.partnerUserId
        if (!isParticipant) {
            return Result.failure(SecurityException("Only relationship participants can cancel this bond."))
        }
        _acceptedRelationships.value = _acceptedRelationships.value.filterNot { it.id == relationshipId }
        appendAuditLog("Relationship ended: ${target.tagLabel} with ${target.partnerName}")
        return Result.success(target)
    }

    override suspend fun toggleSpecialEffectActivation(
        actorUid: String,
        effectId: String,
    ): Result<SpecialEffect> {
        val template = CharmLevelCatalog.defaultEffectsCatalog.firstOrNull { it.id == effectId }
            ?: return Result.failure(IllegalArgumentException("Special effect '$effectId' does not exist."))
        val user = userDao.getCurrentUser()
            ?: return Result.failure(IllegalStateException("User not loaded."))
        if (user.uid != actorUid) {
            return Result.failure(SecurityException("Only the owner can toggle special effects."))
        }
        val saved = specialEffectDao.getByOwnerAndEffect(actorUid, effectId)
        if (saved == null || !saved.isOwned) {
            return Result.failure(IllegalStateException("${template.title} is locked. Unlock or purchase it first."))
        }
        val charmLevel = CharmLevelCatalog.calculateCharmProgress(user.charmPoints).currentLevel
        if (charmLevel < template.requiredCharmLevel) {
            return Result.failure(
                IllegalStateException("Requires Charm Level ${template.requiredCharmLevel} (${template.title}).")
            )
        }
        val nextActive = !saved.isActive
        specialEffectDao.upsert(saved.copy(isActive = nextActive))
        return Result.success(template.copy(isOwned = true, isActive = nextActive))
    }

    suspend fun ensureSeeded() {
        if (userDao.getCurrentUser() == null) {
            userDao.upsertUser(WefunSeedData.defaultUser())
        }
        if (roomDao.countRooms() == 0) {
            roomDao.upsertRooms(WefunSeedData.initialRooms)
        }
        roomBackend.ensureRoomSystemSeeded(WefunSeedData.initialRooms)
        if (txDao.countTransactions() == 0) {
            WefunSeedData.initialTransactions.forEach { txDao.insertTransaction(it) }
        }
        if (postDao.countPosts() == 0) {
            postDao.upsertPosts(WefunSeedData.initialPosts)
        }
        if (msgDao.countMessages() == 0) {
            msgDao.upsertMessages(WefunSeedData.initialMessages)
        }
        if (matchDao.countMatches() == 0) {
            WefunSeedData.initialMatches.forEach { matchDao.insertMatch(it) }
        }
        if (reportDao.countReports() == 0) {
            WefunSeedData.initialReports.forEach { reportDao.upsertReport(it) }
        }
        if (receivedGiftDao.countAll() == 0) {
            receivedGiftDao.upsertAll(CharmLevelCatalog.seedReceivedGifts())
        }
        if (guardDao.countAll() == 0) {
            guardDao.upsertAll(CharmLevelCatalog.seedGuards())
        }
        if (specialEffectDao.countAll() == 0) {
            specialEffectDao.upsertAll(CharmLevelCatalog.seedOwnedEffects())
        }
        vipBackend.ensureSeeded()
    }

    suspend fun completeSignIn(
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
        ensureSeeded()
        val existing = userDao.getCurrentUser() ?: WefunSeedData.defaultUser()
        val updated = existing.copy(
            nickname = nickname.ifBlank { existing.nickname },
            username = username.ifBlank { existing.username },
            phone = phone.ifBlank { existing.phone },
            email = email.ifBlank { existing.email },
            dob = dob.ifBlank { existing.dob },
            gender = gender.ifBlank { existing.gender },
            country = country.ifBlank { existing.country },
            language = language.ifBlank { existing.language },
            photoUri = photoUri ?: existing.photoUri,
        )
        userDao.upsertUser(updated)
        prefs.setLoggedIn(loggedIn = true, onboardingDone = true)
        syncUserProfileToCloudIfAuthenticated(updated.toDomain())
    }

    suspend fun syncUserProfileToCloudIfAuthenticated(user: UserProfile? = null) {
        val uid = authRepository?.currentUserId() ?: return
        val manager = firestoreManager ?: return
        val profile = user ?: userDao.getCurrentUser()?.toDomain() ?: return
        manager.createOrUpdateUserProfile(
            FirestoreUserProfile(
                userId = uid,
                wefunId = profile.wefunId,
                username = profile.username,
                nickname = profile.nickname,
                email = profile.email,
                phone = profile.phone,
                bio = profile.bio,
                country = profile.country,
                language = profile.language,
                avatarUrl = profile.photoUri ?: "",
                coins = profile.coins,
                diamonds = profile.diamonds,
                vipLevel = profile.vipLevel.toLong(),
                userLevel = profile.userLevel.toLong(),
            )
        )
    }

    suspend fun syncRoomToCloudIfAuthenticated(room: VoiceRoom) {
        val uid = authRepository?.currentUserId() ?: return
        val manager = firestoreManager ?: return
        manager.createVoiceRoom(
            FirestoreVoiceRoom(
                id = room.id,
                ownerId = uid,
                memberUids = listOf(uid),
                title = room.title,
                description = room.description,
                category = room.category.label,
                hostName = room.hostName,
                roomLevel = room.roomLevel.toLong().coerceIn(1L, 10L),
                maxSeats = if (room.roomLevel >= 7) room.maxSeats.toLong().coerceIn(8L, 14L) else 8L,
                isPartnerSeatEnabled = room.isPartnerSeatEnabled,
                backdropTheme = room.backdropTheme.name,
                announcement = room.announcement,
                isPasswordLocked = room.password != null,
                onlineCount = room.onlineCount.toLong(),
                popularity = room.popularity,
            )
        )
    }

    suspend fun syncPartnerSeatToCloudIfAuthenticated(roomId: String, enabled: Boolean) {
        if (authRepository?.currentUserId() == null) return
        firestoreManager?.updatePartnerSeatConfig(roomId, enabled)
    }

    suspend fun syncRoomCapacityToCloudIfAuthenticated(roomId: String, roomLevel: Int, maxSeats: Int) {
        if (authRepository?.currentUserId() == null) return
        firestoreManager?.updateRoomSeatCapacity(roomId, roomLevel.toLong(), maxSeats.toLong())
    }

    suspend fun signOut() {
        prefs.setLoggedIn(loggedIn = false, onboardingDone = true)
    }

    suspend fun deleteAccountAndReset() {
        userDao.clearUsers()
        prefs.clearAll()
        ensureSeeded()
    }

    suspend fun setDarkMode(enabled: Boolean) {
        prefs.setDarkMode(enabled)
    }

    suspend fun setParentalControl(enabled: Boolean) {
        prefs.setParentalControl(enabled)
        appendAuditLog("Parental Control ${if (enabled) "enabled" else "disabled"}")
    }

    suspend fun updateLanguage(language: String) {
        val user = userDao.getCurrentUser() ?: return
        userDao.upsertUser(user.copy(language = language))
    }

    suspend fun updateNotificationPrefs(gifts: Boolean, rooms: Boolean, pk: Boolean, messages: Boolean) {
        prefs.updateNotificationPrefs(gifts, rooms, pk, messages)
    }

    suspend fun toggleBlockUser(userId: String, displayName: String) {
        prefs.toggleBlockUser(userId)
        appendAuditLog("User block status toggled for $displayName ($userId)")
    }

    suspend fun claimDailyCheckIn(): Result<Long> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        if (user.isCheckedInToday) {
            return Result.failure(IllegalStateException("Already checked in today!"))
        }
        val nextDay = (user.lastCheckInDay % 7) + 1
        val rewardCoins = (100L * nextDay) + if (user.vipLevel > 0) user.vipLevel * 25L else 0L
        val xpGain = 150L
        val updated = applyXpGain(
            user.copy(
                coins = user.coins + rewardCoins,
                bonusPoints = user.bonusPoints + 40,
                lastCheckInDay = nextDay,
                isCheckedInToday = true
            ),
            xpGain
        )
        userDao.upsertUser(updated)
        recordTransaction(
            type = TransactionType.DAILY_CHECKIN,
            currency = CurrencyKind.COINS,
            amount = rewardCoins,
            isCredit = true,
            description = "Day $nextDay Check-In Reward (+${xpGain} XP)",
            referenceId = "checkin_${System.currentTimeMillis()}"
        )
        return Result.success(rewardCoins)
    }

    suspend fun rechargeCoins(pkg: RechargePackage, referenceId: String): Result<Long> {
        if (txDao.existsByReference(referenceId)) {
            return Result.failure(IllegalStateException("Duplicate transaction blocked"))
        }
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val total = pkg.coins + pkg.bonusCoins
        userDao.upsertUser(user.copy(coins = user.coins + total))
        recordTransaction(
            type = TransactionType.RECHARGE,
            currency = CurrencyKind.COINS,
            amount = total,
            isCredit = true,
            description = "Sandbox Recharge: ${pkg.coins} + ${pkg.bonusCoins} Bonus (${pkg.priceDisplay})",
            referenceId = referenceId
        )
        appendAuditLog("Verified coin recharge +$total Coins (${pkg.id})")
        return Result.success(total)
    }

    suspend fun spendCoinsForGift(
        gift: VirtualGift,
        comboCount: Int,
        receiverCount: Int,
        receiverLabel: String,
    ): Result<Long> = consumePackageOrSpendCoinsForGift(
        gift = gift,
        comboCount = comboCount,
        receiverCount = receiverCount,
        receiverLabel = receiverLabel,
        fromPackage = false,
        referenceId = "gift_${UUID.randomUUID()}",
    )

    suspend fun consumePackageOrSpendCoinsForGift(
        gift: VirtualGift,
        comboCount: Int,
        receiverCount: Int,
        receiverLabel: String,
        fromPackage: Boolean,
        referenceId: String = "gift_${UUID.randomUUID()}",
    ): Result<Long> {
        if (txDao.existsByReference(referenceId)) {
            return Result.failure(IllegalStateException("Duplicate gift request blocked."))
        }
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val totalItems = (comboCount * receiverCount).coerceAtLeast(1)
        val totalValue = gift.coinPrice * totalItems
        if (totalValue <= 0) return Result.failure(IllegalArgumentException("Invalid gift amount"))

        if (fromPackage) {
            val ownedCount = _packageInventory.value[gift.id] ?: 0
            if (ownedCount < totalItems) {
                return Result.failure(IllegalStateException("Only $ownedCount x ${gift.name} available in Package."))
            }
            val nextInv = _packageInventory.value.toMutableMap()
            val remaining = ownedCount - totalItems
            if (remaining <= 0) nextInv.remove(gift.id) else nextInv[gift.id] = remaining
            _packageInventory.value = nextInv

            val xpEarned = (totalValue / 5L).coerceAtLeast(10L)
            val updated = applyXpGain(
                user.copy(
                    giftsSentCount = user.giftsSentCount + totalItems,
                    bonusPoints = user.bonusPoints + (comboCount * 2L),
                ),
                xpEarned
            )
            userDao.upsertUser(updated)
            recordTransaction(
                type = TransactionType.GIFT_SENT,
                currency = CurrencyKind.COINS,
                amount = totalValue,
                isCredit = false,
                description = "Package Gift: ${gift.name} x$totalItems to $receiverLabel",
                referenceId = referenceId,
            )
            return Result.success(totalValue)
        }

        if (user.coins < totalValue) {
            return Result.failure(IllegalStateException("Not enough Coins! Need $totalValue Coins."))
        }
        val xpEarned = (totalValue / 5L).coerceAtLeast(10L)
        val updated = applyXpGain(
            user.copy(
                coins = user.coins - totalValue,
                giftsSentCount = user.giftsSentCount + totalItems,
                bonusPoints = user.bonusPoints + (comboCount * 2L),
            ),
            xpEarned
        )
        userDao.upsertUser(updated)
        recordTransaction(
            type = TransactionType.GIFT_SENT,
            currency = CurrencyKind.COINS,
            amount = totalValue,
            isCredit = false,
            description = "Sent ${gift.name} x$totalItems to $receiverLabel",
            referenceId = referenceId,
        )
        return Result.success(totalValue)
    }

    suspend fun purchaseVipTier(level: Int, costCoins: Long, tierName: String): Result<Unit> {
        return vipBackend.purchaseVipSubscription(level = level, months = 1).map { Unit }
    }

    suspend fun purchaseVipSubscription(
        level: Int,
        months: Int = 1,
        autoRenew: Boolean? = null,
    ): Result<String> = vipBackend.purchaseVipSubscription(level, months, autoRenew)

    suspend fun restoreVipPurchases(): Result<Int> = vipBackend.restoreVipPurchases()

    suspend fun spendDiamondsForVipGrowth(
        category: DiamondSpendCategory,
        diamondsAmount: Long,
        description: String,
        referenceId: String = "dia_${UUID.randomUUID()}",
    ): Result<DiamondSpendOutcome> = vipBackend.spendDiamondsForVipGrowth(
        category = category,
        diamondsAmount = diamondsAmount,
        description = description,
        referenceId = referenceId,
    )

    suspend fun claimDailyVipDiamonds(): Result<Long> = vipBackend.claimDailyVipDiamonds()

    suspend fun toggleVipAutoRenewal(enabled: Boolean): Result<Boolean> =
        vipBackend.toggleAutoRenewal(enabled)

    suspend fun evaluateVipRegulation(simulatedAdvanceDays: Int = 0): Result<RegulationEvaluationResult> =
        vipBackend.evaluateRegulation(simulatedAdvanceDays)

    suspend fun updateVipPrivacyAndCustomization(
        customTitle: String,
        hideRecentGifts: Boolean,
        hideGuardianBoard: Boolean,
        privateProfileVisit: Boolean,
        friendRequestProtection: Boolean,
        customNamecardTheme: String,
    ): Result<Unit> = vipBackend.updateVipPrivacyAndCustomization(
        customTitle = customTitle,
        hideRecentGifts = hideRecentGifts,
        hideGuardianBoard = hideGuardianBoard,
        privateProfileVisit = privateProfileVisit,
        friendRequestProtection = friendRequestProtection,
        customNamecardTheme = customNamecardTheme,
    )

    suspend fun sendVipExclusiveGift(
        gift: VipGiftItem,
        recipientUid: String,
        recipientName: String,
    ): Result<DiamondSpendOutcome> {
        val user = userDao.getCurrentUser()
            ?: return Result.failure(IllegalStateException("User profile not loaded."))
        val active = VipGrowthEngine.isVipActive(user.vipExpiryAtMillis, System.currentTimeMillis()) && user.vipLevel > 0
        if (!active || user.vipLevel < gift.requiredVipLevel) {
            return Result.failure(
                IllegalStateException("Requires active VIP ${gift.requiredVipLevel}+ to send ${gift.name}.")
            )
        }
        val outcome = vipBackend.spendDiamondsForVipGrowth(
            category = DiamondSpendCategory.GIFT,
            diamondsAmount = gift.diamondCost,
            description = "${gift.tierGroup}: ${gift.name} -> $recipientName",
        ).getOrElse { return Result.failure(it) }

        val virtualEquivalent = VirtualGift(
            id = gift.id,
            name = gift.name,
            category = GiftCategory.VIP,
            coinPrice = gift.coinCost,
            diamondValue = gift.diamondCost,
            emoji = gift.emoji,
            accentHex = gift.accentHex,
        )
        creditReceivedGiftLedger(
            recipientUid = recipientUid,
            gift = virtualEquivalent,
            quantity = 1,
            starsPerUnit = (gift.coinCost / 20L).coerceAtLeast(1L),
            charmAdded = gift.diamondCost * 2L,
        )
        return Result.success(outcome)
    }

    suspend fun adminUpdateVipConfig(config: VipTierConfig): Result<Unit> =
        vipBackend.adminUpdateVipConfig(config)

    suspend fun adminGrantOrUpdateUserVip(level: Int, months: Int, active: Boolean): Result<Unit> =
        vipBackend.adminGrantOrUpdateUserVip(level, months, active)

    suspend fun adminAdjustGrowthPoints(delta: Long, reason: String): Result<Unit> =
        vipBackend.adminAdjustGrowthPoints(delta, reason)

    fun adminSaveVipGift(gift: VipGiftItem) = vipBackend.adminSaveVipGift(gift)

    suspend fun updateProfileAndAvatar(
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        avatarConfig: AvatarConfig,
    ) {
        val user = userDao.getCurrentUser() ?: return
        userDao.upsertUser(
            user.copy(
                nickname = nickname.ifBlank { user.nickname },
                bio = bio,
                country = country,
                language = language,
                showGender = showGender,
                showAge = showAge,
                photoUri = photoUri ?: user.photoUri,
                faceStyle = avatarConfig.faceStyle,
                hairStyle = avatarConfig.hairStyle,
                outfitStyle = avatarConfig.outfitStyle,
                accessory = avatarConfig.accessory,
                backgroundAura = avatarConfig.backgroundAura,
                frameName = avatarConfig.frameName,
            )
        )
    }

    suspend fun createVoiceRoom(
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
        roomMode: RoomMode = RoomMode.NORMAL,
        backdropTheme: VoiceRoomBackdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE,
    ): VoiceRoom {
        val user = userDao.getCurrentUser() ?: WefunSeedData.defaultUser()
        val id = "${(1000000..9999999).random()}"
        val resolvedBackdrop = if (roomMode == RoomMode.AUCTION) {
            VoiceRoomBackdropTheme.AUCTION_ROYAL
        } else {
            backdropTheme
        }
        val tags = listOfNotNull(
            category.label,
            if (roomMode == RoomMode.AUCTION) "Auction" else null,
            "Live"
        ).distinct().joinToString(",")
        val entity = VoiceRoomEntity(
            id = id,
            title = title.trim().ifBlank { "${user.nickname}'s Voice Party" },
            description = description.trim().ifBlank {
                if (roomMode == RoomMode.AUCTION) "Live WeFun relationship auction & party stage!"
                else "Live WeFun voice hangout & party games!"
            },
            categoryName = category.name,
            password = password?.trim()?.takeIf { it.isNotBlank() },
            hostId = user.uid,
            hostName = user.nickname,
            hostVip = user.vipLevel,
            maxSeats = if (maxSeats >= 14) 8 else maxSeats.coerceIn(8, 8),
            isPartnerSeatEnabled = false,
            roomLevel = 1,
            totalContribution = 0L,
            isFavorited = false,
            createdAtMillis = System.currentTimeMillis(),
            decorationStyle = "Charcoal Gold Standard",
            musicAdminsOnly = false,
            themeName = theme.name,
            announcement = "Welcome! Tap an open golden mic seat to join the conversation.",
            popularity = 5000,
            onlineCount = 1,
            language = user.language,
            familyName = "Celestial House",
            roomModeName = roomMode.name,
            tagsCsv = tags,
            isRecentlyActive = true,
            isNewRoom = true,
            backdropThemeName = resolvedBackdrop.name,
        )
        roomDao.upsertRoom(entity)
        roomBackend.ensureSingleRoomSeeded(entity, hostUserLevel = user.userLevel)
        markRoomJoined(id)
        appendAuditLog("Created Level 1 ${roomMode.label} '${entity.title}' (ID: $id, 8 seats)")
        val domainRoom = entity.toDomain()
        syncRoomToCloudIfAuthenticated(domainRoom)
        return domainRoom
    }

    suspend fun updateRoomBackdrop(roomId: String, backdropTheme: VoiceRoomBackdropTheme) {
        roomBackend.updateRoomBackdrop(roomId, backdropTheme)
    }

    suspend fun createSocialPost(content: String, hashtag: String, mediaType: String, imageUri: String?) {
        val user = userDao.getCurrentUser() ?: return
        val post = SocialPostEntity(
            id = "post_${UUID.randomUUID()}",
            authorId = user.uid,
            authorName = user.nickname,
            authorVip = user.vipLevel,
            content = content,
            hashtag = if (hashtag.startsWith("#")) hashtag else "#${hashtag.ifBlank { "WEFUN" }}",
            mediaType = mediaType,
            imageUri = imageUri,
            likesCount = 1,
            commentsCount = 0,
            isLiked = true,
            timestamp = System.currentTimeMillis(),
        )
        postDao.upsertPost(post)
        val updated = applyXpGain(user, 45L)
        userDao.upsertUser(updated)
    }

    suspend fun toggleLikePost(post: SocialPost) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        postDao.upsertPost(
            SocialPostEntity(
                id = post.id,
                authorId = post.authorId,
                authorName = post.authorName,
                authorVip = post.authorVip,
                content = post.content,
                hashtag = post.hashtag,
                mediaType = post.mediaType,
                imageUri = post.imageUri,
                likesCount = newCount,
                commentsCount = post.commentsCount,
                isLiked = newLiked,
                timestamp = post.timestamp,
            )
        )
    }

    suspend fun addCommentToPost(post: SocialPost) {
        postDao.upsertPost(
            SocialPostEntity(
                id = post.id,
                authorId = post.authorId,
                authorName = post.authorName,
                authorVip = post.authorVip,
                content = post.content,
                hashtag = post.hashtag,
                mediaType = post.mediaType,
                imageUri = post.imageUri,
                likesCount = post.likesCount,
                commentsCount = post.commentsCount + 1,
                isLiked = post.isLiked,
                timestamp = post.timestamp,
            )
        )
    }

    fun observeThreadMessages(threadId: String): Flow<List<DirectMessage>> =
        msgDao.observeMessagesForThread(threadId).map { list -> list.map { it.toDomain() } }

    suspend fun sendDirectMessage(
        threadId: String,
        text: String,
        giftName: String? = null,
        voiceSeconds: Int? = null,
    ) {
        val user = userDao.getCurrentUser() ?: return
        val msg = DirectMessageEntity(
            id = "dm_${UUID.randomUUID()}",
            threadId = threadId,
            senderId = user.uid,
            senderName = user.nickname,
            text = text,
            giftName = giftName,
            voiceSeconds = voiceSeconds,
            reaction = null,
            isRead = true,
            timestamp = System.currentTimeMillis(),
        )
        msgDao.insertMessage(msg)
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(lastMessagePreview = text, unreadCount = 0, isRequest = false)
            else th
        }
    }

    suspend fun deleteDirectMessage(id: String) {
        msgDao.deleteMessage(id)
    }

    suspend fun reactToDirectMessage(message: DirectMessage, emoji: String) {
        msgDao.insertMessage(
            DirectMessageEntity(
                id = message.id,
                threadId = message.threadId,
                senderId = message.senderId,
                senderName = message.senderName,
                text = message.text,
                giftName = message.giftName,
                voiceSeconds = message.voiceSeconds,
                reaction = emoji,
                isRead = message.isRead,
                timestamp = message.timestamp,
            )
        )
    }

    fun toggleMuteThread(threadId: String) {
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(isMuted = !th.isMuted) else th
        }
    }

    fun markAllThreadsRead() {
        _threads.value = _threads.value.map { it.copy(unreadCount = 0) }
    }

    fun acceptFriendRequest(threadId: String) {
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(isRequest = false, unreadCount = 0) else th
        }
    }

    fun declineFriendRequest(threadId: String) {
        _threads.value = _threads.value.filterNot { it.id == threadId }
    }

    fun startOrOpenChat(peerId: String, peerName: String, isGroup: Boolean = false): String {
        val existing = _threads.value.firstOrNull { it.peerId == peerId || it.peerName.equals(peerName, ignoreCase = true) }
        if (existing != null) return existing.id
        val newId = "th_${UUID.randomUUID().toString().take(6)}"
        val initials = peerName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").ifBlank { "WF" }
        val newThread = ChatThread(
            id = newId,
            peerId = peerId,
            peerName = peerName,
            peerInitials = initials,
            accentHex = 0xFF00C2B8,
            isGroup = isGroup,
            isRequest = false,
            isOnline = true,
            isMuted = false,
            vipLevel = 2,
            timeLabel = "Just now",
            lastSeenLabel = "Online",
            unreadCount = 0,
            lastMessagePreview = "Say hello on WEFUN!"
        )
        _threads.value = listOf(newThread) + _threads.value
        return newId
    }

    suspend fun claimDailyTask(taskId: String): Result<DailyTaskItem> {
        val task = _dailyTasks.value.firstOrNull { it.id == taskId }
            ?: return Result.failure(IllegalArgumentException("Task not found"))
        if (task.isClaimed) return Result.failure(IllegalStateException("Task already claimed"))
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val updatedUser = applyXpGain(user.copy(coins = user.coins + task.rewardCoins), task.rewardXp)
        userDao.upsertUser(updatedUser)
        recordTransaction(
            type = TransactionType.GAME_REWARD,
            currency = CurrencyKind.COINS,
            amount = task.rewardCoins,
            isCredit = true,
            description = "Task Complete: ${task.title}",
            referenceId = "task_${task.id}_${System.currentTimeMillis()}"
        )
        _dailyTasks.value = _dailyTasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = true, isClaimed = true) else it
        }
        return Result.success(task)
    }

    suspend fun recordGameResult(gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) {
        val match = GameMatchEntity(
            id = "gm_${UUID.randomUUID()}",
            gameTitle = gameTitle,
            outcome = outcome,
            score = score,
            coinsWon = coinsWon,
            xpEarned = xpEarned,
            timestamp = System.currentTimeMillis(),
        )
        matchDao.insertMatch(match)
        val user = userDao.getCurrentUser() ?: return
        val updated = applyXpGain(user.copy(coins = user.coins + coinsWon), xpEarned)
        userDao.upsertUser(updated)
        if (coinsWon > 0) {
            recordTransaction(
                type = TransactionType.GAME_REWARD,
                currency = CurrencyKind.COINS,
                amount = coinsWon,
                isCredit = true,
                description = "$gameTitle • $outcome (+$xpEarned XP)",
                referenceId = "game_${UUID.randomUUID()}"
            )
        }
    }

    suspend fun submitSafetyReport(targetType: String, targetName: String, reason: String) {
        val report = SafetyReportEntity(
            id = "rep_${UUID.randomUUID()}",
            targetType = targetType,
            targetName = targetName,
            reason = reason,
            status = "Pending",
            timestamp = System.currentTimeMillis(),
        )
        reportDao.upsertReport(report)
        appendAuditLog("Safety report filed: [$targetType] $targetName — $reason")
    }

    suspend fun resolveReport(report: SafetyReport, newStatus: String) {
        reportDao.upsertReport(
            SafetyReportEntity(
                id = report.id,
                targetType = report.targetType,
                targetName = report.targetName,
                reason = report.reason,
                status = newStatus,
                timestamp = report.timestamp,
            )
        )
        appendAuditLog("Moderator updated report ${report.id} -> $newStatus")
    }

    fun toggleFamilyMembership(familyId: String) {
        _families.value = _families.value.map { fam ->
            if (fam.id == familyId) {
                val joined = !fam.isJoined
                fam.copy(
                    isJoined = joined,
                    memberCount = if (joined) fam.memberCount + 1 else (fam.memberCount - 1).coerceAtLeast(1)
                )
            } else fam
        }
    }

    fun createFamily(name: String, intro: String, badgeEmoji: String) {
        val newFam = FamilyInfo(
            id = "fam_${UUID.randomUUID().toString().take(6)}",
            name = name.ifBlank { "Nova Legends" },
            badgeEmoji = badgeEmoji.ifBlank { "⚡" },
            intro = intro.ifBlank { "New WEFUN community family!" },
            leaderName = "You",
            memberCount = 1,
            level = 1,
            contributionPoints = 500,
            rank = _families.value.size + 1,
            isJoined = true,
        )
        _families.value = listOf(newFam) + _families.value
    }

    fun toggleEventRegistration(eventId: String) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                val reg = !ev.isRegistered
                ev.copy(
                    isRegistered = reg,
                    participantCount = if (reg) ev.participantCount + 1 else (ev.participantCount - 1).coerceAtLeast(0)
                )
            } else ev
        }
    }

    fun toggleFollowUser(userId: String) {
        val current = _followedUserIds.value
        _followedUserIds.value = if (userId in current) current - userId else current + userId
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun pushNotification(category: String, title: String, body: String) {
        val item = NotificationItem(
            id = "n_${UUID.randomUUID()}",
            category = category,
            title = title,
            body = body,
            timeLabel = "Just now",
            isRead = false
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    suspend fun adminGrantCoins(amount: Long, reason: String): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        userDao.upsertUser(user.copy(coins = (user.coins + amount).coerceAtLeast(0)))
        recordTransaction(
            type = TransactionType.REFUND,
            currency = CurrencyKind.COINS,
            amount = kotlin.math.abs(amount),
            isCredit = amount >= 0,
            description = "Admin Adjustment: $reason",
            referenceId = "admin_${UUID.randomUUID()}"
        )
        appendAuditLog("Admin adjusted balance by $amount Coins ($reason)")
        return Result.success(Unit)
    }

    fun appendAuditLog(entry: String) {
        _auditLogs.value = (listOf(entry) + _auditLogs.value).take(25)
    }

    private suspend fun recordTransaction(
        type: TransactionType,
        currency: CurrencyKind,
        amount: Long,
        isCredit: Boolean,
        description: String,
        referenceId: String,
    ) {
        txDao.insertTransaction(
            WalletTransactionEntity(
                id = "tx_${UUID.randomUUID()}",
                typeName = type.name,
                currencyName = currency.name,
                amount = amount,
                isCredit = isCredit,
                description = description,
                referenceId = referenceId,
                timestamp = System.currentTimeMillis(),
            )
        )
    }

    private fun applyXpGain(user: UserProfileEntity, xpGain: Long): UserProfileEntity {
        var newXp = user.xp + xpGain
        var level = user.userLevel
        var threshold = user.nextLevelXp
        while (newXp >= threshold) {
            newXp -= threshold
            level += 1
            threshold = (threshold * 1.25).toLong()
        }
        return user.copy(xp = newXp, userLevel = level, nextLevelXp = threshold)
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        uid = uid,
        wefunId = wefunId,
        username = username,
        nickname = nickname,
        bio = bio,
        phone = phone,
        email = email,
        dob = dob,
        isAgeVerified = isAgeVerified,
        gender = gender,
        showGender = showGender,
        showAge = showAge,
        country = country,
        language = language,
        photoUri = photoUri,
        coverPhotoUri = coverPhotoUri,
        accentColorHex = accentColorHex,
        avatarConfig = AvatarConfig(faceStyle, hairStyle, outfitStyle, accessory, backgroundAura, frameName),
        vipLevel = vipLevel,
        userLevel = userLevel,
        charmPoints = charmPoints,
        activeProfileFrameId = activeProfileFrameId,
        activeEntryEffectId = activeEntryEffectId,
        activeVisualEffectId = activeVisualEffectId,
        vipGrowthPoints = vipGrowthPoints,
        vipActivatedAtMillis = vipActivatedAtMillis,
        vipExpiryAtMillis = vipExpiryAtMillis,
        vipAutoRenewal = vipAutoRenewal,
        vipLastDailyClaimEpochDay = vipLastDailyClaimEpochDay,
        vipRegulationDeadlineMillis = vipRegulationDeadlineMillis,
        vipRegulationPointsEarned = vipRegulationPointsEarned,
        customTitle = customTitle,
        hideRecentGifts = hideRecentGifts,
        hideGuardianBoard = hideGuardianBoard,
        privateProfileVisit = privateProfileVisit,
        friendRequestProtection = friendRequestProtection,
        activeVipFrameId = activeVipFrameId,
        activeVipEntranceId = activeVipEntranceId,
        customNamecardTheme = customNamecardTheme,
        xp = xp,
        nextLevelXp = nextLevelXp,
        coins = coins,
        diamonds = diamonds,
        bonusPoints = bonusPoints,
        followersCount = followersCount,
        followingCount = followingCount,
        friendsCount = friendsCount,
        giftsReceivedCount = giftsReceivedCount,
        giftsSentCount = giftsSentCount,
        visitorsCount = visitorsCount,
        familyId = familyId,
        isHostVerified = isHostVerified,
        hostLevel = hostLevel,
        agencyName = agencyName,
        lastCheckInDay = lastCheckInDay,
        isCheckedInToday = isCheckedInToday,
        activeRelationships = _acceptedRelationships.value,
    )

    private fun VoiceRoomEntity.toDomain(): VoiceRoom {
        val mode = runCatching { RoomMode.valueOf(roomModeName) }.getOrDefault(RoomMode.NORMAL)
        val parsedTags = tagsCsv.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val resolvedBackdrop = if (mode == RoomMode.AUCTION) {
            VoiceRoomBackdropTheme.AUCTION_ROYAL
        } else {
            runCatching { VoiceRoomBackdropTheme.valueOf(backdropThemeName) }
                .recoverCatching { VoiceRoomBackdropTheme.valueOf(themeName) }
                .getOrDefault(VoiceRoomBackdropTheme.ROYAL_BLUE)
        }
        return VoiceRoom(
            id = id,
            title = title,
            description = description,
            category = runCatching { RoomCategory.valueOf(categoryName) }.getOrDefault(RoomCategory.PUBLIC),
            password = password,
            hostId = hostId,
            hostName = hostName,
            hostVip = hostVip,
            maxSeats = if (roomLevel < 7) 8 else maxSeats.coerceIn(8, 14),
            isPartnerSeatEnabled = isPartnerSeatEnabled,
            roomLevel = roomLevel,
            totalContribution = totalContribution,
            isFavorited = isFavorited,
            createdAtMillis = createdAtMillis,
            decorationStyle = decorationStyle,
            musicAdminsOnly = musicAdminsOnly,
            themeStyle = runCatching { RoomThemeStyle.valueOf(themeName) }.getOrDefault(RoomThemeStyle.GALAXY),
            announcement = announcement,
            popularity = popularity,
            onlineCount = onlineCount,
            language = language,
            familyName = familyName,
            isPkActive = categoryName == RoomCategory.PK.name,
            backdropTheme = resolvedBackdrop,
            isOfficial = categoryName == RoomCategory.OFFICIAL.name || hostId == "host_official",
            roomMode = mode,
            tags = parsedTags,
            highQualityAudio = highQualityAudio,
            banTextChatting = banTextChatting,
            banPhotoChatting = banPhotoChatting,
            banRedPacket = banRedPacket,
            membershipFeeCoins = membershipFeeCoins,
            isRecentlyActive = isRecentlyActive,
            isNewRoom = isNewRoom,
        )
    }

    private fun WalletTransactionEntity.toDomain() = WalletTransaction(
        id = id,
        type = runCatching { TransactionType.valueOf(typeName) }.getOrDefault(TransactionType.RECHARGE),
        currency = runCatching { CurrencyKind.valueOf(currencyName) }.getOrDefault(CurrencyKind.COINS),
        amount = amount,
        isCredit = isCredit,
        description = description,
        referenceId = referenceId,
        timestamp = timestamp,
    )

    private fun SocialPostEntity.toDomain() = SocialPost(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorVip = authorVip,
        content = content,
        hashtag = hashtag,
        mediaType = mediaType,
        imageUri = imageUri,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isLiked = isLiked,
        isPublic = isPublic,
        timestamp = timestamp,
    )

    private fun ReceivedGiftEntity.toDomain() = ReceivedGift(
        id = id,
        ownerUid = ownerUid,
        giftId = giftId,
        giftName = giftName,
        emoji = emoji,
        coinValue = coinValue,
        starsPerUnit = starsPerUnit,
        quantity = quantity,
        category = runCatching { GiftCategory.valueOf(categoryName) }.getOrDefault(GiftCategory.ROSES),
        accentHex = accentHex,
        lastReceivedAt = lastReceivedAt,
    )

    private fun GuardRelationshipEntity.toDomain() = GuardRelationship(
        id = id,
        ownerUid = ownerUid,
        guardUid = guardUid,
        guardName = guardName,
        guardAccentHex = guardAccentHex,
        guardLevel = guardLevel,
        tierLabel = tierLabel,
        isAuthorized = isAuthorized,
        isPrivateHidden = isPrivateHidden,
        grantedAt = grantedAt,
    )

    private fun DirectMessageEntity.toDomain() = DirectMessage(
        id = id,
        threadId = threadId,
        senderId = senderId,
        senderName = senderName,
        text = text,
        giftName = giftName,
        voiceSeconds = voiceSeconds,
        reaction = reaction,
        isRead = isRead,
        timestamp = timestamp,
    )

    private fun GameMatchEntity.toDomain() = GameMatchRecord(
        id = id,
        gameTitle = gameTitle,
        outcome = outcome,
        score = score,
        coinsWon = coinsWon,
        xpEarned = xpEarned,
        timestamp = timestamp,
    )

    private fun SafetyReportEntity.toDomain() = SafetyReport(
        id = id,
        targetType = targetType,
        targetName = targetName,
        reason = reason,
        status = status,
        timestamp = timestamp,
    )
}
