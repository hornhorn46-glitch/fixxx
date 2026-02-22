package com.example.finalxaurora.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode
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
import androidx.activity.compose.BackHandler

sealed class Screen {
    data object Now : Screen()
    data object Sun : Screen()
    data object Graphs : Screen()
    data object Settings : Screen()
    data class FullImage(val title: String, val url: String) : Screen()
}

@Composable
fun App(
    vmFactory: VmFactory,
    settings: SettingsStore
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var mode by remember { mutableStateOf(settings.loadMode()) }
    var language by remember { mutableStateOf(settings.loadLanguage()) }
    val strings = remember(language) { stringsFor(language) }

    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)
    val state by vm.state

    // Navigation stack (no navigation-compose)
    val stack = remember { mutableStateListOf<Screen>() }
    LaunchedEffect(Unit) {
        if (stack.isEmpty()) {
            stack.add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }
    val current by remember { derivedStateOf { stack.last() } }

    fun push(s: Screen) {
        stack.add(s)
    }

    fun pop(): Boolean {
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            true
        } else {
            false
        }
    }

    fun setMode(newMode: AppMode) {
        if (newMode == mode) return
        mode = newMode
        settings.saveMode(newMode)

        // If we're on the root screen, swap root content with mode.
        if (stack.size == 1) {
            stack.clear()
            stack.add(if (newMode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

    fun setLanguage(newLang: AppLanguage) {
        if (newLang == language) return
        language = newLang
        settings.saveLanguage(newLang)
    }

    // Double back to exit (delegates to system back on second press)
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var lastBackMillis by remember { mutableLongStateOf(0L) }
    var backSnackbarTick by remember { mutableIntStateOf(0) }

    BackHandler {
        val didPop = pop()
        if (didPop) return@BackHandler

        val now = System.currentTimeMillis()
        if (now - lastBackMillis <= 1500L) {
            dispatcher?.onBackPressed()
        } else {
            lastBackMillis = now
            backSnackbarTick++
        }
    }

    LaunchedEffect(backSnackbarTick) {
        if (backSnackbarTick > 0) {
            snackbarHostState.showSnackbar(strings.backAgainToExit)
        }
    }

    CosmosTheme(mode = mode, auroraScore = state.prediction.score) {
        when (val s = current) {
            is Screen.Now -> {
                NowScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    state = state,
                    onRefresh = { vm.refresh() },
                    onOpenGraphs = { push(Screen.Graphs) },
                    onOpenSun = {
                        if (mode != AppMode.SUN) setMode(AppMode.SUN)
                        if (stack.last() !is Screen.Sun) push(Screen.Sun)
                    },
                    onOpenSettings = { push(Screen.Settings) },
                    snackbarHostState = snackbarHostState
                )
            }

            is Screen.Sun -> {
                SunScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    onOpenImage = { title, url -> push(Screen.FullImage(title, url)) },
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )
            }

            is Screen.Graphs -> {
                GraphsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    state = state,
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )
            }

            is Screen.Settings -> {
                SettingsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    language = language,
                    onLanguageChange = ::setLanguage,
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )
            }

            is Screen.FullImage -> {
                FullImageScreen(
                    title = s.title,
                    url = s.url,
                    strings = strings,
                    mode = mode,
                    auroraScore = state.prediction.score,
                    onBack = { pop() }
                )
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}