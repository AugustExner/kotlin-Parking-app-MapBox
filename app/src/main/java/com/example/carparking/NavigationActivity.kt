package com.example.carparking

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.example.carparking.R.id
import com.example.navigationtest.PermissionHandler
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.common.location.Location
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.tripdata.maneuver.model.ManeuverOptions
import com.mapbox.navigation.tripdata.shield.api.MapboxRouteShieldApi
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.components.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.utils.internal.toPoint

class NavigationActivity : ComponentActivity() {
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private lateinit var navigationLocationProvider: NavigationLocationProvider
    private lateinit var locationObserver: LocationObserver

    private lateinit var maneuverView: MapboxManeuverView
    private lateinit var maneuverApi: MapboxManeuverApi

    private lateinit var destination: Point


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        destination = Point.fromLngLat(
            intent.getDoubleExtra("longitude", 10.188820),
            intent.getDoubleExtra("latitude", 56.171654)
        )

        Log.v(
            TAG,
            "Destination: ${
                intent.getDoubleExtra(
                    "longitude",
                    0.0
                )
            }, ${intent.getDoubleExtra("latitude", 0.0)}."
        )

        setContentView(R.layout.activity_navigation)
        mapView = findViewById(id.mapView)

        // Set up the button click listener
        val changeDestinationButton = findViewById<Button>(id.changeDestinationButton)
        changeDestinationButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
            finish()
        }

        // Initialize PermissionHandler
        permissionHandler = PermissionHandler(this)

        permissionHandler.checkAndRequestPermissions {

            // Permission granted, proceed with your functionality
            mapboxMap = mapView.mapboxMap

            if (!MapboxNavigationApp.isSetup()) {
                MapboxNavigationApp.setup {
                    NavigationOptions.Builder(this)
                        .build()
                }
            }

            val navigationOptions = NavigationOptions.Builder(this)
                .build()
            mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
            navigationLocationProvider = NavigationLocationProvider()


            locationObserver = object : LocationObserver {

                override fun onNewRawLocation(rawLocation: Location) {
                    // Not handled
                }

                override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
                    val enhancedLocation = locationMatcherResult.enhancedLocation

                    // Filter location based on accuracy
                    if (enhancedLocation.horizontalAccuracy!! < 50) { // Only use locations with accuracy < 20 meters
                        navigationLocationProvider.changePosition(
                            location = enhancedLocation,
                            keyPoints = locationMatcherResult.keyPoints,
                        )
                    }


                    // Once we get a valid location, call findRoute()
                    val lastLocation = navigationLocationProvider.lastLocation
                    if (lastLocation != null) {
                        findRoute(destination)
                    } else {
                        Log.d("Location", "No valid location available yet.")
                        // update camera position to account for new location
                    }
                }
            }

            mapboxNavigation.registerLocationObserver(locationObserver)

            Log.v(TAG, "START TRIP!")
            mapboxNavigation.startTripSession()
            initializeManeuverView()

            val drawable = ContextCompat.getDrawable(this@NavigationActivity, R.drawable.red_marker)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val imageHolder = ImageHolder.from(bitmap)

            mapView.location.apply {
                this.locationPuck = LocationPuck2D(
                    bearingImage = imageHolder
                )
                setLocationProvider(navigationLocationProvider)
                enabled = true
            }

            val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
                mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
                mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
            }

            // Pass the user's location to camera
            mapView.location.addOnIndicatorPositionChangedListener(
                onIndicatorPositionChangedListener
            )

            initializeManeuverApi()

            // initialize Viewport Data Source
            viewportDataSource = MapboxNavigationViewportDataSource(mapboxMap)


            // initialize Navigation Camera
            navigationCamera = NavigationCamera(
                mapboxMap,
                mapView.camera,
                viewportDataSource
            )
        }

    }

    // RouteProgressObserver to update the maneuver view
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        // Get maneuvers from maneuverApi and render them in the maneuverView
        val maneuvers = maneuverApi.getManeuvers(routeProgress)
        maneuverView.renderManeuvers(maneuvers)
    }

    private fun findRoute(destination: Point) {
        val originLocation = navigationLocationProvider.lastLocation
        Log.d("OriginLocation", originLocation.toString())


        val originPoint = originLocation?.let {
            Point.fromLngLat(it.longitude, it.latitude)
        } ?: return


        val routeOptions = RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .applyLanguageAndVoiceUnitOptions(this)
            .coordinatesList(listOf(originPoint, destination))
            .build()

        mapboxNavigation.requestRoutes(
            routeOptions,
            object : NavigationRouterCallback {
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                    // Handle the cancellation
                    Log.d("RouterCallback", "onCanceled called")
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    // Handle the failure
                    Log.d("RouterCallback", "onFailure called with reasons: $reasons")
                }

                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                    run {
                        // Choose the first route to display
                        val route = routes.first()

                        val routeSourceId = "route-source"
                        val routeLineString = route.directionsRoute.geometry()?.let {
                            LineString.fromPolyline(it, PRECISION_6)
                        }

                        val routeFeatureCollection = FeatureCollection.fromFeature(
                            Feature.fromGeometry(routeLineString)
                        )

                        mapboxMap.getStyle { style ->

                            // Check if the source already exists
                            if (style.styleSourceExists(routeSourceId)) {
                                // If the source exists, update it
                                val routeSource = style.getSourceAs<GeoJsonSource>(routeSourceId)
                                routeSource?.featureCollection(routeFeatureCollection)
                            } else {
                                // If the source doesn't exist, add it
                                val routeLineSource = geoJsonSource(routeSourceId) {
                                    featureCollection(routeFeatureCollection)
                                }
                                style.addSource(routeLineSource)
                            }

                            // Check if the layer already exists, otherwise add it
                            if (!style.styleLayerExists("route-layer")) {
                                style.addLayer(lineLayer("route-layer", routeSourceId) {
                                    lineColor(Color.BLUE)
                                    lineWidth(5.0)
                                })
                            }
                        }

                        // Set the navigation route for MapboxNavigation
                        mapboxNavigation.setNavigationRoutes(routes)
                        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)

                        val bearing = originLocation.bearing

                        // Update camera position to follow the route
                        viewportDataSource.onRouteChanged(route)
                        viewportDataSource.followingZoomPropertyOverride(16.5)
                        viewportDataSource.followingBearingPropertyOverride(bearing)
                        viewportDataSource.evaluate()

                        navigationCamera.requestNavigationCameraToFollowing()
                    }
                }
            }
        )
    }

    private fun initializeManeuverView() {
        maneuverView = MapboxManeuverView(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        maneuverView.layoutParams = layoutParams

        findViewById<LinearLayout>(id.containerLayout).addView(maneuverView)
    }

    private fun initializeManeuverApi() {
        val distanceFormatterOptions = DistanceFormatterOptions.Builder(this)
            .unitType(mapboxNavigation.navigationOptions.distanceFormatterOptions.unitType)
            .roundingIncrement(mapboxNavigation.navigationOptions.distanceFormatterOptions.roundingIncrement)
            .locale(mapboxNavigation.navigationOptions.distanceFormatterOptions.locale)
            .build()

        val distanceFormatter = MapboxDistanceFormatter(distanceFormatterOptions)

        // Now, create the maneuverApi with the existing options
        maneuverApi = MapboxManeuverApi(
            distanceFormatter,
            ManeuverOptions.Builder().build(),
            MapboxRouteShieldApi()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.stopTripSession()
    }
}




