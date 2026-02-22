package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ModeToggle(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    large: Boolean = false
) {
    val c = LocalCosmosTheme.current.colors

    val size: Dp = if (large) 46.dp else 38.dp
    val borderW: Dp = if (large) 1.6.dp else 1.2.dp

    Box(
        modifier = Modifier
            .size(size)
            .border(borderW, c.textSecondary.copy(alpha = 0.45f), CircleShape)
            .clickable {
                onToggle(if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH)
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = this.size.width
            val h = this.size.height
            val r = min(w, h) * 0.38f
            val cx = w * 0.5f
            val cy = h * 0.5f

            // фон-кружок (слегка "стекло")
            drawCircle(
                color = c.glass.copy(alpha = 0.28f),
                radius = r * 1.35f,
                center = Offset(cx, cy)
            )

            if (mode == AppMode.SUN) {
                // SUN: круг + лучики
                drawCircle(
                    color = c.accent.copy(alpha = 0.90f),
                    radius = r * 0.78f,
                    center = Offset(cx, cy)
                )

                val rays = 8
                val outer = r * 1.15f
                val inner = r * 0.92f
                val stroke = (r * 0.22f).coerceAtLeast(2.2f)

                for (i in 0 until rays) {
                    val a = (2.0 * PI * i / rays)
                    val x1 = (cx + cos(a) * inner).toFloat()
                    val y1 = (cy + sin(a) * inner).toFloat()
                    val x2 = (cx + cos(a) * outer).toFloat()
                    val y2 = (cy + sin(a) * outer).toFloat()
                    drawLine(
                        color = c.accent.copy(alpha = 0.90f),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            } else {
                // EARTH: "планета" + простая дуга-океан
                drawCircle(
                    color = c.ok.copy(alpha = 0.92f),
                    radius = r * 0.88f,
                    center = Offset(cx, cy)
                )

                // дуга
                drawArc(
                    color = c.textPrimary.copy(alpha = 0.20f),
                    startAngle = 210f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(cx - r * 0.88f, cy - r * 0.88f),
                    size = androidx.compose.ui.geometry.Size(r * 1.76f, r * 1.76f),
                    style = Stroke(width = (r * 0.20f).coerceAtLeast(2.0f))
                )
            }
        }
    }
}