package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

private data class SnowP(
    val x: Float,
    val y: Float,
    val r: Float,
    val speedY: Float,
    val driftX: Float,
    val a: Float
)

@Composable
fun SnowParticles(
    modifier: Modifier = Modifier,
    maxParticles: Int = 28,
    baseAlpha: Float = 0.18f
) {
    val particles = remember(maxParticles) {
        val rnd = Random(7)
        List(maxParticles) {
            SnowP(
                x = rnd.nextFloat(),
                y = rnd.nextFloat(),
                r = 0.55f + rnd.nextFloat() * 1.15f,
                speedY = 0.06f + rnd.nextFloat() * 0.12f,
                driftX = (rnd.nextFloat() - 0.5f) * 0.08f,
                a = (baseAlpha * (0.55f + rnd.nextFloat() * 0.65f)).coerceIn(0.06f, 0.40f)
            )
        }
    }

    val inf = rememberInfiniteTransition(label = "snowInf")
    val t = inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing)
        ),
        label = "snowT"
    ).value

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // “ветер” — очень лёгкий
        val wind = (t - 0.5f) * 2f

        particles.forEach { p ->
            val yy = ((p.y + t * p.speedY) % 1f) * h
            val xx = ((p.x + wind * p.driftX) % 1f) * w

            // лёгкое мерцание без дорогостоящих эффектов
            val pulse = 0.88f + 0.12f * (1f - abs(wind))
            drawCircle(
                color = Color.White.copy(alpha = (p.a * pulse).coerceIn(0f, 0.45f)),
                radius = p.r,
                center = Offset(xx, yy)
            )
        }
    }
}