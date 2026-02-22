package com.example.finalxaurora.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalxaurora.data.SpaceWeatherRepository
import com.example.finalxaurora.domain.GraphPoint
import com.example.finalxaurora.domain.GraphSeries
import com.example.finalxaurora.util.ResultX
import com.example.finalxaurora.util.SettingsStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil
import kotlin.math.floor

class SpaceWeatherViewModel(
    private val repo: SpaceWeatherRepository,
    private val settings: SettingsStore
) : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(SpaceWeatherState())
        private set

    private var refreshJob: Job? = null

    init {
        refresh(force = true)
        startAutoRefresh()
    }

    fun refresh(force: Boolean = false) {
        if (state.value.isLoading && !force) return

        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, errorMessage = null)
            when (val res = repo.fetchSnapshot()) {
                is ResultX.Err -> {
                    state.value = state.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
                is ResultX.Ok -> {
                    val snap = res.value
                    val kpSeries = buildKpSeries(snap.kp)
                    val windSeries = buildWindSeries(snap.wind)
                    val bzSeries = buildBzSeries(snap.mag)

                    state.value = state.value.copy(
                        isLoading = false,
                        lastUpdatedMillis = System.currentTimeMillis(),
                        errorMessage = null,
                        kp = snap.kp,
                        wind = snap.wind,
                        mag = snap.mag,
                        prediction = snap.prediction,
                        seriesKp = kpSeries,
                        seriesWind = windSeries,
                        seriesBz = bzSeries
                    )
                }
            }
        }
    }

    fun getSettingsStore(): SettingsStore = settings

    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                refresh()
            }
        }
    }

    private fun buildKpSeries(list: List<com.example.finalxaurora.domain.KpSample>): GraphSeries? {
        if (list.isEmpty()) return null
        val last24 = list.takeLast(24).ifEmpty { list }
        val points = last24.map { GraphPoint(label = labelUtcHH(it.epochMillis), value = it.kp) }
        return GraphSeries(
            title = "Kp",
            unit = "",
            points = points,
            minY = 0.0,
            maxY = 9.0,
            gridStep = 1.0,
            dangerAbove = 5.0
        )
    }

    private fun buildWindSeries(list: List<com.example.finalxaurora.domain.WindSample>): GraphSeries? {
        if (list.isEmpty()) return null
        val last24 = list.takeLast(24).ifEmpty { list }
        val points = last24.map { GraphPoint(label = labelUtcHH(it.epochMillis), value = it.speed) }
        val (minY, maxY) = minMax(points.map { it.value }, pad = 80.0, step = 100.0)
        return GraphSeries(
            title = "Wind",
            unit = "km/s",
            points = points,
            minY = minY,
            maxY = maxY,
            gridStep = 100.0,
            dangerAbove = 700.0
        )
    }

    private fun buildBzSeries(list: List<com.example.finalxaurora.domain.MagSample>): GraphSeries? {
        if (list.isEmpty()) return null
        val last24 = list.takeLast(24).ifEmpty { list }
        val points = last24.map { GraphPoint(label = labelUtcHH(it.epochMillis), value = it.bz) }
        val (minY, maxY) = minMax(points.map { it.value }, pad = 4.0, step = 2.0)
        return GraphSeries(
            title = "Bz",
            unit = "nT",
            points = points,
            minY = minY,
            maxY = maxY,
            gridStep = 2.0,
            dangerBelow = -8.0
        )
    }

    private fun minMax(values: List<Double>, pad: Double, step: Double): Pair<Double, Double> {
        val mn = values.minOrNull() ?: 0.0
        val mx = values.maxOrNull() ?: 0.0
        val minP = floor((mn - pad) / step) * step
        val maxP = ceil((mx + pad) / step) * step
        val minY = if (minP == maxP) minP - step else minP
        val maxY = if (minP == maxP) maxP + step else maxP
        return minY to maxY
    }

    private val utc = TimeZone.getTimeZone("UTC")
    private val hh = SimpleDateFormat("HH", Locale.US).apply { timeZone = utc }

    private fun labelUtcHH(ms: Long): String = hh.format(Date(ms))
}
