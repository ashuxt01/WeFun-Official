package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.GiftSortOrder
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.ProfileStatistics
import com.example.data.model.RoomMode
import com.example.data.repository.CharmLevelCatalog
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun GiftWallCollectionScreen(
    ownerName: String,
    summary: GiftWallSummary,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var sortOrder by remember { mutableStateOf(GiftSortOrder.MOST_RECEIVED) }
    var visibleLimit by remember { mutableIntStateOf(6) }

    val sortedGifts = remember(summary.allGifts, sortOrder) {
        when (sortOrder) {
            GiftSortOrder.MOST_RECEIVED -> summary.allGifts.sortedByDescending { it.quantity }
            GiftSortOrder.HIGHEST_STARS -> summary.allGifts.sortedByDescending { it.totalStarsEarned }
            GiftSortOrder.RECENT -> summary.allGifts.sortedByDescending { it.lastReceivedAt }
        }
    }
    val visibleGifts = remember(sortedGifts, visibleLimit) { sortedGifts.take(visibleLimit) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag("gift_wall_collection_screen")
    ) {
        ProfileSubHeader(title = "$ownerName • Gift Wall", onBack = onBack, backTag = "gift_wall_back_button")
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SecondaryBackground,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatCompactCount(summary.totalGifts.toLong()),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryText,
                                modifier = Modifier.testTag("gift_wall_total_gifts")
                            )
                            Text("Total Received Gifts", fontSize = 12.sp, color = SecondaryText)
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(BorderColor)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Star, null, tint = GoldAmber, modifier = Modifier.size(18.dp))
                                Text(
                                    text = formatCompactCount(summary.totalStars),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryText,
                                    modifier = Modifier.testTag("gift_wall_total_stars")
                                )
                            }
                            Text("Total Gift Stars", fontSize = 12.sp, color = SecondaryText)
                        }
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(GiftSortOrder.entries) { order ->
                        FilterChip(
                            selected = sortOrder == order,
                            onClick = { sortOrder = order },
                            label = { Text(order.label) },
                            modifier = Modifier.testTag("gift_sort_${order.name.lowercase()}")
                        )
                    }
                }
            }

            if (sortedGifts.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.CardGiftcard, null, tint = SecondaryText, modifier = Modifier.size(36.dp))
                            Text("No gifts received yet", fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Received virtual gifts appear here automatically once delivered.", fontSize = 12.sp, color = SecondaryText)
                        }
                    }
                }
            } else {
                items(visibleGifts, key = { it.id }) { gift ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gift_wall_item_${gift.giftId}")
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
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(gift.accentHex).copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(gift.emoji, fontSize = 26.sp)
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(gift.giftName, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 15.sp)
                                Text("${gift.category.label} • ${gift.starsPerUnit}★ / gift", fontSize = 12.sp, color = SecondaryText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("x${gift.quantity}", fontWeight = FontWeight.ExtraBold, color = TurquoiseDeep, fontSize = 16.sp)
                                Text("${gift.totalStarsEarned} Star", fontSize = 12.sp, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (sortedGifts.size > visibleLimit) {
                    item {
                        OutlinedButton(
                            onClick = { visibleLimit += 6 },
                            border = BorderStroke(1.dp, TurquoiseActive),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gift_wall_load_more")
                        ) {
                            Text("Load More (${sortedGifts.size - visibleLimit} remaining)", color = TurquoiseDeep)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatsScreen(
    ownerName: String,
    stats: ProfileStatistics,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag("profile_stats_screen")
    ) {
        ProfileSubHeader(title = "$ownerName • Stats", onBack = onBack, backTag = "stats_back_button")
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Account Level Lv.${stats.accountLevel}", fontWeight = FontWeight.ExtraBold, color = PrimaryText, fontSize = 16.sp)
                            Text("Charm Lv.${stats.charmLevel} (${CharmLevelCatalog.formatThreshold(stats.charmPoints)})", color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        LinearProgressIndicator(
                            progress = { (stats.xp.toFloat() / stats.nextLevelXp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f) },
                            color = TurquoiseActive,
                            trackColor = BorderColor,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50))
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatPillBox("Followers", formatCompactCount(stats.followers.toLong()))
                            StatPillBox("Gifts Rx", formatCompactCount(stats.giftsReceived.toLong()))
                            StatPillBox("Stars", formatCompactCount(stats.totalStars))
                            StatPillBox("Moments", stats.totalMoments.toString())
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatPillBox("Rooms Hosted", stats.roomsHosted.toString())
                            StatPillBox("Rooms Joined", stats.roomsJoined.toString())
                            StatPillBox("Following", stats.following.toString())
                            StatPillBox("Friends", stats.friends.toString())
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.SportsEsports, null, tint = TurquoiseActive)
                    Text("Game Statistics (6 Supported Games)", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp)
                }
            }

            items(stats.gameStats, key = { it.gameId.name }) { gameStat ->
                val winRate = if (gameStat.matchesPlayed > 0) gameStat.wins.toFloat() / gameStat.matchesPlayed.toFloat() else 0f
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("game_stat_${gameStat.gameId.name}")
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(gameStat.gameTitle, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                            Text(
                                text = if (gameStat.matchesPlayed == 0) "No matches yet"
                                else "${gameStat.wins}W / ${gameStat.matchesPlayed}P • Best ${gameStat.bestScore}",
                                fontSize = 12.sp,
                                color = if (gameStat.matchesPlayed == 0) SecondaryText else Color(gameStat.accentHex),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { winRate },
                            color = Color(gameStat.accentHex),
                            trackColor = BorderColor,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPillBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Text(value, fontWeight = FontWeight.ExtraBold, color = PrimaryText, fontSize = 15.sp)
        Text(label, fontSize = 11.sp, color = SecondaryText)
    }
}

@Composable
fun GuardListScreen(
    ownerName: String,
    guards: List<GuardRelationship>,
    onBack: () -> Unit,
    onOpenUserProfile: (uid: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag("guard_list_screen")
    ) {
        ProfileSubHeader(title = "$ownerName • Guard (${guards.size})", onBack = onBack, backTag = "guard_list_back_button")
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (guards.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Shield, null, tint = SecondaryText, modifier = Modifier.size(34.dp))
                            Text("No active guards", fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Only authorized protectors appear in the Guard list.", fontSize = 12.sp, color = SecondaryText)
                        }
                    }
                }
            } else {
                items(guards, key = { it.id }) { guard ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guard_row_${guard.guardUid}"),
                        onClick = { onOpenUserProfile(guard.guardUid, guard.guardName) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarCircle(
                                initials = guard.guardName.take(2),
                                accentColor = Color(guard.guardAccentHex),
                                size = 46.dp,
                                vipLevel = guard.guardLevel
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(guard.guardName, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 15.sp)
                                Text("${guard.tierLabel} • Lv.${guard.guardLevel}", fontSize = 12.sp, color = TurquoiseDeep)
                            }
                            Text("View Profile", fontSize = 12.sp, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedRoomListScreen(
    ownerName: String,
    rooms: List<AdvancedRoomPreview>,
    onBack: () -> Unit,
    onEnterRoomById: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag("advanced_room_list_screen")
    ) {
        ProfileSubHeader(title = "$ownerName • Rooms (${rooms.size})", onBack = onBack, backTag = "adv_rooms_back_button")
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (rooms.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.GraphicEq, null, tint = SecondaryText, modifier = Modifier.size(34.dp))
                            Text("No linked rooms yet", fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Hosted, joined, and followed Voice Rooms appear here.", fontSize = 12.sp, color = SecondaryText)
                        }
                    }
                }
            } else {
                items(rooms, key = { it.roomId }) { room ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adv_room_row_${room.roomId}"),
                        onClick = { onEnterRoomById(room.roomId) }
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
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(room.themePrimaryHex), Color(room.themeSecondaryHex))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.GraphicEq, null, tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(room.title, fontWeight = FontWeight.Bold, color = PrimaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("ID: ${room.roomId} • Lv.${room.roomLevel} • ${room.onlineCount} online", fontSize = 12.sp, color = SecondaryText)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(50), color = TurquoiseActive.copy(alpha = 0.14f)) {
                                        Text(room.relationshipType.label, fontSize = 10.sp, color = TurquoiseDeep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                                    }
                                    if (room.roomMode == RoomMode.AUCTION) {
                                        Surface(shape = RoundedCornerShape(50), color = PrimaryPurple.copy(alpha = 0.14f)) {
                                            Text("Auction", fontSize = 10.sp, color = PrimaryPurple, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                            Text("Enter", color = TurquoiseActive, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSubHeader(
    title: String,
    onBack: () -> Unit,
    backTag: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp).testTag(backTag)) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = PrimaryText)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
