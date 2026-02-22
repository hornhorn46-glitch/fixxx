package com.example.finalxaurora.ui.strings

data class AppStrings(
    val now: String,
    val graphs: String,
    val sun: String,
    val settings: String,
    val refresh: String,
    val updated: String,
    val error: String,

    val kpIndex: String,
    val windSpeed: String,

    // оставляем для совместимости, хотя на главном показываем Bt
    val bz: String,
    val bt: String,

    // старое имя оставим (могут быть другие места), но добавляем новое "человеческое"
    val bField: String,
    val bFieldDirection: String,

    val auroraScore: String,

    val language: String,
    val english: String,
    val russian: String,
    val modeEarth: String,
    val modeSun: String,

    val cme: String,
    val sunspots: String,
    val auroraOval: String,
    val open: String,

    val backAgainToExit: String,

    // UI
    val ok: String,
    val info: String,

    // Help texts
    val helpKpBody: String,
    val helpWindBody: String,
    val helpBtBody: String,
    val helpBFieldBody: String
)