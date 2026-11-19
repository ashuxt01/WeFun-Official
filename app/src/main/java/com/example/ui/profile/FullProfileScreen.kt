package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AcceptedRelationship
import com.example.data.model.ActiveGiftEffect
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.ProfileBadge
import com.example.data.model.RoomSeat
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.repository.CharmLevelCatalog
import com.example.ui.components.AvatarCircle
import com.example.ui.components.CharmLevelPill
import com.example.ui.components.CountryFlagChip
import com.example.ui.components.GenderVectorBadge
import com.example.ui.components.Profile3DOctagonBadge
import com.example.ui.components.RelationshipTag3DIcon
import com.example.ui.components.UserLevelPill
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.rooms.GiftAnimationOverlay
import com.example.ui.rooms.VirtualGiftDrawerSheet
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun FullProfileScreen(
    profileUser: UserProfile,
    isOwnProfile: Boolean,
    isFollowing: Boolean,
    posts: List<SocialPost>,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFollow: () -> Unit,
    onMessageUser: () -> Unit,
    onPickPhoto: (String) -> Unit,
    onToggleLikePost: (SocialPost) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Extended WeFun Profile Ecosystem params with backward-compatible defaults
    viewerCoins: Long = profileUser.coins,
    packageInventory: Map<String, Int> = emptyMap(),
    giftWallSummary: GiftWallSummary = GiftWallSummary(profileUser.uid, profileUser.giftsReceivedCount, 0L, emptyList(), emptyList()),
    guards: List<GuardRelationship> = emptyList(),
    advancedRooms: List<AdvancedRoomPreview> = emptyList(),
    activeGiftAnimation: ActiveGiftEffect? = null,
    onOpenCharmSystem: () -> Unit = {},
    onOpenMoments: () -> Unit = {},
    onOpenGiftWallCollection: () -> Unit = {},
    onUpdateSignature: (String) -> Unit = {},
    onCancelRelationship: (String) -> Unit = {},
    onOpenGuardList: () -> Unit = {},
    onOpenGuardProfile: (uid: String, name: String) -> Unit = { _, _ -> },
    onEnterRoomById: (String) -> Unit = {},
    onOpenAdvancedRoomList: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onSendGiftToProfile: (gift: VirtualGift, qty: Int, fromPackage: Boolean) -> Unit = { _, _, _ -> },
    onOpenWallet: () -> Unit = {},
) {
    BackHandler(onBack = onBack)
    val clipboard = LocalClipboardManager.current
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null && isOwnProfile) onPickPhoto(uri.toString()) }

    val accessibleMoments = remember(posts, profileUser.uid, profileUser.nickname, isOwnProfile) {
        posts.filter { post ->
            val belongs = post.authorId == profileUser.uid ||
                post.authorName.equals(profileUser.nickname, ignoreCase = true)
            belongs && (isOwnProfile || post.isPublic)
        }
    }

    val charmProgress = remember(profileUser.charmPoints) {
        CharmLevelCatalog.calculateCharmProgress(profileUser.charmPoints)
    }
    val badges = remember(profileUser, charmProgress) {
        CharmLevelCatalog.buildProfileBadges(profileUser, charmProgress)
    }

    var showGiftDrawer by remember { mutableStateOf(false) }
    var showSignatureEditor by remember { mutableStateOf(false) }
    var selectedMomentDetail by remember { mutableStateOf<SocialPost?>(null) }
    var selectedRelationshipDetail by remember { mutableStateOf<AcceptedRelationship?>(null) }
    var selectedBadgeDetail by remember { mutableStateOf<ProfileBadge?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag(if (isOwnProfile) "full_profile_own" else "full_profile_public_${profileUser.uid}")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("full_profile_scroll_list"),
            contentPadding = PaddingValues(bottom = 116.dp),
        ) {
            // 1. Edge-to-Edge Top Cover Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    val coverModel = profileUser.coverPhotoUri ?: profileUser.photoUri
                    when {
                        coverModel != null -> AsyncImage(
                            model = coverModel,
                            contentDescription = "${profileUser.nickname} Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        isOwnProfile -> Image(
                            painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                            contentDescription = profileUser.nickname,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(profileUser.accentColorHex).copy(alpha = 0.90f),
                                            Color(0xFF1F2937)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AvatarCircle(
                                initials = profileUser.nickname.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                accentColor = Color(profileUser.accentColorHex),
                                size = 104.dp,
                                vipLevel = profileUser.vipLevel
                            )
                        }
                    }

                    // Soft dark scrim at top & bottom of cover for crisp button contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.30f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )

                    // Top Navigation Row: Back + Own Edit/Settings or Public Share
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.34f))
                                .testTag("full_profile_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color.White)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isOwnProfile) {
                                IconButton(
                                    onClick = {
                                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.34f))
                                        .testTag("own_profile_edit_button")
                                ) {
                                    Icon(Icons.Rounded.Edit, "Change Photo", tint = Color.White)
                                }
                                IconButton(
                                    onClick = onEditProfile,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.34f))
                                        .testTag("full_profile_edit_icon")
                                ) {
                                    Icon(Icons.Rounded.Verified, "Edit Profile", tint = Color.White)
                                }
                                IconButton(
                                    onClick = onOpenSettings,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.34f))
                                        .testTag("full_profile_settings_icon")
                                ) {
                                    Icon(Icons.Rounded.Settings, "Settings", tint = Color.White)
                                }
                            } else {
                                IconButton(
                                    onClick = { onShowToast("Shared ${profileUser.nickname}'s WeFun profile (ID: ${profileUser.wefunId})") },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.34f))
                                ) {
                                    Icon(Icons.Rounded.Share, "Share Profile", tint = Color.White)
                                }
                            }
                        }
                    }

                    // Floating Relationship Partner Pill on Bottom-Right of Cover (Screenshot 1)
                    val topRel = profileUser.activeRelationships.firstOrNull()
                    if (topRel != null) {
                        Surface(
                            onClick = { selectedRelationshipDetail = topRel },
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.48f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 14.dp, bottom = 32.dp)
                                .testTag("cover_relationship_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RelationshipTag3DIcon(iconKey = topRel.tokenKey, size = 18.dp)
                                Text(
                                    text = topRel.partnerName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                )
                                AvatarCircle(
                                    initials = topRel.partnerName.take(2),
                                    accentColor = Color(0xFF8B5CF6),
                                    size = 24.dp
                                )
                            }
                        }
                    }

                    // Rounded Pure-White Transition Cap overlapping Cover bottom edge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(22.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(PrimaryBackground)
                    )
                }
            }

            // 2. Identity Header Block
            item(key = "profile_identity") {
                val vip12Theme = if (profileUser.vipLevel >= 12) profileUser.customNamecardTheme else "Default"
                val isDarkCard = vip12Theme == "Obsidian Gold" || vip12Theme == "Crimson Sovereign"
                val namecardBrush = when (vip12Theme) {
                    "Obsidian Gold" -> Brush.linearGradient(listOf(Color(0xFF141122), Color(0xFF281D3A)))
                    "Crimson Sovereign" -> Brush.linearGradient(listOf(Color(0xFF330A18), Color(0xFF1E0B24)))
                    "Aurora Silk" -> Brush.linearGradient(listOf(Color(0xFFE6FAF8), Color(0xFFF3EEFF)))
                    else -> Brush.linearGradient(listOf(PrimaryBackground, PrimaryBackground))
                }
                val idSecondaryTint = if (isDarkCard) Color(0xFFD6D3E5) else SecondaryText

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (vip12Theme != "Default") 14.dp else 0.dp)
                        .clip(if (vip12Theme != "Default") RoundedCornerShape(18.dp) else RoundedCornerShape(0.dp))
                        .background(namecardBrush)
                        .then(
                            if (vip12Theme != "Default") {
                                Modifier.border(
                                    1.2.dp,
                                    GoldAmber.copy(alpha = 0.65f),
                                    RoundedCornerShape(18.dp)
                                )
                            } else Modifier
                        )
                        .padding(horizontal = if (vip12Theme != "Default") 14.dp else 18.dp, vertical = if (vip12Theme != "Default") 10.dp else 4.dp)
                        .testTag("profile_namecard_header"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Display Name + VIP Badge + Verified + Follow pill (for public profile)
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
                            VipNicknameText(
                                name = profileUser.nickname,
                                vipLevel = profileUser.vipLevel,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                onDarkBackground = isDarkCard,
                                customTitle = profileUser.customTitle,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (profileUser.vipLevel > 0) {
                                VipLevelBadge3D(vipLevel = profileUser.vipLevel, compact = true)
                            }
                            if (profileUser.isHostVerified) {
                                Icon(Icons.Rounded.Verified, "Verified WeFun Creator", tint = TurquoiseActive, modifier = Modifier.size(18.dp))
                            }
                        }
                        if (!isOwnProfile) {
                            Surface(
                                onClick = onToggleFollow,
                                shape = RoundedCornerShape(50),
                                color = if (isFollowing) SecondaryBackground else TurquoiseActive,
                                border = if (isFollowing) BorderStroke(1.dp, BorderColor) else null,
                                modifier = Modifier.testTag("public_profile_follow_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PersonAdd,
                                        contentDescription = null,
                                        tint = if (isFollowing) PrimaryText else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isFollowing) "Following" else "Follow",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFollowing) PrimaryText else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Identity Strip (Screenshot 1): Gender (L), User Level (next to Gender), Charm (right), Country Flag + Name (far right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_identity_badges_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (profileUser.showGender) {
                                GenderVectorBadge(gender = profileUser.gender)
                            }
                            UserLevelPill(level = profileUser.userLevel)
                            CharmLevelPill(
                                charmPoints = profileUser.charmPoints,
                                charmLevel = charmProgress.currentLevel,
                                onClick = onOpenCharmSystem,
                                modifier = Modifier.testTag("profile_charm_badge")
                            )
                        }
                        CountryFlagChip(
                            country = profileUser.country,
                            modifier = Modifier.testTag("profile_country_chip")
                        )
                    }

                    // ID + Copy Action Row + Active VIP Privacy Status Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ID:${profileUser.wefunId}",
                            fontSize = 13.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("profile_id_text")
                        )
                        IconButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(profileUser.wefunId))
                                onShowToast("Copied ID ${profileUser.wefunId}")
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("copy_profile_id_button")
                        ) {
                            Icon(Icons.Rounded.ContentCopy, "Copy User ID", tint = SecondaryText, modifier = Modifier.size(15.dp))
                        }
                        if (profileUser.friendRequestProtection && profileUser.vipLevel >= 4) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = TurquoiseActive.copy(alpha = 0.14f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Rounded.Shield, null, tint = TurquoiseDeep, modifier = Modifier.size(11.dp))
                                    Text("Protected", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TurquoiseDeep)
                                }
                            }
                        }
                        if (isOwnProfile && profileUser.privateProfileVisit && profileUser.vipLevel >= 6) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFA855F7).copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "Private Visit",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7E22CE),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // 3D Octagonal / Shield Achievements Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("profile_3d_badges_row")
                    ) {
                        badges.filter { it.isUnlocked }.forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedBadgeDetail = badge }
                            ) {
                                Profile3DOctagonBadge(badge = badge, size = 44.dp)
                            }
                        }
                    }
                }
            }

            // Moments <count> >
            item(key = "profile_moments") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                    ProfileMomentsStrip(
                        moments = accessibleMoments,
                        isOwnProfile = isOwnProfile,
                        onOpenAllMoments = onOpenMoments,
                        onSelectMoment = { selectedMomentDetail = it }
                    )
                }
            }

            // Gift Wall >
            item(key = "profile_gift_wall") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                    if (!isOwnProfile && profileUser.vipLevel >= 7 && profileUser.hideRecentGifts) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SecondaryBackground,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth().testTag("profile_gift_wall_hidden_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Rounded.Lock, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
                                Text("Gift Wall hidden by VIP 7+ privacy setting", fontSize = 13.sp, color = SecondaryText)
                            }
                        }
                    } else {
                        ProfileGiftWallCard(
                            giftWall = giftWallSummary,
                            onOpenGiftWall = onOpenGiftWallCollection
                        )
                    }
                }
            }

            // Signature
            item(key = "profile_signature") {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileSignatureSection(
                        signature = profileUser.bio,
                        isOwnProfile = isOwnProfile,
                        onClickEdit = { if (isOwnProfile) showSignatureEditor = true }
                    )
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.6f))
                }
            }

            // BFF <heart> <count> >
            item(key = "profile_bff") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                    ProfileBffSection(
                        relationships = profileUser.activeRelationships,
                        onSelectRelationship = { selectedRelationshipDetail = it },
                        onOpenBffOverview = {
                            profileUser.activeRelationships.firstOrNull()?.let { selectedRelationshipDetail = it }
                        }
                    )
                }
            }

            // Guard >
            item(key = "profile_guard") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    if (!isOwnProfile && profileUser.vipLevel >= 8 && profileUser.hideGuardianBoard) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SecondaryBackground,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth().testTag("profile_guard_hidden_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Rounded.Lock, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
                                Text("Guardian Board hidden by VIP 8+ privacy setting", fontSize = 13.sp, color = SecondaryText)
                            }
                        }
                    } else {
                        ProfileGuardStrip(
                            guards = guards,
                            onTapGuardUser = { g -> onOpenGuardProfile(g.guardUid, g.guardName) },
                            onOpenGuardList = onOpenGuardList
                        )
                    }
                }
            }

            // Advanced Room >
            item(key = "profile_advanced_room") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    ProfileAdvancedRoomStrip(
                        rooms = advancedRooms,
                        onEnterRoom = onEnterRoomById,
                        onOpenAllRooms = onOpenAdvancedRoomList
                    )
                }
            }

            // Stats >
            item(key = "profile_stats") {
                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                    ProfileSectionRowHeader(
                        title = "Stats",
                        onClick = onOpenStats,
                        testTag = "profile_stats_row"
                    )
                }
            }
        }

        // Fixed Bottom Bar (Send Gift + Chat / Edit Profile)
        ProfileBottomActionBar(
            isOwnProfile = isOwnProfile,
            onSendGift = { showGiftDrawer = true },
            onChatOrEdit = { if (isOwnProfile) onEditProfile() else onMessageUser() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Gift Celebration Animation Overlay (triggered only after confirmed send)
        GiftAnimationOverlay(
            effect = activeGiftAnimation,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (showGiftDrawer) {
        val targetSeat = RoomSeat(
            seatIndex = 0,
            occupantId = profileUser.uid,
            occupantName = profileUser.nickname,
            occupantInitials = profileUser.nickname.take(2),
            vipLevel = profileUser.vipLevel,
        )
        VirtualGiftDrawerSheet(
            userCoins = viewerCoins,
            occupiedSeats = listOf(targetSeat),
            packageInventory = packageInventory,
            onDismiss = { showGiftDrawer = false },
            onSendGift = { gift, combo, _, _, fromPkg ->
                onSendGiftToProfile(gift, combo, fromPkg)
            },
            onOpenWallet = onOpenWallet
        )
    }

    if (showSignatureEditor && isOwnProfile) {
        SignatureEditorDialog(
            initialText = profileUser.bio,
            onDismiss = { showSignatureEditor = false },
            onSave = { newSig ->
                onUpdateSignature(newSig)
                showSignatureEditor = false
            }
        )
    }

    selectedMomentDetail?.let { post ->
        AlertDialog(
            onDismissRequest = { selectedMomentDetail = null },
            title = { Text("${post.authorName} • ${post.hashtag}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(post.content, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)
                    if (post.imageUri != null) {
                        AsyncImage(
                            model = post.imageUri,
                            contentDescription = "Moment photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onToggleLikePost(post) }) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "Like Moment",
                                tint = if (post.isLiked) PinkAccent else SecondaryText
                            )
                        }
                        Text("${post.likesCount} likes • ${post.commentsCount} comments", fontSize = 12.sp, color = SecondaryText)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedMomentDetail = null
                    onOpenMoments()
                }) {
                    Text("Open Moments Feed", color = TurquoiseActive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMomentDetail = null }) { Text("Close") }
            }
        )
    }

    selectedRelationshipDetail?.let { rel ->
        RelationshipDetailDialog(
            relationship = rel,
            canCancel = isOwnProfile,
            onDismiss = { selectedRelationshipDetail = null },
            onCancelRelationship = onCancelRelationship
        )
    }

    selectedBadgeDetail?.let { badge ->
        AlertDialog(
            onDismissRequest = { selectedBadgeDetail = null },
            title = { Text("${badge.title} (${badge.tierLabel})", fontWeight = FontWeight.Bold) },
            text = { Text(badge.description) },
            confirmButton = {
                TextButton(onClick = { selectedBadgeDetail = null }) {
                    Text("OK", color = TurquoiseActive)
                }
            }
        )
    }
}
