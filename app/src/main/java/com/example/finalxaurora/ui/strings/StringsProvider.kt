package com.example.finalxaurora.ui.strings

import com.example.finalxaurora.domain.AppLanguage

fun stringsFor(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.EN -> AppStrings(
            now = "Now",
            graphs = "Graphs",
            sun = "Sun",
            settings = "Settings",
            refresh = "Refresh",
            updated = "Updated",
            error = "Error",
            kpIndex = "Kp Index",
            windSpeed = "Solar Wind",
            bz = "Bz",
            bField = "B-Field",
            auroraScore = "Aurora Score",
            language = "Language",
            english = "English",
            russian = "Russian",
            modeEarth = "Earth",
            modeSun = "Sun",
            cme = "CME",
            sunspots = "Sunspots",
            auroraOval = "Aurora Oval",
            open = "Open",
            backAgainToExit = "Back again to exit"
        )
        AppLanguage.RU -> AppStrings(
            now = "Сейчас",
            graphs = "Графики",
            sun = "Солнце",
            settings = "Настройки",
            refresh = "Обновить",
            updated = "Обновлено",
            error = "Ошибка",
            kpIndex = "Kp индекс",
            windSpeed = "Солнечный ветер",
            bz = "Bz",
            bField = "B-поле",
            auroraScore = "Аврора-скор",
            language = "Язык",
            english = "Английский",
            russian = "Русский",
            modeEarth = "Земля",
            modeSun = "Солнце",
            cme = "КВМ",
            sunspots = "Пятна",
            auroraOval = "Овал Авроры",
            open = "Открыть",
            backAgainToExit = "Ещё раз — выход"
        )
    }
}
