package com.example.finalxaurora.data

import com.example.finalxaurora.util.ResultX
import okhttp3.OkHttpClient
import okhttp3.Request

class SpaceWeatherApi(private val client: OkHttpClient) {

    fun get(url: String): ResultX<String> {
        return try {
            val req = Request.Builder().url(url).get().build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    ResultX.Err("HTTP ${resp.code} for $url")
                } else {
                    val body = resp.body?.string().orEmpty()
                    ResultX.Ok(body)
                }
            }
        } catch (t: Throwable) {
            ResultX.Err("Network error", t)
        }
    }

    companion object {
        const val URL_KP_1M = "https://services.swpc.noaa.gov/products/noaa-planetary-k-index.json"
        const val URL_WIND = "https://services.swpc.noaa.gov/products/solar-wind/plasma-1-day.json"
        const val URL_MAG = "https://services.swpc.noaa.gov/products/solar-wind/mag-1-day.json"

        const val URL_SUN_CME = "https://services.swpc.noaa.gov/images/animations/lasco-c3/latest.gif"
        const val URL_SUN_SPOTS = "https://services.swpc.noaa.gov/images/sunspot-region-summary.jpg"
        const val URL_AURORA_OVAL = "https://services.swpc.noaa.gov/images/animations/ovation-north/latest.gif"
    }
}
