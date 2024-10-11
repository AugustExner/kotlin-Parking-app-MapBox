package com.example.carparking

import NotificationHandler
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.Components.DestinationSearchBar
import com.example.carparking.components1.MapComponents.DestinationViewModel
import com.example.carparking.components1.MapComponents.MapBoxTest
import com.example.carparking.components1.buttons.FindMyParkingButton
import com.example.carparking.components1.geocoding.GeocodingViewModel
import com.example.carparking.components1.modalBottomSheet.ModalBottomSheetParkingSpots
import com.example.carparking.components1.parkingspots.ParkingViewModel
import com.example.carparking.ui.theme.CarParkingTheme
import com.example.navigationtest.PermissionHandler
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: PermissionHandler

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Check and request location permission
        permissionHandler.checkAndRequestPermissions {
            enableEdgeToEdge()
            setContent {
                CarParkingTheme {
                    // Call the main content including the bottom app bar
                    MainContent()

                }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent(destinationViewModel: DestinationViewModel = viewModel()) {
        var showBottomSheet by remember { mutableStateOf(false) }  // Control for showing the bottom sheet
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var inputText by remember { mutableStateOf("") }  // State for storing the search input
        var destinationCoordinates by remember { mutableStateOf<List<Double>?>(null) } // State for coordinates

        // Create the NotificationHandler
        val notificationHandler = NotificationHandler(context = this@MainActivity)

        // Initialize GeocodingViewModel
        val geocodingViewModel: GeocodingViewModel = viewModel()

        // Use rememberCoroutineScope to launch coroutines
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                //var inputText by remember { mutableStateOf("") }

                // Display the map and components
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Log destination coordinates before passing them
                    Log.d("MainActivity", "Destination Coordinates: $destinationCoordinates")
                    MapBoxTest(
                        context = this@MainActivity,
                        destinationCoordinates = destinationCoordinates,
                        openBottomSheet = { showBottomSheet = true }
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.background)) {
                        Column {
                            // Use the search bar to update the input text
                            DestinationSearchBar(onTextChange = { inputText = it })

                            // Pass the inputText and context to FindMyParkingButton
                            FindMyParkingButton(
                                text = inputText,
                                context = this@MainActivity,
                                onButtonClick = {
                                    // Log the input text before fetching coordinates
                                    Log.d("MainActivity", "Input Text: $inputText")

                                    // Use the GeocodingViewModel to fetch coordinates
                                    coroutineScope.launch {
                                        val coordinates =
                                            geocodingViewModel.getCoordinatesFromDestination(
                                                inputText
                                            )
                                        if (coordinates != null) {
                                            destinationViewModel.setDest(
                                                coordinates[0],
                                                coordinates[1]
                                            )
                                        }
                                        // Log the fetched coordinates
                                        Log.d("MainActivity", "Fetched Coordinates: $coordinates")

                                        coordinates?.let { fetchedCoordinates ->
                                            destinationCoordinates =
                                                fetchedCoordinates // Update state with fetched coordinates
                                            showBottomSheet = true
                                        } ?: run {
                                            // Handle error case when coordinates are not found
                                            Log.e(
                                                "GeocodingError",
                                                "Coordinates not found for the destination."
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Show the PartialBottomSheet when triggered
                if (showBottomSheet) {
                    // Pass the parking spots to the ModalBottomSheetParkingSpots composable
                    ModalBottomSheetParkingSpots(
                        sheetState = sheetState,
                        onDismissRequest = { showBottomSheet = false },
                        searchQuery = inputText,  // Pass the input text (destination) to the bottom sheet
                        notificationHandler = notificationHandler  // Pass NotificationHandler
                    )
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CarParkingTheme {
            MainContent() // Preview the main content including the bottom bar
        }
    }
}
