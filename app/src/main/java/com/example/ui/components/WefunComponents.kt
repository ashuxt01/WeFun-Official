package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RoomRole
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WefunPurpleGradientColors

/**
 * Official WEFUN brand logo rendered directly from the supplied Husky mascot emblem
 * without redrawing or distorting aspect ratio.
 */
@Composable
fun WefunBrandLogo(
    size: Dp = 64.dp,
    cornerRadius: Dp = 18.dp,
    showGoldBorder: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Image(
        painter = painterResource(id = R.drawable.wefun_brand_logo),
        contentDescription = "WEFUN Official Logo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(shape)
            .then(
                if (showGoldBorder) {
                    Modifier.border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(PremiumGold, PrimaryPurple, PinkAccent)),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
    )
}

/**
 * Premium Light Theme primary card container with crisp border and soft elevation shadow.
 */
@Composable
fun WefunLightCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        borderColor = borderColor,
        cornerRadius = cornerRadius,
        content = content,
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    val shape = RoundedCornerShape(cornerRadius)
    val shadowMod = Modifier.shadow(
        elevation = 5.dp,
        shape = shape,
        ambientColor = Color(0xFF1A2233).copy(alpha = 0.08f),
        spotColor = Color(0xFF1A2233).copy(alpha = 0.10f)
    )
    val baseModifier = modifier
        .then(shadowMod)
        .clip(shape)
        .background(
            if (!wefunColors.isDark) {
                Brush.verticalGradient(
                    colors = listOf(
                        wefunColors.glassSurface,
                        Color(0xFFFCFDFE)
                    )
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        wefunColors.glassSurface,
                        wefunColors.elevatedCard.copy(alpha = 0.72f),
                    )
                )
            }
        )
        .border(
            BorderStroke(
                width = 1.dp,
                brush = if (borderColor != null) {
                    Brush.linearGradient(listOf(borderColor, borderColor))
                } else {
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), wefunColors.glassBorder))
                }
            ),
            shape = shape
        )
    val finalModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }
    Box(modifier = finalModifier, content = content)
}

