package com.example.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BottomNav3DIcon
import com.example.ui.theme.BorderColor
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TurquoiseActive

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onSelectDestination: (WefunDestination) -> Unit,
    modifier: Modifier = Modifier,
    unreadChatsCount: Int = 0,
    unreadNotificationsCount: Int = 0,
    showVipGoldIndicator: Boolean = true,
) {
    val barSurface = PrimaryBackground
    val activeColor = TurquoiseActive
    val inactiveColor = SecondaryText

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(barSurface)
    ) {
        HorizontalDivider(thickness = 1.dp, color = BorderColor)
        NavigationBar(
            containerColor = barSurface,
            contentColor = activeColor,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets.navigationBars,
        ) {
            WefunDestination.entries.forEach { destination ->
                val isSelected = currentRoute == destination.route
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) activeColor else inactiveColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "nav_icon_tint_${destination.route}"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) activeColor else inactiveColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "nav_label_color_${destination.route}"
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "nav_icon_scale_${destination.route}"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectDestination(destination) },
                    modifier = Modifier
                        .testTag("nav_tab_${destination.route}")
                        .semantics { selected = isSelected },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = activeColor,
                        selectedTextColor = activeColor,
                        unselectedIconColor = inactiveColor,
                        unselectedTextColor = inactiveColor,
                        indicatorColor = TurquoiseActive.copy(alpha = 0.12f)
                    ),
                    icon = {
                        DestinationTabIcon(
                            destination = destination,
                            selected = isSelected,
                            tint = iconTint,
                            scale = iconScale,
                            unreadChatsCount = unreadChatsCount,
                            unreadNotificationsCount = unreadNotificationsCount,
                            showDiscoverDot = true,
                            showVipGoldIndicator = showVipGoldIndicator,
                            barSurface = barSurface,
                        )
                    },
                    label = {
                        Text(
                            text = destination.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = labelColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun WefunNavigationRail(
    currentRoute: String?,
    onSelectDestination: (WefunDestination) -> Unit,
    modifier: Modifier = Modifier,
    unreadChatsCount: Int = 0,
    unreadNotificationsCount: Int = 0,
    showVipGoldIndicator: Boolean = true,
) {
    val barSurface = PrimaryBackground
    val activeColor = TurquoiseActive
    val inactiveColor = SecondaryText

    NavigationRail(
        containerColor = barSurface,
        contentColor = activeColor,
        modifier = modifier
            .fillMaxHeight()
            .border(width = 1.dp, color = BorderColor)
    ) {
        Spacer(modifier = Modifier.weight(0.2f))
        WefunDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val iconTint by animateColorAsState(
                targetValue = if (isSelected) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = 200),
                label = "rail_icon_tint_${destination.route}"
            )
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.08f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "rail_icon_scale_${destination.route}"
            )
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectDestination(destination) },
                modifier = Modifier
                    .testTag("nav_rail_${destination.route}")
                    .semantics { selected = isSelected },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = activeColor,
                    selectedTextColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    unselectedTextColor = inactiveColor,
                    indicatorColor = TurquoiseActive.copy(alpha = 0.12f)
                ),
                icon = {
                    DestinationTabIcon(
                        destination = destination,
                        selected = isSelected,
                        tint = iconTint,
                        scale = iconScale,
                        unreadChatsCount = unreadChatsCount,
                        unreadNotificationsCount = unreadNotificationsCount,
                        showDiscoverDot = true,
                        showVipGoldIndicator = showVipGoldIndicator,
                        barSurface = barSurface,
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DestinationTabIcon(
    destination: WefunDestination,
    selected: Boolean,
    tint: Color,
    scale: Float,
    unreadChatsCount: Int,
    unreadNotificationsCount: Int,
    showDiscoverDot: Boolean,
    showVipGoldIndicator: Boolean,
    barSurface: Color,
) {
    val iconModifier = Modifier.scale(scale)

    when {
        destination == WefunDestination.Chats && unreadChatsCount > 0 -> {
            BadgedBox(
                badge = {
                    Badge(containerColor = ErrorRed, contentColor = Color.White) {
                        Text(
                            text = if (unreadChatsCount > 99) "99+" else "$unreadChatsCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                BottomNav3DIcon(
                    route = destination.route,
                    selected = selected,
                    modifier = iconModifier,
                    size = 26.dp,
                )
            }
        }
        destination == WefunDestination.Discover && showDiscoverDot -> {
            Box(modifier = Modifier.size(width = 32.dp, height = 28.dp)) {
                BottomNav3DIcon(
                    route = destination.route,
                    selected = selected,
                    modifier = iconModifier.align(Alignment.Center),
                    size = 26.dp,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(ErrorRed)
                        .border(1.dp, barSurface, CircleShape)
                )
            }
        }
        destination == WefunDestination.Profile && (unreadNotificationsCount > 0 || showVipGoldIndicator) -> {
            Box(modifier = Modifier.size(width = 32.dp, height = 28.dp)) {
                BottomNav3DIcon(
                    route = destination.route,
                    selected = selected,
                    modifier = iconModifier.align(Alignment.Center),
                    size = 26.dp,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (showVipGoldIndicator) Color(0xFFFFD700) else ErrorRed)
                        .border(1.dp, barSurface, CircleShape)
                        .then(
                            if (showVipGoldIndicator) Modifier.testTag("nav_vip_gold_indicator") else Modifier
                        )
                )
            }
        }
        else -> {
            BottomNav3DIcon(
                route = destination.route,
                selected = selected,
                modifier = iconModifier,
                size = 26.dp,
            )
        }
    }
}
