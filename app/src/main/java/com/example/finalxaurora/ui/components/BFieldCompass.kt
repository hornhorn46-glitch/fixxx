package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import com.example.finalxaurora.util.Format
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BFieldCompass(
    title: String,
    bx: Double,
    bz: Double,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    // IMPORTANT:
    // Screen Y axis goes down.
    // Negative Bz should point DOWN → invert sign here.
    val vx = bx.toFloat()
    val vy = (-bz).toFloat()

    val rawAngle = atan2(vy, vx)
    val angleDeg = Math.toDegrees(rawAngle.toDouble()).toFloat()

    val animatedAngle by animateFloatAsState(
        targetValue = angleDeg,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessLow
        ),
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
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    val s = min(size.width, size.height)
                    val stroke = s * 0.05f
                    val pad = stroke * 1.2f

                    val rect = Rect(
                        left = (size.width - s) / 2f + pad,
                        top = (size.height - s) / 2f + pad,
                        right = (size.width + s) / 2f - pad,
                        bottom = (size.height + s) / 2f - pad
                    )

                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = rect.width / 2f

                    // Base circle
                    drawArc(
                        color = c.glass.copy(alpha = 0.25f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )

                    // Needle
                    val rad = Math.toRadians(animatedAngle.toDouble()).toFloat()
                    val len = radius * 0.8f
                    val end = Offset(
                        center.x + cos(rad) * len,
                        center.y + sin(rad) * len
                    )

                    drawLine(
                        color = c.accent.copy(alpha = 0.25f),
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.6f,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = c.accent,
                        start = center,
                        end = end,
                        strokeWidth = stroke * 0.25f,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = c.accent,
                        radius = stroke * 0.4f,
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