package jp.kamijima.cyberbriefing.core
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.kamijima.cyberbriefing.worker.BriefingWorker
object WorkScheduler {
    fun enqueueBriefing(context: Context) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val req = OneTimeWorkRequestBuilder<BriefingWorker>().setConstraints(constraints).addTag("daily_briefing").build()
        WorkManager.getInstance(context).enqueue(req)
    }
}
