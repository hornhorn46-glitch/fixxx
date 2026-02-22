package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.max
import kotlin.math.min

@Composable
fun GraphCard(
    series: GraphSeries,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(650),
        label = "graphDraw"
    )

    GlassCard(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            // title
            Text(
                text = "${series.title} ${series.unit}".trim(),
                color = c.textPrimary,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            )

            val points = series.points
            if (points.size < 2) {
                Text(
                    text = "No data",
                    color = c.textSecondary,
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Box
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 44.dp)
            ) {
                val w = size.width
                val h = size.height

                val minY = series.minY
                val maxY = if (series.maxY == series.minY) series.minY + 1.0 else series.maxY

                fun yToPx(v: Double): Float {
                    val t = ((v - minY) / (maxY - minY)).toFloat().coerceIn(0f, 1f)
                    return h - t * h
                }

                // grid
                val step = series.gridStep
                if (step > 0.0) {
                    var v = kotlin.math.ceil(minY / step) * step
                    while (v <= maxY) {
                        val y = yToPx(v)
                        drawLine(
                            color = c.textSecondary.copy(alpha = 0.18f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        v += step
                    }
                }

                // danger zones
                series.dangerAbove?.let { da ->
                    val y = yToPx(da)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(w, max(0f, y))
                    )
                }
                series.dangerBelow?.let { db ->
                    val y = yToPx(db)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(0f, min(h, y)),
                        size = androidx.compose.ui.geometry.Size(w, max(0f, h - min(h, y)))
                    )
                }

                val n = points.size
                val dx = w / max(1, (n - 1)).toFloat()

                val path = Path()
                for (i in 0 until n) {
                    val x = i * dx
                    val y = yToPx(points[i].value)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                // draw partial path (simple: clip by width*progress)
                val clipW = w * progress
                val clipped = Path()
                // rebuild clipped path
                for (i in 0 until n) {
                    val x = i * dx
                    val y = yToPx(points[i].value)
                    if (x > clipW) break
                    if (i == 0) clipped.moveTo(x, y) else clipped.lineTo(x, y)
                }

                drawPath(
                    path = clipped,
                    color = c.accent,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}