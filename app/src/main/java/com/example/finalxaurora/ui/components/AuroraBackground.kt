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
            val resId = if (m == AppMode.SUN) R.drawable.bg_sun else R.drawable.bg_earth
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // лёгкий читаемый оверлей (без blur/bitmap операций)
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

        // тонкий tint под тему (мягко)
        Box(
            Modifier
                .fillMaxSize()
                .background(c.bgMid.copy(alpha = 0.06f))
        )
    }
}