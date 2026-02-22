package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class GaugeZone(val startT: Float, val endT: Float, val color: androidx.compose.ui.graphics.Color)

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

    val rawT = ((value - min) / (max - min)).toFloat().coerceIn(0f, 1f)
    val tTarget = if (invertNeedle) (1f - rawT) else rawT

    val t by animateFloatAsState(
        targetValue = tTarget,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.75f),
        label = "gaugeT"
    )

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            androidx.compose.material3.Text(text = title, color = c.textSecondary)
            androidx.compose.material3.Text(text = valueText, color = c.textPrimary)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 6.dp)
            ) {
                Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                    val w = size.width
                    val h = size.height
                    val r = minOf(w, h) * 0.42f
                    val center = Offset(w * 0.5f, h * 0.60f)

                    val startDeg = 200f
                    val sweepDeg = 220f

                    // зоны
                    zones.forEach { z ->
                        drawArc(
                            color = z.color.copy(alpha = 0.55f),
                            startAngle = startDeg + sweepDeg * z.startT,
                            sweepAngle = sweepDeg * (z.endT - z.startT),
                            useCenter = false,
                            topLeft = Offset(center.x - r, center.y - r),
                            size = androidx.compose.ui.geometry.Size(r * 2f, r * 2f),
                            style = Stroke(width = r * 0.20f, cap = StrokeCap.Round)
                        )
                    }

                    // нейтральный трек
                    drawArc(
                        color = c.glassStroke.copy(alpha = 0.35f),
                        startAngle = startDeg,
                        sweepAngle = sweepDeg,
                        useCenter = false,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = androidx.compose.ui.geometry.Size(r * 2f, r * 2f),
                        style = Stroke(width = r * 0.10f, cap = StrokeCap.Round)
                    )

                    // “стрелка”
                    val ang = (startDeg + sweepDeg * t) * (PI / 180.0)
                    val nx = cos(ang).toFloat()
                    val ny = sin(ang).toFloat()
                    val end = Offset(center.x + nx * r * 0.95f, center.y + ny * r * 0.95f)

                    drawLine(
                        color = c.accent.copy(alpha = 0.95f),
                        start = center,
                        end = end,
                        strokeWidth = r * 0.06f,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = c.accent.copy(alpha = 0.90f),
                        radius = r * 0.10f,
                        center = center
                    )
                }
            }
        }
    }
}