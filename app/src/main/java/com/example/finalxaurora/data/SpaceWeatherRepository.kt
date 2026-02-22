package com.example.finalxaurora.data

import com.example.finalxaurora.data.parsers.NoaaParsers
import com.example.finalxaurora.domain.SpaceWeatherSnapshot
import com.example.finalxaurora.domain.prediction.Predictor
import com.example.finalxaurora.util.ResultX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpaceWeatherRepository(private val api: SpaceWeatherApi) {

    suspend fun fetchSnapshot(): ResultX<SpaceWeatherSnapshot> = withContext(Dispatchers.IO) {
        val kpRes = api.get(SpaceWeatherApi.URL_KP_1M)
        val windRes = api.get(SpaceWeatherApi.URL_WIND)
        val magRes = api.get(SpaceWeatherApi.URL_MAG)

        if (kpRes is ResultX.Err) return@withContext ResultX.Err(kpRes.message, kpRes.throwable)
        if (windRes is ResultX.Err) return@withContext ResultX.Err(windRes.message, windRes.throwable)
        if (magRes is ResultX.Err) return@withContext ResultX.Err(magRes.message, magRes.throwable)

        val kp = NoaaParsers.parseKp((kpRes as ResultX.Ok).value)
        val wind = NoaaParsers.parseWind((windRes as ResultX.Ok).value)
        val mag = NoaaParsers.parseMag((magRes as ResultX.Ok).value)

        val prediction = Predictor.predictAurora3h(kp, wind, mag)

        ResultX.Ok(
            SpaceWeatherSnapshot(
                kp = kp,
                wind = wind,
                mag = mag,
                prediction = prediction
            )
        )
    }
}
