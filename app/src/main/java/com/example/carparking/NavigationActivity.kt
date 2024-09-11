package com.example.carparking

import MyMapboxNavigationObserver
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp


class NavigationActivity : ComponentActivity() {

    private lateinit var mapboxNavigationObserver: MyMapboxNavigationObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "HELLO")


        // Set up MapboxNavigationApp if not already set up
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this).build()
            }
        }

        // Initialize the navigation observer and pass the activity context here
        mapboxNavigationObserver = MyMapboxNavigationObserver(this, this)

        // Attach lifecycle observer
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                MapboxNavigationApp.attach(owner)
                MapboxNavigationApp.registerObserver(mapboxNavigationObserver)
            }

            override fun onPause(owner: LifecycleOwner) {
                MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver)
                MapboxNavigationApp.detach(owner)
            }
        })
    }
}
