package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    var x: Float,
    var y: Float,
    val r: Float,
    val speed: Float,
    val wind: Float,
    val phase: Float
)

@Composable
fun SnowParticles(
    modifier: Modifier = Modifier,
    maxParticles: Int
) {
    val c = LocalCosmosTheme.current.colors
    val particles = remember {
        val rnd = Random(1337)
        List(maxParticles) {
            Particle(
                x = rnd.nextFloat(),
                y = rnd.nextFloat(),
                r = 0.8f + rnd.nextFloat() * 1.6f,
                speed = 0.015f + rnd.nextFloat() * 0.030f,
                wind = -0.020f + rnd.nextFloat() * 0.040f,
                phase = rnd.nextFloat() * (2f * PI.toFloat())
            )
        }
    }

    val infinite = androidx.compose.animation.core.rememberInfiniteTransition(label = "snowInf")
    val t = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(16_000)
        ),
        label = "snowT"
    ).value

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val sway = (sin(p.phase + t * 2f * PI.toFloat()) * 0.5f + 0.5f).toFloat()
            val dx = p.wind * (0.6f + 0.8f * sway)

            p.y += p.speed * 0.9f
            p.x += dx * 0.5f

            if (p.y > 1.05f) p.y = -0.05f
            if (p.x < -0.05f) p.x = 1.05f
            if (p.x > 1.05f) p.x = -0.05f

            val alpha = 0.06f + 0.10f * sway
            drawCircle(
                color = c.textPrimary.copy(alpha = alpha),
                radius = p.r,
                center = Offset(p.x * w, p.y * h)
            )
        }
    }
}
