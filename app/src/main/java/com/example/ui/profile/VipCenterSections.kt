package com.example.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GrowthPointHistoryEntry
import com.example.data.model.UserProfile
import com.example.data.model.VipFaqItem
import com.example.data.model.VipGiftItem
import com.example.data.model.VipMembershipState
import com.example.data.model.VipPaymentHistoryEntry
import com.example.data.model.VipTierConfig
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal val VipDarkBg = Color(0xFF0D071A)
internal val VipDarkCard = Color(0xFF160D29)
internal val VipDarkCardBorder = Color(0xFF3B2163)
internal val VipGold = Color(0xFFFFD700)
internal val VipGoldSoft = Color(0xFFFDE68A)
internal val VipCyan = Color(0xFF00E5FF)
internal val VipPurple = Color(0xFFA855F7)
internal val VipMuted = Color(0xFFB4A9CE)

private val vipMinuteFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

internal fun formatVipMinute(millis: Long): String {
    if (millis <= 0L) return "Not Activated"
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(vipMinuteFormatter)
}

@Composable
internal fun VipLuxuryCard(
    modifier: Modifier = Modifier,
    borderColor: Color = VipDarkCardBorder,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = VipDarkCard,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun VipSectionHeader(
    number: Int,
    title: String,
    subtitle: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = VipGold.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, VipGold.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "%02d".format(number),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VipGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VipMuted,
            )
        }
    }
}

