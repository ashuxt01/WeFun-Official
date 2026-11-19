package com.example

import android.app.Application
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.RoomRole
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.repository.WefunRepository
import com.example.ui.rooms.VoiceRoomsLobbyScreen
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.WefunViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunVoiceRoomCreationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `viewmodel createRoomListing persists metadata and immediately enters room as owner`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = WefunViewModel(app)

        // Let initial seeding complete on main looper
        repeat(20) {
            shadowOf(Looper.getMainLooper()).idle()
            delay(25)
        }

        viewModel.createRoomListing(
            title = "Midnight WeFun Stage",
            description = "Live acoustic & relationship auction",
            category = RoomCategory.MUSIC,
            password = null,
            roomMode = RoomMode.AUCTION,
            backdropTheme = VoiceRoomBackdropTheme.AUCTION_ROYAL,
            autoEnter = true
        )

        var attempts = 0
        while (viewModel.liveRoomState.value.room == null && attempts < 80) {
            shadowOf(Looper.getMainLooper()).idle()
            delay(30)
            attempts++
        }

        val liveSession = viewModel.liveRoomState.value
        val activeRoom = liveSession.room
        assertNotNull("Created voice room must be immediately entered", activeRoom)
        assertEquals("Midnight WeFun Stage", activeRoom!!.title)
        assertEquals(RoomMode.AUCTION, activeRoom.roomMode)
        assertEquals(VoiceRoomBackdropTheme.AUCTION_ROYAL, activeRoom.backdropTheme)
        assertTrue(activeRoom.isNewRoom)
        assertTrue(activeRoom.isRecentlyActive)
        assertFalse(liveSession.isMinimized)
        assertEquals(RoomRole.OWNER, liveSession.currentUserRole)

        val ownerSeat = liveSession.seats.firstOrNull { it.seatIndex == 0 }
        assertNotNull(ownerSeat)
        assertTrue(ownerSeat!!.isHost)
        assertEquals(activeRoom.hostId, ownerSeat.occupantId)

        val repo = WefunRepository(app)
        val persisted = repo.roomsFlow.first().firstOrNull { it.id == activeRoom.id }
        assertNotNull("Created room must persist in database", persisted)
        assertEquals(RoomMode.AUCTION, persisted!!.roomMode)
        assertEquals(VoiceRoomBackdropTheme.AUCTION_ROYAL, persisted.backdropTheme)
        assertTrue(persisted.isNewRoom)
        assertTrue(persisted.tags.contains("Music"))
    }

    @Test
    fun `create room dialog captures mode and backdrop and lobby surfaces own new rooms`() {
        val customUserId = "usr_custom_creator"
        val userCreatedRoom = VoiceRoom(
            id = "9001234",
            title = "Aria Custom Stage",
            description = "Freshly launched room",
            category = RoomCategory.MUSIC,
            hostId = customUserId,
            hostName = "Aria Custom",
            popularity = 5000,
            onlineCount = 1,
            isNewRoom = true,
            isRecentlyActive = true,
            roomMode = RoomMode.NORMAL,
            backdropTheme = VoiceRoomBackdropTheme.AURORA_VIOLET
        )
        val officialRoom = VoiceRoom(
            id = "rm_official",
            title = "WeFun Gala",
            description = "Official Lounge",
            category = RoomCategory.OFFICIAL,
            hostId = "host_official",
            hostName = "WeFun Official",
            popularity = 95_000,
            onlineCount = 210,
            isOfficial = true
        )

        var capturedTitle = ""
        var capturedMode: RoomMode? = null
        var capturedBackdrop: VoiceRoomBackdropTheme? = null

        composeRule.setContent {
            WEFUNTheme {
                VoiceRoomsLobbyScreen(
                    rooms = listOf(officialRoom, userCreatedRoom),
                    currentUserId = customUserId,
                    onCreateRoomListing = { _, _, _, _ -> },
                    onCreateRoomConfigured = { title, _, _, _, mode, backdrop ->
                        capturedTitle = title
                        capturedMode = mode
                        capturedBackdrop = backdrop
                    }
                )
            }
        }
        composeRule.waitForIdle()

        // Own newly created room must surface under the prioritized "new" section ("My & New Rooms")
        composeRule.onNodeWithTag("voice_rooms_lobby_list")
            .performScrollToNode(hasTestTag("voice_section_new"))
        composeRule.onNodeWithTag("voice_section_new").assertIsDisplayed()
        composeRule.onNodeWithTag("voice_rooms_lobby_list")
            .performScrollToNode(hasTestTag("room_card_9001234"))
        composeRule.onNodeWithTag("room_card_9001234").assertIsDisplayed()

        // Switching to "Related" scope filter should display user's own room using dynamic currentUserId
        composeRule.onNodeWithText("Related").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("room_card_9001234").assertIsDisplayed()

        // Open Create Room dialog and configure Auction Mode + Backdrop
        composeRule.onNodeWithTag("fab_create_room").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_room_title_input").performTextInput("Gold Auction Party")
        composeRule.onNodeWithTag("create_room_mode_auction").performScrollTo().performClick()
        composeRule.onNodeWithTag("create_room_backdrop_pearl_light").performScrollTo().performClick()
        composeRule.onNodeWithTag("confirm_create_room_button").performClick()
        composeRule.waitForIdle()

        assertEquals("Gold Auction Party", capturedTitle)
        assertEquals(RoomMode.AUCTION, capturedMode)
        assertEquals(VoiceRoomBackdropTheme.PEARL_LIGHT, capturedBackdrop)
    }
}
