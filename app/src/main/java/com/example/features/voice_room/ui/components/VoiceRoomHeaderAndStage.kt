package com.example.features.voice_room.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioEngineState
import com.example.data.model.MusicTrack
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.VoiceRoom
import com.example.ui.components.AvatarCircle
import com.example.ui.components.LiveVoiceBars
import com.example.ui.components.RoleBadgePill
import com.example.ui.components.RtcStatusChip
import com.example.ui.components.VipBadge
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.components.WefunGradientButton
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun VoiceRoomHeaderBar(
    room: VoiceRoom,
    audioState: AudioEngineState,
    contributionSummary: RoomContributionSummary?,
    isDarkStage: Boolean,
    onOpenThemePicker: () -> Unit,
    onOpenContribution: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentLevel = contributionSummary?.currentLevel ?: room.roomLevel
    val totalPoints = contributionSummary?.totalContribution ?: room.totalContribution
    val nextThreshold = contributionSummary?.nextLevelThreshold ?: totalPoints
    val levelProgress = contributionSummary?.progressFraction ?: 0.25f

    val cardBg = if (isDarkStage) Color.Black.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.94f)
    val borderCol = if (isDarkStage) PremiumGold.copy(alpha = 0.42f) else BorderColor
    val mainTextCol = if (isDarkStage) Color.White else PrimaryText

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RtcStatusChip(
                label = audioState.mode.label,
                isLiveAudioActive = audioState.isCapturingMic,
                modifier = Modifier.weight(1f)
            )

            // Selectable Room Background Theme Chip
            Surface(
                onClick = onOpenThemePicker,
                shape = RoundedCornerShape(50),
                color = cardBg,
                border = BorderStroke(1.dp, borderCol),
                modifier = Modifier.testTag("room_theme_picker_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = "Room theme",
                        tint = if (isDarkStage) PremiumGold else PrimaryPurple,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = room.backdropTheme.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = mainTextCol,
                        maxLines = 1,
                        fontSize = 10.sp
                    )
                }
            }

            // Room Level & Exp Progress Pill
            Surface(
                onClick = onOpenContribution,
                shape = RoundedCornerShape(50),
                color = cardBg,
                border = BorderStroke(1.dp, PremiumGold.copy(alpha = 0.55f)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("room_exp_progress_chip")
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lv.$currentLevel • ${formatCompactCount(totalPoints)}/${formatCompactCount(nextThreshold)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkStage) PremiumGold else PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Room Exp",
                            tint = PremiumGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        color = PremiumGold,
                        trackColor = if (isDarkStage) Color.White.copy(alpha = 0.16f) else BorderColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}

@Composable
fun OwnerAndPartnerStage(
    owner: RoomOwnerInfo?,
    partner: RoomPartnerInfo?,
    isMusicPlaying: Boolean,
    currentTrack: MusicTrack?,
    isDarkStage: Boolean,
    onClickOwner: () -> Unit,
    onClickPartner: () -> Unit,
    onClickMusic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelColor = if (isDarkStage) Color.White else PrimaryText
    val musicBg = if (isDarkStage) Color.Black.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.94f)
    val musicBorder = when {
        isMusicPlaying -> PremiumGold.copy(alpha = 0.75f)
        isDarkStage -> Color.White.copy(alpha = 0.18f)
        else -> BorderColor
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. Dedicated OWNER Main Seat
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(84.dp)
                    .clickable(onClick = onClickOwner)
                    .testTag("room_owner_slot")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (owner != null) {
                        AvatarCircle(
                            initials = owner.initials,
                            accentColor = Color(owner.avatarColorHex),
                            size = 62.dp,
                            vipLevel = owner.vipLevel,
                            isSpeaking = owner.isSpeaking && !owner.isMuted,
                            isVerified = true
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.28f))
                                .border(1.8.dp, PremiumGold, CircleShape)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PremiumGold,
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Text(
                            text = "OWNER",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp)
                        )
                    }
                    if (owner != null) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryPurple,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Text(
                                text = "Lv.${owner.userLevel}",
                                fontSize = 8.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    if (owner?.isSpeaking == true && !owner.isMuted) {
                        LiveVoiceBars(isActive = true, rmsLevel = owner.rmsLevel)
                    }
                    VipNicknameText(
                        name = owner?.displayName ?: "Owner",
                        vipLevel = owner?.vipLevel ?: 0,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        onDarkBackground = isDarkStage,
                    )
                }
            }

            if (partner?.isEnabled == true) {
                VoiceWaveConnector(
                    isActive = (owner?.isSpeaking == true) || partner.isSpeaking,
                    accent = if (isDarkStage) Color.White.copy(alpha = 0.72f) else PrimaryPurple,
                    widthDp = 20
                )

                // 2. Dedicated PARTNER Seat beside Owner (rendered only when Owner enables it)
                val hasPartner = partner.userId != null && partner.displayName != null
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(84.dp)
                        .clickable(onClick = onClickPartner)
                        .testTag("room_partner_slot")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (hasPartner) {
                            AvatarCircle(
                                initials = partner.initials,
                                accentColor = PrimaryPurple,
                                size = 62.dp,
                                vipLevel = partner.vipLevel,
                                isSpeaking = partner.isSpeaking && !partner.isMuted,
                                isVerified = true
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDarkStage) Color.Black.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.9f)
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.linearGradient(listOf(PremiumGold, PinkAccent)),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (partner.isLocked) Icons.Rounded.Lock else Icons.Rounded.Add,
                                    contentDescription = "Partner slot",
                                    tint = if (isDarkStage) Color.White.copy(alpha = 0.9f) else PrimaryPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryPurple,
                            border = BorderStroke(0.8.dp, PremiumGold),
                            modifier = Modifier.align(Alignment.TopCenter)
                        ) {
                            Text(
                                text = "PARTNER",
                                style = MaterialTheme.typography.labelSmall,
                                color = PremiumGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                        if (hasPartner) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PinkAccent,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                Text(
                                    text = "♥ ${partner.relationshipDays}d",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = partner.displayName ?: "Assign Partner",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hasPartner) PremiumGold else labelColor.copy(alpha = 0.78f),
                        fontWeight = if (hasPartner) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Compact Music Player to the right (matching reference screenshots 1, 2, 4)
        Surface(
            onClick = onClickMusic,
            shape = RoundedCornerShape(50),
            color = musicBg,
            border = BorderStroke(1.dp, musicBorder),
            modifier = Modifier
                .width(88.dp)
                .testTag("room_music_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMusicPlaying) PrimaryPurple
                            else if (isDarkStage) Color.White.copy(alpha = 0.14f)
                            else PrimaryPurple.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Music player",
                        tint = if (isMusicPlaying) PremiumGold else if (isDarkStage) Color.White else PrimaryPurple,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isMusicPlaying) (currentTrack?.title ?: "Playing") else "No Songs",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMusicPlaying) PremiumGold else labelColor.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VoiceRoomAnnouncementCard(
    announcement: String,
    isDarkStage: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkStage) Color.Black.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, if (isDarkStage) PremiumGold.copy(alpha = 0.32f) else BorderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_announcement_card")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Campaign,
                contentDescription = "Room Announcement",
                tint = if (isDarkStage) PremiumGold else PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Announcement: $announcement",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkStage) PremiumGold else PrimaryText,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun VoiceWaveConnector(
    isActive: Boolean,
    accent: Color,
    widthDp: Int = 24,
) {
    Canvas(
        modifier = Modifier
            .width(widthDp.dp)
            .height(20.dp)
    ) {
        val midY = size.height / 2f
        val w = size.width
        val amp = if (isActive) size.height * 0.4f else size.height * 0.18f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.25f, midY)
            lineTo(w * 0.40f, midY - amp)
            lineTo(w * 0.55f, midY + amp)
            lineTo(w * 0.72f, midY - amp * 0.5f)
            lineTo(w * 0.82f, midY)
            lineTo(w, midY)
        }
        drawPath(
            path = path,
            color = if (isActive) accent else accent.copy(alpha = 0.35f),
            style = Stroke(width = 2.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerPartnerModalSheet(
    mode: String, // "OWNER" or "PARTNER"
    owner: RoomOwnerInfo?,
    partner: RoomPartnerInfo?,
    memberRoles: List<RoomMemberRoleEntry>,
    currentUserRole: RoomRole,
    onAssignPartner: (userId: String, displayName: String) -> Unit,
    onRemovePartner: () -> Unit,
    onSendGift: () -> Unit,
    onOpenManagement: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mode == "OWNER" && owner != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AvatarCircle(
                        initials = owner.initials,
                        accentColor = Color(owner.avatarColorHex),
                        size = 56.dp,
                        vipLevel = owner.vipLevel,
                        isVerified = true
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VipNicknameText(
                                name = owner.displayName,
                                vipLevel = owner.vipLevel,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            RoleBadgePill(role = RoomRole.OWNER)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Lv.${owner.userLevel}", style = MaterialTheme.typography.labelMedium, color = PrimaryPurple, fontWeight = FontWeight.Bold)
                            VipLevelBadge3D(vipLevel = owner.vipLevel, compact = true)
                            Text("• Charm ${formatCompactCount(owner.charmCount)}", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                    }
                }
                Text(
                    text = "Main Owner position holds full authority over room appearance, seat locks, partner appointment, admins, and ownership transfer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WefunGradientButton(
                        text = "Send Gift to Owner",
                        onClick = { onDismiss(); onSendGift() },
                        modifier = Modifier.weight(1f)
                    )
                    if (currentUserRole.canModerate()) {
                        OutlinedButton(
                            onClick = { onDismiss(); onOpenManagement() },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) { Text("Manage Room") }
                    }
                }
            } else {
                val hasPartner = partner?.userId != null && partner.displayName != null
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (hasPartner && partner != null) {
                        AvatarCircle(
                            initials = partner.initials,
                            accentColor = PrimaryPurple,
                            size = 56.dp,
                            vipLevel = partner.vipLevel,
                            isVerified = true
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple.copy(alpha = 0.1f))
                                .border(1.5.dp, PrimaryPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Favorite, null, tint = PinkAccent, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = partner?.displayName ?: "Room Partner Seat",
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryText
                            )
                            RoleBadgePill(role = RoomRole.PARTNER)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (hasPartner && partner != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = PinkAccent.copy(alpha = 0.14f)
                                ) {
                                    Text(
                                        text = "Partner • ${partner.partnerSinceLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PinkAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text("Lv.${partner.userLevel}", style = MaterialTheme.typography.labelMedium, color = PrimaryPurple)
                            }
                        } else {
                            Text(
                                text = "Dedicated Co-Owner throne beside the Room Owner.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }
                }

                if (hasPartner) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WefunGradientButton(
                            text = "Gift Partner",
                            onClick = { onDismiss(); onSendGift() },
                            modifier = Modifier.weight(1f)
                        )
                        if (currentUserRole == RoomRole.OWNER) {
                            OutlinedButton(
                                onClick = {
                                    onRemovePartner()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.weight(1f).height(50.dp).testTag("remove_partner_button")
                            ) {
                                Icon(Icons.Rounded.PersonRemove, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Remove Partner", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                if (currentUserRole == RoomRole.OWNER) {
                    Text(
                        text = if (hasPartner) "Change Partner (Select Member)" else "Assign Partner (Select Member)",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                    val candidates = memberRoles.filter { it.role != RoomRole.OWNER }
                    LazyColumn(
                        modifier = Modifier.height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(candidates, key = { it.userId }) { candidate ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAssignPartner(candidate.userId, candidate.displayName)
                                        onDismiss()
                                    }
                                    .testTag("assign_partner_candidate_${candidate.userId}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AvatarCircle(
                                            initials = candidate.initials,
                                            accentColor = PrimaryPurple,
                                            size = 34.dp,
                                            vipLevel = candidate.vipLevel
                                        )
                                        Column {
                                            Text(candidate.displayName, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                            Text("Lv.${candidate.userLevel} • ${candidate.role.label}", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                                        }
                                    }
                                    Icon(
                                        imageVector = if (hasPartner) Icons.Rounded.SwapHoriz else Icons.Rounded.Add,
                                        contentDescription = "Assign Partner",
                                        tint = PrimaryPurple
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
