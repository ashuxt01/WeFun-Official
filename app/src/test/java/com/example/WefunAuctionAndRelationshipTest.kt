package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.WefunAppSettings
import com.example.data.local.WefunDatabase
import com.example.data.model.AuctionBidEntry
import com.example.data.model.AuctionSessionState
import com.example.data.model.BiddingMode
import com.example.data.model.RelationshipRequest
import com.example.data.model.RelationshipRequestStatus
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.RoomRole
import com.example.data.model.RoomSeat
import com.example.data.model.StartingToken
import com.example.data.model.UserProfile
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.repository.VoiceRoomBackendEngine
import com.example.data.repository.WefunSeedData
import com.example.ui.profile.ProfileAndEcosystemScreen
import com.example.ui.rooms.AuctionRoomScreen
import com.example.ui.rooms.RelationshipSettingsSheet
import com.example.ui.rooms.RoomSettingsScreen
import com.example.ui.rooms.VirtualGiftDrawerSheet
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.LiveRoomSessionState
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunAuctionAndRelationshipTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var db: WefunDatabase
    private lateinit var backend: VoiceRoomBackendEngine

    @Before
    fun setupDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WefunDatabase::class.java
        ).allowMainThreadQueries().build()
        backend = VoiceRoomBackendEngine(db.voiceRoomDao(), db.voiceRoomSystemDao())
        runBlocking {
            db.voiceRoomDao().upsertRooms(WefunSeedData.initialRooms)
        }
    }

    @After
    fun tearDownDb() {
        db.close()
    }

    @Test
    fun `backend validates room id lookup and enforces owner or admin permissions on full settings`() = runBlocking {
        val found = backend.getRoomByIdValidated("320252")
        assertTrue(found.isSuccess)
        assertEquals("AUCTION", found.getOrNull()?.roomModeName)

        val missing = backend.getRoomByIdValidated("999999")
        assertTrue(missing.isFailure)

        val denied = backend.updateFullRoomSettings(
            roomId = "320252",
            actorName = "Guest",
            actorRole = RoomRole.NORMAL_MEMBER,
            title = "Hacked",
            announcement = "None",
            password = null,
            roomMode = RoomMode.NORMAL,
            tags = listOf("Chill"),
            backdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE,
            highQualityAudio = true,
            banTextChatting = false,
            banPhotoChatting = false,
            banRedPacket = false,
            membershipFeeCoins = 0L,
            musicAdminsOnly = false
        )
        assertTrue(denied.isFailure)

        val allowed = backend.updateFullRoomSettings(
            roomId = "320252",
            actorName = "DJ Rhea",
            actorRole = RoomRole.OWNER,
            title = "Royal Auction Night",
            announcement = "Updated Auction Stage",
            password = null,
            roomMode = RoomMode.AUCTION,
            tags = listOf("Auction", "BFF"),
            backdropTheme = VoiceRoomBackdropTheme.AUCTION_ROYAL,
            highQualityAudio = true,
            banTextChatting = true,
            banPhotoChatting = false,
            banRedPacket = false,
            membershipFeeCoins = 50L,
            musicAdminsOnly = true
        )
        assertTrue(allowed.isSuccess)
        assertEquals("Royal Auction Night", allowed.getOrNull()?.title)
        assertEquals(true, allowed.getOrNull()?.banTextChatting)
        assertEquals(50L, allowed.getOrNull()?.membershipFeeCoins)
    }

    @Test
    fun `custom relationship tag validator blocks empty duplicates and inappropriate labels`() {
        val existing = WefunSeedData.ordinaryRelationshipTags.map { it.label } +
            WefunSeedData.bffRelationshipTags.map { it.label }
        assertTrue(backend.validateCustomTag("", existing).isFailure)
        assertTrue(backend.validateCustomTag("ThisTagIsFarTooLongBeyondTwenty", existing).isFailure)
        assertTrue(backend.validateCustomTag("Baddie", existing).isFailure)
        assertTrue(backend.validateCustomTag("Wedding", existing).isFailure)
        assertTrue(backend.validateCustomTag("Bestie", existing).isFailure)
        assertTrue(backend.validateCustomTag("Twin Flame", existing).isSuccess)
    }

    @Test
    fun `auction room screen shows purple gold stage countdown bid buttons and pending consent`() {
        var acceptedReqId: String? = null
        var bidPlaced: Long = 0L
        val session = LiveRoomSessionState(
            room = VoiceRoom(
                id = "320252",
                title = "Royal Relationship Auction",
                description = "Auction Stage",
                category = RoomCategory.PUBLIC,
                hostId = "host_rhea",
                hostName = "DJ Rhea",
                roomMode = RoomMode.AUCTION,
                backdropTheme = VoiceRoomBackdropTheme.AUCTION_ROYAL,
            ),
            seats = List(9) { idx ->
                if (idx == 0) RoomSeat(0, "host_rhea", "DJ Rhea", "DR", vipLevel = 5)
                else RoomSeat(idx)
            },
            auctionState = AuctionSessionState(
                isConfigured = true,
                biddingMode = BiddingMode.BFF,
                selectedTag = WefunSeedData.bffRelationshipTags.first(),
                selectedToken = StartingToken.SCOOTER,
                startingPricePoints = 200L,
                countdownSeconds = 95,
                isBiddingActive = true,
                history = listOf(
                    AuctionBidEntry("b1", "host_kai", "Kai Storm", "Bro and sis", "Scooter Token", 300L, "host_rhea", "DJ Rhea")
                )
            )
        )
        val pendingReq = RelationshipRequest(
            id = "req_100",
            roomId = "320252",
            senderId = "host_kai",
            senderName = "Kai Storm",
            targetUserId = "usr_nova_01",
            targetUserName = "Aria Vance",
            tagLabel = "Besties",
            mode = BiddingMode.BFF,
            tokenTitle = "Scooter Token",
            bidPoints = 200L,
            status = RelationshipRequestStatus.PENDING
        )

        composeRule.setContent {
            WEFUNTheme {
                AuctionRoomScreen(
                    session = session,
                    currentUserId = "usr_nova_01",
                    pendingRequests = listOf(pendingReq),
                    onSeatClick = {},
                    onOpenRelationshipSettings = {},
                    onPlaceBid = { bidPlaced = it },
                    onAcceptRequest = { acceptedReqId = it },
                    onDeclineRequest = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("auction_room_stage").assertIsDisplayed()
        composeRule.onNodeWithTag("auction_countdown_timer").assertIsDisplayed()
        composeRule.onNodeWithTag("auction_bid_history_row").assertIsDisplayed()
        composeRule.onNodeWithTag("auction_bid_100_button").performClick()
        assertEquals(100L, bidPlaced)

        composeRule.onNodeWithTag("accept_relationship_btn_req_100").performClick()
        assertEquals("req_100", acceptedReqId)
    }

    @Test
    fun `relationship settings sheet switches modes renders chips and confirms`() {
        var confirmedMode: BiddingMode? = null
        composeRule.setContent {
            WEFUNTheme {
                RelationshipSettingsSheet(
                    auctionState = AuctionSessionState(
                        biddingMode = BiddingMode.ORDINARY_BIDDING,
                        selectedTag = WefunSeedData.ordinaryRelationshipTags.first()
                    ),
                    customTags = emptyList(),
                    occupiedSeats = listOf(RoomSeat(0, "host_rhea", "DJ Rhea", "DR")),
                    onCreateCustomTag = { lbl, m -> Result.success(RelationshipTagOption("c1", lbl, m, "clover", true)) },
                    onConfirm = { mode, _, _, _, _, _, _ -> confirmedMode = mode },
                    onDismiss = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("relationship_settings_sheet").assertIsDisplayed()
        composeRule.onNodeWithTag("custom_tag_chip").assertIsDisplayed()
        composeRule.onNodeWithTag("bidding_mode_bff").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Bro and sis").assertIsDisplayed()
        composeRule.onNodeWithText("Besties").assertIsDisplayed()
        composeRule.onNodeWithTag("confirm_relationship_settings_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(BiddingMode.BFF, confirmedMode)
    }

    @Test
    fun `room settings screen allows mode toggle and displays room id`() {
        var switchedMode: RoomMode? = null
        val room = VoiceRoom(
            id = "320252",
            title = "DOMINATORS",
            description = "Room",
            category = RoomCategory.PUBLIC,
            hostId = "usr_nova_01",
            hostName = "Aria Vance",
            roomMode = RoomMode.NORMAL,
            tags = listOf("Friends"),
            isPartnerSeatEnabled = true
        )

        composeRule.setContent {
            WEFUNTheme {
                RoomSettingsScreen(
                    room = room,
                    currentUserRole = RoomRole.OWNER,
                    memberRoles = emptyList(),
                    activityLogs = emptyList(),
                    onBack = {},
                    onSwitchRoomMode = { switchedMode = it },
                    onOpenAuctionRelationshipSettings = {},
                    onTogglePartnerSeat = {},
                    onAssignMemberRole = { _, _, _ -> },
                    onSaveFullSettings = { _, _, _, _, _, _, _, _, _, _, _, _ -> },
                    onShowToast = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("room_settings_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("room_mode_setting_row").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("select_room_mode_auction").performClick()
        assertEquals(RoomMode.AUCTION, switchedMode)
    }

    @Test
    fun `virtual gift drawer displays package inventory and gift categories`() {
        var sentFromPkg = false
        composeRule.setContent {
            WEFUNTheme {
                VirtualGiftDrawerSheet(
                    userCoins = 2800L,
                    occupiedSeats = listOf(RoomSeat(0, "host_rhea", "DJ Rhea", "DR")),
                    packageInventory = WefunSeedData.initialPackageInventory,
                    onDismiss = {},
                    onSendGift = { _, _, _, _, fromPkg -> sentFromPkg = fromPkg },
                    onOpenWallet = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("gift_tab_package").assertIsDisplayed()
        composeRule.onNodeWithTag("gift_tab_gift").assertIsDisplayed()
        composeRule.onNodeWithText("x16").assertIsDisplayed()
        composeRule.onNodeWithTag("gift_preview_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("gift_preview_banner").performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithTag("send_gift_confirm_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(sentFromPkg)
    }

    @Test
    fun `me tab shows help center security center and settings rows`() {
        val sampleUser = UserProfile(
            uid = "usr_nova_01",
            wefunId = "WF-804921",
            username = "ariavance",
            nickname = "Aria Vance",
            bio = "Host"
        )
        var helpClicked = false
        var securityClicked = false
        var settingsClicked = false

        composeRule.setContent {
            WEFUNTheme {
                ProfileAndEcosystemScreen(
                    user = sampleUser,
                    settings = WefunAppSettings(),
                    posts = emptyList(),
                    followedUserIds = emptySet(),
                    onToggleParentalControl = {},
                    onSelectLanguage = {},
                    onToggleFollow = {},
                    onOpenOwnFullProfile = {},
                    onOpenUserProfile = { _, _ -> },
                    onOpenAvatarEditor = {},
                    onOpenMoments = {},
                    onOpenVipCenter = {},
                    onOpenShop = {},
                    onOpenPlayShow = {},
                    onOpenMyHome = {},
                    onOpenHelpCenter = { helpClicked = true },
                    onOpenSecurityCenter = { securityClicked = true },
                    onOpenAppSettings = { settingsClicked = true },
                    onShowToast = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("profile_menu_help_center").performScrollTo().performClick()
        composeRule.onNodeWithTag("profile_menu_security_center").performScrollTo().performClick()
        composeRule.onNodeWithTag("profile_menu_settings").performScrollTo().performClick()

        assertTrue(helpClicked)
        assertTrue(securityClicked)
        assertTrue(settingsClicked)
        composeRule.onAllNodesWithText("Help Center").onFirst().assertIsDisplayed()
    }
}
