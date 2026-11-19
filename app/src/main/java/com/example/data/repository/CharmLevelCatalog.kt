package com.example.data.repository

import com.example.data.local.GuardRelationshipEntity
import com.example.data.local.OwnedSpecialEffectEntity
import com.example.data.local.ReceivedGiftEntity
import com.example.data.model.ActiveLevel
import com.example.data.model.CharmLevel
import com.example.data.model.CharmProgress
import com.example.data.model.ProfileBadge
import com.example.data.model.ProfileBadgeType
import com.example.data.model.SpecialEffect
import com.example.data.model.SpecialEffectCategory
import com.example.data.model.UserProfile
import java.text.NumberFormat
import java.util.Locale

object CharmLevelCatalog {

    /**
     * Authoritative cumulative 21-level WeFun Charm tier table.
     */
    val charmLevels: List<CharmLevel> = listOf(
        CharmLevel(1, 1_000L, "NOVICE", "Silver Crest", 0xFFCBD5E1, 0xFF94A3B8, 0xFFE2E8F0),
        CharmLevel(2, 4_000L, "RISING", "Silver Crest", 0xFFE2E8F0, 0xFF64748B, 0xFFF1F5F9),
        CharmLevel(3, 12_000L, "EXPLORER", "Silver Crest", 0xFFC084FC, 0xFF94A3B8, 0xFFD8B4FE),
        CharmLevel(4, 30_000L, "SUPPORTER", "Sapphire Gem", 0xFF38BDF8, 0xFF0284C7, 0xFF7DD3FC),
        CharmLevel(5, 80_000L, "VALUED", "Sapphire Gem", 0xFF0EA5E9, 0xFF2563EB, 0xFF38BDF8),
        CharmLevel(6, 160_000L, "TRUSTED", "Sapphire Crown", 0xFF38BDF8, 0xFF7C3AED, 0xFF67E8F9),
        CharmLevel(7, 300_000L, "ELITE", "Amethyst Aura", 0xFFC026D3, 0xFF7E22CE, 0xFFE879F9),
        CharmLevel(8, 500_000L, "PRO", "Amethyst Aura", 0xFFA855F7, 0xFF6D28D9, 0xFFD8B4FE),
        CharmLevel(9, 1_000_000L, "PRESTIGE", "Radiant Gold", 0xFFFBBF24, 0xFFD97706, 0xFFFDE047),
        CharmLevel(10, 2_000_000L, "SENIOR", "Radiant Gold", 0xFFF59E0B, 0xFFDB2777, 0xFFFDE68A),
        CharmLevel(11, 3_500_000L, "MASTER", "Crimson Ruby", 0xFFEF4444, 0xFFB91C1C, 0xFFFCA5A5),
        CharmLevel(12, 6_000_000L, "EXPERT", "Crimson Ruby", 0xFFF43F5E, 0xFFEA580C, 0xFFFDA4AF),
        CharmLevel(13, 8_500_000L, "LEGEND", "Mythic Violet", 0xFFA855F7, 0xFF4F46E5, 0xFFC084FC),
        CharmLevel(14, 12_000_000L, "MYTHIC", "Mythic Violet", 0xFF9333EA, 0xFF2563EB, 0xFFE879F9),
        CharmLevel(15, 16_000_000L, "SUPREME", "Mythic Violet", 0xFFC026D3, 0xFFFBBF24, 0xFFF0ABFC),
        CharmLevel(16, 26_000_000L, "ROYAL", "Royal Gold Crown", 0xFFFBBF24, 0xFF9333EA, 0xFFFDE047),
        CharmLevel(17, 48_000_000L, "EMPEROR", "Royal Gold Crown", 0xFFF59E0B, 0xFFDC2626, 0xFFFEF08A),
        CharmLevel(18, 86_000_000L, "SOVEREIGN", "Royal Gold Crown", 0xFFEF4444, 0xFFFBBF24, 0xFFFDE047),
        CharmLevel(19, 120_000_000L, "TITAN", "Celestial Crown", 0xFFEC4899, 0xFF9333EA, 0xFFF9A8D4),
        CharmLevel(20, 240_000_000L, "IMMORTAL", "Celestial Crown", 0xFFD946EF, 0xFFFBBF24, 0xFFF5D0FE),
        CharmLevel(21, 360_000_000L, "ULTIMATE", "Ultimate Sovereign", 0xFFFBBF24, 0xFFD946EF, 0xFFFFEA70),
    )

