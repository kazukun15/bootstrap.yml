package jp.kamijima.cyberbriefing
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import jp.kamijima.cyberbriefing.car.AutoConnectionService
import jp.kamijima.cyberbriefing.notify.BriefingNotifier

class MainActivity : ComponentActivity() {
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            startForegroundServiceCompat()
            // 権限取得後に画面は閉じない（テスト用）
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            BriefingNotifier(this).notifyMessage(getString(R.string.test_message))
        }

        val perms = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        requestPermissions.launch(perms.toTypedArray())
    }
    
    private fun startForegroundServiceCompat() {
        val intent = Intent(this, AutoConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { startForegroundService(intent) } else { startService(intent) }
    }
}
