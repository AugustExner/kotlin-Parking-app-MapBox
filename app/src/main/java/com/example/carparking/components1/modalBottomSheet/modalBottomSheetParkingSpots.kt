package com.example.carparking.components1.modalBottomSheet

import NotificationHandler
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.R
import com.example.carparking.components1.parkingoverview.ParkingOverview
import com.example.carparking.components1.parkingoverview.ParkingViewModel
import com.example.carparking.components1.parkingoverview.directionsAPI.makeApiCallTestWithOriginAndDestinationParameter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetParkingSpots(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    parkingViewModel: ParkingViewModel = viewModel(),
    searchQuery: String,
    notificationHandler: NotificationHandler, // Accept NotificationHandler
) {
    val parkingSpots = parkingViewModel.parkingSpots.sortedByDescending { it.ledigePladser }
    val finalDestination = searchQuery
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = searchQuery.replaceFirstChar { it.uppercase() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location Icon",
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(24.dp)
                )
            }

            parkingSpots.forEach { parkingSpot ->
                ParkingSpotItem(
                    spot = parkingSpot,
                    searchQuery = searchQuery,
                    notificationHandler = notificationHandler // Pass the NotificationHandler to each ParkingSpotItem
                )
            }
        }
    }
}