package jp.kamijima.cyberbriefing.notify
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import jp.kamijima.cyberbriefing.MainActivity
import jp.kamijima.cyberbriefing.R
class BriefingNotifier(private val context: Context) {
    private val channelId = "daily_briefing"
    fun notifyMessage(text: String) {
        ensureChannel()
        val systemPerson = Person.Builder().setName("SYSTEM").build()
        val style = NotificationCompat.MessagingStyle(systemPerson).setConversationTitle("Daily Briefing").addMessage(text, System.currentTimeMillis(), systemPerson)
        val intent = Intent(context, MainActivity::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        val pi = PendingIntent.getActivity(context, 0, intent, flags)
        val n = NotificationCompat.Builder(context, channelId).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Daily Briefing").setContentText(text).setStyle(style).setContentIntent(pi).setAutoCancel(true).setCategory(NotificationCompat.CATEGORY_MESSAGE).setPriority(NotificationCompat.PRIORITY_HIGH).build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1001, n)
    }
    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel(channelId, "Daily Briefing", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "Android Auto 向けの1日1回ブリーフィング" })
    }
}
