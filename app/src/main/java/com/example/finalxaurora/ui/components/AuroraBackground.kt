package com.example.finalxaurora.ui.components

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
fun AuroraBackground(mode: AppMode) {
    val res = if (mode == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // сильнее затемняем, чтобы текст читался
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.52f))
        )

        // снег только на Земле
        if (mode == AppMode.EARTH) {
            SnowParticles(modifier = Modifier.fillMaxSize())
        }
    }
}