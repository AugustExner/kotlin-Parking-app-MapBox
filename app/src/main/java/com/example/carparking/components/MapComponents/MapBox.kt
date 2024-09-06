package com.example.carparking.components.MapComponents

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.R
import com.example.carparking.components.parkingoverview.ParkingOverview
import com.example.carparking.components.parkingoverview.ParkingViewModel
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import getUserLocation

@Composable
fun MapBoxTest(context: Context, parkingViewModel: ParkingViewModel = viewModel()) {
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val parkingSpots = parkingViewModel.parkingSpots

    val markerResourceId by remember {
        mutableIntStateOf(R.drawable.red_marker)
    }

    LaunchedEffect(Unit) {
        try {
            userLocation = getUserLocation(context)

        } catch (e: Exception) {
            Log.e("LocationError", "Failed to get location: ${e.message}")
        }
    }
    if(userLocation != null) {
        val userLatLng = userLocation
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(15.0)
                        center(Point.fromLngLat(userLatLng!!.longitude, userLatLng.latitude))
                    pitch(0.0)
                    bearing(0.0)
                }
            },
        ) {
            val marker = rememberIconImage(key = markerResourceId, painter = painterResource(markerResourceId))
            PointAnnotation(Point.fromLngLat(userLatLng!!.longitude, userLatLng.latitude)) {
                iconImage = marker
            }
            parkingSpots.map {
                CustomMapBoxMarker(parkingSpot = it)
            }
        }
    }
}

@Composable
fun CustomMapBoxMarker(parkingSpot: ParkingOverview) {
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(geometry = Point.fromLngLat(parkingSpot.longitude.toDouble(), parkingSpot.latitude.toDouble()))
            annotationAnchor {
                anchor(ViewAnnotationAnchor.BOTTOM)
            }
            allowOverlap(true)
        }
    )
    {
        Card(elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp
        ),
            modifier = Modifier.size(width = 120.dp, height = 50.dp)
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier.padding(2.dp)) {
                Log.v(TAG,"Hello")
                Image(painter = painterResource(
                    id = R.drawable.parking_icon),
                    contentDescription = "Parking Icon",
                    modifier = Modifier.size(20.dp).weight(1 / 4f))
                Column(modifier = Modifier.weight(3/4f)) {
                    Text(text = parkingSpot.parkeringsplads,
                        fontSize = 10.sp)
                    Text(text = "${parkingSpot.ledigePladser}/${parkingSpot.antalPladser}", fontSize = 10.sp)
                }
            }

        }
    }
}