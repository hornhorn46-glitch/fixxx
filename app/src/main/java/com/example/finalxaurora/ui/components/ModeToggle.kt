package com.example.finalxaurora.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
    val icon = if (mode == AppMode.SUN) Icons.Outlined.WbSunny else Icons.Outlined.Public

    val pressScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(220),
        label = "modeBtnScale"
    )

    Box(
        modifier = modifier
            .size(38.dp)
            .scale(pressScale)
            .background(c.glass.copy(alpha = 0.28f), CircleShape)
            .clickable {
                onToggle(if (mode == AppMode.SUN) AppMode.EARTH else AppMode.SUN)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Mode",
            tint = c.textPrimary.copy(alpha = 0.92f),
            modifier = Modifier.size(20.dp)
        )
    }
}