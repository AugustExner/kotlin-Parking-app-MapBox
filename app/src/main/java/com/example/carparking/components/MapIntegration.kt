package com.example.carparking.components

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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import getUserLocation

@Composable
fun MapsTest(context: Context) {
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        try {
            userLocation = getUserLocation(context)
        } catch (e: Exception) {
            Log.e("LocationError", "Failed to get location: ${e.message}")
        }
    }
    if(userLocation != null) {
        val storCenterNord = userLocation
        val storCenterNordMarkerState = rememberMarkerState(position = storCenterNord!!)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(storCenterNord, 15f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = storCenterNordMarkerState,
                title = "Storcenter Nord",
                snippet = "Marker in Storcenter Nord"
            )
        }
    }



}




