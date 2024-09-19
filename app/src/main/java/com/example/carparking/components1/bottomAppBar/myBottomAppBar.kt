package com.example.carparking.components1.bottomAppBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.carparking.R

@Composable
fun MyBottomAppBar(onButtonClick: () -> Unit) {
    BottomAppBar(
        modifier = Modifier.height(70.dp),
        containerColor = Color(0xFFF5F5F5)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            onClick = onButtonClick,
            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.parking_icon),
                contentDescription = "Parking Icon"
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}



