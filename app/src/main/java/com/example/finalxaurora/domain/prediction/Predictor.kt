package com.example.finalxaurora.domain.prediction

import com.example.finalxaurora.domain.KpSample
import com.example.finalxaurora.domain.MagSample
import com.example.finalxaurora.domain.Prediction
import com.example.finalxaurora.domain.WindSample
import kotlin.math.abs
import kotlin.math.roundToInt

object Predictor {

    fun predictAurora3h(
        kp: List<KpSample>,
        wind: List<WindSample>,
        mag: List<MagSample>
    ): Prediction {
        val kpNow = kp.lastOrNull()?.kp ?: 0.0
        val speedNow = wind.lastOrNull()?.speed ?: 350.0
        val bzNow = mag.lastOrNull()?.bz ?: 0.0

        val kpScore = (kpNow / 9.0).coerceIn(0.0, 1.0)
        val speedScore = ((speedNow - 300.0) / 700.0).coerceIn(0.0, 1.0)
        val bzScore = ((-bzNow) / 20.0).coerceIn(0.0, 1.0)

        val raw = 0.50 * kpScore + 0.30 * speedScore + 0.20 * bzScore
        val score = (raw * 100.0).roundToInt().coerceIn(0, 100)

        val title = when {
            score >= 80 -> "Strong aurora potential"
            score >= 55 -> "Moderate aurora potential"
            score >= 30 -> "Low aurora potential"
            else -> "Quiet conditions"
        }

        val bzHint = when {
            bzNow <= -10 -> "Bz is strongly southward (favorable)."
            bzNow <= -5 -> "Bz is southward (favorable)."
            abs(bzNow) < 3 -> "Bz is near neutral."
            else -> "Bz is northward (unfavorable)."
        }

        val desc = "Kp ${kpNow.format1()} • Wind ${speedNow.format0()} km/s • Bz ${bzNow.format1()} nT. $bzHint"

        return Prediction(score = score, title = title, description = desc)
    }

    private fun Double.format1(): String = String.format("%.1f", this)
    private fun Double.format0(): String = String.format("%.0f", this)
}
