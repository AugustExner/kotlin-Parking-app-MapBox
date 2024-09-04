package com.example.carparking.components.parkingspots

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://letparkeringapi.azurewebsites.net/"

    val instance: ParkingSpotsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ParkingSpotsApiService::class.java)
    }
}
