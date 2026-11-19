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
import com.example.data.model.AnnouncementCategory
import com.example.data.model.ContributionPeriod
import com.example.data.model.MusicTrack
import com.example.data.model.RoomLevelConfig

object VoiceRoomSeedData {

    val defaultLevelConfigs: List<RoomLevelConfig> = listOf(
        RoomLevelConfig(1, 8, 0L, "Lv.1 Lounge", listOf("Charcoal Gold Standard", "Basic Welcome Card")),
        RoomLevelConfig(2, 8, 10_000L, "Lv.2 Vibe Stage", listOf("Purple Pulse Ring", "Stage Spotlight")),
        RoomLevelConfig(3, 8, 35_000L, "Lv.3 Starlight", listOf("Gold Ribbon Crest", "VIP Entry Spark")),
        RoomLevelConfig(4, 8, 100_000L, "Lv.4 Royal Club", listOf("Royal Velvet Aura", "Crown Mic Halo")),
        RoomLevelConfig(5, 8, 220_000L, "Lv.5 Astral Hall", listOf("Astral Halo Frame", "Neon Soundwave")),
        RoomLevelConfig(6, 8, 400_000L, "Lv.6 Diamond Suite", listOf("Diamond Waveform", "Prestige Crest")),
        RoomLevelConfig(7, 14, 650_000L, "Lv.7 Sovereign", listOf("Sovereign Gold Crest", "14-Mic Arena (6 Compact Seats)")),
        RoomLevelConfig(8, 14, 1_000_000L, "Lv.8 Imperial", listOf("Imperial Stage Glow", "Extended Arena Crest")),
        RoomLevelConfig(9, 14, 1_500_000L, "Lv.9 Celestial", listOf("Celestial Dragon Aura", "14-Mic Royal Frame")),
        RoomLevelConfig(10, 14, 2_500_000L, "Lv.10 Legend", listOf("WEFUN Apex Throne", "14-Mic Legend Arena")),
    )

    fun levelEntities(): List<RoomLevelEntity> = defaultLevelConfigs.map {
        RoomLevelEntity(
            level = it.level,
            seatCount = it.seatCount,
            requiredContribution = it.requiredContribution,
            badgeTitle = it.badgeTitle,
            unlockedDecorationsCsv = it.unlockedDecorations.joinToString("|"),
        )
    }

    val licensedTracks = listOf(
        MusicTrack("trk_1", "Midnight Neon Drift", "WEFUN Synth Collective", "03:18"),
        MusicTrack("trk_2", "Gold Horizon Acoustic", "Aria & Rhea Studio", "02:54"),
        MusicTrack("trk_3", "Purple Velvet Lo-Fi", "Cosmic Lo-Fi Pack", "04:05"),
        MusicTrack("trk_4", "Royal Arena Hype", "WEFUN Official Audio", "02:40"),
        MusicTrack("trk_5", "Starlight Chillwave", "Nova Beats Lab", "03:42"),
    )

    fun seedContributionsForRoom(roomId: String, level: Int, totalPoints: Long): RoomContributionEntity =
        RoomContributionEntity(
            roomId = roomId,
            currentLevel = level,
            totalContribution = totalPoints,
            todayContribution = (totalPoints * 0.14).toLong(),
            weeklyContribution = (totalPoints * 0.42).toLong(),
            monthlyContribution = (totalPoints * 0.78).toLong(),
            pointsPerCoinRule = 0.85,
            diamondsPerCoinRule = 0.50,
            updatedAt = System.currentTimeMillis(),
        )

    fun seedOwnerForRoom(
        roomId: String,
        hostId: String,
        hostName: String,
        hostVip: Int,
        hostUserLevel: Int = 24 + hostVip * 4,
    ): RoomOwnerEntity =
        RoomOwnerEntity(
            roomId = roomId,
            userId = hostId,
            displayName = hostName,
            vipLevel = hostVip,
            userLevel = hostUserLevel,
            avatarColorHex = 0xFFFFD700,
        )

    fun seedPartnerForRoom(roomId: String): RoomPartnerEntity =
        RoomPartnerEntity(
            roomId = roomId,
            userId = null,
            displayName = null,
            vipLevel = 0,
            userLevel = 1,
            isLocked = false,
            isEnabled = false,
            canModerate = true,
            canManageSeats = true,
        )

    fun seedAdminsForRoom(roomId: String, hostName: String): List<RoomAdminEntity> = listOf(
        RoomAdminEntity(
            id = "${roomId}_sp_2",
            roomId = roomId,
            userId = "sp_2",
            displayName = "Kabir Beats",
            roleName = "ROOM_ADMIN",
            grantedBy = hostName,
            grantedAt = System.currentTimeMillis() - 86400000L * 2,
        )
    )

