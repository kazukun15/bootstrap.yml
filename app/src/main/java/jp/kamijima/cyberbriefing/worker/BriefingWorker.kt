package jp.kamijima.cyberbriefing.worker
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import jp.kamijima.cyberbriefing.net.LocationProvider
import jp.kamijima.cyberbriefing.net.OnThisDayClient
import jp.kamijima.cyberbriefing.net.WeatherClient
import jp.kamijima.cyberbriefing.notify.BriefingComposer
import jp.kamijima.cyberbriefing.notify.BriefingNotifier
class BriefingWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val loc = LocationProvider(applicationContext).getCurrentLocationOrNull()
            val (mm, dd) = DateUtil.todayMonthDay()
            val onThisDay = OnThisDayClient().fetchOneEventJa(mm, dd)
            val weather = if (loc != null) WeatherClient().fetchWeatherSummary(loc.latitude, loc.longitude) else null
            val text = BriefingComposer.composeCyber(mm, dd, onThisDay, weather)
            BriefingNotifier(applicationContext).notifyMessage(text)
            Result.success()
        } catch (_: Exception) { Result.failure() }
    }
}
