package com.example.data.repository

import com.example.data.model.DiamondSpendCategory
import com.example.data.model.UserProfile
import com.example.data.model.VipMembershipState
import com.example.data.model.VipNicknameColorTier
import com.example.data.model.VipTierConfig
import java.time.Instant
import java.time.ZoneId

data class DiamondSpendOutcome(
    val growthPointsEarned: Long,
    val newGrowthPoints: Long,
    val levelBefore: Int,
    val levelAfter: Int,
    val leveledUp: Boolean,
    val newRegulationDeadlineMillis: Long,
    val newRegulationEarnedPoints: Long,
)

data class RegulationEvaluationResult(
    val wasEvaluated: Boolean,
    val maintained: Boolean,
    val periodsEvaluated: Int,
    val totalDeducted: Long,
    val levelBefore: Int,
    val levelAfter: Int,
    val newGrowthPoints: Long,
    val newRegulationDeadlineMillis: Long,
    val newRegulationEarnedPoints: Long,
    val summaryMessage: String,
)

object VipGrowthEngine {

    private const val DAY_MILLIS = 86_400_000L

    fun isVipActive(expiryAtMillis: Long, nowMillis: Long): Boolean {
        return expiryAtMillis > nowMillis
    }

    /**
     * Calendar-month minute-accurate duration calculation.
     * Example: May 20 at 15:24 + 1 month -> June 20 at 15:24.
     * Month-end overflows (e.g. Jan 31 -> Feb 28/29) follow ZonedDateTime.plusMonths cleanly.
     */
    fun calculateCalendarMonthExpiry(
        startMillis: Long,
        months: Int,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Long {
        val safeMonths = months.coerceAtLeast(1)
        val start = Instant.ofEpochMilli(startMillis).atZone(zoneId)
        return start.plusMonths(safeMonths.toLong()).toInstant().toEpochMilli()
    }

    fun calculateGrowthPointsEarned(
        diamondsSpent: Long,
        isVipActive: Boolean,
        category: DiamondSpendCategory,
    ): Long {
        if (!isVipActive || !category.isEligibleForGrowth || diamondsSpent <= 0L) return 0L
        return diamondsSpent / 100L
    }

    fun resolveLevelForGrowthPoints(
        growthPoints: Long,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): Int {
        if (growthPoints <= 0L) return 0
        val sorted = configs.sortedBy { it.level }
        var achieved = 0
        for (tier in sorted) {
            if (growthPoints >= tier.growthThreshold) {
                achieved = tier.level
            }
        }
        return achieved.coerceIn(0, 12)
    }

    fun nicknameColorTierForLevel(vipLevel: Int): VipNicknameColorTier {
        return when {
            vipLevel in 1..5 -> VipNicknameColorTier.RED
            vipLevel in 6..10 -> VipNicknameColorTier.NOBLE_GOLD
            vipLevel >= 11 -> VipNicknameColorTier.RAINBOW
            else -> VipNicknameColorTier.NONE
        }
    }

    fun cumulativeFriendLimitBonus(
        vipLevel: Int,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): Int {
        if (vipLevel <= 0) return 0
        return configs.filter { it.level <= vipLevel }.sumOf { it.friendLimitIncrement }
    }

    fun dailyDiamondBonus(
        vipLevel: Int,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): Long {
        if (vipLevel <= 0) return 0L
        return configs.firstOrNull { it.level == vipLevel }?.dailyDiamondBonus ?: 60L
    }

    fun unlockedPrivilegeKeys(
        vipLevel: Int,
        isVipActive: Boolean,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): Set<String> {
        if (!isVipActive || vipLevel <= 0) return emptySet()
        return configs.filter { it.level <= vipLevel }.flatMap { it.privilegeKeys }.toSet()
    }

    fun hasPrivilege(
        vipLevel: Int,
        isVipActive: Boolean,
        privilegeKey: String,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): Boolean = privilegeKey in unlockedPrivilegeKeys(vipLevel, isVipActive, configs)

    fun processDiamondSpend(
        currentGrowthPoints: Long,
        currentVipLevel: Int,
        isVipActive: Boolean,
        diamondsSpent: Long,
        category: DiamondSpendCategory,
        currentRegulationDeadlineMillis: Long,
        currentRegulationEarnedPoints: Long,
        nowMillis: Long,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): DiamondSpendOutcome {
        val earned = calculateGrowthPointsEarned(diamondsSpent, isVipActive, category)
        val newGrowth = (currentGrowthPoints + earned).coerceAtLeast(0L)
        val newLevel = if (isVipActive) {
            maxOf(currentVipLevel, resolveLevelForGrowthPoints(newGrowth, configs))
        } else {
            currentVipLevel
        }
        val leveledUp = newLevel > currentVipLevel
        val newConfig = configs.firstOrNull { it.level == newLevel }
        val nextDeadline = when {
            leveledUp && (newConfig?.regulationDays ?: 0) > 0 ->
                nowMillis + newConfig!!.regulationDays * DAY_MILLIS
            leveledUp -> 0L
            else -> currentRegulationDeadlineMillis
        }
        val nextRegEarned = if (leveledUp) 0L else currentRegulationEarnedPoints + earned
        return DiamondSpendOutcome(
            growthPointsEarned = earned,
            newGrowthPoints = newGrowth,
            levelBefore = currentVipLevel,
            levelAfter = newLevel,
            leveledUp = leveledUp,
            newRegulationDeadlineMillis = nextDeadline,
            newRegulationEarnedPoints = nextRegEarned,
        )
    }

    /**
     * Evaluates elapsed regulation windows. Applies even outside active VIP membership period
     * as specified in VIP Regulation Rules.
     */
    fun evaluateRegulation(
        currentGrowthPoints: Long,
        currentVipLevel: Int,
        regulationDeadlineMillis: Long,
        regulationEarnedPoints: Long,
        nowMillis: Long,
        configs: List<VipTierConfig> = VipCatalog.defaultTiers,
    ): RegulationEvaluationResult {
        if (currentVipLevel <= 1 || regulationDeadlineMillis <= 0L || nowMillis < regulationDeadlineMillis) {
            return RegulationEvaluationResult(
                wasEvaluated = false,
                maintained = true,
                periodsEvaluated = 0,
                totalDeducted = 0L,
                levelBefore = currentVipLevel,
                levelAfter = currentVipLevel,
                newGrowthPoints = currentGrowthPoints,
                newRegulationDeadlineMillis = regulationDeadlineMillis,
                newRegulationEarnedPoints = regulationEarnedPoints,
                summaryMessage = "Regulation period still active or exempt (VIP 1).",
            )
        }

        var workingGrowth = currentGrowthPoints
        var workingLevel = currentVipLevel
        var workingDeadline = regulationDeadlineMillis
        var workingEarned = regulationEarnedPoints
        var totalDeducted = 0L
        var periods = 0
        var lastMaintained = true

        while (workingLevel > 1 && workingDeadline > 0L && nowMillis >= workingDeadline && periods < 24) {
            val tier = configs.firstOrNull { it.level == workingLevel } ?: break
            if (tier.regulationDays <= 0) break
            periods++
            if (workingEarned >= tier.regulationTarget) {
                lastMaintained = true
            } else {
                lastMaintained = false
                val deduction = minOf(workingGrowth, tier.growthToDeduct)
                workingGrowth = (workingGrowth - tier.growthToDeduct).coerceAtLeast(0L)
                totalDeducted += deduction
                workingLevel = resolveLevelForGrowthPoints(workingGrowth, configs)
            }
            val nextTier = configs.firstOrNull { it.level == workingLevel }
            workingEarned = 0L
            workingDeadline = if (nextTier != null && nextTier.regulationDays > 0) {
                workingDeadline + nextTier.regulationDays * DAY_MILLIS
            } else {
                0L
            }
        }

        val summary = when {
            periods == 0 -> "No regulation period elapsed."
            totalDeducted == 0L -> "Maintained VIP $workingLevel during regulation check!"
            else -> "Missed regulation target: deducted $totalDeducted Growth Pts (VIP $currentVipLevel → VIP $workingLevel)."
        }

        return RegulationEvaluationResult(
            wasEvaluated = periods > 0,
            maintained = lastMaintained,
            periodsEvaluated = periods,
            totalDeducted = totalDeducted,
            levelBefore = currentVipLevel,
            levelAfter = workingLevel,
            newGrowthPoints = workingGrowth,
            newRegulationDeadlineMillis = workingDeadline,
            newRegulationEarnedPoints = workingEarned,
            summaryMessage = summary,
        )
    }

    fun buildMembershipState(
        user: UserProfile,
        configs: List<VipTierConfig>,
        nowMillis: Long,
        todayEpochDay: Long,
    ): VipMembershipState {
        val sorted = configs.sortedBy { it.level }
        val active = isVipActive(user.vipExpiryAtMillis, nowMillis) && user.vipLevel > 0
        val currentCfg = sorted.firstOrNull { it.level == user.vipLevel }
        val nextCfg = sorted.firstOrNull { it.level == (user.vipLevel + 1) }
        val nextThreshold = nextCfg?.growthThreshold
        val ptsNeeded = if (nextThreshold != null) (nextThreshold - user.vipGrowthPoints).coerceAtLeast(0L) else 0L
        return VipMembershipState(
            userId = user.uid,
            currentLevel = user.vipLevel,
            isActive = active,
            growthPoints = user.vipGrowthPoints,
            nextLevel = nextCfg?.level,
            nextLevelThreshold = nextThreshold,
            pointsToNextLevel = ptsNeeded,
            activatedAtMillis = user.vipActivatedAtMillis,
            expiryAtMillis = user.vipExpiryAtMillis,
            autoRenewalEnabled = user.vipAutoRenewal,
            regulationPeriodDays = currentCfg?.regulationDays ?: 0,
            regulationTargetPoints = currentCfg?.regulationTarget ?: 0L,
            regulationEarnedPoints = user.vipRegulationPointsEarned,
            regulationDeductionPoints = currentCfg?.growthToDeduct ?: 0L,
            regulationDeadlineMillis = user.vipRegulationDeadlineMillis,
            canClaimDailyDiamonds = active && user.vipLastDailyClaimEpochDay < todayEpochDay,
            dailyDiamondBonus = dailyDiamondBonus(user.vipLevel, sorted),
            friendLimitBonus = cumulativeFriendLimitBonus(user.vipLevel, sorted),
            nicknameColorTier = nicknameColorTierForLevel(if (active || user.vipLevel > 0) user.vipLevel else 0),
            unlockedPrivilegeKeys = unlockedPrivilegeKeys(user.vipLevel, active, sorted),
        )
    }
}
