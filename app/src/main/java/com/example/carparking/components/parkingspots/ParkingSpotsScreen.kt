package com.example.carparking.components.parkingspots

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
fun ParkingSpotsScreen(parkingViewModel: ParkingViewModel = viewModel()) {
    val parkingSpots = parkingViewModel.parkingSpots

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Parking Spots") })
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
fun ParkingSpotItem(spot: ParkingSpots) {
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
            Text(text = "Spot: ${spot.id}")
            Text(text = "Status: ${spot.status}")
            Text(text = "Location: ${spot.latitude}, ${spot.longitude}")
        }
    }
}
