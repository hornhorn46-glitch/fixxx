package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

private data class SnowParticle(
    val x0: Float,
    val y0: Float,
    val r: Float,
    val a: Float,
    val speed: Float,
    val wind: Float
)

@Composable
fun SnowParticles(
    modifier: Modifier = Modifier,
    maxParticles: Int = 40
) {
    val seed = rememberParticles(maxParticles)
    val inf = rememberInfiniteTransition(label = "snowInf")

    val t by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 1f || h <= 1f) return@Canvas

        seed.forEach { p ->
            val y = ((p.y0 + (t * p.speed)) % 1f) * h
            val windPhase = (t * 2f - 1f)
            val x = ((p.x0 + windPhase * p.wind) % 1f) * w

            drawCircle(
                color = Color.White.copy(alpha = (p.a * 0.35f).coerceIn(0.02f, 0.22f)),
                radius = p.r,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

@Composable
private fun rememberParticles(count: Int): List<SnowParticle> {
    // стабильный набор частиц на время жизни композиции
    val r = Random(42)
    return List(count.coerceIn(8, 40)) {
        val x0 = r.nextFloat().coerceIn(0.02f, 0.98f)
        val y0 = r.nextFloat().coerceIn(0.02f, 0.98f)
        val rad = (1.0f + r.nextFloat() * 2.2f)
        val alpha = (0.10f + r.nextFloat() * 0.30f)
        val speed = (0.35f + r.nextFloat() * 0.55f) // доля экрана за цикл
        val wind = (0.010f + r.nextFloat() * 0.030f) * (if (r.nextBoolean()) 1f else -1f)
        SnowParticle(x0, y0, rad, alpha, speed, wind)
    }.sortedBy { abs(it.wind) }
}