package pl.kmdpoland.npower.services

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import pl.kmdpoland.npower.MainActivity
import android.app.NotificationChannel
import android.graphics.Bitmap
import android.graphics.Color
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class GeofenceIntentService : IntentService("GeofenceIntentService") {

    var notId: Int = 15452
    var notChannelId = "Channgel_1"
    var notChannelName = "Channgel_1"

    override fun onHandleIntent(intent: Intent?) {

        var geofencingEvent = GeofencingEvent.fromIntent(intent)
        var notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        RoutePlanService.ensureInitialize(applicationContext)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(notChannelId, notChannelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        var geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            var visit = RoutePlanService
                .getVisit(triggeringGeofences[0].requestId)

            var intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("visitID", visit.avatar)

            var pendingIntent = PendingIntent.getActivity(applicationContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            var notification = NotificationCompat.Builder(applicationContext, notChannelId)
                .setAutoCancel(true)
                .setContentTitle("${visit.firstName} ${visit.lastName}")
                .setContentText("Click here to see visit!!")
                .setContentIntent(pendingIntent)
                .setLargeIcon(getBitmapFromURL(visit.avatar))
                .setSmallIcon(pl.kmdpoland.npower.R.drawable.ic_pig)
                .build()

            notId = notId + 1
            notificationManager.notify(notId, notification)


        }
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            return null
        }

    }

}