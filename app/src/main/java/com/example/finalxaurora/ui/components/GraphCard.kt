package com.example.finalxaurora.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.abs
import kotlin.math.max

@Composable
fun GraphCard(
    series: GraphSeries,
    title: String = "",
    unit: String = "",
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    val appear by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(520),
        label = "graphAppear"
    )

    GlassCard(modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (title.isNotBlank()) {
                androidx.compose.material3.Text(
                    text = title,
                    color = c.textSecondary,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // больше места слева под числа Y
                val leftPad = 56f
                val topPad = 8f
                val rightPad = 10f
                val bottomPad = 26f

                val chartW = max(1f, w - leftPad - rightPad)
                val chartH = max(1f, h - topPad - bottomPad)

                val pts = series.points
                if (pts.isEmpty()) return@Canvas

                // grid
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = topPad + (chartH / gridLines) * i
                    drawLine(
                        color = c.textSecondary.copy(alpha = 0.12f),
                        start = Offset(leftPad, y),
                        end = Offset(leftPad + chartW, y),
                        strokeWidth = 1.0f
                    )
                }

                // helpers
                fun yToText(v: Double): String {
                    return when {
                        abs(v) >= 100 -> v.toInt().toString()
                        else -> String.format("%.2f", v).trimEnd('0').trimEnd('.')
                    }
                }

                val yMin = series.minY
                val yMax = series.maxY
                val yMid1 = yMin + (yMax - yMin) * 0.25
                val yMid2 = yMin + (yMax - yMin) * 0.50
                val yMid3 = yMin + (yMax - yMin) * 0.75

                val paint = Paint().apply {
                    isAntiAlias = true
                    textSize = 30f
                    color = c.textSecondary.copy(alpha = 0.80f).toArgb()
                }

                fun drawLabel(txt: String, x: Float, y: Float) {
                    // y here is baseline; keep it readable
                    drawContext.canvas.nativeCanvas.drawText(txt, x, y, paint)
                }

                // Y labels: baseline shifts a bit to avoid clipping
                drawLabel(yToText(yMax), 0f, topPad + 10f)
                drawLabel(yToText(yMid3), 0f, topPad + chartH * 0.25f + 10f)
                drawLabel(yToText(yMid2), 0f, topPad + chartH * 0.50f + 10f)
                drawLabel(yToText(yMid1), 0f, topPad + chartH * 0.75f + 10f)
                drawLabel(yToText(yMin), 0f, topPad + chartH + 10f)

                // Unit label (top-left INSIDE chart but not over Y labels)
                if (unit.isNotBlank()) {
                    val unitPaint = Paint().apply {
                        isAntiAlias = true
                        textSize = 28f
                        color = c.textSecondary.copy(alpha = 0.65f).toArgb()
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        unit,
                        leftPad + 6f,
                        topPad + 26f,
                        unitPaint
                    )
                }

                val minV = series.minY
                val maxV = series.maxY
                val denom = (maxV - minV).takeIf { it != 0.0 } ?: 1.0

                fun mapX(i: Int): Float {
                    val t01 = if (pts.size <= 1) 0f else i.toFloat() / (pts.size - 1).toFloat()
                    return leftPad + chartW * t01
                }

                fun mapY(v: Double): Float {
                    val t01 = ((v - minV) / denom).toFloat().coerceIn(0f, 1f)
                    return topPad + chartH * (1f - t01)
                }

                // Danger tint
                series.dangerAbove?.let { thr ->
                    val y = mapY(thr)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(leftPad, 0f),
                        size = Size(chartW, y)
                    )
                }
                series.dangerBelow?.let { thr ->
                    val y = mapY(thr)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(leftPad, y),
                        size = Size(chartW, (topPad + chartH) - y)
                    )
                }

                // Line path
                val path = Path()
                for (i in pts.indices) {
                    val x = mapX(i)
                    val y = mapY(pts[i].value)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = c.accent.copy(alpha = 0.92f * appear),
                    style = Stroke(width = 3.2f, cap = StrokeCap.Round)
                )

                // X labels: 0h / 12h / 24h (or min/mid/max)
                val xPaint = Paint().apply {
                    isAntiAlias = true
                    textSize = 28f
                    color = c.textSecondary.copy(alpha = 0.65f).toArgb()
                }
                drawContext.canvas.nativeCanvas.drawText("0h", leftPad, topPad + chartH + 24f, xPaint)
                drawContext.canvas.nativeCanvas.drawText("12h", leftPad + chartW * 0.5f - 20f, topPad + chartH + 24f, xPaint)
                drawContext.canvas.nativeCanvas.drawText("24h", leftPad + chartW - 34f, topPad + chartH + 24f, xPaint)
            }
        }
    }
}