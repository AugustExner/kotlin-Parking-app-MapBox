package com.example.carparking.components.mapComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carparking.R
import com.example.carparking.components.parkingoverview.ParkingOverview

import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

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
    ) {
        Card(elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp
        ),
            modifier = Modifier.size(width = 120.dp, height = 50.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

                modifier = Modifier.padding(2.dp)) {
                Image(painter = painterResource(
                    id = R.drawable.parking_icon),
                    contentDescription = "Parking Icon",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier)
                Spacer(modifier = Modifier.width(5.dp))
                Column(modifier = Modifier) {
                    Text(text = parkingSpot.parkeringsplads,
                        fontSize = 10.sp,
                        overflow = TextOverflow.Clip)
                    Text(text = "${parkingSpot.ledigePladser}/${parkingSpot.antalPladser}", fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomMarkerPreview() {
    CustomMapBoxMarker(parkingSpot = ParkingOverview(
        id = 1, parkeringsplads = "Willy Sørensens Plads",
        antalPladser = 100,
        ledigePladser = 69,
        optagedePladser = 31,
        latitude = "69",
        longitude = "69"))
}