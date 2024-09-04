package com.example.carparking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.carparking.Components.FilledButtonExample
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {

                        MapsTest()
                        FilledButtonExample()

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 16.dp)
            .height(650.dp)
            .clip(RoundedCornerShape(25.dp))
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(25.dp)
            ),


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