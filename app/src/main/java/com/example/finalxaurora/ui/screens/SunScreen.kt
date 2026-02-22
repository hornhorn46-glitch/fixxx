package com.example.finalxaurora.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.finalxaurora.data.SpaceWeatherApi
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.components.CosmosTopBar
import com.example.finalxaurora.ui.components.GlassCard
import com.example.finalxaurora.ui.components.ModeToggle
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
    // параметры оставлены, чтобы не ломать сигнатуры/навигацию
    val _unusedSnack = snackbarHostState
    val _unusedOpen = onOpenImage

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
            actions = {
                ModeToggle(
                    mode = mode,
                    onToggle = onModeChange,
                    large = true
                )
            }
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
                url = url
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
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .then(Modifier) // без лишних экспериментальных API
    ) {
        // кликаем всю “пилюлю” через обертку:
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(bg)
                .padding(0.dp)
        ) {
            Text(
                text = text,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(bg)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .run {
                        // локально добавляем клик без импорта clickable сверху
                        androidx.compose.foundation.clickable(onClick = onClick)
                    }
            )
        }
    }
}

@Composable
private fun SunImageCard(
    title: String,
    url: String
) {
    val c = LocalCosmosTheme.current.colors

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(text = title, color = c.textPrimary)
            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(c.glass.copy(alpha = 0.18f))
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}