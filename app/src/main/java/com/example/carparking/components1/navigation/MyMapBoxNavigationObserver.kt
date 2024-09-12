package com.example.carparking.components1.navigation

import PermissionHandler
import android.app.Activity
import android.content.Context
import android.util.Log
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver

class MyMapboxNavigationObserver(
    private val context: Context,
    private val activity: Activity
) : MapboxNavigationObserver {

    // Pass a lambda function to handle location updates
    private val locationObserver = MyLocationObserver { location ->
        // Handle the location update here
        // For example, you could log the location or update some state
        Log.d("MyMapboxNavigationObserver", "Location Update: Lat ${location.latitude}, Lon ${location.longitude}")
    }

    private var isTripSessionStarted = false

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        val permissionHandler = PermissionHandler(activity)
        permissionHandler.checkAndRequestLocationPermission {
            mapboxNavigation.registerLocationObserver(locationObserver)

            // Start Trip only if it hasn't been started already
            if (!isTripSessionStarted) {
                mapboxNavigation.startTripSession()
                isTripSessionStarted = true
            }
        }
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.unregisterLocationObserver(locationObserver)

        // Optionally stop the trip session when detached
        if (isTripSessionStarted) {
            mapboxNavigation.stopTripSession()
            isTripSessionStarted = false
        }
    }
}
