package pl.kmdpoland.npower.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import java.util.*

object GeofenceService {

    private lateinit var geofenceClient: GeofencingClient;
    private lateinit var pendingIntent: PendingIntent;

    fun initialize(context: Context) {
        geofenceClient = LocationServices.getGeofencingClient(context!!)

        var intent: Intent = Intent(context, GeofenceIntentService::class.java)
        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @SuppressLint("MissingPermission")
    fun setGeofence(latitude: Double, longitude: Double, area: Float, id: String){
        var geofence = Geofence.Builder().setCircularRegion(latitude, longitude, area)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL )
            .setRequestId(id)
            .setLoiteringDelay(1000)
            .build()

        var geofencingRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        geofenceClient.addGeofences(geofencingRequest, pendingIntent)
    }

}