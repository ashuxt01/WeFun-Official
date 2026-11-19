package com.example.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Immutable
data class WefunExtraColors(
    val gold: Color,
    val pink: Color,
    val cyan: Color,
    val blue: Color,
    val orange: Color,
    val purple: Color,
    val rose: Color,
    val emerald: Color,
    val warning: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val elevatedCard: Color,
    val mutedText: Color,
    val purpleGradientStart: Color,
    val purpleGradientEnd: Color,
    val isDark: Boolean,
) {
    val purpleGradientBrush: Brush
        get() = Brush.linearGradient(listOf(purpleGradientStart, purpleGradientEnd))

    val turquoiseGradientBrush: Brush
        get() = Brush.horizontalGradient(listOf(TurquoiseActive, TurquoiseCyan))

    val brandGradientBrush: Brush
        get() = Brush.horizontalGradient(listOf(TurquoiseActive, purple))

    val goldGradientBrush: Brush
        get() = Brush.horizontalGradient(listOf(gold, GoldAmber))
}

val LocalWefunColors = staticCompositionLocalOf {
    WefunExtraColors(
        gold = Color(0xFFE6A800),
        pink = PinkAccent,
        cyan = TurquoiseActive,
        blue = BlueAccent,
        orange = OrangeAccent,
        purple = PrimaryPurple,
        rose = ErrorRed,
        emerald = SuccessGreen,
        warning = WarningAmber,
        glassSurface = CardBackground,
        glassBorder = BorderColor,
        elevatedCard = SecondaryBackground,
        mutedText = SecondaryText,
        purpleGradientStart = TurquoiseActive,
        purpleGradientEnd = BlueAccent,
        isDark = false,
    )
}

private val WefunLightColorScheme = lightColorScheme(
    primary = TurquoiseActive,
    onPrimary = Color.White,
    primaryContainer = TurquoiseSoft,
    onPrimaryContainer = TurquoiseDeep,
    secondary = PrimaryPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3EEFF),
    onSecondaryContainer = DeepPurple,
    tertiary = PremiumGold,
    onTertiary = PrimaryText,
    tertiaryContainer = Color(0xFFFFF8DC),
    onTertiaryContainer = Color(0xFF7A5900),
    background = PrimaryBackground,
    onBackground = PrimaryText,
    surface = SecondaryBackground,
    onSurface = PrimaryText,
    surfaceVariant = CardBackground,
    onSurfaceVariant = SecondaryText,
    surfaceContainerLowest = PrimaryBackground,
    surfaceContainerLow = SecondaryBackground,
    surfaceContainer = SecondaryBackground,
    surfaceContainerHigh = CardBackground,
    surfaceContainerHighest = Color(0xFFF0F2F5),
    outline = BorderColor,
    outlineVariant = BorderColor,
    error = ErrorRed,
    onError = Color.White,
)

val WefunShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun WEFUNTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    WefunTheme(
        darkTheme = false, // Main app strictly enforces Pure White Light Theme
        content = content,
    )
}

@Composable
fun WefunTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val extraColors = WefunExtraColors(
        gold = Color(0xFFE6A800),
        pink = PinkAccent,
        cyan = TurquoiseActive,
        blue = BlueAccent,
        orange = OrangeAccent,
        purple = PrimaryPurple,
        rose = ErrorRed,
        emerald = SuccessGreen,
        warning = WarningAmber,
        glassSurface = CardBackground,
        glassBorder = BorderColor,
        elevatedCard = SecondaryBackground,
        mutedText = SecondaryText,
        purpleGradientStart = TurquoiseActive,
        purpleGradientEnd = BlueAccent,
        isDark = false,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    CompositionLocalProvider(LocalWefunColors provides extraColors) {
        MaterialTheme(
            colorScheme = WefunLightColorScheme,
            typography = WEFUNTypography,
            shapes = WefunShapes,
            content = content,
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
