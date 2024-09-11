import android.app.Activity
import android.content.Context
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver

class MyMapboxNavigationObserver(
    private val context: Context,
    private val activity: Activity
) : MapboxNavigationObserver {

    private val locationObserver = MyLocationObserver()

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        val permissionHandler = PermissionHandler(activity)
        permissionHandler.checkAndRequestLocationPermission {
            mapboxNavigation.registerLocationObserver(locationObserver)
            mapboxNavigation.startTripSession()
        }
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.stopTripSession()
    }
}


