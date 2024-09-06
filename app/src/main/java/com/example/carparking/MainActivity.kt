package com.example.carparking

import PermissionHandler
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.carparking.components.parkingoverview.ParkingOverviewScreen
import androidx.compose.ui.unit.dp
import com.example.carparking.components.MapComponents.MapBoxTest
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
                        topBar = {
                            CenterAlignedTopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("PARK3")
                                }
                            )
                        },
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(all = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .height(height = 500.dp)
                            ) {
                                //PermissionAwareLocationDisplay(context = this@MainActivity)
                                MapBoxTest(context = this@MainActivity)
                            }
                            ElevatedButton(
                                onClick = { println("You Clicked the button!") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            ) {
                                Text("Find Parking")

                            }
                            //ParkingOverviewScreen()
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