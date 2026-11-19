package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.GrowthPointHistoryEntry
import com.example.data.model.UserProfile
import com.example.data.model.VipGiftItem
import com.example.data.model.VipMembershipState
import com.example.data.model.VipPaymentHistoryEntry
import com.example.data.model.VipTierConfig
import com.example.data.repository.VipCatalog
import com.example.data.repository.VipGrowthEngine
import com.example.ui.components.VipCenterDiamond3DIcon
import com.example.ui.components.VipEntrancePreviewCard
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText

@Composable
fun VipCenterScreen(
    user: UserProfile,
    vipConfigs: List<VipTierConfig> = VipCatalog.defaultTiers,
    membershipState: VipMembershipState? = null,
    growthHistory: List<GrowthPointHistoryEntry> = emptyList(),
    paymentHistory: List<VipPaymentHistoryEntry> = emptyList(),
    vipGifts: List<VipGiftItem> = VipCatalog.defaultVipGifts,
    onBack: () -> Unit,
    onActivateVip: (level: Int, cost: Long, name: String) -> Unit,
    onClaimDailyDiamonds: () -> Unit = {},
    onToggleAutoRenewal: (Boolean) -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onCheckRegulation: () -> Unit = {},
    onSpendDiamondsForGrowth: (DiamondSpendCategory, Long, String) -> Unit = { _, _, _ -> },
    onSendVipGift: (VipGiftItem) -> Unit = {},
    onOpenAdminPanel: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val sortedTiers = remember(vipConfigs) { vipConfigs.sortedBy { it.level } }
    val resolvedMembership = remember(user, sortedTiers, membershipState) {
        membershipState ?: VipGrowthEngine.buildMembershipState(
            user = user,
            configs = sortedTiers,
            nowMillis = System.currentTimeMillis(),
            todayEpochDay = 0L,
        )
    }
    var selectedLevel by remember(resolvedMembership.currentLevel) {
        mutableIntStateOf(resolvedMembership.currentLevel.coerceIn(1, 12))
    }
    val selectedTier = remember(sortedTiers, selectedLevel) {
        sortedTiers.firstOrNull { it.level == selectedLevel } ?: sortedTiers.first()
    }
    var entranceAnimationPlaying by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VipDarkBg, Color(0xFF140B29), VipDarkBg)))
            .testTag("vip_center_screen")
    ) {
        // Dark Luxury Top Bar (isolated inside VIP Center)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).testTag("vip_center_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "WeFun VIP Center",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = VipGold,
                )
            }
            Surface(
                onClick = onOpenAdminPanel,
                shape = RoundedCornerShape(50),
                color = VipDarkCard,
                border = BorderStroke(1.dp, VipGold.copy(alpha = 0.45f)),
                modifier = Modifier.testTag("vip_open_admin_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Rounded.AdminPanelSettings, contentDescription = "Admin Panel", tint = VipGold, modifier = Modifier.size(16.dp))
                    Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold)
                }
            }
        }
        HorizontalDivider(color = VipDarkCardBorder)

        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("vip_center_scroll_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTION 1: My VIP Membership
            item(key = "sec_1_membership") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2B1055), Color(0xFF4C1D95), Color(0xFFB45309))
                            )
                        )
                        .border(1.5.dp, VipGold.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
                        .padding(18.dp)
                        .testTag("vip_section_membership")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            VipCenterDiamond3DIcon(size = 56.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "My VIP Membership",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VipGoldSoft
                                )
                                VipNicknameText(
                                    name = user.nickname,
                                    vipLevel = resolvedMembership.currentLevel,
                                    style = MaterialTheme.typography.titleLarge,
                                    onDarkBackground = true,
                                    customTitle = user.customTitle,
                                )
                                Text(
                                    text = if (resolvedMembership.isActive) {
                                        "Active VIP ${resolvedMembership.currentLevel} • Expires ${formatVipMinute(resolvedMembership.expiryAtMillis)}"
                                    } else {
                                        "VIP ${resolvedMembership.currentLevel} Inactive • ${user.coins} Coins • ${user.diamonds} Diamonds"
                                    },
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.88f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = onClaimDailyDiamonds,
                                shape = RoundedCornerShape(50),
                                color = if (resolvedMembership.canClaimDailyDiamonds) VipGold else Color(0xFF281845),
                                modifier = Modifier.weight(1f).testTag("vip_claim_daily_diamonds_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.Diamond,
                                        contentDescription = null,
                                        tint = if (resolvedMembership.canClaimDailyDiamonds) VipDarkBg else VipGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (resolvedMembership.canClaimDailyDiamonds) {
                                            "Claim +${resolvedMembership.dailyDiamondBonus} Diamonds"
                                        } else {
                                            "Daily Claimed"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (resolvedMembership.canClaimDailyDiamonds) VipDarkBg else Color.White,
                                    )
                                }
                            }
                            Surface(
                                onClick = {
                                    onSpendDiamondsForGrowth(
                                        DiamondSpendCategory.SHOP_LUCKY_DRAW,
                                        100L,
                                        "VIP Lucky Spin"
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF1F123A),
                                border = BorderStroke(1.dp, VipCyan.copy(alpha = 0.6f)),
                                modifier = Modifier.testTag("vip_lucky_draw_btn")
                            ) {
                                Text(
                                    text = "Lucky Draw (100💎)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VipCyan,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 2: Current VIP Badge
            item(key = "sec_2_badge") {
                VipLuxuryCard(modifier = Modifier.testTag("vip_section_badge")) {
                    VipSectionHeader(
                        number = 2,
                        title = "Current VIP Badge",
                        subtitle = "Active nameplate & multi-tier metallic crest"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            VipLevelBadge3D(vipLevel = resolvedMembership.currentLevel.coerceAtLeast(1))
                            Text(
                                text = sortedTiers.firstOrNull { it.level == resolvedMembership.currentLevel }?.badgeTitle
                                    ?: "Starter Crest",
                                fontSize = 12.sp,
                                color = VipGoldSoft,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Wallet Balance", fontSize = 10.sp, color = VipMuted)
                            Text("${user.coins} Coins • ${user.diamonds} 💎", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // SECTION 3: All 12 VIP Levels Selector
            item(key = "sec_3_all_levels") {
                VipLuxuryCard(modifier = Modifier.testTag("vip_section_levels")) {
                    VipSectionHeader(
                        number = 3,
                        title = "All 12 VIP Levels",
                        subtitle = "Tap any tier (VIP 1–12) to inspect privileges, growth & effects"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sortedTiers, key = { it.level }) { tier ->
                            val isSelected = selectedLevel == tier.level
                            val isCurrent = resolvedMembership.currentLevel == tier.level
                            Surface(
                                onClick = { selectedLevel = tier.level },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF2F1957) else Color(0xFF1C1033),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) VipGold else if (isCurrent) VipCyan else VipDarkCardBorder
                                ),
                                modifier = Modifier.testTag("vip_tier_chip_${tier.level}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    VipLevelBadge3D(vipLevel = tier.level, compact = true)
                                    Text(
                                        text = if (tier.level == 1) "1 Pt" else "%,d".format(tier.growthThreshold),
                                        fontSize = 10.sp,
                                        color = VipGoldSoft,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isCurrent) "Current" else "${tier.monthlySubscriptionCoins}c/mo",
                                        fontSize = 9.sp,
                                        color = if (isCurrent) VipCyan else VipMuted
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedTier.name} — ${selectedTier.badgeTitle}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Threshold: %,d Growth Pts • Regulation: ${if (selectedTier.regulationDays == 0) "Exempt" else "${selectedTier.regulationTarget} pts / ${selectedTier.regulationDays}d"}".format(selectedTier.growthThreshold),
                                fontSize = 11.sp,
                                color = VipMuted
                            )
                        }
                        Surface(
                            onClick = {
                                onActivateVip(
                                    selectedTier.level,
                                    selectedTier.monthlySubscriptionCoins,
                                    selectedTier.name
                                )
                            },
                            shape = RoundedCornerShape(50),
                            color = VipGold,
                            modifier = Modifier.testTag("vip_activate_selected_btn")
                        ) {
                            Text(
                                text = if (resolvedMembership.currentLevel >= selectedTier.level && resolvedMembership.isActive) {
                                    "Extend (${selectedTier.monthlySubscriptionCoins}c)"
                                } else {
                                    "Activate (${selectedTier.monthlySubscriptionCoins}c)"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VipDarkBg,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // SECTION 4: Growth Points & History
            item(key = "sec_4_growth") {
                VipGrowthSection(membership = resolvedMembership, growthHistory = growthHistory)
            }

            // SECTION 5: VIP Privileges (for selected tier)
            item(key = "sec_5_privileges") {
                VipLuxuryCard(modifier = Modifier.testTag("vip_section_privileges")) {
                    VipSectionHeader(
                        number = 5,
                        title = "${selectedTier.name} Privileges",
                        subtitle = "${selectedTier.nameplateTier} • ${selectedTier.chatBubbleTier} • ${selectedTier.voiceBubbleTier}"
                    )
                    val isTierUnlocked = resolvedMembership.isActive && resolvedMembership.currentLevel >= selectedTier.level
                    selectedTier.privileges.forEach { priv ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF21133A))
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = if (isTierUnlocked) VipCyan else VipGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(priv, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                            Text(
                                text = if (isTierUnlocked) "Unlocked" else "VIP ${selectedTier.level}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTierUnlocked) VipCyan else VipGoldSoft
                            )
                        }
                    }
                }
            }

            // SECTION 6: Nickname Colors
            item(key = "sec_6_nickname_colors") {
                VipNicknameColorSection(user = user, selectedLevel = selectedLevel)
            }

            // SECTION 7: Entrance Effects Live Preview
            item(key = "sec_7_entrance") {
                VipLuxuryCard(modifier = Modifier.testTag("vip_section_entrance")) {
                    VipSectionHeader(
                        number = 7,
                        title = "Entrance Effects",
                        subtitle = "Live Voice Room entry animation preview for ${selectedTier.name}"
                    )
                    VipEntrancePreviewCard(
                        vipLevel = selectedTier.level,
                        nickname = user.nickname,
                        effectTitle = selectedTier.entranceEffectTitle,
                        isPlaying = entranceAnimationPlaying,
                        onReplay = { entranceAnimationPlaying = !entranceAnimationPlaying }
                    )
                }
            }

            // SECTION 8: VIP Gifts
            item(key = "sec_8_gifts") {
                VipGiftsShowcaseSection(
                    currentVipLevel = resolvedMembership.currentLevel,
                    isVipActive = resolvedMembership.isActive,
                    vipGifts = vipGifts,
                    onSendGift = onSendVipGift,
                )
            }

            // SECTION 9: Membership Duration
            item(key = "sec_9_duration") {
                VipDurationSection(membership = resolvedMembership)
            }

            // SECTION 10: Regulation / Maintenance Progress
            item(key = "sec_10_regulation") {
                VipRegulationSection(
                    membership = resolvedMembership,
                    onCheckRegulation = onCheckRegulation,
                )
            }

            // SECTION 11: Renewal Settings & Restore Purchase
            item(key = "sec_11_renewal") {
                VipRenewalSection(
                    membership = resolvedMembership,
                    onToggleAutoRenewal = onToggleAutoRenewal,
                    onRestorePurchases = onRestorePurchases,
                )
            }

            // SECTION 12: Payment History
            item(key = "sec_12_payments") {
                VipPaymentHistorySection(paymentHistory = paymentHistory)
            }

            // SECTION 13: VIP FAQ & Official Rules
            item(key = "sec_13_faq") {
                VipFaqSection(
                    configs = sortedTiers,
                    faqs = VipCatalog.vipFaqs,
                    onRestorePurchases = onRestorePurchases,
                )
            }
        }
    }
}
