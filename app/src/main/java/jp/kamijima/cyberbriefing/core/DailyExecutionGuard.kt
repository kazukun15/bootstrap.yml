package jp.kamijima.cyberbriefing.core
import android.content.Context
import java.time.LocalDate
class DailyExecutionGuard(context: Context) {
    private val prefs = context.getSharedPreferences("cyber_briefing", Context.MODE_PRIVATE)
    fun shouldRunToday(): Boolean {
        val today = LocalDate.now().toString()
        val last = prefs.getString("last_execution_date", null)
        if (last == today) return false
        prefs.edit().putString("last_execution_date", today).apply()
        return true
    }
}
