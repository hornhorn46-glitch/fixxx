package com.example.finalxaurora.util

import android.content.SharedPreferences
import com.example.finalxaurora.domain.AppLanguage
import com.example.finalxaurora.domain.AppMode

class SettingsStore(private val prefs: SharedPreferences) {

    fun loadMode(): AppMode {
        val raw = prefs.getString(KEY_MODE, AppMode.EARTH.name) ?: AppMode.EARTH.name
        return raw.toAppModeOrDefault()
    }

    fun saveMode(mode: AppMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
    }

    fun loadLanguage(): AppLanguage {
        val raw = prefs.getString(KEY_LANG, AppLanguage.EN.name) ?: AppLanguage.EN.name
        return raw.toAppLanguageOrDefault()
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANG, language.name).apply()
    }

    private fun String.toAppModeOrDefault(): AppMode =
        AppMode.entries.firstOrNull { it.name == this } ?: AppMode.EARTH

    private fun String.toAppLanguageOrDefault(): AppLanguage =
        AppLanguage.entries.firstOrNull { it.name == this } ?: AppLanguage.EN

    private companion object {
        const val KEY_MODE = "mode"
        const val KEY_LANG = "lang"
    }
}
