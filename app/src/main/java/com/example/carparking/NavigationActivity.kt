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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.tripdata.maneuver.model.PrimaryManeuver


class NavigationActivity : ComponentActivity() {
    private lateinit var mapboxNavigationObserver: MyMapboxNavigationObserver
    private var isTripSessionStarted = false

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var maneuverApi: MapboxManeuverApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "Activity Created")

        // Initialize PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Define your destination
        val destination = Point.fromLngLat(10.189440, 56.171080)

        setupMapboxNavigation(this)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                MapboxNavigationApp.attach(owner)

                val mapboxNavigation = MapboxNavigationApp.current()
                if (mapboxNavigation == null) {
                    Log.e(TAG, "MapboxNavigation instance is null")
                } else {
                    Log.v(TAG, "MapboxNavigation instance is available")

                    // Create DistanceFormatterOptions from MapboxNavigation's options
                    val distanceFormatterOptions = DistanceFormatterOptions.Builder(this@NavigationActivity)
                        .unitType(mapboxNavigation.navigationOptions.distanceFormatterOptions.unitType)
                        .roundingIncrement(mapboxNavigation.navigationOptions.distanceFormatterOptions.roundingIncrement)
                        .locale(mapboxNavigation.navigationOptions.distanceFormatterOptions.locale)
                        .build()

                    // Initialize the DistanceFormatter
                    val distanceFormatter = MapboxDistanceFormatter(distanceFormatterOptions)

                    // Initialize the Maneuver API with the DistanceFormatter
                    maneuverApi = MapboxManeuverApi(distanceFormatter)

                    if (!::mapboxNavigationObserver.isInitialized) {
                        mapboxNavigationObserver = MyMapboxNavigationObserver(
                            this@NavigationActivity,
                            this@NavigationActivity
                        )
                    }

                    MapboxNavigationApp.registerObserver(mapboxNavigationObserver)

                    if (!isTripSessionStarted) {
                        permissionHandler.checkAndRequestLocationPermission {
                            Log.v(TAG, "START TRIP!")
                            isTripSessionStarted = true
                            mapboxNavigation.startTripSession()
                        }
                    }

                    setContent {
                        CarParkingTheme {
                            Scaffold(modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    Card() {
                                        LocationMapDisplay(mapboxNavigation = mapboxNavigation, destination = destination, maneuverApi = maneuverApi)
                                    }
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
fun LocationMapDisplay(mapboxNavigation: MapboxNavigation, destination: Point, maneuverApi: MapboxManeuverApi) {
    val mapViewportState = rememberMapViewportState()
    var location by remember { mutableStateOf<com.mapbox.common.location.Location?>(null) }
    var maneuverText by remember { mutableStateOf("") }

    // Create an instance of MyLocationObserver
    val locationObserver = remember {
        MyLocationObserver { newLocation ->
            location = newLocation
            location?.let { loc ->
                mapViewportState.setCameraOptions {
                    center(Point.fromLngLat(loc.longitude, loc.latitude))
                    zoom(12.0)
                }
                // Request a route to the destination
                requestRoute(mapboxNavigation, Point.fromLngLat(loc.longitude, loc.latitude), destination)
            }
        }
    }

    // RouteProgressObserver for maneuvers
    val routeProgressObserver = remember {
        RouteProgressObserver { routeProgress ->
            val maneuvers = maneuverApi.getManeuvers(routeProgress)

            maneuvers.fold(
                { error ->
                    Log.e(TAG, "Error getting maneuvers: ${error.errorMessage}")
                },
                { maneuverList ->
                    maneuverList.let {
                        val primaryManeuver: PrimaryManeuver = it.firstOrNull()?.primary ?: return@fold
                        maneuverText = primaryManeuver.text
                        Log.v("COMPONENT", "${primaryManeuver.modifier.toString()} | ${primaryManeuver.degrees} | ${primaryManeuver.drivingSide} | ${primaryManeuver.id} | ${primaryManeuver.type}")
                    }
                }
            )
        }
    }

    // Use DisposableEffect to register and unregister observers
    DisposableEffect(mapboxNavigation) {
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)

        onDispose {
            mapboxNavigation.unregisterLocationObserver(locationObserver)
            mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        }
    }

    // Render the map and maneuver text
    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(modifier = Modifier.fillMaxSize(), mapViewportState = mapViewportState) {
            // Here we set up the map style or annotations directly if needed.
            // Example: Adding a marker or changing map style, use the available Mapbox SDK APIs.
            // You can use MapboxMapScope functions here, e.g., addAnnotation, setCamera, etc.
        }
        ManeuverDisplay(maneuverText)
    }
}

@Composable
fun ManeuverDisplay(maneuverText: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(text = maneuverText, modifier = Modifier.padding(16.dp))
    }
}

fun requestRoute(mapboxNavigation: MapboxNavigation, origin: Point, destination: Point) {
    val routeOptions = RouteOptions.builder()
        .applyDefaultNavigationOptions()
        .coordinatesList(listOf(origin, destination))
        .build()

    Log.v(TAG, "Requesting route with options: $routeOptions")

    mapboxNavigation.requestRoutes(
        routeOptions,
        object : NavigationRouterCallback {
            override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                Log.v(TAG, "Routes ready: $routes")
                if (routes.isNotEmpty()) {
                    // Set the first available route and start navigation
                    mapboxNavigation.setNavigationRoutes(routes)
                } else {
                    Log.e(TAG, "No routes available")
                }
            }

            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                Log.e(TAG, "Route request canceled")
            }

            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                Log.e(TAG, "Route request failed: $reasons")
            }
        }
    )
}