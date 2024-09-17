package com.example.carparking

import PermissionHandler
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.carparking.components1.navigation.MyLocationObserver
import com.example.carparking.components1.navigation.MyMapboxNavigationObserver
import com.example.carparking.components1.navigation.setupMapboxNavigation
import com.example.carparking.ui.theme.CarParkingTheme
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.tripdata.maneuver.model.PrimaryManeuver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


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
                            var maneuverText by remember { mutableStateOf("") }
                            var distanceText by remember { mutableStateOf("") }
                            var durationInt by remember { mutableStateOf(0) }
                            var directionText by remember { mutableStateOf("") }
                            var instructionText by remember { mutableStateOf("") }
                            var distanceToTurnInt by remember { mutableStateOf(0) }


                            Scaffold(modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    LocationMapDisplay(
                                        mapboxNavigation = mapboxNavigation,
                                        destination = destination,
                                        maneuverApi = maneuverApi,
                                        onManeuverUpdate = { maneuver ->
                                            maneuverText = maneuver
                                        },
                                        onDistanceUpdate = { distance ->
                                            distanceText = distance
                                        },
                                        onDurationUpdate = { duration ->
                                            durationInt = duration
                                        },
                                        onNextDirection = { direction ->
                                            directionText = direction
                                        },
                                        onNextStepInstruction = { instruction ->
                                            instructionText = instruction
                                        },
                                        onDistanceRemainingBeforeTurn = { distance ->
                                            distanceToTurnInt = distance

                                        }

                                    )


                                        if (maneuverText.isNotEmpty()) {
                                            ManeuverDisplay(
                                                maneuverText,
                                                directionText,
                                                instructionText,
                                                durationInt,
                                                distanceToTurnInt
                                            )
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
fun LocationMapDisplay(
    mapboxNavigation: MapboxNavigation,
    destination: Point,
    maneuverApi: MapboxManeuverApi,
    onManeuverUpdate: (String) -> Unit,  // Callback for sending maneuver text back to parent
    onDistanceUpdate: (String) -> Unit,  // Callback for sending distance information
    onDurationUpdate: (Int) -> Unit,    // Callback for sending duration information
    onNextStepInstruction: (String) -> Unit,
    onNextDirection: (String) -> Unit,
    onDistanceRemainingBeforeTurn: (Int) -> Unit
) {
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
                // Request a route to the destination
                requestRoute(mapboxNavigation, Point.fromLngLat(loc.longitude, loc.latitude), destination)
            }
        }
    }

    // RouteProgressObserver for maneuvers and other metrics
    val routeProgressObserver = remember {
        RouteProgressObserver { routeProgress ->
            val maneuvers = maneuverApi.getManeuvers(routeProgress)

            maneuvers.fold(
                { error ->
                    Log.e(TAG, "Error getting maneuvers: ${error.errorMessage}")
                },
                { maneuverList ->
                    val primaryManeuver: PrimaryManeuver = maneuverList.firstOrNull()?.primary ?: return@fold
                    onManeuverUpdate(primaryManeuver.text)  // Send maneuver text to parent via callback
                }
            )

            // Extract distance and duration information
            val distanceToNextManeuver = routeProgress.currentLegProgress?.currentStepProgress?.distanceRemaining
            val distanceRemaining = routeProgress.currentLegProgress?.distanceRemaining
            val durationRemaining = routeProgress.durationRemaining
            val nextManeuverDirection = routeProgress.currentLegProgress?.upcomingStep
            val currentStepProgress = routeProgress.currentLegProgress?.currentStepProgress
            val distanceRemainingBeforeTurn = routeProgress.currentLegProgress?.currentStepProgress?.distanceRemaining



            if (currentStepProgress != null) {
                onNextStepInstruction(nextManeuverDirection?.maneuver()?.instruction().toString())
                onNextDirection(nextManeuverDirection?.maneuver()?.modifier().toString())
            }

            if (onDurationUpdate != null) {
                onDurationUpdate(durationRemaining.toInt())
            }

            if (distanceRemainingBeforeTurn != null) {
                onDistanceRemainingBeforeTurn(Math.round(distanceRemainingBeforeTurn))
            }

            distanceToNextManeuver?.let {
                if(it >= 1000) {
                    onDistanceUpdate("${(it / 1000).toInt()} km")  // Convert meters to kilometers
                } else {
                    onDistanceUpdate("${it.toInt()} m")
                }
            }

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

    // Render the map (without the maneuver card here)
    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(modifier = Modifier.fillMaxSize(), mapViewportState = mapViewportState) {

        }
    }
}



@Composable
fun ManeuverDisplay(maneuverText: String, maneuverDirection: String, maneuverInstruction: String, maneuverDuration: Int, maneuverDistanceToTurn: Int) {
    val timeETA = LocalTime.now().plusSeconds(maneuverDuration.toLong())
    val formatter = DateTimeFormatter.ofPattern("HH.mm")



    Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Row() {
            //Image(painter = painterResource(
            //    id = R.drawable.parking_icon),
            //    contentDescription = "Parking Icon",
            //    modifier = Modifier)
            Column {
                Row {
                    Text(text = "$maneuverDistanceToTurn m")
                    Text(text = " - ")
                    Text(text = "${timeETA.format(formatter)} ETA")
                }
                Text(text = maneuverInstruction)
            }

        }

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