package com.example.carparking.components1.navigation

import android.app.PendingIntent
import android.content.Context
import android.os.Looper
import com.mapbox.bindgen.ExpectedFactory
import com.mapbox.common.Cancelable
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.DeviceLocationProviderFactory
import com.mapbox.common.location.GetLocationCallback
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProvider
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.navigation.base.options.LocationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp


class MyLocationProvider(private val request: LocationProviderRequest) : LocationProvider, DeviceLocationProvider {

    fun start() {
        // Implement start location updates
    }

    fun stop() {
        // Implement stop location updates
    }

    fun requestLocationUpdates() {
        // Implement request location updates
    }

    fun getLastKnownLocation(): Location? {
        // Return the last known location
        return null
    }

    override fun addLocationObserver(observer: LocationObserver) {
        // Implement adding location observer
    }

    override fun addLocationObserver(observer: LocationObserver, looper: Looper) {
        // Implement adding location observer with looper
    }

    override fun getLastLocation(callback: GetLocationCallback): Cancelable {
        // Implement get last location
        return object : Cancelable {
            override fun cancel() {
                // Implement cancel
            }
        }
    }

    override fun removeLocationObserver(observer: LocationObserver) {
        // Implement removing location observer
    }

    override fun removeLocationUpdates(pendingIntent: PendingIntent) {
        // Implement removing location updates with pending intent
    }

    override fun requestLocationUpdates(pendingIntent: PendingIntent) {
        // Implement requesting location updates with pending intent
    }
}

fun createNavigationOptions(context: Context): NavigationOptions {
    return NavigationOptions.Builder(context)
        .locationOptions(
            LocationOptions.Builder()
                .locationProviderFactory(
                    DeviceLocationProviderFactory { request ->
                        ExpectedFactory.createValue(request?.let { MyLocationProvider(it) }!!)
                    },
                    LocationOptions.LocationProviderType.MIXED // Use the appropriate location provider type
                )
                .build()
        )
        .build()
}

fun setupMapboxNavigation(context: Context) {
    val navigationOptions = createNavigationOptions(context)

    if (!MapboxNavigationApp.isSetup()) {
        MapboxNavigationApp.setup {
            navigationOptions
        }
    }
}
