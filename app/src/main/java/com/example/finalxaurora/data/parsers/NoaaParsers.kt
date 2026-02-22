package com.example.finalxaurora.data.parsers

import com.example.finalxaurora.domain.KpSample
import com.example.finalxaurora.domain.MagSample
import com.example.finalxaurora.domain.WindSample
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object NoaaParsers {

    private val utc = TimeZone.getTimeZone("UTC")
    private val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    ).onEach { it.timeZone = utc }

    fun parseKp(json: String): List<KpSample> {
        return runCatching {
            val arr = JSONArray(json)
            if (arr.length() <= 1) return emptyList()
            val out = ArrayList<KpSample>(arr.length())
            for (i in 1 until arr.length()) {
                val row = arr.optJSONArray(i) ?: continue
                val ts = row.optString(0)
                val kpStr = row.optString(1)
                val t = parseUtcMillis(ts) ?: continue
                val kp = kpStr.toDoubleOrNull() ?: continue
                out.add(KpSample(epochMillis = t, kp = kp))
            }
            out
        }.getOrElse { emptyList() }
    }

    fun parseWind(json: String): List<WindSample> {
        return runCatching {
            val arr = JSONArray(json)
            if (arr.length() <= 1) return emptyList()
            val out = ArrayList<WindSample>(arr.length())
            for (i in 1 until arr.length()) {
                val row = arr.optJSONArray(i) ?: continue
                val ts = row.optString(0)
                val speedStr = row.optString(2)
                val t = parseUtcMillis(ts) ?: continue
                val speed = speedStr.toDoubleOrNull() ?: continue
                out.add(WindSample(epochMillis = t, speed = speed))
            }
            out
        }.getOrElse { emptyList() }
    }

    fun parseMag(json: String): List<MagSample> {
        return runCatching {
            val arr = JSONArray(json)
            if (arr.length() <= 1) return emptyList()
            val out = ArrayList<MagSample>(arr.length())
            for (i in 1 until arr.length()) {
                val row = arr.optJSONArray(i) ?: continue
                val ts = row.optString(0)
                val bxStr = row.optString(1)
                val bzStr = row.optString(3)
                val t = parseUtcMillis(ts) ?: continue
                val bx = bxStr.toDoubleOrNull() ?: continue
                val bz = bzStr.toDoubleOrNull() ?: continue
                out.add(MagSample(epochMillis = t, bx = bx, bz = bz))
            }
            out
        }.getOrElse { emptyList() }
    }

    private fun parseUtcMillis(s: String): Long? {
        for (f in formats) {
            val d = runCatching { f.parse(s) }.getOrNull()
            if (d != null) return d.time
        }
        return null
    }
}
