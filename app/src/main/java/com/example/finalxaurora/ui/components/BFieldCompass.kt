package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import com.example.finalxaurora.util.Format
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun BFieldCompass(
    title: String,
    bx: Double,
    bz: Double,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    GlassCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            if (title.isNotBlank()) {
                Text(text = title, color = c.textSecondary)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                    val w = size.width
                    val h = size.height
                    val r = min(w, h) * 0.40f
                    val cx = w * 0.50f
                    val cy = h * 0.52f

                    // Основа - чуть ярче
                    drawCircle(
                        color = c.glass.copy(alpha = 0.34f),
                        radius = r * 1.25f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = c.textPrimary.copy(alpha = 0.20f),
                        radius = r * 1.25f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.4f)
                    )

                    // зоны вокруг "вниз" (компас риска по Bz)
                    val down = 90f
                    drawArc(
                        color = c.ok.copy(alpha = 0.18f),
                        startAngle = down - 65f,
                        sweepAngle = 130f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                        size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                    )
                    drawArc(
                        color = c.warning.copy(alpha = 0.22f),
                        startAngle = down - 40f,
                        sweepAngle = 80f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                        size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                    )
                    drawArc(
                        color = c.danger.copy(alpha = 0.26f),
                        startAngle = down - 20f,
                        sweepAngle = 40f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                        size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                    )

                    // Вектор: X по горизонтали, Z по вертикали
                    // Требование: отрицательный Bz должен указывать вниз => vy = -bz
                    val vx = bx.toFloat()
                    val vy = (-bz).toFloat()
                    val mag = hypot(vx.toDouble(), vy.toDouble()).toFloat().coerceAtLeast(1e-3f)

                    val nx = vx / mag
                    val ny = vy / mag

                    val needleLen = r * 1.15f
                    val ex = cx + nx * needleLen
                    val ey = cy + ny * needleLen

                    // Свечение + основная линия (сильно ярче, чем было)
                    drawLine(
                        color = c.accent.copy(alpha = 0.30f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 18f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = c.accent.copy(alpha = 0.98f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 7.4f,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = c.accent.copy(alpha = 0.98f),
                        radius = 8.2f,
                        center = Offset(ex, ey)
                    )
                    drawCircle(
                        color = c.textPrimary.copy(alpha = 0.20f),
                        radius = 5.0f,
                        center = Offset(cx, cy)
                    )
                }
            }

            Text(
                text = "Bx ${Format.oneDecOrDash(bx)} nT   •   Bz ${Format.oneDecOrDash(bz)} nT",
                color = c.textSecondary
            )
        }
    }
}