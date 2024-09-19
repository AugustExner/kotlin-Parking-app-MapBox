package com.example.carparking.components1.modalBottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.R
import com.example.carparking.components1.parkingoverview.ParkingViewModel
import com.example.carparking.components1.parkingoverview.directionsAPI.makeApiCallTestWithOriginAndDestinationParameter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetParkingSpots(
    sheetState: androidx.compose.material3.SheetState,
    onDismissRequest: () -> Unit,
    parkingViewModel: ParkingViewModel = viewModel(),
    searchQuery: String,
) {
    val parkingSpots = parkingViewModel.parkingSpots.sortedByDescending { it.ledigePladser }
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        tonalElevation = 8.dp,  // Add slight elevation for modern look
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Available Parking Spots",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            parkingSpots.map {
                val remainingDistance = remember { mutableIntStateOf(0) }

                makeApiCallTestWithOriginAndDestinationParameter(
                    it.latitude,
                    it.longitude,
                    searchQuery
                ) { distance ->
                    remainingDistance.value = distance
                }

                //Card Design
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFECEFF1)  // Light grey background for the card
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)  // Slight elevation for shadow
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Row for Destination and Distance
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Icon for Destination
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Location Icon",
                                    tint = Color(0xFF0D47A1),  // Blue tint for icon
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
                                text = "${remainingDistance.value} m",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            // Image for parking
                            Row {
                                Image(painter = painterResource(
                                    id = R.drawable.parking_icon),
                                    contentDescription = "Parking Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = it.parkeringsplads,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text =it.ledigePladser.toString() +"/"+ it.antalPladser.toString(),
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
