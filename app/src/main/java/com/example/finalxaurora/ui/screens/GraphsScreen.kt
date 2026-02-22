package com.example.finalxaurora.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.R
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.components.GraphCard
import com.example.finalxaurora.ui.components.ModeToggle
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.theme.LocalCosmosTheme
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
    val c = LocalCosmosTheme.current.colors
    AuroraBackground(mode = mode)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 14.dp)
    ) {
        TopAppBar(
            title = {
                Text(text = strings.graphs, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = c.textPrimary
                    )
                }
            },
            actions = { ModeToggle(mode = mode, onToggle = onModeChange) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = c.textPrimary
            )
        )

        Spacer(Modifier.height(12.dp))

        state.seriesKp?.let { GraphCard(it, modifier = Modifier.fillMaxWidth().height(220.dp)) }
        Spacer(Modifier.height(10.dp))
        state.seriesWind?.let { GraphCard(it, modifier = Modifier.fillMaxWidth().height(220.dp)) }
        Spacer(Modifier.height(10.dp))
        state.seriesBz?.let { GraphCard(it, modifier = Modifier.fillMaxWidth().height(220.dp)) }
    }
}
