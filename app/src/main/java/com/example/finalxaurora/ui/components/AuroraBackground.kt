package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.finalxaurora.R
import com.example.finalxaurora.domain.AppMode
import androidx.compose.ui.graphics.Color

@Composable
fun AuroraBackground(mode: AppMode) {
    val res = if (mode == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Реальное затемнение, чтобы контент читался и фон не “орал”
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.38f))
        )

        // Снег — только на EARTH. Усилил заметность.
        if (mode == AppMode.EARTH) {
            SnowParticles(
                modifier = Modifier.fillMaxSize(),
                maxParticles = 46,
                baseAlpha = 0.42f
            )
        }
    }
}