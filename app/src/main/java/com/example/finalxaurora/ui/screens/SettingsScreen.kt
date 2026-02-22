package com.example.finalxaurora.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode
import com.example.finalxaurora.ui.components.AuroraBackground
import com.example.finalxaurora.ui.components.CosmosTopBar
import com.example.finalxaurora.ui.components.GlassCard
import com.example.finalxaurora.ui.components.ModeToggle
import com.example.finalxaurora.ui.strings.AppStrings
import com.example.finalxaurora.ui.theme.LocalCosmosTheme

@Composable
fun SettingsScreen(
    strings: AppStrings,
    mode: AppMode,
    onModeChange: (AppMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val c = LocalCosmosTheme.current.colors
    AuroraBackground(mode = mode)

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CosmosTopBar(
                title = strings.settings,
                onBack = onBack
            ) {
                ModeToggle(mode = mode, onToggle = onModeChange)
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(text = strings.language, color = c.textSecondary)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageChange(AppLanguage.EN) }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = strings.english + if (language == AppLanguage.EN) " ✓" else "",
                            color = c.textPrimary
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageChange(AppLanguage.RU) }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = strings.russian + if (language == AppLanguage.RU) " ✓" else "",
                            color = c.textPrimary
                        )
                    }
                }
            }
        }
    }
}