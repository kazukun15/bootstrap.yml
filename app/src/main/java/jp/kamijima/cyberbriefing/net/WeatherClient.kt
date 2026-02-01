package jp.kamijima.cyberbriefing.net
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.roundToInt
class WeatherClient {
    private val http = OkHttpClient()
    suspend fun fetchWeatherSummary(lat: Double, lon: Double): String? {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&hourly=precipitation_probability,temperature_2m&current_weather=true&timezone=Asia%2FTokyo"
        http.newCall(Request.Builder().url(url).build()).execute().use { res ->
            if (!res.isSuccessful) return null
            val body = res.body?.string() ?: return null
            val json = JSONObject(body)
            val current = json.optJSONObject("current_weather")
            val hourly = json.optJSONObject("hourly")
            val probs = hourly?.optJSONArray("precipitation_probability")
            val tempNow = current?.optDouble("temperature", Double.NaN) ?: Double.NaN
            var maxProb = -1
            if (probs != null) {
                val n = minOf(6, probs.length())
                for (i in 0 until n) { val p = probs.optInt(i, -1); if (p > maxProb) maxProb = p }
            }
            val tStr = if (!tempNow.isNaN()) "${tempNow.roundToInt()}度" else null
            val probStr = when {
                maxProb >= 70 -> "雨の確率は高いです"
                maxProb >= 40 -> "雨の可能性があります"
                maxProb >= 20 -> "一時的に降るかもしれません"
                maxProb in 0..19 -> "降雨の心配はありません"
                else -> null
            }
            return listOfNotNull(probStr, tStr?.let { "気温$it" }).joinToString("、").ifBlank { null }
        }
    }
}
