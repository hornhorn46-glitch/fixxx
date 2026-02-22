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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.hypot

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
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 14.dp)
        ) {
            // Верхняя панель без Experimental API
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FinalXAurora",
                    color = c.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                ModeToggle(mode = mode, onToggle = onModeChange)
            }

            val score = state.prediction.score

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(text = strings.auroraScore, color = c.textSecondary)
                    Text(
                        text = "${score}/100",
                        color = c.accent
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(text = state.prediction.title, color = c.textPrimary)
                    Text(text = state.prediction.description, color = c.textSecondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            val kpNow = state.kp.lastOrNull()?.kp
            val windNow = state.wind.lastOrNull()?.speed

            val bxNow = state.mag.lastOrNull()?.bx
            val bzNow = state.mag.lastOrNull()?.bz

            // Bt считаем из Bx/Bz (компилится без поля bt)
            val btNow: Double? =
                if (bxNow != null && bzNow != null) hypot(bxNow, bzNow) else null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                        GaugeZone((450f - 250f) / (1000f - 250f), (600f - 250f) / (1000f - 250f), c.warning),
                        GaugeZone((600f - 250f) / (1000f - 250f), (750f - 250f) / (1000f - 250f), c.warning),
                        GaugeZone((750f - 250f) / (1000f - 250f), 1f, c.danger)
                    ),
                    modifier = Modifier.weight(1f)
                )

                PremiumGauge(
                    title = "Bt",
                    valueText = Format.unit(Format.oneDecOrDash(btNow), "nT"),
                    value = btNow ?: 5.0,
                    min = 0.0,
                    max = 30.0,
                    zones = listOf(
                        GaugeZone(0f, 10f / 30f, c.ok),
                        GaugeZone(10f / 30f, 15f / 30f, c.warning),
                        GaugeZone(15f / 30f, 20f / 30f, c.warning),
                        GaugeZone(20f / 30f, 1f, c.danger)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            val bx = bxNow ?: 0.0
            val bz = bzNow ?: 0.0
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