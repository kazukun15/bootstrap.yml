package jp.kamijima.cyberbriefing.notify
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 返信ボタンが押されたら、通知を既読（削除）にする
        // これで「話し終わったら消える」挙動になる
        NotificationManagerCompat.from(context).cancel(1001)
    }
}
