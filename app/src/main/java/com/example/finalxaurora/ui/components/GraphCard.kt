package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalTextApi::class)
@Composable
fun GraphCard(
    series: GraphSeries,
    modifier: Modifier = Modifier,
    unitLabel: String? = null
) {
    val c = LocalCosmosTheme.current.colors
    val t: TextMeasurer = rememberTextMeasurer()

    val appear by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(520),
        label = "graphAppear"
    )

    GlassCard(modifier = modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val leftPad = 56f // место под цифры Y (чуть больше)
                val topPad = 12f
                val rightPad = 10f
                val bottomPad = 24f

                val chartW = max(1f, w - leftPad - rightPad)
                val chartH = max(1f, h - topPad - bottomPad)

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

                fun yToText(v: Double): String {
                    return when {
                        abs(v) >= 100 -> v.toInt().toString()
                        abs(v) >= 10 -> String.format("%.1f", v)
                        else -> String.format("%.2f", v)
                    }
                }

                val yMin = series.minY
                val yMax = series.maxY
                val yMid = (yMin + yMax) / 2.0
                val yQ1 = yMin + (yMax - yMin) * 0.25
                val yQ3 = yMin + (yMax - yMin) * 0.75

                fun drawYLabel(value: Double, y: Float) {
                    val txt = yToText(value)
                    val layout = t.measure(txt)
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            x = 0f,
                            y = y - layout.size.height / 2f
                        ),
                        color = c.textSecondary.copy(alpha = 0.82f)
                    )
                }

                // 5 меток по Y: max, 3/4, mid, 1/4, min
                drawYLabel(yMax, topPad)
                drawYLabel(yQ3, topPad + chartH * 0.25f)
                drawYLabel(yMid, topPad + chartH * 0.50f)
                drawYLabel(yQ1, topPad + chartH * 0.75f)
                drawYLabel(yMin, topPad + chartH)

                // единицы измерения (если заданы)
                unitLabel?.let { u ->
                    val layout = t.measure(u)
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(leftPad, 0f),
                        color = c.textSecondary.copy(alpha = 0.70f)
                    )
                }

                val pts = series.points
                if (pts.isEmpty()) return@Canvas

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
                        size = androidx.compose.ui.geometry.Size(chartW, y)
                    )
                }
                series.dangerBelow?.let { thr ->
                    val y = mapY(thr)
                    drawRect(
                        color = c.danger.copy(alpha = 0.10f),
                        topLeft = Offset(leftPad, y),
                        size = androidx.compose.ui.geometry.Size(chartW, (topPad + chartH) - y)
                    )
                }

                // line path
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
            }
        }
    }
}