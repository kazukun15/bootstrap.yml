package jp.kamijima.cyberbriefing.net
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
class OnThisDayClient {
    private val http = OkHttpClient()
    suspend fun fetchOneEventJa(mm: String, dd: String): String? {
        val url = "https://api.wikimedia.org/feed/v1/wikipedia/ja/onthisday/events/$mm/$dd"
        val req = Request.Builder().url(url).header("User-Agent", "CyberDailyBriefing/1.0").build()
        http.newCall(req).execute().use { res ->
            if (!res.isSuccessful) return null
            val body = res.body?.string() ?: return null
            val events = JSONObject(body).optJSONArray("events") ?: return null
            var best: String? = null
            var bestLen = Int.MAX_VALUE
            for (i in 0 until events.length()) {
                val text = events.getJSONObject(i).optString("text").trim()
                if (text.isBlank()) continue
                val len = text.length
                if (len in 25..80 && len < bestLen) { best = text; bestLen = len }
            }
            if (best == null && events.length() > 0) best = events.getJSONObject(0).optString("text").trim().ifBlank { null }
            return best
        }
    }
}
