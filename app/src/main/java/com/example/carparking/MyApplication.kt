package com.example.carparking

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set up MapboxNavigationApp if not already set up
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this)
                    .build()
            }
        }
        // Do not register the observer here since you need an Activity context
        // Instead, this can be handled in your activity.
    }
}
