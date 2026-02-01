package jp.kamijima.cyberbriefing.notify
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import jp.kamijima.cyberbriefing.MainActivity
import jp.kamijima.cyberbriefing.R

class BriefingNotifier(private val context: Context) {
    private val channelId = "daily_briefing_chat_v3"
    
    fun notifyMessage(text: String) {
        ensureChannel()
        
        // 1. Person定義（これが送信者名になる）
        val sender = Person.Builder()
            .setName("Cyber Briefing") // カードの一番上に出る名前
            .setKey("cyber_system")
            .setBot(true)
            .build()
        
        // 2. 返信アクション（これがAutoに「会話」だと認識させる鍵）
        val remoteInput = RemoteInput.Builder("key_reply")
            .setLabel("返信")
            .build()
        
        val replyIntent = Intent(context, ReplyReceiver::class.java)
        val replyPendingIntent = PendingIntent.getBroadcast(
            context, 0, replyIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send,
            "返信",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        // 3. メッセージスタイルの構築
        // 現在時刻のメッセージとして追加
        val style = NotificationCompat.MessagingStyle(sender)
            .addMessage(text, System.currentTimeMillis(), sender)
        
        // 4. 通知発行
        val n = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(style)
            .addAction(replyAction) // 重要！
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_MAX) 
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
            
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1001, n)
    }
    
    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        // 重要度HIGHで音とバイブレーションを有効化
        val channel = NotificationChannel(channelId, "Briefing Chat", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Chats appearing on dashboard"
            enableVibration(true)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
}
