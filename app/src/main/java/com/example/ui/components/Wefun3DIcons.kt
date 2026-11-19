package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscoverCategory
import com.example.data.model.GameId
import com.example.data.model.StartingToken
import com.example.data.model.VirtualGift
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TurquoiseActive
import com.example.ui.theme.TurquoiseCyan
import com.example.ui.theme.TurquoiseDeep

/**
 * Custom stylized WeFun brand mark featuring a glossy 3D turquoise emblem and exact "WeFun"
 * typography (W capital, e small, F capital, u small, n small).
 */
@Composable
fun WefunStylizedBrandMark(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    showWordmark: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .shadow(4.dp, RoundedCornerShape(11.dp), ambientColor = TurquoiseActive.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.linearGradient(
                        listOf(TurquoiseCyan, TurquoiseActive, BlueAccent)
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(iconSize * 0.68f)) {
                val w = size.width
                val h = size.height
                // Soft glossy top sheen
                drawOval(
                    color = Color.White.copy(alpha = 0.28f),
                    topLeft = Offset(w * 0.08f, -h * 0.1f),
                    size = Size(w * 0.84f, h * 0.42f)
                )
                // Joyful stylized "W" + acoustic wave bars
                val path = Path().apply {
                    moveTo(w * 0.12f, h * 0.30f)
                    lineTo(w * 0.31f, h * 0.74f)
                    lineTo(w * 0.50f, h * 0.40f)
                    lineTo(w * 0.69f, h * 0.74f)
                    lineTo(w * 0.88f, h * 0.30f)
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Accent golden spark dot
                drawCircle(
                    color = PremiumGold,
                    radius = w * 0.09f,
                    center = Offset(w * 0.50f, h * 0.18f)
                )
            }
        }

        if (showWordmark) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = PrimaryText, fontWeight = FontWeight.ExtraBold)) {
                        append("W")
                    }
                    withStyle(SpanStyle(color = PrimaryText, fontWeight = FontWeight.Bold)) {
                        append("e")
                    }
                    withStyle(SpanStyle(color = TurquoiseActive, fontWeight = FontWeight.ExtraBold)) {
                        append("F")
                    }
                    withStyle(SpanStyle(color = TurquoiseActive, fontWeight = FontWeight.Bold)) {
                        append("un")
                    }
                },
                fontSize = 21.sp,
                letterSpacing = (-0.4).sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WefunQuickAction3DIcon(
    kind: String,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
) {
    when (kind) {
        "ranking" -> Box(
            modifier = modifier
                .size(size)
                .shadow(4.dp, CircleShape, ambientColor = OrangeAccent.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFFD84D), Color(0xFFFF981A)))),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.62f)) {
                val w = this.size.width
                val h = this.size.height
                drawOval(Color.White.copy(alpha = 0.35f), Offset(w * 0.15f, -h * 0.08f), Size(w * 0.7f, h * 0.35f))
                // Trophy cup
                val cup = Path().apply {
                    moveTo(w * 0.24f, h * 0.18f)
                    lineTo(w * 0.76f, h * 0.18f)
                    lineTo(w * 0.66f, h * 0.58f)
                    lineTo(w * 0.34f, h * 0.58f)
                    close()
                }
                drawPath(cup, Color.White)
                drawRoundRect(Color.White, Offset(w * 0.43f, h * 0.56f), Size(w * 0.14f, h * 0.2f), CornerRadius(4f))
                drawRoundRect(Color.White, Offset(w * 0.28f, h * 0.74f), Size(w * 0.44f, h * 0.12f), CornerRadius(6f))
                drawCircle(Color(0xFFFF981A), w * 0.09f, Offset(w * 0.5f, h * 0.36f))
            }
        }
        "fun_cards" -> Box(
            modifier = modifier
                .size(size)
                .shadow(4.dp, CircleShape, ambientColor = PinkAccent.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFF7EB3), Color(0xFFFF4FA3)))),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.62f)) {
                val w = this.size.width
                val h = this.size.height
                rotate(-12f, pivot = Offset(w * 0.45f, h * 0.5f)) {
                    drawRoundRect(Color.White.copy(alpha = 0.75f), Offset(w * 0.18f, h * 0.22f), Size(w * 0.46f, h * 0.62f), CornerRadius(8f))
                }
                rotate(10f, pivot = Offset(w * 0.58f, h * 0.5f)) {
                    drawRoundRect(Color.White, Offset(w * 0.34f, h * 0.18f), Size(w * 0.48f, h * 0.64f), CornerRadius(8f))
                    drawCircle(PremiumGold, w * 0.12f, Offset(w * 0.58f, h * 0.5f))
                }
            }
        }
        "tasks" -> Box(
            modifier = modifier
                .size(size)
                .shadow(4.dp, CircleShape, ambientColor = TurquoiseActive.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive))),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.6f)) {
                val w = this.size.width
                val h = this.size.height
                drawRoundRect(Color.White, Offset(w * 0.2f, h * 0.16f), Size(w * 0.6f, h * 0.72f), CornerRadius(10f))
                drawRoundRect(PremiumGold, Offset(w * 0.34f, h * 0.1f), Size(w * 0.32f, h * 0.14f), CornerRadius(6f))
                val check = Path().apply {
                    moveTo(w * 0.34f, h * 0.54f)
                    lineTo(w * 0.46f, h * 0.66f)
                    lineTo(w * 0.68f, h * 0.42f)
                }
                drawPath(check, TurquoiseDeep, style = Stroke(width = w * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        else -> Box(
            modifier = modifier
                .size(size)
                .shadow(4.dp, CircleShape, ambientColor = BlueAccent.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF60A5FA), PrimaryPurple))),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.62f)) {
                val w = this.size.width
                val h = this.size.height
                drawCircle(Color.White.copy(alpha = 0.85f), w * 0.16f, Offset(w * 0.34f, h * 0.38f))
                drawArc(Color.White.copy(alpha = 0.85f), 180f, 180f, true, Offset(w * 0.12f, h * 0.56f), Size(w * 0.44f, h * 0.38f))
                drawCircle(Color.White, w * 0.18f, Offset(w * 0.66f, h * 0.34f))
                drawArc(Color.White, 180f, 180f, true, Offset(w * 0.42f, h * 0.54f), Size(w * 0.48f, h * 0.42f))
                drawCircle(SuccessGreen, w * 0.09f, Offset(w * 0.82f, h * 0.24f))
            }
        }
    }
}

