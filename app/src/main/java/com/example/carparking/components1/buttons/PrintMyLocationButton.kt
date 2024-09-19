package com.example.carparking.components1.buttons

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.carparking.components1.MapComponents.LocationHelper

@Composable
fun PrintLocationButton() {
    val context = LocalContext.current
    val locationHelper = LocationHelper(context)

    Button(onClick = {
        // Fetch location when the button is clicked
        locationHelper.getUserLocation { location ->
            Log.d("PrintLocationButton", "This is the user Location: Latitude: ${location!!.latitude} Longitude: ${location!!.longitude}")
        }
    }) {
        Text("Print Location")
    }
}



