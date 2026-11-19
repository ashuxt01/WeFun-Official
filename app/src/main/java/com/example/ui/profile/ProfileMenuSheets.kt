package com.example.ui.profile

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.data.repository.WefunSeedData
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

enum class ProfileMenuDetail {
    NONE,
    STATS,
    VISITORS,
    INVITE_FRIENDS,
    BADGES,
    CONTRIBUTIONS,
    LANGUAGE,
    PARENTAL_CONTROL,
    SETTINGS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailSheetHost(
    detail: ProfileMenuDetail,
    user: UserProfile,
    settings: WefunAppSettings,
    posts: List<SocialPost>,
    followedUserIds: Set<String>,
    onDismiss: () -> Unit,
    onOpenAvatarEditor: () -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit,
    onToggleParentalControl: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onShowToast: (String) -> Unit,
) {
    if (detail == ProfileMenuDetail.NONE) return
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (detail) {
                ProfileMenuDetail.STATS -> {
                    item {
                        Text("Stats", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Account Level: Lv.${user.userLevel} (${user.xp} / ${user.nextLevelXp} XP)", fontWeight = FontWeight.Bold, color = PrimaryText)
                                Text("Followers: ${user.followersCount} • Following: ${user.followingCount}", color = TurquoiseDeep)
                                Text("Friends: ${user.friendsCount} • Visitors: ${user.visitorsCount}", color = PrimaryText)
                                Text("Gifts Received: ${user.giftsReceivedCount} • Sent: ${user.giftsSentCount}", color = GoldAmber, fontWeight = FontWeight.SemiBold)
                                Text("Published Moments: ${posts.size}", color = SecondaryText)
                            }
                        }
                    }
                }

                ProfileMenuDetail.VISITORS -> {
                    item {
                        Text("Recent Visitors (${user.visitorsCount})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Tap any visitor to open their profile.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                    val visitors = listOf(
                        Triple("host_rhea", "DJ Rhea", "Visited 12m ago • VIP 5"),
                        Triple("host_kai", "Kai Storm", "Visited 48m ago • SVIP"),
                        Triple("host_zara", "Zara Moon", "Visited 2h ago • VIP 4"),
                        Triple("host_milo", "Milo Taktik", "Visited Yesterday • VIP 3"),
                    )
                    items(visitors, key = { it.first }) { (uid, name, whenText) ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onDismiss()
                                onOpenUserProfile(uid, name)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AvatarCircle(
                                        initials = name.take(2),
                                        accentColor = TurquoiseActive,
                                        size = 40.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text(whenText, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                    }
                                }
                                Text("View", style = MaterialTheme.typography.labelMedium, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ProfileMenuDetail.INVITE_FRIENDS -> {
                    item {
                        Text("Invite Friends", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Share your WeFun ID (${user.wefunId}) to invite friends!", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Your Invite Code: ${user.wefunId}", style = MaterialTheme.typography.titleMedium, color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                                WefunGradientButton(
                                    text = "Share Invite Link",
                                    leadingIcon = { Icon(Icons.Rounded.Share, null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "Join me on WeFun! Use invite code ${user.wefunId}")
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Invite Friends to WeFun"))
                                    }
                                )
                            }
                        }
                    }
                }

                ProfileMenuDetail.BADGES -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.EmojiEvents, null, tint = GoldAmber)
                            Text("Badges", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                    items(WefunSeedData.achievements, key = { it.id }) { ach ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (ach.isUnlocked) GoldAmber.copy(alpha = 0.5f) else null
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ach.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Text(ach.description, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    ach.progressText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (ach.isUnlocked) EmeraldMint else SecondaryText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                ProfileMenuDetail.CONTRIBUTIONS -> {
                    item {
                        Text("Contributions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Diamonds: ${user.diamonds} • Gifts Sent: ${user.giftsSentCount}", style = MaterialTheme.typography.bodySmall, color = TurquoiseDeep)
                    }
                    val contribs = listOf(
                        Triple("WeFun Official Carnival", "12,400 Pts", "Featured Supporter"),
                        Triple("Celestial House", "48,000 Pts", "Family Rank #1"),
                        Triple("Weekend Quiz Gala", "6,200 Pts", "Top Contributor"),
                    )
                    items(contribs) { (title, pts, badge) ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Text(badge, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                }
                                Text(pts, style = MaterialTheme.typography.labelLarge, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ProfileMenuDetail.LANGUAGE -> {
                    item {
                        Text("Language", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                    val langs = listOf("English", "Hindi", "Arabic", "Spanish", "Turkish", "Indonesian")
                    items(langs) { lang ->
                        val selected = user.language.equals(lang, ignoreCase = true)
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (selected) TurquoiseActive else null,
                            onClick = {
                                onSelectLanguage(lang)
                                onDismiss()
                            }
                        ) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text(lang, style = MaterialTheme.typography.titleSmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = PrimaryText)
                                if (selected) Icon(Icons.Rounded.CheckCircle, null, tint = TurquoiseActive)
                            }
                        }
                    }
                }

                ProfileMenuDetail.PARENTAL_CONTROL -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Shield, null, tint = TurquoiseActive)
                            Text("Parental Control Mode", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Enable Parental Safety Lock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Text("Filters sensitive interactions and restricts coin purchases.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                }
                                Switch(
                                    checked = settings.parentalControlEnabled,
                                    onCheckedChange = onToggleParentalControl
                                )
                            }
                        }
                    }
                }

                ProfileMenuDetail.SETTINGS -> {
                    item {
                        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        onDismiss()
                                        onOpenAvatarEditor()
                                    }.padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Edit Profile & Bio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text("Update nickname, photo, country & privacy", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                    }
                                    Text("Open", color = TurquoiseActive, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Light Pure Theme", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text("Pure white WeFun experience active", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                    }
                                    Text("Active", color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                ProfileMenuDetail.NONE -> {}
            }
        }
    }
}
