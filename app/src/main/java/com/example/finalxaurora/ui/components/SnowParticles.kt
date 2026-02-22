package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.random.Random

private data class SnowParticle(
    var x: Float,
    var y: Float,
    val r: Float,
    val speedY: Float,
    val driftX: Float,
    val alpha: Float
)

@Composable
fun SnowParticles(
    modifier: Modifier = Modifier,
    count: Int = 60 // было меньше — делаем заметнее
) {
    val c = LocalCosmosTheme.current.colors

    val particles = remember {
        val rnd = Random(7)
        List(count) {
            SnowParticle(
                x = rnd.nextFloat(),
                y = rnd.nextFloat(),
                r = 0.8f + rnd.nextFloat() * 1.8f,
                speedY = 0.06f + rnd.nextFloat() * 0.10f,
                driftX = (rnd.nextFloat() - 0.5f) * 0.05f,
                alpha = 0.08f + rnd.nextFloat() * 0.10f
            )
        }.toMutableList()
    }

    // лёгкая “жизнь” без тяжёлых анимаций: используем кадры Canvas (простая интеграция)
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            p.y += p.speedY * 0.9f
            p.x += p.driftX

            if (p.y > 1.1f) p.y = -0.1f
            if (p.x < -0.1f) p.x = 1.1f
            if (p.x > 1.1f) p.x = -0.1f

            drawCircle(
                color = c.textPrimary.copy(alpha = p.alpha),
                radius = p.r,
                center = Offset(p.x * w, p.y * h)
            )
        }
    }
}