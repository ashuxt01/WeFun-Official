package com.example.ui.rooms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ActiveGiftEffect
import com.example.data.model.GiftCategory
import com.example.data.model.RoomSeat
import com.example.data.model.VirtualGift
import com.example.data.repository.WefunSeedData
import com.example.ui.components.VirtualGiftVectorBadge
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GiftAnimationOverlay(
    effect: ActiveGiftEffect?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = effect != null,
        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.72f),
        exit = fadeOut(tween(300)) + scaleOut(targetScale = 1.08f),
        modifier = modifier
    ) {
        if (effect != null) {
            val accent = Color(effect.gift.accentHex)
            val progress = remember(effect.id) { Animatable(0f) }
            val transition = rememberInfiniteTransition(label = "giftGlow")
            val pulse by transition.animateFloat(
                initialValue = 0.94f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(tween(520, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "giftScale"
            )
            LaunchedEffect(effect.id) {
                progress.snapTo(0f)
                progress.animateTo(1f, tween(durationMillis = effect.gift.durationMs.toInt(), easing = LinearEasing))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (effect.gift.isFullScreen) Color.Black.copy(alpha = 0.45f)
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height * 0.42f)
                    val p = progress.value
                    val count = if (effect.gift.isFullScreen) 28 else 14
                    for (i in 0 until count) {
                        val angle = (2.0 * Math.PI * i / count) + p * 2.5
                        val radius = (size.minDimension * 0.45f) * p
                        val x = center.x + (cos(angle) * radius).toFloat()
                        val y = center.y + (sin(angle) * radius).toFloat()
                        drawCircle(
                            color = if (i % 2 == 0) accent.copy(alpha = (1f - p).coerceIn(0f, 1f))
                            else CyanPulse.copy(alpha = (1f - p).coerceIn(0f, 1f)),
                            radius = (14f - 8f * p).coerceAtLeast(3f),
                            center = Offset(x, y)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    if (effect.gift.usesDragonArt) {
                        Box(
                            modifier = Modifier
                                .size(196.dp)
                                .scale(pulse)
                                .clip(CircleShape)
                                .border(3.dp, Brush.linearGradient(listOf(RoyalGold, ElectricViolet, CyanPulse)), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_gift_cosmic_dragon_1794578988186),
                                contentDescription = effect.gift.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulse)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.45f), Color.Transparent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = effect.gift.emoji, fontSize = 68.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xCC120D24),
                        border = BorderStroke(1.5.dp, accent)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, null, tint = RoyalGold, modifier = Modifier.size(18.dp))
                            Text(
                                text = "${effect.senderName} -> ${effect.receiverName}: ${effect.gift.name}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Text(
                                text = "x${effect.comboCount}",
                                style = MaterialTheme.typography.titleLarge,
                                color = RoyalGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualGiftDrawerSheet(
    userCoins: Long,
    occupiedSeats: List<RoomSeat>,
    packageInventory: Map<String, Int> = emptyMap(),
    onDismiss: () -> Unit,
    onSendGift: (gift: VirtualGift, combo: Int, targetSeats: List<Int>, receiverLabel: String, fromPackage: Boolean) -> Unit,
    onOpenWallet: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    var drawerTab by remember { mutableIntStateOf(if (packageInventory.values.any { it > 0 }) 0 else 1) } // 0 = Package, 1 = Gift
    var selectedCategory by remember { mutableStateOf<GiftCategory?>(null) }
    var selectedGift by remember { mutableStateOf(WefunSeedData.virtualGifts.first()) }
    var selectedCombo by remember { mutableIntStateOf(1) }
    var sendToAll by remember { mutableStateOf(false) }
    var selectedSeatIndex by remember { mutableIntStateOf(occupiedSeats.firstOrNull()?.seatIndex ?: 0) }
    var previewActive by remember { mutableStateOf(false) }

    val isPackageTab = drawerTab == 0
    val filteredGifts = remember(drawerTab, selectedCategory, packageInventory) {
        if (drawerTab == 0) {
            WefunSeedData.virtualGifts.filter { (packageInventory[it.id] ?: 0) > 0 }
        } else {
            if (selectedCategory == null) WefunSeedData.virtualGifts
            else WefunSeedData.virtualGifts.filter { it.category == selectedCategory }
        }
    }

    LaunchedEffect(filteredGifts) {
        if (filteredGifts.isNotEmpty() && filteredGifts.none { it.id == selectedGift.id }) {
            selectedGift = filteredGifts.first()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .testTag("virtual_gift_drawer"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Virtual Gifts & Package", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Surface(
                    onClick = onOpenWallet,
                    shape = RoundedCornerShape(50),
                    color = RoyalGold.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.MonetizationOn, null, tint = RoyalGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$userCoins Coins • Top Up", style = MaterialTheme.typography.labelMedium, color = RoyalGold)
                    }
                }
            }

            // Package vs Gift Tabs
            TabRow(selectedTabIndex = drawerTab, containerColor = Color.Transparent) {
                Tab(
                    selected = drawerTab == 0,
                    onClick = { drawerTab = 0 },
                    text = { Text("Package (${packageInventory.values.sum()})") },
                    modifier = Modifier.testTag("gift_tab_package")
                )
                Tab(
                    selected = drawerTab == 1,
                    onClick = { drawerTab = 1 },
                    text = { Text("Gift") },
                    modifier = Modifier.testTag("gift_tab_gift")
                )
            }

            // Target Receiver Strip
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = sendToAll,
                        onClick = { sendToAll = true },
                        label = { Text("All On Mic (${occupiedSeats.size})") }
                    )
                }
                items(occupiedSeats, key = { it.seatIndex }) { seat ->
                    FilterChip(
                        selected = !sendToAll && selectedSeatIndex == seat.seatIndex,
                        onClick = {
                            sendToAll = false
                            selectedSeatIndex = seat.seatIndex
                        },
                        label = { Text("#${seat.seatIndex + 1} ${seat.occupantName ?: "Speaker"}") }
                    )
                }
            }

            // Category Filter Strip (on Gift tab)
            if (!isPackageTab) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All Gifts") }
                        )
                    }
                    items(GiftCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label) }
                        )
                    }
                }
            }

            if (previewActive) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ElectricViolet.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, TurquoiseActive),
                    modifier = Modifier.fillMaxWidth().testTag("gift_preview_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        VirtualGiftVectorBadge(gift = selectedGift, size = 38.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Previewing ${selectedGift.name} Animation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Charm +${selectedGift.coinPrice} • ${if (selectedGift.isFullScreen) "Full-Screen Particle Showcase" else "Floating Seat Effect"}", fontSize = 11.sp, color = SecondaryText)
                        }
                    }
                }
            }

            // Gifts Grid
            if (filteredGifts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your Package inventory is empty — switch to the Gift tab!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredGifts, key = { it.id }) { gift ->
                        val isSelected = selectedGift.id == gift.id
                        val bagQty = packageInventory[gift.id] ?: 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) ElectricViolet.copy(alpha = 0.22f)
                                    else wefunColors.glassSurface
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CyanPulse else wefunColors.glassBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedGift = gift }
                                .padding(8.dp)
                                .testTag("gift_item_${gift.id}")
                        ) {
                            if (isPackageTab && bagQty > 0) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = OrangeAccent,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "x$bagQty",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (gift.usesDragonArt) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_gift_cosmic_dragon_1794578988186),
                                        contentDescription = gift.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    VirtualGiftVectorBadge(gift = gift, size = 38.dp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gift.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isPackageTab) "Free" else "${gift.coinPrice}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPackageTab) TurquoiseActive else RoyalGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Combo, Preview & Send Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1, 5, 10).forEach { combo ->
                    FilterChip(
                        selected = selectedCombo == combo,
                        onClick = { selectedCombo = combo },
                        label = { Text("x$combo") }
                    )
                }
                OutlinedButton(
                    onClick = { previewActive = !previewActive },
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(36.dp).testTag("gift_preview_button")
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, modifier = Modifier.size(15.dp))
                    Text("Preview", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                val targetSeats = if (sendToAll) occupiedSeats.map { it.seatIndex }.ifEmpty { listOf(0) }
                else listOf(selectedSeatIndex)
                val targetLabel = if (sendToAll) "All Speakers"
                else occupiedSeats.firstOrNull { it.seatIndex == selectedSeatIndex }?.occupantName ?: "Host"
                val totalCost = if (isPackageTab) 0L else selectedGift.coinPrice * selectedCombo * targetSeats.size

                WefunGradientButton(
                    text = if (isPackageTab) "Send Free" else "Send ($totalCost)",
                    testTag = "send_gift_confirm_button",
                    colors = listOf(RoseNeon, RoyalGold),
                    onClick = {
                        onSendGift(selectedGift, selectedCombo, targetSeats, targetLabel, isPackageTab)
                        onDismiss()
                    },
                    modifier = Modifier.width(126.dp)
                )
            }
        }
    }
}
