package jp.kamijima.cyberbriefing.car
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale

class BriefingSession : Session(), DefaultLifecycleObserver {
    var tts: TextToSpeech? = null

    override fun onCreateScreen(intent: Intent): Screen {
        lifecycle.addObserver(this)
        tts = TextToSpeech(carContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPAN
            }
        }
        return BriefingScreen(carContext, this)
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        tts?.shutdown()
    }
}
