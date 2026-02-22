package com.example.finalxaurora.ui.vm

import androidx.compose.runtime.Immutable
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.domain.MagSample
import com.example.finalxaurora.domain.Prediction
import com.example.finalxaurora.domain.WindSample
import com.example.finalxaurora.domain.KpSample

@Immutable
data class SpaceWeatherState(
    val isLoading: Boolean = false,
    val lastUpdatedMillis: Long? = null,
    val errorMessage: String? = null,

    val kp: List<KpSample> = emptyList(),
    val wind: List<WindSample> = emptyList(),
    val mag: List<MagSample> = emptyList(),

    val prediction: Prediction = Prediction(
        score = 0,
        title = "—",
        description = "—"
    ),

    val seriesKp: GraphSeries? = null,
    val seriesWind: GraphSeries? = null,
    val seriesBz: GraphSeries? = null
)
