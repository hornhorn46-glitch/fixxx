package com.example.finalxaurora.util

interface Clock {
    fun nowMillis(): Long
}

object SystemClockX : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
