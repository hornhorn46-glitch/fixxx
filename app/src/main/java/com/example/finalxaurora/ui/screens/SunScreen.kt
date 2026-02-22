package com.example.finalxaurora.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.finalxaurora.data.SpaceWeatherApi
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.*
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun SunScreen(
    strings: AppStrings,
    mode: AppMode,
    onModeChange: (AppMode) -> Unit,
    onOpenImage: (title: String, url: String) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val c = LocalCosmosTheme.current.colors
    AuroraBackground(mode = mode)

    var tab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
    ) {
        CosmosTopBar(
            title = strings.sun,
            onBack = onBack,
            actions = { ModeToggle(mode = mode, onToggle = onModeChange) }
        )

        Spacer(Modifier.height(10.dp))

        SunTabs(strings = strings, selected = tab, onSelect = { tab = it })

        Spacer(Modifier.height(12.dp))

        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                (fadeIn(tween(260, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(180, easing = FastOutSlowInEasing)))
            },
            label = "sunTabAnim"
        ) { idx ->
            val (title, url) = when (idx) {
                0 -> strings.cme to SpaceWeatherApi.URL_SUN_CME
                1 -> strings.sunspots to SpaceWeatherApi.URL_SUN_SPOTS
                else -> strings.auroraOval to SpaceWeatherApi.URL_AURORA_OVAL
            }

            SunImageCard(
                title = title,
                url = url,
                strings = strings,
                onOpen = { onOpenImage(title, url) }
            )
        }
    }
}

@Composable
private fun SunTabs(
    strings: AppStrings,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabPill(strings.cme, selected == 0, onClick = { onSelect(0) })
            TabPill(strings.sunspots, selected == 1, onClick = { onSelect(1) })
            TabPill(strings.auroraOval, selected == 2, onClick = { onSelect(2) })
        }
    }
}

@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val c = LocalCosmosTheme.current.colors
    val bg = if (selected) c.accentSoft.copy(alpha = 0.55f) else c.glass.copy(alpha = 0.30f)
    val fg = if (selected) c.textPrimary else c.textSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SunImageCard(
    title: String,
    url: String,
    strings: AppStrings,
    onOpen: () -> Unit
) {
    val c = LocalCosmosTheme.current.colors
    val ctx = LocalContext.current

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(text = title, color = c.textPrimary)

            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpen() }
                    .background(c.glass.copy(alpha = 0.18f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = strings.tapToOpen,
                color = c.textSecondary
            )
        }
    }
}