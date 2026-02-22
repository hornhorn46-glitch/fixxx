package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val bob by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(
        modifier = modifier
            .size(36.dp) // ключевое: иначе Canvas = 0x0 и “лягушки нет”
            .offset { IntOffset(0, (bob * 2.5f).roundToInt()) }
    ) {
        val s = size.minDimension
        if (s <= 0f) return@Canvas

        val px = s / 12f
        fun dot(x: Int, y: Int, alpha: Float) {
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = px * 0.55f,
                center = Offset((x + 0.5f) * px, (y + 0.5f) * px)
            )
        }

        for (x in 3..8) dot(x, 6, 0.22f)
        for (x in 4..7) dot(x, 7, 0.22f)
        dot(4, 5, 0.22f); dot(7, 5, 0.22f)
        dot(5, 5, 0.12f); dot(6, 5, 0.12f)
    }
}