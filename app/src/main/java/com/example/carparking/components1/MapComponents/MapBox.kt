package com.example.carparking.components1.MapComponents

import PermissionHandler
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.R
import com.example.carparking.components1.parkingoverview.ParkingViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage

@Composable
fun MapBoxTest(
    context: Context,
    parkingViewModel: ParkingViewModel = viewModel(),
    destinationCoordinates: List<Double>? = null // Accept coordinates as parameter
) {
    val parkingSpots = parkingViewModel.parkingSpots

    val markerResourceId by remember {
        mutableIntStateOf(R.drawable.red_marker)
    }

    val context123 = LocalContext.current
    val permissionHandler = PermissionHandler(context123 as Activity)
    var location by remember { mutableStateOf<Location?>(null) }
    permissionHandler.checkAndRequestLocationPermission {
        permissionHandler.fetchLocation {
            location = it  // Update the location state
            Log.d(TAG, "Fetched Location: $location")
        }
    }

    val mapViewportState = rememberMapViewportState()

    // Update the camera location based on user location or search destination
    LaunchedEffect(location, destinationCoordinates) {
        // Log the current location when it changes
        location?.let { loc ->
            Log.d(
                "MapBoxTest",
                "Current Location: Longitude=${loc.longitude}, Latitude=${loc.latitude}"
            )
            mapViewportState.setCameraOptions {
                center(Point.fromLngLat(loc.longitude, loc.latitude))
                zoom(12.0)
            }
        } ?: Log.d("MapBoxTest", "Location is null, not updating to user location.")

        // If destination coordinates are provided, update the camera to the destination
        destinationCoordinates?.let { coords ->
            Log.d(
                "MapBoxTest",
                "Destination Coordinates: Longitude=${coords[0]}, Latitude=${coords[1]}"
            )
            val destinationPoint = Point.fromLngLat(coords[0], coords[1])
            mapViewportState.setCameraOptions {
                center(destinationPoint)
                zoom(12.0)
            }
        } ?: Log.d("MapBoxTest", "No destination coordinates provided.")
    }

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    ) {
        // Add a marker for the user's current location if available
        location?.let { loc ->
            val marker = rememberIconImage(
                key = markerResourceId,
                painter = painterResource(markerResourceId)
            )
            PointAnnotation(Point.fromLngLat(loc.longitude, loc.latitude)) {
                iconImage = marker
            }
        } ?: Log.d("MapBoxTest", "No location available to add marker.")

        // Render parking spots on the map
        parkingSpots.map {
            CustomMapBoxMarker(parkingSpot = it)
        }
    }
}
