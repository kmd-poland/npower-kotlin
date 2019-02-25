package pl.kmdpoland.npower.viewModels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.kmdpoland.npower.data.RoutePlan
import pl.kmdpoland.npower.services.ApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RoutePlanViewModel : ViewModel() {
    private val apiService: ApiService

    val routePlanObservable: Observable<RoutePlan>

    init {

        apiService = Retrofit.Builder()
            .baseUrl("https://npower.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)

        routePlanObservable = apiService
            .getRoutePlan()
            .subscribeOn(Schedulers.io())
    }
}