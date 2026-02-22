package com.example.finalxaurora.ui.strings

import com.example.finalxaurora.domain.AppLanguage

fun stringsFor(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.EN -> AppStrings(
            appName = "FinalXAurora",
            ok = "OK",
            info = "Info",
            back = "Back",

            now = "Now",
            graphs = "Graphs",
            sun = "Sun",
            settings = "Settings",

            refresh = "Refresh",
            open = "Open",

            updated = "Updated",
            error = "Error",
            backAgainToExit = "Back again to exit",

            kpIndex = "Kp Index",
            windSpeed = "Solar Wind",
            bz = "Bz",
            bt = "Bt",
            bField = "B-Field",
            bFieldLong = "Direction of the solar wind magnetic field lines",

            auroraScore = "Aurora Score",

            language = "Language",
            english = "English",
            russian = "Russian",
            modeEarth = "Earth",
            modeSun = "Sun",

            cme = "CME",
            sunspots = "Sunspots",
            auroraOval = "Aurora Oval",

            helpKpTitle = "Kp Index",
            helpKpBody =
                "Kp is a geomagnetic activity index (0–9). Higher Kp usually means brighter aurora and visibility at lower latitudes.",

            helpWindTitle = "Solar Wind Speed",
            helpWindBody =
                "Solar wind speed affects how strongly the magnetosphere is disturbed. Higher speed often means stronger activity.",

            helpBtTitle = "Bt (Total Field)",
            helpBtBody =
                "Bt is the total magnetic field magnitude (from Bx and Bz). Higher Bt often means more energy in the system.",

            helpBFieldTitle = "Magnetic Field Direction",
            helpBFieldBody =
                "The compass shows field direction: horizontal axis is Bx, vertical axis is Bz.\n" +
                    "When Bz is southward (negative), aurora activity often increases."
        )

        AppLanguage.RU -> AppStrings(
            appName = "FinalXAurora",
            ok = "ОК",
            info = "Инфо",
            back = "Назад",

            now = "Сейчас",
            graphs = "Графики",
            sun = "Солнце",
            settings = "Настройки",

            refresh = "Обновить",
            open = "Открыть",

            updated = "Обновлено",
            error = "Ошибка",
            backAgainToExit = "Ещё раз — выход",

            kpIndex = "Kp индекс",
            windSpeed = "Солнечный ветер",
            bz = "Bz",
            bt = "Bt",
            bField = "B-поле",
            bFieldLong = "Направление магнитных линий солнечного ветра",

            auroraScore = "Аврора-скор",

            language = "Язык",
            english = "Английский",
            russian = "Русский",
            modeEarth = "Земля",
            modeSun = "Солнце",

            cme = "КВМ",
            sunspots = "Пятна",
            auroraOval = "Овал Авроры",

            helpKpTitle = "Kp индекс",
            helpKpBody =
                "Kp — индекс геомагнитной активности (0–9). Чем выше Kp, тем выше шанс яркого сияния и тем южнее оно может быть видно.",

            helpWindTitle = "Солнечный ветер",
            helpWindBody =
                "Скорость солнечного ветра влияет на «давление» на магнитосферу. Выше скорость — чаще сильнее возмущения.",

            helpBtTitle = "Bt (суммарное поле)",
            helpBtBody =
                "Bt — общая сила магнитного поля (по Bx и Bz). Обычно: выше Bt = больше энергии в системе.",

            helpBFieldTitle = "Направление поля",
            helpBFieldBody =
                "Компас показывает направление поля: по горизонтали Bx, по вертикали Bz.\n" +
                    "Когда Bz «южный» (отрицательный), сияние часто усиливается."
        )
    }
}