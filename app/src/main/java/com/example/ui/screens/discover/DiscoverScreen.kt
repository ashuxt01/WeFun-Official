package com.example.ui.screens.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.DiscoverCategory
import com.example.ui.discover.DiscoverSocialScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun DiscoverScreen(
    onOpenMoments: () -> Unit = {},
    onOpenDiscoverCategory: (DiscoverCategory) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        DiscoverSocialScreen(
            onOpenMoments = onOpenMoments,
            onOpenDiscoverCategory = onOpenDiscoverCategory
        )
    }
}
