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
import android.widget.RemoteViews
import com.bumptech.glide.request.target.NotificationTarget
import pl.kmdpoland.npower.R
import android.os.Looper
import android.os.Handler


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

            val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
            notificationLayout.setTextViewText(R.id.title, "Approaching ${visit.firstName} ${visit.lastName}")
            notificationLayout.setTextViewText(R.id.details, "Click here to see visit!!")

            val notification = NotificationCompat.Builder(applicationContext, notChannelId)
                .setOngoing(true)
                .setSmallIcon(pl.kmdpoland.npower.R.drawable.ic_pig)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
                .build()

            notId = notId + 1

            val notificationTarget = NotificationTarget(
                applicationContext,
                R.id.image,
                notificationLayout,
                notification,
                notId
            )

            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                Glide
                    .with(applicationContext)
                    .asBitmap()
                    .load(visit.avatar)
                    .apply(RequestOptions.overrideOf(90))
                    .apply(RequestOptions.circleCropTransform())
                    .into( notificationTarget );
            }

            mainHandler.post(myRunnable)
            notificationManager.notify(notId, notification)
        }

    }
}