package com.example.finalxaurora.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.R
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.components.BFieldCompass
import com.example.finalxaurora.ui.components.GaugeZone
import com.example.finalxaurora.ui.components.GlassCard
import com.example.finalxaurora.ui.components.ModeToggle
import com.example.finalxaurora.ui.components.PixelFrog
import com.example.finalxaurora.ui.components.PremiumGauge
import com.example.finalxaurora.ui.components.SimplePullToRefresh
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherState
import com.example.finalxaurora.util.Format

@Composable
fun NowScreen(
    strings: AppStrings,
    mode: AppMode,
    onModeChange: (AppMode) -> Unit,
    state: SpaceWeatherState,
    onRefresh: () -> Unit,
    onOpenGraphs: () -> Unit,
    onOpenSun: () -> Unit,
    onOpenSettings: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val c = LocalCosmosTheme.current.colors

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar("${strings.error}: $it") }
    }

    AuroraBackground(mode = mode)

    SimplePullToRefresh(
        enabled = true,
        isRefreshing = state.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 14.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "FinalXAurora",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    ModeToggle(mode = mode, onToggle = onModeChange)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = c.textPrimary
                )
            )

            Spacer(Modifier.height(10.dp))

            val score = state.prediction.score

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(text = strings.auroraScore, color = c.textSecondary)
                    Text(
                        text = "${score}/100",
                        color = c.accent,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(text = state.prediction.title, color = c.textPrimary)
                    Text(text = state.prediction.description, color = c.textSecondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val kpNow = state.kp.lastOrNull()?.kp
                val windNow = state.wind.lastOrNull()?.speed
                val bzNow = state.mag.lastOrNull()?.bz

                PremiumGauge(
                    title = strings.kpIndex,
                    valueText = Format.intOrDash(kpNow),
                    value = kpNow ?: 0.0,
                    min = 0.0,
                    max = 9.0,
                    zones = listOf(
                        GaugeZone(0f, 5f / 9f, c.ok),
                        GaugeZone(5f / 9f, 6f / 9f, c.warning),
                        GaugeZone(6f / 9f, 7f / 9f, c.warning),
                        GaugeZone(7f / 9f, 1f, c.danger)
                    ),
                    modifier = Modifier.weight(1f)
                )

                PremiumGauge(
                    title = strings.windSpeed,
                    valueText = Format.unit(Format.intOrDash(windNow), "km/s"),
                    value = windNow ?: 350.0,
                    min = 250.0,
                    max = 1000.0,
                    zones = listOf(
                        GaugeZone(0f, (450f - 250f) / (1000f - 250f), c.ok),
                        GaugeZone(
                            (450f - 250f) / (1000f - 250f),
                            (600f - 250f) / (1000f - 250f),
                            c.warning
                        ),
                        GaugeZone(
                            (600f - 250f) / (1000f - 250f),
                            (750f - 250f) / (1000f - 250f),
                            c.warning
                        ),
                        GaugeZone((750f - 250f) / (1000f - 250f), 1f, c.danger)
                    ),
                    modifier = Modifier.weight(1f)
                )

                PremiumGauge(
                    title = strings.bz,
                    valueText = Format.unit(Format.oneDecOrDash(bzNow), "nT"),
                    value = bzNow ?: 0.0,
                    min = -20.0,
                    max = 20.0,
                    zones = listOf(
                        GaugeZone(0f, 0.25f, c.danger),
                        GaugeZone(0.25f, 0.40f, c.warning),
                        GaugeZone(0.40f, 0.60f, c.ok),
                        GaugeZone(0.60f, 1f, c.warning)
                    ),
                    modifier = Modifier.weight(1f),
                    invertNeedle = true
                )
            }

            Spacer(Modifier.height(12.dp))

            val bx = state.mag.lastOrNull()?.bx ?: 0.0
            val bz = state.mag.lastOrNull()?.bz ?: 0.0
            BFieldCompass(
                title = strings.bField,
                bx = bx,
                bz = bz,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_refresh),
                            contentDescription = strings.refresh,
                            tint = c.textPrimary
                        )
                    }
                    IconButton(onClick = onOpenGraphs) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_graph),
                            contentDescription = strings.graphs,
                            tint = c.textPrimary
                        )
                    }
                    IconButton(onClick = onOpenSun) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_sun),
                            contentDescription = strings.sun,
                            tint = c.textPrimary
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings),
                            contentDescription = strings.settings,
                            tint = c.textPrimary
                        )
                    }
                }

                PixelFrog()
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}