@Composable
fun CopyableRoomIdChip(
    roomId: String,
    categoryLabel: String? = null,
    onCopied: (String) -> Unit,
    darkSurface: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    val textColor = if (darkSurface) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (darkSurface) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    val border = if (darkSurface) Color.White.copy(alpha = 0.2f) else BorderColor

    Surface(
        onClick = {
            clipboard.setText(AnnotatedString(roomId))
            onCopied(roomId)
        },
        modifier = modifier.testTag("copy_room_id_button"),
        shape = RoundedCornerShape(50),
        color = bgColor,
        border = BorderStroke(0.8.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (!categoryLabel.isNullOrBlank()) "$categoryLabel ID: $roomId" else "ID: $roomId",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = "Copy Room ID",
                tint = textColor,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
fun RoleBadgePill(
    role: RoomRole,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val (bg, fg, label) = when (role) {
        RoomRole.OWNER -> Triple(PremiumGold, Color(0xFF20202A), "Owner")
        RoomRole.PARTNER -> Triple(PrimaryPurple, PremiumGold, "Co-Owner")
        RoomRole.SUPER_ADMIN -> Triple(DeepPurple, Color.White, "Super Admin")
        RoomRole.ROOM_ADMIN -> Triple(Color(0xFF0EA5E9), Color.White, "Admin")
        RoomRole.MODERATOR -> Triple(SuccessGreen, Color.White, "Mod")
        RoomRole.VIP_MEMBER -> Triple(PinkAccent, Color.White, "VIP")
        RoomRole.NORMAL_MEMBER -> Triple(Color(0xFF64748B), Color.White, "Member")
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontSize = if (compact) 8.sp else 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = if (compact) 5.dp else 8.dp,
                vertical = if (compact) 1.dp else 2.dp
            )
        )
    }
}

@Composable
fun PopularityPill(
    popularity: Long,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = WarningAmber.copy(alpha = 0.2f),
        border = BorderStroke(0.8.dp, WarningAmber.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = "Popularity",
                tint = WarningAmber,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = formatCompactCount(popularity),
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun WefunGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "",
    colors: List<Color> = WefunPurpleGradientColors,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val tagMod = if (testTag.isNotEmpty()) modifier.testTag(testTag) else modifier
    val btnShape = RoundedCornerShape(16.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = tagMod
            .height(50.dp)
            .then(
                if (enabled) Modifier.shadow(
                    elevation = 6.dp,
                    shape = btnShape,
                    ambientColor = colors.first().copy(alpha = 0.35f),
                    spotColor = colors.last().copy(alpha = 0.35f)
                ) else Modifier
            ),
        shape = btnShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        if (enabled) colors else listOf(Color.Gray.copy(0.4f), Color.Gray.copy(0.4f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(RoyalGold.copy(alpha = if (enabled) 0.45f else 0.1f), PurplePrimary.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun VipBadge(vipLevel: Int, modifier: Modifier = Modifier) {
    if (vipLevel <= 0) return
    val clamped = vipLevel.coerceIn(1, 12)
    val label = if (clamped >= 11) "SVIP $clamped" else "VIP $clamped"
    val gradient = when {
        clamped >= 11 -> listOf(RoyalGold, RoseNeon, ElectricViolet)
        clamped >= 9 -> listOf(Color(0xFFEF4444), RoyalGold)
        clamped >= 7 -> listOf(ElectricViolet, PinkAccent)
        clamped >= 5 -> listOf(RoyalGold, Color(0xFFF59E0B))
        clamped >= 3 -> listOf(CyanPulse, Color(0xFF0284C7))
        else -> listOf(Color(0xFF60A5FA), Color(0xFF2563EB))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(gradient))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun LevelBadge(level: Int, modifier: Modifier = Modifier) {
    val wefunColors = LocalWefunColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, wefunColors.cyan.copy(alpha = 0.45f))
    ) {
        Text(
            text = "Lv.$level",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun CurrencyPill(
    coins: Long,
    diamonds: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val wefunColors = LocalWefunColors.current
    Surface(
        onClick = onClick,
        modifier = modifier.testTag("wallet_pill_button"),
        shape = RoundedCornerShape(50),
        color = wefunColors.glassSurface,
        border = BorderStroke(1.dp, wefunColors.gold.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Wefun3DCoinIcon(size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCompactCount(coins),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                WefunDiamond3DIcon(size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCompactCount(diamonds),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AvatarCircle(
    initials: String,
    accentColor: Color,
    size: Dp = 48.dp,
    vipLevel: Int = 0,
    isSpeaking: Boolean = false,
    isVerified: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "voicePulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .border(2.5.dp, CyanPulse, CircleShape)
            )
        }
        val frameBrush = when {
            vipLevel >= 6 -> Brush.linearGradient(listOf(RoyalGold, RoseNeon, ElectricViolet))
            vipLevel >= 3 -> Brush.linearGradient(listOf(RoyalGold, WarmAmberCompat))
            vipLevel >= 1 -> Brush.linearGradient(listOf(ElectricViolet, CyanPulse))
            else -> Brush.linearGradient(listOf(accentColor.copy(alpha = 0.85f), DeepViolet))
        }
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.9f), accentColor.copy(alpha = 0.55f))
                    )
                )
                .border(2.dp, frameBrush, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials.take(2).uppercase(),
                style = if (size <= 36.dp) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        if (isVerified) {
            Icon(
                imageVector = Icons.Rounded.Verified,
                contentDescription = "Verified Host",
                tint = CyanPulse,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

private val WarmAmberCompat = Color(0xFFF59E0B)

@Composable
fun LiveVoiceBars(
    isActive: Boolean,
    rmsLevel: Float = 0.5f,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "eqBars")
    val bar1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isActive) (0.55f + rmsLevel * 0.45f).coerceIn(0.3f, 1f) else 0.2f,
        animationSpec = infiniteRepeatable(tween(260), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isActive) (0.7f + rmsLevel * 0.3f).coerceIn(0.4f, 1f) else 0.2f,
        animationSpec = infiniteRepeatable(tween(340), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isActive) (0.5f + rmsLevel * 0.5f).coerceIn(0.3f, 1f) else 0.2f,
        animationSpec = infiniteRepeatable(tween(290), RepeatMode.Reverse),
        label = "b3"
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(bar1, bar2, bar3).forEach { heightFraction ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((14 * heightFraction).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) CyanPulse else Color.Gray)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalWefunColors.current.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (actionLabel != null && onAction != null) {
            Surface(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RtcStatusChip(
    label: String,
    isLiveAudioActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (isLiveAudioActive) SuccessGreen else PremiumGold
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.28f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun formatCompactCount(value: Long): String = when {
    value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
    value >= 10_000 -> String.format("%.1fK", value / 1_000.0)
    else -> value.toString()
}
