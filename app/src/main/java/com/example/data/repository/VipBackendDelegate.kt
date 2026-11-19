package com.example.data.repository

import com.example.data.local.GrowthPointHistoryEntity
import com.example.data.local.TransactionDao
import com.example.data.local.UserDao
import com.example.data.local.VipAuditLogEntity
import com.example.data.local.VipConfigEntity
import com.example.data.local.VipEcosystemDao
import com.example.data.local.VipPaymentHistoryEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.model.CurrencyKind
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.GrowthPointHistoryEntry
import com.example.data.model.TransactionType
import com.example.data.model.VipAuditLogEntry
import com.example.data.model.VipGiftItem
import com.example.data.model.VipNicknameColorTier
import com.example.data.model.VipPaymentHistoryEntry
import com.example.data.model.VipTierConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class VipBackendDelegate(
    private val userDao: UserDao,
    private val vipDao: VipEcosystemDao,
    private val txDao: TransactionDao,
    private val onAudit: (String) -> Unit,
) {
    private val _vipGifts = MutableStateFlow(VipCatalog.defaultVipGifts)
    val vipGiftsFlow = _vipGifts.asStateFlow()

    val vipConfigsFlow: Flow<List<VipTierConfig>> = vipDao.observeVipConfigs().map { list ->
        if (list.isEmpty()) VipCatalog.defaultTiers else list.map { it.toDomain() }
    }

    val vipAuditLogsFlow: Flow<List<VipAuditLogEntry>> = vipDao.observeAuditLogs().map { list ->
        list.map { it.toDomain() }
    }

    fun observeGrowthHistory(userId: String): Flow<List<GrowthPointHistoryEntry>> =
        vipDao.observeGrowthHistory(userId).map { list -> list.map { it.toDomain() } }

    fun observePaymentHistory(userId: String): Flow<List<VipPaymentHistoryEntry>> =
        vipDao.observePaymentHistory(userId).map { list -> list.map { it.toDomain() } }

    suspend fun ensureSeeded(defaultUserId: String = "usr_nova_01") {
        if (vipDao.countVipConfigs() == 0) {
            vipDao.upsertVipConfigs(VipCatalog.defaultTiers.map { it.toEntity() })
        }
        val now = System.currentTimeMillis()
        if (vipDao.countPaymentHistory() == 0) {
            vipDao.upsertPaymentHistory(
                VipPaymentHistoryEntity(
                    id = "vp_seed_applied",
                    userId = defaultUserId,
                    planTitle = "VIP 2 Membership (1 Month)",
                    months = 1,
                    targetLevel = 2,
                    amountDisplay = "600 Coins",
                    coinsSpent = 600L,
                    status = "APPLIED",
                    referenceId = "vip_seed_applied_001",
                    timestamp = now - 86_400_000L * 5,
                )
            )
            vipDao.upsertPaymentHistory(
                VipPaymentHistoryEntity(
                    id = "vp_seed_pending_restore",
                    userId = defaultUserId,
                    planTitle = "VIP Renewal Pass (+1 Month Verified Receipt)",
                    months = 1,
                    targetLevel = 2,
                    amountDisplay = "Store Receipt #WF-9921",
                    coinsSpent = 0L,
                    status = "PENDING_RESTORE",
                    referenceId = "vip_seed_pending_002",
                    timestamp = now - 3_600_000L * 12,
                )
            )
        }
        if (vipDao.countGrowthHistory() == 0) {
            vipDao.insertGrowthHistory(
                GrowthPointHistoryEntity(
                    id = "gh_seed_1",
                    userId = defaultUserId,
                    sourceCategoryName = DiamondSpendCategory.GIFT.name,
                    diamondsSpent = 700L,
                    pointsDelta = 7L,
                    regulationPointsDelta = 7L,
                    balanceAfter = 1_195L,
                    levelBefore = 2,
                    levelAfter = 2,
                    description = "Sent Gift Card Bundle (700 Diamonds → +7 Growth Pts)",
                    referenceId = "gh_seed_ref_1",
                    timestamp = now - 86_400_000L * 3,
                )
            )
            vipDao.insertGrowthHistory(
                GrowthPointHistoryEntity(
                    id = "gh_seed_2",
                    userId = defaultUserId,
                    sourceCategoryName = DiamondSpendCategory.SHOP_LUCKY_DRAW.name,
                    diamondsSpent = 500L,
                    pointsDelta = 5L,
                    regulationPointsDelta = 5L,
                    balanceAfter = 1_200L,
                    levelBefore = 2,
                    levelAfter = 2,
                    description = "Shop Lucky Draw (500 Diamonds → +5 Growth Pts)",
                    referenceId = "gh_seed_ref_2",
                    timestamp = now - 86_400_000L,
                )
            )
        }
        if (vipDao.countAuditLogs() == 0) {
            vipDao.insertAuditLog(
                VipAuditLogEntity(
                    id = "val_seed_1",
                    actorName = "System",
                    actionType = "INIT_VIP_ECOSYSTEM",
                    targetUserId = defaultUserId,
                    summary = "VIP 1–12 Growth & Regulation Rules Initialized",
                    timestamp = now - 86_400_000L * 5,
                )
            )
        }
    }

    suspend fun getLiveConfigs(): List<VipTierConfig> {
        val entities = vipDao.getVipConfigs()
        return if (entities.isEmpty()) VipCatalog.defaultTiers else entities.map { it.toDomain() }
    }

    suspend fun purchaseVipSubscription(
        level: Int,
        months: Int = 1,
        autoRenew: Boolean? = null,
    ): Result<String> {
        val safeMonths = months.coerceIn(1, 12)
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val configs = getLiveConfigs()
        val cfg = configs.firstOrNull { it.level == level }
            ?: return Result.failure(IllegalArgumentException("VIP level $level is not configured."))
        val totalCost = cfg.monthlySubscriptionCoins * safeMonths
        if (user.coins < totalCost) {
            return Result.failure(IllegalStateException("Insufficient Coins for ${cfg.name} ($totalCost Coins needed)."))
        }

        val now = System.currentTimeMillis()
        val wasActive = VipGrowthEngine.isVipActive(user.vipExpiryAtMillis, now) && user.vipLevel > 0
        val baseStart = if (wasActive) user.vipExpiryAtMillis else now
        val newExpiry = VipGrowthEngine.calculateCalendarMonthExpiry(baseStart, safeMonths)
        val newActivated = if (wasActive && user.vipActivatedAtMillis > 0L) user.vipActivatedAtMillis else now
        val nextLevel = maxOf(user.vipLevel, level).coerceIn(1, 12)
        val nextGrowth = maxOf(user.vipGrowthPoints, cfg.growthThreshold)
        val nextCfg = configs.firstOrNull { it.level == nextLevel } ?: cfg
        val nextDeadline = if (nextLevel > user.vipLevel || user.vipRegulationDeadlineMillis <= now) {
            if (nextCfg.regulationDays > 0) now + nextCfg.regulationDays * 86_400_000L else 0L
        } else {
            user.vipRegulationDeadlineMillis
        }

        val refId = "vip_sub_${UUID.randomUUID()}"
        val updatedUser = user.copy(
            coins = user.coins - totalCost,
            vipLevel = nextLevel,
            vipGrowthPoints = nextGrowth,
            vipActivatedAtMillis = newActivated,
            vipExpiryAtMillis = newExpiry,
            vipAutoRenewal = autoRenew ?: user.vipAutoRenewal,
            vipRegulationDeadlineMillis = nextDeadline,
            vipRegulationPointsEarned = if (nextLevel > user.vipLevel) 0L else user.vipRegulationPointsEarned,
            frameName = nextCfg.badgeTitle,
        )
        userDao.upsertUser(updatedUser)

        txDao.insertTransaction(
            WalletTransactionEntity(
                id = "tx_${UUID.randomUUID()}",
                typeName = TransactionType.VIP_PURCHASE.name,
                currencyName = CurrencyKind.COINS.name,
                amount = totalCost,
                isCredit = false,
                description = "${cfg.name} Membership ($safeMonths cal. mo.)",
                referenceId = refId,
                timestamp = now,
            )
        )
        vipDao.upsertPaymentHistory(
            VipPaymentHistoryEntity(
                id = "vp_${UUID.randomUUID()}",
                userId = user.uid,
                planTitle = "${cfg.name} Membership ($safeMonths Month${if (safeMonths > 1) "s" else ""})",
                months = safeMonths,
                targetLevel = level,
                amountDisplay = "$totalCost Coins",
                coinsSpent = totalCost,
                status = "APPLIED",
                referenceId = refId,
                timestamp = now,
            )
        )
        recordAudit(
            actorName = user.nickname,
            actionType = "VIP_SUBSCRIPTION",
            targetUserId = user.uid,
            summary = "Activated ${cfg.name} for $safeMonths calendar month(s) ($totalCost Coins)",
        )
        return Result.success("${cfg.name} active for $safeMonths calendar month(s)!")
    }

    suspend fun restoreVipPurchases(): Result<Int> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val pending = vipDao.getPendingRestorePayments(user.uid)
        val configs = getLiveConfigs()
        val now = System.currentTimeMillis()
        if (pending.isEmpty()) {
            // Re-sync current entitlement honestly without granting fake upgrades
            val levelFromGrowth = VipGrowthEngine.resolveLevelForGrowthPoints(user.vipGrowthPoints, configs)
            val syncedLevel = maxOf(user.vipLevel, levelFromGrowth).coerceIn(0, 12)
            if (syncedLevel != user.vipLevel) {
                userDao.upsertUser(user.copy(vipLevel = syncedLevel))
            }
            recordAudit(
                actorName = user.nickname,
                actionType = "RESTORE_PURCHASE",
                targetUserId = user.uid,
                summary = "Restore Purchase checked — all verified receipts already applied.",
            )
            return Result.success(0)
        }

        var workingExpiry = if (user.vipExpiryAtMillis > now) user.vipExpiryAtMillis else now
        var workingLevel = user.vipLevel
        var workingGrowth = user.vipGrowthPoints
        val workingActivated = if (user.vipActivatedAtMillis > 0L) user.vipActivatedAtMillis else now

        pending.forEach { receipt ->
            workingExpiry = VipGrowthEngine.calculateCalendarMonthExpiry(workingExpiry, receipt.months)
            workingLevel = maxOf(workingLevel, receipt.targetLevel).coerceIn(1, 12)
            val cfg = configs.firstOrNull { it.level == workingLevel }
            if (cfg != null) {
                workingGrowth = maxOf(workingGrowth, cfg.growthThreshold)
            }
            vipDao.upsertPaymentHistory(receipt.copy(status = "RESTORED"))
        }

        userDao.upsertUser(
            user.copy(
                vipLevel = workingLevel,
                vipGrowthPoints = workingGrowth,
                vipActivatedAtMillis = workingActivated,
                vipExpiryAtMillis = workingExpiry,
            )
        )
        recordAudit(
            actorName = user.nickname,
            actionType = "RESTORE_PURCHASE",
            targetUserId = user.uid,
            summary = "Restored ${pending.size} verified store receipt(s) -> VIP $workingLevel",
        )
        return Result.success(pending.size)
    }

    suspend fun spendDiamondsForVipGrowth(
        category: DiamondSpendCategory,
        diamondsAmount: Long,
        description: String,
        referenceId: String = "dia_${UUID.randomUUID()}",
    ): Result<DiamondSpendOutcome> {
        if (diamondsAmount <= 0L) {
            return Result.failure(IllegalArgumentException("Diamond spend must be positive."))
        }
        if (txDao.existsByReference(referenceId) || vipDao.growthReferenceExists(referenceId)) {
            return Result.failure(IllegalStateException("Duplicate Diamond transaction blocked."))
        }
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        if (user.diamonds < diamondsAmount) {
            return Result.failure(IllegalStateException("Insufficient Diamonds ($diamondsAmount needed)."))
        }

        val now = System.currentTimeMillis()
        val configs = getLiveConfigs()
        val active = VipGrowthEngine.isVipActive(user.vipExpiryAtMillis, now) && user.vipLevel > 0
        val outcome = VipGrowthEngine.processDiamondSpend(
            currentGrowthPoints = user.vipGrowthPoints,
            currentVipLevel = user.vipLevel,
            isVipActive = active,
            diamondsSpent = diamondsAmount,
            category = category,
            currentRegulationDeadlineMillis = user.vipRegulationDeadlineMillis,
            currentRegulationEarnedPoints = user.vipRegulationPointsEarned,
            nowMillis = now,
            configs = configs,
        )

        userDao.upsertUser(
            user.copy(
                diamonds = user.diamonds - diamondsAmount,
                vipGrowthPoints = outcome.newGrowthPoints,
                vipLevel = outcome.levelAfter,
                vipRegulationDeadlineMillis = outcome.newRegulationDeadlineMillis,
                vipRegulationPointsEarned = outcome.newRegulationEarnedPoints,
            )
        )
        txDao.insertTransaction(
            WalletTransactionEntity(
                id = "tx_${UUID.randomUUID()}",
                typeName = if (category == DiamondSpendCategory.GIFT) TransactionType.GIFT_SENT.name else TransactionType.VIP_PURCHASE.name,
                currencyName = CurrencyKind.DIAMONDS.name,
                amount = diamondsAmount,
                isCredit = false,
                description = "$description (${category.label})",
                referenceId = referenceId,
                timestamp = now,
            )
        )
        vipDao.insertGrowthHistory(
            GrowthPointHistoryEntity(
                id = "gh_${UUID.randomUUID()}",
                userId = user.uid,
                sourceCategoryName = category.name,
                diamondsSpent = diamondsAmount,
                pointsDelta = outcome.growthPointsEarned,
                regulationPointsDelta = outcome.growthPointsEarned,
                balanceAfter = outcome.newGrowthPoints,
                levelBefore = outcome.levelBefore,
                levelAfter = outcome.levelAfter,
                description = if (outcome.growthPointsEarned > 0L) {
                    "$description (+${outcome.growthPointsEarned} Growth Pts)"
                } else {
                    "$description (0 Growth Pts — non-VIP or excluded spend)"
                },
                referenceId = referenceId,
                timestamp = now,
            )
        )
        if (outcome.leveledUp) {
            recordAudit(
                actorName = user.nickname,
                actionType = "VIP_LEVEL_UP",
                targetUserId = user.uid,
                summary = "Leveled up VIP ${outcome.levelBefore} -> VIP ${outcome.levelAfter} via ${category.label}",
            )
        }
        return Result.success(outcome)
    }

    suspend fun claimDailyVipDiamonds(): Result<Long> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val now = System.currentTimeMillis()
        if (!VipGrowthEngine.isVipActive(user.vipExpiryAtMillis, now) || user.vipLevel <= 0) {
            return Result.failure(IllegalStateException("Active VIP membership is required for daily VIP Diamonds."))
        }
        val todayEpochDay = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
        if (user.vipLastDailyClaimEpochDay >= todayEpochDay) {
            return Result.failure(IllegalStateException("Today's VIP Diamond reward is already claimed."))
        }
        val configs = getLiveConfigs()
        val bonus = VipGrowthEngine.dailyDiamondBonus(user.vipLevel, configs)
        userDao.upsertUser(
            user.copy(
                diamonds = user.diamonds + bonus,
                vipLastDailyClaimEpochDay = todayEpochDay,
            )
        )
        txDao.insertTransaction(
            WalletTransactionEntity(
                id = "tx_${UUID.randomUUID()}",
                typeName = TransactionType.DAILY_CHECKIN.name,
                currencyName = CurrencyKind.DIAMONDS.name,
                amount = bonus,
                isCredit = true,
                description = "VIP ${user.vipLevel} Daily Sign-in Bonus (+$bonus Diamonds)",
                referenceId = "vip_daily_${user.uid}_$todayEpochDay",
                timestamp = now,
            )
        )
        return Result.success(bonus)
    }

    suspend fun toggleAutoRenewal(enabled: Boolean): Result<Boolean> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        userDao.upsertUser(user.copy(vipAutoRenewal = enabled))
        recordAudit(
            actorName = user.nickname,
            actionType = "AUTO_RENEWAL_TOGGLE",
            targetUserId = user.uid,
            summary = if (enabled) "Auto-renewal enabled (billed 24h before expiry)" else "Auto-renewal disabled",
        )
        return Result.success(enabled)
    }

    suspend fun evaluateRegulation(simulatedAdvanceDays: Int = 0): Result<RegulationEvaluationResult> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val now = System.currentTimeMillis() + simulatedAdvanceDays * 86_400_000L
        val configs = getLiveConfigs()
        val eval = VipGrowthEngine.evaluateRegulation(
            currentGrowthPoints = user.vipGrowthPoints,
            currentVipLevel = user.vipLevel,
            regulationDeadlineMillis = user.vipRegulationDeadlineMillis,
            regulationEarnedPoints = user.vipRegulationPointsEarned,
            nowMillis = now,
            configs = configs,
        )
        if (eval.wasEvaluated) {
            userDao.upsertUser(
                user.copy(
                    vipGrowthPoints = eval.newGrowthPoints,
                    vipLevel = eval.levelAfter,
                    vipRegulationDeadlineMillis = eval.newRegulationDeadlineMillis,
                    vipRegulationPointsEarned = eval.newRegulationEarnedPoints,
                )
            )
            if (eval.totalDeducted > 0L) {
                vipDao.insertGrowthHistory(
                    GrowthPointHistoryEntity(
                        id = "gh_reg_${UUID.randomUUID()}",
                        userId = user.uid,
                        sourceCategoryName = DiamondSpendCategory.OTHER_EXCLUDED.name,
                        diamondsSpent = 0L,
                        pointsDelta = -eval.totalDeducted,
                        regulationPointsDelta = 0L,
                        balanceAfter = eval.newGrowthPoints,
                        levelBefore = eval.levelBefore,
                        levelAfter = eval.levelAfter,
                        description = eval.summaryMessage,
                        referenceId = "reg_${UUID.randomUUID()}",
                        timestamp = System.currentTimeMillis(),
                    )
                )
            }
            recordAudit(
                actorName = "RegulationEngine",
                actionType = "REGULATION_EVAL",
                targetUserId = user.uid,
                summary = eval.summaryMessage,
            )
        }
        return Result.success(eval)
    }

    suspend fun updateVipPrivacyAndCustomization(
        customTitle: String,
        hideRecentGifts: Boolean,
        hideGuardianBoard: Boolean,
        privateProfileVisit: Boolean,
        friendRequestProtection: Boolean,
        customNamecardTheme: String,
    ): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val now = System.currentTimeMillis()
        val active = VipGrowthEngine.isVipActive(user.vipExpiryAtMillis, now) && user.vipLevel > 0
        val effectiveLevel = if (active) user.vipLevel else 0

        userDao.upsertUser(
            user.copy(
                customTitle = if (effectiveLevel >= 8) customTitle.trim().take(24) else user.customTitle,
                hideRecentGifts = if (effectiveLevel >= 7) hideRecentGifts else false,
                hideGuardianBoard = if (effectiveLevel >= 8) hideGuardianBoard else false,
                privateProfileVisit = if (effectiveLevel >= 6) privateProfileVisit else false,
                friendRequestProtection = if (effectiveLevel >= 4) friendRequestProtection else false,
                customNamecardTheme = if (effectiveLevel >= 12) customNamecardTheme else user.customNamecardTheme,
            )
        )
        return Result.success(Unit)
    }

    suspend fun adminUpdateVipConfig(config: VipTierConfig, actorName: String = "Admin"): Result<Unit> {
        vipDao.upsertVipConfig(config.toEntity())
        recordAudit(
            actorName = actorName,
            actionType = "ADMIN_EDIT_TIER",
            targetUserId = "tier_${config.level}",
            summary = "Updated VIP ${config.level}: threshold=${config.growthThreshold}, reg=${config.regulationDays}d/${config.regulationTarget}/${config.growthToDeduct}",
        )
        return Result.success(Unit)
    }

    suspend fun adminGrantOrUpdateUserVip(
        level: Int,
        months: Int,
        active: Boolean,
        actorName: String = "Admin",
    ): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val configs = getLiveConfigs()
        val safeLevel = level.coerceIn(0, 12)
        val cfg = configs.firstOrNull { it.level == safeLevel }
        val now = System.currentTimeMillis()
        val newExpiry = if (active && safeLevel > 0) {
            VipGrowthEngine.calculateCalendarMonthExpiry(now, months.coerceIn(1, 24))
        } else {
            now - 60_000L
        }
        val newGrowth = if (cfg != null) maxOf(user.vipGrowthPoints, cfg.growthThreshold) else 0L
        val nextRegDeadline = if (cfg != null && cfg.regulationDays > 0) {
            now + cfg.regulationDays * 86_400_000L
        } else {
            0L
        }
        userDao.upsertUser(
            user.copy(
                vipLevel = safeLevel,
                vipGrowthPoints = if (safeLevel == 0) 0L else newGrowth,
                vipActivatedAtMillis = if (safeLevel > 0) now else 0L,
                vipExpiryAtMillis = newExpiry,
                vipRegulationDeadlineMillis = nextRegDeadline,
                vipRegulationPointsEarned = 0L,
            )
        )
        recordAudit(
            actorName = actorName,
            actionType = "ADMIN_SET_USER_VIP",
            targetUserId = user.uid,
            summary = "Admin set ${user.nickname} -> VIP $safeLevel (active=$active, months=$months)",
        )
        return Result.success(Unit)
    }

    suspend fun adminAdjustGrowthPoints(
        delta: Long,
        reason: String,
        actorName: String = "Admin",
    ): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("User not loaded."))
        val configs = getLiveConfigs()
        val nextGrowth = (user.vipGrowthPoints + delta).coerceAtLeast(0L)
        val nextLevel = VipGrowthEngine.resolveLevelForGrowthPoints(nextGrowth, configs)
        val now = System.currentTimeMillis()
        userDao.upsertUser(
            user.copy(
                vipGrowthPoints = nextGrowth,
                vipLevel = nextLevel,
            )
        )
        vipDao.insertGrowthHistory(
            GrowthPointHistoryEntity(
                id = "gh_adm_${UUID.randomUUID()}",
                userId = user.uid,
                sourceCategoryName = DiamondSpendCategory.OTHER_EXCLUDED.name,
                diamondsSpent = 0L,
                pointsDelta = delta,
                regulationPointsDelta = if (delta > 0L) delta else 0L,
                balanceAfter = nextGrowth,
                levelBefore = user.vipLevel,
                levelAfter = nextLevel,
                description = "Admin Adjustment: $reason",
                referenceId = "adm_gp_${UUID.randomUUID()}",
                timestamp = now,
            )
        )
        recordAudit(
            actorName = actorName,
            actionType = "ADMIN_ADJUST_GROWTH",
            targetUserId = user.uid,
            summary = "Adjusted Growth Pts by $delta ($reason) -> $nextGrowth (VIP $nextLevel)",
        )
        return Result.success(Unit)
    }

    fun adminSaveVipGift(gift: VipGiftItem, actorName: String = "Admin") {
        val current = _vipGifts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == gift.id }
        if (idx >= 0) current[idx] = gift else current.add(0, gift)
        _vipGifts.value = current
        onAudit("Admin updated VIP Gift '${gift.name}' (VIP ${gift.requiredVipLevel}+)")
    }

    private suspend fun recordAudit(
        actorName: String,
        actionType: String,
        targetUserId: String,
        summary: String,
    ) {
        onAudit(summary)
        vipDao.insertAuditLog(
            VipAuditLogEntity(
                id = "val_${UUID.randomUUID()}",
                actorName = actorName,
                actionType = actionType,
                targetUserId = targetUserId,
                summary = summary,
                timestamp = System.currentTimeMillis(),
            )
        )
    }

    private fun VipTierConfig.toEntity() = VipConfigEntity(
        level = level,
        name = name,
        badgeTitle = badgeTitle,
        growthThreshold = growthThreshold,
        regulationDays = regulationDays,
        regulationTarget = regulationTarget,
        growthToDeduct = growthToDeduct,
        dailyDiamondBonus = dailyDiamondBonus,
        monthlyDiamondSummary = monthlyDiamondSummary,
        friendLimitIncrement = friendLimitIncrement,
        nicknameColorTierName = nicknameColorTier.name,
        nameplateTier = nameplateTier,
        chatBubbleTier = chatBubbleTier,
        voiceBubbleTier = voiceBubbleTier,
        entranceEffectTitle = entranceEffectTitle,
        monthlySubscriptionCoins = monthlySubscriptionCoins,
        accentHex = accentHex,
        secondaryAccentHex = secondaryAccentHex,
        privilegesPipe = privileges.joinToString("|"),
        privilegeKeysPipe = privilegeKeys.joinToString("|"),
    )

    private fun VipConfigEntity.toDomain() = VipTierConfig(
        level = level,
        name = name,
        badgeTitle = badgeTitle,
        growthThreshold = growthThreshold,
        regulationDays = regulationDays,
        regulationTarget = regulationTarget,
        growthToDeduct = growthToDeduct,
        dailyDiamondBonus = dailyDiamondBonus,
        monthlyDiamondSummary = monthlyDiamondSummary,
        friendLimitIncrement = friendLimitIncrement,
        nicknameColorTier = runCatching { VipNicknameColorTier.valueOf(nicknameColorTierName) }
            .getOrDefault(VipNicknameColorTier.RED),
        nameplateTier = nameplateTier,
        chatBubbleTier = chatBubbleTier,
        voiceBubbleTier = voiceBubbleTier,
        entranceEffectTitle = entranceEffectTitle,
        monthlySubscriptionCoins = monthlySubscriptionCoins,
        accentHex = accentHex,
        secondaryAccentHex = secondaryAccentHex,
        privileges = privilegesPipe.split("|").filter { it.isNotBlank() },
        privilegeKeys = privilegeKeysPipe.split("|").filter { it.isNotBlank() },
    )

    private fun GrowthPointHistoryEntity.toDomain() = GrowthPointHistoryEntry(
        id = id,
        userId = userId,
        sourceCategory = runCatching { DiamondSpendCategory.valueOf(sourceCategoryName) }
            .getOrDefault(DiamondSpendCategory.OTHER_EXCLUDED),
        diamondsSpent = diamondsSpent,
        pointsDelta = pointsDelta,
        regulationPointsDelta = regulationPointsDelta,
        balanceAfter = balanceAfter,
        levelBefore = levelBefore,
        levelAfter = levelAfter,
        description = description,
        referenceId = referenceId,
        timestamp = timestamp,
    )

    private fun VipPaymentHistoryEntity.toDomain() = VipPaymentHistoryEntry(
        id = id,
        userId = userId,
        planTitle = planTitle,
        months = months,
        targetLevel = targetLevel,
        amountDisplay = amountDisplay,
        coinsSpent = coinsSpent,
        status = status,
        referenceId = referenceId,
        timestamp = timestamp,
    )

    private fun VipAuditLogEntity.toDomain() = VipAuditLogEntry(
        id = id,
        actorName = actorName,
        actionType = actionType,
        targetUserId = targetUserId,
        summary = summary,
        timestamp = timestamp,
    )
}
