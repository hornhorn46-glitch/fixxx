package com.example.finalxaurora.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.finalxaurora.domain.AppMode

@Immutable
data class CosmosThemeState(
    val colors: CosmosColors
)

val LocalCosmosTheme = compositionLocalOf {
    CosmosThemeState(colors = ThemeTokens.colorsFor(modeIsSun = false, auroraScore = 0))
}

@Composable
fun CosmosTheme(
    mode: AppMode,
    auroraScore: Int,
    content: @Composable () -> Unit
) {
    val isSun = mode == AppMode.SUN

    val t by animateFloatAsState(
        targetValue = (auroraScore.coerceIn(0, 100)) / 100f,
        animationSpec = tween(durationMillis = 900),
        label = "auroraScoreT"
    )

    val colors = ThemeTokens.colorsFor(modeIsSun = isSun, auroraScore = (t * 100).toInt())

    val scheme = darkColorScheme(
        primary = colors.accent,
        secondary = colors.accentSoft,
        background = Color.Black,
        surface = Color.Black,
        onPrimary = Color.Black,
        onSecondary = colors.textPrimary,
        onBackground = colors.textPrimary,
        onSurface = colors.textPrimary
    )

    androidx.compose.runtime.CompositionLocalProvider(
        LocalCosmosTheme provides CosmosThemeState(colors = colors)
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}
