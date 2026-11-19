package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VipNicknameColorTier
import com.example.data.repository.VipGrowthEngine

fun vipRainbowBrush(): Brush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF3366),
        Color(0xFFFFB703),
        Color(0xFF00E5FF),
        Color(0xFFA855F7),
        Color(0xFFFF4D9D),
    )
)

@Composable
fun VipNicknameText(
    name: String,
    vipLevel: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    fontWeight: FontWeight = FontWeight.Bold,
    maxLines: Int = 1,
    onDarkBackground: Boolean = false,
    customTitle: String = "",
) {
    val tier = VipGrowthEngine.nicknameColorTierForLevel(vipLevel)
    val resolvedStyle = when (tier) {
        VipNicknameColorTier.RED -> style.copy(color = Color(0xFFFF2E4D), fontWeight = fontWeight)
        VipNicknameColorTier.NOBLE_GOLD -> style.copy(
            color = if (onDarkBackground) Color(0xFFFFD700) else Color(0xFFD97706),
            fontWeight = fontWeight
        )
        VipNicknameColorTier.RAINBOW -> style.copy(brush = vipRainbowBrush(), fontWeight = FontWeight.ExtraBold)
        VipNicknameColorTier.NONE -> style.copy(
            color = if (onDarkBackground) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = fontWeight
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = name,
            style = resolvedStyle,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
        if (customTitle.isNotBlank() && vipLevel >= 8) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFA855F7).copy(alpha = 0.22f),
                border = BorderStroke(0.8.dp, Color(0xFFFFD700).copy(alpha = 0.7f))
            ) {
                Text(
                    text = customTitle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (onDarkBackground) Color(0xFFFFD700) else Color(0xFF7E22CE),
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun VipLevelBadge3D(
    vipLevel: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    if (vipLevel <= 0) return
    val clamped = vipLevel.coerceIn(1, 12)
    val (colors, borderCol) = when {
        clamped >= 11 -> listOf(Color(0xFFFFB703), Color(0xFFDC2626), Color(0xFF9333EA)) to Color(0xFFFFF3B0)
        clamped >= 9 -> listOf(Color(0xFFEF4444), Color(0xFF991B1B)) to Color(0xFFFFD700)
        clamped >= 7 -> listOf(Color(0xFFA855F7), Color(0xFF6D28D9)) to Color(0xFFE9D5FF)
        clamped >= 5 -> listOf(Color(0xFFFFD700), Color(0xFFD97706)) to Color(0xFFFFF7CC)
        clamped >= 3 -> listOf(Color(0xFF22D3EE), Color(0xFF0284C7)) to Color(0xFFCFFAFE)
        else -> listOf(Color(0xFF60A5FA), Color(0xFF2563EB)) to Color(0xFFDBEAFE)
    }

    val badgeHeight = if (compact) 20.dp else 26.dp
    val gemSize = if (compact) 14.dp else 18.dp
    val fontSize = if (compact) 9.sp else 11.sp
    val labelText = if (clamped >= 11) "SVIP $clamped" else "VIP $clamped"

    Surface(
        modifier = modifier
            .height(badgeHeight)
            .shadow(if (compact) 2.dp else 4.dp, RoundedCornerShape(50), ambientColor = colors.first()),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderCol.copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors))
                .padding(horizontal = if (compact) 6.dp else 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Canvas(modifier = Modifier.size(gemSize)) {
                val w = size.width
                val h = size.height
                when {
                    clamped >= 11 -> {
                        // Supreme Ruby Crown + Laurel Wings
                        val crown = Path().apply {
                            moveTo(w * 0.14f, h * 0.78f)
                            lineTo(w * 0.14f, h * 0.34f)
                            lineTo(w * 0.34f, h * 0.54f)
                            lineTo(w * 0.50f, h * 0.16f)
                            lineTo(w * 0.66f, h * 0.54f)
                            lineTo(w * 0.86f, h * 0.34f)
                            lineTo(w * 0.86f, h * 0.78f)
                            close()
                        }
                        drawPath(crown, Color(0xFFFFF8DC))
                        drawCircle(Color(0xFFFF2E4D), w * 0.12f, Offset(w * 0.50f, h * 0.58f))
                    }
                    clamped >= 9 -> {
                        // Crimson Royal Crest
                        val crest = Path().apply {
                            moveTo(w * 0.50f, h * 0.10f)
                            lineTo(w * 0.88f, h * 0.40f)
                            lineTo(w * 0.72f, h * 0.88f)
                            lineTo(w * 0.28f, h * 0.88f)
                            lineTo(w * 0.12f, h * 0.40f)
                            close()
                        }
                        drawPath(crest, Color(0xFFFFD700))
                        drawCircle(Color.White, w * 0.14f, Offset(w * 0.50f, h * 0.48f))
                    }
                    clamped >= 7 -> {
                        // Amethyst Diamond Gem
                        val gem = Path().apply {
                            moveTo(w * 0.50f, h * 0.10f)
                            lineTo(w * 0.86f, h * 0.45f)
                            lineTo(w * 0.50f, h * 0.90f)
                            lineTo(w * 0.14f, h * 0.45f)
                            close()
                        }
                        drawPath(gem, Color.White)
                        drawPath(gem, Color(0xFFE9D5FF), style = Stroke(width = 1.5f))
                    }
                    clamped >= 5 -> {
                        // Gold Wings Shield
                        val leftWing = Path().apply {
                            moveTo(w * 0.46f, h * 0.50f)
                            lineTo(w * 0.08f, h * 0.24f)
                            lineTo(w * 0.18f, h * 0.72f)
                            close()
                        }
                        val rightWing = Path().apply {
                            moveTo(w * 0.54f, h * 0.50f)
                            lineTo(w * 0.92f, h * 0.24f)
                            lineTo(w * 0.82f, h * 0.72f)
                            close()
                        }
                        drawPath(leftWing, Color.White)
                        drawPath(rightWing, Color.White)
                        drawCircle(Color(0xFFFFF59D), w * 0.18f, Offset(w * 0.50f, h * 0.50f))
                    }
                    clamped >= 3 -> {
                        // Cyan Shield
                        val shield = Path().apply {
                            moveTo(w * 0.50f, h * 0.12f)
                            lineTo(w * 0.84f, h * 0.28f)
                            lineTo(w * 0.76f, h * 0.72f)
                            lineTo(w * 0.50f, h * 0.90f)
                            lineTo(w * 0.24f, h * 0.72f)
                            lineTo(w * 0.16f, h * 0.28f)
                            close()
                        }
                        drawPath(shield, Color.White)
                    }
                    else -> {
                        // Azure Crystal
                        val crystal = Path().apply {
                            moveTo(w * 0.50f, h * 0.12f)
                            lineTo(w * 0.82f, h * 0.50f)
                            lineTo(w * 0.50f, h * 0.88f)
                            lineTo(w * 0.18f, h * 0.50f)
                            close()
                        }
                        drawPath(crystal, Color.White)
                    }
                }
            }
            Text(
                text = labelText,
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = if (clamped in 5..6) Color(0xFF2B1700) else Color.White,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun VipEntranceBannerOverlay(
    visible: Boolean,
    vipLevel: Int,
    nickname: String,
    effectTitle: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible && vipLevel > 0,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val gradientColors = when {
            vipLevel >= 11 -> listOf(Color(0xFF4C0519), Color(0xFFB45309), Color(0xFF6B21A8))
            vipLevel >= 7 -> listOf(Color(0xFF3B0764), Color(0xFF7E22CE), Color(0xFF0E7490))
            vipLevel >= 5 -> listOf(Color(0xFF451A03), Color(0xFFD97706), Color(0xFF9333EA))
            else -> listOf(Color(0xFF0C4A6E), Color(0xFF0284C7), Color(0xFF1E1B4B))
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .testTag("vip_entrance_banner_overlay")
        ) {
            Row(
                modifier = Modifier
                    .background(Brush.horizontalGradient(gradientColors))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VipLevelBadge3D(vipLevel = vipLevel, compact = true)
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    VipNicknameText(
                        name = "$nickname entered the room",
                        vipLevel = vipLevel,
                        style = MaterialTheme.typography.labelLarge,
                        onDarkBackground = true,
                    )
                    Text(
                        text = effectTitle,
                        fontSize = 10.sp,
                        color = Color(0xFFFDE68A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun VipEntrancePreviewCard(
    vipLevel: Int,
    nickname: String,
    effectTitle: String,
    isPlaying: Boolean,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "vipEntranceShimmer")
    val drift by transition.animateFloat(
        initialValue = -16f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerDrift"
    )

    val stageColors = when {
        vipLevel >= 11 -> listOf(Color(0xFF2D0A31), Color(0xFF581C87), Color(0xFF991B1B))
        vipLevel >= 7 -> listOf(Color(0xFF1E1038), Color(0xFF6B21A8), Color(0xFF0E7490))
        vipLevel >= 4 -> listOf(Color(0xFF16133B), Color(0xFF0284C7), Color(0xFFB45309))
        else -> listOf(Color(0xFF121836), Color(0xFF1D4ED8), Color(0xFF0891B2))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(stageColors))
            .border(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("vip_entrance_preview_card")
    ) {
        // Floating glowing aura orbs inside stage
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(x = drift.dp, y = (-8).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color(0xFFFFD700).copy(alpha = if (isPlaying) 0.28f else 0.12f))
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = effectTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFF8DC)
                    )
                    Text(
                        text = if (isPlaying) "Playing Live Room Stage Preview..." else "VIP $vipLevel Stage Entrance Effect",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
                Surface(
                    onClick = onReplay,
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFFD700),
                    modifier = Modifier.testTag("vip_replay_entrance_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color(0xFF1B102E), modifier = Modifier.size(16.dp))
                        Text("Play", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B102E))
                    }
                }
            }

            VipEntranceBannerOverlay(
                visible = true,
                vipLevel = vipLevel.coerceAtLeast(1),
                nickname = nickname,
                effectTitle = effectTitle,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
