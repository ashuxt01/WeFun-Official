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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharmLevel
import com.example.data.model.SpecialEffect
import com.example.data.model.SpecialEffectCategory
import com.example.data.repository.CharmLevelCatalog
import com.example.ui.components.CharmLevel3DBadge
import com.example.ui.components.CharmLevelHaloRingPreview
import com.example.ui.components.CharmNumberCrestPill

// Strictly isolated dark black/purple & royal gold palette for the Charm Level Screen only.
private val CharmBgTop = Color(0xFF0A0414)
private val CharmBgMid = Color(0xFF1E083E)
private val CharmBgBottom = Color(0xFF070210)
private val CharmPanelDark = Color(0xFF140726)
private val CharmRowHighlight = Color(0xFF210B40)
private val CharmGold = Color(0xFFFBBF24)
private val CharmGoldBright = Color(0xFFFDE047)
private val CharmPurpleGlow = Color(0xFFA855F7)
private val CharmTextMuted = Color(0xFFC4B5FD)

@Composable
fun CharmLevelScreen(
    userDisplayName: String,
    userCharmPoints: Long,
    userActiveLevel: Int = 2,
    specialEffects: List<SpecialEffect> = emptyList(),
    initialTab: Int = 0,
    onBack: () -> Unit,
    onToggleSpecialEffect: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val progress = remember(userCharmPoints) {
        CharmLevelCatalog.calculateCharmProgress(userCharmPoints)
    }
    val charmTiers = remember { CharmLevelCatalog.charmLevels }
    val activeTiers = remember { CharmLevelCatalog.activeLevels }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CharmBgTop, CharmBgMid, CharmBgBottom)))
            .navigationBarsPadding()
            .testTag("charm_level_screen")
    ) {
        // Exclusive Dark Purple/Gold Header: Back | Centered WeFun + Crown | Help ?
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
                    .testTag("charm_screen_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "WeFun",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                CharmLevel3DBadge(level = 21, size = 22.dp)
            }

            IconButton(
                onClick = { showHelpDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
                    .testTag("charm_screen_help_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                    contentDescription = "Charm Level Rules",
                    tint = Color.White
                )
            }
        }

        // Top Tabs: Charm Levels | Active Levels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CharmTopTab(
                label = "Charm Levels",
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f).testTag("charm_tab_levels")
            )
            CharmTopTab(
                label = "Active Levels",
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f).testTag("charm_tab_active")
            )
        }

        HorizontalDivider(color = CharmPurpleGlow.copy(alpha = 0.25f))

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("charm_levels_list"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CharmProgressSummaryBanner(
                        userDisplayName = userDisplayName,
                        progress = progress,
                    )
                }

                // Levels 1..20 inside a royal dark-purple/gold framed showcase card
                items(charmTiers.filter { it.level < 21 }, key = { it.level }) { tier ->
                    CharmLevelTierRow(
                        tier = tier,
                        isCurrentLevel = progress.currentLevel == tier.level,
                        isUnlocked = progress.currentLevel >= tier.level,
                    )
                }

                // Level 21 Ultimate Finale Card
                val level21 = charmTiers.last()
                item(key = level21.level) {
                    UltimateLevel21Card(
                        tier = level21,
                        isUnlocked = progress.currentLevel >= 21,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("active_levels_effects_list"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CharmPanelDark,
                        border = BorderStroke(1.2.dp, CharmGold.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Active Level • Lv.$userActiveLevel",
                                color = CharmGoldBright,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Separate from cumulative Charm Points — Active Level tracks consecutive login streaks, room hosting, and community events.",
                                color = CharmTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                items(activeTiers, key = { it.level }) { active ->
                    val reached = userActiveLevel >= active.level
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (reached) CharmRowHighlight else CharmPanelDark,
                        border = BorderStroke(
                            1.dp,
                            if (reached) Color(active.accentHex).copy(alpha = 0.65f) else CharmPurpleGlow.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Active Lv.${active.level} • ${active.title}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = active.perkSummary,
                                    color = CharmTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (reached) Color(0xFF10B981).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = if (reached) "Reached" else "${active.minActiveDays}d",
                                    color = if (reached) Color(0xFF6EE7B7) else CharmTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Special Effects & Frame Studio",
                        color = CharmGoldBright,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = "Profile frames, room entry effects, and decorations require both tier eligibility and ownership.",
                        color = CharmTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                items(specialEffects, key = { it.id }) { effect ->
                    SpecialEffectRowCard(
                        effect = effect,
                        userCharmLevel = progress.currentLevel,
                        userActiveLevel = userActiveLevel,
                        onToggle = { onToggleSpecialEffect(effect.id) },
                    )
                }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = CharmPanelDark,
            titleContentColor = CharmGoldBright,
            textContentColor = Color.White,
            title = { Text("WeFun Charm & Active Levels", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "• Charm Levels (1–21) measure your cumulative Charm earned from verified virtual gifts received on WeFun.",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        "• Level 1 begins at 1,000 Charm (NOVICE) and ascends to Level 21 at 360,000,000 Charm (ULTIMATE).",
                        fontSize = 13.sp,
                        color = CharmTextMuted
                    )
                    Text(
                        "• Active Levels & Special Effects reflect daily check-ins, unlocked frames, and room entry banners you own.",
                        fontSize = 13.sp,
                        color = CharmTextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got It", color = CharmGoldBright, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CharmTopTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else CharmTextMuted.copy(alpha = 0.65f),
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (selected) Brush.horizontalGradient(listOf(CharmPurpleGlow, Color.White, CharmGold))
                    else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                )
        )
    }
}

@Composable
private fun CharmProgressSummaryBanner(
    userDisplayName: String,
    progress: com.example.data.model.CharmProgress,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CharmPanelDark.copy(alpha = 0.92f),
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(CharmGold, CharmPurpleGlow))),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("charm_progress_summary_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CharmLevel3DBadge(level = progress.currentLevel.coerceAtLeast(1), size = 34.dp)
                    Column {
                        Text(
                            text = userDisplayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (progress.currentLevel == 0) "UNRANKED • Build Charm to 1,000"
                            else "Level ${progress.currentLevel} • ${progress.currentTierTitle}",
                            color = CharmGoldBright,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CharmLevelCatalog.formatThreshold(progress.totalCharm),
                        color = CharmGoldBright,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text("Total Charm", color = CharmTextMuted, fontSize = 11.sp)
                }
            }

            LinearProgressIndicator(
                progress = { progress.fractionToNext },
                color = CharmGold,
                trackColor = Color.White.copy(alpha = 0.14f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (progress.nextLevel != null && progress.nextThreshold != null) {
                    Text(
                        text = "Next: Lv.${progress.nextLevel} (${CharmLevelCatalog.formatThreshold(progress.nextThreshold)})",
                        color = CharmTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${CharmLevelCatalog.formatThreshold(progress.remainingNeeded)} Charm needed",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Maximum Tier Reached: ULTIMATE",
                        color = CharmGoldBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CharmLevelTierRow(
    tier: CharmLevel,
    isCurrentLevel: Boolean,
    isUnlocked: Boolean,
) {
    val borderBrush = if (isCurrentLevel) {
        Brush.horizontalGradient(listOf(CharmGoldBright, CharmPurpleGlow, CharmGoldBright))
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(tier.accentHex).copy(alpha = 0.45f),
                CharmPurpleGlow.copy(alpha = 0.25f)
            )
        )
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isCurrentLevel) CharmRowHighlight else CharmPanelDark.copy(alpha = 0.88f),
        border = BorderStroke(if (isCurrentLevel) 1.6.dp else 0.9.dp, borderBrush),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("charm_tier_row_${tier.level}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CharmNumberCrestPill(level = tier.level)

            // Diamond spark + exact cumulative threshold
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.width(102.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = if (isUnlocked) CharmGold else CharmGold.copy(alpha = 0.65f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = CharmLevelCatalog.formatThreshold(tier.thresholdPoints),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }

            // Custom 3D sculpted level crystal/crown badge
            CharmLevel3DBadge(level = tier.level, size = 28.dp)

            // Tier Name
            Text(
                text = tier.title,
                color = if (isCurrentLevel) CharmGoldBright else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Right-side Halo Ring / Crown Pedestal Preview
            CharmLevelHaloRingPreview(level = tier.level)
        }
    }
}

@Composable
private fun UltimateLevel21Card(
    tier: CharmLevel,
    isUnlocked: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1F093D),
        border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(CharmGoldBright, CharmPurpleGlow, CharmGoldBright))),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("charm_tier_row_21")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CharmNumberCrestPill(level = 21)

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = CharmGoldBright, modifier = Modifier.size(14.dp))
                    Text(
                        text = CharmLevelCatalog.formatThreshold(tier.thresholdPoints),
                        color = CharmGoldBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = if (isUnlocked) "Sovereign Tier Unlocked" else "Highest Prestige Tier",
                    color = CharmTextMuted,
                    fontSize = 11.sp
                )
            }

            CharmLevel3DBadge(level = 21, size = 48.dp)

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF140724),
                border = BorderStroke(1.4.dp, CharmGold)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CharmLevel3DBadge(level = 21, size = 16.dp)
                    Text(
                        text = tier.title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialEffectRowCard(
    effect: SpecialEffect,
    userCharmLevel: Int,
    userActiveLevel: Int,
    onToggle: () -> Unit,
) {
    val meetsRequirement = userCharmLevel >= effect.requiredCharmLevel && userActiveLevel >= effect.requiredActiveLevel
    val canToggle = effect.isOwned && meetsRequirement

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CharmPanelDark,
        border = BorderStroke(
            1.dp,
            if (effect.isActive) CharmGoldBright else Color(effect.accentHex).copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("special_effect_card_${effect.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(effect.accentHex).copy(alpha = 0.4f), Color(0xFF0E051E))
                        )
                    )
                    .border(2.dp, Brush.linearGradient(listOf(Color(effect.accentHex), Color(effect.secondaryHex))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (effect.category) {
                        SpecialEffectCategory.PROFILE_FRAME -> Icons.Rounded.AutoAwesome
                        SpecialEffectCategory.ROOM_ENTRY_EFFECT -> Icons.Rounded.Star
                        SpecialEffectCategory.VISUAL_DECORATION -> Icons.Rounded.CheckCircle
                    },
                    contentDescription = effect.category.label,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = effect.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(shape = RoundedCornerShape(50), color = Color(effect.accentHex).copy(alpha = 0.22f)) {
                        Text(
                            text = effect.category.label,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(effect.description, color = CharmTextMuted, fontSize = 11.sp)
                Text(
                    text = "Requires Charm Lv.${effect.requiredCharmLevel} • Active Lv.${effect.requiredActiveLevel}",
                    color = if (meetsRequirement) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                onClick = onToggle,
                enabled = true,
                shape = RoundedCornerShape(50),
                color = when {
                    effect.isActive -> CharmGold
                    canToggle -> CharmPurpleGlow
                    else -> Color.White.copy(alpha = 0.1f)
                },
                modifier = Modifier.testTag("toggle_effect_btn_${effect.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!canToggle) {
                        Icon(Icons.Rounded.Lock, null, tint = CharmTextMuted, modifier = Modifier.size(13.dp))
                    }
                    Text(
                        text = when {
                            effect.isActive -> "Active"
                            canToggle -> "Use"
                            !effect.isOwned -> "Locked"
                            else -> "Lv.${effect.requiredCharmLevel}"
                        },
                        color = if (effect.isActive) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
