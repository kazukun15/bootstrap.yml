package jp.kamijima.cyberbriefing.car
import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.lifecycle.lifecycleScope
import jp.kamijima.cyberbriefing.net.LocationProvider
import jp.kamijima.cyberbriefing.net.OnThisDayClient
import jp.kamijima.cyberbriefing.net.WeatherClient
import jp.kamijima.cyberbriefing.notify.BriefingComposer
import kotlinx.coroutines.launch
import java.time.LocalDate

class BriefingScreen(carContext: CarContext, private val session: BriefingSession) : Screen(carContext) {
    private var message: String = "データを取得しています..."
    private var isLoading = true

    init {
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val loc = LocationProvider(carContext).getCurrentLocationOrNull()
                val d = LocalDate.now()
                val mm = d.monthValue.toString().padStart(2, '0')
                val dd = d.dayOfMonth.toString().padStart(2, '0')
                val onThisDay = OnThisDayClient().fetchOneEventJa(mm, dd)
                val weather = if (loc != null) WeatherClient().fetchWeatherSummary(loc.latitude, loc.longitude) else null
                message = BriefingComposer.composeCyber(mm, dd, onThisDay, weather)
            } catch (e: Exception) {
                message = "エラーが発生しました: ${e.localizedMessage}"
            } finally {
                isLoading = false
                invalidate()
                speak()
            }
        }
    }

    private fun speak() {
        session.tts?.speak(message, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "briefing")
    }

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()
        
        if (isLoading) {
            paneBuilder.setLoading(true)
        } else {
            paneBuilder.addRow(Row.Builder().setTitle("Cyber Daily Briefing").addText(message).build())
            paneBuilder.addAction(
                Action.Builder()
                    .setTitle("読み上げ")
                    .setOnClickListener { speak() }
                    .build()
            )
        }

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle("Cyber Briefing")
            .build()
    }
}
