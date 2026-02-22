package com.example.finalxaurora.util

import kotlin.math.roundToInt

object Format {
    fun intOrDash(v: Double?): String = if (v == null || v.isNaN()) "—" else v.roundToInt().toString()
    fun oneDecOrDash(v: Double?): String = if (v == null || v.isNaN()) "—" else String.format("%.1f", v)
    fun unit(value: String, unit: String): String = if (value == "—") "—" else "$value $unit"
}
