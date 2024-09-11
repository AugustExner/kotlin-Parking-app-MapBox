import android.location.Location
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
        TODO("Not yet implemented")
    }

    override fun onNewRawLocation(rawLocation: com.mapbox.common.location.Location) {
        TODO("Not yet implemented")
    }
}
