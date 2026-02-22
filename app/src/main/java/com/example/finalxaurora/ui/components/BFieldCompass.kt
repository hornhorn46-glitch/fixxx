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
import kotlin.math.PI
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

    val rawAngle = atan2(bx, -bz)
    val angleDeg = (rawAngle * 180.0 / PI).toFloat()

    val animAngle by animateFloatAsState(
        targetValue = angleDeg,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.75f),
        label = "compassAngle"
    )

    GlassCard(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(170.dp)) {

            Canvas(Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val r = min(w, h) * 0.34f
                val cx = w / 2f
                val cy = h * 0.56f

                fun drawSector(spread: Float, color: androidx.compose.ui.graphics.Color, alpha: Float) {
                    drawArc(
                        color = color.copy(alpha = alpha),
                        startAngle = 90f - spread,
                        sweepAngle = spread * 2f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = 16f, cap = StrokeCap.Butt)
                    )
                }

                drawSector(spread = 65f, color = c.ok, alpha = 0.20f)
                drawSector(spread = 40f, color = c.warning, alpha = 0.18f)
                drawSector(spread = 20f, color = c.warning, alpha = 0.22f)
                drawSector(spread = 5f, color = c.danger, alpha = 0.26f)

                drawCircle(
                    color = c.glassStroke.copy(alpha = 0.60f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.5f)
                )

                val ang = Math.toRadians((animAngle - 90f).toDouble())
                val nx = cx + cos(ang).toFloat() * (r - 10f)
                val ny = cy + sin(ang).toFloat() * (r - 10f)
                drawLine(
                    color = c.accent.copy(alpha = 0.85f),
                    start = Offset(cx, cy),
                    end = Offset(nx, ny),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
                drawCircle(color = c.accent.copy(alpha = 0.90f), radius = 7f, center = Offset(cx, cy))
            }

            Text(
                text = title,
                color = c.textSecondary,
                modifier = Modifier.align(Alignment.TopCenter),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bx ${String.format("%.1f", bx)} • Bz ${String.format("%.1f", bz)}",
                color = c.textPrimary,
                modifier = Modifier.align(Alignment.BottomCenter),
                textAlign = TextAlign.Center
            )
        }
    }
}
