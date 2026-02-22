package com.example.finalxaurora.domain

enum class AppMode { EARTH, SUN }
enum class AppLanguage { EN, RU }

data class KpSample(val epochMillis: Long, val kp: Double)
data class WindSample(val epochMillis: Long, val speed: Double)
data class MagSample(val epochMillis: Long, val bx: Double, val bz: Double)

data class Prediction(
    val score: Int,
    val title: String,
    val description: String
)

data class SpaceWeatherSnapshot(
    val kp: List<KpSample>,
    val wind: List<WindSample>,
    val mag: List<MagSample>,
    val prediction: Prediction
)

data class GraphPoint(
    val label: String,
    val value: Double
)

data class GraphSeries(
    val title: String,
    val unit: String,
    val points: List<GraphPoint>,
    val minY: Double,
    val maxY: Double,
    val gridStep: Double,
    val dangerAbove: Double? = null,
    val dangerBelow: Double? = null
)
