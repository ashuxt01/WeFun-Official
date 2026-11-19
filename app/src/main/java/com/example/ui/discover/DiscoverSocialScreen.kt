package com.example.ui.discover

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.DiscoverCategory
import com.example.ui.components.DiscoverArch3DIcon
import com.example.ui.components.DiscoverMomentsRainbowIcon
import com.example.ui.components.GlassCard
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

/**
 * Clean Discover Tab matching the reference design:
 * - Discover Heading
 * - Moments Banner Card (navigates to separate Moments screen)
 * - 4-card grid: Church, Family, Mentorship, Nearby
 */
@Composable
fun DiscoverSocialScreen(
    onOpenMoments: () -> Unit,
    onOpenDiscoverCategory: (DiscoverCategory) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText
            )
        }

        // Moments Banner Card -> Opens Dedicated Separate Moments Screen
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discover_moments_banner"),
                cornerRadius = 18.dp,
                onClick = onOpenMoments
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DiscoverMomentsRainbowIcon(size = 30.dp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Moments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PinkAccent)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Open Moments",
                            tint = SecondaryText
                        )
                    }
                }
            }
        }

        // 4 Discovery Cards Grid (3-column Row 1: Church, Family, Mentorship | Row 2: Nearby)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DiscoverGridCard(
                        category = DiscoverCategory.CHURCH,
                        onClick = { onOpenDiscoverCategory(DiscoverCategory.CHURCH) },
                        modifier = Modifier.weight(1f).testTag("discover_church_card")
                    )
                    DiscoverGridCard(
                        category = DiscoverCategory.FAMILY,
                        onClick = { onOpenDiscoverCategory(DiscoverCategory.FAMILY) },
                        modifier = Modifier.weight(1f).testTag("discover_family_card")
                    )
                    DiscoverGridCard(
                        category = DiscoverCategory.MENTORSHIP,
                        onClick = { onOpenDiscoverCategory(DiscoverCategory.MENTORSHIP) },
                        modifier = Modifier.weight(1f).testTag("discover_mentorship_card")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DiscoverGridCard(
                        category = DiscoverCategory.NEARBY,
                        onClick = { onOpenDiscoverCategory(DiscoverCategory.NEARBY) },
                        modifier = Modifier.weight(1f).testTag("discover_nearby_card")
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DiscoverGridCard(
    category: DiscoverCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 18.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DiscoverArch3DIcon(category = category, size = 62.dp)
            Text(
                text = category.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
