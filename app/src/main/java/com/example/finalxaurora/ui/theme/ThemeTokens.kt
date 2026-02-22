package com.example.finalxaurora.ui.theme

import androidx.compose.ui.graphics.Color

data class CosmosColors(
    val bgTop: Color,
    val bgMid: Color,
    val bgBottom: Color,
    val glass: Color,
    val glassStroke: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentSoft: Color,
    val danger: Color,
    val warning: Color,
    val ok: Color
)

object ThemeTokens {
    fun colorsFor(modeIsSun: Boolean, auroraScore: Int): CosmosColors {
        val scoreT = (auroraScore.coerceIn(0, 100)) / 100f

        val deepBlue = Color(0xFF071026)
        val deepBlue2 = Color(0xFF0A1A3A)
        val deepBlue3 = Color(0xFF04101F)

        val sunTop = Color(0xFF0B0D14)
        val sunMid = Color(0xFF141125)
        val sunBot = Color(0xFF070712)

        val bgTop = lerpColor(if (modeIsSun) sunTop else deepBlue, if (modeIsSun) sunTop else Color(0xFF061A2B), scoreT * 0.20f)
        val bgMid = lerpColor(if (modeIsSun) sunMid else deepBlue2, if (modeIsSun) Color(0xFF1A1430) else Color(0xFF0E274B), scoreT * 0.35f)
        val bgBot = lerpColor(if (modeIsSun) sunBot else deepBlue3, if (modeIsSun) Color(0xFF0A0713) else Color(0xFF031326), scoreT * 0.25f)

        val accentLow = if (modeIsSun) Color(0xFF8C6BFF) else Color(0xFF4AA3FF)
        val accentHigh = if (modeIsSun) Color(0xFFB8FFCA) else Color(0xFF34FF8C)
        val accent = lerpColor(accentLow, accentHigh, scoreT)

        return CosmosColors(
            bgTop = bgTop,
            bgMid = bgMid,
            bgBottom = bgBot,
            glass = Color(0x33FFFFFF),
            glassStroke = Color(0x26FFFFFF),
            textPrimary = Color(0xFFEAF0FF),
            textSecondary = Color(0xBFEAF0FF),
            accent = accent,
            accentSoft = Color(accent.red, accent.green, accent.blue, 0.35f),
            danger = Color(0xFFFF4D6D),
            warning = Color(0xFFFFB020),
            ok = Color(0xFF2CFF88)
        )
    }

    private fun lerpColor(a: Color, b: Color, t: Float): Color {
        val clamped = t.coerceIn(0f, 1f)
        return Color(
            red = a.red + (b.red - a.red) * clamped,
            green = a.green + (b.green - a.green) * clamped,
            blue = a.blue + (b.blue - a.blue) * clamped,
            alpha = a.alpha + (b.alpha - a.alpha) * clamped
        )
    }
}
