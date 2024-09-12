package com.example.carparking

import com.example.carparking.components1.navigation.MyLocationObserver
import com.example.carparking.components1.navigation.MyMapboxNavigationObserver
import PermissionHandler
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.carparking.components1.navigation.setupMapboxNavigation
import com.example.carparking.ui.theme.CarParkingTheme
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp


class NavigationActivity : ComponentActivity() {
    private lateinit var mapboxNavigationObserver: MyMapboxNavigationObserver
    private var isTripSessionStarted = false // Flag to ensure trip session starts only once

    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "Activity Created")

        // Initialize the PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Define your destination as a Point (latitude, longitude)
        val destination = Point.fromLngLat(10.193860, 56.170060)

        // Setup Mapbox Navigation
        setupMapboxNavigation(this)

        // Attach lifecycle observer after setup
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                MapboxNavigationApp.attach(owner)

                // Check if MapboxNavigationApp is properly initialized
                val mapboxNavigation = MapboxNavigationApp.current()
                if (mapboxNavigation == null) {
                    Log.e(TAG, "MapboxNavigation instance is null")
                } else {
                    Log.v(TAG, "MapboxNavigation instance is available")
                    // Initialize observer only once
                    if (!::mapboxNavigationObserver.isInitialized) {
                        mapboxNavigationObserver = MyMapboxNavigationObserver(
                            this@NavigationActivity,
                            this@NavigationActivity
                        )
                    }

                    MapboxNavigationApp.registerObserver(mapboxNavigationObserver)

                    // Ensure trip session starts only once
                    if (!isTripSessionStarted) {
                        permissionHandler.checkAndRequestLocationPermission {
                            Log.v(TAG, "START TRIP!")
                            isTripSessionStarted = true
                            // Start the trip session after permissions are granted
                            mapboxNavigation.startTripSession()
                        }
                    }

                    setContent {
                        CarParkingTheme {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    LocationMapDisplay(mapboxNavigation = mapboxNavigation, destination)
                                }
                            }
                        }
                    }
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                val mapboxNavigation = MapboxNavigationApp.current()
                mapboxNavigation?.let {
                    MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver)
                }
                MapboxNavigationApp.detach(owner)
            }
        })
        enableEdgeToEdge()
    }
}

@Composable
fun LocationMapDisplay(mapboxNavigation: MapboxNavigation, destination: Point) {
    val mapViewportState = rememberMapViewportState()
    var location by remember { mutableStateOf<com.mapbox.common.location.Location?>(null) }

    // Create an instance of MyLocationObserver
    val locationObserver = remember {
        MyLocationObserver { newLocation ->
            location = newLocation
            location?.let { loc ->
                mapViewportState.setCameraOptions {
                    center(Point.fromLngLat(loc.longitude, loc.latitude))
                    zoom(12.0)
                }

                // Once the location is available, request a route to the destination
                requestRoute(mapboxNavigation, Point.fromLngLat(loc.longitude, loc.latitude), destination)
            }
        }
    }

    // Use DisposableEffect to handle resource management
    DisposableEffect(mapboxNavigation) {
        mapboxNavigation.registerLocationObserver(locationObserver)

        // Clean up to avoid redrawing issues
        onDispose {
            mapboxNavigation.unregisterLocationObserver(locationObserver)
        }
    }

    // Render the map with the location
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    ) {
        location?.let {
            val marker = rememberIconImage(
                key = R.drawable.red_marker,
                painter = painterResource(R.drawable.red_marker)
            )
            PointAnnotation(Point.fromLngLat(it.longitude, it.latitude)) {
                iconImage = marker
            }
        }
    }
}

fun requestRoute(mapboxNavigation: MapboxNavigation, origin: Point, destination: Point) {
    val routeOptions = RouteOptions.builder()
        .applyDefaultNavigationOptions()
        .coordinatesList(listOf(origin, destination))
        .build()

    mapboxNavigation.requestRoutes(
        routeOptions,
        object : NavigationRouterCallback {
            override fun onRoutesReady(
                routes: List<NavigationRoute>,
                @RouterOrigin routerOrigin: String
            ) {
                if (routes.isNotEmpty()) {
                    // Set the first available route and start navigation
                    mapboxNavigation.setNavigationRoutes(routes)
                }
            }

            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                TODO("Not yet implemented")
            }

            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                Log.e(TAG, "Route request failed: $reasons")
            }


            fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                Log.e(TAG, "Route request canceled")
            }
        }
    )
}