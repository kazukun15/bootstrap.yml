package jp.kamijima.cyberbriefing.car
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.car.app.connection.CarConnection
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import jp.kamijima.cyberbriefing.R
import jp.kamijima.cyberbriefing.core.DailyExecutionGuard
import jp.kamijima.cyberbriefing.core.WorkScheduler
class AutoConnectionService : LifecycleService() {
    private var lastState: Int = CarConnection.CONNECTION_TYPE_NOT_CONNECTED
    private val CHANNEL_ID = "monitor_channel"
    private val NOTIFICATION_ID = 999
    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        val guard = DailyExecutionGuard(this)
        CarConnection(applicationContext).type.observe(this) { state ->
            val prev = lastState
            lastState = state
            if (prev != CarConnection.CONNECTION_TYPE_PROJECTION && state == CarConnection.CONNECTION_TYPE_PROJECTION) {
                if (guard.shouldRunToday()) { WorkScheduler.enqueueBriefing(applicationContext) }
            }
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }
    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, getString(R.string.notification_channel_monitor), NotificationManager.IMPORTANCE_MIN))
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.monitor_service_title))
            .setContentText(getString(R.string.monitor_service_desc))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }
}
