// app/src/main/java/com/example/finalxaurora/ui/components/Gauge.kt
package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Immutable
data class GaugeZone(
    val start: Float, // 0..1
    val end: Float,   // 0..1
    val color: Color
)

/**
 * PremiumGauge
 * - invertNeedle: flips normalized value 0..1 -> 1..0 (useful for Bz when you want "negative" to point down/other way)
 */
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

    val norm0 = if (max == min) 0f else ((value - min) / (max - min)).toFloat().coerceIn(0f, 1f)
    val norm = if (invertNeedle) (1f - norm0) else norm0

    val sweep by animateFloatAsState(
        targetValue = norm,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessLow),
        label = "gaugeSweep"
    )

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = c.textPrimary,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                    val s = min(size.width, size.height)
                    val stroke = s * 0.07f
                    val pad = stroke * 1.4f
                    val rect = Rect(
                        left = (size.width - s) / 2f + pad,
                        top = (size.height - s) / 2f + pad,
                        right = (size.width + s) / 2f - pad,
                        bottom = (size.height + s) / 2f - pad
                    )

                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = rect.width / 2f

                    // Gauge arc: from 210° to -30° (i.e. 300° sweep)
                    val startAngle = 210f
                    val totalSweep = 300f

                    // Base arc
                    drawArc(
                        color = c.glass.copy(alpha = 0.22f),
                        startAngle = startAngle,
                        sweepAngle = totalSweep,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )

                    // Zones
                    zones.forEach { z ->
                        val a0 = startAngle + totalSweep * z.start
                        val a1 = startAngle + totalSweep * (z.end - z.start)
                        drawArc(
                            color = z.color.copy(alpha = 0.65f),
                            startAngle = a0,
                            sweepAngle = a1,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = rect.size,
                            style = Stroke(width = stroke * 0.80f, cap = StrokeCap.Round)
                        )
                    }

                    // Needle
                    val needleAngle = startAngle + totalSweep * sweep
                    val rad = (needleAngle * (PI.toFloat() / 180f))
                    val len = r * 0.78f
                    val end = Offset(center.x + cos(rad) * len, center.y + sin(rad) * len)

                    // Glow + core
                    drawLine(
                        color = c.accent.copy(alpha = 0.22f),
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.55f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = c.accent.copy(alpha = 0.92f),
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.22f,
                        cap = StrokeCap.Round
                    )

                    // Hub
                    drawCircle(
                        color = c.textPrimary.copy(alpha = 0.18f),
                        radius = stroke * 0.55f,
                        center = center
                    )
                    drawCircle(
                        color = c.accent.copy(alpha = 0.9f),
                        radius = stroke * 0.28f,
                        center = center
                    )
                }

                // Value lower than center
                Text(
                    text = valueText,
                    color = c.textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 44.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}