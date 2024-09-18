package com.example.carparking.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.carparking.components.buttons.FindMyParkingButton

// This composable manages the state for both the search bar and button
@Composable
fun DestinationScreen() {
    var destination by remember { mutableStateOf("") }
    val context = LocalContext.current  // Get the context in composable

    // Pass the destination state to both the search bar and button
    DestinationSearchBar(onTextChange = { newText ->
        destination = newText // Update the state when the user types in the search bar
    })

    // Pass the current text and context to the button
    FindMyParkingButton(text = destination, context = context)
}

// The DestinationSearchBar that updates the state
@Composable
fun DestinationSearchBar(onTextChange: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChange(it)  // Pass the updated text to the parent composable
        },
        label = { Text("Destination") },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    )
}

// Preview of the DestinationScreen
@Preview(showBackground = true)
@Composable
fun DestinationScreenPreview() {
    DestinationScreen()
}
