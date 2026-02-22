package com.example.finalxaurora.ui.strings

import com.example.finalxaurora.domain.AppLanguage

object StringsFactory {

    fun forLanguage(lang: AppLanguage): AppStrings = when (lang) {
        AppLanguage.RU -> ru()
        AppLanguage.EN -> en()
    }

    private fun ru() = AppStrings(
        appName = "FinalXAurora",
        earth = "Земля",
        sun = "Солнце",
        now = "Сейчас",
        graphs = "Графики",
        events = "События",
        settings = "Настройки",
        refresh = "Обновить",
        updated = "обновлено:",
        pressBackAgain = "Нажми ещё раз",
        backAgainToExit = "Нажми ещё раз, чтобы выйти",
        open = "Открыть",
        tapToOpen = "Нажми, чтобы открыть",
        error = "Ошибка",
        noData = "нет данных",
        prediction = "Прогноз сияний (3 часа)",
        parametersNow = "Параметры (сейчас)",
        bFieldCompass = "Компас B-field (Bx/Bz)",
        auroraScore = "Aurora Score",
        kp = "Kp",
        speed = "Speed",
        bz = "Bz",
        rho = "ρ",
        cme = "CME",
        sunspots = "Пятна",
        auroraOval = "Овал сияний"
    )

    private fun en() = AppStrings(
        appName = "FinalXAurora",
        earth = "Earth",
        sun = "Sun",
        now = "Now",
        graphs = "Graphs",
        events = "Events",
        settings = "Settings",
        refresh = "Refresh",
        updated = "updated:",
        pressBackAgain = "Press again",
        backAgainToExit = "Press again to exit",
        open = "Open",
        tapToOpen = "Tap to open",
        error = "Error",
        noData = "no data",
        prediction = "Aurora forecast (3 hours)",
        parametersNow = "Parameters (now)",
        bFieldCompass = "B-field compass (Bx/Bz)",
        auroraScore = "Aurora Score",
        kp = "Kp",
        speed = "Speed",
        bz = "Bz",
        rho = "ρ",
        cme = "CME",
        sunspots = "Sunspots",
        auroraOval = "Aurora oval"
    )
}