@Composable
fun GameBannerArtwork(
    gameId: GameId,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Soft glossy spheres backdrop
        drawCircle(
            color = Color.White.copy(alpha = 0.14f),
            radius = h * 0.46f,
            center = Offset(w * 0.65f, h * 0.50f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = h * 0.28f,
            center = Offset(w * 0.25f, h * 0.26f)
        )

        when (gameId) {
            GameId.SPACE_WEREWOLF -> {
                // Astronaut 3D helmet + glowing visor + star
                drawCircle(Color.White, h * 0.32f, Offset(w * 0.58f, h * 0.52f))
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF1E1B4B))),
                    topLeft = Offset(w * 0.38f, h * 0.36f),
                    size = Size(w * 0.42f, h * 0.24f),
                    cornerRadius = CornerRadius(24f)
                )
                drawCircle(PremiumGold, h * 0.07f, Offset(w * 0.22f, h * 0.3f))
                drawCircle(Color.White.copy(alpha = 0.7f), h * 0.04f, Offset(w * 0.84f, h * 0.24f))
            }
            GameId.JACKAROO -> {
                // Marbles + playing card deck
                rotate(-10f, pivot = Offset(w * 0.5f, h * 0.5f)) {
                    drawRoundRect(Color.White, Offset(w * 0.38f, h * 0.2f), Size(w * 0.34f, h * 0.56f), CornerRadius(14f))
                    drawCircle(PinkAccent, h * 0.09f, Offset(w * 0.55f, h * 0.48f))
                }
                drawCircle(TurquoiseCyan, h * 0.13f, Offset(w * 0.28f, h * 0.66f))
                drawCircle(Color.White.copy(alpha = 0.65f), h * 0.04f, Offset(w * 0.24f, h * 0.61f))
                drawCircle(PremiumGold, h * 0.12f, Offset(w * 0.78f, h * 0.68f))
            }
            GameId.GUESS_THEIR_ANSWERS -> {
                // Speech bubbles + glowing idea bulb
                drawRoundRect(Color.White, Offset(w * 0.28f, h * 0.24f), Size(w * 0.54f, h * 0.42f), CornerRadius(22f))
                drawCircle(PremiumGold, h * 0.14f, Offset(w * 0.55f, h * 0.45f))
                drawRoundRect(Color.White.copy(alpha = 0.7f), Offset(w * 0.42f, h * 0.70f), Size(w * 0.26f, h * 0.12f), CornerRadius(10f))
            }
            GameId.BRAIN_STORM -> {
                // Lightning bolt inside glowing orb
                drawCircle(Color.White.copy(alpha = 0.22f), h * 0.36f, Offset(w * 0.56f, h * 0.5f))
                val bolt = Path().apply {
                    moveTo(w * 0.62f, h * 0.14f)
                    lineTo(w * 0.36f, h * 0.54f)
                    lineTo(w * 0.54f, h * 0.54f)
                    lineTo(w * 0.44f, h * 0.86f)
                    lineTo(w * 0.74f, h * 0.44f)
                    lineTo(w * 0.56f, h * 0.44f)
                    close()
                }
                drawPath(bolt, PremiumGold)
            }
            GameId.OH_MY_CARD -> {
                // Fanned colorful cards
                rotate(-16f, pivot = Offset(w * 0.45f, h * 0.6f)) {
                    drawRoundRect(PremiumGold, Offset(w * 0.26f, h * 0.22f), Size(w * 0.32f, h * 0.54f), CornerRadius(12f))
                }
                rotate(4f, pivot = Offset(w * 0.55f, h * 0.6f)) {
                    drawRoundRect(Color.White, Offset(w * 0.38f, h * 0.18f), Size(w * 0.34f, h * 0.56f), CornerRadius(12f))
                    drawOval(PinkAccent, Offset(w * 0.44f, h * 0.32f), Size(w * 0.22f, h * 0.28f))
                }
            }
            else -> {
                // Happy Carrom board corner + red queen + striker
                drawRoundRect(Color(0xFFFFF3D6), Offset(w * 0.26f, h * 0.2f), Size(w * 0.58f, h * 0.62f), CornerRadius(16f))
                drawCircle(Color(0xFF4A2C11), h * 0.08f, Offset(w * 0.34f, h * 0.28f))
                drawCircle(Color(0xFF4A2C11), h * 0.08f, Offset(w * 0.76f, h * 0.28f))
                drawCircle(PinkAccent, h * 0.12f, Offset(w * 0.55f, h * 0.48f))
                drawCircle(TurquoiseActive, h * 0.10f, Offset(w * 0.40f, h * 0.64f))
                drawCircle(Color.White, h * 0.04f, Offset(w * 0.38f, h * 0.62f))
            }
        }
    }
}

@Composable
fun DiscoverMomentsRainbowIcon(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        drawCircle(
            brush = Brush.sweepGradient(
                listOf(TurquoiseActive, BlueAccent, PrimaryPurple, PinkAccent, OrangeAccent, PremiumGold, TurquoiseActive)
            ),
            style = Stroke(width = w * 0.22f)
        )
        drawCircle(color = TurquoiseActive, radius = w * 0.14f)
    }
}