    val activeLevels: List<ActiveLevel> = listOf(
        ActiveLevel(1, "Dawn Spark", 1, 0xFF38BDF8, "Daily check-in aura & Starter shimmer"),
        ActiveLevel(2, "Pulse Regular", 7, 0xFF00C2B8, "Custom mic wave glow & Chat entrance tag"),
        ActiveLevel(3, "Stage Star", 21, 0xFF8B5CF6, "Priority room spotlight & 10% XP bonus"),
        ActiveLevel(4, "Royal Patron", 45, 0xFFF59E0B, "Golden profile shimmer & Special entry ribbon"),
        ActiveLevel(5, "Eternal Legend", 90, 0xFFD946EF, "Mythic halo aura & VIP priority greeting"),
    )

    val defaultEffectsCatalog: List<SpecialEffect> = listOf(
        SpecialEffect(
            id = "fx_frame_silver_nova",
            title = "Nova Crystal Frame",
            category = SpecialEffectCategory.PROFILE_FRAME,
            description = "Luminous silver-violet geometric profile ring.",
            requiredCharmLevel = 1,
            requiredActiveLevel = 1,
            isOwned = false,
            isActive = false,
            accentHex = 0xFFCBD5E1,
            secondaryHex = 0xFF8B5CF6,
        ),
        SpecialEffect(
            id = "fx_entry_starlight",
            title = "Starlight Entry Beam",
            category = SpecialEffectCategory.ROOM_ENTRY_EFFECT,
            description = "Radiant cyan particle entrance when stepping into any Voice Room.",
            requiredCharmLevel = 2,
            requiredActiveLevel = 1,
            isOwned = false,
            isActive = false,
            accentHex = 0xFF38BDF8,
            secondaryHex = 0xFF2563EB,
        ),
        SpecialEffect(
            id = "fx_deco_amethyst_wing",
            title = "Amethyst Crest Halo",
            category = SpecialEffectCategory.VISUAL_DECORATION,
            description = "Floating violet crystal sparkles displayed around your profile card.",
            requiredCharmLevel = 3,
            requiredActiveLevel = 2,
            isOwned = false,
            isActive = false,
            accentHex = 0xFFC026D3,
            secondaryHex = 0xFF7E22CE,
        ),
        SpecialEffect(
            id = "fx_frame_royal_gold",
            title = "Royal Sovereign Frame",
            category = SpecialEffectCategory.PROFILE_FRAME,
            description = "Forged 24K gold double-halo portrait frame with ember glints.",
            requiredCharmLevel = 9,
            requiredActiveLevel = 3,
            isOwned = false,
            isActive = false,
            accentHex = 0xFFFBBF24,
            secondaryHex = 0xFFDB2777,
        ),
        SpecialEffect(
            id = "fx_entry_imperial_dragon",
            title = "Imperial Gold Arrival",
            category = SpecialEffectCategory.ROOM_ENTRY_EFFECT,
            description = "Full-banner royal gold announcement upon entering Auction or VIP rooms.",
            requiredCharmLevel = 16,
            requiredActiveLevel = 4,
            isOwned = false,
            isActive = false,
            accentHex = 0xFFF59E0B,
            secondaryHex = 0xFF9333EA,
        ),
        SpecialEffect(
            id = "fx_deco_ultimate_crown",
            title = "Ultimate Crown Aura",
            category = SpecialEffectCategory.VISUAL_DECORATION,
            description = "Signature Level 21 magenta-gold sovereign crown aura.",
            requiredCharmLevel = 21,
            requiredActiveLevel = 5,
            isOwned = false,
            isActive = false,
            accentHex = 0xFFD946EF,
            secondaryHex = 0xFFFBBF24,
        ),
    )

