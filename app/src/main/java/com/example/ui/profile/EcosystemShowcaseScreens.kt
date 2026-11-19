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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import com.example.data.model.AvatarConfig
import com.example.data.model.UserProfile
import com.example.data.repository.WefunSeedData
import com.example.ui.components.GlassCard
import com.example.ui.components.MyHomeHouse3DIcon
import com.example.ui.components.PlayShowShirt3DIcon
import com.example.ui.components.ShopFront3DIcon
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun ShopScreen(
    user: UserProfile,
    onBack: () -> Unit,
    onPreviewItem: (String) -> Unit,
    onShopLuckyDraw: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var selectedCategory by remember { mutableStateOf("All") }
    var equippedItemId by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        listOf("All", "Avatar Frame", "Profile Decoration", "Entrance Mount", "Chat Bubble", "Badge")
    }
    val items = remember(selectedCategory) {
        if (selectedCategory == "All") WefunSeedData.shopItems
        else WefunSeedData.shopItems.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("shop_screen")
    ) {
        EcosystemTopBar(title = "Shop", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ShopFront3DIcon(size = 50.dp, showDot = false)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("WeFun Look Store", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Profile frames, decorations & badges (${user.coins} Coins • ${user.diamonds}💎)", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                    }
                }
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().testTag("shop_lucky_draw_card"),
                    borderColor = OrangeAccent.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Shop Diamond Lucky Draw", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("100 💎 = 1 VIP Growth Pt (Active VIP only)", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            onClick = onShopLuckyDraw,
                            shape = RoundedCornerShape(50),
                            color = TurquoiseActive,
                            modifier = Modifier.testTag("shop_lucky_draw_button")
                        ) {
                            Text(
                                text = "Draw Now",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            items(items.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { item ->
                        val isEquipped = equippedItemId == item.id
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            borderColor = if (isEquipped) TurquoiseActive else null
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(item.accentHex).copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color(item.accentHex))
                                    }
                                    if (item.badge != null) {
                                        Surface(shape = RoundedCornerShape(50), color = PinkAccent.copy(alpha = 0.14f)) {
                                            Text(item.badge, style = MaterialTheme.typography.labelSmall, color = PinkAccent, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText, maxLines = 1)
                                Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = SecondaryText, maxLines = 2)
                                Surface(
                                    onClick = {
                                        equippedItemId = item.id
                                        onPreviewItem("Selected preview: ${item.title}")
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = if (isEquipped) SecondaryBackground else TurquoiseActive,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 7.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isEquipped) "Previewing" else "${item.coinPrice} Coins",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isEquipped) TurquoiseDeep else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PlayShowScreen(
    user: UserProfile,
    onBack: () -> Unit,
    onSaveAvatarConfig: (AvatarConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var hair by remember { mutableStateOf(user.avatarConfig.hairStyle) }
    var outfit by remember { mutableStateOf(user.avatarConfig.outfitStyle) }
    var accessory by remember { mutableStateOf(user.avatarConfig.accessory) }
    var frame by remember { mutableStateOf(user.avatarConfig.frameName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("play_show_screen")
    ) {
        EcosystemTopBar(title = "PLAY Show", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlayShowShirt3DIcon(size = 64.dp)
                        Text("${user.nickname}'s Look", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                        Text("$hair • $outfit • $accessory", style = MaterialTheme.typography.bodySmall, color = TurquoiseDeep)
                    }
                }
            }

            item {
                StyleOptionRow("Hairstyle", listOf("Neon Braids", "Sakura Bob", "Violet Waves", "Star Crest"), hair) { hair = it }
            }
            item {
                StyleOptionRow("Outfit", listOf("Cyber Jacket", "Turquoise Blazer", "Velvet Stage", "Starlight Cape"), outfit) { outfit = it }
            }
            item {
                StyleOptionRow("Accessory", listOf("Holo Headphones", "Studio Mic", "Moon Pin", "Gold Shades"), accessory) { accessory = it }
            }
            item {
                StyleOptionRow("Profile Ring", listOf("Astral Gold", "Cyan Glow", "Prism Crown", "Default Ring"), frame) { frame = it }
            }
            item {
                WefunGradientButton(
                    text = "Save Free Look",
                    onClick = {
                        onSaveAvatarConfig(
                            user.avatarConfig.copy(
                                hairStyle = hair,
                                outfitStyle = outfit,
                                accessory = accessory,
                                frameName = frame
                            )
                        )
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
fun MyHomeScreen(
    user: UserProfile,
    onBack: () -> Unit,
    onOpenPlayShow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("my_home_screen")
    ) {
        EcosystemTopBar(title = "My Home", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFE6FAF8), Color(0xFFF3EEFF))))
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        MyHomeHouse3DIcon(size = 56.dp)
                        Column {
                            Text("${user.nickname}'s Space", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                            Text("WEFUN ID: ${user.wefunId} • ${user.country}", style = MaterialTheme.typography.bodySmall, color = TurquoiseDeep)
                        }
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bio & Welcome Note", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text(user.bio, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
                        Text("Active Outfit: ${user.avatarConfig.outfitStyle} • ${user.avatarConfig.accessory}", style = MaterialTheme.typography.labelMedium, color = OrangeAccent)
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Home Highlights", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("• ${user.visitorsCount} profile visitors", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        Text("• Level ${user.userLevel} (${user.xp} XP)", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        Text("• ${user.friendsCount} friends • ${user.followersCount} followers", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }

            item {
                WefunGradientButton(
                    text = "Customize PLAY Show Look",
                    onClick = onOpenPlayShow
                )
            }
        }
    }
}

@Composable
private fun StyleOptionRow(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { opt ->
                FilterChip(
                    selected = opt == selected,
                    onClick = { onSelect(opt) },
                    label = { Text(opt) }
                )
            }
        }
    }
}

@Composable
private fun EcosystemTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PrimaryText)
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
    }
    HorizontalDivider(color = BorderColor)
}
