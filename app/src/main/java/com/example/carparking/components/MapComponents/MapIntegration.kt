package com.example.carparking.components.MapComponents

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.components.parkingoverview.ParkingOverview
import com.example.carparking.components.parkingoverview.ParkingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import getUserLocation


@Composable
fun MapsTest(context: Context, parkingViewModel: ParkingViewModel = viewModel()) {
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val parkingSpots = parkingViewModel.parkingSpots

    LaunchedEffect(Unit) {
        try {
            userLocation = getUserLocation(context)

        } catch (e: Exception) {
            Log.e("LocationError", "Failed to get location: ${e.message}")
        }
    }
    if(userLocation != null) {
        val userLatLng = userLocation
        val userMarkerState = rememberMarkerState(position = userLatLng!!)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

            Marker(
                state = userMarkerState,
                title = "Your Location",
                snippet = "Marker at user location"
            )

            parkingSpots.map {

                CustomMarker(parkingSpot = it)
            }
        }
    }
}

@Composable
fun CustomMarker(parkingSpot: ParkingOverview) {
    val markerPositionState = rememberMarkerState(position = LatLng(parkingSpot.latitude.toDouble(), parkingSpot.longitude.toDouble()))
    Marker(
        state = markerPositionState,
        title = parkingSpot.parkeringsplads
    )
    MarkerComposable {

    }
}

