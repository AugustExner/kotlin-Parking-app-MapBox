package com.example.carparking.components1.parkingoverview.directionsAPI

import com.example.carparking.components1.googlemaps.GoogleMapsRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Function to make the API call
 fun makeApiCallTestWithOriginAndDestinationParameter(
    originLat: String,
    originLng: String,
    destination: String,
    onDistanceFetched: (Int) -> Unit  // Add a callback to return the distance
) {
    val origin = "$originLat,$originLng"  // Set a fixed origin or dynamic based on your app
    val apiKey = "AIzaSyDgORILdn4tqoGRbvGsH3eKXix5LGPldi8"  // Replace with your actual API key
    val mode = "walking"  // Replace with the desired mode
    // Make the API call
    val call = GoogleMapsRetrofitClient.instance.getDirections(origin, mode, destination, apiKey )

    call.enqueue(object : Callback<DirectionsResponse> {
        override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
            if (response.isSuccessful) {
                val directions = response.body()
                val distance = directions?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance
                distance?.let {
                    println("Success: Distance - ${it.text} (${it.value} meters)")
                    onDistanceFetched(it.value)  // Pass the distance to the callback
                }
            } else {
                println("Error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
            println("Failure: ${t.message}")
        }
    })
}