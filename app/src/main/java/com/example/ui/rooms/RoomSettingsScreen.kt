package com.example.ui.rooms

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomMode
import com.example.data.model.RoomRole
import com.example.data.model.RoomThemeStyle
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

/**
 * Complete white list-based Room Settings screen matching the Settings list reference:
 * Title, Cover, Announcement, Room Mode (Normal / Auction), Room Tag, Background, Theme,
 * Partner Seat, Audio/Chat Moderation switches, Password, Membership Fees, Admin/Moderator Roles,
 * Blocklist, Operation Record, and Copyable Room ID.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomSettingsScreen(
    room: VoiceRoom,
    currentUserRole: RoomRole,
    memberRoles: List<RoomMemberRoleEntry>,
    activityLogs: List<RoomActivityLogEntry>,
    onBack: () -> Unit,
    onSwitchRoomMode: (RoomMode) -> Unit,
    onOpenAuctionRelationshipSettings: () -> Unit,
    onTogglePartnerSeat: (Boolean) -> Unit,
    onAssignMemberRole: (userId: String, name: String, role: RoomRole) -> Unit,
    onSaveFullSettings: (
        title: String,
        announcement: String,
        password: String?,
        roomMode: RoomMode,
        tags: List<String>,
        backdropTheme: VoiceRoomBackdropTheme,
        highQualityAudio: Boolean,
        banTextChatting: Boolean,
        banPhotoChatting: Boolean,
        banRedPacket: Boolean,
        membershipFeeCoins: Long,
        musicAdminsOnly: Boolean,
    ) -> Unit,
    onShowToast: (String) -> Unit,
) {
    BackHandler(onBack = onBack)
    val clipboard = LocalClipboardManager.current
    val canManage = currentUserRole.canManageRoom()

    var title by remember(room.id) { mutableStateOf(room.title) }
    var announcement by remember(room.id) { mutableStateOf(room.announcement) }
    var roomMode by remember(room.id, room.roomMode) { mutableStateOf(room.roomMode) }
    var tags by remember(room.id) { mutableStateOf(room.tags) }
    var backdropTheme by remember(room.id) { mutableStateOf(room.backdropTheme) }
    var themeStyle by remember(room.id) { mutableStateOf(room.themeStyle) }
    var partnerSeatEnabled by remember(room.id) { mutableStateOf(room.isPartnerSeatEnabled) }
    var highQualityAudio by remember(room.id) { mutableStateOf(room.highQualityAudio) }
    var banTextChatting by remember(room.id) { mutableStateOf(room.banTextChatting) }
    var banPhotoChatting by remember(room.id) { mutableStateOf(room.banPhotoChatting) }
    var banRedPacket by remember(room.id) { mutableStateOf(room.banRedPacket) }
    var passwordEnabled by remember(room.id) { mutableStateOf(!room.password.isNullOrBlank()) }
    var passwordPin by remember(room.id) { mutableStateOf(room.password ?: "") }
    var membershipFeeCoins by remember(room.id) { mutableLongStateOf(room.membershipFeeCoins) }
    var musicAdminsOnly by remember(room.id) { mutableStateOf(room.musicAdminsOnly) }

    var activeDialog by remember { mutableStateOf<String?>(null) }

    fun persistSettings() {
        if (!canManage) {
            onShowToast("Only Room Owner or Room Admins can modify settings.")
            return
        }
        onSaveFullSettings(
            title,
            announcement,
            if (passwordEnabled) passwordPin.ifBlank { "1234" } else null,
            roomMode,
            tags,
            backdropTheme,
            highQualityAudio,
            banTextChatting,
            banPhotoChatting,
            banRedPacket,
            membershipFeeCoins,
            musicAdminsOnly
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("room_settings_screen"),
        color = PrimaryBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("room_settings_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PrimaryText)
                    }
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }

                if (canManage) {
                    Button(
                        onClick = {
                            persistSettings()
                            onBack()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive),
                        modifier = Modifier.padding(end = 8.dp).testTag("room_settings_save_button")
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SecondaryBackground,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.Lock, null, tint = SecondaryText, modifier = Modifier.size(13.dp))
                            Text("View Only", fontSize = 11.sp, color = SecondaryText)
                        }
                    }
                }
            }
            HorizontalDivider(color = BorderColor)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SettingsNavRow(
                        label = "Title",
                        value = title,
                        testTag = "setting_row_title",
                        onClick = { if (canManage) activeDialog = "title" else onShowToast("Admin permission required") }
                    )
                    SettingsDivider()

                    // Cover row with circular swatch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (canManage) activeDialog = "theme" }
                            .padding(horizontal = 18.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cover", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(themeStyle.primaryHex))
                            )
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
                        }
                    }
                    SettingsDivider()

                    SettingsNavRow(
                        label = "Announcement",
                        value = announcement.take(24),
                        testTag = "setting_row_announcement",
                        onClick = { if (canManage) activeDialog = "announcement" }
                    )
                    SettingsDivider()

                    SettingsNavRow(
                        label = "Room Mode",
                        value = if (roomMode == RoomMode.AUCTION) "Auction" else "Normal",
                        valueColor = if (roomMode == RoomMode.AUCTION) PrimaryPurple else SecondaryText,
                        testTag = "room_mode_setting_row",
                        onClick = { if (canManage) activeDialog = "mode" else onShowToast("Only Owner/Admin can switch Room Mode") }
                    )

                    if (roomMode == RoomMode.AUCTION) {
                        SettingsDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PrimaryPurple.copy(alpha = 0.06f))
                                .clickable { onOpenAuctionRelationshipSettings() }
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                                .testTag("room_settings_open_auction_relationship"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Rounded.Gavel, null, tint = PrimaryPurple, modifier = Modifier.size(18.dp))
                                Text("Auction Relationship Settings", style = MaterialTheme.typography.titleSmall, color = PrimaryPurple, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = PrimaryPurple, modifier = Modifier.size(18.dp))
                        }
                    }
                    SettingsDivider()

                    SettingsNavRow(
                        label = "Room Tag",
                        value = tags.firstOrNull() ?: "Friends",
                        testTag = "setting_row_tag",
                        onClick = { if (canManage) activeDialog = "tags" }
                    )
                    SettingsDivider()

                    SettingsNavRow(
                        label = "Room Background",
                        value = backdropTheme.label,
                        testTag = "setting_row_backdrop",
                        onClick = { if (canManage) activeDialog = "backdrop" }
                    )
                    SettingsDivider()

                    SettingsNavRow(
                        label = "Room Theme",
                        value = themeStyle.label,
                        onClick = { if (canManage) activeDialog = "theme" }
                    )

                    SettingsSectionSpacer()

                    SettingsSwitchRow(
                        label = "Partner Seat",
                        checked = partnerSeatEnabled,
                        enabled = currentUserRole == RoomRole.OWNER,
                        testTag = "setting_switch_partner_seat",
                        onCheckedChange = {
                            partnerSeatEnabled = it
                            onTogglePartnerSeat(it)
                        }
                    )

                    SettingsSectionSpacer()

                    SettingsSwitchRow(
                        label = "High Quality Mode",
                        checked = highQualityAudio,
                        enabled = canManage,
                        testTag = "setting_switch_hq",
                        onCheckedChange = { highQualityAudio = it }
                    )
                    SettingsDivider()
                    SettingsSwitchRow(
                        label = "Ban Text Chatting",
                        checked = banTextChatting,
                        enabled = canManage,
                        testTag = "setting_switch_ban_text",
                        onCheckedChange = { banTextChatting = it }
                    )
                    SettingsDivider()
                    SettingsSwitchRow(
                        label = "Ban Photo Chatting",
                        checked = banPhotoChatting,
                        enabled = canManage,
                        testTag = "setting_switch_ban_photo",
                        onCheckedChange = { banPhotoChatting = it }
                    )
                    SettingsDivider()
                    SettingsSwitchRow(
                        label = "Ban Red Packet",
                        checked = banRedPacket,
                        enabled = canManage,
                        testTag = "setting_switch_ban_red_packet",
                        onCheckedChange = { banRedPacket = it }
                    )
                    SettingsDivider()
                    SettingsSwitchRow(
                        label = "Music Admins Only",
                        checked = musicAdminsOnly,
                        enabled = canManage,
                        testTag = "setting_switch_music_admins",
                        onCheckedChange = { musicAdminsOnly = it }
                    )

                    SettingsSectionSpacer()

                    SettingsSwitchRow(
                        label = "Password",
                        checked = passwordEnabled,
                        enabled = canManage,
                        testTag = "setting_switch_password",
                        onCheckedChange = {
                            passwordEnabled = it
                            if (it && passwordPin.isBlank()) passwordPin = "8564"
                        }
                    )
                    if (passwordEnabled) {
                        SettingsDivider()
                        SettingsNavRow(
                            label = "Passcode PIN",
                            value = passwordPin.ifBlank { "Set PIN" },
                            testTag = "setting_row_password_pin",
                            onClick = { if (canManage) activeDialog = "password" }
                        )
                    }

                    SettingsSectionSpacer()

                    SettingsNavRow(
                        label = "Membership Fees",
                        value = if (membershipFeeCoins == 0L) "Free" else "$membershipFeeCoins Coins",
                        testTag = "setting_row_membership_fee",
                        onClick = { if (canManage) activeDialog = "fee" }
                    )
                    SettingsDivider()
                    SettingsNavRow(
                        label = "Admin & Moderator",
                        value = "${memberRoles.count { it.role.canModerate() }} staff",
                        testTag = "setting_row_admin_roles",
                        onClick = { activeDialog = "admins" }
                    )
                    SettingsDivider()
                    SettingsNavRow(
                        label = "Operation Record",
                        value = "${activityLogs.size} logs",
                        testTag = "setting_row_logs",
                        onClick = { activeDialog = "logs" }
                    )
                    SettingsDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboard.setText(AnnotatedString(room.id))
                                onShowToast("Copied Room ID ${room.id}")
                            }
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                            .testTag("setting_row_room_id"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Room ID", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(room.id, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                            Icon(Icons.Rounded.ContentCopy, "Copy Room ID", tint = SecondaryText, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }

    // Inline Dialog Editors
    when (activeDialog) {
        "title" -> {
            var draft by remember { mutableStateOf(title) }
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Title") },
                text = { OutlinedTextField(value = draft, onValueChange = { draft = it }, singleLine = true) },
                confirmButton = {
                    TextButton(onClick = {
                        if (draft.isNotBlank()) title = draft.trim()
                        activeDialog = null
                    }) { Text("Apply", color = TurquoiseActive) }
                },
                dismissButton = { TextButton(onClick = { activeDialog = null }) { Text("Cancel") } }
            )
        }
        "announcement" -> {
            var draft by remember { mutableStateOf(announcement) }
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Announcement") },
                text = { OutlinedTextField(value = draft, onValueChange = { draft = it }, maxLines = 4) },
                confirmButton = {
                    TextButton(onClick = {
                        if (draft.isNotBlank()) announcement = draft.trim()
                        activeDialog = null
                    }) { Text("Apply", color = TurquoiseActive) }
                },
                dismissButton = { TextButton(onClick = { activeDialog = null }) { Text("Cancel") } }
            )
        }
        "mode" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Select Room Mode") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomMode.entries.forEach { m ->
                            val picked = roomMode == m
                            Surface(
                                onClick = {
                                    roomMode = m
                                    onSwitchRoomMode(m)
                                    activeDialog = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (picked) TurquoiseActive.copy(alpha = 0.14f) else SecondaryBackground,
                                border = BorderStroke(1.dp, if (picked) TurquoiseActive else BorderColor),
                                modifier = Modifier.fillMaxWidth().testTag("select_room_mode_${m.name.lowercase()}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(m.label, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Text(
                                        text = if (m == RoomMode.AUCTION) {
                                            "Purple & gold live bidding stage with Relationship Tags & Tokens"
                                        } else {
                                            "Standard voice room seating, owner/partner stage & music"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SecondaryText
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Close") } }
            )
        }
        "tags" -> {
            val presets = listOf("Friends", "Music", "Auction", "PK", "Games", "Chill", "VIP")
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Tags") },
                text = {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        presets.forEach { tag ->
                            val has = tag in tags
                            FilterChip(
                                selected = has,
                                onClick = { tags = if (has) tags - tag else tags + tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Done", color = TurquoiseActive) } }
            )
        }
        "backdrop" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Background") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        VoiceRoomBackdropTheme.entries.forEach { bg ->
                            FilterChip(
                                selected = backdropTheme == bg,
                                onClick = { backdropTheme = bg; activeDialog = null },
                                label = { Text(bg.label) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Close") } }
            )
        }
        "theme" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Theme") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        RoomThemeStyle.entries.forEach { ts ->
                            FilterChip(
                                selected = themeStyle == ts,
                                onClick = { themeStyle = ts; activeDialog = null },
                                label = { Text(ts.label) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Close") } }
            )
        }
        "password" -> {
            var pin by remember { mutableStateOf(passwordPin) }
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Room Passcode PIN") },
                text = { OutlinedTextField(value = pin, onValueChange = { pin = it.take(8) }, label = { Text("4-8 Digit PIN") }, singleLine = true) },
                confirmButton = {
                    TextButton(onClick = {
                        passwordPin = pin.ifBlank { "8564" }
                        activeDialog = null
                    }) { Text("Save PIN", color = TurquoiseActive) }
                },
                dismissButton = { TextButton(onClick = { activeDialog = null }) { Text("Cancel") } }
            )
        }
        "fee" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Membership Entry Fee") },
                text = {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0L, 20L, 50L, 100L).forEach { fee ->
                            FilterChip(
                                selected = membershipFeeCoins == fee,
                                onClick = { membershipFeeCoins = fee; activeDialog = null },
                                label = { Text(if (fee == 0L) "Free" else "$fee Coins") }
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Done", color = TurquoiseActive) } }
            )
        }
        "admins" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Admins & Moderators") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (memberRoles.isEmpty()) {
                            Text("No custom staff roles assigned yet.", color = SecondaryText)
                        } else {
                            memberRoles.forEach { m ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(m.displayName, fontWeight = FontWeight.Bold)
                                        Text(m.role.label, fontSize = 11.sp, color = TurquoiseDeep)
                                    }
                                    if (canManage && m.role != RoomRole.OWNER) {
                                        val nextRole = when (m.role) {
                                            RoomRole.ROOM_ADMIN -> RoomRole.MODERATOR
                                            RoomRole.MODERATOR -> RoomRole.NORMAL_MEMBER
                                            else -> RoomRole.ROOM_ADMIN
                                        }
                                        TextButton(onClick = { onAssignMemberRole(m.userId, m.displayName, nextRole) }) {
                                            Text("Set ${nextRole.label}", fontSize = 11.sp, color = TurquoiseActive)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Close") } }
            )
        }
        "logs" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Operation Record") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        activityLogs.take(8).forEach { log ->
                            Text("• ${log.actorName}: ${log.action} (${log.detail})", fontSize = 12.sp, color = PrimaryText)
                        }
                        if (activityLogs.isEmpty()) {
                            Text("No operations logged yet.", color = SecondaryText)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { activeDialog = null }) { Text("Close") } }
            )
        }
    }
}

@Composable
private fun SettingsNavRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = SecondaryText,
    testTag: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = valueColor)
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TurquoiseActive
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 18.dp),
        color = BorderColor
    )
}

@Composable
private fun SettingsSectionSpacer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFF7F8FA))
    )
}
