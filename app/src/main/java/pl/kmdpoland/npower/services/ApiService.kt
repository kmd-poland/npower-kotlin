package pl.kmdpoland.npower.services

import io.reactivex.Observable
import pl.kmdpoland.npower.data.RoutePlan
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api/routeplan")
    fun getRoutePlan() : Observable<RoutePlan>

}