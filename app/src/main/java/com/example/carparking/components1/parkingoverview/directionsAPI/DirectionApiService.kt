
import com.example.carparking.components1.parkingoverview.directionsAPI.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("mode") mode: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>
}
