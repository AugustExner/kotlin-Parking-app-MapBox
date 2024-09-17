package com.example.carparking.components1.parkingoverview

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingOverviewScreen(parkingViewModel: ParkingViewModel = viewModel()) {
    val parkingSpots = parkingViewModel.parkingSpots

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Parking Overview") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(parkingSpots) { spot ->
                ParkingSpotItem(spot)
            }
        }
    }
}

@Composable
fun ParkingSpotItem(spot: ParkingOverview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Parking Spot: ${spot.parkeringsplads}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Total Spots: ${spot.antalPladser}")
            Text(text = "Available: ${spot.ledigePladser}")
            Text(text = "Occupied: ${spot.optagedePladser}")
            Text(text = "Location: ${spot.latitude}, ${spot.longitude}")
        }
    }
}
