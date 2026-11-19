package com.example.ui.profile

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AcceptedRelationship
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.SocialPost
import com.example.ui.components.AvatarCircle
import com.example.ui.components.BffToken3DArt
import com.example.ui.components.RelationshipTag3DIcon
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive

@Composable
internal fun ProfileSectionRowHeader(
    title: String,
    countText: String? = null,
    accentHeart: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String = "",
) {
    val rowMod = Modifier
        .fillMaxWidth()
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
        .padding(vertical = 4.dp)

    Row(
        modifier = rowMod,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText,
            )
            if (accentHeart) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFFF6B8B),
                    modifier = Modifier.size(16.dp)
                )
            }
            if (!countText.isNullOrBlank()) {
                Text(
                    text = countText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (accentHeart) Color(0xFFFF6B8B) else SecondaryText,
                )
            }
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Open $title",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun ProfileMomentsStrip(
    moments: List<SocialPost>,
    isOwnProfile: Boolean,
    onOpenAllMoments: () -> Unit,
    onSelectMoment: (SocialPost) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ProfileSectionRowHeader(
            title = "Moments",
            countText = moments.size.toString(),
            onClick = onOpenAllMoments,
            testTag = "profile_moments_header"
        )
        if (moments.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SecondaryBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAllMoments)
                    .testTag("profile_moments_empty")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isOwnProfile) "No moments shared yet. Tap to post your first Moment!" else "No public moments yet.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        modifier = Modifier.weight(1f)
                    )
                    if (isOwnProfile) {
                        Text("Post", color = TurquoiseActive, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(moments, key = { it.id }) { post ->
                    Surface(
                        onClick = { onSelectMoment(post) },
                        shape = RoundedCornerShape(14.dp),
                        color = SecondaryBackground,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier
                            .size(82.dp)
                            .testTag("profile_moment_thumb_${post.id}")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when {
                                post.imageUri != null -> AsyncImage(
                                    model = post.imageUri,
                                    contentDescription = post.content,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                post.mediaType == "SHORT_VIDEO" -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(listOf(Color(0xFF1E293B), PrimaryPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.PlayCircle, null, tint = Color.White, modifier = Modifier.size(30.dp))
                                }
                                post.mediaType == "IMAGE" -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(listOf(Color(0xFFE0F2FE), Color(0xFFFCE7F3)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Image, null, tint = TurquoiseActive, modifier = Modifier.size(28.dp))
                                }
                                else -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFF8FAFC))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = post.content,
                                        fontSize = 10.sp,
                                        color = PrimaryText,
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileGiftWallCard(
    giftWall: GiftWallSummary,
    onOpenGiftWall: () -> Unit,
) {
    val leadGift = giftWall.featuredGifts.firstOrNull()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ProfileSectionRowHeader(
            title = "Gift Wall",
            onClick = onOpenGiftWall,
            testTag = "profile_gift_wall_header"
        )
        Surface(
            onClick = onOpenGiftWall,
            shape = RoundedCornerShape(16.dp),
            color = SecondaryBackground,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_gift_wall_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, BorderColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (leadGift != null) {
                        Text(leadGift.emoji, fontSize = 30.sp)
                    } else {
                        Icon(Icons.Rounded.CardGiftcard, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(28.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCompactCount(giftWall.totalGifts.toLong()),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText,
                        modifier = Modifier.testTag("profile_gift_wall_total_gifts")
                    )
                    Text("Gift", fontSize = 13.sp, color = SecondaryText)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCompactCount(giftWall.totalStars),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText,
                        modifier = Modifier.testTag("profile_gift_wall_total_stars")
                    )
                    Text("Star", fontSize = 13.sp, color = SecondaryText)
                }

                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

@Composable
internal fun ProfileSignatureSection(
    signature: String,
    isOwnProfile: Boolean,
    onClickEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_signature_section"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Signature",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText,
            )
            if (isOwnProfile) {
                Surface(
                    onClick = onClickEdit,
                    shape = RoundedCornerShape(50),
                    color = TurquoiseActive.copy(alpha = 0.12f),
                    modifier = Modifier.testTag("edit_signature_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.Edit, null, tint = TurquoiseActive, modifier = Modifier.size(13.dp))
                        Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TurquoiseActive)
                    }
                }
            }
        }
        Text(
            text = signature.ifBlank { "This person says nothing!" },
            fontSize = 14.sp,
            color = SecondaryText,
            modifier = Modifier.testTag("profile_signature_text")
        )
    }
}

@Composable
internal fun ProfileBffSection(
    relationships: List<AcceptedRelationship>,
    onSelectRelationship: (AcceptedRelationship) -> Unit,
    onOpenBffOverview: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_bff_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileSectionRowHeader(
            title = "BFF",
            countText = relationships.size.toString(),
            accentHeart = true,
            onClick = onOpenBffOverview,
            testTag = "profile_bff_header"
        )
        if (relationships.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SecondaryBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No accepted BFF relationships yet",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(relationships, key = { it.id }) { rel ->
                    val gradient = if (rel.tokenKey.contains("bro", ignoreCase = true) ||
                        rel.tagLabel.equals("Bro", ignoreCase = true)
                    ) {
                        listOf(Color(0xFF7DD3FC), Color(0xFF3B82F6))
                    } else {
                        listOf(Color(0xFFC4B5FD), Color(0xFF8B5CF6))
                    }
                    Surface(
                        onClick = { onSelectRelationship(rel) },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(126.dp)
                            .height(166.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(gradient))
                            .testTag("bff_card_${rel.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.28f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    RelationshipTag3DIcon(iconKey = rel.tokenKey, size = 14.dp)
                                    Text(
                                        text = "LV${rel.level}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            BffToken3DArt(tokenKey = rel.tokenKey, size = 56.dp)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                ) {
                                    AvatarCircle(
                                        initials = rel.partnerName.take(2),
                                        accentColor = Color(0xFF1E293B),
                                        size = 38.dp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${rel.tagLabel} • ${rel.partnerName}",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileGuardStrip(
    guards: List<GuardRelationship>,
    onTapGuardUser: (GuardRelationship) -> Unit,
    onOpenGuardList: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenGuardList)
            .padding(vertical = 8.dp)
            .testTag("profile_guard_section"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Guard",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryText,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (guards.isEmpty()) {
                Text("None", fontSize = 13.sp, color = SecondaryText)
            } else {
                guards.take(3).forEach { guard ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onTapGuardUser(guard) }
                            .testTag("guard_avatar_${guard.guardUid}")
                    ) {
                        AvatarCircle(
                            initials = guard.guardName.take(2),
                            accentColor = Color(guard.guardAccentHex),
                            size = 40.dp,
                            vipLevel = guard.guardLevel
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "All Guards",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun ProfileAdvancedRoomStrip(
    rooms: List<AdvancedRoomPreview>,
    onEnterRoom: (String) -> Unit,
    onOpenAllRooms: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenAllRooms)
            .padding(vertical = 8.dp)
            .testTag("profile_advanced_room_section"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Advanced Room",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryText,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (rooms.isEmpty()) {
                Text("None", fontSize = 13.sp, color = SecondaryText)
            } else {
                val preview = rooms.take(3)
                preview.forEachIndexed { idx, rm ->
                    val overflow = rooms.size - 3
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(rm.themePrimaryHex), Color(rm.themeSecondaryHex))
                                )
                            )
                            .clickable { onEnterRoom(rm.roomId) }
                            .testTag("adv_room_tile_${rm.roomId}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.GraphicEq, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(20.dp))
                        if (idx == 2 && overflow > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+$overflow", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "All Advanced Rooms",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun ProfileBottomActionBar(
    isOwnProfile: Boolean,
    onSendGift: () -> Unit,
    onChatOrEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_bottom_action_bar"),
        color = Color.White.copy(alpha = 0.97f),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF6B8B), Color(0xFFFF85A1))))
                    .clickable(onClick = onSendGift)
                    .testTag("profile_send_gift_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.CardGiftcard, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("Send Gift", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF00C2B8))))
                    .clickable(onClick = onChatOrEdit)
                    .testTag("public_profile_message_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isOwnProfile) Icons.Rounded.AutoAwesome else Icons.Rounded.ChatBubble,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                    Text(
                        text = if (isOwnProfile) "Edit Profile" else "Chat",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
internal fun SignatureEditorDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var draft by remember(initialText) { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Signature", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { if (it.length <= 120) draft = it },
                    label = { Text("Personal Signature (${draft.length}/120)") },
                    placeholder = { Text("Write a short bio or quote...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("signature_editor_input")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft) },
                modifier = Modifier.testTag("signature_save_button")
            ) {
                Text("Save", color = TurquoiseActive, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = { onSave("") },
                    modifier = Modifier.testTag("signature_clear_button")
                ) {
                    Text("Remove", color = PinkAccent)
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
internal fun RelationshipDetailDialog(
    relationship: AcceptedRelationship,
    canCancel: Boolean,
    onDismiss: () -> Unit,
    onCancelRelationship: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${relationship.tagLabel} Relationship", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Partner: ${relationship.partnerName}", fontWeight = FontWeight.SemiBold, color = PrimaryText)
                Text("Relationship Level: LV${relationship.level}", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                Text("Token: ${relationship.tokenTitle}", color = SecondaryText)
                Text("Duration: ${relationship.daysTogether} days active", color = SecondaryText)
            }
        },
        confirmButton = {
            if (canCancel) {
                OutlinedButton(
                    onClick = {
                        onCancelRelationship(relationship.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkAccent),
                    border = BorderStroke(1.dp, PinkAccent),
                    modifier = Modifier.testTag("cancel_relationship_button")
                ) {
                    Text("Cancel Relationship")
                }
            } else {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
        dismissButton = {
            if (canCancel) {
                TextButton(onClick = onDismiss) { Text("Keep") }
            }
        }
    )
}
