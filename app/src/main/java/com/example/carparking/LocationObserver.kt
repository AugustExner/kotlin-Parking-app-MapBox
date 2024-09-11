import android.location.Location
import android.util.Log
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver

class MyLocationObserver : LocationObserver {

    fun onRawLocationChanged(rawLocation: Location) {
        // Handle device's raw GPS location here
        println("Raw location: ${rawLocation.latitude}, ${rawLocation.longitude}")
    }

    fun onEnhancedLocationChanged(
        enhancedLocation: Location,
        keyPoints: List<Location>
    ) {
        // Handle enhanced location here
        println("Enhanced location: ${enhancedLocation.latitude}, ${enhancedLocation.longitude}")
    }

    override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
        // Extract the location from the LocationMatcherResult
        val location = locationMatcherResult.enhancedLocation

        // Do something with the location, like updating UI or processing data
        location.let {
            // For example, you could log the location or update a live data object
            Log.d("LocationObserver", "New Location: Lat ${it.latitude}, Lon ${it.longitude}")

            // If you have a live data or state to update:
            // _currentLocation.value = it
        }
    }

    override fun onNewRawLocation(rawLocation: com.mapbox.common.location.Location) {
        // Convert the rawLocation into a usable format if necessary
        val latitude = rawLocation.latitude
        val longitude = rawLocation.longitude

        // Do something with the raw location data, like logging or updating UI
        Log.d("LocationObserver", "New Raw Location: Lat $latitude, Lon $longitude")

        // Update any observers or live data
        // _rawLocation.value = rawLocation
    }
}
