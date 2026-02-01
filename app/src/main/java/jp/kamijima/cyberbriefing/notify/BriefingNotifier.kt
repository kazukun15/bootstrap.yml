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
    // チャンネルIDを変更して強制的に設定を更新させる
    private val channelId = "daily_briefing_v2"
    
    fun notifyMessage(text: String) {
        ensureChannel()
        val systemPerson = Person.Builder().setName("Cyber Briefing").setBot(true).build()
        
        // メッセージスタイルの構築
        val style = NotificationCompat.MessagingStyle(systemPerson)
            .setConversationTitle("今日のブリーフィング") // 会話タイトル
            .addMessage(text, System.currentTimeMillis(), systemPerson)
        
        val intent = Intent(context, MainActivity::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        val pi = PendingIntent.getActivity(context, 0, intent, flags)
        
        // 通知ビルダー (Heads-up設定)
        val n = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("今日のブリーフィング")
            .setContentText(text)
            .setStyle(style)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            // 重要度MAX: これでAndroid Auto上でポップアップを誘発する
            .setPriority(NotificationCompat.PRIORITY_MAX) 
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            // バイブレーション設定 (ポップアップ条件のクリア用)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
            
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1001, n)
    }
    
    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        // チャンネル重要度をHIGHに設定 (Heads-up必須条件)
        val channel = NotificationChannel(channelId, "Daily Briefing High", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "ポップアップ通知を行うためのチャンネル"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
}
