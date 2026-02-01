package jp.kamijima.cyberbriefing
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import jp.kamijima.cyberbriefing.car.AutoConnectionService
class MainActivity : ComponentActivity() {
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            startForegroundServiceCompat()
            finish()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val perms = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        requestPermissions.launch(perms.toTypedArray())
    }
    private fun startForegroundServiceCompat() {
        val intent = Intent(this, AutoConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { startForegroundService(intent) } else { startService(intent) }
    }
}
