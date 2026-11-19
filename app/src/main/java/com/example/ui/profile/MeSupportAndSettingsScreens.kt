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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WefunAppSettings
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.HelpCenterTealIcon
import com.example.ui.components.SecurityCenterTealIcon
import com.example.ui.components.SettingsTealGearIcon
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

private data class FaqEntry(val topic: String, val question: String, val answer: String)

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit,
    onSubmitProblem: (targetType: String, targetName: String, reason: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var query by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            FaqEntry("Voice Rooms", "How do I join a room by ID?", "Tap the dialpad icon at the top of Voice Room Discovery and enter any active Room ID such as 320252 or 1579056."),
            FaqEntry("Auction Mode", "How do Relationship Tags & Bidding work?", "In an Auction Room, tap Settings to choose Ordinary bidding or BFF, pick a relationship tag or custom label, select a reward token, and invite a participant. Relationships activate once the partner accepts."),
            FaqEntry("Gifts & Package", "How do I send items from my Package?", "Open the gift drawer inside any live room and switch to the Package tab to send inventory items for free with full animations."),
            FaqEntry("Safety", "How do I report inappropriate audio or behavior?", "Tap the ... menu inside a Voice Room or use Report a Problem below to alert the WeFun safety team immediately."),
        )
    }

    val filteredFaqs = remember(query) {
        if (query.isBlank()) faqs
        else faqs.filter {
            it.question.contains(query, ignoreCase = true) ||
                it.answer.contains(query, ignoreCase = true) ||
                it.topic.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("help_center_screen")
    ) {
        MeTopBar(
            title = "Help Center",
            onBack = onBack,
            backTag = "help_center_back_button",
            icon = { HelpCenterTealIcon(size = 22.dp) }
        )
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search FAQs, Auction Mode, Gifts...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, tint = TurquoiseActive) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("help_center_search_input")
                )
            }

            items(filteredFaqs, key = { it.question }) { faq ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(shape = RoundedCornerShape(50), color = TurquoiseActive.copy(alpha = 0.12f)) {
                            Text(
                                text = faq.topic,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(faq.question, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text(faq.answer, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { showReportDialog = true },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("help_center_report_button")
                ) {
                    Icon(Icons.Rounded.ReportProblem, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report a Problem", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showReportDialog) {
        var issue by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report a Problem") },
            text = {
                OutlinedTextField(
                    value = issue,
                    onValueChange = { issue = it },
                    label = { Text("Describe the issue") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (issue.isNotBlank()) {
                        onSubmitProblem("Support", "WeFun Help Center", issue.trim())
                    }
                    showReportDialog = false
                }) { Text("Submit", color = TurquoiseActive) }
            },
            dismissButton = { TextButton(onClick = { showReportDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun SecurityCenterScreen(
    user: UserProfile,
    settings: WefunAppSettings,
    onBack: () -> Unit,
    onToggleParentalControl: (Boolean) -> Unit,
    onUnblockUser: (userId: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("security_center_screen")
    ) {
        MeTopBar(
            title = "Security Center",
            onBack = onBack,
            backTag = "security_center_back_button",
            icon = { SecurityCenterTealIcon(size = 22.dp) }
        )
        HorizontalDivider(color = BorderColor)

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
                        Icon(Icons.Rounded.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(34.dp))
                        Column {
                            Text("Account Protection Active", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text(
                                text = "WeFun ID: ${user.wefunId} • Guardian & privacy checks enabled.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Parental Control & Safe Filter", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Filters mature keywords and enforces room guardian notices.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                        Switch(
                            checked = settings.parentalControlEnabled,
                            onCheckedChange = onToggleParentalControl,
                            colors = SwitchDefaults.colors(checkedTrackColor = TurquoiseActive, checkedThumbColor = Color.White)
                        )
                    }
                }
            }

            item {
                Text("Blocked Accounts (${settings.blockedUserIds.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
            }

            if (settings.blockedUserIds.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SecondaryBackground,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No blocked accounts. You can block users from any chat or voice seat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            } else {
                items(settings.blockedUserIds.toList(), key = { it }) { blockedId ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("User $blockedId", fontWeight = FontWeight.SemiBold, color = PrimaryText)
                            OutlinedButton(onClick = { onUnblockUser(blockedId, blockedId) }) {
                                Text("Unblock", color = TurquoiseDeep)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSettingsScreen(
    user: UserProfile,
    settings: WefunAppSettings,
    onBack: () -> Unit,
    onToggleParentalControl: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onUpdateNotificationPrefs: (gifts: Boolean, rooms: Boolean, pk: Boolean, messages: Boolean) -> Unit,
    onOpenAdminPanel: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var showLangDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("app_settings_screen")
    ) {
        MeTopBar(
            title = "Settings",
            onBack = onBack,
            backTag = "app_settings_back_button",
            icon = { SettingsTealGearIcon(size = 22.dp) }
        )
        HorizontalDivider(color = BorderColor)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().background(PrimaryBackground)) {
                    MeSettingSwitchItem(
                        title = "Gift Delivery Alerts",
                        subtitle = "Notify when gifts arrive in your rooms",
                        checked = settings.notifyGifts,
                        onCheckedChange = { onUpdateNotificationPrefs(it, settings.notifyRoomInvites, settings.notifyPkAlerts, settings.notifyMessages) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
                    MeSettingSwitchItem(
                        title = "Voice Room Invites",
                        subtitle = "Alerts when friends open live voice rooms",
                        checked = settings.notifyRoomInvites,
                        onCheckedChange = { onUpdateNotificationPrefs(settings.notifyGifts, it, settings.notifyPkAlerts, settings.notifyMessages) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
                    MeSettingSwitchItem(
                        title = "PK & Auction Updates",
                        subtitle = "Match timers and relationship bid notices",
                        checked = settings.notifyPkAlerts,
                        onCheckedChange = { onUpdateNotificationPrefs(settings.notifyGifts, settings.notifyRoomInvites, it, settings.notifyMessages) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
                    MeSettingSwitchItem(
                        title = "Direct Messages",
                        subtitle = "Unread chat and friend request badges",
                        checked = settings.notifyMessages,
                        onCheckedChange = { onUpdateNotificationPrefs(settings.notifyGifts, settings.notifyRoomInvites, settings.notifyPkAlerts, it) }
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF7F8FA)))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLangDialog = true }
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Language", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        Text(user.language, style = MaterialTheme.typography.bodySmall, color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
                    MeSettingSwitchItem(
                        title = "Parental Control Mode",
                        subtitle = "Keep community safety protections active",
                        checked = settings.parentalControlEnabled,
                        onCheckedChange = onToggleParentalControl
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF7F8FA)))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenAdminPanel)
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                            .testTag("settings_admin_panel_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.AdminPanelSettings, null, tint = TurquoiseActive, modifier = Modifier.size(22.dp))
                            Column {
                                Text("Admin Panel", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                Text("Configure 12-level VIP thresholds, gifts, users & audit logs", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                            }
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF7F8FA)))

                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("About WeFun", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("WeFun Premium Social Voice & Entertainment • Pure Light Edition", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }
        }
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text("App Language") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("English", "Hindi", "Arabic", "Spanish", "Indonesian").forEach { lang ->
                        Surface(
                            onClick = { onSelectLanguage(lang); showLangDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            color = if (user.language == lang) TurquoiseActive.copy(alpha = 0.14f) else SecondaryBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lang,
                                fontWeight = if (user.language == lang) FontWeight.Bold else FontWeight.Normal,
                                color = PrimaryText,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLangDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
private fun MeTopBar(
    title: String,
    onBack: () -> Unit,
    backTag: String,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag(backTag)) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PrimaryText)
        }
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryText
        )
    }
}

@Composable
private fun MeSettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = TurquoiseActive, checkedThumbColor = Color.White)
        )
    }
}
