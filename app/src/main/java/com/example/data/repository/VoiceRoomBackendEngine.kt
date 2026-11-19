package com.example.data.repository

import com.example.data.local.ContributorRankingEntity
import com.example.data.local.RoomActivityLogEntity
import com.example.data.local.RoomAdminEntity
import com.example.data.local.RoomAnnouncementEntity
import com.example.data.local.RoomContributionEntity
import com.example.data.local.RoomGiftTransactionEntity
import com.example.data.local.RoomLevelEntity
import com.example.data.local.RoomModeratorEntity
import com.example.data.local.RoomOwnerEntity
import com.example.data.local.RoomPartnerEntity
import com.example.data.local.RoomSettingsEntity
import com.example.data.local.VoiceRoomDao
import com.example.data.local.VoiceRoomEntity
import com.example.data.local.VoiceRoomSystemDao
import com.example.data.model.AnnouncementCategory
import com.example.data.model.ContributionConversionRule
import com.example.data.model.ContributionPeriod
import com.example.data.model.ContributorRankEntry
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomAnnouncementEntry
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomGiftLedgerEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomLevelUpEvent
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomMode
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.RoomThemeStyle
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoomBackdropTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class RoomGiftProcessResult(
    val updatedRoomLevel: Int,
    val updatedTotalContribution: Long,
    val updatedSeatCount: Int,
    val contributionPointsAdded: Long,
    val receiverDiamondsEarned: Long,
    val levelUpEvent: RoomLevelUpEvent?,
)

