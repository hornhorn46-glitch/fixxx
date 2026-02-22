package com.example.finalxaurora.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.screens.FullImageScreen
import com.example.finalxaurora.ui.screens.GraphsScreen
import com.example.finalxaurora.ui.screens.NowScreen
import com.example.finalxaurora.ui.screens.SettingsScreen
import com.example.finalxaurora.ui.screens.SunScreen
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.strings.stringsFor
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Now : Screen
    data object Graphs : Screen
    data object Sun : Screen
    data object Settings : Screen
    data class FullImage(val title: String, val url: String) : Screen
}

@Composable
fun App(
    vmFactory: VmFactory,
    settings: SettingsStore
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var mode by remember { mutableStateOf(settings.loadMode()) }
    var language by remember { mutableStateOf(settings.loadLanguage()) }

    val strings: AppStrings = remember(language) { stringsFor(language) }

    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)
    val state by vm.state
    val auroraScore = state.prediction.score

    val stack = remember {
        mutableStateListOf<Screen>().apply {
            add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

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

    fun resetToHome() {
        stack.clear()
        stack.add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
    }

    fun finishApp() {
        (context as? Activity)?.finish()
    }

    LaunchedEffect(mode) { settings.saveMode(mode) }
    LaunchedEffect(language) { settings.saveLanguage(language) }

    // Double-back exit (без зависимостей от AppStrings)
    var backArmed by remember { mutableStateOf(false) }
    val backAgainMsg = remember(language) {
        if (language == AppLanguage.RU) "Нажми ещё раз, чтобы выйти" else "Press back again to exit"
    }

    BackHandler {
        val popped = pop()
        if (popped) return@BackHandler

        if (backArmed) {
            finishApp()
        } else {
            backArmed = true
            scope.launch { snackbarHostState.showSnackbar(backAgainMsg) }
            scope.launch {
                delay(1600)
                backArmed = false
            }
        }
    }

    val current = stack.lastOrNull() ?: Screen.Now

    CosmosTheme(mode = mode, auroraScore = auroraScore) {
        when (current) {
            Screen.Now -> {
                NowScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { newMode ->
                        mode = newMode
                        resetToHome()
                    },
                    state = state,
                    onRefresh = { vm.refresh() },
                    onOpenGraphs = { push(Screen.Graphs) },
                    onOpenSun = { push(Screen.Sun) },
                    onOpenSettings = { push(Screen.Settings) },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Graphs -> {
                GraphsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { newMode ->
                        mode = newMode
                        resetToHome()
                    },
                    state = state,
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Sun -> {
                SunScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { newMode ->
                        mode = newMode
                        resetToHome()
                    },
                    onOpenImage = { title, url ->
                        push(Screen.FullImage(title, url))
                    },
                    onBack = { pop() },
                    snackbarHostState = snackbarHostState
                )
            }

            Screen.Settings -> {
                SettingsScreen(
                    strings = strings,
                    mode = mode,
                    onModeChange = { newMode ->
                        mode = newMode
                    },
                    language = language,
                    onLanguageChange = { newLang ->
                        language = newLang
                    },
                    onBack = { pop() },
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
                    onBack = { pop() }
                )
            }
        }
    }
}