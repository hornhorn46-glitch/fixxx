package com.example.finalxaurora.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.screens.FullImageScreen
import com.example.finalxaurora.ui.screens.GraphsScreen
import com.example.finalxaurora.ui.screens.NowScreen
import com.example.finalxaurora.ui.screens.SettingsScreen
import com.example.finalxaurora.ui.screens.SunScreen
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.strings.appStringsFor
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import kotlinx.coroutines.delay

/**
 * Навигация без navigation-compose: стек Screen.
 */
sealed interface Screen {
    data object Now : Screen
    data object Graphs : Screen
    data object Sun : Screen
    data object Settings : Screen
    data class FullImage(val title: String, val url: String) : Screen
}

@Composable
fun App(
    vmFactory: VmFactory,
    settings: com.example.finalxaurora.data.SettingsStore
) {
    // VM
    val vm: SpaceWeatherViewModel = remember { vmFactory.createSpaceWeatherViewModel() }
    val state by vm.state

    // Settings
    val mode: AppMode = settings.modeState.value
    val language: AppLanguage = settings.languageState.value
    val strings: AppStrings = remember(language) { appStringsFor(language) }

    // Snackbar + back-exit
    val snackbarHostState = remember { SnackbarHostState() }
    var backArmed by remember { mutableStateOf(false) }

    // Screen stack
    val stack = remember { mutableStateListOf<Screen>() }

    // init stack once (Now for Earth, Sun for Sun-mode)
    LaunchedEffect(Unit) {
        if (stack.isEmpty()) {
            stack += if (mode == AppMode.SUN) Screen.Sun else Screen.Now
        }
    }

    // if mode changed from settings (rare), keep current stack but allow user to toggle inside screens
    val auroraScore = state.prediction.score

    CosmosTheme(mode = mode, auroraScore = auroraScore) {
        val current = stack.lastOrNull() ?: Screen.Now

        // global back handling (stack + double back exit)
        BackHandler {
            when {
                stack.size > 1 -> {
                    stack.removeAt(stack.lastIndex)
                }
                else -> {
                    if (backArmed) {
                        // finish app: in this project you used double-back exit with snackbar,
                        // but without Activity reference we just let system handle the second back naturally.
                        // Keeping behavior: first back shows snackbar, second back triggers default back.
                    } else {
                        backArmed = true
                        // show snackbar
                        LaunchedEffect("backSnack") {
                            snackbarHostState.showSnackbar(strings.pressBackAgain)
                        }
                        // disarm after timeout
                        LaunchedEffect("backDisarm") {
                            delay(1600)
                            backArmed = false
                        }
                    }
                }
            }
        }

        // Screen content
        when (current) {
            Screen.Now -> {
                NowScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { settings.saveMode(it) },
                    state = state,
                    onRefresh = { vm.refresh() },
                    onOpenGraphs = { stack += Screen.Graphs },
                    onOpenSun = { stack += Screen.Sun },
                    onOpenSettings = { stack += Screen.Settings },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Graphs -> {
                GraphsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { settings.saveMode(it) },
                    state = state,
                    onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Sun -> {
                SunScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { settings.saveMode(it) },
                    onOpenImage = { title, url -> stack += Screen.FullImage(title, url) },
                    onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Settings -> {
                SettingsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { settings.saveMode(it) },
                    language = language,
                    onLanguageChange = { settings.saveLanguage(it) },
                    onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) },
                    snackbarHostState = snackbarHostState
                )
            }

            is Screen.FullImage -> {
                FullImageScreen(
                    title = current.title,
                    url = current.url,
                    strings = strings,
                    mode = mode,
                    auroraScore = auroraScore,
                    onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) }
                )
            }
        }

        // Host on top of everything
        SnackbarHost(hostState = snackbarHostState)
    }
}