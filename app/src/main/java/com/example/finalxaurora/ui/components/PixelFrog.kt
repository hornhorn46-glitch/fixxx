package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.sin

@Composable
fun PixelFrog(modifier: Modifier = Modifier) {
    val c = LocalCosmosTheme.current.colors
    val inf = rememberInfiniteTransition(label = "frogInf")
    val t by inf.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing)),
        label = "frogT"
    )

    val bounce = (sin(t).toFloat() * 2.5f)

    Canvas(modifier = modifier.size(34.dp)) {
        val px = size.minDimension / 10f
        val baseY = size.height / 2f + bounce
        fun p(x: Int, y: Int) = Offset(x * px + px, baseY + y * px)

        val body = c.accent.copy(alpha = 0.55f)
        val eye = c.textPrimary.copy(alpha = 0.75f)

        drawRect(body, topLeft = p(2, 0), size = androidx.compose.ui.geometry.Size(px * 6, px * 4))
        drawRect(body, topLeft = p(1, 2), size = androidx.compose.ui.geometry.Size(px * 8, px * 4))
        drawRect(eye, topLeft = p(3, 1), size = androidx.compose.ui.geometry.Size(px, px))
        drawRect(eye, topLeft = p(6, 1), size = androidx.compose.ui.geometry.Size(px, px))
    }
}
