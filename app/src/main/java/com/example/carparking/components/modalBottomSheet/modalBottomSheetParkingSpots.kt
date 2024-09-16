package com.example.carparking.components.modalBottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carparking.components.parkingoverview.ParkingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetParkingSpots(
    sheetState: androidx.compose.material3.SheetState,
    onDismissRequest: () -> Unit,
    parkingViewModel: ParkingViewModel = viewModel()
) {
    val parkingSpots = parkingViewModel.parkingSpots
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Swipe up to open sheet. Swipe down to dismiss."
            )


            Log.v("parkingSpots", "ParkingSpots: $parkingSpots")
            parkingSpots.map {
                Text(
                    text = it.parkeringsplads
                )
                Spacer(modifier = Modifier.height(8.dp)) // Add space between cards
            }

        }
    }

}