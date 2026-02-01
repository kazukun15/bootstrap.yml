package jp.kamijima.cyberbriefing.net
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
data class SimpleLocation(val latitude: Double, val longitude: Double)
class LocationProvider(private val context: Context) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOrNull(): SimpleLocation? {
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            client.lastLocation.addOnSuccessListener { loc -> cont.resume(loc?.let { SimpleLocation(it.latitude, it.longitude) }) }.addOnFailureListener { cont.resume(null) }
        }
    }
}
