package com.example.carparking.components.MapComponents

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
import com.example.carparking.components.parkingoverview.ParkingViewModel
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import getUserLocation


@Composable
fun MapBoxTest(context: Context, parkingViewModel: ParkingViewModel = viewModel()) {
    val parkingSpots = parkingViewModel.parkingSpots

    val markerResourceId by remember {
        mutableIntStateOf(R.drawable.red_marker)
    }

    val context123 = LocalContext.current
    val permissionHandler = PermissionHandler(context123 as Activity)
    var location by remember { mutableStateOf<Location?>(null) }
    permissionHandler.checkAndRequestLocationPermission {
        // Once permission is granted, fetch the location
        permissionHandler.fetchLocation {
            location = it  // Update the location state
            Log.d(TAG, "Location: $location")
        }
    }

    val mapViewportState = rememberMapViewportState {
    }

    LaunchedEffect(location) {
        location?.let { loc ->
            // Update camera to center on user location when available
            mapViewportState.setCameraOptions() {
                center(Point.fromLngLat(loc.longitude, loc.latitude))
                zoom(12.0)
            }
        }
    }

    // Render the map regardless of userLocation
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    ) {
        // Once the location is fetched, show the user marker
        if (location != null) {
            val marker = rememberIconImage(
                key = markerResourceId,
                painter = painterResource(markerResourceId)
            )
            PointAnnotation(Point.fromLngLat(location!!.longitude, location!!.latitude)) {
                iconImage = marker
            }
        }
        parkingSpots.map {
            CustomMapBoxMarker(parkingSpot = it)
        }
    }
}