@Composable
fun DiscoverArch3DIcon(
    category: DiscoverCategory,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
) {
    val (topTint, bottomTint, accent) = when (category) {
        DiscoverCategory.CHURCH -> Triple(Color(0xFFFFE5F1), Color(0xFFFFBFD9), PinkAccent)
        DiscoverCategory.FAMILY -> Triple(Color(0xFFFFF0DB), Color(0xFFFFD39C), OrangeAccent)
        DiscoverCategory.MENTORSHIP -> Triple(Color(0xFFE3F2FF), Color(0xFFB5DCFF), BlueAccent)
        DiscoverCategory.NEARBY -> Triple(Color(0xFFDFFAF4), Color(0xFFA2EFE0), TurquoiseActive)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
            .background(Brush.verticalGradient(listOf(topTint, bottomTint)))
            .border(
                width = 1.5.dp,
                color = accent.copy(alpha = 0.32f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val w = this.size.width
            val h = this.size.height
            when (category) {
                DiscoverCategory.CHURCH -> {
                    // Twin overlapping hearts / arch rings
                    drawCircle(accent, w * 0.22f, Offset(w * 0.36f, h * 0.44f))
                    drawCircle(Color(0xFFFF7EB6), w * 0.22f, Offset(w * 0.64f, h * 0.44f))
                    drawCircle(Color.White.copy(alpha = 0.55f), w * 0.08f, Offset(w * 0.30f, h * 0.36f))
                    val base = Path().apply {
                        moveTo(w * 0.16f, h * 0.50f)
                        lineTo(w * 0.50f, h * 0.86f)
                        lineTo(w * 0.84f, h * 0.50f)
                        close()
                    }
                    drawPath(base, accent)
                }
                DiscoverCategory.FAMILY -> {
                    // Golden crest shield with star
                    val shield = Path().apply {
                        moveTo(w * 0.50f, h * 0.12f)
                        lineTo(w * 0.84f, h * 0.26f)
                        lineTo(w * 0.78f, h * 0.68f)
                        lineTo(w * 0.50f, h * 0.88f)
                        lineTo(w * 0.22f, h * 0.68f)
                        lineTo(w * 0.16f, h * 0.26f)
                        close()
                    }
                    drawPath(shield, Brush.verticalGradient(listOf(PremiumGold, OrangeAccent)))
                    drawCircle(Color.White, w * 0.14f, Offset(w * 0.50f, h * 0.46f))
                }
                DiscoverCategory.MENTORSHIP -> {
                    // Two mentor/learner figures
                    drawCircle(Color.White, w * 0.16f, Offset(w * 0.34f, h * 0.34f))
                    drawRoundRect(BlueAccent, Offset(w * 0.16f, h * 0.52f), Size(w * 0.36f, h * 0.32f), CornerRadius(12f))
                    drawCircle(Color.White, w * 0.15f, Offset(w * 0.68f, h * 0.38f))
                    drawRoundRect(TurquoiseActive, Offset(w * 0.52f, h * 0.54f), Size(w * 0.34f, h * 0.30f), CornerRadius(12f))
                }
                DiscoverCategory.NEARBY -> {
                    // Glossy 3D Location Pin
                    val pin = Path().apply {
                        moveTo(w * 0.50f, h * 0.88f)
                        cubicTo(w * 0.20f, h * 0.56f, w * 0.18f, h * 0.20f, w * 0.50f, h * 0.16f)
                        cubicTo(w * 0.82f, h * 0.20f, w * 0.80f, h * 0.56f, w * 0.50f, h * 0.88f)
                        close()
                    }
                    drawPath(pin, Brush.verticalGradient(listOf(TurquoiseCyan, TurquoiseDeep)))
                    drawCircle(Color.White, w * 0.14f, Offset(w * 0.50f, h * 0.42f))
                }
            }
        }
    }
}

@Composable
fun VipCenterDiamond3DIcon(modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val topLeft = Path().apply {
            moveTo(w * 0.22f, h * 0.20f)
            lineTo(w * 0.78f, h * 0.20f)
            lineTo(w * 0.92f, h * 0.44f)
            lineTo(w * 0.50f, h * 0.88f)
            lineTo(w * 0.08f, h * 0.44f)
            close()
        }
        drawPath(topLeft, Brush.verticalGradient(listOf(Color(0xFFFFEB70), Color(0xFFFFA800))))
        val facet = Path().apply {
            moveTo(w * 0.28f, h * 0.20f)
            lineTo(w * 0.72f, h * 0.20f)
            lineTo(w * 0.50f, h * 0.88f)
            close()
        }
        drawPath(facet, Color.White.copy(alpha = 0.26f))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(w * 0.08f, h * 0.44f), Offset(w * 0.92f, h * 0.44f), strokeWidth = 2.5f)
    }
}

