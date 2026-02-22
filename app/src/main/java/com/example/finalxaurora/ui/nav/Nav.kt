package com.example.finalxaurora.ui.nav

import androidx.compose.runtime.Immutable

@Immutable
data class NavState(
    val stack: List<Screen>
) {
    val current: Screen get() = stack.last()
    fun canGoBack(): Boolean = stack.size > 1
}

class NavController(
    initial: Screen
) {
    private var _stack: MutableList<Screen> = mutableListOf(initial)
    val state: NavState get() = NavState(_stack.toList())

    fun push(screen: Screen) {
        _stack.add(screen)
    }

    fun pop(): Boolean {
        if (_stack.size <= 1) return false
        _stack.removeAt(_stack.lastIndex)
        return true
    }

    fun replaceRoot(screen: Screen) {
        _stack = mutableListOf(screen)
    }
}
