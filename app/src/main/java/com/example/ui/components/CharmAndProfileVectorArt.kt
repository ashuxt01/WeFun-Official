package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfileBadge
import com.example.data.model.ProfileBadgeType
import com.example.data.repository.CharmLevelCatalog
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GenderVectorBadge(
    gender: String,
    modifier: Modifier = Modifier,
) {
    val g = gender.lowercase()
    val isMale = g.contains("male") && !g.contains("female")
    val isFemale = g.contains("female")
    val tint = when {
        isMale -> Color(0xFF3B82F6)
        isFemale -> Color(0xFFFF4F8B)
        else -> Color(0xFF8B5CF6)
    }
    val icon = when {
        isMale -> Icons.Rounded.Male
        isFemale -> Icons.Rounded.Female
        else -> Icons.Rounded.Transgender
    }
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Gender: $gender",
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun UserLevelPill(
    level: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFF6E3A)
    ) {
        Text(
            text = "Lv.$level",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun CharmLevelPill(
    charmPoints: Long,
    charmLevel: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val tier = CharmLevelCatalog.charmLevels.firstOrNull { it.level == charmLevel }
    val bg = when {
        charmLevel >= 16 -> Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFD97706)))
        charmLevel >= 9 -> Brush.horizontalGradient(listOf(Color(0xFF7E22CE), Color(0xFFF59E0B)))
        charmLevel >= 4 -> Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF06B6D4)))
        charmLevel >= 1 -> Brush.horizontalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
        else -> Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)))
    }
    val label = if (charmLevel > 0) "Lv.$charmLevel • ${formatCompactCount(charmPoints)}" else formatCompactCount(charmPoints)
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bg)
                .padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (charmLevel > 0) {
                CharmLevel3DBadge(level = charmLevel, size = 13.dp)
            }
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            if (tier != null && charmLevel >= 1) {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(6.dp),
            color = Color.Transparent,
            modifier = modifier
        ) {
            content()
        }
    } else {
        Box(modifier = modifier) { content() }
    }
}

@Composable
fun CountryFlagChip(
    country: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CountryFlagVector(country = country, modifier = Modifier.size(width = 22.dp, height = 15.dp))
        Text(
            text = country,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4B5563),
            maxLines = 1,
        )
    }
}

@Composable
fun CountryFlagVector(
    country: String,
    modifier: Modifier = Modifier,
) {
    val c = country.trim().lowercase()
    if (c == "global" || c.isBlank()) {
        Icon(
            imageVector = Icons.Rounded.Public,
            contentDescription = country,
            tint = Color(0xFF00C2B8),
            modifier = modifier
        )
        return
    }
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(3.dp))
    ) {
        val w = size.width
        val h = size.height
        when {
            c.contains("india") -> {
                drawRect(Color(0xFFFF9933), Offset(0f, 0f), Size(w, h / 3f))
                drawRect(Color.White, Offset(0f, h / 3f), Size(w, h / 3f))
                drawRect(Color(0xFF138808), Offset(0f, 2f * h / 3f), Size(w, h / 3f))
                drawCircle(Color(0xFF000080), radius = h * 0.13f, center = Offset(w / 2f, h / 2f))
            }
            c.contains("uae") -> {
                drawRect(Color(0xFF00732F), Offset(w * 0.25f, 0f), Size(w * 0.75f, h / 3f))
                drawRect(Color.White, Offset(w * 0.25f, h / 3f), Size(w * 0.75f, h / 3f))
                drawRect(Color.Black, Offset(w * 0.25f, 2f * h / 3f), Size(w * 0.75f, h / 3f))
                drawRect(Color(0xFFFF0000), Offset(0f, 0f), Size(w * 0.25f, h))
            }
            c.contains("uk") || c.contains("united kingdom") -> {
                drawRect(Color(0xFF012169), Offset.Zero, size)
                drawLine(Color.White, Offset(0f, 0f), Offset(w, h), strokeWidth = h * 0.22f)
                drawLine(Color.White, Offset(w, 0f), Offset(0f, h), strokeWidth = h * 0.22f)
                drawRect(Color.White, Offset(w * 0.42f, 0f), Size(w * 0.16f, h))
                drawRect(Color.White, Offset(0f, h * 0.36f), Size(w, h * 0.28f))
                drawRect(Color(0xFFC8102E), Offset(w * 0.45f, 0f), Size(w * 0.10f, h))
                drawRect(Color(0xFFC8102E), Offset(0f, h * 0.41f), Size(w, h * 0.18f))
            }
            c.contains("singapore") -> {
                drawRect(Color(0xFFEF3340), Offset(0f, 0f), Size(w, h / 2f))
                drawRect(Color.White, Offset(0f, h / 2f), Size(w, h / 2f))
                drawCircle(Color.White, radius = h * 0.15f, center = Offset(w * 0.22f, h * 0.25f))
            }
            else -> {
                drawRect(Color(0xFF00C2B8), Offset(0f, 0f), Size(w, h / 2f))
                drawRect(Color(0xFF3B82F6), Offset(0f, h / 2f), Size(w, h / 2f))
                drawCircle(Color.White, radius = h * 0.16f, center = Offset(w / 2f, h / 2f))
            }
        }
    }
}

