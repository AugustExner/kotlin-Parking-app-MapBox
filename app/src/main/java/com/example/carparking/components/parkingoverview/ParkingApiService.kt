package com.example.carparking.components.parkingoverview

import retrofit2.http.GET

interface ParkingApiService {
    @GET("api/ParkingOverview")
    suspend fun getParkingSpots(): List<ParkingOverview>
}
