package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ModeToggle(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    large: Boolean = false
) {
    val c = LocalCosmosTheme.current.colors

    val size = if (large) 46.dp else 38.dp
    val icon = if (large) 22.dp else 18.dp
    val borderW = if (large) 1.6.dp else 1.2.dp

    val next = if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH

    Box(
        modifier = Modifier
            .size(size)
            .border(
                width = borderW,
                color = c.textSecondary.copy(alpha = 0.40f),
                shape = CircleShape
            )
            .clickable { onToggle(next) }
            .padding(7.dp)
    ) {
        Canvas(modifier = Modifier.size(icon)) {
            // общий “бейдж”
            drawCircle(
                color = c.glass.copy(alpha = 0.55f),
                radius = size.minDimension * 0.50f,
                center = center
            )

            if (mode == AppMode.SUN) {
                // SUN: круг + лучи
                val r = size.minDimension * 0.22f
                drawCircle(
                    color = c.accent.copy(alpha = 0.95f),
                    radius = r,
                    center = center
                )

                val rayR1 = r * 1.55f
                val rayR2 = r * 2.25f
                for (i in 0 until 8) {
                    val a = (i * (Math.PI * 2.0) / 8.0).toFloat()
                    val s = Offset(
                        x = center.x + cos(a) * rayR1,
                        y = center.y + sin(a) * rayR1
                    )
                    val e = Offset(
                        x = center.x + cos(a) * rayR2,
                        y = center.y + sin(a) * rayR2
                    )
                    drawLine(
                        color = c.accent.copy(alpha = 0.85f),
                        start = s,
                        end = e,
                        strokeWidth = (size.minDimension * 0.10f),
                        cap = StrokeCap.Round
                    )
                }
            } else {
                // EARTH: круг + “континенты” (упрощённо)
                val r = size.minDimension * 0.30f
                drawCircle(
                    color = c.ok.copy(alpha = 0.95f),
                    radius = r,
                    center = center
                )
                // “океан”
                drawCircle(
                    color = Color(0xFF0A2A5E).copy(alpha = 0.55f),
                    radius = r * 0.92f,
                    center = center
                )
                // “материки” 2 шт
                drawCircle(
                    color = c.ok.copy(alpha = 0.85f),
                    radius = r * 0.24f,
                    center = Offset(center.x - r * 0.22f, center.y - r * 0.10f)
                )
                drawCircle(
                    color = c.ok.copy(alpha = 0.75f),
                    radius = r * 0.18f,
                    center = Offset(center.x + r * 0.25f, center.y + r * 0.15f)
                )

                // обводка шара
                drawCircle(
                    color = c.textSecondary.copy(alpha = 0.30f),
                    radius = r,
                    center = center,
                    style = Stroke(width = size.minDimension * 0.08f)
                )
            }
        }
    }
}