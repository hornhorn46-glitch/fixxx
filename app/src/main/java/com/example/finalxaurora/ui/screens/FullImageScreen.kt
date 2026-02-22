package com.example.finalxaurora.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.finalxaurora.R
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun FullImageScreen(
    title: String,
    url: String,
    strings: AppStrings,
    mode: AppMode,
    auroraScore: Int,
    onBack: () -> Unit
) {
    CosmosTheme(mode = mode, auroraScore = auroraScore) {
        val c = LocalCosmosTheme.current.colors
        val ctx = LocalContext.current

        AuroraBackground(mode = mode)

        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        var fade by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) { fade = 1f }

        BackHandler { onBack() }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .statusBarsPadding()
        ) {
            Text(
                text = title,
                color = c.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .alpha(0.95f)
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = c.textPrimary
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(fade)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}