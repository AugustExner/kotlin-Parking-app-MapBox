package com.example.carparking.components1.parkingoverview

data class ParkingOverview(
    val id: Int,
    val parkeringsplads: String,
    val antalPladser: Int,
    val ledigePladser: Int,
    val optagedePladser: Int,
    val latitude: String,
    val longitude: String,
    var distance: Int,
    var price: Double
)
