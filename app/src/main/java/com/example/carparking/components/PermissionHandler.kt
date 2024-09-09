import android.app.Activity
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

class PermissionHandler(private val activity: Activity) {

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun checkAndRequestLocationPermission(onPermissionGranted: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Permission is granted, fetch location
            onPermissionGranted()
        } else {
            // Request location permissions
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    // Optionally show explanation to the user
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        onPermissionGranted()  // Permission granted, fetch location
                    } else {
                        // Handle permission denial
                        Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(onLocationReceived: (Location?) -> Unit) {
        // Check if permission is granted, then fetch the location
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Pass the location to the callback
                onLocationReceived(location)
            }.addOnFailureListener {
                // Handle failure in obtaining location
                Toast.makeText(activity, "Failed to get location", Toast.LENGTH_SHORT).show()
                onLocationReceived(null)
            }
        } else {
            Toast.makeText(activity, "Location permission is not granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun handlePermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
