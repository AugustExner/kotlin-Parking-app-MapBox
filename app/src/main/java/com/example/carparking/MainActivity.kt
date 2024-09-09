package com.example.carparking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.carparking.components.parkingoverview.ParkingOverviewScreen
import androidx.compose.ui.unit.dp
import com.example.carparking.Components.FindParkingButton
import com.example.carparking.Components.MySearchBar
import com.example.carparking.components.MapComponents.MapBoxTest
import com.example.carparking.ui.theme.CarParkingTheme
import com.mapbox.android.core.permissions.PermissionsManager


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarParkingTheme {
                var inputText by remember { mutableStateOf("") }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                ) { innerPadding ->
                    Column(modifier = Modifier.padding(all = 8.dp)) {

                        Box(modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(end = 8.dp, start = 8.dp)
                            .height(650.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(25.dp)

                            )) {
                            //PermissionAwareLocationDisplay(context = this@MainActivity)
                            MapBoxTest(context = this@MainActivity)
                        }

                        MySearchBar(onTextChange = { inputText = it })
                        FindParkingButton(text = inputText)

                       // ParkingOverviewScreen()
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