    fun calculateCharmProgress(totalCharm: Long): CharmProgress {
        val safeCharm = totalCharm.coerceAtLeast(0L)
        val reached = charmLevels.lastOrNull { safeCharm >= it.thresholdPoints }
        val currentLevel = reached?.level ?: 0
        val currentTitle = reached?.title ?: "UNRANKED"
        val currentThreshold = reached?.thresholdPoints ?: 0L
        val nextTier = charmLevels.firstOrNull { it.level == currentLevel + 1 }

        return if (nextTier == null) {
            CharmProgress(
                totalCharm = safeCharm,
                currentLevel = currentLevel,
                currentTierTitle = currentTitle,
                nextLevel = null,
                nextThreshold = null,
                remainingNeeded = 0L,
                fractionToNext = 1f,
            )
        } else {
            val span = (nextTier.thresholdPoints - currentThreshold).coerceAtLeast(1L)
            val earnedInSpan = (safeCharm - currentThreshold).coerceAtLeast(0L)
            val fraction = (earnedInSpan.toFloat() / span.toFloat()).coerceIn(0f, 1f)
            CharmProgress(
                totalCharm = safeCharm,
                currentLevel = currentLevel,
                currentTierTitle = currentTitle,
                nextLevel = nextTier.level,
                nextThreshold = nextTier.thresholdPoints,
                remainingNeeded = (nextTier.thresholdPoints - safeCharm).coerceAtLeast(0L),
                fractionToNext = fraction,
            )
        }
    }

    fun formatThreshold(value: Long): String =
        NumberFormat.getNumberInstance(Locale.US).format(value)

    fun buildProfileBadges(user: UserProfile, charmProgress: CharmProgress): List<ProfileBadge> {
        return listOf(
            ProfileBadge(
                id = "b_host",
                title = "Voice Host",
                tierLabel = "Lv.${user.hostLevel}",
                badgeType = ProfileBadgeType.HOST_VERIFIED,
                description = "Verified live room stage creator.",
                isUnlocked = user.isHostVerified || user.hostLevel >= 3,
            ),
            ProfileBadge(
                id = "b_charm",
                title = charmProgress.currentTierTitle,
                tierLabel = if (charmProgress.currentLevel > 0) "Charm ${charmProgress.currentLevel}" else "Starter",
                badgeType = ProfileBadgeType.TOP_CHARM,
                description = "Cumulative Charm progression shield.",
                isUnlocked = charmProgress.currentLevel >= 1,
            ),
            ProfileBadge(
                id = "b_social",
                title = "Social Circle",
                tierLabel = "VIP ${user.vipLevel}",
                badgeType = ProfileBadgeType.SOCIAL_STAR,
                description = "Earned from community followers & active rooms.",
                isUnlocked = user.followersCount >= 500 || user.vipLevel >= 1,
            ),
            ProfileBadge(
                id = "b_room",
                title = "Room Patron",
                tierLabel = "Lv.${user.userLevel}",
                badgeType = ProfileBadgeType.ROOM_OWNER,
                description = "Active WeFun party host & supporter.",
                isUnlocked = user.userLevel >= 10,
            ),
        )
    }

