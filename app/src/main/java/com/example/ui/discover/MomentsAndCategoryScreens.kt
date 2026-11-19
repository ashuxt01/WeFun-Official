package com.example.ui.discover

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.DiscoverCategory
import com.example.data.model.SocialPost
import com.example.data.repository.WefunSeedData
import com.example.ui.components.AvatarCircle
import com.example.ui.components.DiscoverArch3DIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.VipBadge
import com.example.ui.components.VipLevelBadge3D
import com.example.ui.components.VipNicknameText
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseDeep

@Composable
fun MomentsScreen(
    posts: List<SocialPost>,
    onBack: () -> Unit,
    onCreatePost: (content: String, hashtag: String, mediaType: String, imageUri: String?) -> Unit,
    onToggleLikePost: (SocialPost) -> Unit,
    onCommentPost: (SocialPost) -> Unit,
    onReportPost: (SocialPost) -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var showCreateComposer by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("moments_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("moments_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to Discover", tint = PrimaryText)
                }
                Text(
                    text = "Moments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
            }

            Surface(
                onClick = { showCreateComposer = true },
                shape = RoundedCornerShape(50),
                color = TurquoiseActive,
                modifier = Modifier.padding(end = 8.dp).testTag("create_moment_post_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(posts, key = { it.id }) { post ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    onOpenUserProfile(post.authorId, post.authorName)
                                }
                            ) {
                                AvatarCircle(
                                    initials = post.authorName.take(2),
                                    accentColor = TurquoiseActive,
                                    size = 42.dp,
                                    vipLevel = post.authorVip
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        VipNicknameText(
                                            name = post.authorName,
                                            vipLevel = post.authorVip,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (post.authorVip > 0) {
                                            VipLevelBadge3D(vipLevel = post.authorVip, compact = true)
                                        }
                                    }
                                    Text(post.hashtag, style = MaterialTheme.typography.labelSmall, color = TurquoiseDeep)
                                }
                            }
                            IconButton(onClick = { onReportPost(post) }) {
                                Icon(Icons.Rounded.Report, "Report post", tint = SecondaryText, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text(post.content, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)

                        if (post.imageUri != null) {
                            AsyncImage(
                                model = post.imageUri,
                                contentDescription = "Moment media",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } else if (post.mediaType == "SHORT_VIDEO") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(124.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.linearGradient(listOf(TurquoiseActive, PrimaryPurple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.PlayCircle, null, tint = Color.White, modifier = Modifier.size(34.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Short Video Highlight • 0:24", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else if (post.mediaType == "IMAGE") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.horizontalGradient(listOf(PinkAccent.copy(0.14f), TurquoiseActive.copy(0.16f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Image, null, tint = TurquoiseDeep, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("WeFun Moment Snapshot", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onToggleLikePost(post) }
                                ) {
                                    Icon(
                                        imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = "Like moment",
                                        tint = if (post.isLiked) PinkAccent else SecondaryText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${post.likesCount}", style = MaterialTheme.typography.labelMedium, color = PrimaryText)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onCommentPost(post) }
                                ) {
                                    Icon(Icons.Rounded.ChatBubbleOutline, "Comment", tint = SecondaryText, modifier = Modifier.size(19.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${post.commentsCount}", style = MaterialTheme.typography.labelMedium, color = PrimaryText)
                                }
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "${post.authorName} on WeFun: ${post.content} ${post.hashtag}")
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Moment"))
                                }
                            ) {
                                Icon(Icons.Rounded.Share, "Share moment", tint = TurquoiseActive, modifier = Modifier.size(19.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateComposer) {
        CreateMomentComposerDialog(
            onDismiss = { showCreateComposer = false },
            onPublish = { content, hashtag, mediaType, uri ->
                showCreateComposer = false
                onCreatePost(content, hashtag, mediaType, uri)
            }
        )
    }
}

@Composable
private fun CreateMomentComposerDialog(
    onDismiss: () -> Unit,
    onPublish: (content: String, hashtag: String, mediaType: String, imageUri: String?) -> Unit,
) {
    var caption by remember { mutableStateOf("") }
    var hashtag by remember { mutableStateOf("#WeFun") }
    var mediaType by remember { mutableStateOf("TEXT") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            mediaType = "IMAGE"
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            mediaType = "SHORT_VIDEO"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PrimaryBackground,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Create Moment", fontWeight = FontWeight.Bold, color = PrimaryText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption") },
                    placeholder = { Text("Share what you're up to...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("moment_caption_input")
                )
                OutlinedTextField(
                    value = hashtag,
                    onValueChange = { hashtag = it },
                    label = { Text("Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Image, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Photo", maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = {
                            videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Videocam, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Video", maxLines = 1)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = mediaType == "IMAGE",
                        onClick = { mediaType = if (mediaType == "IMAGE") "TEXT" else "IMAGE" },
                        label = { Text("Photo Card") }
                    )
                    FilterChip(
                        selected = mediaType == "SHORT_VIDEO",
                        onClick = { mediaType = if (mediaType == "SHORT_VIDEO") "TEXT" else "SHORT_VIDEO" },
                        label = { Text("Video Clip") }
                    )
                }

                // Live Post Preview Card
                if (caption.isNotBlank() || selectedUri != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SecondaryBackground,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("POST PREVIEW", style = MaterialTheme.typography.labelSmall, color = TurquoiseDeep, fontWeight = FontWeight.Bold)
                            Text(caption.ifBlank { "New WeFun moment..." }, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            if (selectedUri != null) {
                                AsyncImage(
                                    model = selectedUri,
                                    contentDescription = "Selected media preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(10.dp))
                                )
                            } else if (mediaType != "TEXT") {
                                Text("Attached: $mediaType", style = MaterialTheme.typography.labelSmall, color = TurquoiseActive)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            WefunGradientButton(
                text = "Publish Moment",
                enabled = caption.isNotBlank(),
                onClick = { onPublish(caption, hashtag, mediaType, selectedUri?.toString()) },
                testTag = "publish_moment_button"
            )
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun DiscoverCategoryScreen(
    category: DiscoverCategory,
    followedUserIds: Set<String>,
    onBack: () -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenUserProfile: (userId: String, userName: String) -> Unit,
    onChatWithUser: (userId: String, userName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val members = remember(category) { WefunSeedData.discoverMembers(category) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .testTag("discover_category_screen_${category.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("discover_category_back_button")
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to Discover", tint = PrimaryText)
            }
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText
            )
        }
        HorizontalDivider(color = BorderColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DiscoverArch3DIcon(category = category, size = 60.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(category.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text(category.subtitle, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                    }
                }
            }

            items(members, key = { it.userId }) { member ->
                val isFollowing = member.userId in followedUserIds
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenUserProfile(member.userId, member.name) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box {
                                AvatarCircle(
                                    initials = member.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                    accentColor = Color(member.accentHex),
                                    size = 48.dp,
                                    vipLevel = member.vipLevel
                                )
                                if (member.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF26B887))
                                            .border(2.dp, Color.White, CircleShape)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    VipNicknameText(
                                        name = member.name,
                                        vipLevel = member.vipLevel,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (member.vipLevel > 0) {
                                        VipLevelBadge3D(vipLevel = member.vipLevel, compact = true)
                                    }
                                }
                                Text(member.roleTag, style = MaterialTheme.typography.bodySmall, color = TurquoiseDeep)
                                Text(member.distanceOrGroup, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                onClick = { onToggleFollow(member.userId) },
                                shape = RoundedCornerShape(50),
                                color = if (isFollowing) SecondaryBackground else TurquoiseActive
                            ) {
                                Text(
                                    text = if (isFollowing) "Following" else "Follow",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isFollowing) PrimaryText else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                            Surface(
                                onClick = { onChatWithUser(member.userId, member.name) },
                                shape = RoundedCornerShape(50),
                                color = SecondaryBackground,
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Text(
                                    text = "Chat",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
