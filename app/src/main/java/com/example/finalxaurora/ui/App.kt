package com.example.finalxaurora.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.nav.NavController
import com.example.finalxaurora.ui.nav.Screen
import com.example.finalxaurora.ui.screens.FullImageScreen
import com.example.finalxaurora.ui.screens.GraphsScreen
import com.example.finalxaurora.ui.screens.NowScreen
import com.example.finalxaurora.ui.screens.SettingsScreen
import com.example.finalxaurora.ui.screens.SunScreen
import com.example.finalxaurora.ui.strings.stringsFor
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.delay

@Composable
fun App(
    vmFactory: VmFactory,
    settings: SettingsStore
) {
    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)

    var mode by remember { mutableStateOf(settings.loadMode()) }
    var language by remember { mutableStateOf(settings.loadLanguage()) }

    val nav = remember { NavController(Screen.Now) }
    val snack = remember { SnackbarHostState() }

    val state = vm.state.value
    val strings = stringsFor(language)

    CosmosTheme(mode = mode, auroraScore = state.prediction.score) {
        when (val s = nav.state.current) {
            Screen.Now -> NowScreen(
                strings = strings,
                mode = mode,
                onModeChange = { new -> mode = new; settings.saveMode(new) },
                state = state,
                onRefresh = { vm.refresh() },
                onOpenGraphs = { nav.push(Screen.Graphs) },
                onOpenSun = { nav.push(Screen.Sun) },
                onOpenSettings = { nav.push(Screen.Settings) },
                snackbarHostState = snack
            )
            Screen.Graphs -> GraphsScreen(
                strings = strings,
                mode = mode,
                onModeChange = { new -> mode = new; settings.saveMode(new) },
                state = state,
                onBack = { nav.pop() },
                snackbarHostState = snack
            )
            Screen.Sun -> SunScreen(
                strings = strings,
                mode = mode,
                onModeChange = { new -> mode = new; settings.saveMode(new) },
                onOpenImage = { title, url -> nav.push(Screen.FullImage(title, url)) },
                onBack = { nav.pop() },
                snackbarHostState = snack
            )
            Screen.Settings -> SettingsScreen(
                strings = strings,
                mode = mode,
                onModeChange = { new -> mode = new; settings.saveMode(new) },
                language = language,
                onLanguageChange = { lang: AppLanguage -> language = lang; settings.saveLanguage(lang) },
                onBack = { nav.pop() },
                snackbarHostState = snack
            )
            is Screen.FullImage -> FullImageScreen(
                title = s.title,
                url = s.url,
                strings = strings,
                mode = mode,
                auroraScore = state.prediction.score,
                onBack = { nav.pop() }
            )
        }

        SnackbarHost(hostState = snack)

        BackHandler(enabled = nav.state.canGoBack()) { nav.pop() }

        DoubleBackExitHandler(
            enabled = !nav.state.canGoBack(),
            snackbarHostState = snack,
            message = strings.backAgainToExit
        )
    }
}

@Composable
private fun DoubleBackExitHandler(
    enabled: Boolean,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    if (!enabled) return

    var lastBack by remember { mutableLongStateOf(0L) }
    var armed by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        val now = System.currentTimeMillis()
        if (armed && now - lastBack < 1600) {
            // Let Activity handle process lifecycle; no hard exit here.
        } else {
            armed = true
            lastBack = now
        }
    }

    LaunchedEffect(armed) {
        if (armed) {
            snackbarHostState.showSnackbar(message)
            delay(1600)
            armed = false
        }
    }
}
