package com.example.finalxaurora.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        val bgRes = if (mode == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg

        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Снег — только на Земле. Без "несуществующих" параметров.
        if (mode == AppMode.EARTH) {
            SnowParticles(modifier = Modifier.fillMaxSize())
        }
    }
}