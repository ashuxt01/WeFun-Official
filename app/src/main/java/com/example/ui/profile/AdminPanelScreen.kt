package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.VipAuditLogEntry
import com.example.data.model.VipGiftItem
import com.example.data.model.VipTierConfig
import com.example.ui.components.GlassCard
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun AdminPanelScreen(
    user: UserProfile,
    vipConfigs: List<VipTierConfig>,
    vipGifts: List<VipGiftItem>,
    vipAuditLogs: List<VipAuditLogEntry>,
    onBack: () -> Unit,
    onUpdateVipConfig: (VipTierConfig) -> Unit,
    onGrantUserVip: (level: Int, months: Int, active: Boolean) -> Unit,
    onAdjustGrowthPoints: (delta: Long, reason: String) -> Unit,
    onSaveVipGift: (VipGiftItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var selectedTab by remember { mutableIntStateOf(0) }
    var editingConfig by remember { mutableStateOf<VipTierConfig?>(null) }
    var editingGift by remember { mutableStateOf<VipGiftItem?>(null) }
    var showNewGiftDialog by remember { mutableStateOf(false) }

    val tabs = listOf("VIP Levels", "User Override", "VIP Gifts", "Audit Logs")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("admin_panel_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).testTag("admin_panel_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PrimaryText)
            }
            Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, tint = TurquoiseActive)
            Text(
                text = "WeFun Admin Panel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText
            )
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = PrimaryBackground,
            contentColor = TurquoiseActive,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    modifier = Modifier.testTag("admin_tab_$idx"),
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == idx) TurquoiseDeep else SecondaryText
                        )
                    }
                )
            }
        }
        HorizontalDivider(color = BorderColor)

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(vipConfigs.sortedBy { it.level }, key = { it.level }) { cfg ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        VipLevelBadge3D(vipLevel = cfg.level, compact = true)
                                        Text(cfg.badgeTitle, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                                    }
                                    Text(
                                        text = "Threshold: %,d • Daily: +%d💎 • Sub: %dc".format(
                                            cfg.growthThreshold,
                                            cfg.dailyDiamondBonus,
                                            cfg.monthlySubscriptionCoins
                                        ),
                                        fontSize = 12.sp,
                                        color = SecondaryText
                                    )
                                    Text(
                                        text = if (cfg.regulationDays == 0) "Regulation: Exempt" else "Regulation: ${cfg.regulationDays}d / ${cfg.regulationTarget} pts / -${cfg.growthToDeduct}",
                                        fontSize = 11.sp,
                                        color = TurquoiseDeep
                                    )
                                }
                                IconButton(
                                    onClick = { editingConfig = cfg },
                                    modifier = Modifier.testTag("admin_edit_tier_${cfg.level}")
                                ) {
                                    Icon(Icons.Rounded.Edit, "Edit VIP ${cfg.level}", tint = TurquoiseActive)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                var targetLevelText by remember(user.vipLevel) { mutableStateOf(user.vipLevel.toString()) }
                var monthsText by remember { mutableStateOf("1") }
                var activeChecked by remember { mutableStateOf(true) }
                var growthDeltaText by remember { mutableStateOf("1000") }
                var adjustReason by remember { mutableStateOf("Admin Promotion") }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Target Account Status", fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    VipNicknameText(name = user.nickname, vipLevel = user.vipLevel)
                                    VipLevelBadge3D(vipLevel = user.vipLevel.coerceAtLeast(1), compact = true)
                                }
                                Text(
                                    text = "ID: ${user.wefunId} • Growth Pts: %,d • Coins: %,d • Diamonds: %,d".format(
                                        user.vipGrowthPoints, user.coins, user.diamonds
                                    ),
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }

                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Grant / Revoke VIP Status", fontWeight = FontWeight.Bold, color = PrimaryText)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = targetLevelText,
                                        onValueChange = { targetLevelText = it.filter { c -> c.isDigit() } },
                                        label = { Text("VIP Level (0–12)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("admin_grant_level_input")
                                    )
                                    OutlinedTextField(
                                        value = monthsText,
                                        onValueChange = { monthsText = it.filter { c -> c.isDigit() } },
                                        label = { Text("Months") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Membership Active", color = PrimaryText, fontSize = 14.sp)
                                    Switch(
                                        checked = activeChecked,
                                        onCheckedChange = { activeChecked = it },
                                        colors = SwitchDefaults.colors(checkedTrackColor = TurquoiseActive, checkedThumbColor = Color.White)
                                    )
                                }
                                Button(
                                    onClick = {
                                        val lvl = (targetLevelText.toIntOrNull() ?: 1).coerceIn(0, 12)
                                        val mo = (monthsText.toIntOrNull() ?: 1).coerceIn(1, 24)
                                        onGrantUserVip(lvl, mo, activeChecked)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_apply_vip_btn")
                                ) {
                                    Text("Apply VIP Status", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Adjust Growth Points", fontWeight = FontWeight.Bold, color = PrimaryText)
                                OutlinedTextField(
                                    value = growthDeltaText,
                                    onValueChange = { growthDeltaText = it },
                                    label = { Text("Growth Pts Delta (+/-)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("admin_growth_delta_input")
                                )
                                OutlinedTextField(
                                    value = adjustReason,
                                    onValueChange = { adjustReason = it },
                                    label = { Text("Reason") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val delta = growthDeltaText.toLongOrNull() ?: 0L
                                            if (delta != 0L) onAdjustGrowthPoints(delta, adjustReason.ifBlank { "Admin Update" })
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                                        modifier = Modifier.weight(1f).testTag("admin_apply_growth_btn")
                                    ) {
                                        Text("Update Growth Pts", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { onGrantUserVip(0, 1, false) },
                                        modifier = Modifier.weight(0.7f)
                                    ) {
                                        Text("Revoke VIP", color = Color(0xFFDC2626))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showNewGiftDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive),
                            modifier = Modifier.fillMaxWidth().testTag("admin_new_vip_gift_btn")
                        ) {
                            Text("Create New VIP Gift", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(vipGifts, key = { it.id }) { gift ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(gift.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(gift.name, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text("${gift.tierGroup} • VIP ${gift.requiredVipLevel}+ • ${gift.diamondCost}💎", fontSize = 12.sp, color = SecondaryText)
                                    }
                                }
                                IconButton(onClick = { editingGift = gift }) {
                                    Icon(Icons.Rounded.Edit, "Edit Gift", tint = TurquoiseActive)
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vipAuditLogs, key = { it.id }) { log ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text(log.actionType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TurquoiseDeep)
                                    Text(formatVipMinute(log.timestamp), fontSize = 10.sp, color = SecondaryText)
                                }
                                Text(log.summary, fontSize = 13.sp, color = PrimaryText, fontWeight = FontWeight.Medium)
                                Text("Actor: ${log.actorName}", fontSize = 11.sp, color = SecondaryText)
                            }
                        }
                    }
                }
            }
        }
    }

    editingConfig?.let { cfg ->
        var thresholdStr by remember(cfg.level) { mutableStateOf(cfg.growthThreshold.toString()) }
        var regDaysStr by remember(cfg.level) { mutableStateOf(cfg.regulationDays.toString()) }
        var regTargetStr by remember(cfg.level) { mutableStateOf(cfg.regulationTarget.toString()) }
        var regDeductStr by remember(cfg.level) { mutableStateOf(cfg.growthToDeduct.toString()) }
        var dailyBonusStr by remember(cfg.level) { mutableStateOf(cfg.dailyDiamondBonus.toString()) }

        AlertDialog(
            onDismissRequest = { editingConfig = null },
            title = { Text("Edit ${cfg.name} Rules") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = thresholdStr,
                        onValueChange = { thresholdStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Growth Threshold") },
                        singleLine = true,
                        modifier = Modifier.testTag("admin_edit_threshold_input")
                    )
                    OutlinedTextField(
                        value = regDaysStr,
                        onValueChange = { regDaysStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Regulation Days") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = regTargetStr,
                        onValueChange = { regTargetStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Regulation Target Pts") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = regDeductStr,
                        onValueChange = { regDeductStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Deduction Pts") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dailyBonusStr,
                        onValueChange = { dailyBonusStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Daily Diamond Bonus") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdateVipConfig(
                            cfg.copy(
                                growthThreshold = thresholdStr.toLongOrNull() ?: cfg.growthThreshold,
                                regulationDays = regDaysStr.toIntOrNull() ?: cfg.regulationDays,
                                regulationTarget = regTargetStr.toLongOrNull() ?: cfg.regulationTarget,
                                growthToDeduct = regDeductStr.toLongOrNull() ?: cfg.growthToDeduct,
                                dailyDiamondBonus = dailyBonusStr.toLongOrNull() ?: cfg.dailyDiamondBonus,
                            )
                        )
                        editingConfig = null
                    },
                    modifier = Modifier.testTag("admin_save_tier_btn")
                ) {
                    Text("Save", color = TurquoiseActive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingConfig = null }) { Text("Cancel") }
            }
        )
    }

    val activeGiftDialog = editingGift ?: if (showNewGiftDialog) {
        VipGiftItem(
            id = "vg_custom_${System.currentTimeMillis()}",
            name = "Celestial Crown",
            tierGroup = "Custom Gift",
            requiredVipLevel = 10,
            diamondCost = 2_000L,
            coinCost = 2_000L,
            emoji = "✨",
            effectSummary = "Exclusive VIP celebration spotlight",
            accentHex = 0xFFFFD700,
        )
    } else null

    activeGiftDialog?.let { g ->
        var name by remember(g.id) { mutableStateOf(g.name) }
        var vipReq by remember(g.id) { mutableStateOf(g.requiredVipLevel.toString()) }
        var diamonds by remember(g.id) { mutableStateOf(g.diamondCost.toString()) }
        var tierGrp by remember(g.id) { mutableStateOf(g.tierGroup) }
        AlertDialog(
            onDismissRequest = { editingGift = null; showNewGiftDialog = false },
            title = { Text(if (editingGift != null) "Edit VIP Gift" else "New VIP Gift") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Gift Name") }, singleLine = true)
                    OutlinedTextField(value = tierGrp, onValueChange = { tierGrp = it }, label = { Text("Tier Group") }, singleLine = true)
                    OutlinedTextField(value = vipReq, onValueChange = { vipReq = it.filter { c -> c.isDigit() } }, label = { Text("Min VIP Level") }, singleLine = true)
                    OutlinedTextField(value = diamonds, onValueChange = { diamonds = it.filter { c -> c.isDigit() } }, label = { Text("Diamond Cost") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val reqLvl = (vipReq.toIntOrNull() ?: g.requiredVipLevel).coerceIn(1, 12)
                    val cost = (diamonds.toLongOrNull() ?: g.diamondCost).coerceAtLeast(100L)
                    onSaveVipGift(
                        g.copy(
                            name = name.ifBlank { g.name },
                            tierGroup = tierGrp.ifBlank { g.tierGroup },
                            requiredVipLevel = reqLvl,
                            diamondCost = cost,
                            coinCost = cost,
                        )
                    )
                    editingGift = null
                    showNewGiftDialog = false
                }) { Text("Save", color = TurquoiseActive, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingGift = null; showNewGiftDialog = false }) { Text("Cancel") }
            }
        )
    }
}
