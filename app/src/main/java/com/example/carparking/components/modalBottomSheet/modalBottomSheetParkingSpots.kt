package com.example.carparking.components.modalBottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
    val parkingSpots = parkingViewModel.parkingSpots.sortedByDescending { it.ledigePladser }

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        val scrollState = rememberScrollState()


        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState) // Add verticalScroll modifier
        ) {
            Text(
                text = "Swipe up to open sheet. Swipe down to dismiss."
            )

            parkingSpots.map {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) { // Wrap the Text in a Column for padding
                        Text(
                            text = it.parkeringsplads,
                        )
                        Text(
                            text = "Frie Pladser: " + it.ledigePladser.toString(),
                        )
                        Text(
                            text = "Antal Pladser: " + it.antalPladser.toString(),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp)) // Add space between cards
            }
        }

    }

}





