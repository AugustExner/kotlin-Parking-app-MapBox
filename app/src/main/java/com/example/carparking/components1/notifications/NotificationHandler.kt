import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.carparking.R
import kotlin.random.Random

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "notification_channel_id"

    // SIMPLE NOTIFICATION WITH INTENT TO OPEN THE APP
    fun showSimpleNotification(title: String, message: String) {
        // Create an intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            // Add flags to make sure the activity is brought to the foreground
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a PendingIntent to wrap the intent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,  // requestCode (optional, you can set to 0)
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android 12 and above
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.parking_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)  // Dismiss the notification when clicked
            .setContentIntent(pendingIntent)  // Set the intent that will fire when the user clicks the notification
            .build()

        // Show the notification
        notificationManager.notify(Random.nextInt(), notification)
    }
}
