package com.example.finalxaurora.ui.strings

import com.example.finalxaurora.domain.AppLanguage

fun appStringsFor(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.RU -> RuStrings
        AppLanguage.EN -> EnStrings
    }
}