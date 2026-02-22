package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.max

@Composable
fun GraphCard(
    series: GraphSeries,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val anim by animateFloatAsState(targetValue = 1f, animationSpec = tween(700), label = "graphAnim")

    GlassCard(modifier = modifier) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                text = "${series.title} ${series.unit}".trim(),
                color = c.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Canvas(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas
                val padL = 10f
                val padR = 10f
                val padT = 6f
                val padB = 18f

                val plotW = w - padL - padR
                val plotH = h - padT - padB

                series.dangerAbove?.let { d ->
                    val y = yFor(d, series.minY, series.maxY, padT, plotH)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(padL, padT),
                        size = androidx.compose.ui.geometry.Size(plotW, max(0f, y - padT))
                    )
                }
                series.dangerBelow?.let { d ->
                    val y = yFor(d, series.minY, series.maxY, padT, plotH)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(padL, y),
                        size = androidx.compose.ui.geometry.Size(plotW, max(0f, padT + plotH - y))
                    )
                }

                val step = series.gridStep
                var g = series.minY
                while (g <= series.maxY + 0.0001) {
                    val y = yFor(g, series.minY, series.maxY, padT, plotH)
                    drawLine(
                        color = c.glassStroke.copy(alpha = 0.30f),
                        start = Offset(padL, y),
                        end = Offset(padL + plotW, y),
                        strokeWidth = 1.2f
                    )
                    g += step
                }

                val pts = series.points
                if (pts.size < 2) return@Canvas

                fun xAt(i: Int): Float {
                    val t = i.toFloat() / (pts.size - 1).toFloat()
                    return padL + plotW * t
                }

                val path = Path()
                val y0 = yFor(pts[0].value, series.minY, series.maxY, padT, plotH)
                path.moveTo(xAt(0), y0)

                for (i in 1 until pts.size) {
                    val x1 = xAt(i)
                    val y1 = yFor(pts[i].value, series.minY, series.maxY, padT, plotH)
                    val x0 = xAt(i - 1)
                    val yPrev = yFor(pts[i - 1].value, series.minY, series.maxY, padT, plotH)

                    val cx = (x0 + x1) / 2f
                    path.cubicTo(cx, yPrev, cx, y1, x1, y1)
                }

                val clipW = plotW * anim.coerceIn(0f, 1f)
                clipRect(left = padL, top = padT, right = padL + clipW, bottom = padT + plotH) {
                    drawPath(
                        path = path,
                        color = c.accent.copy(alpha = 0.90f),
                        style = Stroke(width = 4.2f, cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = c.accent.copy(alpha = 0.22f),
                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

private fun yFor(v: Double, minY: Double, maxY: Double, padT: Float, plotH: Float): Float {
    val clamped = v.coerceIn(minY, maxY)
    val t = ((clamped - minY) / (maxY - minY)).toFloat()
    return padT + plotH * (1f - t)
}
