import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.os.Looper
import androidx.compose.runtime.*
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.example.carparking.components.MapsTest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.log

// Constants
const val LOCATION_TAG = "LocationUpdates"
const val REQUEST_LOCATION_PERMISSION = 1

// A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback

// The main entry point for interacting with the Fused Location Provider.
lateinit var locationProvider: FusedLocationProviderClient

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("MissingPermission")
suspend fun getUserLocation(context: Context): LatLng = suspendCancellableCoroutine { continuation ->

    // The Fused Location Provider provides access to location APIs.
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    var isContinuationResumed = false

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            // This will be called whenever the location is updated
            val location = result.locations.lastOrNull() // Get the most recent location
            if (location != null && !isContinuationResumed) {
                val latLng = LatLng(location.latitude, location.longitude)
                Log.d(LOCATION_TAG, "Location obtained: $latLng")
                continuation.resume(latLng) // Resume the coroutine with the obtained location
                isContinuationResumed = true
            }
        }
    }

    // Ensure the callback is removed if the coroutine is cancelled
    continuation.invokeOnCancellation {
        locationProvider.removeLocationUpdates(locationCallback)
    }

    // Check for permissions and start requesting location updates
    if (hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    } else {
        askPermissions(context as ComponentActivity, REQUEST_LOCATION_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        continuation.resumeWithException(SecurityException("Location permissions not granted"))
    }
}

fun stopLocationUpdate() {
    try {
        // Removes all location updates for the given callback.
        val removeTask = locationProvider.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOCATION_TAG, "Location Callback removed.")
            } else {
                Log.d(LOCATION_TAG, "Failed to remove Location Callback.")
            }
        }
    } catch (se: SecurityException) {
        Log.e(LOCATION_TAG, "Failed to remove Location Callback.. $se")
    }
}

@SuppressLint("MissingPermission")
fun locationUpdate() {
    locationCallback.let {
        // An encapsulation of various parameters for requesting
        // location through FusedLocationProviderClient.
        val locationRequest: LocationRequest =
            LocationRequest.create().apply {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(30)
                maxWaitTime = TimeUnit.MINUTES.toMillis(2)
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }
        // Use FusedLocationProviderClient to request location updates.
        locationProvider.requestLocationUpdates(
            locationRequest,
            it,
            Looper.getMainLooper()
        )
    }
}


fun getReadableLocation(latitude: Double, longitude: Double, context: Context): String {
    var addressText = ""
    val geocoder = Geocoder(context, Locale.getDefault())

    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            addressText = "${address.getAddressLine(0)}, ${address.locality}"
            // Use the addressText in your app.
            Log.d("geolocation", addressText)
        }
    } catch (e: IOException) {
        Log.d("geolocation", e.message.toString())
    }

    return addressText
}

fun hasPermissions(context: Context, vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun askPermissions(context: ComponentActivity, requestCode: Int, vararg permissions: String) {
    ActivityCompat.requestPermissions(context, permissions, requestCode)
}

@Composable
fun PermissionAwareLocationDisplay(context: ComponentActivity) {
    // Ensure permissions are granted
    if (hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        MapsTest(context = context)
    } else {
        askPermissions(
            context, REQUEST_LOCATION_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
