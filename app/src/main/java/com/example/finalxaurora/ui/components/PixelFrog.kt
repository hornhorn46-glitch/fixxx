package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PixelFrog(
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "frogInf")
    val bob = inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    ).value

    Canvas(
        modifier = modifier
            .size(44.dp)
            .offset { IntOffset(0, (bob * 3.5f).roundToInt()) }
    ) {
        val s = size.minDimension
        if (s <= 0f) return@Canvas
        val px = s / 12f

        fun dot(x: Int, y: Int, alpha: Float) {
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = px * 0.62f,
                center = Offset((x + 0.5f) * px, (y + 0.5f) * px)
            )
        }

        // контур
        for (x in 3..8) dot(x, 6, 0.42f)
        for (x in 4..7) dot(x, 7, 0.42f)
        dot(4, 5, 0.42f); dot(7, 5, 0.42f)

        // глаза
        dot(5, 5, 0.18f); dot(6, 5, 0.18f)

        // тело
        dot(5, 6, 0.28f); dot(6, 6, 0.28f)
    }
}