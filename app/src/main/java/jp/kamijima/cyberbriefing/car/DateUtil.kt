package jp.kamijima.cyberbriefing.car
import java.time.LocalDate
object DateUtil {
    fun todayMonthDay(): Pair<String, String> {
        val d = LocalDate.now()
        return d.monthValue.toString().padStart(2, '0') to d.dayOfMonth.toString().padStart(2, '0')
    }
}
