package com.example.ui.rooms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.ui.components.AuctionGavel3DIcon
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveVoiceBars
import com.example.ui.components.RoomRankCrestBadge
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.components.VoiceRoom3DStageIcon
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseCyan
import com.example.ui.theme.TurquoiseDeep

private data class FeaturedCarnivalItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val colors: List<Color>,
    val categoryMatch: RoomCategory,
    val targetRoomId: String? = null,
)

private data class RoomDiscoverySection(
    val id: String,
    val title: String,
    val badge: String,
    val rooms: List<VoiceRoom>,
)

@Composable
fun VoiceRoomsLobbyScreen(
    rooms: List<VoiceRoom>,
    onEnterRoom: (VoiceRoom) -> Unit = {},
    onEnterRoomById: (String) -> Unit = {},
    onCreateRoomListing: (
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
    ) -> Unit,
    onCreateRoomConfigured: ((
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
        roomMode: RoomMode,
        backdropTheme: VoiceRoomBackdropTheme,
    ) -> Unit)? = null,
    currentUserId: String = "usr_nova_01",
    onOpenEventsRankings: () -> Unit = {},
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
    onShowToast: (String) -> Unit = {},
    initialCategoryFilter: RoomCategory = RoomCategory.ALL,
) {
    var headerScopeTab by remember { mutableIntStateOf(1) } // 0 = Related, 1 = All, 2 = Popular
    var selectedCategory by remember(initialCategoryFilter) { mutableStateOf(initialCategoryFilter) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showEnterByIdDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var previewRoom by remember { mutableStateOf<VoiceRoom?>(null) }
    var bookmarkedRoomIds by remember { mutableStateOf(setOf<String>()) }

    val featuredCarousels = remember {
        listOf(
            FeaturedCarnivalItem(
                title = "WeFun Official Carnival",
                subtitle = "Verified Creators & Weekly Highlights",
                badge = "OFFICIAL",
                colors = listOf(TurquoiseActive, BlueAccent),
                categoryMatch = RoomCategory.OFFICIAL,
                targetRoomId = "rm_107"
            ),
            FeaturedCarnivalItem(
                title = "Royal Relationship Auction",
                subtitle = "Purple & Gold Stage • Live BFF Bidding",
                badge = "AUCTION MODE",
                colors = listOf(PrimaryPurple, Color(0xFF431173)),
                categoryMatch = RoomCategory.ALL,
                targetRoomId = "320252"
            ),
            FeaturedCarnivalItem(
                title = "PK Arena Highlights",
                subtitle = "Top Community Battle Teams",
                badge = "PK EVENT",
                colors = listOf(PrimaryPurple, PinkAccent),
                categoryMatch = RoomCategory.PK
            ),
            FeaturedCarnivalItem(
                title = "Music & Acoustic Hubs",
                subtitle = "Chill playlists & live showcases",
                badge = "MUSIC",
                colors = listOf(OrangeAccent, PinkAccent),
                categoryMatch = RoomCategory.MUSIC
            ),
        )
    }

    val categoryTabs = remember {
        listOf(
            RoomCategory.ALL to "All",
            RoomCategory.DATING to "Friends",
            RoomCategory.PK to "PK",
            RoomCategory.MUSIC to "Music",
            RoomCategory.VIDEO to "Video",
            RoomCategory.GAME to "Game",
        )
    }

    val filteredRooms = remember(rooms, headerScopeTab, selectedCategory, searchQuery, currentUserId) {
        rooms.filter { rm ->
            val matchesScope = when (headerScopeTab) {
                0 -> rm.isFavorited || rm.category == RoomCategory.FAMILY || rm.hostId == currentUserId || rm.isNewRoom
                2 -> rm.popularity >= 25000L || rm.onlineCount >= 100
                else -> true
            }
            val matchesCat = when (selectedCategory) {
                RoomCategory.ALL -> true
                RoomCategory.DATING -> rm.category == RoomCategory.DATING || rm.category == RoomCategory.PUBLIC
                else -> rm.category == selectedCategory
            }
            val matchesQuery = searchQuery.isBlank() ||
                rm.title.contains(searchQuery, ignoreCase = true) ||
                rm.hostName.contains(searchQuery, ignoreCase = true) ||
                rm.id.contains(searchQuery, ignoreCase = true) ||
                rm.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesScope && matchesCat && matchesQuery
        }.sortedWith(
            compareByDescending<VoiceRoom> { it.isOfficial || it.category == RoomCategory.OFFICIAL }
                .thenByDescending { it.hostId == currentUserId && it.isNewRoom }
                .thenByDescending { it.roomMode == RoomMode.AUCTION }
                .thenByDescending { it.popularity }
                .thenByDescending { it.createdAtMillis }
        )
    }

    val sections = remember(filteredRooms, selectedCategory, headerScopeTab, searchQuery, currentUserId) {
        val showCategorizedSections =
            selectedCategory == RoomCategory.ALL && headerScopeTab == 1 && searchQuery.isBlank()
        if (!showCategorizedSections) {
            if (filteredRooms.isEmpty()) emptyList()
            else listOf(
                RoomDiscoverySection(
                    id = "filtered",
                    title = "${selectedCategory.label} Voice Rooms",
                    badge = "${filteredRooms.size} Live",
                    rooms = filteredRooms
                )
            )
        } else {
            val assigned = mutableSetOf<String>()
            fun takeUnique(predicate: (VoiceRoom) -> Boolean): List<VoiceRoom> {
                val matched = filteredRooms.filter { it.id !in assigned && predicate(it) }
                matched.forEach { assigned.add(it.id) }
                return matched
            }

            val official = takeUnique { it.isOfficial || it.category == RoomCategory.OFFICIAL }
            val myAndNew = takeUnique { it.hostId == currentUserId && it.isNewRoom }
            val recommended = takeUnique { it.roomMode == RoomMode.AUCTION || it.isFavorited }
            val popular = takeUnique { it.popularity >= 30_000L || it.onlineCount >= 120 }
            val friends = takeUnique {
                it.category == RoomCategory.DATING ||
                    it.category == RoomCategory.FAMILY ||
                    it.tags.any { tag -> tag.equals("Friends", ignoreCase = true) }
            }
            val recentlyActive = takeUnique { it.isRecentlyActive && !it.isNewRoom }
            val newRooms = myAndNew + takeUnique { it.isNewRoom || it.id !in assigned }

            listOfNotNull(
                official.takeIf { it.isNotEmpty() }?.let {
                    RoomDiscoverySection("official", "Official Rooms", "VERIFIED", it)
                },
                newRooms.takeIf { it.isNotEmpty() }?.let {
                    val hasOwnRoom = it.any { rm -> rm.hostId == currentUserId }
                    RoomDiscoverySection(
                        id = "new",
                        title = if (hasOwnRoom) "My & New Rooms" else "New Rooms",
                        badge = if (hasOwnRoom) "LIVE HOST" else "NEW",
                        rooms = it
                    )
                },
                recommended.takeIf { it.isNotEmpty() }?.let {
                    RoomDiscoverySection("recommended", "Recommended Rooms", "HOT", it)
                },
                popular.takeIf { it.isNotEmpty() }?.let {
                    RoomDiscoverySection("popular", "Popular Rooms", "TRENDING", it)
                },
                friends.takeIf { it.isNotEmpty() }?.let {
                    RoomDiscoverySection("friends", "Friends' Rooms", "SOCIAL", it)
                },
                recentlyActive.takeIf { it.isNotEmpty() }?.let {
                    RoomDiscoverySection("recent", "Recently Active Rooms", "ACTIVE", it)
                },
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("voice_rooms_lobby_list"),
        contentPadding = PaddingValues(bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header: Voice Room + Related / All / Popular + Search + Enter ID + Create (+) + Notifications
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBackground)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Room",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showEnterByIdDialog = true },
                            modifier = Modifier.testTag("enter_room_by_id_button")
                        ) {
                            Icon(Icons.Rounded.Dialpad, "Enter Room ID", tint = TurquoiseDeep)
                        }
                        IconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier.testTag("voice_rooms_search_toggle")
                        ) {
                            Icon(Icons.Rounded.Search, "Search Voice Rooms", tint = PrimaryText)
                        }
                        Surface(
                            onClick = { showCreateDialog = true },
                            shape = RoundedCornerShape(50),
                            color = TurquoiseActive,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("fab_create_room")
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Brush.horizontalGradient(listOf(TurquoiseCyan, TurquoiseActive)))
                                    .padding(horizontal = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Rounded.Add, "Create Room", tint = Color.White, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Host",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        IconButton(
                            onClick = onOpenEventsRankings,
                            modifier = Modifier.testTag("voice_rooms_announcements_button")
                        ) {
                            Icon(Icons.Rounded.NotificationsNone, "Announcements & Rankings", tint = PrimaryText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Related", "All", "Popular").forEachIndexed { idx, label ->
                        val selected = headerScopeTab == idx
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { headerScopeTab = idx }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                style = if (selected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (selected) PrimaryText else SecondaryText
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (selected) TurquoiseActive else Color.Transparent)
                            )
                        }
                    }
                }

                if (showSearchBar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search room title, host or Room ID...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("voice_rooms_search_input")
                    )
                }
            }
            HorizontalDivider(color = BorderColor)
        }

        // Featured Promotional Banner Carousel
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredCarousels) { banner ->
                    Box(
                        modifier = Modifier
                            .width(296.dp)
                            .height(100.dp)
                            .shadow(5.dp, RoundedCornerShape(20.dp), ambientColor = banner.colors.first().copy(alpha = 0.28f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(banner.colors))
                            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                            .clickable {
                                val directRoom = banner.targetRoomId?.let { id ->
                                    rooms.firstOrNull { it.id == id }
                                }
                                if (directRoom != null) {
                                    onEnterRoom(directRoom)
                                } else {
                                    selectedCategory = banner.categoryMatch
                                    onOpenEventsRankings()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.24f)) {
                                    Text(
                                        text = banner.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = banner.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = banner.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(modifier = Modifier.width(64.dp).height(34.dp)) {
                                listOf("WF" to 0xFF00C2B8, "DR" to 0xFF7B2CFF, "KS" to 0xFFFF8A2B).forEachIndexed { idx, (init, col) ->
                                    Box(
                                        modifier = Modifier
                                            .offset(x = (idx * 15).dp)
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(col))
                                            .border(2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(init, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Tabs
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryTabs) { (cat, label) ->
                    val selected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(50),
                        color = if (selected) TurquoiseActive else SecondaryBackground,
                        border = BorderStroke(1.dp, if (selected) TurquoiseActive else BorderColor),
                        modifier = Modifier.testTag("voice_category_tab_${label.lowercase()}")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) Color.White else PrimaryText,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        if (sections.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.GraphicEq, null, tint = TurquoiseActive, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No matching rooms found", style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                        Text("Try another category tab or enter a Room ID.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }
        } else {
            sections.forEach { section ->
                item(key = "header_${section.id}") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .testTag("voice_section_${section.id}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryText
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = TurquoiseActive.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = section.badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                items(section.rooms, key = { it.id }) { room ->
                    VoiceRoomListingCard(
                        room = room,
                        isSaved = room.id in bookmarkedRoomIds,
                        onEnterRoom = { onEnterRoom(room) },
                        onOpenInfoPreview = { previewRoom = room },
                        onClickHostAvatar = { onOpenUserProfile(room.hostId, room.hostName) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Quick Voice Match Section at bottom
            item(key = "quick_voice_match_card") {
                val matchTarget = filteredRooms.maxByOrNull { it.onlineCount }
                if (matchTarget != null) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("quick_voice_match_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(TurquoiseActive.copy(alpha = 0.12f), PrimaryPurple.copy(alpha = 0.08f))
                                    )
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Rounded.AutoAwesome, null, tint = TurquoiseActive, modifier = Modifier.size(18.dp))
                                    Text("Quick Voice Match", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                                }
                                Text(
                                    text = "Instant match to ${matchTarget.title} (${matchTarget.onlineCount} online)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            WefunGradientButton(
                                text = "Match Now",
                                testTag = "quick_voice_match_button",
                                onClick = { onEnterRoom(matchTarget) },
                                modifier = Modifier.width(124.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEnterByIdDialog) {
        var roomIdInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showEnterByIdDialog = false },
            containerColor = PrimaryBackground,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Enter by Room ID", fontWeight = FontWeight.Bold, color = PrimaryText) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter an active WeFun Room ID (e.g., 320252 or 1579056) to join directly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                    OutlinedTextField(
                        value = roomIdInput,
                        onValueChange = { roomIdInput = it },
                        label = { Text("Room ID") },
                        placeholder = { Text("320252") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("enter_room_id_input")
                    )
                }
            },
            confirmButton = {
                WefunGradientButton(
                    text = "Join Room",
                    testTag = "confirm_enter_room_by_id",
                    onClick = {
                        showEnterByIdDialog = false
                        onEnterRoomById(roomIdInput)
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEnterByIdDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Room Discovery Info Preview Card
    if (previewRoom != null) {
        val target = previewRoom!!
        val isSaved = target.id in bookmarkedRoomIds
        AlertDialog(
            onDismissRequest = { previewRoom = null },
            containerColor = PrimaryBackground,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(target.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryText, maxLines = 2)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        RoomRankCrestBadge(level = target.roomLevel, isOfficial = target.isOfficial || target.category == RoomCategory.OFFICIAL)
                        if (target.roomMode == RoomMode.AUCTION) {
                            Surface(shape = RoundedCornerShape(6.dp), color = PrimaryPurple.copy(alpha = 0.14f)) {
                                Text(
                                    text = "AUCTION",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryPurple,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(target.description, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)
                    Surface(
                        onClick = {
                            previewRoom = null
                            onOpenUserProfile(target.hostId, target.hostName)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = SecondaryBackground,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarCircle(
                                initials = target.hostName.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                accentColor = TurquoiseActive,
                                size = 38.dp,
                                vipLevel = target.hostVip
                            )
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    VipNicknameText(
                                        name = target.hostName,
                                        vipLevel = target.hostVip,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (target.hostVip > 0) {
                                        VipLevelBadge3D(vipLevel = target.hostVip, compact = true)
                                    }
                                }
                                Text("Room ID: ${target.id} • Tap to view host", style = MaterialTheme.typography.labelSmall, color = TurquoiseDeep)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Rounded.People, null, tint = TurquoiseActive, modifier = Modifier.size(16.dp))
                        Text("${target.onlineCount} online • ${target.category.label}", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        previewRoom = null
                        onEnterRoom(target)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive)
                ) {
                    Text("Enter Room", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            bookmarkedRoomIds = if (isSaved) bookmarkedRoomIds - target.id else bookmarkedRoomIds + target.id
                            onShowToast(if (isSaved) "Removed ${target.title} from bookmarks" else "Saved ${target.title} to bookmarks")
                            previewRoom = null
                        }
                    ) {
                        Text(if (isSaved) "Saved" else "Save")
                    }
                    TextButton(onClick = { previewRoom = null }) { Text("Close") }
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateRoomListingDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, cat, pass, mode, backdrop ->
                showCreateDialog = false
                if (onCreateRoomConfigured != null) {
                    onCreateRoomConfigured(title, desc, cat, pass, mode, backdrop)
                } else {
                    onCreateRoomListing(title, desc, cat, pass)
                }
            }
        )
    }
}

@Composable
private fun VoiceRoomListingCard(
    room: VoiceRoom,
    isSaved: Boolean,
    onEnterRoom: () -> Unit,
    onOpenInfoPreview: () -> Unit,
    onClickHostAvatar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOfficial = room.isOfficial || room.category == RoomCategory.OFFICIAL
    val isAuction = room.roomMode == RoomMode.AUCTION
    val coverColor = Color(room.themeStyle.primaryHex)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}"),
        onClick = onEnterRoom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            when {
                                isAuction -> listOf(Color(0xFF431173), PrimaryPurple)
                                isOfficial -> listOf(TurquoiseActive, BlueAccent)
                                else -> listOf(coverColor, Color(room.themeStyle.secondaryHex))
                            }
                        )
                    )
                    .clickable(onClick = onClickHostAvatar),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(
                    initials = room.hostName.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                    accentColor = if (isOfficial) TurquoiseActive else coverColor,
                    size = 44.dp,
                    vipLevel = room.hostVip,
                    isSpeaking = false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = room.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!room.password.isNullOrBlank()) {
                        Icon(Icons.Rounded.Lock, "Locked", tint = GoldAmber, modifier = Modifier.size(13.dp))
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    RoomRankCrestBadge(level = room.roomLevel, isOfficial = isOfficial)
                    if (isAuction) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryPurple.copy(alpha = 0.14f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                AuctionGavel3DIcon(size = 11.dp)
                                Text(
                                    text = "Auction",
                                    fontSize = 9.sp,
                                    color = PrimaryPurple,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SecondaryBackground
                    ) {
                        Text(
                            text = "ID ${room.id}",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Host:", fontSize = 10.sp, color = SecondaryText)
                    VipNicknameText(
                        name = room.hostName,
                        vipLevel = room.hostVip,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (room.hostVip > 0) {
                        VipLevelBadge3D(vipLevel = room.hostVip, compact = true)
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onOpenInfoPreview,
                    modifier = Modifier.size(28.dp).testTag("room_preview_btn_${room.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "Room info & bookmark",
                        tint = if (isSaved) TurquoiseActive else SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                LiveVoiceBars(isActive = true, rmsLevel = 0.65f)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Rounded.People, "Online", tint = SecondaryText, modifier = Modifier.size(13.dp))
                    Text(
                        text = "${room.onlineCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateRoomListingDialog(
    onDismiss: () -> Unit,
    onCreate: (
        title: String,
        description: String,
        category: RoomCategory,
        password: String?,
        roomMode: RoomMode,
        backdropTheme: VoiceRoomBackdropTheme,
    ) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(RoomCategory.DATING) }
    var password by remember { mutableStateOf("") }
    var roomMode by remember { mutableStateOf(RoomMode.NORMAL) }
    var backdropTheme by remember { mutableStateOf(VoiceRoomBackdropTheme.ROYAL_BLUE) }

    val backdropOptions = remember {
        listOf(
            VoiceRoomBackdropTheme.ROYAL_BLUE to "Starlight",
            VoiceRoomBackdropTheme.AUCTION_ROYAL to "Auction",
            VoiceRoomBackdropTheme.PEARL_LIGHT to "Daylight",
            VoiceRoomBackdropTheme.AURORA_VIOLET to "Aurora",
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PrimaryBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VoiceRoom3DStageIcon(size = 42.dp)
                Column {
                    Text(
                        text = "Create Voice Room",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
                    Text(
                        text = "Launch your live stage & auto-enter as Host",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Room Name") },
                    placeholder = { Text("e.g., Sunset Chill Lounge") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("create_room_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Topic / Welcome Notice") },
                    placeholder = { Text("Music, games & good vibes!") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Room Mode",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val normalSelected = roomMode == RoomMode.NORMAL
                    Surface(
                        onClick = {
                            roomMode = RoomMode.NORMAL
                            if (backdropTheme == VoiceRoomBackdropTheme.AUCTION_ROYAL) {
                                backdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (normalSelected) TurquoiseActive.copy(alpha = 0.10f) else SecondaryBackground,
                        border = BorderStroke(1.5.dp, if (normalSelected) TurquoiseActive else BorderColor),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_room_mode_normal")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VoiceRoom3DStageIcon(size = 24.dp)
                            Column {
                                Text("Normal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryText)
                                Text("8-Seat Hangout", fontSize = 10.sp, color = SecondaryText)
                            }
                        }
                    }

                    val auctionSelected = roomMode == RoomMode.AUCTION
                    Surface(
                        onClick = {
                            roomMode = RoomMode.AUCTION
                            backdropTheme = VoiceRoomBackdropTheme.AUCTION_ROYAL
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (auctionSelected) PrimaryPurple.copy(alpha = 0.12f) else SecondaryBackground,
                        border = BorderStroke(1.5.dp, if (auctionSelected) PrimaryPurple else BorderColor),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_room_mode_auction")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AuctionGavel3DIcon(size = 24.dp)
                            Column {
                                Text("Auction", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryText)
                                Text("BFF & Bidding", fontSize = 10.sp, color = PrimaryPurple)
                            }
                        }
                    }
                }

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        RoomCategory.DATING to "Friends",
                        RoomCategory.PK to "PK",
                        RoomCategory.MUSIC to "Music",
                        RoomCategory.VIDEO to "Video",
                        RoomCategory.GAME to "Game",
                    ).forEach { (cat, label) ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TurquoiseActive.copy(alpha = 0.16f),
                                selectedLabelColor = TurquoiseDeep
                            )
                        )
                    }
                }

                Text(
                    text = "Stage Backdrop",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    backdropOptions.forEach { (theme, label) ->
                        val isSelected = backdropTheme == theme
                        Surface(
                            onClick = { backdropTheme = theme },
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) TurquoiseActive.copy(alpha = 0.12f) else SecondaryBackground,
                            border = BorderStroke(1.2.dp, if (isSelected) TurquoiseActive else BorderColor),
                            modifier = Modifier.testTag("create_room_backdrop_${theme.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(theme.topHex), Color(theme.bottomHex))))
                                        .border(1.dp, BorderColor, CircleShape)
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = PrimaryText
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Optional Passcode") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            WefunGradientButton(
                text = "Create & Enter Room",
                testTag = "confirm_create_room_button",
                onClick = {
                    onCreate(
                        title,
                        description,
                        category,
                        password.takeIf { it.isNotBlank() },
                        roomMode,
                        backdropTheme
                    )
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}
