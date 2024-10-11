package com.example.carparking.components1.buttons

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carparking.components1.MapComponents.LocationHelper

// Helper function to print the user's location and destination
fun printUserRoute(context: Context, text: String) {
    val locationHelper = LocationHelper(context)

    locationHelper.getUserLocation { location ->
        Log.v(
            "PrintLocationButton",
            "From: Latitude: ${location!!.latitude} Longitude: ${location!!.longitude} To: $text"
        )
    }
}

// The button composable that triggers the printUserRoute function
@Composable
fun FindMyParkingButton(text: String, context: Context, onButtonClick: () -> Unit = {}) {
    Button(
        onClick = {
            onButtonClick()
            printUserRoute(
                context = context,
                text = text
            )
        },  // Pass context and text to the non-composable function
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(60.dp),
    ) {
        Text("Find Parking", fontSize = 24.sp)

    }
}
