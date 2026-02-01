package jp.kamijima.cyberbriefing.notify
object BriefingComposer {
    fun composeCyber(mm: String, dd: String, onThisDay: String?, weather: String?): String {
        val datePart = "${mm.toInt()}月${dd.toInt()}日"
        val today = onThisDay?.let { it.replace(Regex("\\s+"), " ").trim() } ?: "過去データなし"
        val w = weather?.let { it.replace(Regex("\\s+"), " ").trim() } ?: "気象データなし"
        return "システム通知。本日は${datePart}。${today}。現在地は、${w}。安全運転を開始してください。"
    }
}
