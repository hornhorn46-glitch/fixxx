package com.example.finalxaurora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    large: Boolean = true
) {
    val c = LocalCosmosTheme.current.colors

    val size = if (large) 52.dp else 44.dp
    val border = BorderStroke(2.dp, c.textSecondary.copy(alpha = 0.55f))

    Surface(
        onClick = {
            val next = if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH
            onToggle(next)
        },
        shape = CircleShape,
        color = c.glass.copy(alpha = 0.35f),
        border = border
    ) {
        Box(Modifier.size(size)) {
            Canvas(Modifier.matchParentSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val r = size.minDimension * 0.23f

                val stroke = size.minDimension * 0.06f
                val iconColor = c.textPrimary.copy(alpha = 0.92f)

                if (mode == AppMode.SUN) {
                    // SUN icon (кружок + лучи)
                    drawCircle(
                        color = iconColor,
                        radius = r,
                        center = center,
                        style = Stroke(width = stroke)
                    )
                    val rays = 8
                    val inner = r + stroke * 0.8f
                    val outer = inner + r * 0.75f
                    for (i in 0 until rays) {
                        val a = (Math.PI * 2.0 * i / rays).toFloat()
                        val x1 = center.x + inner * cos(a)
                        val y1 = center.y + inner * sin(a)
                        val x2 = center.x + outer * cos(a)
                        val y2 = center.y + outer * sin(a)
                        drawLine(
                            color = iconColor,
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = stroke
                        )
                    }
                } else {
                    // EARTH icon (кружок + меридианы/параллели)
                    drawCircle(
                        color = iconColor,
                        radius = r * 1.10f,
                        center = center,
                        style = Stroke(width = stroke)
                    )
                    // экватор
                    drawLine(
                        color = iconColor,
                        start = Offset(center.x - r, center.y),
                        end = Offset(center.x + r, center.y),
                        strokeWidth = stroke
                    )
                    // меридиан
                    drawLine(
                        color = iconColor,
                        start = Offset(center.x, center.y - r),
                        end = Offset(center.x, center.y + r),
                        strokeWidth = stroke
                    )
                    // две “дуги”
                    drawCircle(
                        color = iconColor,
                        radius = r * 0.70f,
                        center = center,
                        style = Stroke(width = stroke)
                    )
                }
            }
        }
    }
}