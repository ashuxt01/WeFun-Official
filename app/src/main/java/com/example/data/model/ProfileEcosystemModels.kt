package com.example.data.model

enum class ProfileBadgeType {
    HOST_VERIFIED,
    TOP_CHARM,
    SOCIAL_STAR,
    ROOM_OWNER,
}

data class ProfileBadge(
    val id: String,
    val title: String,
    val tierLabel: String,
    val badgeType: ProfileBadgeType,
    val description: String,
    val isUnlocked: Boolean,
)

enum class GiftSortOrder(val label: String) {
    MOST_RECEIVED("Most Received"),
    HIGHEST_STARS("Highest Stars"),
    RECENT("Recent"),
}

data class ReceivedGift(
    val id: String,
    val ownerUid: String,
    val giftId: String,
    val giftName: String,
    val emoji: String,
    val coinValue: Long,
    val starsPerUnit: Long,
    val quantity: Int,
    val category: GiftCategory,
    val accentHex: Long,
    val lastReceivedAt: Long,
) {
    val totalStarsEarned: Long get() = starsPerUnit * quantity
}

data class GiftWallSummary(
    val ownerUid: String,
    val totalGifts: Int,
    val totalStars: Long,
    val featuredGifts: List<ReceivedGift>,
    val allGifts: List<ReceivedGift>,
)

data class GuardRelationship(
    val id: String,
    val ownerUid: String,
    val guardUid: String,
    val guardName: String,
    val guardAccentHex: Long,
    val guardLevel: Int,
    val tierLabel: String,
    val isAuthorized: Boolean,
    val isPrivateHidden: Boolean = false,
    val grantedAt: Long,
)

enum class RoomRelationType(val label: String) {
    OWNED("Host"),
    JOINED("Joined"),
    FOLLOWED("Followed"),
}

data class AdvancedRoomPreview(
    val roomId: String,
    val title: String,
    val hostId: String,
    val hostName: String,
    val category: RoomCategory,
    val roomMode: RoomMode,
    val roomLevel: Int,
    val onlineCount: Int,
    val popularity: Long,
    val relationshipType: RoomRelationType,
    val themePrimaryHex: Long,
    val themeSecondaryHex: Long,
)

data class GameStatSummary(
    val gameId: GameId,
    val gameTitle: String,
    val matchesPlayed: Int,
    val wins: Int,
    val bestScore: Int,
    val totalCoinsWon: Long,
    val accentHex: Long,
)

data class ProfileStatistics(
    val userId: String,
    val accountLevel: Int,
    val xp: Long,
    val nextLevelXp: Long,
    val charmPoints: Long,
    val charmLevel: Int,
    val followers: Int,
    val following: Int,
    val friends: Int,
    val giftsReceived: Int,
    val giftsSent: Int,
    val totalStars: Long,
    val totalMoments: Int,
    val roomsJoined: Int,
    val roomsHosted: Int,
    val gameStats: List<GameStatSummary>,
)

data class CharmLevel(
    val level: Int,
    val thresholdPoints: Long,
    val title: String,
    val tierGroup: String,
    val accentHex: Long,
    val secondaryHex: Long,
    val glowHex: Long,
)

data class CharmProgress(
    val totalCharm: Long,
    val currentLevel: Int,
    val currentTierTitle: String,
    val nextLevel: Int?,
    val nextThreshold: Long?,
    val remainingNeeded: Long,
    val fractionToNext: Float,
)

data class ActiveLevel(
    val level: Int,
    val title: String,
    val minActiveDays: Int,
    val accentHex: Long,
    val perkSummary: String,
)

enum class SpecialEffectCategory(val label: String) {
    PROFILE_FRAME("Profile Frame"),
    ROOM_ENTRY_EFFECT("Room Entry"),
    VISUAL_DECORATION("Decoration"),
}

data class SpecialEffect(
    val id: String,
    val title: String,
    val category: SpecialEffectCategory,
    val description: String,
    val requiredCharmLevel: Int,
    val requiredActiveLevel: Int,
    val isOwned: Boolean,
    val isActive: Boolean,
    val accentHex: Long,
    val secondaryHex: Long,
)

data class ProfileGiftSendResult(
    val gift: VirtualGift,
    val quantity: Int,
    val totalCoins: Long,
    val starsAdded: Long,
    val charmAdded: Long,
)
