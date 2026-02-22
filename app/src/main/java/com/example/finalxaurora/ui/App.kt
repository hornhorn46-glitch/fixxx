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
import com.example.finalxaurora.ui.strings.StringsFactory
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

    // Settings state (stored in SharedPreferences via SettingsStore)
    var mode by remember { mutableStateOf(settings.loadMode()) }
    var language by remember { mutableStateOf(settings.loadLanguage()) }
    val strings: AppStrings = remember(language) { StringsFactory.stringsFor(language) }

    // ViewModel from factory (your VmFactory is a ViewModelProvider.Factory)
    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)
    val state by vm.state
    val auroraScore = state.prediction.score

    // Navigation stack (non-empty from start => fixes crash)
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

    // Persist settings whenever changed
    LaunchedEffect(mode) { settings.saveMode(mode) }
    LaunchedEffect(language) { settings.saveLanguage(language) }

    // Double-back exit
    var backArmed by remember { mutableStateOf(false) }

    BackHandler {
        val popped = pop()
        if (popped) return@BackHandler

        if (backArmed) {
            finishApp()
        } else {
            backArmed = true
            scope.launch { snackbarHostState.showSnackbar(strings.pressBackAgain) }
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
                    onOpenSun = {
                        // explicit open sun screen (button)
                        push(Screen.Sun)
                    },
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
                        // settings screen stays, theme updates automatically
                    },
                    language = language,
                    onLanguageChange = { newLang ->
                        language = newLang
                        // keep on settings; strings will update via remember(language)
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