class VoiceRoomBackendEngine(
    private val roomDao: VoiceRoomDao,
    private val sysDao: VoiceRoomSystemDao,
) {

    fun observeLevelConfigs(): Flow<List<RoomLevelConfig>> = sysDao.observeRoomLevels().map { list ->
        if (list.isEmpty()) VoiceRoomSeedData.defaultLevelConfigs
        else list.map {
            RoomLevelConfig(
                level = it.level,
                seatCount = it.seatCount,
                requiredContribution = it.requiredContribution,
                badgeTitle = it.badgeTitle,
                unlockedDecorations = it.unlockedDecorationsCsv.split("|").filter { d -> d.isNotBlank() }
            )
        }
    }

    fun observeContributionSummary(roomId: String): Flow<RoomContributionSummary?> =
        sysDao.observeRoomContribution(roomId).map { entity ->
            if (entity == null) null else buildSummary(entity)
        }

    fun observeContributorRankings(roomId: String): Flow<List<ContributorRankEntry>> =
        sysDao.observeContributorRankings(roomId).map { list ->
            list.groupBy { it.periodName }.flatMap { (_, items) ->
                items.sortedByDescending { it.contributionPoints }.mapIndexed { idx, e ->
                    ContributorRankEntry(
                        id = e.id,
                        roomId = e.roomId,
                        userId = e.userId,
                        displayName = e.displayName,
                        initials = initialsOf(e.displayName),
                        avatarColorHex = e.avatarColorHex,
                        vipLevel = e.vipLevel,
                        period = runCatching { ContributionPeriod.valueOf(e.periodName) }.getOrDefault(ContributionPeriod.TOP),
                        contributionPoints = e.contributionPoints,
                        totalGiftCoins = e.totalGiftCoins,
                        rank = idx + 1,
                    )
                }
            }
        }

    fun observeRoomGiftTransactions(roomId: String): Flow<List<RoomGiftLedgerEntry>> =
        sysDao.observeRoomGiftTransactions(roomId).map { list ->
            list.map {
                RoomGiftLedgerEntry(
                    id = it.id,
                    roomId = it.roomId,
                    senderId = it.senderId,
                    senderName = it.senderName,
                    receiverId = it.receiverId,
                    receiverName = it.receiverName,
                    giftName = it.giftName,
                    giftEmoji = it.giftEmoji,
                    coinValue = it.coinValue,
                    contributionPoints = it.contributionPoints,
                    receiverDiamondsEarned = it.receiverDiamondsEarned,
                    timestamp = it.timestamp,
                )
            }
        }

    fun observeAnnouncements(roomId: String): Flow<List<RoomAnnouncementEntry>> =
        sysDao.observeAnnouncements(roomId).map { list ->
            list.map {
                RoomAnnouncementEntry(
                    id = it.id,
                    roomId = it.roomId,
                    category = runCatching { AnnouncementCategory.valueOf(it.categoryName) }.getOrDefault(AnnouncementCategory.NOTICE),
                    title = it.title,
                    content = it.content,
                    authorName = it.authorName,
                    timestamp = it.timestamp,
                )
            }
        }

    fun observeActivityLogs(roomId: String): Flow<List<RoomActivityLogEntry>> =
        sysDao.observeActivityLogs(roomId).map { list ->
            list.map {
                RoomActivityLogEntry(
                    id = it.id,
                    roomId = it.roomId,
                    actorName = it.actorName,
                    action = it.action,
                    detail = it.detail,
                    timestamp = it.timestamp,
                )
            }
        }

    suspend fun ensureRoomSystemSeeded(rooms: List<VoiceRoomEntity>) {
        if (sysDao.countRoomLevels() == 0) {
            sysDao.upsertRoomLevels(VoiceRoomSeedData.levelEntities())
        }
        rooms.forEach { rm ->
            ensureSingleRoomSeeded(rm)
        }
    }

    suspend fun ensureSingleRoomSeeded(rm: VoiceRoomEntity, hostUserLevel: Int? = null) {
        if (sysDao.getRoomOwner(rm.id) == null) {
            sysDao.upsertRoomOwner(
                VoiceRoomSeedData.seedOwnerForRoom(
                    roomId = rm.id,
                    hostId = rm.hostId,
                    hostName = rm.hostName,
                    hostVip = rm.hostVip,
                    hostUserLevel = hostUserLevel ?: (24 + rm.hostVip * 4),
                )
            )
        }
        if (sysDao.getRoomPartner(rm.id) == null) {
            sysDao.upsertRoomPartner(VoiceRoomSeedData.seedPartnerForRoom(rm.id))
        }
        if (sysDao.getRoomAdmins(rm.id).isEmpty()) {
            VoiceRoomSeedData.seedAdminsForRoom(rm.id, rm.hostName).forEach { sysDao.upsertRoomAdmin(it) }
        }
        if (sysDao.getRoomModerators(rm.id).isEmpty()) {
            VoiceRoomSeedData.seedModeratorsForRoom(rm.id, rm.hostName).forEach { sysDao.upsertRoomModerator(it) }
        }
        if (sysDao.getRoomContribution(rm.id) == null) {
            sysDao.upsertRoomContribution(VoiceRoomSeedData.seedContributionsForRoom(rm.id, rm.roomLevel, rm.totalContribution))
            sysDao.upsertContributorRankings(VoiceRoomSeedData.seedRankings(rm.id))
            VoiceRoomSeedData.seedGiftTransactions(rm.id, rm.hostId, rm.hostName).forEach {
                runCatching { sysDao.insertRoomGiftTransaction(it) }
            }
            sysDao.upsertAnnouncements(VoiceRoomSeedData.seedAnnouncements(rm.id, rm.hostName, rm.announcement))
            sysDao.upsertRoomSettings(VoiceRoomSeedData.seedSettings(rm.id, rm.themeName, rm.decorationStyle))
            VoiceRoomSeedData.seedActivityLogs(rm.id, rm.hostName).forEach { sysDao.insertActivityLog(it) }
        }
    }

    suspend fun loadRoomSnapshot(roomId: String): Triple<RoomOwnerInfo?, RoomPartnerInfo?, List<RoomMemberRoleEntry>> {
        val ownerE = sysDao.getRoomOwner(roomId)
        val partnerE = sysDao.getRoomPartner(roomId)
        val admins = sysDao.getRoomAdmins(roomId)
        val mods = sysDao.getRoomModerators(roomId)

        val owner = ownerE?.let {
            RoomOwnerInfo(
                roomId = it.roomId,
                userId = it.userId,
                displayName = it.displayName,
                initials = initialsOf(it.displayName),
                avatarColorHex = it.avatarColorHex,
                vipLevel = it.vipLevel,
                userLevel = it.userLevel,
                isSpeaking = true,
                isMuted = false,
                rmsLevel = 0.42f,
                charmCount = 1850L,
            )
        }
        val partner = partnerE?.let {
            RoomPartnerInfo(
                roomId = it.roomId,
                userId = it.userId,
                displayName = it.displayName,
                initials = it.displayName?.let(::initialsOf) ?: "",
                vipLevel = it.vipLevel,
                userLevel = it.userLevel,
                isLocked = it.isLocked,
                isEnabled = it.isEnabled,
                canModerate = it.canModerate,
                canManageSeats = it.canManageSeats,
            )
        }
        val roster = buildList {
            if (owner != null) {
                add(RoomMemberRoleEntry(roomId, owner.userId, owner.displayName, owner.initials, RoomRole.OWNER, owner.vipLevel, owner.userLevel))
            }
            if (partner != null && partner.userId != null && partner.displayName != null) {
                add(RoomMemberRoleEntry(roomId, partner.userId, partner.displayName, partner.initials, RoomRole.PARTNER, partner.vipLevel, partner.userLevel))
            }
            admins.forEach { a ->
                val role = if (a.roleName == RoomRole.SUPER_ADMIN.name) RoomRole.SUPER_ADMIN else RoomRole.ROOM_ADMIN
                add(RoomMemberRoleEntry(roomId, a.userId, a.displayName, initialsOf(a.displayName), role, 4, 21))
            }
            mods.forEach { m ->
                add(RoomMemberRoleEntry(roomId, m.userId, m.displayName, initialsOf(m.displayName), RoomRole.MODERATOR, 2, 16))
            }
            add(RoomMemberRoleEntry(roomId, "sp_4", "Zain Flow", "ZF", RoomRole.VIP_MEMBER, 3, 14))
            add(RoomMemberRoleEntry(roomId, "usr_guest_1", "Neha Pulse", "NP", RoomRole.NORMAL_MEMBER, 0, 7))
        }.distinctBy { it.userId }
        return Triple(owner, partner, roster)
    }

    /**
     * Separate backend-validated accounting:
     * 1. Gift coin value is deducted from sender's wallet (in WefunRepository).
     * 2. Room contribution points are computed from configurable ContributionConversionRule (not 1:1 coin assumption).
     * 3. Receiver earnings (diamonds) are computed separately.
     * 4. Room level 1..10 progression and seat capacity upgrades are strictly validated against RoomLevels table.
     */
    suspend fun processRoomGiftContribution(
        roomId: String,
        sender: UserProfile,
        receiverId: String,
        receiverName: String,
        gift: VirtualGift,
        comboCount: Int,
        totalCoins: Long,
    ): RoomGiftProcessResult {
        val roomEntity = roomDao.getRoomById(roomId)
        val existing = sysDao.getRoomContribution(roomId) ?: VoiceRoomSeedData.seedContributionsForRoom(roomId, 1, 0L)
        val settings = sysDao.getRoomSettings(roomId)
        val levels = getValidatedLevelConfigs()

        val minCoins = settings?.minGiftCoinsForPoints ?: 10L
        val rule = ContributionConversionRule(
            pointsPerCoin = existing.pointsPerCoinRule,
            diamondsPerCoin = existing.diamondsPerCoinRule,
            minGiftCoinsForPoints = minCoins
        )

        val eligible = gift.category in rule.eligibleCategories && totalCoins >= rule.minGiftCoinsForPoints
        val pointsAdded = if (eligible) {
            (totalCoins * rule.pointsPerCoin).toLong().coerceAtLeast(1L)
        } else 0L
        val diamondsEarned = (totalCoins * rule.diamondsPerCoin).toLong().coerceAtLeast(1L)

        val newTotalPoints = existing.totalContribution + pointsAdded
        val oldLevel = existing.currentLevel
        val resolvedConfig = levels.lastOrNull { newTotalPoints >= it.requiredContribution } ?: levels.first()
        val newLevel = resolvedConfig.level.coerceIn(1, 10)
        val newSeatCount = resolvedConfig.seatCount

        val levelUpEvent = if (newLevel > oldLevel) {
            RoomLevelUpEvent(
                oldLevel = oldLevel,
                newLevel = newLevel,
                newSeatCount = newSeatCount,
                unlockedDecorations = resolvedConfig.unlockedDecorations,
            )
        } else null

        sysDao.upsertRoomContribution(
            existing.copy(
                currentLevel = newLevel,
                totalContribution = newTotalPoints,
                todayContribution = existing.todayContribution + pointsAdded,
                weeklyContribution = existing.weeklyContribution + pointsAdded,
                monthlyContribution = existing.monthlyContribution + pointsAdded,
                updatedAt = System.currentTimeMillis(),
            )
        )

        if (roomEntity != null) {
            val effectiveMaxSeats = if (newLevel < 7) 8 else maxOf(roomEntity.maxSeats, newSeatCount).coerceIn(8, 14)
            roomDao.upsertRoom(
                roomEntity.copy(
                    roomLevel = newLevel,
                    totalContribution = newTotalPoints,
                    maxSeats = effectiveMaxSeats,
                    popularity = roomEntity.popularity + totalCoins,
                    decorationStyle = resolvedConfig.unlockedDecorations.firstOrNull() ?: roomEntity.decorationStyle,
                )
            )
        }

        // Record immutable Room Gift Accounting Ledger
        sysDao.insertRoomGiftTransaction(
            RoomGiftTransactionEntity(
                id = "rgtx_${UUID.randomUUID()}",
                roomId = roomId,
                senderId = sender.uid,
                senderName = sender.nickname,
                receiverId = receiverId,
                receiverName = receiverName,
                giftId = gift.id,
                giftName = "${gift.name} x$comboCount",
                giftEmoji = gift.emoji,
                coinValue = totalCoins,
                contributionPoints = pointsAdded,
                receiverDiamondsEarned = diamondsEarned,
                timestamp = System.currentTimeMillis(),
            )
        )

        // Update Top / Today / Weekly / Monthly Contributor Rankings
        if (pointsAdded > 0) {
            ContributionPeriod.entries.forEach { period ->
                val rankId = "${roomId}_${period.name}_${sender.uid}"
                val prev = sysDao.getRankingById(rankId)
                sysDao.upsertContributorRanking(
                    ContributorRankingEntity(
                        id = rankId,
                        roomId = roomId,
                        userId = sender.uid,
                        displayName = sender.nickname,
                        avatarColorHex = sender.accentColorHex,
                        vipLevel = sender.vipLevel,
                        periodName = period.name,
                        contributionPoints = (prev?.contributionPoints ?: 0L) + pointsAdded,
                        totalGiftCoins = (prev?.totalGiftCoins ?: 0L) + totalCoins,
                    )
                )
            }
        }

        if (levelUpEvent != null) {
            logActivity(
                roomId = roomId,
                actorName = "System Validator",
                action = "Room Upgraded to Level ${levelUpEvent.newLevel}",
                detail = "Verified $newTotalPoints contribution points unlocked ${levelUpEvent.newSeatCount} mic seats & ${levelUpEvent.unlockedDecorations.joinToString()}"
            )
        } else {
            logActivity(
                roomId = roomId,
                actorName = sender.nickname,
                action = "Gift Contribution",
                detail = "${gift.emoji} ${gift.name} x$comboCount ($totalCoins Coins -> +$pointsAdded pts, +$diamondsEarned host diamonds)"
            )
        }

        return RoomGiftProcessResult(
            updatedRoomLevel = newLevel,
            updatedTotalContribution = newTotalPoints,
            updatedSeatCount = newSeatCount,
            contributionPointsAdded = pointsAdded,
            receiverDiamondsEarned = diamondsEarned,
            levelUpEvent = levelUpEvent,
        )
    }

    suspend fun toggleFavoriteRoom(roomId: String): Boolean {
        val rm = roomDao.getRoomById(roomId) ?: return false
        val next = !rm.isFavorited
        roomDao.upsertRoom(rm.copy(isFavorited = next))
        return next
    }

    suspend fun publishAnnouncement(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
        category: AnnouncementCategory,
        title: String,
        content: String,
    ): Result<Unit> {
        if (!actorRole.canModerate()) {
            return Result.failure(IllegalStateException("Permission denied: Moderator or higher required"))
        }
        val entry = RoomAnnouncementEntity(
            id = "ann_${UUID.randomUUID()}",
            roomId = roomId,
            categoryName = category.name,
            title = title.ifBlank { category.label },
            content = content,
            authorName = actorName,
            timestamp = System.currentTimeMillis(),
        )
        sysDao.upsertAnnouncement(entry)
        roomDao.getRoomById(roomId)?.let { rm ->
            roomDao.upsertRoom(rm.copy(announcement = content))
        }
        logActivity(roomId, actorName, "Announcement Published", "[${category.label}] $content")
        return Result.success(Unit)
    }

    suspend fun assignRole(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
        targetUserId: String,
        targetName: String,
        newRole: RoomRole,
    ): Result<Unit> {
        if (!actorRole.canManageRoles() && !actorRole.isOwnerOrPartner()) {
            return Result.failure(IllegalStateException("Only Owner, Partner or Super Admin can assign roles"))
        }
        // Clean existing role assignments for target
        sysDao.deleteRoomAdmin(roomId, targetUserId)
        sysDao.deleteRoomModerator(roomId, targetUserId)

        when (newRole) {
            RoomRole.PARTNER -> {
                if (actorRole != RoomRole.OWNER) {
                    return Result.failure(IllegalStateException("Only the Room Owner can assign a Partner"))
                }
                sysDao.upsertRoomPartner(
                    RoomPartnerEntity(
                        roomId = roomId,
                        userId = targetUserId,
                        displayName = targetName,
                        vipLevel = 4,
                        userLevel = 22,
                        isLocked = false,
                        isEnabled = true,
                        canModerate = true,
                        canManageSeats = true,
                    )
                )
                sysDao.getRoomSettings(roomId)?.let { settings ->
                    sysDao.upsertRoomSettings(settings.copy(isPartnerSeatEnabled = true, updatedAt = System.currentTimeMillis()))
                }
                roomDao.getRoomById(roomId)?.let { rm ->
                    roomDao.upsertRoom(rm.copy(isPartnerSeatEnabled = true))
                }
            }
            RoomRole.SUPER_ADMIN, RoomRole.ROOM_ADMIN -> {
                sysDao.upsertRoomAdmin(
                    RoomAdminEntity(
                        id = "${roomId}_$targetUserId",
                        roomId = roomId,
                        userId = targetUserId,
                        displayName = targetName,
                        roleName = newRole.name,
                        grantedBy = actorName,
                        grantedAt = System.currentTimeMillis(),
                    )
                )
            }
            RoomRole.MODERATOR -> {
                sysDao.upsertRoomModerator(
                    RoomModeratorEntity(
                        id = "${roomId}_$targetUserId",
                        roomId = roomId,
                        userId = targetUserId,
                        displayName = targetName,
                        grantedBy = actorName,
                        grantedAt = System.currentTimeMillis(),
                    )
                )
            }
            RoomRole.OWNER -> {
                if (actorRole != RoomRole.OWNER) {
                    return Result.failure(IllegalStateException("Only the Owner can transfer ownership"))
                }
                transferOwnership(roomId, targetUserId, targetName, actorName)
            }
            else -> {}
        }
        logActivity(roomId, actorName, "Role Updated", "$targetName -> ${newRole.label}")
        return Result.success(Unit)
    }

    suspend fun removePartner(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
    ): Result<Unit> {
        if (actorRole != RoomRole.OWNER) {
            return Result.failure(IllegalStateException("Only the Room Owner can remove the Room Partner"))
        }
        val currentEnabled = sysDao.getRoomPartner(roomId)?.isEnabled ?: false
        sysDao.upsertRoomPartner(
            RoomPartnerEntity(
                roomId = roomId,
                userId = null,
                displayName = null,
                vipLevel = 0,
                userLevel = 1,
                isLocked = false,
                isEnabled = currentEnabled,
                canModerate = true,
                canManageSeats = true,
            )
        )
        logActivity(roomId, actorName, "Partner Removed", "Partner position vacated and ready for assignment")
        return Result.success(Unit)
    }

    suspend fun setPartnerSeatEnabled(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
        enabled: Boolean,
    ): Result<Unit> {
        if (actorRole != RoomRole.OWNER) {
            return Result.failure(IllegalStateException("Only the Room Owner can toggle the Partner Seat"))
        }
        val existingPartner = sysDao.getRoomPartner(roomId) ?: VoiceRoomSeedData.seedPartnerForRoom(roomId)
        sysDao.upsertRoomPartner(
            existingPartner.copy(
                isEnabled = enabled,
                isLocked = false,
            )
        )
        sysDao.getRoomSettings(roomId)?.let { settings ->
            sysDao.upsertRoomSettings(
                settings.copy(
                    isPartnerSeatEnabled = enabled,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        roomDao.getRoomById(roomId)?.let { rm ->
            roomDao.upsertRoom(rm.copy(isPartnerSeatEnabled = enabled))
        }
        logActivity(
            roomId = roomId,
            actorName = actorName,
            action = if (enabled) "Partner Seat Enabled" else "Partner Seat Hidden",
            detail = if (enabled) "Owner enabled the dedicated Partner throne" else "Owner disabled the Partner throne"
        )
        return Result.success(Unit)
    }

    suspend fun transferOwnership(roomId: String, newOwnerId: String, newOwnerName: String, previousOwnerName: String) {
        sysDao.upsertRoomOwner(
            RoomOwnerEntity(
                roomId = roomId,
                userId = newOwnerId,
                displayName = newOwnerName,
                vipLevel = 5,
                userLevel = 28,
                avatarColorHex = 0xFFFFD700,
            )
        )
        roomDao.getRoomById(roomId)?.let { rm ->
            roomDao.upsertRoom(rm.copy(hostId = newOwnerId, hostName = newOwnerName))
        }
        logActivity(roomId, previousOwnerName, "Ownership Transferred", "New Room Owner: $newOwnerName")
    }

    suspend fun updateLevelSeatMappingAndRules(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
        updatedSeatCounts: Map<Int, Int>,
        pointsPerCoin: Double,
        diamondsPerCoin: Double,
    ): Result<Unit> {
        if (!actorRole.canManageRoles()) {
            return Result.failure(IllegalStateException("Admin permission required to configure Level rules"))
        }
        val current = getValidatedLevelConfigs()
        val updatedEntities = current.map { cfg ->
            val newSeats = if (cfg.level < 7) 8 else (updatedSeatCounts[cfg.level] ?: cfg.seatCount).coerceIn(8, 14)
            RoomLevelEntity(
                level = cfg.level,
                seatCount = newSeats,
                requiredContribution = cfg.requiredContribution,
                badgeTitle = cfg.badgeTitle,
                unlockedDecorationsCsv = cfg.unlockedDecorations.joinToString("|"),
            )
        }
        sysDao.upsertRoomLevels(updatedEntities)

        val contrib = sysDao.getRoomContribution(roomId) ?: VoiceRoomSeedData.seedContributionsForRoom(roomId, 1, 0L)
        sysDao.upsertRoomContribution(
            contrib.copy(
                pointsPerCoinRule = pointsPerCoin.coerceIn(0.1, 5.0),
                diamondsPerCoinRule = diamondsPerCoin.coerceIn(0.1, 1.0),
                updatedAt = System.currentTimeMillis(),
            )
        )
        logActivity(roomId, actorName, "Level & Conversion Rules Saved", "points/coin=$pointsPerCoin, diamonds/coin=$diamondsPerCoin")
        return Result.success(Unit)
    }

    suspend fun updateRoomAppearanceAndPermissions(
        roomId: String,
        actorName: String,
        title: String,
        theme: RoomThemeStyle,
        decorationStyle: String,
        musicAdminsOnly: Boolean,
    ) {
        roomDao.getRoomById(roomId)?.let { rm ->
            roomDao.upsertRoom(
                rm.copy(
                    title = title.ifBlank { rm.title },
                    themeName = theme.name,
                    decorationStyle = decorationStyle,
                    musicAdminsOnly = musicAdminsOnly,
                )
            )
        }
        val settings = sysDao.getRoomSettings(roomId) ?: VoiceRoomSeedData.seedSettings(roomId, theme.name, decorationStyle)
        sysDao.upsertRoomSettings(
            settings.copy(
                musicAdminsOnly = musicAdminsOnly,
                decorationStyle = decorationStyle,
                themeName = theme.name,
                updatedAt = System.currentTimeMillis(),
            )
        )
        logActivity(roomId, actorName, "Room Appearance Updated", "Theme: ${theme.label}, Decor: $decorationStyle")
    }

    suspend fun updateRoomBackdrop(roomId: String, backdropTheme: VoiceRoomBackdropTheme) {
        roomDao.getRoomById(roomId)?.let { rm ->
            roomDao.upsertRoom(rm.copy(backdropThemeName = backdropTheme.name))
        }
        sysDao.getRoomSettings(roomId)?.let { settings ->
            sysDao.upsertRoomSettings(
                settings.copy(
                    themeName = backdropTheme.name,
                    updatedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun getRoomByIdValidated(roomId: String): Result<VoiceRoomEntity> {
        val cleanId = roomId.trim()
        if (cleanId.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter a valid Room ID."))
        }
        val entity = roomDao.getRoomById(cleanId)
            ?: return Result.failure(NoSuchElementException("Room ID '$cleanId' was not found."))
        return Result.success(entity)
    }

    suspend fun updateFullRoomSettings(
        roomId: String,
        actorName: String,
        actorRole: RoomRole,
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
    ): Result<VoiceRoomEntity> {
        if (!actorRole.canManageRoom()) {
            return Result.failure(IllegalStateException("Permission denied: Room Admin or Owner required"))
        }
        val existing = roomDao.getRoomById(roomId)
            ?: return Result.failure(NoSuchElementException("Room not found"))
        val safeFee = membershipFeeCoins.coerceAtLeast(0L)
        val effectiveBackdrop = if (roomMode == RoomMode.AUCTION) {
            VoiceRoomBackdropTheme.AUCTION_ROYAL
        } else {
            backdropTheme
        }
        val updatedRoom = existing.copy(
            title = title.trim().ifBlank { existing.title },
            announcement = announcement.trim().ifBlank { existing.announcement },
            password = password?.trim()?.takeIf { it.isNotBlank() },
            roomModeName = roomMode.name,
            tagsCsv = tags.filter { it.isNotBlank() }.distinct().joinToString(","),
            highQualityAudio = highQualityAudio,
            banTextChatting = banTextChatting,
            banPhotoChatting = banPhotoChatting,
            banRedPacket = banRedPacket,
            membershipFeeCoins = safeFee,
            musicAdminsOnly = musicAdminsOnly,
            backdropThemeName = effectiveBackdrop.name,
        )
        roomDao.upsertRoom(updatedRoom)
        val settings = sysDao.getRoomSettings(roomId) ?: VoiceRoomSeedData.seedSettings(roomId, effectiveBackdrop.name, existing.decorationStyle)
        sysDao.upsertRoomSettings(
            settings.copy(
                roomModeName = roomMode.name,
                highQualityAudio = highQualityAudio,
                banTextChatting = banTextChatting,
                banPhotoChatting = banPhotoChatting,
                banRedPacket = banRedPacket,
                membershipFeeCoins = safeFee,
                musicAdminsOnly = musicAdminsOnly,
                themeName = effectiveBackdrop.name,
                updatedAt = System.currentTimeMillis(),
            )
        )
        logActivity(
            roomId = roomId,
            actorName = actorName,
            action = "Room Settings Updated",
            detail = "Mode=${roomMode.label}, HQ=$highQualityAudio, BanText=$banTextChatting, Fee=$safeFee"
        )
        return Result.success(updatedRoom)
    }

    fun validateCustomTag(rawLabel: String, existingTags: List<String>): Result<String> {
        val clean = rawLabel.trim()
        if (clean.isBlank()) {
            return Result.failure(IllegalArgumentException("Tag name cannot be empty."))
        }
        if (clean.length > 20) {
            return Result.failure(IllegalArgumentException("Tag name must be 20 characters or fewer."))
        }
        val bannedWords = setOf("baddie", "wedding", "scam", "cash", "gamble", "nsfw", "hate", "abuse")
        if (bannedWords.any { clean.lowercase().contains(it) }) {
            return Result.failure(IllegalArgumentException("That relationship tag is not permitted on WeFun."))
        }
        if (existingTags.any { it.equals(clean, ignoreCase = true) }) {
            return Result.failure(IllegalStateException("Tag '$clean' already exists."))
        }
        return Result.success(clean)
    }

    suspend fun logActivity(roomId: String, actorName: String, action: String, detail: String) {
        sysDao.insertActivityLog(
            RoomActivityLogEntity(
                id = "log_${UUID.randomUUID()}",
                roomId = roomId,
                actorName = actorName,
                action = action,
                detail = detail,
                timestamp = System.currentTimeMillis(),
            )
        )
    }

    private suspend fun getValidatedLevelConfigs(): List<RoomLevelConfig> {
        val entities = sysDao.getRoomLevels()
        return if (entities.isEmpty()) VoiceRoomSeedData.defaultLevelConfigs
        else entities.map {
            RoomLevelConfig(
                level = it.level,
                seatCount = it.seatCount,
                requiredContribution = it.requiredContribution,
                badgeTitle = it.badgeTitle,
                unlockedDecorations = it.unlockedDecorationsCsv.split("|").filter { d -> d.isNotBlank() },
            )
        }
    }

    private suspend fun buildSummary(entity: RoomContributionEntity): RoomContributionSummary {
        val levels = getValidatedLevelConfigs()
        val currentCfg = levels.firstOrNull { it.level == entity.currentLevel } ?: levels.first()
        val nextCfg = levels.firstOrNull { it.level == entity.currentLevel + 1 }
        return RoomContributionSummary(
            roomId = entity.roomId,
            currentLevel = entity.currentLevel,
            totalContribution = entity.totalContribution,
            currentLevelThreshold = currentCfg.requiredContribution,
            nextLevelThreshold = nextCfg?.requiredContribution ?: currentCfg.requiredContribution,
            nextLevel = nextCfg?.level,
            seatCapacity = currentCfg.seatCount,
        )
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
