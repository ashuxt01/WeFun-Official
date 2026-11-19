package com.example.ui.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.ui.profile.ProfileAndEcosystemScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun ProfileScreen(
    user: UserProfile = UserProfile(
        uid = "usr_nova_01",
        wefunId = "WF-804921",
        username = "ariavance",
        nickname = "Aria Vance",
        bio = "Music host, late-night storyteller & Ludo strategist."
    ),
    settings: WefunAppSettings = WefunAppSettings(),
    posts: List<SocialPost> = emptyList(),
    followedUserIds: Set<String> = emptySet(),
    onToggleParentalControl: (Boolean) -> Unit = {},
    onSelectLanguage: (String) -> Unit = {},
    onToggleFollow: (String) -> Unit = {},
    onOpenOwnFullProfile: () -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    onOpenAvatarEditor: () -> Unit = {},
    onOpenMoments: () -> Unit = {},
    onOpenVipCenter: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenPlayShow: () -> Unit = {},
    onOpenMyHome: () -> Unit = {},
    onOpenHelpCenter: () -> Unit = {},
    onOpenSecurityCenter: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        ProfileAndEcosystemScreen(
            user = user,
            settings = settings,
            posts = posts,
            followedUserIds = followedUserIds,
            onToggleParentalControl = onToggleParentalControl,
            onSelectLanguage = onSelectLanguage,
            onToggleFollow = onToggleFollow,
            onOpenOwnFullProfile = onOpenOwnFullProfile,
            onOpenUserProfile = onOpenUserProfile,
            onOpenAvatarEditor = onOpenAvatarEditor,
            onOpenMoments = onOpenMoments,
            onOpenVipCenter = onOpenVipCenter,
            onOpenShop = onOpenShop,
            onOpenPlayShow = onOpenPlayShow,
            onOpenMyHome = onOpenMyHome,
            onOpenHelpCenter = onOpenHelpCenter,
            onOpenSecurityCenter = onOpenSecurityCenter,
            onOpenAppSettings = onOpenAppSettings,
            onShowToast = onShowToast
        )
    }
}
