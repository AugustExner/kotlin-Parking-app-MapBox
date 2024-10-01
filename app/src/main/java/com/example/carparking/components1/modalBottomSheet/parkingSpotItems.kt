package com.example.carparking.components1.modalBottomSheet

import NotificationHandler
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.carparking.NavigationActivity
import com.example.carparking.R
import com.example.carparking.components1.parkingoverview.ParkingOverview
import com.example.carparking.components1.directionsAPI.makeApiCallTestWithOriginAndDestinationParameter



@Composable
fun ParkingSpotItem(
    spot: ParkingOverview,
    searchQuery: String,
    notificationHandler: NotificationHandler
) {
    val remainingDistance = remember { mutableIntStateOf(0) }
    val walkingTimeInMinutes = remember { mutableIntStateOf(0) }
    val availableSpotsInPercentage = remember { mutableIntStateOf(0) }
    val selectedParkngSpot = remember { mutableStateOf("") }
    val isGoogleMapsLaunched = remember { mutableStateOf(false) } // Track if Google Maps is launched
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner



    fun calculateWalkingTime(distance: Int): Int {
        val walkingSpeed = 1.3889 // meters per second
        val timeInSeconds = distance / walkingSpeed
        val timeInMinutes = (timeInSeconds / 60).toInt()
        return (timeInMinutes)
    }

    fun calculateAvailableSpots(spot: ParkingOverview): Int {
        val result = ((spot.ledigePladser.toFloat() / spot.antalPladser.toFloat()) * 100).toInt()
        Log.d("ParkingSpotItem", "Available spots percentage: $result%")
        return (result)
    }


    fun checkAndSendNotification() {
        if (isGoogleMapsLaunched.value && availableSpotsInPercentage.intValue < 10) {
            notificationHandler.showSimpleNotification(
                title = "Find new spot",
                message = "Only ${availableSpotsInPercentage.intValue}% of the spots are available in ${spot.parkeringsplads}"
            )
        }
    }

    // Periodically check availability after Google Maps is launched
    LaunchedEffect(isGoogleMapsLaunched.value) {
        if (isGoogleMapsLaunched.value && availableSpotsInPercentage.intValue < 10) {
            while (true) {
                // Simulate periodic checks (e.g., every 30 seconds)
                kotlinx.coroutines.delay(30000L)
                checkAndSendNotification()
            }
        }
    }


    // Lifecycle event observer to track when the user returns to the app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // When the app is resumed, set Google Maps as not launched
                isGoogleMapsLaunched.value = false
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // Cleanup when the composable leaves the composition
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    makeApiCallTestWithOriginAndDestinationParameter(
        spot.latitude, spot.longitude, searchQuery
    ) { distance ->
        remainingDistance.intValue = distance
        walkingTimeInMinutes.intValue = calculateWalkingTime(distance)
        availableSpotsInPercentage.intValue = calculateAvailableSpots(spot)
    }

    val borderColor = if (availableSpotsInPercentage.intValue < 10) Color.Red else Color.White
    val printSpots = availableSpotsInPercentage.intValue

    val pris =
        if (spot.parkeringsplads.equals("Bryggen")) "14.kr pr time"
        else if (spot.parkeringsplads.equals("P-hus Cronhammar")) "9.kr pr time"
        else if (spot.parkeringsplads.equals("P-hus Albert")) "9.kr pr time"
        else if (spot.parkeringsplads.equals("Gunhilds Plads")) "7,5.kr pr time"
        else "6.kr pr time"



    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ), // Add border with conditional color
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    println("Clicked on ${spot.parkeringsplads}")

                    // Set the selected parking spot
                    selectedParkngSpot.value = spot.parkeringsplads

                    // Check available spots percentage, if less than 10%, send notification
                    if (availableSpotsInPercentage.intValue < 10) {
                        notificationHandler.showSimpleNotification(
                            title = "Find new spot",
                            message = "Only ${availableSpotsInPercentage.intValue}% of the spots are available in ${spot.parkeringsplads}"
                        )
                    }



                    val intent = Intent(context, NavigationActivity::class.java)
                    Log.v("LatititudeToFloatCHECKKKKKKK", "Latitude: ${spot.latitude.toDouble()}, ${spot.longitude.toFloat()}")
                    intent.putExtra("latitude", spot.latitude.toDouble())
                    intent.putExtra("longitude", spot.longitude.toDouble())
                    context.startActivity(intent)

//                    // Google Maps navigation URI
//                    val gmmIntentUri =
//                        Uri.parse("google.navigation:q=${spot.latitude},${spot.longitude}&mode=d")
//                    // Create an Intent to open Google Maps in navigation mode
//                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
//                        setPackage("com.google.android.apps.maps")
//                    }
//                    // Start the activity if Google Maps is available
//                    if (mapIntent.resolveActivity(context.packageManager) != null) {
//                        context.startActivity(mapIntent)
//                        isGoogleMapsLaunched.value = true // Set to true when Google Maps is launched
//                    }




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
                    text = "${remainingDistance.value} m" + " | " + "${walkingTimeInMinutes.value} min",
                    fontSize = 14.sp,
                    color = Color.Gray
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
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ledige ${spot.ledigePladser} " + " | " + " Antal ${spot.antalPladser}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = pris,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
