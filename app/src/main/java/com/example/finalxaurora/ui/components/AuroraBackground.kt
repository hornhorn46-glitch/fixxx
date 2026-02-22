package com.example.finalxaurora.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun AuroraBackground(
    mode: AppMode,
    modifier: Modifier = Modifier
) {
    val c = LocalCosmosTheme.current.colors

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = mode,
            animationSpec = tween(420),
            label = "bgCrossfade"
        ) { m ->
            val resId = if (m == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // затемнение фона (чтобы UI читался)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.38f))
        )

        // Лёгкий “туман/стекло” под UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(c.glass.copy(alpha = 0.06f))
        )

        // Снег — только Earth (оставляю как было; интенсивность отдельно позже)
        if (mode == AppMode.EARTH) {
            SnowParticles(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}