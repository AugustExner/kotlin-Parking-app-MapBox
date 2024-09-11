package com.example.carparking.components1.parkingoverview

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientFile1 {
    private const val BASE_URL = "https://letparkeringapi.azurewebsites.net/"

    val instance: ParkingApiServiceFile by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ParkingApiServiceFile::class.java)
    }
}
