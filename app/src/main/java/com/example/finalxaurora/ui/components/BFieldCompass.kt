// app/src/main/java/com/example/finalxaurora/ui/components/BFieldCompass.kt
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
import androidx.compose.runtime.Stable
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
import com.example.finalxaurora.util.Format
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Stable
private data class Sector(val startDeg: Float, val sweepDeg: Float, val color: Color)

@Composable
fun BFieldCompass(
    title: String,
    bx: Double,
    bz: Double,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    // Angle where "down" (southward Bz, negative) is considered the "good" direction.
    // We map vector (bx, bz) to screen: x = bx, y = -bz (so negative Bz points DOWN).
    val vx = bx.toFloat()
    val vy = (-bz).toFloat()

    val rawAngle = atan2(vy, vx) // radians, 0 = right, pi/2 = down
    val angleDeg = Math.toDegrees(rawAngle.toDouble()).toFloat()

    val animated by animateFloatAsState(
        targetValue = angleDeg,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
        label = "bfieldNeedle"
    )

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = c.textPrimary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.75f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                    val sMin = min(size.width, size.height)
                    val stroke = sMin * 0.045f
                    val pad = stroke * 1.3f
                    val rect = Rect(
                        left = (size.width - sMin) / 2f + pad,
                        top = (size.height - sMin) / 2f + pad,
                        right = (size.width + sMin) / 2f - pad,
                        bottom = (size.height + sMin) / 2f - pad
                    )
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = rect.width / 2f

                    // Danger sectors around "down" direction.
                    // In our coordinate system, "down" is angle 90°.
                    val sectors = listOf(
                        Sector(90f - 5f, 10f, c.danger.copy(alpha = 0.55f)),
                        Sector(90f - 20f, 40f, c.warning.copy(alpha = 0.35f)),
                        Sector(90f - 40f, 80f, c.warning.copy(alpha = 0.22f)),
                        Sector(90f - 65f, 130f, c.ok.copy(alpha = 0.18f))
                    )

                    // Base ring
                    drawArc(
                        color = c.glass.copy(alpha = 0.20f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )

                    // Sectors (draw from widest to narrowest so center is more “intense”)
                    for (sec in sectors.reversed()) {
                        drawArc(
                            color = sec.color,
                            startAngle = sec.startDeg,
                            sweepAngle = sec.sweepDeg,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = rect.size,
                            style = Stroke(width = stroke * 0.80f, cap = StrokeCap.Round)
                        )
                    }

                    // Needle: rotate around center
                    val a = Math.toRadians(animated.toDouble()).toFloat()
                    val len = r * 0.78f
                    val end = Offset(
                        x = center.x + cos(a) * len,
                        y = center.y + sin(a) * len
                    )

                    // Glow
                    drawLine(
                        color = c.accent.copy(alpha = 0.22f),
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.65f,
                        cap = StrokeCap.Round
                    )
                    // Core
                    drawLine(
                        color = c.accent.copy(alpha = 0.95f),
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.28f,
                        cap = StrokeCap.Round
                    )

                    // Hub
                    drawCircle(
                        color = c.textPrimary.copy(alpha = 0.22f),
                        radius = stroke * 0.55f,
                        center = center
                    )
                    drawCircle(
                        color = c.accent.copy(alpha = 0.9f),
                        radius = stroke * 0.28f,
                        center = center
                    )
                }

                Text(
                    text = "Bx ${Format.oneDecOrDash(bx)} · Bz ${Format.oneDecOrDash(bz)}",
                    color = c.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}