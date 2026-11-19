package com.example.ui.rooms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.ContributionPeriod
import com.example.data.model.ContributorRankEntry
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomGiftLedgerEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.VoiceRoom
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.VipBadge
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomInfoAndLevelsSheet(
    room: VoiceRoom,
    owner: RoomOwnerInfo?,
    partner: RoomPartnerInfo?,
    memberRoles: List<RoomMemberRoleEntry>,
    levelConfigs: List<RoomLevelConfig>,
    contributionSummary: RoomContributionSummary?,
    activityLogs: List<RoomActivityLogEntry>,
    onOpenContributions: () -> Unit,
    onDismiss: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    val currentLevel = contributionSummary?.currentLevel ?: room.roomLevel
    val totalPoints = contributionSummary?.totalContribution ?: room.totalContribution
    val nextThreshold = contributionSummary?.nextLevelThreshold ?: totalPoints
    val nextLevel = contributionSummary?.nextLevel
    val progress = contributionSummary?.progressFraction ?: 0.25f
    val createdDate = remember(room.createdAtMillis) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(room.createdAtMillis))
    }
    val admins = memberRoles.filter {
        it.role == RoomRole.SUPER_ADMIN || it.role == RoomRole.ROOM_ADMIN || it.role == RoomRole.MODERATOR
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(room.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Room ID: ${room.id} • Created $createdDate • ${room.decorationStyle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = wefunColors.mutedText
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GoldAccent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, GoldAccent)
                    ) {
                        Text(
                            text = "Room Lv.$currentLevel",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Verified Contribution Progress Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = GoldAccent.copy(alpha = 0.55f),
                    onClick = onOpenContributions
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Room Level $currentLevel", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                            Text("Tap for Leaderboard →", style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                        }
                        Text(
                            text = if (nextLevel != null) {
                                "Total Contribution: ${formatNum(totalPoints)} / ${formatNum(nextThreshold)}"
                            } else {
                                "Total Contribution: ${formatNum(totalPoints)} (Max Level 10)"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            color = GoldAccent,
                            trackColor = wefunColors.glassBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )
                        Text(
                            text = if (nextLevel != null) {
                                "Next Upgrade: Level $nextLevel • Validated automatically from eligible gifts"
                            } else {
                                "Apex Level 10 unlocked with full 14-seat capacity"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = wefunColors.mutedText
                        )
                    }
                }
            }

            // Owner, Partner & Administrators Roster
            item {
                Text("Leadership & Staff", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (owner != null) {
                            RoleRosterRow(
                                name = owner.displayName,
                                initials = owner.initials,
                                roleLabel = "Room Owner",
                                accent = GoldAccent,
                                vipLevel = owner.vipLevel
                            )
                        }
                        if (partner?.isEnabled == true || partner?.userId != null) {
                            RoleRosterRow(
                                name = partner.displayName ?: "Open Partner Slot",
                                initials = partner.initials.ifBlank { "PT" },
                                roleLabel = if (partner.userId != null) "Room Partner" else "Partner Position (Owner Assignable)",
                                accent = PurplePrimary,
                                vipLevel = partner.vipLevel
                            )
                        }
                        admins.forEach { admin ->
                            RoleRosterRow(
                                name = admin.displayName,
                                initials = admin.initials,
                                roleLabel = admin.role.label,
                                accent = CyanPulse,
                                vipLevel = admin.vipLevel
                            )
                        }
                    }
                }
            }

            // Level 1-10 Progression Roadmap
            item {
                Text("Room Level 1–10 Progression", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Upgrades unlock additional microphone seats and exclusive decorations automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = wefunColors.mutedText
                )
            }

            items(levelConfigs, key = { it.level }) { cfg ->
                val unlocked = currentLevel >= cfg.level
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (unlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        width = if (currentLevel == cfg.level) 1.5.dp else 1.dp,
                        color = if (currentLevel == cfg.level) wefunColors.gold else if (unlocked) PurplePrimary.copy(alpha = 0.45f) else wefunColors.glassBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (unlocked) wefunColors.gold.copy(alpha = 0.18f) else wefunColors.glassBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = if (unlocked) wefunColors.gold else wefunColors.mutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Level ${cfg.level} • ${cfg.seatCount} Seats",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (unlocked) MaterialTheme.colorScheme.onSurface else wefunColors.mutedText
                                )
                                Text(
                                    text = "${cfg.badgeTitle} • ${cfg.unlockedDecorations.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (unlocked) PurplePrimary else wefunColors.mutedText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Text(
                            text = "${formatNum(cfg.requiredContribution)} pts",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (unlocked) EmeraldMint else wefunColors.mutedText
                        )
                    }
                }
            }

            // Recent Room History
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.History, null, tint = CyanPulse, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Recent Room History", style = MaterialTheme.typography.titleMedium)
                }
            }
            items(activityLogs.take(8), key = { it.id }) { log ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, wefunColors.glassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("${log.action} • ${log.actorName}", style = MaterialTheme.typography.labelMedium, color = PurplePrimary)
                        Text(log.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomContributionSheet(
    room: VoiceRoom,
    summary: RoomContributionSummary?,
    rankings: List<ContributorRankEntry>,
    giftLedger: List<RoomGiftLedgerEntry>,
    onDismiss: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    var tabIndex by remember { mutableIntStateOf(0) }
    var selectedPeriod by remember { mutableStateOf(ContributionPeriod.TOP) }

    val currentLevel = summary?.currentLevel ?: room.roomLevel
    val totalContribution = summary?.totalContribution ?: room.totalContribution
    val nextThreshold = summary?.nextLevelThreshold ?: totalContribution
    val nextLevel = summary?.nextLevel
    val progress = summary?.progressFraction ?: 0.3f

    val filteredRanks = remember(rankings, selectedPeriod) {
        rankings.filter { it.period == selectedPeriod }.sortedBy { it.rank }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.EmojiEvents, null, tint = wefunColors.gold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Room Contribution & Rankings", style = MaterialTheme.typography.titleLarge)
            }

            // Room Level & Progress Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, wefunColors.gold.copy(alpha = 0.55f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Room Level $currentLevel (${room.maxSeats} Seats)", style = MaterialTheme.typography.titleSmall, color = PurplePrimary)
                        Text(
                            text = if (nextLevel != null) "Next: Lv.$nextLevel" else "MAX",
                            style = MaterialTheme.typography.labelMedium,
                            color = wefunColors.cyan
                        )
                    }
                    Text(
                        text = if (nextLevel != null) {
                            "Total Contribution: ${formatNum(totalContribution)} / ${formatNum(nextThreshold)}"
                        } else {
                            "Total Contribution: ${formatNum(totalContribution)}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        color = wefunColors.gold,
                        trackColor = wefunColors.glassBorder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Text(
                        text = "Accounting Note: Gift coin value, room contribution points (0.85x), receiver diamonds (0.50x), and user wallet balances are stored in separate verified records.",
                        style = MaterialTheme.typography.labelSmall,
                        color = wefunColors.mutedText
                    )
                }
            }

            TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Contributors") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Gift Ledger") })
            }

            if (tabIndex == 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ContributionPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { Text(period.label) }
                        )
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    if (filteredRanks.isEmpty()) {
                        item {
                            Text(
                                text = "Send a virtual gift in this room to become #1 on the ${selectedPeriod.label} leaderboard!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = wefunColors.mutedText,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                    } else {
                        items(filteredRanks, key = { it.id }) { entry ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = if (entry.rank == 1) GoldAccent.copy(alpha = 0.65f) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "#${entry.rank}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = when (entry.rank) {
                                                1 -> GoldAccent
                                                2 -> CyanPulse
                                                3 -> RoseNeon
                                                else -> wefunColors.mutedText
                                            },
                                            modifier = Modifier.width(34.dp)
                                        )
                                        AvatarCircle(
                                            initials = entry.initials,
                                            accentColor = Color(entry.avatarColorHex),
                                            size = 40.dp,
                                            vipLevel = entry.vipLevel
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(entry.displayName, style = MaterialTheme.typography.titleSmall)
                                                VipBadge(vipLevel = entry.vipLevel)
                                            }
                                            Text(
                                                text = "Gift Value: ${formatNum(entry.totalGiftCoins)} Coins",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = wefunColors.mutedText
                                            )
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = formatNum(entry.contributionPoints),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = GoldAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Pts", style = MaterialTheme.typography.labelSmall, color = wefunColors.mutedText)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(giftLedger, key = { it.id }) { tx ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, wefunColors.glassBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${tx.giftEmoji} ${tx.giftName}", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = "${tx.senderName} → ${tx.receiverName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = wefunColors.cyan
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${tx.coinValue} Coins", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        text = "+${tx.contributionPoints} Pts • +${tx.receiverDiamondsEarned} 💎",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PurplePrimary
                                    )
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
private fun RoleRosterRow(
    name: String,
    initials: String,
    roleLabel: String,
    accent: Color,
    vipLevel: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(initials = initials, accentColor = accent, size = 36.dp, vipLevel = vipLevel)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(name, style = MaterialTheme.typography.titleSmall)
                Text(roleLabel, style = MaterialTheme.typography.labelSmall, color = accent)
            }
        }
        if (vipLevel > 0) {
            VipBadge(vipLevel = vipLevel)
        }
    }
}

internal fun formatNum(value: Long): String = NumberFormat.getNumberInstance(Locale.US).format(value)
