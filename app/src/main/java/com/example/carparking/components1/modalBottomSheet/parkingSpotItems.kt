package com.example.carparking.components1.modalBottomSheet

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carparking.R
import com.example.carparking.components1.parkingoverview.ParkingOverview
import com.example.carparking.components1.parkingoverview.directionsAPI.makeApiCallTestWithOriginAndDestinationParameter


@Composable
fun ParkingSpotItem(spot: ParkingOverview, searchQuery: String) {
    val remainingDistance = remember { mutableIntStateOf(0) }
    val walkingTimeInMinutes = remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    fun calculateWalkingTime(distance: Int): Int {
        val walkingSpeed = 1.3889 // meters per second
        val timeInSeconds = distance / walkingSpeed
        val timeInMinutes = (timeInSeconds / 60).toInt()
        return (timeInMinutes)
    }

    makeApiCallTestWithOriginAndDestinationParameter(
        spot.latitude, spot.longitude, searchQuery
    ) { distance ->
        remainingDistance.value = distance
        walkingTimeInMinutes.value = calculateWalkingTime(distance)

    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    println("Clicked on ${spot.parkeringsplads}")
                    // Google Maps navigation URI
                    val gmmIntentUri = Uri.parse("google.navigation:q=${spot.latitude},${spot.longitude}&mode=d")
                    // Create an Intent to open Google Maps in navigation mode
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    // Start the activity if Google Maps is available
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    }
                }
        ) {
            // Distance row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location Icon",
                        tint = Color(0xFF0D47A1),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = searchQuery.replaceFirstChar { it.uppercase() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "${remainingDistance.value} m" + " | " + "${walkingTimeInMinutes.value} min", fontSize = 14.sp, color = Color.Gray
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parking spot info
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.parking_icon),
                        contentDescription = "Parking Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = spot.parkeringsplads,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Fri :${spot.ledigePladser}/${spot.antalPladser}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}
