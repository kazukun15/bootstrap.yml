package jp.kamijima.cyberbriefing
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import jp.kamijima.cyberbriefing.car.AutoConnectionService
import jp.kamijima.cyberbriefing.notify.BriefingNotifier
import jp.kamijima.cyberbriefing.notify.BriefingComposer
import jp.kamijima.cyberbriefing.net.*
import jp.kamijima.cyberbriefing.car.DateUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            startForegroundServiceCompat()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            // 手動でデータを取得して通知を出す
            lifecycleScope.launch {
                try {
                    val loc = LocationProvider(this@MainActivity).getCurrentLocationOrNull()
                    val (mm, dd) = DateUtil.todayMonthDay()
                    val onThisDay = OnThisDayClient().fetchOneEventJa(mm, dd)
                    val weather = if (loc != null) WeatherClient().fetchWeatherSummary(loc.latitude, loc.longitude) else null
                    val text = BriefingComposer.composeCyber(mm, dd, onThisDay, weather)
                    
                    BriefingNotifier(this@MainActivity).notifyMessage(text)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