@Composable
fun ShopFront3DIcon(modifier: Modifier = Modifier, size: Dp = 44.dp, showDot: Boolean = true) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = this.size.width
            val h = this.size.height
            // Store body
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFFF88B8), PinkAccent)),
                topLeft = Offset(w * 0.14f, h * 0.36f),
                size = Size(w * 0.72f, h * 0.50f),
                cornerRadius = CornerRadius(12f)
            )
            // White awning
            drawRoundRect(
                color = Color(0xFFFFD8E9),
                topLeft = Offset(w * 0.08f, h * 0.20f),
                size = Size(w * 0.84f, h * 0.24f),
                cornerRadius = CornerRadius(14f)
            )
            // Window / door highlight
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(w * 0.34f, h * 0.54f),
                size = Size(w * 0.32f, h * 0.32f),
                cornerRadius = CornerRadius(8f)
            )
        }
        if (showDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun PlayShowShirt3DIcon(modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Box(modifier = modifier.size(width = size + 16.dp, height = size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val shirt = Path().apply {
                moveTo(w * 0.30f, h * 0.22f)
                lineTo(w * 0.70f, h * 0.22f)
                lineTo(w * 0.88f, h * 0.42f)
                lineTo(w * 0.74f, h * 0.54f)
                lineTo(w * 0.70f, h * 0.48f)
                lineTo(w * 0.70f, h * 0.86f)
                lineTo(w * 0.30f, h * 0.86f)
                lineTo(w * 0.30f, h * 0.48f)
                lineTo(w * 0.26f, h * 0.54f)
                lineTo(w * 0.12f, h * 0.42f)
                close()
            }
            drawPath(shirt, Brush.verticalGradient(listOf(Color(0xFF5EB3FF), Color(0xFF1A73E8))))
            drawArc(Color.White.copy(alpha = 0.75f), 0f, 180f, false, Offset(w * 0.38f, h * 0.18f), Size(w * 0.24f, h * 0.14f), style = Stroke(3f))
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = PinkAccent,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = (-2).dp)
        ) {
            Text(
                text = "Free",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
fun MyHomeHouse3DIcon(modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Roof
        val roof = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            lineTo(w * 0.88f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.12f, h * 0.48f)
            close()
        }
        drawPath(roof, Brush.verticalGradient(listOf(Color(0xFFFF8882), Color(0xFFFF4B5C))))
        // Sparkle star inside house
        val star = Path().apply {
            moveTo(w * 0.50f, h * 0.42f)
            lineTo(w * 0.56f, h * 0.56f)
            lineTo(w * 0.70f, h * 0.62f)
            lineTo(w * 0.56f, h * 0.68f)
            lineTo(w * 0.50f, h * 0.80f)
            lineTo(w * 0.44f, h * 0.68f)
            lineTo(w * 0.30f, h * 0.62f)
            lineTo(w * 0.44f, h * 0.56f)
            close()
        }
        drawPath(star, Color.White)
    }
}

@Composable
fun RoomRankCrestBadge(
    level: Int,
    isOfficial: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isOfficial) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = TurquoiseActive.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "OFFICIAL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TurquoiseDeep,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = OrangeAccent.copy(alpha = 0.14f)
        ) {
            Text(
                text = "★ Lv.$level",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeAccent,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

/**
 * Custom crisp outline + teal accent icons matching the Me tab reference (Help Center, Security Center, Settings).
 */
@Composable
fun HelpCenterTealIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val bubble = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            cubicTo(w * 0.80f, h * 0.14f, w * 0.88f, h * 0.34f, w * 0.88f, h * 0.50f)
            cubicTo(w * 0.88f, h * 0.72f, w * 0.72f, h * 0.84f, w * 0.44f, h * 0.84f)
            lineTo(w * 0.20f, h * 0.88f)
            lineTo(w * 0.26f, h * 0.70f)
            cubicTo(w * 0.14f, h * 0.62f, w * 0.12f, h * 0.42f, w * 0.18f, h * 0.30f)
            cubicTo(w * 0.24f, h * 0.19f, w * 0.36f, h * 0.14f, w * 0.50f, h * 0.14f)
            close()
        }
        drawPath(bubble, Color(0xFF282E3A), style = stroke)
        drawArc(
            color = TurquoiseActive,
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(w * 0.33f, h * 0.42f),
            size = Size(w * 0.34f, h * 0.22f),
            style = Stroke(width = w * 0.095f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SecurityCenterTealIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val shield = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.84f, h * 0.24f)
            lineTo(w * 0.82f, h * 0.56f)
            cubicTo(w * 0.80f, h * 0.74f, w * 0.64f, h * 0.86f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.36f, h * 0.86f, w * 0.20f, h * 0.74f, w * 0.18f, h * 0.56f)
            lineTo(w * 0.16f, h * 0.24f)
            close()
        }
        drawPath(shield, Color(0xFF282E3A), style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val check = Path().apply {
            moveTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.47f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.39f)
        }
        drawPath(check, TurquoiseActive, style = Stroke(width = w * 0.095f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SettingsTealGearIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        for (i in 0 until 6) {
            rotate(degrees = i * 60f, pivot = Offset(cx, cy)) {
                drawRoundRect(
                    color = Color(0xFF282E3A),
                    topLeft = Offset(cx - w * 0.09f, h * 0.08f),
                    size = Size(w * 0.18f, h * 0.22f),
                    cornerRadius = CornerRadius(4f)
                )
            }
        }
        drawCircle(color = Color.White, radius = w * 0.29f, center = Offset(cx, cy))
        drawCircle(color = Color(0xFF282E3A), radius = w * 0.29f, center = Offset(cx, cy), style = Stroke(width = w * 0.09f))
        drawCircle(color = TurquoiseActive, radius = w * 0.14f, center = Offset(cx, cy), style = Stroke(width = w * 0.085f))
    }
}

@Composable
fun AuctionGavel3DIcon(modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        rotate(-35f, pivot = Offset(w * 0.45f, h * 0.45f)) {
            drawRoundRect(
                brush = Brush.linearGradient(listOf(PremiumGold, OrangeAccent)),
                topLeft = Offset(w * 0.20f, h * 0.20f),
                size = Size(w * 0.48f, h * 0.24f),
                cornerRadius = CornerRadius(8f)
            )
            drawRoundRect(
                color = Color(0xFFFFF4B8),
                topLeft = Offset(w * 0.40f, h * 0.44f),
                size = Size(w * 0.12f, h * 0.42f),
                cornerRadius = CornerRadius(6f)
            )
        }
        drawRoundRect(
            color = PremiumGold,
            topLeft = Offset(w * 0.52f, h * 0.78f),
            size = Size(w * 0.36f, h * 0.12f),
            cornerRadius = CornerRadius(6f)
        )
    }
}

@Composable
fun RelationshipTag3DIcon(
    iconKey: String,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (iconKey) {
            "clover" -> {
                val leafBrush = Brush.linearGradient(listOf(Color(0xFF5CF2E2), TurquoiseActive))
                drawCircle(leafBrush, w * 0.20f, Offset(w * 0.34f, h * 0.32f))
                drawCircle(leafBrush, w * 0.20f, Offset(w * 0.66f, h * 0.32f))
                drawCircle(leafBrush, w * 0.20f, Offset(w * 0.34f, h * 0.62f))
                drawCircle(leafBrush, w * 0.20f, Offset(w * 0.66f, h * 0.62f))
                drawCircle(Color.White.copy(alpha = 0.7f), w * 0.08f, Offset(w * 0.50f, h * 0.47f))
            }
            "ribbon" -> {
                val bow = Brush.linearGradient(listOf(Color(0xFFFF9BD2), PinkAccent))
                val leftWing = Path().apply {
                    moveTo(w * 0.48f, h * 0.48f)
                    lineTo(w * 0.14f, h * 0.24f)
                    lineTo(w * 0.14f, h * 0.74f)
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(w * 0.52f, h * 0.48f)
                    lineTo(w * 0.86f, h * 0.24f)
                    lineTo(w * 0.86f, h * 0.74f)
                    close()
                }
                drawPath(leftWing, bow)
                drawPath(rightWing, bow)
                drawCircle(Color.White, w * 0.14f, Offset(w * 0.50f, h * 0.48f))
            }
            "bro" -> {
                drawCircle(Brush.linearGradient(listOf(Color(0xFF8BE2FF), BlueAccent)), w * 0.38f, Offset(w * 0.50f, h * 0.50f))
                drawCircle(Color.White.copy(alpha = 0.75f), w * 0.13f, Offset(w * 0.40f, h * 0.42f))
                drawCircle(Color.White.copy(alpha = 0.75f), w * 0.13f, Offset(w * 0.60f, h * 0.56f))
            }
            "puzzle" -> {
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(Color(0xFFC084FC), PrimaryPurple)),
                    topLeft = Offset(w * 0.16f, h * 0.20f),
                    size = Size(w * 0.64f, h * 0.64f),
                    cornerRadius = CornerRadius(10f)
                )
                drawCircle(Color(0xFFC084FC), w * 0.15f, Offset(w * 0.50f, h * 0.18f))
                drawCircle(Color.White.copy(alpha = 0.85f), w * 0.10f, Offset(w * 0.50f, h * 0.52f))
            }
            "shield" -> {
                val shield = Path().apply {
                    moveTo(w * 0.50f, h * 0.14f)
                    lineTo(w * 0.84f, h * 0.28f)
                    lineTo(w * 0.78f, h * 0.70f)
                    lineTo(w * 0.50f, h * 0.88f)
                    lineTo(w * 0.22f, h * 0.70f)
                    lineTo(w * 0.16f, h * 0.28f)
                    close()
                }
                drawPath(shield, Brush.verticalGradient(listOf(Color(0xFFFFEA7A), OrangeAccent)))
                drawCircle(Color.White, w * 0.13f, Offset(w * 0.50f, h * 0.48f))
            }
            else -> {
                drawCircle(Brush.linearGradient(listOf(PinkAccent, PremiumGold)), w * 0.34f, Offset(w * 0.50f, h * 0.50f))
                drawCircle(Color.White, w * 0.12f, Offset(w * 0.50f, h * 0.50f))
            }
        }
    }
}

