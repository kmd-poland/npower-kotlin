package pl.kmdpoland.npower.services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

object LocationService {
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val SMALLEST_DISPLACEMENT: Float = 100F

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var providerClient: FusedLocationProviderClient
    private lateinit var looper: Looper

    private var locationSubject = BehaviorSubject.create<Location>()
    val locationObservable : Observable<Location> = locationSubject
    val latestLocation = locationSubject.value

    fun initialize(activity: Activity) {

        looper = activity.mainLooper

        locationRequest = LocationRequest()
        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT

        ensureLocationEnabled(activity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                val currentLocation = locationResult!!.lastLocation
                locationSubject.onNext(currentLocation)
            }
        }

        providerClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    @SuppressLint("MissingPermission")
    fun startObservingLocation(){
        providerClient?.removeLocationUpdates(locationCallback)
        providerClient.requestLocationUpdates(locationRequest, locationCallback, looper)
    }

    private fun ensureLocationEnabled(activity: Activity){

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(activity)

        val task: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->

            if (exception is ResolvableApiException){
                exception.startResolutionForResult(activity, 1)
            }
        }
    }
}