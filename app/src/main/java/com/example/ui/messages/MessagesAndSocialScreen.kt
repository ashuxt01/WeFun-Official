package com.example.ui.messages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatThread
import com.example.data.model.DirectMessage
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.ui.components.AvatarCircle
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun MessagesAndSocialScreen(
    currentUser: UserProfile,
    threads: List<ChatThread>,
    activeThreadId: String?,
    activeMessages: List<DirectMessage>,
    blockedUserIds: Set<String>,
    onOpenThread: (String?) -> Unit,
    onStartOrOpenChat: (peerId: String, peerName: String, isGroup: Boolean) -> Unit,
    onSendDirectMessage: (threadId: String, text: String, gift: VirtualGift?, voiceSeconds: Int?) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit = { _, _ -> },
) {
    val activeThread = threads.firstOrNull { it.id == activeThreadId }
    if (activeThread != null) {
        BackHandler { onOpenThread(null) }
        CleanChatThreadPane(
            currentUser = currentUser,
            thread = activeThread,
            messages = activeMessages,
            onBack = { onOpenThread(null) },
            onOpenPeerProfile = { onOpenUserProfile(activeThread.peerId, activeThread.peerName) },
            onSend = { txt -> onSendDirectMessage(activeThread.id, txt, null, null) },
            onDeleteMessage = onDeleteMessage
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showContactsDialog by remember { mutableStateOf(false) }

    val unblockedThreads = remember(threads, blockedUserIds) {
        threads.filterNot { it.peerId in blockedUserIds }
    }
    val visibleThreads = remember(unblockedThreads, searchQuery) {
        unblockedThreads.filter { th ->
            !th.isRequest && (searchQuery.isBlank() ||
                th.peerName.contains(searchQuery, ignoreCase = true) ||
                th.lastMessagePreview.contains(searchQuery, ignoreCase = true))
        }
    }
    val pendingRequests = remember(unblockedThreads) {
        unblockedThreads.filter { it.isRequest }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        // Minimal Clean Header: Chats + Contacts + New Chat (+)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Rounded.Search, "Search Chats", tint = PrimaryText)
                    }
                    Box {
                        IconButton(
                            onClick = { showContactsDialog = true },
                            modifier = Modifier.testTag("chats_friends_button")
                        ) {
                            Icon(Icons.Rounded.People, "Contacts", tint = PrimaryText)
                        }
                        if (pendingRequests.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PinkAccent)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showNewChatDialog = true },
                        modifier = Modifier.testTag("chats_new_chat_button")
                    ) {
                        Icon(Icons.Rounded.Add, "New Chat", tint = PrimaryText)
                    }
                }
            }

            if (showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search messages...", color = SecondaryText) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, tint = SecondaryText) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Rounded.Close, "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("chats_search_input")
                )
            }
        }

        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            if (visibleThreads.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = TurquoiseActive, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No conversations yet", style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                        Text("Tap + above to start a chat with a friend.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            } else {
                items(visibleThreads, key = { it.id }) { thread ->
                    CleanChatListRow(
                        thread = thread,
                        onClick = { onOpenThread(thread.id) },
                        onClickAvatar = {
                            if (!thread.isGroup) onOpenUserProfile(thread.peerId, thread.peerName)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 78.dp, end = 16.dp),
                        color = BorderColor
                    )
                }
            }
        }
    }

    if (showNewChatDialog) {
        val candidates = listOf(
            "host_rhea" to "DJ Rhea",
            "host_kai" to "Kai Storm",
            "host_zara" to "Zara Moon",
            "host_milo" to "Milo Taktik",
            "host_leo" to "Leo Beats",
        )
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            containerColor = PrimaryBackground,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Start New Chat", fontWeight = FontWeight.Bold, color = PrimaryText) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    candidates.forEach { (uid, name) ->
                        OutlinedButton(
                            onClick = {
                                showNewChatDialog = false
                                onStartOrOpenChat(uid, name, false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(name, color = PrimaryText)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showNewChatDialog = false }) { Text("Close") } }
        )
    }

    if (showContactsDialog) {
        AlertDialog(
            onDismissRequest = { showContactsDialog = false },
            containerColor = PrimaryBackground,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Contacts & Requests", fontWeight = FontWeight.Bold, color = PrimaryText) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (pendingRequests.isEmpty()) {
                        Text("No pending friend requests right now.", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    } else {
                        pendingRequests.forEach { req ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = req.peerName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText,
                                    modifier = Modifier.clickable {
                                        showContactsDialog = false
                                        onOpenUserProfile(req.peerId, req.peerName)
                                    }
                                )
                                Text(req.lastMessagePreview, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onAcceptRequest(req.id) }) { Text("Accept") }
                                    TextButton(onClick = { onDeclineRequest(req.id) }) { Text("Decline", color = PinkAccent) }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactsDialog = false }) { Text("Done") }
            }
        )
    }
}