@Composable
fun Profile3DOctagonBadge(
    badge: ProfileBadge,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val (topColor, bottomColor, rimColor) = when (badge.badgeType) {
        ProfileBadgeType.HOST_VERIFIED -> Triple(Color(0xFFE58A63), Color(0xFF9C3D1F), Color(0xFFFFC19E))
        ProfileBadgeType.TOP_CHARM -> Triple(Color(0xFFA855F7), Color(0xFF5B21B6), Color(0xFFE9D5FF))
        ProfileBadgeType.SOCIAL_STAR -> Triple(Color(0xFF38BDF8), Color(0xFF0369A1), Color(0xFFBAE6FD))
        ProfileBadgeType.ROOM_OWNER -> Triple(Color(0xFFFBBF24), Color(0xFFB45309), Color(0xFFFEF08A))
    }
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val hex = Path().apply {
            moveTo(w * 0.50f, h * 0.08f)
            lineTo(w * 0.86f, h * 0.28f)
            lineTo(w * 0.86f, h * 0.72f)
            lineTo(w * 0.50f, h * 0.92f)
            lineTo(w * 0.14f, h * 0.72f)
            lineTo(w * 0.14f, h * 0.28f)
            close()
        }
        drawPath(hex, Brush.verticalGradient(listOf(rimColor, bottomColor)))
        val innerHex = Path().apply {
            moveTo(w * 0.50f, h * 0.17f)
            lineTo(w * 0.78f, h * 0.33f)
            lineTo(w * 0.78f, h * 0.67f)
            lineTo(w * 0.50f, h * 0.83f)
            lineTo(w * 0.22f, h * 0.67f)
            lineTo(w * 0.22f, h * 0.33f)
            close()
        }
        drawPath(innerHex, Brush.verticalGradient(listOf(topColor, bottomColor)))

        if (badge.badgeType == ProfileBadgeType.HOST_VERIFIED) {
            // Mic silhouette inside badge
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.42f, h * 0.32f),
                size = Size(w * 0.16f, h * 0.24f),
                cornerRadius = CornerRadius(w * 0.08f)
            )
            drawLine(Color.White, Offset(w * 0.50f, h * 0.58f), Offset(w * 0.50f, h * 0.68f), strokeWidth = w * 0.06f)
        } else {
            // Star crest inside shield
            drawCircle(Color.White.copy(alpha = 0.95f), w * 0.14f, Offset(w * 0.50f, h * 0.50f))
        }
    }
}

