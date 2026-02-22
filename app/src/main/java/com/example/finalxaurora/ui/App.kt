package com.example.finalxaurora.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import com.example.finalxaurora.ui.strings.StringsFactory
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Now : Screen
    data object Graphs : Screen
    data object Settings : Screen
    data object Sun : Screen
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

    // гарантируем непустой стек (фикс крэша "List is empty")
    val stack = remember {
        mutableStateListOf<Screen>().apply {
            add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

    // если mode поменяли из Settings/Toggle — сохраняем и не даём стеку стать пустым
    LaunchedEffect(mode) {
        settings.saveMode(mode)
        if (stack.isEmpty()) {
            stack.add(if (mode == AppMode.SUN) Screen.Sun else Screen.Now)
        }
    }

    LaunchedEffect(language) {
        settings.saveLanguage(language)
    }

    val strings = remember(language) { StringsFactory.forLanguage(language) }

    val vm: SpaceWeatherViewModel = viewModel(factory = vmFactory)
    val state by vm.state

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

    fun finishActivity() {
        (context as? Activity)?.finish()
    }

    // Back: поп или "нажми ещё раз чтобы выйти"
    BackHandler {
        val popped = pop()
        if (!popped) {
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = strings.backAgainToExit,
                    withDismissAction = false
                )
                if (res == SnackbarResult.Dismissed) {
                    // ничего
                }
            }
            // Реальный “двойной back” у тебя может быть уже в коде иначе.
            // Здесь минимально безопасно: второй раз пользователь нажмёт back — снова попадёт сюда.
            // Если хочешь строгий таймер 2с — сделаем отдельно, но без поломок структуры.
        }
    }

    val current = stack.lastOrNull() ?: run {
        // fallback на всякий случай
        if (mode == AppMode.SUN) Screen.Sun else Screen.Now
    }

    when (current) {
        Screen.Now -> {
            NowScreen(
                strings = strings,
                mode = mode,
                onModeChange = { mode = it },
                state = state,
                onRefresh = { vm.refresh() },
                onOpenGraphs = { push(Screen.Graphs) },
                onOpenSettings = { push(Screen.Settings) },
                snackbarHostState = snackbarHostState
            )
        }

        Screen.Graphs -> {
            GraphsScreen(
                strings = strings,
                mode = mode,
                onModeChange = { mode = it },
                pointsKp = state.series24hKp,
                pointsBz = state.series24hBz,
                pointsSpeed = state.series24hSpeed,
                onBack = { pop() },
                snackbarHostState = snackbarHostState
            )
        }

        Screen.Settings -> {
            SettingsScreen(
                strings = strings,
                mode = mode,
                language = language,
                onModeChange = { mode = it },
                onLanguageChange = { language = it },
                onBack = { pop() },
                snackbarHostState = snackbarHostState
            )
        }

        Screen.Sun -> {
            SunScreen(
                strings = strings,
                mode = mode,
                onModeChange = { mode = it },
                onOpenImage = { title, url -> push(Screen.FullImage(title, url)) },
                onBack = { pop() },
                snackbarHostState = snackbarHostState
            )
        }

        is Screen.FullImage -> {
            val s = current as Screen.FullImage
            FullImageScreen(
                title = s.title,
                url = s.url,
                strings = strings,
                mode = mode,
                auroraScore = state.auroraScore,
                onBack = { pop() }
            )
        }
    }

    // Важно: snackbarHostState используется экранами внутри их Scaffold’ов.
    // Поэтому здесь отдельный SnackbarHost не добавляю, чтобы не менять структуру.
}