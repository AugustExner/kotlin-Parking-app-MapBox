package com.example.carparking.components1.MapComponents

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.navigationtest.PermissionHandler

class LocationHelper(private val context: Context) {

    // Method to fetch user location with a callback to handle the result
    fun getUserLocation(onLocationFetched: (Location?) -> Unit) {
        val permissionHandler = PermissionHandler(context as Activity)

        // Check and request location permission
        permissionHandler.checkAndRequestPermissions {
            // Fetch location once permission is granted
            permissionHandler.fetchLocation { location ->
                Log.d("LocationHelper", "Location: $location")
                // Call the provided callback function with the location
                onLocationFetched(location)
            }
        }
    }
}
