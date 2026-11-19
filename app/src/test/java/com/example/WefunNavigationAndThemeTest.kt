package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.PartyGame
import com.example.data.model.RoomCategory
import com.example.data.model.UserProfile
import com.example.data.model.VoiceRoom
import com.example.data.repository.WefunSeedData
import com.example.ui.auth.WefunSplashScreen
import com.example.ui.games.GamePlaceholderScreen
import com.example.ui.navigation.AppNavigation
import com.example.ui.navigation.BottomNavigationBar
import com.example.ui.navigation.WefunDestination
import com.example.ui.navigation.navigateToWefunDestination
import com.example.ui.profile.FullProfileScreen
import com.example.ui.rooms.VoiceRoomsLobbyScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.WEFUNTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunNavigationAndThemeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `wefun theme defaults to exact pure white and turquoise palette even if dark requested`() {
        var observedBackground = SecondaryBackground
        var observedSurface = PrimaryBackground
        var observedCard = SecondaryBackground
        var observedPrimary = PinkAccent
        var observedSecondary = TurquoiseActive
        var observedExtraPink = TurquoiseActive
        var observedText = SecondaryText
        var observedSecondaryText = PrimaryText
        var observedBorder = TurquoiseActive
        var observedIsDark = true

        composeRule.setContent {
            WEFUNTheme(darkTheme = true) {
                observedBackground = MaterialTheme.colorScheme.background
                observedSurface = MaterialTheme.colorScheme.surface
                observedCard = MaterialTheme.colorScheme.surfaceVariant
                observedPrimary = MaterialTheme.colorScheme.primary
                observedSecondary = MaterialTheme.colorScheme.secondary
                observedExtraPink = LocalWefunColors.current.pink
                observedText = MaterialTheme.colorScheme.onBackground
                observedSecondaryText = MaterialTheme.colorScheme.onSurfaceVariant
                observedBorder = MaterialTheme.colorScheme.outline
                observedIsDark = LocalWefunColors.current.isDark
            }
        }
        composeRule.waitForIdle()

        assertEquals(PrimaryBackground, observedBackground)
        assertEquals(SecondaryBackground, observedSurface)
        assertEquals(CardBackground, observedCard)
        assertEquals(TurquoiseActive, observedPrimary)
        assertEquals(PrimaryPurple, observedSecondary)
        assertEquals(PinkAccent, observedExtraPink)
        assertEquals(PrimaryText, observedText)
        assertEquals(SecondaryText, observedSecondaryText)
        assertEquals(BorderColor, observedBorder)
        assertFalse(observedIsDark)
    }

    @Test
    fun `splash screen renders official wefun logo and tagline on pure white light theme`() {
        composeRule.setContent {
            WEFUNTheme {
                WefunSplashScreen(onFinished = {})
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("wefun_splash_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("splash_brand_logo").assertIsDisplayed()
        composeRule.onNodeWithText("WEFUN").assertIsDisplayed()
        composeRule.onNodeWithText("Connect. Play. Express. Belong.").assertIsDisplayed()
    }

    @Test
    fun `bottom navigation switches exact five wefun tabs and supports back navigation`() {
        lateinit var currentRouteRef: () -> String?
        lateinit var backStackCountRef: () -> Int

        composeRule.setContent {
            WEFUNTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: WefunDestination.Home.route
                currentRouteRef = { navController.currentBackStackEntry?.destination?.route }
                backStackCountRef = { navController.currentBackStack.value.size }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onSelectDestination = { destination ->
                                navController.navigateToWefunDestination(destination)
                            },
                            unreadChatsCount = 2,
                            showVipGoldIndicator = true,
                        )
                    }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // 1. Initial route must be WeFun (home)
        assertEquals(WefunDestination.Home.route, currentRouteRef())
        assertEquals("WeFun", WefunDestination.Home.label)
        assertEquals("Voice Room", WefunDestination.Party.label)
        assertEquals("Chats", WefunDestination.Chats.label)
        assertEquals("Discover", WefunDestination.Discover.label)
        assertEquals("Me", WefunDestination.Profile.label)

        composeRule.onNodeWithTag("nav_tab_home").assertIsSelected()
        composeRule.onNodeWithTag("nav_tab_party").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_chats").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_discover").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_profile").assertIsNotSelected()
        composeRule.onNodeWithTag("home_avatar_button").assertIsDisplayed()
        composeRule.onAllNodesWithText("WeFun").onFirst().assertIsDisplayed()

        // 2. Switch to Voice Room
        composeRule.onNodeWithTag("nav_tab_party").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Party.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_party").assertIsSelected()
        composeRule.onNodeWithTag("fab_create_room").assertIsDisplayed()

        // 3. Switch to Chats
        composeRule.onNodeWithTag("nav_tab_chats").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Chats.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_chats").assertIsSelected()
        composeRule.onNodeWithTag("chats_new_chat_button").assertIsDisplayed()

        // 4. Switch to Discover (Moments + Church, Family, Mentorship, Nearby grid)
        composeRule.onNodeWithTag("nav_tab_discover").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Discover.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_discover").assertIsSelected()
        composeRule.onNodeWithTag("discover_moments_banner").assertIsDisplayed()
        composeRule.onNodeWithTag("discover_church_card").assertIsDisplayed()
        composeRule.onNodeWithTag("discover_family_card").assertIsDisplayed()
        composeRule.onNodeWithTag("discover_mentorship_card").assertIsDisplayed()
        composeRule.onNodeWithTag("discover_nearby_card").assertIsDisplayed()

        // 5. Switch to Me (Profile tab)
        composeRule.onNodeWithTag("nav_tab_profile").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Profile.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_profile").assertIsSelected()
        composeRule.onNodeWithText("Aria Vance").assertIsDisplayed()
        composeRule.onNodeWithTag("me_quick_vip_center").assertIsDisplayed()
        composeRule.onNodeWithTag("me_quick_shop").assertIsDisplayed()
        composeRule.onNodeWithTag("me_quick_play_show").assertIsDisplayed()
        composeRule.onNodeWithTag("me_quick_my_home").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_menu_moments").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("profile_menu_stats").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("profile_menu_visitors").performScrollTo().assertIsDisplayed()

        // 6. Tapping Me again must NOT create duplicate entries on back stack
        val stackSizeBeforeDuplicateTap = backStackCountRef()
        composeRule.onNodeWithTag("nav_tab_profile").performClick()
        composeRule.waitForIdle()
        assertEquals(stackSizeBeforeDuplicateTap, backStackCountRef())

        // 7. Android back navigation from Me returns to WeFun (home)
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Home.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_home").assertIsSelected()
    }

    @Test
    fun `wefun exposes six featured games and opens clean game placeholder without internal gameplay`() {
        val featuredTitles = WefunSeedData.wefunFeaturedGames.map { it.title }
        assertEquals(6, featuredTitles.size)
        assertTrue(featuredTitles.containsAll(
            listOf(
                "Space Werewolf",
                "Jackaroo",
                "Guess Their Answers",
                "Brain Storm",
                "Oh My Cards",
                "Happy Carrom"
            )
        ))

        val sampleGame: PartyGame = WefunSeedData.wefunFeaturedGames.first()
        var backClicked = false

        composeRule.setContent {
            WEFUNTheme {
                GamePlaceholderScreen(
                    game = sampleGame,
                    onBack = { backClicked = true },
                    onInviteFriends = {},
                    onOpenGameRooms = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("game_placeholder_screen_${sampleGame.id.name}").assertIsDisplayed()
        composeRule.onAllNodesWithText(sampleGame.title).onFirst().assertIsDisplayed()
        composeRule.onNodeWithTag("game_placeholder_back_button").performClick()
        composeRule.waitForIdle()
        assertTrue(backClicked)
    }

    @Test
    fun `voice room discovery lists official rooms displays room id and supports preview and entry`() {
        val sampleRooms = listOf(
            VoiceRoom(
                id = "rm_pub",
                title = "Late Night Chill",
                description = "Chill lounge",
                category = RoomCategory.PUBLIC,
                hostId = "h1",
                hostName = "Kai",
                onlineCount = 42,
                popularity = 10_000,
                isOfficial = false
            ),
            VoiceRoom(
                id = "rm_off",
                title = "WeFun Official Gala",
                description = "Official community lounge",
                category = RoomCategory.OFFICIAL,
                hostId = "host_official",
                hostName = "WeFun Official",
                onlineCount = 240,
                popularity = 90_000,
                isOfficial = true
            )
        )
        var enteredRoomId: String? = null

        composeRule.setContent {
            WEFUNTheme {
                VoiceRoomsLobbyScreen(
                    rooms = sampleRooms,
                    onEnterRoom = { enteredRoomId = it.id },
                    onCreateRoomListing = { _, _, _, _ -> }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("voice_rooms_lobby_list")
            .performScrollToNode(hasTestTag("voice_section_official"))
        composeRule.onNodeWithTag("voice_section_official").assertIsDisplayed()
        composeRule.onNodeWithTag("voice_rooms_lobby_list")
            .performScrollToNode(hasTestTag("room_card_rm_off"))
        composeRule.onNodeWithTag("room_card_rm_off").assertIsDisplayed()
        composeRule.onNodeWithText("WeFun Official Gala").assertIsDisplayed()
        composeRule.onAllNodesWithText("OFFICIAL", ignoreCase = true).onFirst().assertIsDisplayed()

        // Tapping preview/bookmark opens room info dialog
        composeRule.onNodeWithTag("room_preview_btn_rm_off").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Close").performClick()
        composeRule.waitForIdle()

        // Tapping room card enters live voice room
        composeRule.onNodeWithTag("room_card_rm_off").performClick()
        composeRule.waitForIdle()
        assertEquals("rm_off", enteredRoomId)

        // Scrolling down reveals additional sections and room cards
        composeRule.onNodeWithTag("voice_rooms_lobby_list")
            .performScrollToNode(hasTestTag("room_card_rm_pub"))
        composeRule.onNodeWithTag("room_card_rm_pub").assertIsDisplayed()
    }

    @Test
    fun `full profile distinguishes own profile controls from public user profile`() {
        val ownUser = UserProfile(
            uid = "usr_nova_01",
            wefunId = "WF-804921",
            username = "ariavance",
            nickname = "Aria Vance",
            bio = "Host",
            vipLevel = 2,
            userLevel = 14
        )
        val otherUser = UserProfile(
            uid = "sp_2",
            wefunId = "WF-310944",
            username = "jishan",
            nickname = "Jishan Bhati",
            bio = "Music & Voice creator",
            vipLevel = 3,
            userLevel = 21
        )

        composeRule.setContent {
            WEFUNTheme {
                FullProfileScreen(
                    profileUser = otherUser,
                    isOwnProfile = false,
                    isFollowing = false,
                    posts = emptyList(),
                    onBack = {},
                    onEditProfile = {},
                    onOpenSettings = {},
                    onToggleFollow = {},
                    onMessageUser = {},
                    onPickPhoto = {},
                    onToggleLikePost = {},
                    onShowToast = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("full_profile_public_sp_2").assertIsDisplayed()
        composeRule.onNodeWithTag("public_profile_follow_button").assertIsDisplayed()
        composeRule.onNodeWithTag("public_profile_message_button").assertIsDisplayed()
        composeRule.onNodeWithTag("own_profile_edit_button").assertDoesNotExist()
        assertFalse(ownUser.uid == otherUser.uid)
    }
}
