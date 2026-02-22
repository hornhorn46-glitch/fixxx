package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class GaugeZone(
    val start: Float, // 0..1
    val end: Float,   // 0..1
    val color: Color
)

@Composable
fun PremiumGauge(
    title: String,
    valueText: String,
    value: Double,
    min: Double,
    max: Double,
    zones: List<GaugeZone>,
    modifier: Modifier = Modifier,
    invertNeedle: Boolean = false
) {
    val c = LocalCosmosTheme.current.colors

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(text = title, color = c.textSecondary)
            Spacer(Modifier.height(6.dp))

            // ВАЖНО: отображаем ровно то, что передал экран (никаких "${...}" тут не должно быть)
            Text(
                text = valueText,
                color = c.textPrimary,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val w = size.width
                    val h = size.height
                    val stroke = 10.dp.toPx()
                    val radius = min(w, h) / 2f - stroke
                    val topLeft = Offset((w - 2 * radius) / 2f, (h - 2 * radius) / 2f)
                    val arcSize = Size(2 * radius, 2 * radius)

                    // дуга: от 210° до -30° (240° сектор)
                    val startAngle = 210f
                    val sweepTotal = 240f

                    fun clamp01(x: Float) = x.coerceIn(0f, 1f)

                    // зоны
                    for (z in zones) {
                        val s = clamp01(z.start)
                        val e = clamp01(z.end)
                        val sweep = (e - s) * sweepTotal
                        drawArc(
                            color = z.color.copy(alpha = 0.95f),
                            startAngle = startAngle + s * sweepTotal,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    // нормализуем значение
                    val norm = if (max == min) 0f else ((value - min) / (max - min)).toFloat().coerceIn(0f, 1f)
                    val needleNorm = if (invertNeedle) (1f - norm) else norm
                    val angleDeg = startAngle + needleNorm * sweepTotal
                    val angleRad = Math.toRadians(angleDeg.toDouble())

                    val cx = w / 2f
                    val cy = h / 2f
                    val needleLen = radius * 0.75f

                    val endX = cx + (cos(angleRad) * needleLen).toFloat()
                    val endY = cy + (sin(angleRad) * needleLen).toFloat()

                    // игла
                    drawLine(
                        color = c.accent,
                        start = Offset(cx, cy),
                        end = Offset(endX, endY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // центр
                    drawCircle(
                        color = c.accent,
                        radius = 5.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }
}