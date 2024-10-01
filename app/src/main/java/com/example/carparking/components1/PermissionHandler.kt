package com.example.navigationtest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    fun checkAndRequestPermissions(onPermissionGranted: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(activity) && isNotificationPermissionGranted()) {
            // Both location and notification permissions are granted
            onPermissionGranted()
        } else {
            // Request permissions if not granted
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    // Optionally show explanation to the user
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted && isNotificationPermissionGranted()) {
                        onPermissionGranted()  // Permissions granted, continue the flow
                    } else {
                        // Handle permission denial
                        Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(onLocationReceived: (Location?) -> Unit) {
        // Check if location permission is granted
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

    // Check if notification permission is granted
    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No need to request notification permission on Android versions lower than 13
        }
    }

    // Request both location and notification permissions
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Add location permission if not granted
        if (!PermissionsManager.areLocationPermissionsGranted(activity)) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Add notification permission if required and not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Request permissions if there are any permissions left to request
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 0)
        }
    }
}