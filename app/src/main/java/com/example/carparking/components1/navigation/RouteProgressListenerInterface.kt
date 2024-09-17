package com.example.carparking.components1.navigation

import com.mapbox.navigation.base.trip.model.RouteProgress

interface RouteProgressListener {
    fun onRouteProgressUpdated(routeProgress: RouteProgress)
}