package com.example.carparking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carparking.Components.FilledButtonExample
import com.example.carparking.Components.MapsTestMapBox
import com.example.carparking.Components.SimpleOutlinedTextField
import com.example.carparking.ui.theme.CarParkingTheme
import com.example.carparking.utils.PermissionHandler

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Check and request location permission
        permissionHandler.checkAndRequestLocationPermission {
            // Permission granted, load the map
            setContent {
                CarParkingTheme {
                    var inputText by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        // Input field at the top
                        SimpleOutlinedTextField(onTextChange = { inputText = it })

                        // Map in the middle
                        MapsTestMapBox() // Assuming this is your map implementation

                        // Button at the bottom
                        FilledButtonExample(text = inputText)

                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward the result to the PermissionHandler
        permissionHandler.handlePermissionResult(requestCode, permissions, grantResults)
    }
}
