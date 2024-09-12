package com.example.carparking.components1.navigation

import android.util.Log
import com.mapbox.common.location.Location as MapboxLocation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver

class MyLocationObserver(
    private val onLocationUpdate: (MapboxLocation) -> Unit
) : LocationObserver {

    override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
        val location = locationMatcherResult.enhancedLocation
        Log.d("MyLocationObserver", "New Location: Lat ${location.latitude}, Lon ${location.longitude}")
        onLocationUpdate(location)
    }

    override fun onNewRawLocation(rawLocation: MapboxLocation) {
        Log.d("MyLocationObserver", "New Raw Location: Lat ${rawLocation.latitude}, Lon ${rawLocation.longitude}")
        onLocationUpdate(rawLocation)
    }

}
