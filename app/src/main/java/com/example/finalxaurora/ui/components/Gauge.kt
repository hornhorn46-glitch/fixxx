package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PremiumGauge(
    title: String,
    valueText: String,
    value: Double,
    min: Double,
    max: Double,
    zones: List<GaugeZone>,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val pct = ((value - min) / (max - min)).toFloat().coerceIn(0f, 1f)

    val sweep by animateFloatAsState(
        targetValue = pct,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.78f),
        label = "gaugeSweep"
    )

    GlassCard(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(150.dp)) {
            Canvas(Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val r = min(w, h) * 0.42f
                val cx = w / 2f
                val cy = h * 0.56f

                val start = 210f
                val total = 240f

                zones.forEach { z ->
                    val a0 = start + total * z.from
                    val a1 = total * (z.to - z.from)
                    drawArc(
                        color = z.color,
                        startAngle = a0,
                        sweepAngle = a1,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = 14f, cap = StrokeCap.Round),
                        alpha = 0.55f
                    )
                }

                drawArc(
                    color = c.glassStroke.copy(alpha = 0.55f),
                    startAngle = start,
                    sweepAngle = total,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )

                drawArc(
                    color = c.accent.copy(alpha = 0.90f),
                    startAngle = start,
                    sweepAngle = total * sweep,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )

                val angle = Math.toRadians((start + total * sweep).toDouble())
                val nx = cx + cos(angle).toFloat() * (r - 8f)
                val ny = cy + sin(angle).toFloat() * (r - 8f)
                drawLine(
                    color = c.accent.copy(alpha = 0.75f),
                    start = Offset(cx, cy),
                    end = Offset(nx, ny),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawCircle(color = c.accent.copy(alpha = 0.85f), radius = 6f, center = Offset(cx, cy))
            }

            Text(
                text = title,
                color = c.textSecondary,
                modifier = Modifier.align(Alignment.TopCenter),
                textAlign = TextAlign.Center
            )

            Text(
                text = valueText,
                color = c.textPrimary,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class GaugeZone(
    val from: Float,
    val to: Float,
    val color: androidx.compose.ui.graphics.Color
)
