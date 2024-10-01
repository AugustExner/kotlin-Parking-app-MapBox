package com.example.carparking.components1.geocoding

import android.util.Log
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingViewModel : ViewModel() {
    private val geocodingService: GeocodingApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        geocodingService = retrofit.create(GeocodingApi::class.java)
    }

    suspend fun getCoordinatesFromDestination(destination: String): List<Double>? {
        Log.d("GeocodingViewModel", "Making API request for destination: $destination")

        // Log the full API call URL for debugging
        val apiUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/$destination.json?access_token=sk.eyJ1IjoibWlncm9icCIsImEiOiJjbTBxaDYxNjAwYXd4MmxyMmRraHVhaWg2In0.yi7A6FcZnxX4Ozgb0PGCgA"
        Log.d("GeocodingViewModel", "Full API request URL: $apiUrl")

        return try {
            // Make the API call
            val response = geocodingService.getCoordinates(
                accessToken = "sk.eyJ1IjoibWlncm9icCIsImEiOiJjbTBxaDYxNjAwYXd4MmxyMmRraHVhaWg2In0.yi7A6FcZnxX4Ozgb0PGCgA",
                destination = destination // No need to manually encode, Retrofit handles this
            )

            // Log the full response for debugging
            Log.d("GeocodingViewModel", "Full API Response: $response")
            Log.d("GeocodingViewModel", "Features: ${response.features}")

            // Get the first feature (if any)
            val firstFeature = response.features.firstOrNull()
            if (firstFeature == null) {
                Log.w("GeocodingViewModel", "No features returned for destination: $destination")
                return null
            }

            // Extract center coordinates
            val center = firstFeature.center
            if (center == null || center.size < 2) {
                Log.e("GeocodingViewModel", "Invalid center coordinates for destination: $destination")
                return null
            }

            Log.d("GeocodingViewModel", "Valid center coordinates: $center")
            return center

        } catch (e: Exception) {
            Log.e("GeocodingError", "Error fetching coordinates: ${e.message}")
            return null
        }
    }
}
