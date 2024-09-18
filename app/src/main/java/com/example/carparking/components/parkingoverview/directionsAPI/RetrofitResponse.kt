package com.example.carparking.components.parkingoverview.directionsAPI

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance
)

data class Distance(
    val text: String,   // E.g., "5.4 km"
    val value: Int      // E.g., distance in meters (5400)
)
