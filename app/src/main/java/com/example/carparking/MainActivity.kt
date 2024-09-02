package com.example.carparking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.carparking.ui.theme.CarParkingTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarParkingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MapsTest()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun MapsTest() {
    val storCenterNord = LatLng(56.171080, 10.189440)
    val storCenterNordMarkerState = rememberMarkerState(position = storCenterNord)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarParkingTheme {
    }
}