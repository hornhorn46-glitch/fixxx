package com.example.finalxaurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun ModeToggle(
    mode: AppMode,
    onToggle: (AppMode) -> Unit,
    large: Boolean = false
) {
    val c = LocalCosmosTheme.current.colors

    val size = if (large) 46.dp else 34.dp
    val borderW = if (large) 1.6.dp else 1.dp

    val bg = c.glass.copy(alpha = if (large) 0.32f else 0.22f)
    val stroke = c.accentSoft.copy(alpha = if (large) 0.75f else 0.55f)

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .border(borderW, stroke, CircleShape)
            .clickable {
                val next = if (mode == AppMode.EARTH) AppMode.SUN else AppMode.EARTH
                onToggle(next)
            }
    ) {
        // Внутреннюю иконку/рисунок ты уже делал “руническим”.
        // Здесь не лезем в твои ресурсы — если у тебя рисуется внутри Canvas/иконкой,
        // оставь как было (или скажи — подхвачу текущий способ).
    }
}