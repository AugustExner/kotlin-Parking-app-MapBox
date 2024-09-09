package com.example.carparking.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Public method to print the content of the input field
fun printTextContent(text: String) {
    println("Destination: $text")
}

@Composable
fun GoToDestination(onTextChange: (String) -> Unit) {
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


@Preview(showBackground = true)
@Composable
fun SimpleOutlinedTextFieldPreview() {
    // Simulate a function to handle text changes (not used in preview)
    val onTextChange: (String) -> Unit = {}
    GoToDestination(onTextChange = onTextChange)
}