@Composable
fun BffToken3DArt(
    tokenKey: String,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when {
            tokenKey.contains("bro", ignoreCase = true) || tokenKey.contains("bike", ignoreCase = true) -> {
                // Sleek electric bike silhouette
                drawCircle(Color(0xFF1E293B), w * 0.14f, Offset(w * 0.26f, h * 0.68f), style = Stroke(width = w * 0.06f))
                drawCircle(Color(0xFF1E293B), w * 0.14f, Offset(w * 0.74f, h * 0.68f), style = Stroke(width = w * 0.06f))
                val frame = Path().apply {
                    moveTo(w * 0.26f, h * 0.68f)
                    lineTo(w * 0.46f, h * 0.40f)
                    lineTo(w * 0.66f, h * 0.40f)
                    lineTo(w * 0.74f, h * 0.68f)
                    moveTo(w * 0.46f, h * 0.40f)
                    lineTo(w * 0.52f, h * 0.64f)
                    lineTo(w * 0.26f, h * 0.68f)
                }
                drawPath(frame, Brush.linearGradient(listOf(Color.White, Color(0xFF93C5FD))), style = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            else -> {
                // Glossy 3D Paper Plane / Starlight Crest
                val plane = Path().apply {
                    moveTo(w * 0.14f, h * 0.48f)
                    lineTo(w * 0.86f, h * 0.20f)
                    lineTo(w * 0.64f, h * 0.80f)
                    lineTo(w * 0.46f, h * 0.56f)
                    close()
                }
                drawPath(plane, Brush.linearGradient(listOf(Color.White, Color(0xFFCBD5E1))))
                val fold = Path().apply {
                    moveTo(w * 0.86f, h * 0.20f)
                    lineTo(w * 0.46f, h * 0.56f)
                    lineTo(w * 0.48f, h * 0.74f)
                    close()
                }
                drawPath(fold, Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun CharmNumberCrestPill(
    level: Int,
    modifier: Modifier = Modifier,
) {
    val tier = CharmLevelCatalog.charmLevels.firstOrNull { it.level == level }
    val primary = Color(tier?.accentHex ?: 0xFFFBBF24)
    val secondary = Color(tier?.secondaryHex ?: 0xFF9333EA)
    val isTop21 = level == 21
    Box(
        modifier = modifier
            .size(if (isTop21) 46.dp else 34.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(secondary.copy(alpha = 0.55f), Color(0xFF140724))
                )
            )
            .border(
                width = if (isTop21) 2.2.dp else 1.4.dp,
                brush = Brush.linearGradient(listOf(Color(0xFFFDE047), primary)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.toString(),
            color = Color.White,
            fontSize = if (isTop21) 18.sp else 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/**
 * Custom faceted 3D crystal / crown artwork distinctly sculpted for all 21 Charm levels.
 */
@Composable
fun CharmLevel3DBadge(
    level: Int,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    val tier = CharmLevelCatalog.charmLevels.firstOrNull { it.level == level }
    val c1 = Color(tier?.glowHex ?: 0xFFE2E8F0)
    val c2 = Color(tier?.accentHex ?: 0xFFCBD5E1)
    val c3 = Color(tier?.secondaryHex ?: 0xFF64748B)

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.5f
        val cy = h * 0.5f

        when {
            level <= 3 -> {
                // Multi-point faceted silver crystal star (Levels 1-3)
                val spikes = 6 + (level - 1) * 2
                val star = Path()
                val outerR = w * 0.44f
                val innerR = w * 0.19f
                for (i in 0 until spikes * 2) {
                    val r = if (i % 2 == 0) outerR else innerR
                    val angle = (Math.PI * i / spikes) - Math.PI / 2.0
                    val px = cx + (cos(angle) * r).toFloat()
                    val py = cy + (sin(angle) * r).toFloat()
                    if (i == 0) star.moveTo(px, py) else star.lineTo(px, py)
                }
                star.close()
                drawPath(star, Brush.linearGradient(listOf(c1, c2, c3)))
                drawCircle(Color.White.copy(alpha = 0.85f), w * 0.11f, Offset(cx, cy))
            }
            level in 4..6 -> {
                // Sapphire diamond / cyan crown (Levels 4-6)
                val gem = Path().apply {
                    moveTo(cx, h * 0.10f)
                    lineTo(w * 0.88f, h * 0.36f)
                    lineTo(cx, h * 0.90f)
                    lineTo(w * 0.12f, h * 0.36f)
                    close()
                }
                drawPath(gem, Brush.verticalGradient(listOf(c1, c2, c3)))
                val facet = Path().apply {
                    moveTo(cx, h * 0.10f)
                    lineTo(w * 0.68f, h * 0.36f)
                    lineTo(cx, h * 0.70f)
                    lineTo(w * 0.32f, h * 0.36f)
                    close()
                }
                drawPath(facet, Color.White.copy(alpha = 0.36f))
                if (level == 6) {
                    drawCircle(Color(0xFFFEF08A), w * 0.08f, Offset(cx, h * 0.22f))
                }
            }
            level in 7..10 -> {
                // Amethyst Star / Prestige Gold Emblem (Levels 7-10)
                val shield = Path().apply {
                    moveTo(cx, h * 0.08f)
                    lineTo(w * 0.88f, h * 0.30f)
                    lineTo(w * 0.78f, h * 0.74f)
                    lineTo(cx, h * 0.92f)
                    lineTo(w * 0.22f, h * 0.74f)
                    lineTo(w * 0.12f, h * 0.30f)
                    close()
                }
                drawPath(shield, Brush.linearGradient(listOf(c1, c2, c3)))
                drawCircle(Color.White.copy(alpha = 0.30f), w * 0.20f, Offset(cx, cy))
                drawCircle(Color(0xFFFFFBEB), w * 0.09f, Offset(cx, cy * 0.9f))
            }
            level in 11..15 -> {
                // Winged Ruby / Mythic Crest (Levels 11-15)
                val wingL = Path().apply {
                    moveTo(cx, h * 0.55f)
                    lineTo(w * 0.06f, h * 0.28f)
                    lineTo(w * 0.22f, h * 0.70f)
                    close()
                }
                val wingR = Path().apply {
                    moveTo(cx, h * 0.55f)
                    lineTo(w * 0.94f, h * 0.28f)
                    lineTo(w * 0.78f, h * 0.70f)
                    close()
                }
                drawPath(wingL, Brush.linearGradient(listOf(Color(0xFFFDE047), c2)))
                drawPath(wingR, Brush.linearGradient(listOf(Color(0xFFFDE047), c2)))
                val oct = Path().apply {
                    moveTo(cx, h * 0.14f)
                    lineTo(w * 0.78f, h * 0.34f)
                    lineTo(w * 0.72f, h * 0.76f)
                    lineTo(cx, h * 0.90f)
                    lineTo(w * 0.28f, h * 0.76f)
                    lineTo(w * 0.22f, h * 0.34f)
                    close()
                }
                drawPath(oct, Brush.verticalGradient(listOf(c1, c2, c3)))
                drawCircle(Color.White.copy(alpha = 0.7f), w * 0.11f, Offset(cx, cy))
            }
            else -> {
                // Imperial & Celestial Royal Crowns (Levels 16-21)
                val crown = Path().apply {
                    moveTo(w * 0.14f, h * 0.78f)
                    lineTo(w * 0.08f, h * 0.30f)
                    lineTo(w * 0.32f, h * 0.48f)
                    lineTo(cx, h * 0.12f)
                    lineTo(w * 0.68f, h * 0.48f)
                    lineTo(w * 0.92f, h * 0.30f)
                    lineTo(w * 0.86f, h * 0.78f)
                    close()
                }
                drawPath(crown, Brush.verticalGradient(listOf(Color(0xFFFFF3A1), c2, c3)))
                drawPath(crown, Color(0xFFFEF08A), style = Stroke(width = w * 0.045f, join = StrokeJoin.Round))
                // Crown jewels
                drawCircle(if (level >= 19) Color(0xFFE879F9) else Color(0xFFEF4444), w * 0.11f, Offset(cx, h * 0.56f))
                drawCircle(Color(0xFFFEF9C3), w * 0.06f, Offset(w * 0.28f, h * 0.62f))
                drawCircle(Color(0xFFFEF9C3), w * 0.06f, Offset(w * 0.72f, h * 0.62f))
                if (level == 21) {
                    // Ultimate luminous side wings
                    val wL = Path().apply {
                        moveTo(w * 0.16f, h * 0.64f)
                        lineTo(w * 0.02f, h * 0.18f)
                        lineTo(w * 0.24f, h * 0.42f)
                        close()
                    }
                    val wR = Path().apply {
                        moveTo(w * 0.84f, h * 0.64f)
                        lineTo(w * 0.98f, h * 0.18f)
                        lineTo(w * 0.76f, h * 0.42f)
                        close()
                    }
                    drawPath(wL, Color(0xFFF0ABFC))
                    drawPath(wR, Color(0xFFF0ABFC))
                }
            }
        }
    }
}

@Composable
fun CharmLevelHaloRingPreview(
    level: Int,
    modifier: Modifier = Modifier,
) {
    val tier = CharmLevelCatalog.charmLevels.firstOrNull { it.level == level }
    val glow = Color(tier?.glowHex ?: 0xFFE2E8F0)
    val accent = Color(tier?.accentHex ?: 0xFF94A3B8)

    Canvas(modifier = modifier.size(width = 64.dp, height = 28.dp)) {
        val w = size.width
        val h = size.height
        // Outer luminous ellipse ring
        drawOval(
            brush = Brush.horizontalGradient(listOf(accent.copy(alpha = 0.2f), glow, accent.copy(alpha = 0.2f))),
            topLeft = Offset(w * 0.06f, h * 0.42f),
            size = Size(w * 0.88f, h * 0.48f),
            style = Stroke(width = 2.4.dp.toPx())
        )
        // Inner secondary shimmer ellipse
        drawOval(
            color = glow.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.16f, h * 0.50f),
            size = Size(w * 0.68f, h * 0.32f),
            style = Stroke(width = 1.2.dp.toPx())
        )
        // Crown spires emerging for higher levels
        val spires = when {
            level >= 16 -> 5
            level >= 7 -> 3
            else -> 1
        }
        val midX = w * 0.5f
        for (i in 0 until spires) {
            val offsetFactor = if (spires == 1) 0f else (i - (spires - 1) / 2f) * 0.14f
            val x = midX + w * offsetFactor
            val spireHeight = if (i == spires / 2) h * 0.44f else h * 0.28f
            drawLine(
                brush = Brush.verticalGradient(listOf(Color.White, glow)),
                start = Offset(x, h * 0.56f),
                end = Offset(x, h * 0.56f - spireHeight),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color.White,
                radius = 1.6.dp.toPx(),
                center = Offset(x, h * 0.56f - spireHeight)
            )
        }
    }
}