@Composable
fun StartingToken3DArt(
    token: StartingToken,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (token) {
            StartingToken.SCOOTER -> {
                // Glossy yellow scooter silhouette
                drawCircle(Color(0xFF1F2430), w * 0.14f, Offset(w * 0.28f, h * 0.76f))
                drawCircle(Color(0xFF1F2430), w * 0.14f, Offset(w * 0.74f, h * 0.76f))
                drawCircle(Color(0xFFD6DBE8), w * 0.06f, Offset(w * 0.28f, h * 0.76f))
                drawCircle(Color(0xFFD6DBE8), w * 0.06f, Offset(w * 0.74f, h * 0.76f))
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(Color(0xFFFFEA61), Color(0xFFF59E0B))),
                    topLeft = Offset(w * 0.22f, h * 0.48f),
                    size = Size(w * 0.56f, h * 0.22f),
                    cornerRadius = CornerRadius(14f)
                )
                drawRoundRect(
                    color = Color(0xFFFFD83B),
                    topLeft = Offset(w * 0.60f, h * 0.22f),
                    size = Size(w * 0.14f, h * 0.34f),
                    cornerRadius = CornerRadius(8f)
                )
                drawCircle(Color.White.copy(alpha = 0.9f), w * 0.07f, Offset(w * 0.74f, h * 0.28f))
            }
            StartingToken.LUXURY_HOME -> {
                // Warm golden sunlit isometric cube room
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(Color(0xFFFFD586), Color(0xFFCC7A29))),
                    topLeft = Offset(w * 0.16f, h * 0.20f),
                    size = Size(w * 0.68f, h * 0.64f),
                    cornerRadius = CornerRadius(12f)
                )
                drawRoundRect(
                    color = Color(0xFFFFF4DC),
                    topLeft = Offset(w * 0.24f, h * 0.30f),
                    size = Size(w * 0.52f, h * 0.44f),
                    cornerRadius = CornerRadius(8f)
                )
                drawRoundRect(
                    color = Color(0xFFE07A5F),
                    topLeft = Offset(w * 0.30f, h * 0.50f),
                    size = Size(w * 0.26f, h * 0.16f),
                    cornerRadius = CornerRadius(6f)
                )
                drawCircle(PremiumGold, w * 0.08f, Offset(w * 0.64f, h * 0.42f))
            }
            StartingToken.BICYCLE -> {
                // Crisp metallic sport bicycle
                drawCircle(Color(0xFFE2E8F0), w * 0.16f, Offset(w * 0.26f, h * 0.68f), style = Stroke(width = w * 0.06f))
                drawCircle(Color(0xFFE2E8F0), w * 0.16f, Offset(w * 0.74f, h * 0.68f), style = Stroke(width = w * 0.06f))
                val frame = Path().apply {
                    moveTo(w * 0.26f, h * 0.68f)
                    lineTo(w * 0.48f, h * 0.42f)
                    lineTo(w * 0.66f, h * 0.42f)
                    lineTo(w * 0.74f, h * 0.68f)
                    moveTo(w * 0.48f, h * 0.42f)
                    lineTo(w * 0.52f, h * 0.66f)
                    lineTo(w * 0.26f, h * 0.68f)
                    moveTo(w * 0.66f, h * 0.42f)
                    lineTo(w * 0.62f, h * 0.28f)
                }
                drawPath(frame, TurquoiseCyan, style = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

@Composable
fun VirtualGiftVectorBadge(
    gift: VirtualGift,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val accent = Color(gift.accentHex)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.28f), accent.copy(alpha = 0.08f))))
            .border(1.2.dp, accent.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = gift.emoji, fontSize = (size.value * 0.52f).sp)
    }
}

