package com.example.finalxaurora.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.finalxaurora.ui.theme.CosmosTheme
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.data.SettingsStore

/**
 * Custom navigation stack (no navigation-compose).
 * Fix: never call stack.last() when stack can be empty.
 */
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
    val vm: SpaceWeatherViewModel = vmFactory.spaceWeather()

    // App settings (these are intentionally accessed in a defensive way)
    val mode: AppMode = settings.loadMode()
    val language: AppLanguage = settings.loadLanguage()

    val strings: AppStrings = AppStrings.forLanguage(language)

    val snackbarHostState = remember { SnackbarHostState() }

    // ---- Navigation stack ----
    val stack = remember { mutableStateListOf<Screen>() }

    // Ensure non-empty stack (THIS fixes the crash)
    LaunchedEffect(Unit) {
        if (stack.isEmpty()) {
            // стартовый экран можно выбирать как угодно:
            // - если mode == SUN → Sun
            // - иначе → Now
            stack.add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

    // Always read current screen safely
    val current: Screen = stack.lastOrNull() ?: Screen.Now

    // Double-back-to-exit logic (only on root)
    var lastBackAt by remember { mutableLongStateOf(0L) }

    fun popOrExit() {
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackAt < 1600L) {
                // Let Activity handle finish (BackHandler will fall through)
                // We do nothing here: caller should not intercept.
            } else {
                lastBackAt = now
                // If you have strings.pressBackAgain — use it; otherwise fallback.
                // We avoid compile issues by using safe text:
                val msg = runCatching { strings.pressBackAgain }.getOrNull()
                    ?: "Press back again to exit"
                // show snackbar
                // (must be launched, because showSnackbar is suspend)
                vm.launchSnack(snackbarHostState, msg)
            }
        }
    }

    // Root BackHandler (only intercept when we actually want)
    BackHandler(enabled = true) {
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackAt < 1600L) {
                // allow system back to close activity
                // by disabling interception effectively:
                // simplest: do nothing and rely on Activity default? — but BackHandler intercepts.
                // so we just call finish via vmFactory hook if you have it;
                // if нет — то лучше отключить BackHandler в Activity.
                vmFactory.finishActivity()
            } else {
                lastBackAt = now
                val msg = runCatching { strings.pressBackAgain }.getOrNull()
                    ?: "Press back again to exit"
                vm.launchSnack(snackbarHostState, msg)
            }
        }
    }

    // Theme must use CURRENT mode and score
    val state by vm.state
    val auroraScore = state.prediction.score

    CosmosTheme(mode = mode, auroraScore = auroraScore) {
        Surface {
            when (current) {
                Screen.Now -> {
                    NowScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            settings.saveMode(newMode)
                            // keep user on a meaningful root when switching modes
                            stack.clear()
                            stack.add(if (newMode == AppMode.SUN) Screen.Sun else Screen.Now)
                        },
                        state = state,
                        onRefresh = { vm.refresh() },
                        onOpenGraphs = { stack.add(Screen.Graphs) },
                        onOpenSun = { stack.add(Screen.Sun) },
                        onOpenSettings = { stack.add(Screen.Settings) },
                        snackbarHostState = snackbarHostState
                    )
                }

                Screen.Graphs -> {
                    GraphsScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            settings.saveMode(newMode)
                            // allow switch even here
                            stack.clear()
                            stack.add(if (newMode == AppMode.SUN) Screen.Sun else Screen.Now)
                        },
                        state = state,
                        onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) },
                        snackbarHostState = snackbarHostState
                    )
                }

                Screen.Sun -> {
                    SunScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            settings.saveMode(newMode)
                            stack.clear()
                            stack.add(if (newMode == AppMode.SUN) Screen.Sun else Screen.Now)
                        },
                        onOpenImage = { title, url -> stack.add(Screen.FullImage(title, url)) },
                        onBack = { if (stack.size > 1) stack.removeAt(stack.lastIndex) },
                        snackbarHostState = snackbarHostState
                    )
                }

                Screen.Settings -> {
                    SettingsScreen(
                        strings = strings,
                        mode = mode,
                        onModeChange = { newMode ->
                            settings.saveMode(newMode)
                            // stay on settings, but update theme next recomposition
                        },
                        language = language,
                        onLanguageChange = { newLang ->
                            settings.saveLanguage(newLang)
                            // rebuild UI by forcing a root reset
                            stack.clear()
                            stack.add(if (settings.loadMode() == AppMode.SUN) Screen.Sun else Screen.Now)
                        },
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

            // Host snackbar above everything
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}