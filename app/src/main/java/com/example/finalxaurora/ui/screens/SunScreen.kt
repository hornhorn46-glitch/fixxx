package com.example.finalxaurora.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val c = LocalCosmosTheme.current.colors
    val scroll = rememberScrollState()

    AuroraBackground(mode = mode)

    var tab by remember { mutableIntStateOf(0) }
    val isRu = strings.sun == "Солнце"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
            .verticalScroll(scroll)
            .padding(bottom = 110.dp)
    ) {
        CosmosTopBar(
            title = strings.sun,
            onBack = onBack,
            actions = {
                // Крупная, как на главном
                ModeToggle(mode = mode, onToggle = onModeChange, large = true)
            }
        )

        Spacer(Modifier.height(10.dp))

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = if (isRu) "Как это читать" else "How to read",
                    color = c.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (isRu) {
                        "• CME — главный кандидат на бурю.\n" +
                            "• Пятна — вероятность вспышек.\n" +
                            "• Овал — где сияние вероятнее."
                    } else {
                        "• CME — prime candidate for storms.\n" +
                            "• Sunspots — flare potential.\n" +
                            "• Oval — where aurora is more likely."
                    },
                    color = c.textSecondary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

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
            val title: String
            val url: String
            val hint: String

            when (idx) {
                0 -> {
                    title = strings.cme
                    url = SpaceWeatherApi.URL_SUN_CME
                    hint = if (isRu)
                        "Ищем выброс/облако плазмы. Если направлено к Земле — шанс бури выше."
                    else
                        "Look for ejections/plasma clouds. Earth-directed events raise storm chances."
                }
                1 -> {
                    title = strings.sunspots
                    url = SpaceWeatherApi.URL_SUN_SPOTS
                    hint = if (isRu)
                        "Большие/сложные области чаще дают вспышки."
                    else
                        "Large/complex regions tend to flare more."
                }
                else -> {
                    title = strings.auroraOval
                    url = SpaceWeatherApi.URL_AURORA_OVAL
                    hint = if (isRu)
                        "Модель вероятности сияния по широтам (примерно)."
                    else
                        "Model-based aurora probability by latitude (approx)."
                }
            }

            SunImageCard(
                strings = strings,
                title = title,
                url = url,
                hint = hint,
                // пока “просто картинками” — но тап оставляем на будущее
                onTap = { onOpenImage(title, url) }
            )
        }

        Spacer(Modifier.height(8.dp))
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
    strings: AppStrings,
    title: String,
    url: String,
    hint: String,
    onTap: () -> Unit
) {
    val c = LocalCosmosTheme.current.colors

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(text = title, color = c.textPrimary)
            Spacer(Modifier.height(6.dp))
            Text(text = hint, color = c.textSecondary)

            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onTap() }
                    .background(c.glass.copy(alpha = 0.18f))
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(text = strings.open, color = c.textSecondary)
        }
    }
}