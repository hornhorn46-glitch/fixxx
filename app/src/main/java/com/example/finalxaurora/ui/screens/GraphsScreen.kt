package com.example.finalxaurora.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.components.CosmosTopBar
import com.example.finalxaurora.ui.components.GraphCard
import com.example.finalxaurora.ui.components.ModeToggle
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.vm.SpaceWeatherState

@Composable
fun GraphsScreen(
    strings: AppStrings,
    mode: AppMode,
    onModeChange: (AppMode) -> Unit,
    state: SpaceWeatherState,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AuroraBackground(mode = mode)

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CosmosTopBar(
                title = strings.graphs,
                onBack = onBack
            ) {
                ModeToggle(mode = mode, onToggle = onModeChange)
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            state.seriesKp?.let {
                GraphCard(
                    series = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            state.seriesWind?.let {
                GraphCard(
                    series = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            state.seriesBz?.let {
                GraphCard(
                    series = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }
    }
}