package com.example.data.model

enum class RoomRole(val label: String, val priority: Int) {
    OWNER("Owner", 100),
    PARTNER("Partner", 90),
    SUPER_ADMIN("Super Admin", 80),
    ROOM_ADMIN("Room Admin", 70),
    MODERATOR("Moderator", 60),
    VIP_MEMBER("VIP Member", 30),
    NORMAL_MEMBER("Member", 10);

    fun canModerate(): Boolean = priority >= MODERATOR.priority
    fun canManageRoom(): Boolean = priority >= ROOM_ADMIN.priority
    fun canManageRoles(): Boolean = priority >= SUPER_ADMIN.priority
    fun isOwnerOrPartner(): Boolean = this == OWNER || this == PARTNER
}

enum class ContributionPeriod(val label: String) {
    TOP("Top"),
    TODAY("Today"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
}

enum class AnnouncementCategory(val label: String, val emoji: String) {
    WELCOME("Welcome", "👋"),
    RULES("Room Rules", "📜"),
    EVENT("Event", "🎉"),
    FAMILY("Family", "🏰"),
    NOTICE("Special Notice", "📢"),
}

data class RoomLevelConfig(
    val level: Int,
    val seatCount: Int,
    val requiredContribution: Long,
    val badgeTitle: String,
    val unlockedDecorations: List<String>,
)

data class ContributionConversionRule(
    val pointsPerCoin: Double = 0.85,
    val diamondsPerCoin: Double = 0.50,
    val eligibleCategories: Set<GiftCategory> = GiftCategory.entries.toSet(),
    val minGiftCoinsForPoints: Long = 10L,
)

data class RoomOwnerInfo(
    val roomId: String,
    val userId: String,
    val displayName: String,
    val initials: String,
    val avatarColorHex: Long,
    val vipLevel: Int,
    val userLevel: Int,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val rmsLevel: Float = 0f,
    val charmCount: Long = 0L,
)

data class RoomPartnerInfo(
    val roomId: String,
    val userId: String?,
    val displayName: String?,
    val initials: String = "",
    val avatarColorHex: Long = 0xFFFFD700,
    val vipLevel: Int = 0,
    val userLevel: Int = 1,
    val isLocked: Boolean = false,
    val isEnabled: Boolean = false,
    val canModerate: Boolean = true,
    val canManageSeats: Boolean = true,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val rmsLevel: Float = 0f,
    val charmCount: Long = 0L,
    val relationshipDays: Int = 14,
    val partnerSinceLabel: String = "14d Together",
)

data class RoomMemberRoleEntry(
    val roomId: String,
    val userId: String,
    val displayName: String,
    val initials: String,
    val role: RoomRole,
    val vipLevel: Int,
    val userLevel: Int,
    val isOnline: Boolean = true,
    val isMutedByAdmin: Boolean = false,
    val isBanned: Boolean = false,
)

data class RoomContributionSummary(
    val roomId: String,
    val currentLevel: Int,
    val totalContribution: Long,
    val currentLevelThreshold: Long,
    val nextLevelThreshold: Long,
    val nextLevel: Int?,
    val seatCapacity: Int,
) {
    val progressFraction: Float
        get() {
            if (nextLevel == null || nextLevelThreshold <= currentLevelThreshold) return 1f
            val span = (nextLevelThreshold - currentLevelThreshold).coerceAtLeast(1L)
            val gained = (totalContribution - currentLevelThreshold).coerceAtLeast(0L)
            return (gained.toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }
}

data class ContributorRankEntry(
    val id: String,
    val roomId: String,
    val userId: String,
    val displayName: String,
    val initials: String,
    val avatarColorHex: Long,
    val vipLevel: Int,
    val period: ContributionPeriod,
    val contributionPoints: Long,
    val totalGiftCoins: Long,
    val rank: Int,
)

data class RoomGiftLedgerEntry(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val giftName: String,
    val giftEmoji: String,
    val coinValue: Long,
    val contributionPoints: Long,
    val receiverDiamondsEarned: Long,
    val timestamp: Long,
)

data class RoomAnnouncementEntry(
    val id: String,
    val roomId: String,
    val category: AnnouncementCategory,
    val title: String,
    val content: String,
    val authorName: String,
    val timestamp: Long,
)

data class RoomActivityLogEntry(
    val id: String,
    val roomId: String,
    val actorName: String,
    val action: String,
    val detail: String,
    val timestamp: Long,
)

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationLabel: String,
    val licenseNote: String = "WEFUN Licensed Synth",
)

data class SongRequestItem(
    val id: String,
    val track: MusicTrack,
    val requestedBy: String,
    val status: String = "Queued",
)

data class RoomLevelUpEvent(
    val oldLevel: Int,
    val newLevel: Int,
    val newSeatCount: Int,
    val unlockedDecorations: List<String>,
)
