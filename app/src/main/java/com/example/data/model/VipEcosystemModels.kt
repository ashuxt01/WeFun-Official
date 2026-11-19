package com.example.data.model

enum class VipNicknameColorTier(val label: String) {
    NONE("Standard"),
    RED("Red Nickname"),
    NOBLE_GOLD("Noble Gold"),
    RAINBOW("Bright Colorful"),
}

enum class DiamondSpendCategory(val label: String, val isEligibleForGrowth: Boolean) {
    GIFT("Give Gifts & Gift Cards", true),
    SHOP_LUCKY_DRAW("Shop & Lucky Draws", true),
    EVENT_LUCKY_DRAW("Event Lucky Draws", true),
    VOICE_ROOM_BINGO("Voice Room Bingo", true),
    OTHER_EXCLUDED("Other Spend", false),
}

data class VipPrivilege(
    val key: String,
    val title: String,
    val description: String,
    val minLevel: Int,
)

data class VipTierConfig(
    val level: Int,
    val name: String,
    val badgeTitle: String,
    val growthThreshold: Long,
    val regulationDays: Int,
    val regulationTarget: Long,
    val growthToDeduct: Long,
    val dailyDiamondBonus: Long,
    val monthlyDiamondSummary: Long,
    val friendLimitIncrement: Int,
    val nicknameColorTier: VipNicknameColorTier,
    val nameplateTier: String,
    val chatBubbleTier: String,
    val voiceBubbleTier: String,
    val entranceEffectTitle: String,
    val monthlySubscriptionCoins: Long,
    val accentHex: Long,
    val secondaryAccentHex: Long,
    val privileges: List<String>,
    val privilegeKeys: List<String>,
)

data class VipMembershipState(
    val userId: String,
    val currentLevel: Int,
    val isActive: Boolean,
    val growthPoints: Long,
    val nextLevel: Int?,
    val nextLevelThreshold: Long?,
    val pointsToNextLevel: Long,
    val activatedAtMillis: Long,
    val expiryAtMillis: Long,
    val autoRenewalEnabled: Boolean,
    val regulationPeriodDays: Int,
    val regulationTargetPoints: Long,
    val regulationEarnedPoints: Long,
    val regulationDeductionPoints: Long,
    val regulationDeadlineMillis: Long,
    val canClaimDailyDiamonds: Boolean,
    val dailyDiamondBonus: Long,
    val friendLimitBonus: Int,
    val nicknameColorTier: VipNicknameColorTier,
    val unlockedPrivilegeKeys: Set<String>,
)

data class GrowthPointHistoryEntry(
    val id: String,
    val userId: String,
    val sourceCategory: DiamondSpendCategory,
    val diamondsSpent: Long,
    val pointsDelta: Long,
    val regulationPointsDelta: Long,
    val balanceAfter: Long,
    val levelBefore: Int,
    val levelAfter: Int,
    val description: String,
    val referenceId: String,
    val timestamp: Long,
)

data class VipPaymentHistoryEntry(
    val id: String,
    val userId: String,
    val planTitle: String,
    val months: Int,
    val targetLevel: Int,
    val amountDisplay: String,
    val coinsSpent: Long,
    val status: String, // "APPLIED", "PENDING_RESTORE", "RESTORED"
    val referenceId: String,
    val timestamp: Long,
)

data class VipAuditLogEntry(
    val id: String,
    val actorName: String,
    val actionType: String,
    val targetUserId: String,
    val summary: String,
    val timestamp: Long,
)

data class VipFaqItem(
    val number: Int,
    val question: String,
    val answer: String,
    val includesGrowthTable: Boolean = false,
    val includesRelegationTable: Boolean = false,
    val includesPrivilegeTable: Boolean = false,
    val includesRestoreButton: Boolean = false,
)

data class VipGiftItem(
    val id: String,
    val name: String,
    val tierGroup: String, // "VIP Gifts I", "VIP Gifts II", "VIP Gifts III", "Mute Protection VIP Gift", "Custom Gift"
    val requiredVipLevel: Int,
    val diamondCost: Long,
    val coinCost: Long,
    val emoji: String,
    val effectSummary: String,
    val accentHex: Long,
    val grantsMuteProtectionSeconds: Int = 0,
)
