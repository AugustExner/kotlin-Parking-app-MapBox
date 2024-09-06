package com.example.carparking.utils

import android.app.Activity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

class PermissionHandler(private val activity: Activity) {

    private lateinit var permissionsManager: PermissionsManager

    fun checkAndRequestLocationPermission(onPermissionGranted: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Permission is granted, run your location-sensitive logic
            onPermissionGranted()
        } else {
            // Request location permissions
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    // Optionally show explanation to the user
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        onPermissionGranted()  // Permission granted, run logic
                    } else {
                        // Handle permission denial
                    }
                }
            })
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    fun handlePermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
