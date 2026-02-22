// app/src/main/java/com/example/finalxaurora/ui/components/Gauge.kt
package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Stable
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
    // Clamp + normalize to 0..1
    val norm = when {
        max <= min -> 0f
        else -> (((value - min) / (max - min)).toFloat()).coerceIn(0f, 1f)
    }

    val animated by animateFloatAsState(
        targetValue = norm,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "gaugeSweep"
    )

    GlassCard(
        modifier = modifier
            .widthIn(min = 88.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = LocalCosmosTheme.current.colors.textSecondary,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawGauge(
                        t = animated,
                        zones = zones,
                        invertNeedle = invertNeedle
                    )
                }

                // Value text a bit below center (premium look)
                Text(
                    text = valueText,
                    color = LocalCosmosTheme.current.colors.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 22.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun DrawScope.drawGauge(
    t: Float,
    zones: List<GaugeZone>,
    invertNeedle: Boolean
) {
    val c = LocalCosmosTheme.current.colors
    val sizeMin = min(size.width, size.height)

    val strokeOuter = sizeMin * 0.085f
    val strokeInner = sizeMin * 0.050f

    val pad = strokeOuter * 1.35f
    val rect = Rect(
        left = (size.width - sizeMin) / 2f + pad,
        top = (size.height - sizeMin) / 2f + pad,
        right = (size.width + sizeMin) / 2f - pad,
        bottom = (size.height + sizeMin) / 2f - pad
    )

    // Gauge arc settings (semi-ish: from 210° to -30°)
    val startAngle = 210f
    val sweepTotal = 240f

    // Subtle background arc
    drawArc(
        color = c.glass.copy(alpha = 0.20f),
        startAngle = startAngle,
        sweepAngle = sweepTotal,
        useCenter = false,
        topLeft = rect.topLeft,
        size = rect.size,
        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
    )

    // Zones
    for (z in zones) {
        val zs = z.start.coerceIn(0f, 1f)
        val ze = z.end.coerceIn(0f, 1f)
        if (ze <= zs) continue

        drawArc(
            color = z.color.copy(alpha = 0.85f),
            startAngle = startAngle + sweepTotal * zs,
            sweepAngle = sweepTotal * (ze - zs),
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = strokeInner, cap = StrokeCap.Round)
        )
    }

    // Needle
    val needleT = if (invertNeedle) 1f - t else t
    val angleDeg = startAngle + sweepTotal * needleT
    val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()

    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = rect.width / 2f

    val needleLen = radius * 0.78f
    val needleEnd = Offset(
        x = center.x + cos(angleRad) * needleLen,
        y = center.y + sin(angleRad) * needleLen
    )

    // Needle glow (soft)
    drawLine(
        color = c.accent.copy(alpha = 0.20f),
        start = center,
        end = needleEnd,
        strokeWidth = strokeOuter * 0.55f,
        cap = StrokeCap.Round
    )

    // Needle core
    drawLine(
        color = c.accent.copy(alpha = 0.95f),
        start = center,
        end = needleEnd,
        strokeWidth = strokeOuter * 0.26f,
        cap = StrokeCap.Round
    )

    // Center hub
    drawCircle(
        color = c.textPrimary.copy(alpha = 0.22f),
        radius = strokeOuter * 0.55f,
        center = center
    )
    drawCircle(
        color = c.accent.copy(alpha = 0.90f),
        radius = strokeOuter * 0.28f,
        center = center
    )
}