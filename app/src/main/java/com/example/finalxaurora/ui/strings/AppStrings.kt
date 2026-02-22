package com.example.finalxaurora.ui.strings

data class AppStrings(
    // App / common
    val appName: String,
    val ok: String,
    val info: String,
    val back: String,

    // Tabs / screens
    val now: String,
    val graphs: String,
    val sun: String,
    val settings: String,

    // Actions
    val refresh: String,
    val open: String,

    // Status
    val updated: String,
    val error: String,
    val backAgainToExit: String,

    // Labels
    val kpIndex: String,
    val windSpeed: String,
    val bz: String,
    val bt: String,
    val bField: String,
    val bFieldLong: String, // “Направление магнитных линий ...”

    val auroraScore: String,

    // Settings
    val language: String,
    val english: String,
    val russian: String,
    val modeEarth: String,
    val modeSun: String,

    // Sun screen
    val cme: String,
    val sunspots: String,
    val auroraOval: String,

    // Help (titles + bodies)
    val helpKpTitle: String,
    val helpKpBody: String,
    val helpWindTitle: String,
    val helpWindBody: String,
    val helpBtTitle: String,
    val helpBtBody: String,
    val helpBFieldTitle: String,
    val helpBFieldBody: String,
)