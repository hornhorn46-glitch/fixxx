package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun ModeToggle(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val t by animateFloatAsState(
        targetValue = if (mode == AppMode.SUN) 1f else 0f,
        animationSpec = tween(420),
        label = "modeToggle"
    )

    Box(
        modifier = modifier
            .width(64.dp)
            .height(30.dp)
            .clickable {
                onToggle(if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH)
            }
    ) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val r = h / 2f
            drawRoundRect(
                color = c.glassStroke.copy(alpha = 0.60f),
                cornerRadius = CornerRadius(r, r)
            )
            drawRoundRect(
                color = c.glass.copy(alpha = 0.55f),
                cornerRadius = CornerRadius(r, r),
                size = Size(w, h)
            )

            val knob = h - 6f
            val x = lerp(3f, w - knob - 3f, t)
            drawRoundRect(
                color = c.accent.copy(alpha = 0.85f),
                topLeft = Offset(x, 3f),
                size = Size(knob, knob),
                cornerRadius = CornerRadius(knob / 2f, knob / 2f)
            )
        }
    }
}

private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
