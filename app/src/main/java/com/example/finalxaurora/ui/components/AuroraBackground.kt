package com.example.finalxaurora.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
            animationSpec = tween(durationMillis = 450),
            label = "bgCrossfade"
        ) { m ->
            // Имена файлов как у тебя: earth_bg.jpg / sun_bg.jpg
            val resId = if (m == AppMode.SUN) R.drawable.sun_bg else R.drawable.earth_bg

            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Оверлей для читабельности (дёшево по GPU)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.30f),
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.35f)
                        )
                    )
                )
        )

        // Лёгкий theme-tint
        Box(
            Modifier
                .fillMaxSize()
                .background(c.bgMid.copy(alpha = 0.06f))
        )

        // Снег только на Земле, чуть более выраженный
        if (mode == AppMode.EARTH) {
            SnowParticles(
                modifier = Modifier.fillMaxSize(),
                maxParticles = 40,
                baseAlpha = 0.28f
            )
        }
    }
}