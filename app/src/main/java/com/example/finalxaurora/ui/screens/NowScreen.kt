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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private enum class HelpTopic { KP, WIND, BT, BFIELD }

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
    val scroll = rememberScrollState()

    var help by remember { mutableStateOf<HelpTopic?>(null) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar("${strings.error}: $it") }
    }

    AuroraBackground(mode = mode)

    if (help != null) {
        val (title, body) = when (help!!) {
            HelpTopic.KP -> strings.helpKpTitle to strings.helpKpBody
            HelpTopic.WIND -> strings.helpWindTitle to strings.helpWindBody
            HelpTopic.BT -> strings.helpBtTitle to strings.helpBtBody
            HelpTopic.BFIELD -> strings.helpBFieldTitle to strings.helpBFieldBody
        }

        AlertDialog(
            onDismissRequest = { help = null },
            title = { Text(title) },
            text = { Text(body) },
            confirmButton = {
                TextButton(onClick = { help = null }) { Text(strings.ok) }
            },
            containerColor = c.glass.copy(alpha = 0.92f),
            titleContentColor = c.textPrimary,
            textContentColor = c.textSecondary
        )
    }

    SimplePullToRefresh(
        enabled = true,
        isRefreshing = state.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(scroll)
                .padding(horizontal = 14.dp)
                .padding(bottom = 110.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.appName,
                    color = c.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onOpenGraphs) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_graph),
                        contentDescription = strings.graphs,
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

                ModeToggle(mode = mode, onToggle = onModeChange, large = true)
            }

            Spacer(Modifier.height(10.dp))

            val score = state.prediction.score

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.auroraScore,
                            color = c.textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* позже */ }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_info),
                                contentDescription = strings.info,
                                tint = c.textSecondary
                            )
                        }
                    }

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

            val kpNow = state.kp.lastOrNull()?.kp
            val windNow = state.wind.lastOrNull()?.speed
            val bxNow = state.mag.lastOrNull()?.bx
            val bzNow = state.mag.lastOrNull()?.bz
            val btNow = if (bxNow != null && bzNow != null) hypot(bxNow, bzNow) else null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.kpIndex, color = c.textSecondary, modifier = Modifier.weight(1f))
                            IconButton(onClick = { help = HelpTopic.KP }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_info),
                                    contentDescription = strings.info,
                                    tint = c.textSecondary
                                )
                            }
                        }

                        PremiumGauge(
                            title = "",
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
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.windSpeed, color = c.textSecondary, modifier = Modifier.weight(1f))
                            IconButton(onClick = { help = HelpTopic.WIND }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_info),
                                    contentDescription = strings.info,
                                    tint = c.textSecondary
                                )
                            }
                        }

                        PremiumGauge(
                            title = "",
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
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.bt, color = c.textSecondary, modifier = Modifier.weight(1f))
                            IconButton(onClick = { help = HelpTopic.BT }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_info),
                                    contentDescription = strings.info,
                                    tint = c.textSecondary
                                )
                            }
                        }

                        PremiumGauge(
                            title = "",
                            valueText = Format.unit(Format.oneDecOrDash(btNow), "nT"),
                            value = btNow ?: 0.0,
                            min = 0.0,
                            max = 50.0,
                            zones = listOf(
                                GaugeZone(0f, 0.45f, c.ok),
                                GaugeZone(0.45f, 0.70f, c.warning),
                                GaugeZone(0.70f, 0.85f, c.warning),
                                GaugeZone(0.85f, 1f, c.danger)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.bFieldLong, color = c.textSecondary, modifier = Modifier.weight(1f))
                IconButton(onClick = { help = HelpTopic.BFIELD }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_info),
                        contentDescription = strings.info,
                        tint = c.textSecondary
                    )
                }
            }

            BFieldCompass(
                title = "",
                bx = bxNow ?: 0.0,
                bz = bzNow ?: 0.0,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PixelFrog()
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}