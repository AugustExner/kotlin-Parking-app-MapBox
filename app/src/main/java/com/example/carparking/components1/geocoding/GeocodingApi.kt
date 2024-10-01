package com.example.carparking.components1.geocoding

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocoding/v5/mapbox.places/{destination}.json")
    suspend fun getCoordinates(
        @Path("destination") destination: String,  // Automatically URL-encoded
        @Query("access_token") accessToken: String,
        @Query("limit") limit: Int = 1 // Optional parameter to limit the number of results
    ): GeocodingResponse
}
