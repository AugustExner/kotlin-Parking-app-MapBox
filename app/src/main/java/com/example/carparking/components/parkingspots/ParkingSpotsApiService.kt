package com.example.carparking.components.parkingspots

import retrofit2.http.GET

interface ParkingSpotsApiService {
    @GET("api/ParkingSpotOverview")
    suspend fun getParkingSpots(): List<ParkingSpots>
}
