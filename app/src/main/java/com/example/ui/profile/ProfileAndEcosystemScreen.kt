package com.example.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.ui.components.HelpCenterTealIcon
import com.example.ui.components.MeMenuRow3DIcon
import com.example.ui.components.MyHomeHouse3DIcon
import com.example.ui.components.PlayShowShirt3DIcon
import com.example.ui.components.SecurityCenterTealIcon
import com.example.ui.components.SettingsTealGearIcon
import com.example.ui.components.ShopFront3DIcon
import com.example.ui.components.VipCenterDiamond3DIcon
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.theme.BorderColor
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftHeaderGradientTop
import com.example.ui.theme.TurquoiseActive

private data class MeMenuRow(
    val title: String,
    val iconKey: String,
    val trailingText: String? = null,
    val trailingColor: Color = SecondaryText,
    val onClick: () -> Unit,
)

@Composable
fun ProfileAndEcosystemScreen(
    user: UserProfile,
    settings: WefunAppSettings,
    posts: List<SocialPost>,
    followedUserIds: Set<String>,
    onToggleParentalControl: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenOwnFullProfile: () -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit,
    onOpenAvatarEditor: () -> Unit,
    onOpenMoments: () -> Unit,
    onOpenVipCenter: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenPlayShow: () -> Unit,
    onOpenMyHome: () -> Unit,
    onOpenHelpCenter: () -> Unit = {},
    onOpenSecurityCenter: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onShowToast: (String) -> Unit,
) {
    var activeDetail by remember { mutableStateOf(ProfileMenuDetail.NONE) }

    // Exact 8 vertical menu items with crisp 3D badge icons
    val menuItems = remember(user, settings) {
        listOf(
            MeMenuRow(
                title = "Moments",
                iconKey = "moments",
                onClick = onOpenMoments
            ),
            MeMenuRow(
                title = "Stats",
                iconKey = "stats",
                onClick = { activeDetail = ProfileMenuDetail.STATS }
            ),
            MeMenuRow(
                title = "Visitors",
                iconKey = "visitors",
                trailingText = "${user.visitorsCount} New Visitors",
                trailingColor = OrangeAccent,
                onClick = { activeDetail = ProfileMenuDetail.VISITORS }
            ),
            MeMenuRow(
                title = "Invite Friends",
                iconKey = "invite",
                onClick = { activeDetail = ProfileMenuDetail.INVITE_FRIENDS }
            ),
            MeMenuRow(
                title = "Badge",
                iconKey = "badge",
                onClick = { activeDetail = ProfileMenuDetail.BADGES }
            ),
            MeMenuRow(
                title = "Contributions",
                iconKey = "contributions",
                onClick = { activeDetail = ProfileMenuDetail.CONTRIBUTIONS }
            ),
            MeMenuRow(
                title = "Language",
                iconKey = "language",
                trailingText = user.language,
                trailingColor = SecondaryText,
                onClick = { activeDetail = ProfileMenuDetail.LANGUAGE }
            ),
            MeMenuRow(
                title = "Parental Control Mode",
                iconKey = "parental",
                trailingText = if (settings.parentalControlEnabled) "On" else null,
                trailingColor = TurquoiseActive,
                onClick = { activeDetail = ProfileMenuDetail.PARENTAL_CONTROL }
            ),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        // Soft Header Gradient + Profile Card + 4 Quick Access 3D Icons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(SoftHeaderGradientTop, PrimaryBackground)))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Subtle Top Right Edit / Settings Shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenAvatarEditor,
                        modifier = Modifier.size(48.dp).testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Rounded.Edit, "Edit Profile", tint = PrimaryText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onOpenAppSettings,
                        modifier = Modifier.size(48.dp).testTag("me_settings_button")
                    ) {
                        Icon(Icons.Outlined.Settings, "Settings", tint = PrimaryText, modifier = Modifier.size(20.dp))
                    }
                }

                // Identity Row (Tap opens own FullProfileScreen)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenOwnFullProfile)
                        .testTag("me_profile_header_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Large Rounded-Square Profile Photo matching Ref 1
                    Box(
                        modifier = Modifier
                            .size(74.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, Color.White, RoundedCornerShape(18.dp))
                    ) {
                        if (user.photoUri != null) {
                            AsyncImage(
                                model = user.photoUri,
                                contentDescription = user.nickname,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                contentDescription = user.nickname,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VipNicknameText(
                                name = user.nickname,
                                vipLevel = user.vipLevel,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                customTitle = user.customTitle,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (user.vipLevel > 0) {
                                VipLevelBadge3D(vipLevel = user.vipLevel, compact = true)
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFF0F5)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Favorite,
                                        contentDescription = "WeFun Emblem",
                                        tint = PinkAccent,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp).size(13.dp)
                                    )
                                }
                            }
                        }

                        // Level + Orange Progress Bar + Counter matching Ref 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Lv.${user.userLevel}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            LinearProgressIndicator(
                                progress = { (user.xp.toFloat() / user.nextLevelXp.toFloat()).coerceIn(0f, 1f) },
                                color = OrangeAccent,
                                trackColor = Color(0xFFEAECEF),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            Text(
                                text = "${user.xp}/${user.nextLevelXp}",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Open Full Profile",
                        tint = SecondaryText
                    )
                }

                // 4 Quick-Access 3D Icon Tiles: VIP Center, Shop, PLAY Show (Free), My Home
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MeQuickTile(
                        label = "VIP Center",
                        onClick = onOpenVipCenter,
                        modifier = Modifier.weight(1f).testTag("me_quick_vip_center")
                    ) {
                        VipCenterDiamond3DIcon(size = 44.dp)
                    }
                    MeQuickTile(
                        label = "Shop",
                        onClick = onOpenShop,
                        modifier = Modifier.weight(1f).testTag("me_quick_shop")
                    ) {
                        ShopFront3DIcon(size = 44.dp, showDot = true)
                    }
                    MeQuickTile(
                        label = "PLAY Show",
                        onClick = onOpenPlayShow,
                        modifier = Modifier.weight(1f).testTag("me_quick_play_show")
                    ) {
                        PlayShowShirt3DIcon(size = 44.dp)
                    }
                    MeQuickTile(
                        label = "My Home",
                        onClick = onOpenMyHome,
                        modifier = Modifier.weight(1f).testTag("me_quick_my_home")
                    ) {
                        MyHomeHouse3DIcon(size = 44.dp)
                    }
                }
            }
        }

        // Exact 8 Vertical Menu Items separated by subtle dividers
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBackground)
            ) {
                menuItems.forEachIndexed { idx, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = entry.onClick)
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                            .testTag("profile_menu_${entry.title.lowercase().replace(" ", "_")}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            MeMenuRow3DIcon(kind = entry.iconKey, size = 28.dp)
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (entry.trailingText != null) {
                                Text(
                                    text = entry.trailingText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = entry.trailingColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (idx == 2 || idx == 5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color(0xFFF7F8FA))
                        )
                    } else if (idx < menuItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 54.dp, end = 16.dp),
                            color = BorderColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFF7F8FA))
                )

                // Help Center, Security Center, Settings rows matching uploaded Me reference
                MeSupportVectorRow(
                    title = "Help Center",
                    testTag = "profile_menu_help_center",
                    onClick = onOpenHelpCenter
                ) {
                    HelpCenterTealIcon(size = 22.dp)
                }
                HorizontalDivider(modifier = Modifier.padding(start = 54.dp, end = 16.dp), color = BorderColor)
                MeSupportVectorRow(
                    title = "Security Center",
                    testTag = "profile_menu_security_center",
                    onClick = onOpenSecurityCenter
                ) {
                    SecurityCenterTealIcon(size = 22.dp)
                }
                HorizontalDivider(modifier = Modifier.padding(start = 54.dp, end = 16.dp), color = BorderColor)
                MeSupportVectorRow(
                    title = "Settings",
                    testTag = "profile_menu_settings",
                    onClick = onOpenAppSettings
                ) {
                    SettingsTealGearIcon(size = 22.dp)
                }
            }
        }
    }

    ProfileDetailSheetHost(
        detail = activeDetail,
        user = user,
        settings = settings,
        posts = posts,
        followedUserIds = followedUserIds,
        onDismiss = { activeDetail = ProfileMenuDetail.NONE },
        onOpenAvatarEditor = onOpenAvatarEditor,
        onOpenUserProfile = onOpenUserProfile,
        onToggleParentalControl = onToggleParentalControl,
        onSelectLanguage = onSelectLanguage,
        onToggleFollow = onToggleFollow,
        onShowToast = onShowToast
    )
}

@Composable
private fun MeQuickTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier.height(46.dp),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MeSupportVectorRow(
    title: String,
    testTag: String,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            iconContent()
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Normal,
                color = PrimaryText
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}
