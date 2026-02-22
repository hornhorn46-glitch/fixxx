package com.example.finalxaurora.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.finalxaurora.R
import com.example.finalxaurora.domain.AppMode

@Composable
fun AuroraBackground(
    mode: AppMode,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(targetState = mode, label = "bg") { m ->
            val res = if (m == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg
            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Притенение фона, чтобы UI читался и фон не отвлекал
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Снег — только на Земле (оставляем твою текущую реализацию SnowParticles без изменения сигнатуры)
        if (mode != AppMode.SUN) {
            SnowParticles(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}