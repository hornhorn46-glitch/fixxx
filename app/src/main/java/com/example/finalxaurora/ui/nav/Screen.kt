package com.example.finalxaurora.ui.nav

sealed class Screen {
    data object Now : Screen()
    data object Graphs : Screen()
    data object Sun : Screen()
    data object Settings : Screen()
    data class FullImage(val title: String, val url: String) : Screen()
}
