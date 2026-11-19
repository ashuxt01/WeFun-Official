package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_levels")
data class RoomLevelEntity(
    @PrimaryKey val level: Int,
    val seatCount: Int,
    val requiredContribution: Long,
    val badgeTitle: String,
    val unlockedDecorationsCsv: String,
)

@Entity(tableName = "room_seats")
data class RoomSeatEntity(
    @PrimaryKey val id: String, // "${roomId}_$seatIndex"
    val roomId: String,
    val seatIndex: Int,
    val occupantId: String?,
    val occupantName: String?,
    val isMuted: Boolean,
    val isMicLocked: Boolean,
    val isSeatLocked: Boolean,
    val charmCount: Long,
    val reservedForUserId: String?,
    val reservedForName: String?,
)

@Entity(tableName = "room_owners")
data class RoomOwnerEntity(
    @PrimaryKey val roomId: String,
    val userId: String,
    val displayName: String,
    val vipLevel: Int,
    val userLevel: Int,
    val avatarColorHex: Long,
)

@Entity(tableName = "room_partners")
data class RoomPartnerEntity(
    @PrimaryKey val roomId: String,
    val userId: String?,
    val displayName: String?,
    val vipLevel: Int,
    val userLevel: Int,
    val isLocked: Boolean,
    val isEnabled: Boolean = false,
    val canModerate: Boolean,
    val canManageSeats: Boolean,
)

@Entity(tableName = "room_admins")
data class RoomAdminEntity(
    @PrimaryKey val id: String, // "${roomId}_$userId"
    val roomId: String,
    val userId: String,
    val displayName: String,
    val roleName: String, // SUPER_ADMIN or ROOM_ADMIN
    val grantedBy: String,
    val grantedAt: Long,
)

@Entity(tableName = "room_moderators")
data class RoomModeratorEntity(
    @PrimaryKey val id: String, // "${roomId}_$userId"
    val roomId: String,
    val userId: String,
    val displayName: String,
    val grantedBy: String,
    val grantedAt: Long,
)

@Entity(tableName = "room_participants")
data class RoomParticipantEntity(
    @PrimaryKey val id: String, // "${roomId}_$userId"
    val roomId: String,
    val userId: String,
    val displayName: String,
    val roleName: String,
    val vipLevel: Int,
    val userLevel: Int,
    val isBanned: Boolean,
    val isMutedByAdmin: Boolean,
    val joinedAt: Long,
)

@Entity(tableName = "room_contributions")
data class RoomContributionEntity(
    @PrimaryKey val roomId: String,
    val currentLevel: Int,
    val totalContribution: Long,
    val todayContribution: Long,
    val weeklyContribution: Long,
    val monthlyContribution: Long,
    val pointsPerCoinRule: Double,
    val diamondsPerCoinRule: Double,
    val updatedAt: Long,
)

@Entity(tableName = "room_gift_transactions")
data class RoomGiftTransactionEntity(
    @PrimaryKey val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val giftId: String,
    val giftName: String,
    val giftEmoji: String,
    val coinValue: Long,
    val contributionPoints: Long,
    val receiverDiamondsEarned: Long,
    val timestamp: Long,
)

@Entity(tableName = "contributor_rankings")
data class ContributorRankingEntity(
    @PrimaryKey val id: String, // "${roomId}_${periodName}_$userId"
    val roomId: String,
    val userId: String,
    val displayName: String,
    val avatarColorHex: Long,
    val vipLevel: Int,
    val periodName: String, // TOP, TODAY, WEEKLY, MONTHLY
    val contributionPoints: Long,
    val totalGiftCoins: Long,
)

@Entity(tableName = "room_announcements")
data class RoomAnnouncementEntity(
    @PrimaryKey val id: String,
    val roomId: String,
    val categoryName: String,
    val title: String,
    val content: String,
    val authorName: String,
    val timestamp: Long,
)

@Entity(tableName = "room_settings")
data class RoomSettingsEntity(
    @PrimaryKey val roomId: String,
    val allowPublicMicRequests: Boolean,
    val musicAdminsOnly: Boolean,
    val isPartnerSeatEnabled: Boolean = false,
    val decorationStyle: String,
    val themeName: String,
    val minGiftCoinsForPoints: Long,
    val roomModeName: String = "NORMAL",
    val highQualityAudio: Boolean = true,
    val banTextChatting: Boolean = false,
    val banPhotoChatting: Boolean = false,
    val banRedPacket: Boolean = false,
    val membershipFeeCoins: Long = 0L,
    val updatedAt: Long,
)

@Entity(tableName = "room_activity_logs")
data class RoomActivityLogEntity(
    @PrimaryKey val id: String,
    val roomId: String,
    val actorName: String,
    val action: String,
    val detail: String,
    val timestamp: Long,
)
