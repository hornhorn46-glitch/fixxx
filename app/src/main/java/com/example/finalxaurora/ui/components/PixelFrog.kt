package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
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

    // Если у тебя лягушка была через painterResource(R.drawable.xxx) — оставь как было.
    // Здесь я оставляю заглушку на случай, если ресурс уже есть: R.drawable.pixel_frog
    Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.finalxaurora.R.drawable.pixel_frog),
        contentDescription = null,
        modifier = modifier.offset { IntOffset(0, (bob * 2.5f).roundToInt()) }
    )
}