@Composable
fun Wefun3DCoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        // Outer metallic gold rim
        drawCircle(
            brush = Brush.linearGradient(
                listOf(Color(0xFFFFF59D), Color(0xFFFFC107), Color(0xFFB45309))
            ),
            radius = w * 0.48f,
            center = Offset(cx, cy)
        )
        // Inner embossed coin well
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFEB3B), Color(0xFFF59E0B), Color(0xFFD97706)),
                center = Offset(cx * 0.85f, cy * 0.82f),
                radius = w * 0.42f
            ),
            radius = w * 0.37f,
            center = Offset(cx, cy)
        )
        // Glossy specular top highlight
        drawOval(
            color = Color.White.copy(alpha = 0.48f),
            topLeft = Offset(w * 0.24f, h * 0.14f),
            size = Size(w * 0.52f, h * 0.22f)
        )
        // Subtle "W" star emblem inside coin
        val wMark = Path().apply {
            moveTo(w * 0.30f, h * 0.39f)
            lineTo(w * 0.39f, h * 0.65f)
            lineTo(w * 0.50f, h * 0.44f)
            lineTo(w * 0.61f, h * 0.65f)
            lineTo(w * 0.70f, h * 0.39f)
        }
        drawPath(
            path = wMark,
            color = Color(0xFF78350F).copy(alpha = 0.85f),
            style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun WefunDiamond3DIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val gem = Path().apply {
            moveTo(w * 0.24f, h * 0.16f)
            lineTo(w * 0.76f, h * 0.16f)
            lineTo(w * 0.94f, h * 0.42f)
            lineTo(w * 0.50f, h * 0.90f)
            lineTo(w * 0.06f, h * 0.42f)
            close()
        }
        drawPath(
            path = gem,
            brush = Brush.verticalGradient(listOf(Color(0xFFFF9BD2), PinkAccent, Color(0xFFD81B60)))
        )
        val topFacet = Path().apply {
            moveTo(w * 0.30f, h * 0.16f)
            lineTo(w * 0.70f, h * 0.16f)
            lineTo(w * 0.50f, h * 0.88f)
            close()
        }
        drawPath(topFacet, Color.White.copy(alpha = 0.30f))
        drawLine(
            color = Color.White.copy(alpha = 0.65f),
            start = Offset(w * 0.07f, h * 0.42f),
            end = Offset(w * 0.93f, h * 0.42f),
            strokeWidth = w * 0.06f
        )
    }
}