@Composable
private fun CleanChatListRow(
    thread: ChatThread,
    onClick: () -> Unit,
    onClickAvatar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("chat_thread_${thread.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.clickable(onClick = onClickAvatar)) {
            AvatarCircle(
                initials = thread.peerInitials,
                accentColor = Color(thread.accentHex),
                size = 50.dp,
                vipLevel = thread.vipLevel,
                isSpeaking = false
            )
            if (thread.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                        .border(2.dp, PrimaryBackground, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                VipNicknameText(
                    name = thread.peerName,
                    vipLevel = thread.vipLevel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (thread.vipLevel > 0) {
                    VipLevelBadge3D(vipLevel = thread.vipLevel, compact = true)
                }
                if (thread.isOfficial) {
                    Surface(shape = RoundedCornerShape(50), color = TurquoiseActive.copy(alpha = 0.14f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Verified, null, tint = TurquoiseActive, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("OFFICIAL", fontSize = 9.sp, color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Text(
                text = thread.lastMessagePreview,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = thread.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            if (thread.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PinkAccent)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${thread.unreadCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanChatThreadPane(
    currentUser: UserProfile,
    thread: ChatThread,
    messages: List<DirectMessage>,
    onBack: () -> Unit,
    onOpenPeerProfile: () -> Unit,
    onSend: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(bottom = 78.dp)
    ) {
        Surface(color = PrimaryBackground) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to Chats", tint = PrimaryText)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.clickable(enabled = !thread.isGroup, onClick = onOpenPeerProfile)
                ) {
                    AvatarCircle(
                        initials = thread.peerInitials,
                        accentColor = Color(thread.accentHex),
                        size = 38.dp,
                        vipLevel = thread.vipLevel
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VipNicknameText(
                                name = thread.peerName,
                                vipLevel = thread.vipLevel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (thread.vipLevel > 0) {
                                VipLevelBadge3D(vipLevel = thread.vipLevel, compact = true)
                            }
                        }
                        Text(thread.lastSeenLabel, style = MaterialTheme.typography.labelSmall, color = TurquoiseDeep)
                    }
                }
            }
        }
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMine = msg.senderId == currentUser.uid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isMine) TurquoiseActive else SecondaryBackground,
                        border = if (isMine) null else BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMine) Color.White else PrimaryText
                            )
                            if (isMine) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.DoneAll,
                                        "Read",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Icon(
                                        Icons.Rounded.Delete,
                                        "Delete",
                                        tint = Color.White.copy(alpha = 0.75f),
                                        modifier = Modifier.size(13.dp).clickable { onDeleteMessage(msg.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = BorderColor)
        Row(
            modifier = Modifier.fillMaxWidth().background(PrimaryBackground).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Message...") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).testTag("dm_input")
            )
            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        onSend(input)
                        input = ""
                    }
                },
                modifier = Modifier.size(46.dp).clip(CircleShape).background(TurquoiseActive).testTag("dm_send_button")
            ) {
                Icon(Icons.Rounded.Send, "Send message", tint = Color.White)
            }
        }
    }
}
