package com.example.ui.rooms

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import com.example.data.model.RelationshipRequest
import com.example.data.model.RelationshipRequestStatus
import com.example.data.model.RoomSeat
import com.example.data.model.StartingToken
import com.example.ui.components.AuctionGavel3DIcon
import com.example.ui.components.AvatarCircle
import com.example.ui.components.RelationshipTag3DIcon
import com.example.ui.components.StartingToken3DArt
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TurquoiseActive
import com.example.ui.viewmodel.LiveRoomSessionState

private val AuctionCardBg = Color(0xFF330C5B)
private val AuctionCardDeep = Color(0xFF21053F)
private val AuctionGoldBorder = Brush.linearGradient(listOf(PremiumGold, Color(0xFFFFA800), PremiumGold))

/**
 * Purple-and-gold Auction Room Stage composable integrated into LiveVoiceRoomScreen
 * whenever room.roomMode == RoomMode.AUCTION.
 */
@Composable
fun AuctionRoomScreen(
    session: LiveRoomSessionState,
    currentUserId: String,
    pendingRequests: List<RelationshipRequest>,
    onSeatClick: (RoomSeat) -> Unit,
    onOpenRelationshipSettings: () -> Unit,
    onPlaceBid: (Long) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val auction = session.auctionState
    val tagLabel = auction.selectedTag?.label
        ?: auction.customTagLabel.ifBlank { "BFF" }
    val tagIconKey = auction.selectedTag?.iconKey ?: "clover"
    val token = auction.selectedToken ?: StartingToken.SCOOTER
    val mins = auction.countdownSeconds / 60
    val secs = auction.countdownSeconds % 60
    val timerText = String.format("%02d:%02d", mins, secs)

    val vipPodiumSeats = session.seats.take(3)
    val circleSeats = session.seats.drop(3).take(6)

    val incomingForUser = pendingRequests.filter {
        it.status == RelationshipRequestStatus.PENDING &&
            (it.targetUserId == currentUserId || it.roomId == session.room?.id)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("auction_room_stage"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Auction Stage Top Status Strip
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AuctionCardDeep.copy(alpha = 0.88f),
            border = BorderStroke(1.dp, AuctionGoldBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        AuctionGavel3DIcon(size = 20.dp)
                        Text(
                            text = if (auction.isBiddingActive) "The relationship is in progress..." else "Configure Auction Stage",
                            style = MaterialTheme.typography.labelLarge,
                            color = PremiumGold,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryPurple.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, PremiumGold.copy(alpha = 0.65f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Timer, null, tint = PremiumGold, modifier = Modifier.size(13.dp))
                                Text(
                                    text = timerText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.testTag("auction_countdown_timer")
                                )
                            }
                        }

                        Surface(
                            onClick = onOpenRelationshipSettings,
                            shape = RoundedCornerShape(50),
                            color = TurquoiseActive,
                            modifier = Modifier.testTag("open_auction_settings_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Settings, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Mode pills + TOP badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PremiumGold.copy(alpha = 0.22f),
                            border = BorderStroke(0.8.dp, PremiumGold)
                        ) {
                            Text(
                                text = "Bidding • ${auction.biddingMode.label}",
                                fontSize = 10.sp,
                                color = PremiumGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = CyanPulse.copy(alpha = 0.18f),
                            border = BorderStroke(0.8.dp, CyanPulse.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "Relationship Advancement",
                                fontSize = 10.sp,
                                color = CyanPulse,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PinkAccent.copy(alpha = 0.25f),
                        border = BorderStroke(0.8.dp, PinkAccent)
                    ) {
                        Text(
                            text = "TOP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // 2. Central Relationship Pair Display + Reward Token + Bid Controls
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = AuctionCardBg.copy(alpha = 0.92f),
            border = BorderStroke(1.4.dp, AuctionGoldBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Host / Top Bidder + Tag + Target User
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        AvatarCircle(
                            initials = (auction.currentTopBidderName ?: session.ownerInfo?.displayName ?: "Host")
                                .split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                            accentColor = PremiumGold,
                            size = 38.dp,
                            vipLevel = session.ownerInfo?.vipLevel ?: 4
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF4B1980),
                            border = BorderStroke(1.dp, PremiumGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                RelationshipTag3DIcon(iconKey = tagIconKey, size = 16.dp)
                                Text(
                                    text = tagLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Icon(Icons.Rounded.Favorite, null, tint = PinkAccent, modifier = Modifier.size(12.dp))
                            }
                        }

                        AvatarCircle(
                            initials = (auction.targetUserName ?: "Guest")
                                .split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                            accentColor = PinkAccent,
                            size = 38.dp,
                            vipLevel = 2
                        )
                    }

                    // Starting Token Art & Points
                    Surface(
                        onClick = onOpenRelationshipSettings,
                        shape = RoundedCornerShape(12.dp),
                        color = AuctionCardDeep,
                        border = BorderStroke(1.dp, PremiumGold.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StartingToken3DArt(token = token, size = 28.dp)
                            Column {
                                Text(
                                    text = token.title.removeSuffix(" Token"),
                                    fontSize = 10.sp,
                                    color = PremiumGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${maxOf(auction.startingPricePoints, auction.currentTopBidPoints)} pts",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Virtual Points Bid Action Strip (Virtual points & rewards ONLY)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (auction.currentTopBidderName != null) {
                            "Top: ${auction.currentTopBidderName} (${auction.currentTopBidPoints} pts)"
                        } else {
                            "Start bid: ${auction.startingPricePoints} virtual pts"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onPlaceBid(100L) },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold, contentColor = AuctionCardDeep),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("auction_bid_100_button")
                        ) {
                            Icon(Icons.Rounded.Gavel, null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+100 pts", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        OutlinedButton(
                            onClick = { onPlaceBid(500L) },
                            border = BorderStroke(1.dp, PremiumGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("auction_bid_500_button")
                        ) {
                            Text("+500 pts", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Bid History Strip
                if (auction.history.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.testTag("auction_bid_history_row")
                    ) {
                        items(auction.history.take(5), key = { it.id }) { entry ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${entry.bidderName}: ${entry.points} pts (${entry.tagLabel})",
                                    fontSize = 10.sp,
                                    color = PremiumGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Pending Relationship Consent Banner (Activates ONLY upon ACCEPTED response)
        incomingForUser.firstOrNull()?.let { pending ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF3E146B),
                border = BorderStroke(1.2.dp, CyanPulse),
                modifier = Modifier.fillMaxWidth().testTag("pending_relationship_banner_${pending.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${pending.senderName} invited ${pending.targetUserName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Relationship '${pending.tagLabel}' • ${pending.tokenTitle}",
                            fontSize = 10.sp,
                            color = PremiumGold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { onDeclineRequest(pending.id) },
                            border = BorderStroke(1.dp, PinkAccent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp).testTag("decline_relationship_btn_${pending.id}")
                        ) {
                            Text("Decline", fontSize = 11.sp, color = PinkAccent)
                        }
                        Button(
                            onClick = { onAcceptRequest(pending.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp).testTag("accept_relationship_btn_${pending.id}")
                        ) {
                            Text("Accept", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. VIP Top 3 Podium Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            vipPodiumSeats.forEachIndexed { idx, seat ->
                Surface(
                    onClick = { onSeatClick(seat) },
                    shape = RoundedCornerShape(14.dp),
                    color = AuctionCardDeep.copy(alpha = 0.85f),
                    border = BorderStroke(1.3.dp, AuctionGoldBorder),
                    modifier = Modifier.weight(1f).testTag("auction_vip_seat_${seat.seatIndex}")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PremiumGold
                        ) {
                            Text(
                                text = "VIP ${idx + 1}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AuctionCardDeep,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                        if (seat.occupantId != null) {
                            AvatarCircle(
                                initials = seat.occupantInitials,
                                accentColor = Color(seat.accentColorHex),
                                size = 38.dp,
                                vipLevel = seat.vipLevel,
                                isSpeaking = seat.isSpeaking
                            )
                            Text(
                                text = seat.occupantName ?: "Speaker",
                                fontSize = 10.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple.copy(alpha = 0.45f))
                                    .border(1.dp, PremiumGold.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (seat.isSeatLocked) Icons.Rounded.Lock else Icons.Rounded.Add,
                                    contentDescription = "VIP auction seat",
                                    tint = PremiumGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = if (seat.isSeatLocked) "Locked" else "Join VIP",
                                fontSize = 10.sp,
                                color = PremiumGold.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // 5. Six Circular Auction Participant Seats
        if (circleSeats.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                circleSeats.forEach { seat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onSeatClick(seat) }
                            .testTag("auction_seat_${seat.seatIndex}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AuctionCardBg.copy(alpha = 0.8f))
                                .border(1.2.dp, PremiumGold.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (seat.occupantId != null) {
                                AvatarCircle(
                                    initials = seat.occupantInitials,
                                    accentColor = Color(seat.accentColorHex),
                                    size = 40.dp,
                                    vipLevel = seat.vipLevel
                                )
                                if (seat.isMuted) {
                                    Icon(
                                        Icons.Rounded.MicOff,
                                        "Muted",
                                        tint = PinkAccent,
                                        modifier = Modifier.align(Alignment.BottomEnd).size(12.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (seat.isSeatLocked) Icons.Rounded.Lock else Icons.Rounded.Add,
                                    contentDescription = "Seat ${seat.seatIndex + 1}",
                                    tint = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = seat.occupantName?.take(7) ?: "No.${seat.seatIndex + 1}",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.82f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
