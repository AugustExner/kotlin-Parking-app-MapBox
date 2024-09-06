package com.example.carparking.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun MapsTestMapBox() {
    MapboxMap(
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
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.0)
                center(Point.fromLngLat(-98.0, 39.5))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}