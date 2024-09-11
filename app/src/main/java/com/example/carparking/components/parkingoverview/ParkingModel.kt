package com.example.carparking.components.parkingoverview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ParkingModel : ViewModel() {

    // Corrected state management
    var parkingSpots: List<ParkingOverview> by mutableStateOf(emptyList())
        private set

    init {
        fetchParkingDataPeriodically()
    }

    private fun fetchParkingDataPeriodically() {
        viewModelScope.launch {
            while (true) {
                fetchParkingData()
                delay(60000)  // Wait for 1 minute
            }
        }
    }

    private suspend fun fetchParkingData() {
        try {
            val response = RetrofitClientFile.instance.getParkingSpots()
            parkingSpots = response
        } catch (e: Exception) {
            e.printStackTrace()  // Handle the error appropriately
        }
    }
}
