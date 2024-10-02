package com.example.carparking.components1.parkingoverview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ParkingViewModel : ViewModel() {

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
            val response = RetrofitClient.instance.getParkingSpots()
            parkingSpots = response
        } catch (e: Exception) {
            e.printStackTrace()  // Handle the error appropriately
        }
    }

    fun calculateAvailableSpots(spot: ParkingOverview): Int {
        return ((spot.ledigePladser.toFloat() / spot.antalPladser.toFloat()) * 100).toInt()
    }

    fun findBestAvailableSpot(currentSpot: ParkingOverview): ParkingOverview? {
        // Filter for spots that have more than 10% available, are closer, and are not the current spot
        val availableSpots = parkingSpots.filter {
            it != currentSpot && calculateAvailableSpots(it) > 10 && it.distance < 300
        }
        // Find the spot with the lowest price from the filtered list
        return availableSpots.minByOrNull { spot -> spot.price }
    }
}



