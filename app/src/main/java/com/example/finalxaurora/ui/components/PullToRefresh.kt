package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import kotlin.math.roundToInt

@Composable
fun SimplePullToRefresh(
    enabled: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val c = LocalCosmosTheme.current.colors
    var drag by remember { mutableFloatStateOf(0f) }
    val threshold = 84f

    val offsetY by animateFloatAsState(
        targetValue = if (isRefreshing) threshold else drag.coerceIn(0f, threshold * 1.35f),
        animationSpec = tween(220),
        label = "ptrOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                enabled = enabled && !isRefreshing,
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    if (delta > 0) drag = (drag + delta * 0.65f).coerceAtMost(threshold * 1.7f)
                    else drag = (drag + delta * 0.35f).coerceAtLeast(0f)
                },
                onDragStopped = {
                    if (drag >= threshold && enabled && !isRefreshing) onRefresh()
                    drag = 0f
                }
            )
    ) {
        if (offsetY > 6f) {
            Text(
                text = if (isRefreshing) "…" else "",
                color = c.textSecondary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(0, (offsetY * 0.35f).roundToInt()) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.roundToInt()) }
        ) {
            content()
        }
    }
}
