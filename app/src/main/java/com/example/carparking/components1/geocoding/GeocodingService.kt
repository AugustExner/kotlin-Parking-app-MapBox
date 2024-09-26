package com.example.carparking.components1.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

interface MapboxGeocodingService {
    @GET("geocoding/v5/mapbox.places/{query}.json") // Correct the endpoint to include the query
    suspend fun getCoordinates(
        @Query("access_token") accessToken: String,
        @Query("query") query: String, // This will replace the {query} in the URL
        @Query("limit") limit: Int = 1 // Optional parameter to limit the number of results
    ): GeocodingResponse
}