    fun seedModeratorsForRoom(roomId: String, hostName: String): List<RoomModeratorEntity> = listOf(
        RoomModeratorEntity(
            id = "${roomId}_sp_3",
            roomId = roomId,
            userId = "sp_3",
            displayName = "Meera Sky",
            grantedBy = hostName,
            grantedAt = System.currentTimeMillis() - 86400000L,
        )
    )

    fun seedAnnouncements(roomId: String, hostName: String, defaultText: String): List<RoomAnnouncementEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            RoomAnnouncementEntity(
                id = "${roomId}_ann_1",
                roomId = roomId,
                categoryName = AnnouncementCategory.WELCOME.name,
                title = "Announcement",
                content = defaultText,
                authorName = hostName,
                timestamp = now - 3600_000L,
            ),
            RoomAnnouncementEntity(
                id = "${roomId}_ann_2",
                roomId = roomId,
                categoryName = AnnouncementCategory.RULES.name,
                title = "Room Rules",
                content = "Respect every speaker, keep mics clear during singing turns & abide by WEFUN Community Guidelines. 🙏✨",
                authorName = hostName,
                timestamp = now - 7200_000L,
            ),
        )
    }

    fun seedSettings(roomId: String, themeName: String, decoration: String): RoomSettingsEntity =
        RoomSettingsEntity(
            roomId = roomId,
            allowPublicMicRequests = true,
            musicAdminsOnly = false,
            isPartnerSeatEnabled = false,
            decorationStyle = decoration,
            themeName = themeName,
            minGiftCoinsForPoints = 10L,
            updatedAt = System.currentTimeMillis(),
        )

    fun seedRankings(roomId: String): List<ContributorRankingEntity> {
        val contributors = listOf(
            Triple("top_1", "Aarav Royal", 31_400L) to 0xFFFFD700L,
            Triple("top_2", "Elena Noir", 22_850L) to 0xFF6200EEL,
            Triple("top_3", "Vikram X", 12_900L) to 0xFF38BDF8L,
            Triple("top_4", "Sanya Star", 7_850L) to 0xFFF43F5EL,
        )
        val out = mutableListOf<ContributorRankingEntity>()
        ContributionPeriod.entries.forEach { period ->
            val factor = when (period) {
                ContributionPeriod.TOP -> 1.0
                ContributionPeriod.TODAY -> 0.18
                ContributionPeriod.WEEKLY -> 0.45
                ContributionPeriod.MONTHLY -> 0.80
            }
            contributors.forEachIndexed { idx, (triple, color) ->
                val pts = (triple.third * factor).toLong().coerceAtLeast(100L)
                out += ContributorRankingEntity(
                    id = "${roomId}_${period.name}_${triple.first}",
                    roomId = roomId,
                    userId = triple.first,
                    displayName = triple.second,
                    avatarColorHex = color,
                    vipLevel = (6 - idx).coerceAtLeast(2),
                    periodName = period.name,
                    contributionPoints = pts,
                    totalGiftCoins = (pts / 0.85).toLong(),
                )
            }
        }
        return out
    }

    fun seedGiftTransactions(roomId: String, hostId: String, hostName: String): List<RoomGiftTransactionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            RoomGiftTransactionEntity(
                id = "${roomId}_gtx_1",
                roomId = roomId,
                senderId = "top_1",
                senderName = "Aarav Royal",
                receiverId = hostId,
                receiverName = hostName,
                giftId = "g_dragon",
                giftName = "Cosmic Dragon",
                giftEmoji = "🐉",
                coinValue = 2500,
                contributionPoints = 2125,
                receiverDiamondsEarned = 1250,
                timestamp = now - 900_000L,
            ),
            RoomGiftTransactionEntity(
                id = "${roomId}_gtx_2",
                roomId = roomId,
                senderId = "top_2",
                senderName = "Elena Noir",
                receiverId = hostId,
                receiverName = hostName,
                giftId = "g_crown",
                giftName = "Royal Crown",
                giftEmoji = "👑",
                coinValue = 199,
                contributionPoints = 169,
                receiverDiamondsEarned = 100,
                timestamp = now - 2_400_000L,
            ),
        )
    }

    fun seedActivityLogs(roomId: String, hostName: String): List<RoomActivityLogEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            RoomActivityLogEntity("${roomId}_log_1", roomId, hostName, "Room Opened", "Voice stage initialized with Level rules", now - 3_600_000L),
            RoomActivityLogEntity("${roomId}_log_2", roomId, hostName, "Announcement Updated", "Pinned welcome message & rules", now - 1_800_000L),
        )
    }
}
