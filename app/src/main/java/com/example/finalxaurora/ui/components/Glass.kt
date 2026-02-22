package com.example.finalxaurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Dp = 22.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val c = LocalCosmosTheme.current.colors
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(c.glass)
            .border(width = 1.dp, color = c.glassStroke, shape = shape),
        content = content
    )
}
