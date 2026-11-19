package com.example.features.voice_room.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.VoiceRoom
import com.example.ui.components.AvatarCircle
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.WarningAmber

@Composable
fun VoiceRoomTopNav(
    room: VoiceRoom,
    roomLevel: Int,
    owner: RoomOwnerInfo?,
    partner: RoomPartnerInfo?,
    memberRoles: List<RoomMemberRoleEntry>,
    isDarkStage: Boolean,
    onBackMinimize: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopyRoomId: (String) -> Unit,
    onClickOwner: () -> Unit,
    onClickCoOwner: () -> Unit,
    onOpenRoomInfo: () -> Unit,
    onOpenContribution: () -> Unit,
    onOpenManagement: () -> Unit,
    onOpenRoomSettings: () -> Unit = {},
    onOpenThemePicker: () -> Unit,
    onShareRoom: () -> Unit,
    onReportRoom: () -> Unit,
    onExitRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    var showMoreMenu by remember { mutableStateOf(false) }

    val primaryContentColor = if (isDarkStage) Color.White else PrimaryText
    val pillBackground = if (isDarkStage) Color.Black.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.92f)
    val pillBorder = if (isDarkStage) Color.White.copy(alpha = 0.16f) else Color(0xFFEAEAF0)

    // Co-owner fallback: assigned & enabled partner first, else top Super Admin / Room Admin
    val coOwnerEntry = remember(partner, memberRoles) {
        if (partner?.isEnabled == true && partner.userId != null && partner.displayName != null) {
            Triple(partner.displayName, partner.initials, partner.userLevel to partner.vipLevel)
        } else {
            val admin = memberRoles.firstOrNull {
                it.role == RoomRole.SUPER_ADMIN || it.role == RoomRole.ROOM_ADMIN
            }
            if (admin != null) Triple(admin.displayName, admin.initials, admin.userLevel to admin.vipLevel)
            else null
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Group: Back + Room Level Diamond + Title + Copyable ID + Popularity + Favorite
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onBackMinimize,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("room_minimize_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "Back / Minimize room",
                    tint = primaryContentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = pillBackground,
                border = BorderStroke(1.dp, if (isDarkStage) PremiumGold.copy(alpha = 0.35f) else pillBorder),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level Diamond Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyanPulse.copy(alpha = 0.22f),
                        border = BorderStroke(0.8.dp, CyanPulse.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .clickable(onClick = onOpenRoomInfo)
                            .testTag("room_top_level_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Diamond,
                                contentDescription = "Room Level",
                                tint = CyanPulse,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "$roomLevel",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkStage) Color.White else PrimaryText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = room.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = primaryContentColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            // Copyable Room ID
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        clipboard.setText(AnnotatedString(room.id))
                                        onCopyRoomId(room.id)
                                    }
                                    .testTag("room_top_copy_id")
                            ) {
                                Text(
                                    text = "${room.category.label} ID:${room.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = primaryContentColor.copy(alpha = 0.78f),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy Room ID",
                                    tint = primaryContentColor.copy(alpha = 0.72f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }

                            // Popularity indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = "Popularity",
                                    tint = WarningAmber,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = formatCompactCount(room.popularity),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarningAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Favorite / Recommend Heart Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (room.isFavorited) PinkAccent else CyanPulse)
                            .clickable(onClick = onToggleFavorite)
                            .testTag("room_favorite_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (room.isFavorited) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite / Recommend room",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Right Group: Owner Profile Pill, Co-Owner Profile Pill, Online Count, More Options (...)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (owner != null) {
                TopNavMemberAvatarBadge(
                    initials = owner.initials,
                    accentColor = Color(owner.avatarColorHex),
                    userLevel = owner.userLevel,
                    vipLevel = owner.vipLevel,
                    badgeColor = PremiumGold,
                    badgeTextColor = PrimaryText,
                    onClick = onClickOwner,
                    testTag = "top_nav_owner_profile"
                )
            }

            if (coOwnerEntry != null) {
                TopNavMemberAvatarBadge(
                    initials = coOwnerEntry.second,
                    accentColor = PrimaryPurple,
                    userLevel = coOwnerEntry.third.first,
                    vipLevel = coOwnerEntry.third.second,
                    badgeColor = PrimaryPurple,
                    badgeTextColor = PremiumGold,
                    onClick = onClickCoOwner,
                    testTag = "top_nav_coowner_profile"
                )
            }

            // Online Member Count Pill
            Surface(
                onClick = onOpenRoomInfo,
                shape = RoundedCornerShape(50),
                color = pillBackground,
                border = BorderStroke(1.dp, pillBorder),
                modifier = Modifier.testTag("room_online_count_pill")
            ) {
                Text(
                    text = "${room.onlineCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = primaryContentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }

            // More Options Menu (...)
            Box {
                IconButton(
                    onClick = { showMoreMenu = true },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("room_more_options_button")
                ) {
                    Icon(Icons.Rounded.MoreHoriz, "More options", tint = primaryContentColor)
                }
                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Room Theme & Backdrop") },
                        onClick = { showMoreMenu = false; onOpenThemePicker() }
                    )
                    DropdownMenuItem(
                        text = { Text("Room Info & Levels 1–10") },
                        onClick = { showMoreMenu = false; onOpenRoomInfo() }
                    )
                    DropdownMenuItem(
                        text = { Text("Contribution Leaderboard") },
                        onClick = { showMoreMenu = false; onOpenContribution() }
                    )
                    DropdownMenuItem(
                        text = { Text("Room Settings") },
                        onClick = { showMoreMenu = false; onOpenRoomSettings() }
                    )
                    DropdownMenuItem(
                        text = { Text("Room Management Panel") },
                        onClick = { showMoreMenu = false; onOpenManagement() }
                    )
                    DropdownMenuItem(
                        text = { Text("Share Room") },
                        onClick = { showMoreMenu = false; onShareRoom() }
                    )
                    DropdownMenuItem(
                        text = { Text("Report Room", color = ErrorRed) },
                        onClick = { showMoreMenu = false; onReportRoom() }
                    )
                    DropdownMenuItem(
                        text = { Text("Exit Room", color = ErrorRed) },
                        onClick = { showMoreMenu = false; onExitRoom() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopNavMemberAvatarBadge(
    initials: String,
    accentColor: Color,
    userLevel: Int,
    vipLevel: Int,
    badgeColor: Color,
    badgeTextColor: Color,
    onClick: () -> Unit,
    testTag: String,
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.BottomCenter
    ) {
        AvatarCircle(
            initials = initials,
            accentColor = accentColor,
            size = 30.dp,
            vipLevel = vipLevel
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = badgeColor,
            modifier = Modifier.border(0.6.dp, Color.White, RoundedCornerShape(50))
        ) {
            Text(
                text = "$userLevel",
                style = MaterialTheme.typography.labelSmall,
                color = badgeTextColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
