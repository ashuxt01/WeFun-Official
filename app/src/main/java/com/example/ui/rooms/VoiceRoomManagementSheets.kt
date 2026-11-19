package com.example.ui.rooms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AnnouncementCategory
import com.example.data.model.MicRequest
import com.example.data.model.MusicTrack
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomLevelUpEvent
import com.example.data.model.RoomRole
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SongRequestItem
import com.example.data.repository.VoiceRoomSeedData
import com.example.ui.components.GlassCard
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.viewmodel.LiveRoomSessionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoomManagementPanelSheet(
    session: LiveRoomSessionState,
    levelConfigs: List<RoomLevelConfig>,
    activityLogs: List<RoomActivityLogEntry>,
    onAcceptMicRequest: (MicRequest) -> Unit,
    onRejectMicRequest: (String) -> Unit,
    onToggleSeatLock: (Int) -> Unit,
    onToggleMicLock: (Int) -> Unit,
    onToggleMuteOccupant: (Int) -> Unit,
    onReserveSeat: (Int, String?) -> Unit,
    onAssignUserToSeat: (Int, String, String, Int) -> Unit,
    onRemoveFromSeat: (Int) -> Unit,
    onBanUser: (String, String) -> Unit,
    onAssignRole: (String, String, RoomRole) -> Unit,
    onPublishAnnouncement: (AnnouncementCategory, String, String) -> Unit,
    onUpdateAppearance: (String, RoomThemeStyle, String, Boolean) -> Unit,
    onUpdateAdminLevelConfig: (Map<Int, Int>, Double, Double) -> Unit,
    onTogglePartnerSeatEnabled: (Boolean) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val room = session.room ?: return
    val wefunColors = LocalWefunColors.current
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Seats", "Members", "Announce", "Admin")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AdminPanelSettings, null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Room Management Panel", style = MaterialTheme.typography.titleLarge)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PurplePrimary.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = session.currentUserRole.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent) {
                tabs.forEachIndexed { idx, label ->
                    Tab(selected = tabIndex == idx, onClick = { tabIndex = idx }, text = { Text(label, maxLines = 1) })
                }
            }

            when (tabIndex) {
                0 -> {
                    var selectedSeatIdx by remember { mutableIntStateOf(0) }
                    var reserveName by remember { mutableStateOf("") }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        item {
                            Text("Mic Requests (${session.micRequests.size})", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                            if (session.micRequests.isEmpty()) {
                                Text("No pending microphone requests.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                            }
                        }
                        items(session.micRequests, key = { it.userId }) { req ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, wefunColors.glassBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(req.userName, style = MaterialTheme.typography.titleSmall)
                                        Text("Lv.${req.userLevel} • VIP ${req.vipLevel}", style = MaterialTheme.typography.bodySmall, color = CyanPulse)
                                    }
                                    if (session.currentUserRole.canModerate()) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(onClick = { onRejectMicRequest(req.userId) }) { Text("Reject") }
                                            Button(onClick = { onAcceptMicRequest(req) }) { Text("Approve") }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Seat Matrix (${session.seats.size} Regular Seats)", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                session.seats.forEach { seat ->
                                    val label = when {
                                        seat.occupantName != null -> "#${seat.seatIndex + 1} ${seat.occupantName.take(6)}"
                                        seat.isSeatLocked -> "#${seat.seatIndex + 1} 🔒"
                                        seat.reservedForName != null -> "#${seat.seatIndex + 1} ★"
                                        else -> "#${seat.seatIndex + 1} +"
                                    }
                                    FilterChip(
                                        selected = selectedSeatIdx == seat.seatIndex,
                                        onClick = { selectedSeatIdx = seat.seatIndex },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        item {
                            val activeSeat = session.seats.getOrNull(selectedSeatIdx)
                            if (activeSeat != null && session.currentUserRole.canModerate()) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Seat #${activeSeat.seatIndex + 1}: ${activeSeat.occupantName ?: if (activeSeat.isSeatLocked) "Locked" else "Open"}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = GoldAccent
                                        )
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            FilterChip(
                                                selected = activeSeat.isSeatLocked,
                                                onClick = { onToggleSeatLock(activeSeat.seatIndex) },
                                                label = { Text(if (activeSeat.isSeatLocked) "Unlock Seat" else "Lock Seat") }
                                            )
                                            FilterChip(
                                                selected = activeSeat.isMicLocked,
                                                onClick = { onToggleMicLock(activeSeat.seatIndex) },
                                                label = { Text(if (activeSeat.isMicLocked) "Unlock Mic" else "Lock Mic") }
                                            )
                                            if (activeSeat.occupantId != null) {
                                                FilterChip(
                                                    selected = activeSeat.isMuted,
                                                    onClick = { onToggleMuteOccupant(activeSeat.seatIndex) },
                                                    label = { Text(if (activeSeat.isMuted) "Unmute" else "Mute") }
                                                )
                                                FilterChip(
                                                    selected = false,
                                                    onClick = { onRemoveFromSeat(activeSeat.seatIndex) },
                                                    label = { Text("Remove") }
                                                )
                                            } else {
                                                FilterChip(
                                                    selected = false,
                                                    onClick = { onAssignUserToSeat(activeSeat.seatIndex, "sp_4", "Zain Flow", 3) },
                                                    label = { Text("Assign Zain") }
                                                )
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = reserveName,
                                                onValueChange = { reserveName = it },
                                                placeholder = { Text("Reserve for nickname...") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Button(onClick = {
                                                onReserveSeat(activeSeat.seatIndex, reserveName.ifBlank { null })
                                                reserveName = ""
                                            }) {
                                                Text(if (reserveName.isBlank() && activeSeat.reservedForName != null) "Clear" else "Reserve")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        item {
                            Text(
                                text = "7-Tier Role Hierarchy: Owner > Partner > Super Admin > Room Admin > Moderator > VIP > Member",
                                style = MaterialTheme.typography.bodySmall,
                                color = wefunColors.mutedText
                            )
                        }
                        items(session.memberRoles, key = { it.userId }) { member ->
                            val isBanned = member.userId in session.bannedUserIds
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(member.displayName, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                text = "Lv.${member.userLevel} • VIP ${member.vipLevel} • ${if (isBanned) "BANNED" else member.role.label}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isBanned) RoseNeon else GoldAccent
                                            )
                                        }
                                        if (session.currentUserRole.canModerate() && member.role != RoomRole.OWNER && !isBanned) {
                                            OutlinedButton(onClick = { onBanUser(member.userId, member.displayName) }) {
                                                Text("Ban", color = RoseNeon)
                                            }
                                        }
                                    }
                                    if (session.currentUserRole.canManageRoles() && member.role != RoomRole.OWNER) {
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            val assignable = listOf(
                                                RoomRole.PARTNER,
                                                RoomRole.SUPER_ADMIN,
                                                RoomRole.ROOM_ADMIN,
                                                RoomRole.MODERATOR,
                                                RoomRole.NORMAL_MEMBER
                                            )
                                            assignable.forEach { targetRole ->
                                                if (targetRole != RoomRole.PARTNER || session.currentUserRole == RoomRole.OWNER) {
                                                    FilterChip(
                                                        selected = member.role == targetRole,
                                                        onClick = { onAssignRole(member.userId, member.displayName, targetRole) },
                                                        label = { Text(targetRole.label) }
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

                2 -> {
                    var selectedCat by remember { mutableStateOf(AnnouncementCategory.WELCOME) }
                    var annTitle by remember { mutableStateOf("Announcement") }
                    var annBody by remember { mutableStateOf(room.announcement) }
                    var editTitle by remember { mutableStateOf(room.title) }
                    var editTheme by remember { mutableStateOf(room.themeStyle) }
                    var editDecor by remember { mutableStateOf(room.decorationStyle) }
                    var musicAdminsOnly by remember { mutableStateOf(room.musicAdminsOnly) }

                    val unlockedDecorations = remember(levelConfigs, room.roomLevel) {
                        levelConfigs.filter { it.level <= room.roomLevel }.flatMap { it.unlockedDecorations }.distinct()
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        item {
                            Text("Publish Room Announcement", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AnnouncementCategory.entries.forEach { cat ->
                                    FilterChip(
                                        selected = selectedCat == cat,
                                        onClick = { selectedCat = cat },
                                        label = { Text("${cat.emoji} ${cat.label}") }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = annTitle,
                                onValueChange = { annTitle = it },
                                label = { Text("Heading") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = annBody,
                                onValueChange = { annBody = it },
                                label = { Text("Message (supports emojis & rules)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            WefunGradientButton(
                                text = "Publish Announcement",
                                enabled = session.currentUserRole.canModerate(),
                                onClick = { onPublishAnnouncement(selectedCat, annTitle, annBody) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Appearance & Music Permissions", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                            OutlinedTextField(
                                value = editTitle,
                                onValueChange = { editTitle = it },
                                label = { Text("Room Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Room Backdrop Theme", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                RoomThemeStyle.entries.forEach { st ->
                                    FilterChip(selected = editTheme == st, onClick = { editTheme = st }, label = { Text(st.label) })
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Unlocked Level ${room.roomLevel} Decorations", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                unlockedDecorations.forEach { dec ->
                                    FilterChip(selected = editDecor == dec, onClick = { editDecor = dec }, label = { Text(dec) })
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Restrict Music Controls to Admins Only", style = MaterialTheme.typography.bodySmall)
                                Switch(checked = musicAdminsOnly, onCheckedChange = { musicAdminsOnly = it })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Enable Partner Seat Beside Owner", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = "Off by default; only the Room Owner can reveal or hide this seat.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = wefunColors.mutedText
                                    )
                                }
                                Switch(
                                    checked = session.partnerInfo?.isEnabled ?: room.isPartnerSeatEnabled,
                                    onCheckedChange = onTogglePartnerSeatEnabled,
                                    enabled = session.currentUserRole == RoomRole.OWNER,
                                    modifier = Modifier.testTag("room_partner_seat_switch")
                                )
                            }
                            WefunGradientButton(
                                text = "Save Appearance & Permissions",
                                enabled = session.currentUserRole.canManageRoom(),
                                onClick = { onUpdateAppearance(editTitle, editTheme, editDecor, musicAdminsOnly) }
                            )
                        }
                    }
                }

                else -> {
                    val editableSeats = remember(levelConfigs) {
                        mutableStateMapOf<Int, Int>().apply {
                            levelConfigs.forEach { put(it.level, it.seatCount) }
                        }
                    }
                    var pointsPerCoin by remember { mutableDoubleStateOf(0.85) }
                    var diamondsPerCoin by remember { mutableDoubleStateOf(0.50) }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        item {
                            Text("Admin Level 1–10 Seat Capacity & Accounting Rules", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                            Text(
                                text = "Room levels never upgrade manually—they are validated automatically by verified gift contribution rules.",
                                style = MaterialTheme.typography.bodySmall,
                                color = wefunColors.mutedText
                            )
                        }
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Contribution Conversion Rule", style = MaterialTheme.typography.labelLarge, color = CyanPulse)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(0.5, 0.85, 1.0, 1.25).forEach { rate ->
                                            FilterChip(
                                                selected = pointsPerCoin == rate,
                                                onClick = { pointsPerCoin = rate },
                                                label = { Text("${rate}x Pts") }
                                            )
                                        }
                                    }
                                    Text("Level Seat Capacities (Lv.1–6 = 8, Lv.7–10 = 8 or 14)", style = MaterialTheme.typography.labelMedium)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        (1..10).forEach { lvl ->
                                            val defaultSeats = if (lvl < 7) 8 else 14
                                            val cnt = (editableSeats[lvl] ?: defaultSeats).let {
                                                if (lvl < 7) 8 else it.coerceIn(8, 14)
                                            }
                                            FilterChip(
                                                selected = room.roomLevel == lvl,
                                                onClick = {
                                                    editableSeats[lvl] = if (lvl < 7) {
                                                        8
                                                    } else {
                                                        if (cnt == 14) 8 else 14
                                                    }
                                                },
                                                label = { Text("Lv.$lvl: $cnt") }
                                            )
                                        }
                                    }
                                    WefunGradientButton(
                                        text = "Save Backend Progression Rules",
                                        enabled = session.currentUserRole.canManageRoles(),
                                        onClick = { onUpdateAdminLevelConfig(editableSeats.toMap(), pointsPerCoin, diamondsPerCoin) }
                                    )
                                }
                            }
                        }
                        item {
                            Text("Room Activity Logs", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                        }
                        items(activityLogs.take(15), key = { it.id }) { log ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, wefunColors.glassBorder)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                    Text("${log.action} • ${log.actorName}", style = MaterialTheme.typography.labelMedium, color = PurplePrimary)
                                    Text(log.detail, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicQueueSheet(
    session: LiveRoomSessionState,
    onTogglePlayPause: () -> Unit,
    onSelectOrRequestTrack: (MusicTrack) -> Unit,
    onApproveQueueItem: (SongRequestItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    val room = session.room ?: return
    val currentTrack = session.currentTrack

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 580.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LibraryMusic, null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WEFUN Music & Requests", style = MaterialTheme.typography.titleLarge)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PurplePrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (room.musicAdminsOnly) "Admins Only" else "Open Requests",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GoldAccent.copy(alpha = 0.55f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(PurplePrimary, PurpleDeep))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.MusicNote, null, tint = GoldAccent)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(currentTrack?.title ?: "No Song Playing", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = "${currentTrack?.artist ?: "WEFUN Audio"} • ${currentTrack?.licenseNote ?: "Licensed"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyanPulse
                                )
                            }
                        }
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldAccent)
                        ) {
                            Icon(
                                imageVector = if (room.isMusicPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "Play or pause music",
                                tint = CharcoalBackground
                            )
                        }
                    }
                }
            }

            if (session.musicQueue.isNotEmpty()) {
                item {
                    Text("Song Queue (${session.musicQueue.size})", style = MaterialTheme.typography.titleSmall, color = GoldAccent)
                }
                items(session.musicQueue, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, wefunColors.glassBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.track.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = "Requested by ${item.requestedBy} • ${item.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (item.status == "Playing") EmeraldMint else wefunColors.mutedText
                                )
                            }
                            if (session.currentUserRole.canModerate() && item.status != "Playing") {
                                Button(onClick = { onApproveQueueItem(item) }) { Text("Play") }
                            }
                        }
                    }
                }
            }

            item {
                Text("Licensed Track Library", style = MaterialTheme.typography.titleSmall, color = CyanPulse)
            }
            items(VoiceRoomSeedData.licensedTracks, key = { it.id }) { track ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, wefunColors.glassBorder),
                    modifier = Modifier.clickable { onSelectOrRequestTrack(track) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.titleSmall)
                            Text("${track.artist} • ${track.durationLabel}", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        }
                        Text(
                            text = if (session.currentUserRole.canModerate() || !room.musicAdminsOnly) "Play" else "Request",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldAccent
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFeaturesSheet(
    canManageRoom: Boolean,
    isPkActive: Boolean,
    onShareRoom: () -> Unit,
    onInviteFriends: () -> Unit,
    onOpenRoomInfo: () -> Unit,
    onOpenContributions: () -> Unit,
    onOpenManagement: () -> Unit,
    onOpenRoomSettings: () -> Unit = {},
    onOpenMusic: () -> Unit,
    onTogglePk: () -> Unit,
    onPlayDiamondBingo: () -> Unit = {},
    onReportRoom: () -> Unit,
    onExitRoom: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Room Features & Tools", style = MaterialTheme.typography.titleLarge)

            val actions = listOf(
                Triple("Share Room", Icons.Rounded.Share, onShareRoom),
                Triple("Invite Friends", Icons.Rounded.PersonAdd, onInviteFriends),
                Triple("Room Info", Icons.Rounded.Info, onOpenRoomInfo),
                Triple("Contributions", Icons.Rounded.EmojiEvents, onOpenContributions),
                Triple(if (canManageRoom) "Manage Room" else "Room Staff", Icons.Rounded.AdminPanelSettings, onOpenManagement),
                Triple("Room Settings", Icons.Rounded.Settings, onOpenRoomSettings),
                Triple("Music Queue", Icons.Rounded.LibraryMusic, onOpenMusic),
                Triple("Diamond Bingo", Icons.Rounded.AutoAwesome, onPlayDiamondBingo),
                Triple(if (isPkActive) "End PK" else "PK Battle", Icons.Rounded.Bolt, onTogglePk),
                Triple("Report Room", Icons.Rounded.Report, onReportRoom),
                Triple("Exit Room", Icons.Rounded.ExitToApp, onExitRoom),
            )

            actions.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { (label, icon, action) ->
                        val isDanger = label == "Report Room" || label == "Exit Room"
                        Surface(
                            onClick = {
                                onDismiss()
                                action()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isDanger) RoseNeon.copy(alpha = 0.45f) else LocalWefunColors.current.glassBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isDanger) RoseNeon else PurplePrimary
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomLevelUpOverlay(
    event: RoomLevelUpEvent?,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = event != null,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
        if (event != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = CharcoalCard,
                    border = BorderStroke(2.dp, GoldAccent),
                    modifier = Modifier.padding(28.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(PurplePrimary.copy(alpha = 0.45f), CharcoalCard)
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("✨ ROOM LEVEL UP! ✨", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                        Text(
                            text = "Level ${event.newLevel}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${event.newSeatCount} Golden Mic Seats Unlocked!",
                            style = MaterialTheme.typography.titleSmall,
                            color = CyanPulse
                        )
                        if (event.unlockedDecorations.isNotEmpty()) {
                            Text(
                                text = "Unlocked: ${event.unlockedDecorations.joinToString(" • ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = CharcoalBackground)
                        ) {
                            Text("Awesome!", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
