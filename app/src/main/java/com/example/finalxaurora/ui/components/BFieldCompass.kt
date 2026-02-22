package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            HeaderRow(
                title = title,
                value = "Bx ${Format.oneDecOrDash(bx)}   Bz ${Format.oneDecOrDash(bz)}",
                valueColor = c.textSecondary
            )

            Spacer(Modifier.height(10.dp))

            // Вектор: X вправо, Z вниз при отрицательном.
            // То есть рисуем y = -bz (чтобы отрицательный Bz был вниз на экране).
            val vx = bx.toFloat()
            val vy = (-bz).toFloat()

            val targetAngle = atan2(vy, vx) // radians, 0 -> вправо

            val angle by animateFloatAsState(
                targetValue = targetAngle,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.78f),
                label = "compassAngle"
            )

            Canvas(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                val r = min(size.width, size.height) * 0.42f
                val cx = size.width / 2f
                val cy = size.height / 2f

                // ring
                drawCircle(
                    color = c.textSecondary.copy(alpha = 0.18f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.2f)
                )

                // Sector zones around "down" direction (negative Bz => down)
                // Сектора по углу от "вниз" (90deg) +/- thresholds.
                // Красн ±5°, оранж ±20°, жёлт ±40°, зел ±65°
                fun degToRad(d: Float) = (d * Math.PI.toFloat() / 180f)
                val down = (Math.PI.toFloat() / 2f) // вниз в системе Canvas (x вправо, y вниз)

                fun drawSector(spanDeg: Float, color: androidx.compose.ui.graphics.Color, alpha: Float) {
                    val start = down - degToRad(spanDeg)
                    val sweep = degToRad(spanDeg * 2f)
                    drawArc(
                        color = color.copy(alpha = alpha),
                        startAngle = (start * 180f / Math.PI.toFloat()),
                        sweepAngle = (sweep * 180f / Math.PI.toFloat()),
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(width = r * 0.24f, cap = StrokeCap.Round)
                    )
                }

                drawSector(65f, c.ok, 0.10f)
                drawSector(40f, c.warning, 0.12f)
                drawSector(20f, c.warning, 0.14f)
                drawSector(5f, c.danger, 0.18f)

                // needle
                val nx = cx + cos(angle) * (r * 0.92f)
                val ny = cy + sin(angle) * (r * 0.92f)

                drawLine(
                    color = c.accent.copy(alpha = 0.95f),
                    start = Offset(cx, cy),
                    end = Offset(nx, ny),
                    strokeWidth = 6.0f,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = c.glass.copy(alpha = 0.30f),
                    radius = r * 0.10f,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}