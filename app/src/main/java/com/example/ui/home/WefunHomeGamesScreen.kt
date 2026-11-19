package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DailyTaskItem
import com.example.data.model.EventItem
import com.example.data.model.PartyGame
import com.example.data.model.UserProfile
import com.example.data.repository.WefunSeedData
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GameBannerArtwork
import com.example.ui.components.GlassCard
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.components.Wefun3DCoinIcon
import com.example.ui.components.WefunQuickAction3DIcon
import com.example.ui.components.WefunStylizedBrandMark
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftHeaderGradientTop
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WefunHomeGamesScreen(
    user: UserProfile,
    events: List<EventItem>,
    dailyTasks: List<DailyTaskItem>,
    followedUserIds: Set<String>,
    onOpenWallet: () -> Unit,
    onOpenEventsRankings: () -> Unit,
    onOpenGamePlaceholder: (PartyGame) -> Unit,
    onOpenGameRooms: () -> Unit,
    onOpenOwnFullProfile: () -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit,
    onOpenChatWithPeer: (peerId: String, peerName: String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onClaimDailyTask: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTasksSheet by remember { mutableStateOf(false) }
    var showFriendsSheet by remember { mutableStateOf(false) }
    var showFunCardsSheet by remember { mutableStateOf(false) }

    val games = WefunSeedData.wefunFeaturedGames
    val horizontalGames = remember(games) { games.take(4) }
    val bottomGridGames = remember(games) { games.drop(4) }
    val morePartyGames = remember { WefunSeedData.partyGames.drop(games.size) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("home_games_list"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Soft 3D Gradient Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(SoftHeaderGradientTop, PrimaryBackground)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WefunStylizedBrandMark(
                        iconSize = 30.dp,
                        showWordmark = true,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .testTag("home_brand_logo")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 3D Coin Balance Pill
                        Surface(
                            onClick = onOpenWallet,
                            shape = RoundedCornerShape(50),
                            color = PrimaryBackground,
                            border = BorderStroke(1.dp, GoldAmber.copy(alpha = 0.55f)),
                            shadowElevation = 4.dp,
                            modifier = Modifier.testTag("home_coin_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Wefun3DCoinIcon(size = 19.dp)
                                Text(
                                    text = formatCompactCount(user.coins),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(GoldAmber.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Add, "Add Coins", tint = GoldAmber, modifier = Modifier.size(11.dp))
                                }
                            }
                        }

                        // Events Button
                        Surface(
                            onClick = onOpenEventsRankings,
                            shape = RoundedCornerShape(50),
                            color = PrimaryBackground,
                            border = BorderStroke(1.dp, TurquoiseActive.copy(alpha = 0.4f)),
                            shadowElevation = 3.dp,
                            modifier = Modifier.testTag("home_events_shortcut")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Rounded.Star, "Events", tint = TurquoiseActive, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Events",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryText,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        // User Avatar with VIP Crown/Ring Indicator -> Opens Own Full Profile
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clickable(onClick = onOpenOwnFullProfile)
                                .testTag("home_avatar_button")
                        ) {
                            val ringBrush = if (user.vipLevel > 0) {
                                Brush.linearGradient(listOf(Color(0xFFFFD700), TurquoiseActive))
                            } else {
                                Brush.linearGradient(listOf(TurquoiseActive, TurquoiseActive))
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .align(Alignment.Center)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .border(2.2.dp, ringBrush, CircleShape)
                            ) {
                                if (user.photoUri != null) {
                                    AsyncImage(
                                        model = user.photoUri,
                                        contentDescription = "My Profile",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                        contentDescription = "My Profile",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            if (user.vipLevel > 0) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFFFD700),
                                    border = BorderStroke(1.dp, Color.White),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .testTag("home_avatar_vip_tag")
                                ) {
                                    Text(
                                        text = "V${user.vipLevel}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF281600),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4 Raised 3D Quick Action Tiles: Ranking, Fun Cards, Tasks, Online Friend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionTile3D(
                        label = "Ranking",
                        kind = "ranking",
                        onClick = onOpenEventsRankings,
                        testTag = "quick_action_ranking",
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile3D(
                        label = "Fun Cards",
                        kind = "fun_cards",
                        onClick = { showFunCardsSheet = true },
                        testTag = "quick_action_fun_cards",
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile3D(
                        label = "Tasks",
                        kind = "tasks",
                        badgeCount = dailyTasks.count { !it.isClaimed },
                        onClick = { showTasksSheet = true },
                        testTag = "quick_action_tasks",
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile3D(
                        label = "Online Friend",
                        kind = "friends",
                        onClick = { showFriendsSheet = true },
                        testTag = "quick_action_friends",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Games Section Heading + Game Rooms Shortcut
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Games",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = TurquoiseActive.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "${games.size} Featured",
                            style = MaterialTheme.typography.labelSmall,
                            color = TurquoiseDeep,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Surface(
                    onClick = onOpenGameRooms,
                    shape = RoundedCornerShape(50),
                    color = SecondaryBackground,
                    border = BorderStroke(1.dp, BorderColor),
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("game_room_shortcut")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.MeetingRoom, null, tint = TurquoiseActive, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Game Rooms", style = MaterialTheme.typography.labelMedium, color = PrimaryText, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = SecondaryText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 4 Featured Horizontal 3D Illustrated Game Banners
        items(horizontalGames, key = { it.id.name }) { game ->
            FeaturedHorizontalGameBanner(
                game = game,
                onClick = { onOpenGamePlaceholder(game) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 2-Column Illustrated Game Cards (Oh My Cards & Happy Carrom)
        if (bottomGridGames.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bottomGridGames.forEach { game ->
                        TwoColumnGameCard(
                            game = game,
                            onClick = { onOpenGamePlaceholder(game) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (bottomGridGames.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // More Party Games Strip (Who's the Spy, Ludo, Quiz Battle, Draw & Guess)
        if (morePartyGames.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "More Party Games",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(morePartyGames, key = { it.id.name }) { game ->
                            Surface(
                                onClick = { onOpenGamePlaceholder(game) },
                                shape = RoundedCornerShape(18.dp),
                                shadowElevation = 5.dp,
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .width(156.dp)
                                    .testTag("more_party_game_${game.id.name}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(game.accentHex), Color(game.secondaryAccentHex))
                                            )
                                        )
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(game.emoji, fontSize = 24.sp)
                                    Text(
                                        text = game.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${game.playersOnline} online",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.88f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Compact Tournament Highlight Card
        if (events.isNotEmpty()) {
            val topEvent = events.first()
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = onOpenEventsRankings
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FEATURED EVENT", style = MaterialTheme.typography.labelSmall, color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                            Text(topEvent.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text(topEvent.schedule, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = SecondaryText)
                    }
                }
            }
        }
    }

    // Fun Cards Sheet
    if (showFunCardsSheet) {
        ModalBottomSheet(onDismissRequest = { showFunCardsSheet = false }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Fun Cards Collection", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text("Collect social perk cards from daily streaks and events.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                items(WefunSeedData.funCards, key = { it.id }) { card ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(card.accentHex).copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.EmojiEvents, null, tint = Color(card.accentHex))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(card.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Surface(shape = RoundedCornerShape(50), color = Color(card.accentHex).copy(alpha = 0.15f)) {
                                        Text(card.rarity, fontSize = 10.sp, color = Color(card.accentHex), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text(card.description, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                            }
                        }
                    }
                }
            }
        }
    }

    // Daily Tasks Modal Sheet
    if (showTasksSheet) {
        ModalBottomSheet(onDismissRequest = { showTasksSheet = false }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Daily Tasks & Rewards", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text("Complete social & party tasks to earn Coins and XP.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                items(dailyTasks, key = { it.id }) { task ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                Text(task.description, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                Text("+${task.rewardCoins} Coins • +${task.rewardXp} XP", style = MaterialTheme.typography.labelSmall, color = OrangeAccent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (task.isClaimed) {
                                Surface(shape = RoundedCornerShape(50), color = SuccessGreen.copy(alpha = 0.16f)) {
                                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Claimed", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                                    }
                                }
                            } else {
                                Surface(
                                    onClick = { onClaimDailyTask(task.id) },
                                    shape = RoundedCornerShape(50),
                                    color = TurquoiseActive
                                ) {
                                    Text(
                                        text = if (task.isCompleted) "Claim" else "Complete",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Online Friends Modal Sheet (with VIP Nickname colors & badges)
    if (showFriendsSheet) {
        val people = remember {
            WefunSeedData.publicProfiles.filter { it.uid != "host_official" }
        }
        ModalBottomSheet(onDismissRequest = { showFriendsSheet = false }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Online Friends", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text("Tap a friend to view their profile or start a chat.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                items(people, key = { it.uid }) { person ->
                    val isFollowing = person.uid in followedUserIds
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showFriendsSheet = false
                            onOpenUserProfile(person.uid, person.nickname)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                AvatarCircle(
                                    initials = person.nickname.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                    accentColor = Color(person.accentColorHex),
                                    size = 44.dp,
                                    vipLevel = person.vipLevel
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        VipNicknameText(
                                            name = person.nickname,
                                            vipLevel = person.vipLevel,
                                            style = MaterialTheme.typography.titleSmall,
                                        )
                                        VipLevelBadge3D(vipLevel = person.vipLevel, compact = true)
                                    }
                                    Text("Lv.${person.userLevel} • Online", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(onClick = { onToggleFollow(person.uid) }) {
                                    Text(if (isFollowing) "Following" else "Follow", style = MaterialTheme.typography.labelSmall)
                                }
                                Surface(
                                    onClick = {
                                        showFriendsSheet = false
                                        onOpenChatWithPeer(person.uid, person.nickname)
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = TurquoiseActive
                                ) {
                                    Text("Chat", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
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
private fun QuickActionTile3D(
    label: String,
    kind: String,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = PrimaryBackground,
        shadowElevation = 4.dp,
        border = BorderStroke(0.8.dp, BorderColor),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                WefunQuickAction3DIcon(kind = kind, size = 46.dp)
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(PinkAccent)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$badgeCount", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryText,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FeaturedHorizontalGameBanner(
    game: PartyGame,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
            .shadow(7.dp, RoundedCornerShape(22.dp), ambientColor = Color(game.accentHex).copy(alpha = 0.28f))
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(Color(game.accentHex), Color(game.secondaryAccentHex))))
            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("game_card_${game.id.name}")
    ) {
        // Specular 3D top glass highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)))
        )
        GameBannerArtwork(
            gameId = game.id,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 136.dp, height = 98.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.68f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (game.isNew) {
                        Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.25f)) {
                            Text("HOT", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(
                    text = "${game.category} • ${game.playersOnline} online",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TwoColumnGameCard(
    game: PartyGame,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(126.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = Color(game.accentHex).copy(alpha = 0.26f))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(Color(game.accentHex), Color(game.secondaryAccentHex))))
            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("game_card_${game.id.name}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)))
        )
        GameBannerArtwork(
            gameId = game.id,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(92.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Rounded.People, null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(12.dp))
                Text("${game.playersOnline} online", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}
