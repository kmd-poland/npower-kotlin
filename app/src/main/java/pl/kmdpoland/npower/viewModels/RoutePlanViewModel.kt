package pl.kmdpoland.npower.viewModels

import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import pl.kmdpoland.npower.data.RoutePlan
import pl.kmdpoland.npower.data.Visit
import pl.kmdpoland.npower.services.ApiService
import pl.kmdpoland.npower.services.GeofenceIntentService
import pl.kmdpoland.npower.services.GeofenceService
import pl.kmdpoland.npower.services.RoutePlanService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class RoutePlanViewModel : ViewModel() {
    val routePlanObservable: Observable<List<Visit>>

    var selectedVisit: MutableLiveData<Visit> = MutableLiveData()

    init {

        routePlanObservable = RoutePlanService.getVisits()

        routePlanObservable
            .subscribe {
                it.forEach {
                    GeofenceService.setGeofence(it.coordinates[1], it.coordinates[0], 100f, it.avatar)
                }
            }
    }
}