package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import com.example.finalxaurora.util.Format
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
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
        Column(Modifier.padding(12.dp)) {
            if (title.isNotBlank()) {
                androidx.compose.material3.Text(text = title, color = c.textSecondary)
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

                    // стекло ярче
                    drawCircle(
                        color = c.glass.copy(alpha = 0.30f),
                        radius = r * 1.25f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = c.textSecondary.copy(alpha = 0.18f),
                        radius = r * 1.25f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.2f)
                    )

                    // зоны вокруг "вниз" (отрицательный Bz вниз)
                    fun sector(alpha: Float, startDeg: Float, sweepDeg: Float) {
                        drawArc(
                            color = c.danger.copy(alpha = alpha),
                            startAngle = startDeg,
                            sweepAngle = sweepDeg,
                            useCenter = true,
                            topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                            size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                        )
                    }

                    // Упрощённо: подкраска по окружности около направления "вниз"
                    // Красный узко, оранжевый шире, зелёный ещё шире.
                    // (Если ты хочешь ровно как ранее — скажи, верну точные углы.)
                    // 0° — вправо, 90° — вниз (в Canvas Compose это обычно так),
                    // поэтому "вниз" = 90°.
                    val down = 90f
                    drawArc(
                        color = c.ok.copy(alpha = 0.16f),
                        startAngle = down - 65f,
                        sweepAngle = 130f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                        size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                    )
                    drawArc(
                        color = c.warning.copy(alpha = 0.18f),
                        startAngle = down - 40f,
                        sweepAngle = 80f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 1.25f, cy - r * 1.25f),
                        size = androidx.compose.ui.geometry.Size(r * 2.5f, r * 2.5f)
                    )
                    drawArc(
                        color = c.danger.copy(alpha = 0.20f),
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

                    // свечения/яркость стрелки
                    drawLine(
                        color = c.accent.copy(alpha = 0.25f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 14f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = c.accent.copy(alpha = 0.92f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 6.2f,
                        cap = StrokeCap.Round
                    )

                    // маленькая “головка”
                    drawCircle(
                        color = c.accent.copy(alpha = 0.95f),
                        radius = 7.5f,
                        center = Offset(ex, ey)
                    )

                    // подпись
                    val angle = atan2(ny, nx)
                    val angleDeg = (angle * 180f / Math.PI.toFloat())
                    drawCircle(color = c.textSecondary.copy(alpha = 0.22f), radius = 4.5f, center = Offset(cx, cy))

                    // текст снизу (без лишних зависимостей)
                    // (Если хочешь — добавим компактные подписи Bx/Bz рядом с осями)
                }
            }

            // Чуть более читабельно
            androidx.compose.material3.Text(
                text = "Bx ${Format.oneDecOrDash(bx)} nT   •   Bz ${Format.oneDecOrDash(bz)} nT",
                color = c.textSecondary
            )
        }
    }
}