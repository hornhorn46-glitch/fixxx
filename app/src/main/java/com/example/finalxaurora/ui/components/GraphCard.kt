package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun GraphCard(
    series: GraphSeries,
    unitLabel: String = "",
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    val appear by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(520),
        label = "graphAppear"
    )

    GlassCard(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // constraints.* здесь уже в PX
            val wPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val hPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

            val leftPad = 44f
            val topPad = 12f
            val rightPad = 10f
            val bottomPad = 26f

            val chartW = max(1f, wPx - leftPad - rightPad)
            val chartH = max(1f, hPx - topPad - bottomPad)

            fun yToText(v: Double): String {
                return when {
                    abs(v) >= 100 -> v.toInt().toString()
                    abs(v) >= 10 -> String.format("%.1f", v)
                    else -> String.format("%.2f", v)
                }
            }

            // Y-ticks: 5 значений (max, 75%, mid, 25%, min)
            val yMin = series.minY
            val yMax = series.maxY
            val yMid = (yMin + yMax) / 2.0
            val yQ1 = yMin + (yMid - yMin) / 2.0
            val yQ3 = yMid + (yMax - yMid) / 2.0

            val yTicks = listOf(
                yMax to topPad,
                yQ3 to (topPad + chartH * 0.25f),
                yMid to (topPad + chartH * 0.50f),
                yQ1 to (topPad + chartH * 0.75f),
                yMin to (topPad + chartH)
            )

            val pts = series.points
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

            // График (без текста на Canvas)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // grid (горизонтальные линии по тиккам)
                for ((_, y) in yTicks) {
                    drawLine(
                        color = c.textSecondary.copy(alpha = 0.12f),
                        start = Offset(leftPad, y),
                        end = Offset(leftPad + chartW, y),
                        strokeWidth = 1.0f
                    )
                }

                // danger tint
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

                if (pts.isNotEmpty()) {
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

            // Y-цифры (композаблами поверх Canvas)
            for ((value, yPx) in yTicks) {
                Text(
                    text = yToText(value),
                    color = c.textSecondary.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (yPx - 8f).roundToInt() // чуть центрируем
                            )
                        }
                )
            }

            // Подписи X (0 / середина / конец) + единицы измерения
            val xLabelY = (topPad + chartH + 6f).roundToInt()
            Text(
                text = "0",
                color = c.textSecondary.copy(alpha = 0.72f),
                modifier = Modifier.offset { IntOffset(leftPad.roundToInt(), xLabelY) }
            )
            Text(
                text = "12h",
                color = c.textSecondary.copy(alpha = 0.72f),
                modifier = Modifier.offset { IntOffset((leftPad + chartW * 0.5f).roundToInt() - 10, xLabelY) }
            )
            Text(
                text = "24h",
                color = c.textSecondary.copy(alpha = 0.72f),
                modifier = Modifier.offset { IntOffset((leftPad + chartW).roundToInt() - 18, xLabelY) }
            )

            if (unitLabel.isNotBlank()) {
                Text(
                    text = unitLabel,
                    color = c.textSecondary.copy(alpha = 0.72f),
                    modifier = Modifier.offset {
                        IntOffset(
                            x = (leftPad + 6f).roundToInt(),
                            y = 0
                        )
                    }
                )
            }
        }
    }
}