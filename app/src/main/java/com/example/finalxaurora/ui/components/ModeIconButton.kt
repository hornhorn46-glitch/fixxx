package com.example.finalxaurora.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun ModeIconButton(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors
    val next = if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(c.glass.copy(alpha = 0.32f))
            .clickable { onToggle(next) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (mode == AppMode.SUN) Icons.Outlined.WbSunny else Icons.Outlined.Public,
            contentDescription = if (mode == AppMode.SUN) "Sun mode" else "Earth mode",
            tint = c.textPrimary
        )
    }
}