@Composable
internal fun VipGrowthSection(
    membership: VipMembershipState,
    growthHistory: List<GrowthPointHistoryEntry>,
) {
    val nextThreshold = membership.nextLevelThreshold
    val progress = if (nextThreshold != null && nextThreshold > 0L) {
        (membership.growthPoints.toFloat() / nextThreshold.toFloat()).coerceIn(0f, 1f)
    } else 1f

    VipLuxuryCard(modifier = Modifier.testTag("vip_section_growth")) {
        VipSectionHeader(
            number = 4,
            title = "Growth Points & History",
            subtitle = "Earn 1 Growth Pt per 100 Diamonds spent while VIP is active"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${membership.growthPoints} Growth Pts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = VipGold
            )
            Text(
                text = if (membership.nextLevel != null) {
                    "Need ${membership.pointsToNextLevel} for VIP ${membership.nextLevel}"
                } else {
                    "Max VIP 12 Reached"
                },
                style = MaterialTheme.typography.labelMedium,
                color = VipCyan
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            color = VipGold,
            trackColor = Color(0xFF2B1B4A),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
        )
        Text(
            text = "Eligible: 1. Give gifts (incl. gift cards) • 2. Shop Purchases & Lucky Draws • 3. Event Lucky Draws • 4. Voice Room Bingo",
            fontSize = 11.sp,
            color = VipMuted
        )
        if (growthHistory.isEmpty()) {
            Text("No Growth Point transactions yet.", fontSize = 12.sp, color = VipMuted)
        } else {
            growthHistory.take(6).forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF21133A))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.description,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${entry.sourceCategory.label} • ${formatVipMinute(entry.timestamp)}",
                            fontSize = 10.sp,
                            color = VipMuted
                        )
                    }
                    Text(
                        text = if (entry.pointsDelta >= 0) "+${entry.pointsDelta}" else "${entry.pointsDelta}",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (entry.pointsDelta >= 0) VipCyan else Color(0xFFFF5E7E),
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun VipNicknameColorSection(user: UserProfile, selectedLevel: Int) {
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_nickname_colors")) {
        VipSectionHeader(
            number = 6,
            title = "Nickname Colors",
            subtitle = "Distinctive color hierarchy across VIP 1–12"
        )
        val tiers = listOf(
            Triple(2, "VIP 1–5 • Red Nickname", "VIP 1+"),
            Triple(6, "VIP 6–10 • Noble Gold Nickname", "VIP 6+"),
            Triple(12, "VIP 11–12 • Bright Colorful Rainbow", "VIP 11+"),
        )
        tiers.forEach { (lvl, caption, unlock) ->
            val highlighted = (selectedLevel in 1..5 && lvl == 2) ||
                (selectedLevel in 6..10 && lvl == 6) ||
                (selectedLevel >= 11 && lvl == 12)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (highlighted) Color(0xFF2A194A) else Color(0xFF1C1033))
                    .border(
                        width = if (highlighted) 1.dp else 0.5.dp,
                        color = if (highlighted) VipGold else VipDarkCardBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    VipNicknameText(
                        name = user.nickname,
                        vipLevel = lvl,
                        style = MaterialTheme.typography.titleMedium,
                        onDarkBackground = true,
                    )
                    Text(caption, fontSize = 11.sp, color = VipMuted)
                }
                VipLevelBadge3D(vipLevel = lvl, compact = true)
                Spacer(modifier = Modifier.width(6.dp))
                Text(unlock, fontSize = 10.sp, color = VipGoldSoft, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun VipGiftsShowcaseSection(
    currentVipLevel: Int,
    isVipActive: Boolean,
    vipGifts: List<VipGiftItem>,
    onSendGift: (VipGiftItem) -> Unit,
) {
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_gifts")) {
        VipSectionHeader(
            number = 8,
            title = "VIP Gifts",
            subtitle = "VIP Gifts I–III, Custom Gifts (VIP 10+) & Mute Protection Gift (VIP 12)"
        )
        vipGifts.forEach { gift ->
            val unlocked = isVipActive && currentVipLevel >= gift.requiredVipLevel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF21133A))
                    .border(1.dp, Color(gift.accentHex).copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(gift.emoji, fontSize = 26.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(gift.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(gift.accentHex).copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = gift.tierGroup,
                                fontSize = 9.sp,
                                color = Color(gift.accentHex),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(gift.effectSummary, fontSize = 11.sp, color = VipMuted, maxLines = 2)
                    Text(
                        text = "Requires VIP ${gift.requiredVipLevel}+ • ${gift.diamondCost} Diamonds",
                        fontSize = 11.sp,
                        color = VipGoldSoft,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    onClick = { onSendGift(gift) },
                    shape = RoundedCornerShape(50),
                    color = if (unlocked) VipGold else Color(0xFF332252),
                    modifier = Modifier.testTag("vip_send_gift_${gift.id}")
                ) {
                    Text(
                        text = if (unlocked) "Send" else "VIP ${gift.requiredVipLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (unlocked) VipDarkBg else VipMuted,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun VipDurationSection(
    membership: VipMembershipState,
) {
    // Section 9: Membership Duration
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_duration")) {
        VipSectionHeader(
            number = 9,
            title = "Membership Duration",
            subtitle = "Calendar-month accuracy to the minute (e.g. May 20 15:24 → Jun 20 15:24)"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Activated", fontSize = 11.sp, color = VipMuted)
                Text(formatVipMinute(membership.activatedAtMillis), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(if (membership.isActive) "Expires" else "Expired", fontSize = 11.sp, color = VipMuted)
                Text(
                    text = formatVipMinute(membership.expiryAtMillis),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (membership.isActive) VipCyan else Color(0xFFFF5E7E)
                )
            }
        }
    }
}

@Composable
internal fun VipRegulationSection(
    membership: VipMembershipState,
    onCheckRegulation: () -> Unit,
) {
    // Section 10: Regulation / Maintenance Progress
    val regProgress = if (membership.regulationTargetPoints > 0L) {
        (membership.regulationEarnedPoints.toFloat() / membership.regulationTargetPoints.toFloat()).coerceIn(0f, 1f)
    } else 1f
    val daysRemaining = if (membership.regulationDeadlineMillis > System.currentTimeMillis()) {
        ((membership.regulationDeadlineMillis - System.currentTimeMillis()) / 86_400_000L).coerceAtLeast(0L)
    } else 0L

    VipLuxuryCard(modifier = Modifier.testTag("vip_section_regulation")) {
        VipSectionHeader(
            number = 10,
            title = "Regulation / Maintenance Progress",
            subtitle = "1 Relegation Pt = 1 Growth Pt • Applies even outside membership period"
        )
        if (membership.currentLevel <= 1) {
            Text(
                text = "VIP 1 is exempt from relegation deductions.",
                fontSize = 13.sp,
                color = VipCyan,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${membership.regulationEarnedPoints} / ${membership.regulationTargetPoints} Pts",
                    fontWeight = FontWeight.ExtraBold,
                    color = VipGold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${daysRemaining}d left (${membership.regulationPeriodDays}d cycle)",
                    fontSize = 12.sp,
                    color = VipMuted
                )
            }
            LinearProgressIndicator(
                progress = { regProgress },
                color = if (regProgress >= 1f) Color(0xFF10B981) else VipPurple,
                trackColor = Color(0xFF2B1B4A),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
            Text(
                text = "Miss penalty: -${membership.regulationDeductionPoints} Growth Pts if target isn't met.",
                fontSize = 11.sp,
                color = Color(0xFFFCA5A5)
            )
            Surface(
                onClick = onCheckRegulation,
                shape = RoundedCornerShape(50),
                color = Color(0xFF2A194A),
                border = BorderStroke(1.dp, VipPurple),
                modifier = Modifier.testTag("vip_check_regulation_btn")
            ) {
                Text(
                    text = "Evaluate Regulation Status",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
internal fun VipRenewalSection(
    membership: VipMembershipState,
    onToggleAutoRenewal: (Boolean) -> Unit,
    onRestorePurchases: () -> Unit,
) {
    // Section 11: Renewal Settings & Restore Purchase
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_renewal")) {
        VipSectionHeader(
            number = 11,
            title = "Renewal Settings & Restore Purchase",
            subtitle = "Auto-renewal bills 24h before expiration • Cancel anytime"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Automatic Renewal", fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = if (membership.autoRenewalEnabled) "Enabled (charges 24h before expiry)" else "Manual renewal only",
                    fontSize = 11.sp,
                    color = VipMuted
                )
            }
            Switch(
                checked = membership.autoRenewalEnabled,
                onCheckedChange = onToggleAutoRenewal,
                colors = SwitchDefaults.colors(checkedTrackColor = VipGold, checkedThumbColor = VipDarkBg),
                modifier = Modifier.testTag("vip_auto_renew_switch")
            )
        }
        Surface(
            onClick = onRestorePurchases,
            shape = RoundedCornerShape(50),
            color = VipGold,
            modifier = Modifier.fillMaxWidth().testTag("vip_restore_purchase_button")
        ) {
            Row(
                modifier = Modifier.padding(vertical = 11.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Restore, contentDescription = null, tint = VipDarkBg, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Restore Purchase", fontWeight = FontWeight.ExtraBold, color = VipDarkBg, fontSize = 14.sp)
            }
        }
    }
}

@Composable
internal fun VipPaymentHistorySection(
    paymentHistory: List<VipPaymentHistoryEntry>,
) {
    // Section 12: Payment History
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_payments")) {
        VipSectionHeader(
            number = 12,
            title = "Payment History",
            subtitle = "Verified VIP subscriptions and pending store receipts"
        )
        if (paymentHistory.isEmpty()) {
            Text("No VIP payment history yet.", fontSize = 12.sp, color = VipMuted)
        } else {
            paymentHistory.take(8).forEach { pay ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF21133A))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pay.planTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${pay.amountDisplay} • ${formatVipMinute(pay.timestamp)}", fontSize = 11.sp, color = VipMuted)
                    }
                    val statusColor = when (pay.status) {
                        "RESTORED" -> VipCyan
                        "PENDING_RESTORE" -> VipGold
                        else -> Color(0xFF10B981)
                    }
                    Surface(shape = RoundedCornerShape(50), color = statusColor.copy(alpha = 0.18f)) {
                        Text(
                            text = pay.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun VipFaqSection(
    configs: List<VipTierConfig>,
    faqs: List<VipFaqItem>,
    onRestorePurchases: () -> Unit,
) {
    // Section 13: VIP FAQ (All 8 Questions + Tables + Restore Button)
    VipLuxuryCard(modifier = Modifier.testTag("vip_section_faq")) {
        VipSectionHeader(
            number = 13,
            title = "VIP FAQ & Official Rules",
            subtitle = "Growth thresholds, relegation schedule, privileges & cancellation guide"
        )
        faqs.forEach { faq ->
            var expanded by remember(faq.number) { mutableStateOf(faq.number in listOf(3, 4, 6)) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF201238))
                    .border(0.8.dp, VipDarkCardBorder, RoundedCornerShape(12.dp))
                    .clickable { expanded = !expanded }
                    .padding(12.dp)
                    .testTag("vip_faq_item_${faq.number}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = faq.question,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = VipGold
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(faq.answer, fontSize = 12.sp, color = VipMuted)

                        if (faq.includesGrowthTable) {
                            VipFaqGrowthTable(configs = configs)
                        }
                        if (faq.includesRelegationTable) {
                            VipFaqRelegationTable(configs = configs)
                        }
                        if (faq.includesPrivilegeTable) {
                            VipFaqPrivilegeTable(configs = configs)
                        }
                        if (faq.includesRestoreButton) {
                            Surface(
                                onClick = onRestorePurchases,
                                shape = RoundedCornerShape(50),
                                color = VipGold,
                                modifier = Modifier.fillMaxWidth().testTag("vip_faq_restore_btn")
                            ) {
                                Box(modifier = Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                                    Text("Restore Purchase", fontWeight = FontWeight.ExtraBold, color = VipDarkBg, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VipFaqGrowthTable(configs: List<VipTierConfig>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF140B26))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Levels", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold)
            Text("Growth Pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold)
        }
        HorizontalDivider(color = VipDarkCardBorder)
        configs.sortedBy { it.level }.forEach { cfg ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("VIP${cfg.level}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(if (cfg.level == 1) "\\" else "%,d".format(cfg.growthThreshold), fontSize = 11.sp, color = VipCyan)
            }
        }
    }
}

@Composable
private fun VipFaqRelegationTable(configs: List<VipTierConfig>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF140B26))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Level", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VipGold, modifier = Modifier.weight(0.9f))
            Text("Period", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VipGold, modifier = Modifier.weight(1f))
            Text("Target", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VipGold, modifier = Modifier.weight(1f))
            Text("Deducted", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VipGold, modifier = Modifier.weight(1.1f))
        }
        HorizontalDivider(color = VipDarkCardBorder)
        configs.sortedBy { it.level }.forEach { cfg ->
            Row(Modifier.fillMaxWidth()) {
                Text("VIP${cfg.level}", fontSize = 10.sp, color = Color.White, modifier = Modifier.weight(0.9f))
                Text(if (cfg.regulationDays == 0) "\\" else "${cfg.regulationDays} Days", fontSize = 10.sp, color = VipMuted, modifier = Modifier.weight(1f))
                Text(if (cfg.regulationTarget == 0L) "\\" else "%,d".format(cfg.regulationTarget), fontSize = 10.sp, color = VipCyan, modifier = Modifier.weight(1f))
                Text(if (cfg.growthToDeduct == 0L) "\\" else "%,d".format(cfg.growthToDeduct), fontSize = 10.sp, color = Color(0xFFFCA5A5), modifier = Modifier.weight(1.1f))
            }
        }
    }
}

@Composable
private fun VipFaqPrivilegeTable(configs: List<VipTierConfig>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF140B26))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        configs.sortedBy { it.level }.forEach { cfg ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(cfg.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold)
                Text(
                    text = cfg.privileges.mapIndexed { idx, p -> "${idx + 1}. $p" }.joinToString("\n"),
                    fontSize = 10.sp,
                    color = VipMuted
                )
            }
            HorizontalDivider(color = VipDarkCardBorder.copy(alpha = 0.6f))
        }
    }
}
