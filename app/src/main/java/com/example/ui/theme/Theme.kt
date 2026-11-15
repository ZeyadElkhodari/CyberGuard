package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.model.Language

enum class AppAppearance(val key: String) {
    CYBER_DARK("cyber_dark"),
    OLED_BLACK("oled_black"),
    MATRIX_NAVY("matrix_navy");

    companion object {
        fun fromKey(key: String?): AppAppearance = when (key) {
            "oled_black" -> OLED_BLACK
            "matrix_navy" -> MATRIX_NAVY
            else -> CYBER_DARK
        }
    }
}

enum class AppDisplayMode(val key: String) {
    STANDARD("standard"),
    COMPACT("compact"),
    LARGE("large");

    companion object {
        fun fromKey(key: String?): AppDisplayMode = when (key) {
            "compact" -> COMPACT
            "large" -> LARGE
            else -> STANDARD
        }
    }
}

val LocalDisplayMode = staticCompositionLocalOf { AppDisplayMode.STANDARD }
val LocalCyberBackground = staticCompositionLocalOf { CyberDarkBg }
val LocalCyberCard = staticCompositionLocalOf { CyberCard }
val LocalCyberCard2 = staticCompositionLocalOf { CyberCard2 }
val LocalCyberBorder = staticCompositionLocalOf { CyberBorder }

@Composable
fun CyberGuardTheme(
    lang: Language = Language.AR,
    appearance: AppAppearance = AppAppearance.CYBER_DARK,
    displayMode: AppDisplayMode = AppDisplayMode.STANDARD,
    content: @Composable () -> Unit
) {
    val (bgColor, cardColor, card2Color, borderColor) = when (appearance) {
        AppAppearance.OLED_BLACK -> Quadruple(OledBackground, OledCard, OledCard2, OledBorder)
        AppAppearance.MATRIX_NAVY -> Quadruple(MatrixBackground, MatrixCard, MatrixCard2, MatrixBorder)
        AppAppearance.CYBER_DARK -> Quadruple(CyberDarkBg, CyberCard, CyberCard2, CyberBorder)
    }

    val darkColorScheme = darkColorScheme(
        primary = CyberBlue,
        secondary = CyberPink,
        tertiary = CyberGreen,
        background = bgColor,
        surface = cardColor,
        onPrimary = Color.Black,
        onSecondary = Color.White,
        onTertiary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val currentDensity = LocalDensity.current
    val fontScaleMultiplier = when (displayMode) {
        AppDisplayMode.COMPACT -> 0.92f
        AppDisplayMode.LARGE -> 1.15f
        AppDisplayMode.STANDARD -> 1.0f
    }
    val adjustedDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * fontScaleMultiplier
    )

    CompositionLocalProvider(
        LocalDisplayMode provides displayMode,
        LocalDensity provides adjustedDensity,
        LocalCyberBackground provides bgColor,
        LocalCyberCard provides cardColor,
        LocalCyberCard2 provides card2Color,
        LocalCyberBorder provides borderColor
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme,
            typography = Typography,
            content = content
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
