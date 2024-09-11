package com.example.carparking

import PermissionHandler
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.carparking.Components.DestinationSearchBar
import com.example.carparking.components.MapComponents.MapBoxTest
import com.example.carparking.components.buttons.FindMyParkingButton
import com.example.carparking.components.buttons.PrintLocationButton
import com.example.carparking.ui.theme.CarParkingTheme
import com.mapbox.android.core.permissions.PermissionsManager

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: PermissionHandler

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Check and request location permission
        permissionHandler.checkAndRequestLocationPermission {
            enableEdgeToEdge()
            setContent {
                CarParkingTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        // Apply the innerPadding to the main content
                        Column(modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)) {

                            // Manage the input text state here
                            var inputText by remember { mutableStateOf("") }

                            // Display the map and components
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(550.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .border(
                                        width = 2.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(25.dp)
                                    )
                            ) {
                                MapBoxTest(context = this@MainActivity)
                            }
                            // Use the search bar to update the input text
                            DestinationSearchBar(onTextChange = { inputText = it })

                            // Pass the inputText and context to FindMySparkingButton
                            FindMyParkingButton(text = inputText, context = this@MainActivity)

                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CarParkingTheme {
        }
    }
}
