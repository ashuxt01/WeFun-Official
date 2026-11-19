package com.example.ui.games

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.PartyGame
import com.example.ui.components.GameBannerArtwork
import com.example.ui.components.GlassCard
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
fun GamePlaceholderScreen(
    game: PartyGame,
    onBack: () -> Unit,
    onInviteFriends: (String) -> Unit,
    onOpenGameRooms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("game_placeholder_screen_${game.id.name}")
    ) {
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
                    modifier = Modifier.testTag("game_placeholder_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back to WeFun",
                        tint = PrimaryText
                    )
                }
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = TurquoiseActive.copy(alpha = 0.14f)
            ) {
                Text(
                    text = game.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = TurquoiseDeep,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(game.accentHex), Color(game.secondaryAccentHex))
                            )
                        )
                ) {
                    GameBannerArtwork(
                        gameId = game.id,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(width = 180.dp, height = 170.dp)
                            .padding(8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                            .fillMaxWidth(0.65f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (game.isNew) {
                            Surface(shape = RoundedCornerShape(50), color = PinkAccent) {
                                Text(
                                    text = "POPULAR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Groups, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${game.playersOnline} players active",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(TurquoiseActive.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.HourglassTop, null, tint = TurquoiseActive)
                            }
                            Column {
                                Text(
                                    text = "Game Preview & Match Lobby",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Interactive table experience launching soon",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TurquoiseDeep,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = game.rules,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SecondaryBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Table Size", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                                    Text("${game.maxPlayers} Players", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Entry", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                                    Text(
                                        if (game.entryCoins == 0L) "Free" else "${game.entryCoins} Coins",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Winner Prize", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Star, null, tint = OrangeAccent, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("+${game.rewardCoins}", style = MaterialTheme.typography.titleSmall, color = OrangeAccent, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onInviteFriends("Sent ${game.title} game invite to online friends!") },
                        border = BorderStroke(1.dp, TurquoiseActive),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Rounded.GroupAdd, null, tint = TurquoiseActive, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Invite Friends", color = TurquoiseActive)
                    }
                    Surface(
                        onClick = onOpenGameRooms,
                        shape = RoundedCornerShape(50),
                        color = TurquoiseActive,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.MeetingRoom, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Game Rooms", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
