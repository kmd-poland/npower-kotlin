package pl.kmdpoland.npower.services

import android.content.Context
import androidx.room.Room
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.kmdpoland.npower.data.AppDatabase
import pl.kmdpoland.npower.data.Visit
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RoutePlanService {
    private lateinit var database: AppDatabase
    private lateinit var apiService: ApiService

    private var initialized = false

    fun ensureInitialize(context: Context) {
        if(initialized)
            return

        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build()

        apiService = Retrofit.Builder()
            .baseUrl("https://npower.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)

        initialized = true
    }

    fun getVisit(id: String): Visit {
        return database.visitDao().loadSingle(id)
    }

    fun getVisitAsync(id: String): Observable<Visit> {
        return database.visitDao().loadSingleAsync(id).toObservable().subscribeOn(Schedulers.io())
    }


    fun getVisits(): Observable<List<Visit>> {
        return getVisitsFromDb()
            .mergeWith(getVisitsFromApi())
            .subscribeOn(Schedulers.io())
            .debounce(400, TimeUnit.MILLISECONDS)
            .map { it.toList() }
            .publish()
            .refCount()
    }

    fun getVisitsFromDb(): Observable<Array<Visit>> {
        return database
            .visitDao()
            .getAll()
            .toObservable()
    }

    fun getVisitsFromApi(): Observable<Array<Visit>> {
        return apiService
            .getRoutePlan()
            .subscribeOn(Schedulers.io())
            .map { it.visits }
            .doAfterNext { database.visitDao().insertAll(*it) }
    }
}