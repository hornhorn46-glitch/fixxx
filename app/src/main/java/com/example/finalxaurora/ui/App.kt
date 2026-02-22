package com.example.finalxaurora.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.launch

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
    val strings = remember(language) { AppStrings.forLanguage(language) }

    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)
    val state by vm.state

    // --- Simple in-app navigation (stack) ---
    sealed interface Screen {
        data object Now : Screen
        data object Graphs : Screen
        data object Settings : Screen
        data object Sun : Screen
        data class FullImage(val title: String, val url: String) : Screen
    }

    val stack = remember { mutableStateListOf<Screen>() }

    fun homeForMode(m: AppMode): Screen = if (m == AppMode.SUN) Screen.Sun else Screen.Now

    // Ensure non-empty stack (fixes "List is empty" crash)
    LaunchedEffect(Unit) {
        if (stack.isEmpty()) {
            stack.add(homeForMode(mode))
        }
    }

    fun resetToHome() {
        val home = homeForMode(mode)
        stack.clear()
        stack.add(home)
    }

    fun push(screen: Screen) {
        stack.add(screen)
    }

    fun pop(): Boolean {
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            true
        } else {
            false
        }
    }

    // If mode changes, move to соответствующий home
    LaunchedEffect(mode) {
        if (stack.isEmpty()) {
            stack.add(homeForMode(mode))
        } else {
            // аккуратно: оставляем стек валидным
            val desiredHome = homeForMode(mode)
            if (stack.first() != desiredHome) {
                stack.clear()
                stack.add(desiredHome)
            }
        }
    }

    // Double-back-to-exit
    var lastBackMs by remember { mutableLongStateOf(0L) }

    fun finishApp(ctx: Context) {
        (ctx as? Activity)?.finish()
    }

    BackHandler {
        val popped = pop()
        if (!popped) {
            val now = System.currentTimeMillis()
            if (now - lastBackMs < 1800L) {
                finishApp(context)
            } else {
                lastBackMs = now
                scope.launch {
                    snackbarHostState.showSnackbar(strings.backAgainToExit)
                }
            }
        }
    }

    // App theme wrapper
    CosmosTheme(mode = mode, auroraScore = state.prediction.score) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { _ ->
            val screen = stack.lastOrNull() ?: homeForMode(mode)

            when (screen) {
                is Screen.Now -> {
                    NowScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            mode = newMode
                            settings.saveMode(newMode)
                            resetToHome()
                        },
                        state = state,
                        onRefresh = { vm.refresh() },
                        onOpenGraphs = { push(Screen.Graphs) },
                        onOpenSun = {
                            mode = AppMode.SUN
                            settings.saveMode(mode)
                            resetToHome()
                        },
                        onOpenSettings = { push(Screen.Settings) },
                        snackbarHostState = snackbarHostState
                    )
                }

                is Screen.Graphs -> {
                    GraphsScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            mode = newMode
                            settings.saveMode(newMode)
                            resetToHome()
                        },
                        state = state,
                        onBack = { pop() },
                        snackbarHostState = snackbarHostState
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            mode = newMode
                            settings.saveMode(newMode)
                            resetToHome()
                        },
                        language = language,
                        onLanguageChange = { newLang ->
                            language = newLang
                            settings.saveLanguage(newLang)
                        },
                        onBack = { pop() },
                        snackbarHostState = snackbarHostState
                    )
                }

                is Screen.Sun -> {
                    SunScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            mode = newMode
                            settings.saveMode(newMode)
                            resetToHome()
                        },
                        onOpenImage = { title, url ->
                            push(Screen.FullImage(title, url))
                        },
                        onBack = { pop() },
                        snackbarHostState = snackbarHostState
                    )
                }

                is Screen.FullImage -> {
                    FullImageScreen(
                        title = screen.title,
                        url = screen.url,
                        strings = strings,
                        mode = mode,
                        auroraScore = state.prediction.score,
                        onBack = { pop() }
                    )
                }
            }
        }
    }
}