package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.sin

@Composable
fun AuroraBackground(
    mode: AppMode,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val isSun = mode == AppMode.SUN

    val infinite = rememberInfiniteTransition(label = "bgInf")

    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI.toFloat() * 2f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    val modeT by animateFloatAsState(
        targetValue = if (isSun) 1f else 0f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "modeT"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(c.bgTop, c.bgMid, c.bgBottom)
                )
            )

            val alphaWave = (0.35f + 0.25f * sin(phase)).coerceIn(0.10f, 0.65f)

            val bandBrush = Brush.linearGradient(
                colors = listOf(
                    c.accentSoft.copy(alpha = 0.00f),
                    c.accentSoft.copy(alpha = alphaWave),
                    c.accentSoft.copy(alpha = 0.00f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )

            val y1 = size.height * (0.22f + 0.10f * sin(phase * 0.70f))
            val y2 = size.height * (0.55f + 0.08f * sin(phase * 0.90f))

            rotate(degrees = -12f + 7f * modeT) {
                drawRect(
                    brush = bandBrush,
                    topLeft = Offset(
                        x = -size.width * 0.20f + size.width * 0.10f * drift,
                        y = y1
                    ),
                    size = Size(size.width * 1.40f, size.height * 0.18f),
                    alpha = 0.90f
                )

                drawRect(
                    brush = bandBrush,
                    topLeft = Offset(
                        x = -size.width * 0.25f + size.width * 0.12f * (1f - drift),
                        y = y2
                    ),
                    size = Size(size.width * 1.50f, size.height * 0.16f),
                    alpha = 0.75f
                )
            }
        }

        SnowParticles(
            modifier = Modifier.fillMaxSize(),
            maxParticles = 40
        )
    }
}