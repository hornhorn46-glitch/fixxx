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
            bt = "Bt",

            bField = "B-Field",
            bFieldDirection = "Solar wind magnetic field direction",

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

            backAgainToExit = "Back again to exit",

            ok = "OK",
            info = "Info",

            helpKpBody =
                "Kp is a geomagnetic activity index (0–9). Higher Kp usually means higher chance of bright aurora and it can be visible farther south.",
            helpWindBody =
                "Solar wind speed affects how strongly the solar wind pushes the magnetosphere. Higher speed can increase disturbances.",
            helpBtBody =
                "Bt is the total magnetic field strength (from Bx and Bz). In general: higher Bt means more energy in the system.",
            helpBFieldBody =
                "Compass shows field direction: horizontal axis is Bx, vertical axis is Bz.\nWhen Bz is southward (negative), aurora often becomes stronger."
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
            bt = "Bt",

            bField = "B-поле",
            bFieldDirection = "Направление магнитных линий солнечного ветра",

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

            backAgainToExit = "Ещё раз — выход",

            ok = "ОК",
            info = "Инфо",

            helpKpBody =
                "Kp — индекс геомагнитной активности (0–9). Чем выше Kp, тем выше шанс яркого сияния и тем южнее его можно увидеть.",
            helpWindBody =
                "Скорость солнечного ветра показывает, насколько сильно поток давит на магнитосферу. Выше скорость — чаще сильнее возмущения.",
            helpBtBody =
                "Bt — общая сила магнитного поля (по Bx и Bz). Обычно: выше Bt = больше энергии в системе.",
            helpBFieldBody =
                "Компас показывает направление поля: по горизонтали Bx, по вертикали Bz.\nКогда Bz «южный» (отрицательный), сияние часто усиливается."
        )
    }
}