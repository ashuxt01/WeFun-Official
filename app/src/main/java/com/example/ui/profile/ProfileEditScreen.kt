package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditScreen(
    user: UserProfile,
    isOwner: Boolean,
    onBack: () -> Unit,
    onSave: (
        targetUid: String,
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        coverPhotoUri: String?,
        onDone: (Result<UserProfile>) -> Unit,
    ) -> Unit,
    onSaveVipPrivacy: (
        customTitle: String,
        hideRecentGifts: Boolean,
        hideGuardianBoard: Boolean,
        privateProfileVisit: Boolean,
        friendRequestProtection: Boolean,
        customNamecardTheme: String,
    ) -> Unit = { _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var nickname by remember(user.uid) { mutableStateOf(user.nickname) }
    var bio by remember(user.uid) { mutableStateOf(user.bio) }
    var country by remember(user.uid) { mutableStateOf(user.country) }
    var language by remember(user.uid) { mutableStateOf(user.language) }
    var showGender by remember(user.uid) { mutableStateOf(user.showGender) }
    var showAge by remember(user.uid) { mutableStateOf(user.showAge) }
    var photoUri by remember(user.uid) { mutableStateOf(user.photoUri) }
    var coverUri by remember(user.uid) { mutableStateOf(user.coverPhotoUri) }
    var customTitle by remember(user.uid) { mutableStateOf(user.customTitle) }
    var hideRecentGifts by remember(user.uid) { mutableStateOf(user.hideRecentGifts) }
    var hideGuardianBoard by remember(user.uid) { mutableStateOf(user.hideGuardianBoard) }
    var privateProfileVisit by remember(user.uid) { mutableStateOf(user.privateProfileVisit) }
    var friendRequestProtection by remember(user.uid) { mutableStateOf(user.friendRequestProtection) }
    var customNamecardTheme by remember(user.uid) { mutableStateOf(user.customNamecardTheme) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null && isOwner) photoUri = uri.toString() }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null && isOwner) coverUri = uri.toString() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .navigationBarsPadding()
            .testTag("profile_edit_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).testTag("profile_edit_back_button")
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PrimaryText)
            }
            Text(
                text = "Edit Profile & Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText
            )
        }
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!isOwner) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PinkAccent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, PinkAccent.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth().testTag("profile_edit_unauthorized_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.Lock, null, tint = PinkAccent)
                            Text("Read-only: Only the profile owner can modify these settings.", color = PrimaryText, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                // Cover & Avatar Media Pickers with Live Preview
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Cover & Profile Photo", fontWeight = FontWeight.Bold, color = PrimaryText)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SecondaryBackground)
                        ) {
                            if (coverUri != null) {
                                AsyncImage(
                                    model = coverUri,
                                    contentDescription = "Cover Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                    contentDescription = "Default Cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (isOwner) {
                                OutlinedButton(
                                    onClick = { coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .testTag("pick_cover_photo_btn"),
                                    border = BorderStroke(1.dp, Color.White)
                                ) {
                                    Icon(Icons.Rounded.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Change Cover", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, TurquoiseActive, CircleShape)
                            ) {
                                if (photoUri != null) {
                                    AsyncImage(
                                        model = photoUri,
                                        contentDescription = "Avatar Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                        contentDescription = "Default Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            if (isOwner) {
                                OutlinedButton(
                                    onClick = { avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    modifier = Modifier.testTag("pick_avatar_photo_btn")
                                ) {
                                    Text("Update Profile Photo", color = TurquoiseActive, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { if (isOwner) nickname = it },
                    enabled = isOwner && !isSaving,
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )
            }

            item {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (isOwner && it.length <= 120) bio = it },
                    enabled = isOwner && !isSaving,
                    label = { Text("Signature (${bio.length}/120)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio_input")
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { if (isOwner) country = it },
                        enabled = isOwner && !isSaving,
                        label = { Text("Country") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("edit_profile_country_input")
                    )
                    OutlinedTextField(
                        value = language,
                        onValueChange = { if (isOwner) language = it },
                        enabled = isOwner && !isSaving,
                        label = { Text("Language") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Privacy Preferences", fontWeight = FontWeight.Bold, color = PrimaryText)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("Show Gender Badge", color = PrimaryText)
                            Switch(checked = showGender, onCheckedChange = { if (isOwner) showGender = it }, enabled = isOwner)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("Show Age Status", color = PrimaryText)
                            Switch(checked = showAge, onCheckedChange = { if (isOwner) showAge = it }, enabled = isOwner)
                        }
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("VIP Privileges & Privacy", fontWeight = FontWeight.Bold, color = PrimaryText)
                            if (user.vipLevel > 0) VipLevelBadge3D(vipLevel = user.vipLevel, compact = true)
                        }
                        VipPrivacySwitchRow(
                            title = "Friend Request Protection",
                            requiredLevel = 4,
                            userVipLevel = user.vipLevel,
                            checked = friendRequestProtection,
                            isOwner = isOwner,
                            testTag = "vip_privacy_friend_protection",
                            onCheckedChange = { friendRequestProtection = it }
                        )
                        VipPrivacySwitchRow(
                            title = "Private Profile Visit Mode",
                            requiredLevel = 6,
                            userVipLevel = user.vipLevel,
                            checked = privateProfileVisit,
                            isOwner = isOwner,
                            testTag = "vip_privacy_private_visit",
                            onCheckedChange = { privateProfileVisit = it }
                        )
                        VipPrivacySwitchRow(
                            title = "Hide Recent Gifts Wall",
                            requiredLevel = 7,
                            userVipLevel = user.vipLevel,
                            checked = hideRecentGifts,
                            isOwner = isOwner,
                            testTag = "vip_privacy_hide_gifts",
                            onCheckedChange = { hideRecentGifts = it }
                        )
                        VipPrivacySwitchRow(
                            title = "Hide Guardian Board",
                            requiredLevel = 8,
                            userVipLevel = user.vipLevel,
                            checked = hideGuardianBoard,
                            isOwner = isOwner,
                            testTag = "vip_privacy_hide_guard",
                            onCheckedChange = { hideGuardianBoard = it }
                        )
                        OutlinedTextField(
                            value = customTitle,
                            onValueChange = { if (isOwner && user.vipLevel >= 8 && it.length <= 24) customTitle = it },
                            enabled = isOwner && user.vipLevel >= 8 && !isSaving,
                            label = { Text("Customized Title (VIP 8+)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_vip_custom_title")
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (user.vipLevel >= 12) "Namecard Background Theme (VIP 12)" else "Namecard Background Theme (Requires VIP 12)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (user.vipLevel >= 12) TurquoiseDeep else SecondaryText
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Default", "Obsidian Gold", "Crimson Sovereign", "Aurora Silk").forEach { themeName ->
                                    FilterChip(
                                        selected = customNamecardTheme == themeName,
                                        enabled = isOwner && user.vipLevel >= 12 && !isSaving,
                                        onClick = { customNamecardTheme = themeName },
                                        label = { Text(themeName, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (errorMsg != null) {
                item {
                    Text(
                        text = errorMsg!!,
                        color = PinkAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("profile_edit_error_text")
                    )
                }
            }

            if (isOwner) {
                item {
                    if (isSaving) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = TurquoiseActive, modifier = Modifier.size(28.dp))
                        }
                    } else {
                        WefunGradientButton(
                            text = "Save Profile Changes",
                            testTag = "save_profile_edits_button",
                            onClick = {
                                errorMsg = null
                                isSaving = true
                                onSave(
                                    user.uid,
                                    nickname,
                                    bio,
                                    country,
                                    language,
                                    showGender,
                                    showAge,
                                    photoUri,
                                    coverUri,
                                ) { res ->
                                    isSaving = false
                                    res.onSuccess {
                                        onSaveVipPrivacy(
                                            customTitle.trim(),
                                            hideRecentGifts,
                                            hideGuardianBoard,
                                            privateProfileVisit,
                                            friendRequestProtection,
                                            customNamecardTheme,
                                        )
                                        onBack()
                                    }
                                        .onFailure { errorMsg = it.message }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VipPrivacySwitchRow(
    title: String,
    requiredLevel: Int,
    userVipLevel: Int,
    checked: Boolean,
    isOwner: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    val unlocked = userVipLevel >= requiredLevel
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, color = if (unlocked) PrimaryText else SecondaryText, fontWeight = FontWeight.Medium)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (unlocked) GoldAmber.copy(alpha = 0.16f) else SecondaryBackground
                ) {
                    Text(
                        text = "VIP $requiredLevel+",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (unlocked) Color(0xFFB45309) else SecondaryText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (!unlocked) {
                Text("Requires VIP $requiredLevel to toggle", fontSize = 11.sp, color = SecondaryText)
            }
        }
        Switch(
            checked = checked && unlocked,
            onCheckedChange = { if (isOwner && unlocked) onCheckedChange(it) },
            enabled = isOwner && unlocked,
            modifier = Modifier.testTag(testTag)
        )
    }
}
