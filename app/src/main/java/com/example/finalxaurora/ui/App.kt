package com.example.finalxaurora.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModelFactory
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.delay

sealed class Screen {
    data object Now : Screen()
    data object Sun : Screen()
    data object Graphs : Screen()
    data object Settings : Screen()
    data class FullImage(val title: String, val url: String) : Screen()
}

@Composable
fun App(
    vmFactory: SpaceWeatherViewModelFactory,
    settings: SettingsStore
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var mode by remember { mutableStateOf(settings.loadMode()) }
    var language by remember { mutableStateOf(settings.loadLanguage()) }

    val strings = remember(language) { stringsFor(language) }

    val vm: SpaceWeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = vmFactory)
    val state = vm.state

    // back stack
    val stack: SnapshotStateList<Screen> = remember { mutableStateListOf(Screen.Now) }
    val current = stack.last()

    fun push(s: Screen) {
        // push должен менять state прямо сейчас — иначе и бывают “залипания”
        stack.add(s)
    }
    fun pop(): Boolean {
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            true
        } else false
    }

    // Переключение режима (Earth/Sun) должно менять главный экран без лагов
    fun setMode(newMode: AppMode) {
        if (newMode == mode) return
        mode = newMode
        settings.saveMode(newMode)

        // Если пользователь на базовом экране — меняем на соответствующий режиму.
        // Если он в Settings/Graphs/FullImage — не дёргаем.
        val top = stack.lastOrNull()
        if (top == Screen.Now || top == Screen.Sun) {
            stack.removeAt(stack.lastIndex)
            stack.add(if (newMode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

    // Double back to exit
    var backArmed by remember { mutableStateOf(false) }
    BackHandler {
        val didPop = pop()
        if (!didPop) {
            if (backArmed) {
                // дать системе закрыть Activity
            } else {
                backArmed = true
                // коротко, без “спама”
                snackbarHostState.showSnackbar(strings.pressBackAgain)
                // авто-сброс
                LaunchedEffect(Unit) {
                    delay(1300)
                    backArmed = false
                }
            }
        }
    }

    CosmosTheme(mode = mode, auroraScore = state.prediction.score) {
        // SnackbarHost показываем один раз глобально
        androidx.compose.foundation.layout.Box {
            when (current) {
                is Screen.Now -> NowScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    state = state,
                    onRefresh = { vm.refresh() },
                    onOpenGraphs = { push(Screen.Graphs) },
                    onOpenSun = { push(Screen.Sun) },
                    onOpenSettings = { push(Screen.Settings) },
                    snackbarHostState = snackbarHostState
                )

                is Screen.Sun -> SunScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    onOpenImage = { t, u -> push(Screen.FullImage(t, u)) },
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )

                is Screen.Graphs -> GraphsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    state = state,
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )

                is Screen.Settings -> SettingsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = ::setMode,
                    language = language,
                    onLanguageChange = {
                        language = it
                        settings.saveLanguage(it)
                    },
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )

                is Screen.FullImage -> {
                    val s = current as Screen.FullImage
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
}