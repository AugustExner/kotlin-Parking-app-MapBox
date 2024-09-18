package com.example.carparking.components.googlemaps

import DirectionsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleMapsRetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    val instance: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }
}
