package com.example.carparking.components1.geocoding

import android.util.Log
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingViewModel : ViewModel() {
    private val geocodingService: MapboxGeocodingService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        geocodingService = retrofit.create(MapboxGeocodingService::class.java)
    }

    suspend fun getCoordinatesFromDestination(destination: String): List<Double>? {
        Log.d("Geocoding", "Fetching coordinates for destination: $destination")

        return try {
            val response = geocodingService.getCoordinates(
                accessToken = "sk.eyJ1IjoibWlncm9icCIsImEiOiJjbTBxaDYxNjAwYXd4MmxyMmRraHVhaWg2In0.yi7A6FcZnxX4Ozgb0PGCgA", // Your token
                query = destination
            )

            Log.d("Geocoding", "Response: ${response.features}") // Log the response
            response.features.firstOrNull()?.center // This should return [longitude, latitude]
        } catch (e: Exception) {
            Log.e("GeocodingError", "Error fetching coordinates: ${e.message}")
            Log.e(
                "GeocodingError",
                "Error fetching coordinates",
                e
            ) // Log the entire exception for more detail
            null
        }
    }

}
