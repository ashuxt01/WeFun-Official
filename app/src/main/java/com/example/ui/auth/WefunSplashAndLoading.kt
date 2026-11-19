package com.example.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.WefunBrandLogo
import com.example.ui.theme.BorderColor
import com.example.ui.theme.LightLavenderTop
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import kotlinx.coroutines.delay

@Composable
fun WefunSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(0.90f) }
    val alpha = remember { Animatable(0.35f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(380))
        scale.animateTo(1f, animationSpec = tween(460, easing = FastOutSlowInEasing))
        delay(650)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LightLavenderTop,
                        PrimaryBackground,
                        SecondaryBackground
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("wefun_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFinished
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = PrimaryBackground,
                shadowElevation = 10.dp,
                border = BorderStroke(1.5.dp, BorderColor)
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    WefunBrandLogo(
                        size = 128.dp,
                        cornerRadius = 24.dp,
                        showGoldBorder = true,
                        modifier = Modifier.testTag("splash_brand_logo")
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "WEFUN",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Connect. Play. Express. Belong.",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryPurple,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                color = PrimaryPurple,
                trackColor = BorderColor,
                modifier = Modifier
                    .width(148.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            shape = RoundedCornerShape(50),
            color = PrimaryBackground,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Text(
                text = "Live Voice Rooms • Social Party • Games",
                style = MaterialTheme.typography.labelMedium,
                color = SecondaryText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
fun WefunAppLoadingScreen(
    message: String = "Entering your WEFUN party space...",
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "loadingLogoPulse").animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LightLavenderTop, PrimaryBackground, SecondaryBackground)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("wefun_loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PrimaryBackground,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WefunBrandLogo(
                    size = 84.dp,
                    cornerRadius = 20.dp,
                    showGoldBorder = true,
                    modifier = Modifier.scale(pulse.value)
                )
                Text(
                    text = "WEFUN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
                CircularProgressIndicator(
                    color = PrimaryPurple,
                    trackColor = PinkAccent.copy(alpha = 0.2f),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PremiumGold.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Connect. Play. Express. Belong.",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