@Composable
fun VoiceRoom3DStageIcon(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = TurquoiseActive.copy(alpha = 0.32f), spotColor = TurquoiseActive.copy(alpha = 0.32f))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive, BlueAccent)))
            .border(1.2.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.64f)) {
            val w = this.size.width
            val h = this.size.height
            drawOval(Color.White.copy(alpha = 0.28f), Offset(w * 0.1f, -h * 0.06f), Size(w * 0.8f, h * 0.36f))
            // 3D Mic Capsule
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.36f, h * 0.14f),
                size = Size(w * 0.28f, h * 0.44f),
                cornerRadius = CornerRadius(w * 0.14f)
            )
            drawLine(
                color = TurquoiseActive.copy(alpha = 0.65f),
                start = Offset(w * 0.42f, h * 0.28f),
                end = Offset(w * 0.58f, h * 0.28f),
                strokeWidth = w * 0.055f,
                cap = StrokeCap.Round
            )
            // Cradle arc & stand
            drawArc(
                color = Color.White,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.22f, h * 0.28f),
                size = Size(w * 0.56f, h * 0.40f),
                style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
            )
            drawLine(Color.White, Offset(w * 0.50f, h * 0.68f), Offset(w * 0.50f, h * 0.86f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(w * 0.34f, h * 0.86f), Offset(w * 0.66f, h * 0.86f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun BottomNav3DIcon(
    route: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
) {
    val mutedStroke = Color(0xFF8C92A4)
    val softFill = Color(0xFFEFF2F7)
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = w * 0.085f
        when (route) {
            "home" -> {
                val bodyBrush = if (selected) {
                    Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive, BlueAccent))
                } else {
                    Brush.linearGradient(listOf(softFill, softFill))
                }
                drawRoundRect(
                    brush = bodyBrush,
                    topLeft = Offset(w * 0.08f, h * 0.24f),
                    size = Size(w * 0.84f, h * 0.54f),
                    cornerRadius = CornerRadius(w * 0.26f)
                )
                if (!selected) {
                    drawRoundRect(
                        color = mutedStroke,
                        topLeft = Offset(w * 0.08f, h * 0.24f),
                        size = Size(w * 0.84f, h * 0.54f),
                        cornerRadius = CornerRadius(w * 0.26f),
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    drawOval(Color.White.copy(alpha = 0.28f), Offset(w * 0.16f, h * 0.26f), Size(w * 0.68f, h * 0.16f))
                }
                val fg = if (selected) Color.White else mutedStroke
                drawLine(fg, Offset(w * 0.24f, h * 0.51f), Offset(w * 0.42f, h * 0.51f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(fg, Offset(w * 0.33f, h * 0.42f), Offset(w * 0.33f, h * 0.60f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawCircle(if (selected) PremiumGold else mutedStroke, w * 0.065f, Offset(w * 0.66f, h * 0.45f))
                drawCircle(if (selected) PinkAccent else mutedStroke, w * 0.065f, Offset(w * 0.74f, h * 0.57f))
            }
            "party" -> {
                val roofPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.12f)
                    lineTo(w * 0.88f, h * 0.46f)
                    lineTo(w * 0.78f, h * 0.46f)
                    lineTo(w * 0.78f, h * 0.86f)
                    lineTo(w * 0.22f, h * 0.86f)
                    lineTo(w * 0.22f, h * 0.46f)
                    lineTo(w * 0.12f, h * 0.46f)
                    close()
                }
                if (selected) {
                    drawPath(roofPath, Brush.verticalGradient(listOf(TurquoiseCyan, TurquoiseActive, BlueAccent)))
                } else {
                    drawPath(roofPath, softFill)
                    drawPath(roofPath, mutedStroke, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
                }
                val barCol = if (selected) Color.White else mutedStroke
                drawLine(barCol, Offset(w * 0.38f, h * 0.56f), Offset(w * 0.38f, h * 0.72f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(barCol, Offset(w * 0.50f, h * 0.46f), Offset(w * 0.50f, h * 0.74f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(barCol, Offset(w * 0.62f, h * 0.54f), Offset(w * 0.62f, h * 0.72f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            "chats" -> {
                val bubble = Path().apply {
                    moveTo(w * 0.50f, h * 0.16f)
                    cubicTo(w * 0.82f, h * 0.16f, w * 0.90f, h * 0.36f, w * 0.90f, h * 0.52f)
                    cubicTo(w * 0.90f, h * 0.72f, w * 0.72f, h * 0.82f, w * 0.44f, h * 0.82f)
                    lineTo(w * 0.20f, h * 0.88f)
                    lineTo(w * 0.26f, h * 0.70f)
                    cubicTo(w * 0.14f, h * 0.62f, w * 0.10f, h * 0.42f, w * 0.18f, h * 0.30f)
                    cubicTo(w * 0.25f, h * 0.20f, w * 0.36f, h * 0.16f, w * 0.50f, h * 0.16f)
                    close()
                }
                if (selected) {
                    drawPath(bubble, Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive, BlueAccent)))
                } else {
                    drawPath(bubble, softFill)
                    drawPath(bubble, mutedStroke, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
                }
                val dotCol = if (selected) Color.White else mutedStroke
                drawCircle(dotCol, w * 0.055f, Offset(w * 0.36f, h * 0.50f))
                drawCircle(dotCol, w * 0.055f, Offset(w * 0.52f, h * 0.50f))
                drawCircle(dotCol, w * 0.055f, Offset(w * 0.68f, h * 0.50f))
            }
            "discover" -> {
                if (selected) {
                    drawCircle(Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive, PrimaryPurple)), w * 0.41f, Offset(w * 0.5f, h * 0.5f))
                } else {
                    drawCircle(softFill, w * 0.41f, Offset(w * 0.5f, h * 0.5f))
                    drawCircle(mutedStroke, w * 0.41f, Offset(w * 0.5f, h * 0.5f), style = Stroke(width = strokeWidth))
                }
                val needle = Path().apply {
                    moveTo(w * 0.64f, h * 0.34f)
                    lineTo(w * 0.56f, h * 0.56f)
                    lineTo(w * 0.36f, h * 0.66f)
                    lineTo(w * 0.44f, h * 0.44f)
                    close()
                }
                drawPath(needle, if (selected) Color.White else mutedStroke)
                drawCircle(if (selected) PremiumGold else softFill, w * 0.055f, Offset(w * 0.5f, h * 0.5f))
            }
            else -> {
                val headCenter = Offset(w * 0.50f, h * 0.33f)
                val bodyTop = Offset(w * 0.18f, h * 0.56f)
                val bodySize = Size(w * 0.64f, h * 0.36f)
                if (selected) {
                    drawCircle(Brush.linearGradient(listOf(TurquoiseCyan, TurquoiseActive)), w * 0.19f, headCenter)
                    drawArc(Brush.linearGradient(listOf(TurquoiseActive, BlueAccent)), 180f, 180f, true, bodyTop, Size(w * 0.64f, h * 0.64f))
                    drawCircle(Color.White.copy(alpha = 0.4f), w * 0.06f, Offset(w * 0.44f, h * 0.27f))
                } else {
                    drawCircle(softFill, w * 0.19f, headCenter)
                    drawCircle(mutedStroke, w * 0.19f, headCenter, style = Stroke(width = strokeWidth))
                    drawArc(softFill, 180f, 180f, true, bodyTop, Size(bodySize.width, h * 0.64f))
                    drawArc(mutedStroke, 180f, 180f, false, bodyTop, Size(bodySize.width, h * 0.64f), style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                }
            }
        }
    }
}

@Composable
fun MeMenuRow3DIcon(
    kind: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
) {
    val (gradient, fgColor) = when (kind) {
        "moments" -> listOf(Color(0xFFFF8BC2), PinkAccent) to Color.White
        "stats" -> listOf(Color(0xFF60A5FA), BlueAccent) to Color.White
        "visitors" -> listOf(Color(0xFFFFB74D), OrangeAccent) to Color.White
        "invite" -> listOf(Color(0xFF2DD4BF), TurquoiseActive) to Color.White
        "badge" -> listOf(Color(0xFFFFE066), Color(0xFFF59E0B)) to Color.White
        "contributions" -> listOf(Color(0xFFA78BFA), PrimaryPurple) to Color.White
        "language" -> listOf(Color(0xFF38BDF8), BlueAccent) to Color.White
        else -> listOf(Color(0xFF34D399), SuccessGreen) to Color.White
    }
    Box(
        modifier = modifier
            .size(size)
            .shadow(3.dp, RoundedCornerShape(9.dp), ambientColor = gradient.last().copy(alpha = 0.25f))
            .clip(RoundedCornerShape(9.dp))
            .background(Brush.linearGradient(gradient))
            .border(0.8.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.56f)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            when (kind) {
                "moments" -> {
                    drawRoundRect(fgColor, Offset(w * 0.12f, h * 0.16f), Size(w * 0.76f, h * 0.68f), CornerRadius(w * 0.16f), style = stroke)
                    drawCircle(fgColor, w * 0.11f, Offset(w * 0.36f, h * 0.40f))
                    val mountain = Path().apply {
                        moveTo(w * 0.20f, h * 0.72f)
                        lineTo(w * 0.46f, h * 0.48f)
                        lineTo(w * 0.64f, h * 0.62f)
                        lineTo(w * 0.74f, h * 0.52f)
                        lineTo(w * 0.82f, h * 0.72f)
                    }
                    drawPath(mountain, fgColor, style = stroke)
                }
                "stats" -> {
                    drawLine(fgColor, Offset(w * 0.24f, h * 0.80f), Offset(w * 0.24f, h * 0.52f), strokeWidth = w * 0.16f, cap = StrokeCap.Round)
                    drawLine(fgColor, Offset(w * 0.50f, h * 0.80f), Offset(w * 0.50f, h * 0.24f), strokeWidth = w * 0.16f, cap = StrokeCap.Round)
                    drawLine(fgColor, Offset(w * 0.76f, h * 0.80f), Offset(w * 0.76f, h * 0.40f), strokeWidth = w * 0.16f, cap = StrokeCap.Round)
                }
                "visitors" -> {
                    val eye = Path().apply {
                        moveTo(w * 0.10f, h * 0.50f)
                        cubicTo(w * 0.32f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.90f, h * 0.50f)
                        cubicTo(w * 0.68f, h * 0.78f, w * 0.32f, h * 0.78f, w * 0.10f, h * 0.50f)
                        close()
                    }
                    drawPath(eye, fgColor, style = stroke)
                    drawCircle(fgColor, w * 0.14f, Offset(w * 0.50f, h * 0.50f))
                }
                "invite" -> {
                    drawCircle(fgColor, w * 0.17f, Offset(w * 0.40f, h * 0.34f))
                    drawArc(fgColor, 180f, 180f, true, Offset(w * 0.14f, h * 0.54f), Size(w * 0.52f, h * 0.52f))
                    drawLine(fgColor, Offset(w * 0.76f, h * 0.32f), Offset(w * 0.76f, h * 0.58f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
                    drawLine(fgColor, Offset(w * 0.63f, h * 0.45f), Offset(w * 0.89f, h * 0.45f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
                }
                "badge" -> {
                    val hex = Path().apply {
                        moveTo(w * 0.50f, h * 0.10f)
                        lineTo(w * 0.86f, h * 0.30f)
                        lineTo(w * 0.86f, h * 0.70f)
                        lineTo(w * 0.50f, h * 0.90f)
                        lineTo(w * 0.14f, h * 0.70f)
                        lineTo(w * 0.14f, h * 0.30f)
                        close()
                    }
                    drawPath(hex, fgColor)
                    drawCircle(OrangeAccent, w * 0.14f, Offset(w * 0.50f, h * 0.50f))
                }
                "contributions" -> {
                    drawRoundRect(fgColor, Offset(w * 0.16f, h * 0.14f), Size(w * 0.68f, h * 0.72f), CornerRadius(w * 0.14f), style = stroke)
                    drawLine(fgColor, Offset(w * 0.32f, h * 0.38f), Offset(w * 0.68f, h * 0.38f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
                    drawLine(fgColor, Offset(w * 0.32f, h * 0.58f), Offset(w * 0.58f, h * 0.58f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
                }
                "language" -> {
                    drawCircle(fgColor, w * 0.38f, Offset(w * 0.50f, h * 0.50f), style = stroke)
                    drawOval(fgColor, Offset(w * 0.32f, h * 0.12f), Size(w * 0.36f, h * 0.76f), style = Stroke(width = w * 0.08f))
                    drawLine(fgColor, Offset(w * 0.14f, h * 0.50f), Offset(w * 0.86f, h * 0.50f), strokeWidth = w * 0.08f)
                }
                else -> {
                    drawCircle(fgColor, w * 0.36f, Offset(w * 0.50f, h * 0.50f), style = stroke)
                    val check = Path().apply {
                        moveTo(w * 0.34f, h * 0.52f)
                        lineTo(w * 0.46f, h * 0.64f)
                        lineTo(w * 0.68f, h * 0.38f)
                    }
                    drawPath(check, fgColor, style = stroke)
                }
            }
        }
    }
}



