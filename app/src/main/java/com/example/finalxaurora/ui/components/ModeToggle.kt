package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ModeToggle(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    val size: Dp = if (large) 46.dp else 34.dp
    val borderW: Dp = if (large) 2.dp else 1.5.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(c.glass.copy(alpha = 0.22f))
            .border(borderW, c.accent.copy(alpha = 0.65f), CircleShape)
            .clickable {
                val next = if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH
                onToggle(next)
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = min(size.width, size.height)
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = s * 0.30f

            // subtle inner glow
            drawCircle(
                color = c.accent.copy(alpha = 0.10f),
                radius = r * 1.55f,
                center = Offset(cx, cy)
            )

            if (mode == AppMode.EARTH) {
                // Earth: circle + a couple of "continents"
                drawCircle(
                    color = c.textPrimary.copy(alpha = 0.10f),
                    radius = r * 1.25f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = c.textPrimary.copy(alpha = 0.35f),
                    radius = r * 1.25f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.4f)
                )

                // continents (simple blobs)
                drawCircle(
                    color = c.accent.copy(alpha = 0.55f),
                    radius = r * 0.38f,
                    center = Offset(cx - r * 0.35f, cy - r * 0.10f)
                )
                drawCircle(
                    color = c.accent.copy(alpha = 0.45f),
                    radius = r * 0.30f,
                    center = Offset(cx + r * 0.25f, cy + r * 0.18f)
                )
            } else {
                // Sun: circle + rays
                drawCircle(
                    color = c.accent.copy(alpha = 0.22f),
                    radius = r * 1.05f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = c.accent.copy(alpha = 0.85f),
                    radius = r * 1.05f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.4f)
                )

                val rays = 8
                val inner = r * 1.28f
                val outer = r * 1.62f
                for (i in 0 until rays) {
                    val a = (i.toFloat() / rays.toFloat()) * (Math.PI.toFloat() * 2f)
                    val dx = cos(a)
                    val dy = sin(a)
                    val p1 = Offset(cx + dx * inner, cy + dy * inner)
                    val p2 = Offset(cx + dx * outer, cy + dy * outer)
                    drawLine(
                        color = c.accent.copy(alpha = 0.75f),
                        start = p1,
                        end = p2,
                        strokeWidth = 3.6f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}