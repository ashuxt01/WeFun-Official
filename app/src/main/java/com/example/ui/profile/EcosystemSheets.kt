package com.example.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.WefunAppSettings
import com.example.data.model.AdminRole
import com.example.data.model.AvatarConfig
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.NotificationItem
import com.example.data.model.RechargePackage
import com.example.data.model.SafetyReport
import com.example.data.model.UserProfile
import com.example.data.model.WalletTransaction
import com.example.data.repository.WefunSeedData
import com.example.ui.components.GlassCard
import com.example.ui.components.VipBadge
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletSheet(
    user: UserProfile,
    transactions: List<WalletTransaction>,
    onRecharge: (RechargePackage) -> Unit,
    onDismiss: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Virtual Wallet & Ledger", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Coins and Diamonds are virtual in-app entertainment currencies with no real-world monetary value.",
                    style = MaterialTheme.typography.bodySmall,
                    color = wefunColors.mutedText
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    GlassCard(modifier = Modifier.weight(1f), borderColor = RoyalGold.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Rounded.MonetizationOn, null, tint = RoyalGold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${user.coins}", style = MaterialTheme.typography.headlineMedium, color = RoyalGold)
                            Text("Coins Balance", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        }
                    }
                    GlassCard(modifier = Modifier.weight(1f), borderColor = CyanPulse.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Rounded.Diamond, null, tint = CyanPulse)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${user.diamonds}", style = MaterialTheme.typography.headlineMedium, color = CyanPulse)
                            Text("Diamonds • ${user.bonusPoints} Bonus", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        }
                    }
                }
            }
            item {
                Text("Recharge Packages (Demo Play Billing Sandbox)", style = MaterialTheme.typography.titleMedium)
            }
            items(WefunSeedData.rechargePackages, key = { it.id }) { pkg ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().testTag("recharge_${pkg.id}"),
                    onClick = { onRecharge(pkg) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🪙 ${pkg.coins} Coins", style = MaterialTheme.typography.titleSmall)
                                if (pkg.badge != null) {
                                    Surface(shape = RoundedCornerShape(50), color = RoyalGold.copy(alpha = 0.2f)) {
                                        Text(pkg.badge, style = MaterialTheme.typography.labelSmall, color = RoyalGold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text("+${pkg.bonusCoins} Bonus Coins • Duplicate-protected", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
                        }
                        Surface(shape = RoundedCornerShape(50), color = ElectricViolet) {
                            Text(
                                text = pkg.priceDisplay,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            item {
                Text("Verified Transaction History", style = MaterialTheme.typography.titleMedium)
            }
            items(transactions, key = { it.id }) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(wefunColors.glassSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.description, style = MaterialTheme.typography.bodyMedium)
                        Text("${tx.type.label} • Ref: ${tx.referenceId.take(14)}", style = MaterialTheme.typography.labelSmall, color = wefunColors.mutedText)
                    }
                    Text(
                        text = "${if (tx.isCredit) "+" else "-"}${tx.amount} ${tx.currency.label}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (tx.isCredit) wefunColors.emerald else RoseNeon
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipMembershipSheet(
    user: UserProfile,
    onPurchaseVip: (Int, Long, String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = RoyalGold)
                    Text("VIP & SVIP Memberships", style = MaterialTheme.typography.headlineMedium)
                }
                Text("Current tier: VIP ${user.vipLevel} • Frame: ${user.avatarConfig.frameName}", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
            }
            items(WefunSeedData.vipTiers, key = { it.level }) { tier ->
                val accent = Color(tier.accentHex)
                val owned = user.vipLevel >= tier.level
                GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = accent.copy(alpha = 0.55f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                VipBadge(vipLevel = tier.level)
                                Text(tier.name, style = MaterialTheme.typography.titleMedium)
                            }
                            Text("${tier.monthlyCoins} Coins/mo", style = MaterialTheme.typography.labelLarge, color = RoyalGold)
                        }
                        Text("Frame: ${tier.frameTitle} • Entry: ${tier.entryEffect}", style = MaterialTheme.typography.bodySmall, color = accent)
                        Text("Chat Bubble: ${tier.chatBubble}", style = MaterialTheme.typography.bodySmall)
                        tier.privileges.forEach { priv ->
                            Text("• $priv", style = MaterialTheme.typography.bodySmall, color = LocalWefunColors.current.mutedText)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        WefunGradientButton(
                            text = if (owned) "Unlocked (Renew ${tier.monthlyCoins})" else "Activate ${tier.name}",
                            colors = listOf(accent, ElectricViolet),
                            onClick = { onPurchaseVip(tier.level, tier.monthlyCoins, tier.name) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AvatarAndProfileEditSheet(
    user: UserProfile,
    onSave: (
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        avatarConfig: AvatarConfig,
    ) -> Unit,
    onDismiss: () -> Unit,
) {
    var nickname by remember { mutableStateOf(user.nickname) }
    var bio by remember { mutableStateOf(user.bio) }
    var country by remember { mutableStateOf(user.country) }
    var language by remember { mutableStateOf(user.language) }
    var showGender by remember { mutableStateOf(user.showGender) }
    var showAge by remember { mutableStateOf(user.showAge) }
    var cfg by remember { mutableStateOf(user.avatarConfig) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text("3D Avatar & Profile Studio", style = MaterialTheme.typography.headlineMedium) }
            item {
                OutlinedTextField(value = nickname, onValueChange = { nickname = it }, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = language, onValueChange = { language = it }, label = { Text("Language") }, modifier = Modifier.weight(1f))
                }
            }
            item {
                Text("Avatar Hair Style", style = MaterialTheme.typography.labelLarge, color = CyanPulse)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Neon Braids", "Silver Fade", "Starlight Bob", "Cyber Spikes").forEach { item ->
                        FilterChip(selected = cfg.hairStyle == item, onClick = { cfg = cfg.copy(hairStyle = item) }, label = { Text(item) })
                    }
                }
                Text("Outfit & Accessories", style = MaterialTheme.typography.labelLarge, color = CyanPulse)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Cyber Jacket", "Royal Tuxedo", "Velvet Hoodie", "Astral Armor").forEach { item ->
                        FilterChip(selected = cfg.outfitStyle == item, onClick = { cfg = cfg.copy(outfitStyle = item) }, label = { Text(item) })
                    }
                    listOf("Holo Headphones", "Royal Crown", "Neon Visor", "Dragon Aura").forEach { item ->
                        FilterChip(selected = cfg.accessory == item, onClick = { cfg = cfg.copy(accessory = item) }, label = { Text(item) })
                    }
                }
                Text("Privacy Controls", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Show Gender on Profile")
                    Switch(checked = showGender, onCheckedChange = { showGender = it })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Show Age Badge")
                    Switch(checked = showAge, onCheckedChange = { showAge = it })
                }
            }
            item {
                WefunGradientButton(
                    text = "Save Profile & 3D Avatar",
                    onClick = {
                        onSave(nickname, bio, country, language, showGender, showAge, cfg)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamiliesSheet(
    families: List<FamilyInfo>,
    onToggleJoin: (String) -> Unit,
    onCreateFamily: (String, String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var intro by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Groups, null, tint = RoyalGold)
                    Text("Families & Guilds", style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Create New Family", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Family Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = intro, onValueChange = { intro = it }, label = { Text("Family Intro") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        WefunGradientButton(
                            text = "Launch Family",
                            enabled = name.isNotBlank(),
                            onClick = {
                                onCreateFamily(name, intro, "⚡")
                                name = ""
                                intro = ""
                            }
                        )
                    }
                }
            }
            items(families, key = { it.id }) { fam ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("${fam.badgeEmoji} #${fam.rank} ${fam.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Lv.${fam.level}", color = RoyalGold, fontWeight = FontWeight.Bold)
                        }
                        Text(fam.intro, style = MaterialTheme.typography.bodySmall)
                        Text("Leader: ${fam.leaderName} • ${fam.memberCount} members • ${fam.contributionPoints} pts", style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                        OutlinedButton(onClick = { onToggleJoin(fam.id) }) {
                            Text(if (fam.isJoined) "Joined (Tap to Leave)" else "Request to Join Family")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsAndRankingsSheet(
    events: List<EventItem>,
    onToggleRegisterEvent: (String) -> Unit,
    onEventLuckyDraw: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    var selectedBoard by remember { mutableStateOf("Popularity") }
    val tabs = listOf("Popularity", "Senders", "Receivers", "Hosts", "Agencies", "PK")
    val entries = remember(selectedBoard) { WefunSeedData.leaderboardFor(selectedBoard) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.EmojiEvents, null, tint = RoyalGold)
                    Text("Events & Leaderboards", style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tabs) { tab ->
                        FilterChip(selected = selectedBoard == tab, onClick = { selectedBoard = tab }, label = { Text(tab) })
                    }
                }
            }
            items(entries, key = { "${selectedBoard}_${it.rank}" }) { entry ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("#${entry.rank}  ${entry.name}", style = MaterialTheme.typography.titleSmall)
                            Text(entry.subtitle, style = MaterialTheme.typography.bodySmall, color = CyanPulse)
                        }
                        Text("${entry.score}", style = MaterialTheme.typography.titleMedium, color = RoyalGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().testTag("event_lucky_draw_card"),
                    borderColor = RoyalGold.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Event Lucky Draw", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("100 💎 = 1 VIP Growth Pt (Active VIP only)", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onEventLuckyDraw,
                            modifier = Modifier.testTag("event_lucky_draw_button")
                        ) {
                            Text("Spin 100 💎")
                        }
                    }
                }
            }
            item { Text("Official Events & Tournaments", style = MaterialTheme.typography.titleMedium) }
            items(events, key = { it.id }) { ev ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${ev.category} • ${ev.schedule}", style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                        Text(ev.title, style = MaterialTheme.typography.titleSmall)
                        Text("Rewards: ${ev.rewardSummary}", style = MaterialTheme.typography.bodySmall, color = RoyalGold)
                        Text("${ev.participantCount} registered • Room ${ev.roomId}", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = { onToggleRegisterEvent(ev.id) }) {
                            Text(if (ev.isRegistered) "Registered ✓" else "Register Now")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostAgencySheet(onDismiss: () -> Unit) {
    val stats = WefunSeedData.hostStats
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Verified, null, tint = CyanPulse)
                    Text("Host & Agency Center", style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Host Verification: ${stats.verifiedStatus}", style = MaterialTheme.typography.titleSmall, color = CyanPulse)
                        Text("Hosting Hours This Month: ${stats.totalHoursThisMonth} hrs")
                        Text("Gift Diamonds Earned: ${stats.monthlyDiamonds}")
                        Text("Avg Room Audience: ${stats.avgListeners} listeners")
                        Text("Next Schedule: ${stats.nextSchedule}", style = MaterialTheme.typography.bodySmall, color = RoyalGold)
                    }
                }
            }
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = RoyalGold.copy(alpha = 0.5f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Agency Dashboard • ${stats.agencyName}", style = MaterialTheme.typography.titleSmall, color = RoyalGold)
                        Text("Admin-Configured Commission Tier: ${stats.agencyCommissionPct}%")
                        Text("Settlement Record: ${stats.settlementStatus}", style = MaterialTheme.typography.bodySmall)
                        Text("Role: Verified Host & Squad Mentor", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    notifications: List<NotificationItem>,
    settings: WefunAppSettings,
    onMarkAllRead: () -> Unit,
    onUpdatePrefs: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var gifts by remember(settings) { mutableStateOf(settings.notifyGifts) }
    var rooms by remember(settings) { mutableStateOf(settings.notifyRoomInvites) }
    var pk by remember(settings) { mutableStateOf(settings.notifyPkAlerts) }
    var msgs by remember(settings) { mutableStateOf(settings.notifyMessages) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.NotificationsActive, null, tint = CyanPulse)
                        Text("Notifications", style = MaterialTheme.typography.headlineMedium)
                    }
                    TextButton(onClick = onMarkAllRead) { Text("Mark All Read") }
                }
            }
            items(notifications, key = { it.id }) { n ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (!n.isRead) CyanPulse.copy(alpha = 0.6f) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${n.category} • ${n.title}", style = MaterialTheme.typography.titleSmall)
                            Text(n.timeLabel, style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                        }
                        Text(n.body, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                Text("Push & Alert Preferences", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Virtual Gifts")
                    Switch(checked = gifts, onCheckedChange = { gifts = it; onUpdatePrefs(it, rooms, pk, msgs) })
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Room & Family Invites")
                    Switch(checked = rooms, onCheckedChange = { rooms = it; onUpdatePrefs(gifts, it, pk, msgs) })
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("PK Battle Alerts")
                    Switch(checked = pk, onCheckedChange = { pk = it; onUpdatePrefs(gifts, rooms, it, msgs) })
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Direct Messages")
                    Switch(checked = msgs, onCheckedChange = { msgs = it; onUpdatePrefs(gifts, rooms, pk, it) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminAndSafetySheet(
    user: UserProfile,
    roomsCount: Int,
    reports: List<SafetyReport>,
    auditLogs: List<String>,
    onResolveReport: (SafetyReport, String) -> Unit,
    onAdminGrantCoins: (Long, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRole by remember { mutableStateOf(AdminRole.SUPER_ADMIN) }
    var coinAmount by remember { mutableStateOf("500") }
    var reason by remember { mutableStateOf("Event Winner Bonus") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.AdminPanelSettings, null, tint = RoyalGold)
                    Text("Admin & Safety Console", style = MaterialTheme.typography.headlineMedium)
                }
                Text("Role-based moderation, coin ledger audit & compliance controls", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
            }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminRole.entries.forEach { role ->
                        FilterChip(selected = selectedRole == role, onClick = { selectedRole = role }, label = { Text(role.title) })
                    }
                }
                Text("Active Scope: ${selectedRole.permissionsSummary}", style = MaterialTheme.typography.bodySmall, color = RoyalGold)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("$roomsCount", style = MaterialTheme.typography.titleLarge, color = CyanPulse)
                            Text("Live Rooms", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${reports.count { it.status == "Pending" }}", style = MaterialTheme.typography.titleLarge, color = RoseNeon)
                            Text("Pending Reports", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${user.coins}", style = MaterialTheme.typography.titleLarge, color = RoyalGold)
                            Text("User Coins", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            if (selectedRole == AdminRole.SUPER_ADMIN || selectedRole == AdminRole.FINANCE_MANAGER) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Authorized Coin Ledger Adjustment", style = MaterialTheme.typography.titleSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = coinAmount, onValueChange = { coinAmount = it.filter { c -> c.isDigit() } }, label = { Text("Coins") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Audit Reason") }, modifier = Modifier.weight(2f))
                            }
                            WefunGradientButton(
                                text = "Apply Verified Credit",
                                onClick = {
                                    val amt = coinAmount.toLongOrNull() ?: 0L
                                    if (amt > 0) onAdminGrantCoins(amt, reason)
                                }
                            )
                        }
                    }
                }
            }
            item { Text("Moderation Review Queue", style = MaterialTheme.typography.titleMedium) }
            items(reports, key = { it.id }) { rep ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("[${rep.targetType}] ${rep.targetName}", style = MaterialTheme.typography.titleSmall)
                            Text(rep.status, style = MaterialTheme.typography.labelMedium, color = if (rep.status == "Pending") RoseNeon else CyanPulse)
                        }
                        Text(rep.reason, style = MaterialTheme.typography.bodySmall)
                        if (rep.status == "Pending") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { onResolveReport(rep, "Resolved - Warned") }) { Text("Resolve") }
                                OutlinedButton(onClick = { onResolveReport(rep, "Banned") }) { Text("Ban Target", color = RoseNeon) }
                            }
                        }
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Rounded.Security, null, tint = CyanPulse, modifier = Modifier.size(18.dp))
                    Text("System Audit Logs", style = MaterialTheme.typography.titleMedium)
                }
            }
            items(auditLogs) { log ->
                Text("• $log", style = MaterialTheme.typography.bodySmall, color = LocalWefunColors.current.mutedText)
            }
        }
    }
}
