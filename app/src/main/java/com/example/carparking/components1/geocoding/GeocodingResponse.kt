package com.example.carparking.components1.geocoding

data class GeocodingResponse(
    val features: List<Feature>
)

data class Feature(
    val center: List<Double>  // center contains [longitude, latitude]
)