    fun seedReceivedGifts(): List<ReceivedGiftEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // Own user (usr_nova_01) received gifts
            ReceivedGiftEntity("usr_nova_01_g_crown", "usr_nova_01", "g_crown", "Royal Crown", "👑", 199, 10, 6, "VIP", 0xFFFBBF24, now - 3600_000L * 2),
            ReceivedGiftEntity("usr_nova_01_g_rose", "usr_nova_01", "g_rose", "Crystal Rose", "🌹", 10, 1, 18, "ROSES", 0xFFF43F5E, now - 3600_000L * 5),
            ReceivedGiftEntity("usr_nova_01_g_ring", "usr_nova_01", "g_ring", "Starlight Ring", "💍", 99, 5, 4, "ROMANTIC", 0xFF00C2B8, now - 3600_000L * 11),
            ReceivedGiftEntity("usr_nova_01_g_phoenix", "usr_nova_01", "g_phoenix", "Astral Phoenix", "🔥", 1200, 60, 2, "FANTASY", 0xFFA855F7, now - 86400_000L),
            // DJ Rhea
            ReceivedGiftEntity("host_rhea_g_dragon", "host_rhea", "g_dragon", "Cosmic Dragon", "🐉", 2500, 125, 3, "DRAGONS", 0xFFFBBF24, now - 1800_000L),
            ReceivedGiftEntity("host_rhea_g_crown", "host_rhea", "g_crown", "Royal Crown", "👑", 199, 10, 12, "VIP", 0xFFFBBF24, now - 7200_000L),
            ReceivedGiftEntity("host_rhea_g_bouquet", "host_rhea", "g_bouquet", "Velvet Bouquet", "💐", 39, 2, 24, "FLOWERS", 0xFFEC4899, now - 14400_000L),
            // Kai Storm
            ReceivedGiftEntity("host_kai_g_lion", "host_kai", "g_lion", "Golden Lion", "🦁", 888, 44, 5, "GOLDEN_LIONS", 0xFFFBBF24, now - 3600_000L),
            ReceivedGiftEntity("host_kai_g_supercar", "host_kai", "g_supercar", "Hyper Roadster", "🏎️", 599, 30, 4, "SUPERCARS", 0xFFF43F5E, now - 10800_000L),
            // Zara Moon
            ReceivedGiftEntity("host_zara_g_teddy", "host_zara", "g_teddy", "Cosmic Bear", "🧸", 99, 5, 11, "ANIMALS", 0xFF38BDF8, now - 5400_000L),
            ReceivedGiftEntity("host_zara_g_rose", "host_zara", "g_rose", "Crystal Rose", "🌹", 10, 1, 30, "ROSES", 0xFFF43F5E, now - 18000_000L),
        )
    }

    fun seedGuards(): List<GuardRelationshipEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // Authorized Guards protecting Aria Vance (usr_nova_01)
            GuardRelationshipEntity("usr_nova_01_host_rhea", "usr_nova_01", "host_rhea", "DJ Rhea", 0xFF7B2CFF, 5, "Diamond Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 12),
            GuardRelationshipEntity("usr_nova_01_host_kai", "usr_nova_01", "host_kai", "Kai Storm", 0xFFFF4FA3, 6, "Royal Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 8),
            GuardRelationshipEntity("usr_nova_01_usr_kiran", "usr_nova_01", "usr_kiran", "Kiran Beats", 0xFF26B887, 3, "Silver Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 3),
            // Authorized Guards protecting DJ Rhea
            GuardRelationshipEntity("host_rhea_host_kai", "host_rhea", "host_kai", "Kai Storm", 0xFFFF4FA3, 6, "Royal Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 15),
            GuardRelationshipEntity("host_rhea_host_leo", "host_rhea", "host_leo", "Leo Beats", 0xFFFF8A2B, 5, "Gold Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 6),
            // Authorized Guard protecting Kai Storm
            GuardRelationshipEntity("host_kai_host_milo", "host_kai", "host_milo", "Milo Taktik", 0xFF3B82F6, 4, "Silver Guard", isAuthorized = true, isPrivateHidden = false, grantedAt = now - 86400_000L * 9),
        )
    }

    fun seedOwnedEffects(): List<OwnedSpecialEffectEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            OwnedSpecialEffectEntity("usr_nova_01_fx_frame_silver_nova", "usr_nova_01", "fx_frame_silver_nova", isOwned = true, isActive = true, unlockedAt = now - 86400_000L * 5),
            OwnedSpecialEffectEntity("usr_nova_01_fx_entry_starlight", "usr_nova_01", "fx_entry_starlight", isOwned = true, isActive = false, unlockedAt = now - 86400_000L * 3),
            OwnedSpecialEffectEntity("usr_nova_01_fx_deco_amethyst_wing", "usr_nova_01", "fx_deco_amethyst_wing", isOwned = true, isActive = false, unlockedAt = now - 86400_000L * 1),
            // Store-locked premium items explicitly NOT owned even if Charm is higher
            OwnedSpecialEffectEntity("usr_nova_01_fx_frame_royal_gold", "usr_nova_01", "fx_frame_royal_gold", isOwned = false, isActive = false, unlockedAt = 0L),
            OwnedSpecialEffectEntity("usr_nova_01_fx_entry_imperial_dragon", "usr_nova_01", "fx_entry_imperial_dragon", isOwned = false, isActive = false, unlockedAt = 0L),
            OwnedSpecialEffectEntity("usr_nova_01_fx_deco_ultimate_crown", "usr_nova_01", "fx_deco_ultimate_crown", isOwned = false, isActive = false, unlockedAt = 0L),
        )
    }
}
