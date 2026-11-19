package com.example.features.voice_room.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import kotlin.math.sin

private val StarSeedPositions = listOf(
    0.12f to 0.08f, 0.28f to 0.14f, 0.45f to 0.06f, 0.72f to 0.11f, 0.89f to 0.16f,
    0.08f to 0.28f, 0.24f to 0.34f, 0.56f to 0.25f, 0.82f to 0.31f, 0.94f to 0.23f,
    0.16f to 0.48f, 0.39f to 0.44f, 0.68f to 0.52f, 0.88f to 0.46f, 0.50f to 0.62f,
    0.11f to 0.68f, 0.77f to 0.66f, 0.31f to 0.76f, 0.62f to 0.80f, 0.91f to 0.74f,
)

@Composable
fun VoiceRoomBackdrop(
    theme: VoiceRoomBackdropTheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "roomStarlight")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(tween(4800, easing = LinearEasing), RepeatMode.Restart),
        label = "starPhase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(theme.topHex),
                        Color(theme.midHex),
                        Color(theme.bottomHex),
                    )
                )
            )
    ) {
        if (theme.isDarkStage) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                StarSeedPositions.forEachIndexed { idx, (fx, fy) ->
                    val alpha = (0.18f + 0.35f * ((sin(phase + idx * 0.9f) + 1f) * 0.5f)).coerceIn(0.1f, 0.65f)
                    val radius = if (idx % 3 == 0) 3.6f else 2.2f
                    drawCircle(
                        color = if (idx % 4 == 0) PremiumGold.copy(alpha = alpha) else Color.White.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(size.width * fx, size.height * fy)
                    )
                }
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomBackdropThemePickerSheet(
    selectedTheme: VoiceRoomBackdropTheme,
    onSelectTheme: (VoiceRoomBackdropTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Palette, null, tint = PrimaryPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Voice Room Background Theme",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select stage atmosphere while keeping sheets crisp & readable",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }

            VoiceRoomBackdropTheme.entries.forEach { option ->
                val isSelected = option == selectedTheme
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) PrimaryPurple.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (isSelected) 1.6.dp else 1.dp,
                        color = if (isSelected) PrimaryPurple else BorderColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectTheme(option)
                            onDismiss()
                        }
                        .testTag("room_theme_option_${option.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(option.topHex), Color(option.bottomHex))
                                        )
                                    )
                            )
                            Column {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PrimaryText,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (option.isDarkStage) "Immersive Stage Gradient" else "Daylight White Mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Selected",
                                tint = PrimaryPurple
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
