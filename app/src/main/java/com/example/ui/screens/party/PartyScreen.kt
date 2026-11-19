package com.example.ui.screens.party

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.ui.rooms.VoiceRoomsLobbyScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun PartyScreen(
    rooms: List<VoiceRoom> = emptyList(),
    currentUserId: String = "usr_nova_01",
    onEnterRoom: (VoiceRoom) -> Unit = {},
    onEnterRoomById: (String) -> Unit = {},
    onCreateRoomListing: (
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
    ) -> Unit = { _, _, _, _ -> },
    onCreateRoomConfigured: ((
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
        roomMode: RoomMode,
        backdropTheme: VoiceRoomBackdropTheme,
    ) -> Unit)? = null,
    onOpenEventsRankings: () -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    onShowToast: (String) -> Unit = {},
    initialCategoryFilter: RoomCategory = RoomCategory.ALL,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        VoiceRoomsLobbyScreen(
            rooms = rooms,
            currentUserId = currentUserId,
            onEnterRoom = onEnterRoom,
            onEnterRoomById = onEnterRoomById,
            onCreateRoomListing = onCreateRoomListing,
            onCreateRoomConfigured = onCreateRoomConfigured,
            onOpenEventsRankings = onOpenEventsRankings,
            onOpenUserProfile = onOpenUserProfile,
            onShowToast = onShowToast,
            initialCategoryFilter = initialCategoryFilter
        )
    }
}
