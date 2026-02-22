package com.example.finalxaurora.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
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
            Text(text = title, color = c.textPrimary)
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                    val w = size.width
                    val h = size.height
                    val r = min(w, h) * 0.42f
                    val center = Offset(w / 2f, h / 2f)

                    // ring
                    drawCircle(
                        color = c.textSecondary.copy(alpha = 0.35f),
                        radius = r,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // ✅ Координаты: X=Bx вправо, Y=Bz вверх.
                    // В Canvas Y вниз, поэтому для правильного “Bz<0 вниз” берём y = -bz
                    val x = bx.toFloat()
                    val y = (-bz).toFloat()

                    val angle = atan2(y, x)

                    val needleLen = r * 0.85f
                    val tip = Offset(
                        x = center.x + needleLen * cos(angle),
                        y = center.y + needleLen * sin(angle)
                    )

                    drawLine(
                        color = c.accent,
                        start = center,
                        end = tip,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = c.textPrimary.copy(alpha = 0.95f),
                        radius = 5.dp.toPx(),
                        center = center
                    )

                    // subtle danger arc (southward favorable)
                    drawArc(
                        color = c.ok.copy(alpha = 0.35f),
                        startAngle = 90f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(center.x - r, center.y - r),
                        size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Bx ${Format.oneDec(bx)} · Bz ${Format.oneDec(bz)}",
                color = c.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}