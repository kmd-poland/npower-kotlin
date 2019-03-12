package pl.kmdpoland.npower.ui.routePlan

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.route_plan_fragment.*
import kotlinx.android.synthetic.main.route_plan_fragment.view.*
import kotlinx.android.synthetic.main.route_plan_panel_view.*
import kotlinx.android.synthetic.main.route_plan_panel_view.view.*
import pl.kmdpoland.npower.R
import pl.kmdpoland.npower.services.GeofenceIntentService
import pl.kmdpoland.npower.services.LocationService
import pl.kmdpoland.npower.ui.main.MainFragmentDirections
import pl.kmdpoland.npower.viewModels.RoutePlanViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RoutePlanFragment : Fragment() {
    companion object {
        fun newInstance() = RoutePlanFragment()
    }

    private lateinit var disposable: CompositeDisposable
    private lateinit var viewModel: RoutePlanViewModel

    private lateinit var map: MapboxMap
    private lateinit var mapView: MapView
    private lateinit var symbolManager: SymbolManager
    private lateinit var style: Style

    private var naviRoute: NavigationMapRoute? = null
    private var annotationList: MutableList<Symbol> = mutableListOf()

    private var routePlanAdapter: RoutePlanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(RoutePlanViewModel::class.java)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(pl.kmdpoland.npower.R.layout.route_plan_fragment, container, false)

        Mapbox.getInstance(this.context!!, getString(R.string.mapbox_access_token))
        view.mapView.onCreate(savedInstanceState)

        mapView = view.mapView
        mapView?.getMapAsync { mapboxMap ->

            map = mapboxMap

            map.setStyle(Style.MAPBOX_STREETS) {

                symbolManager = SymbolManager(mapView, map, it);
                symbolManager.iconAllowOverlap = true

                style = it

                startObservingRoutePlan()
                configureMapBoxLocationComponent()
            }
        }

        view.recyclerView.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?

        return view
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        disposable = CompositeDisposable()

        viewModel
            .routePlanObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                routePlanAdapter = RoutePlanAdapter(it, context!!)
                recyclerView.adapter = routePlanAdapter

                routePlanAdapter!!.itemSelectedSubject.subscribe {
                    viewModel.selectedVisit.value = it.first


                    val extras = FragmentNavigatorExtras(
                        it.second to "avatar", panel_layout to "layout"
                    )

                    var action = RoutePlanFragmentDirections.actionRoutePlanFragmentToVisitFragment()
                    findNavController().navigate(action, extras)

                }.addTo(disposable)
            }.addTo(disposable)
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    private fun startObservingRoutePlan() {

        viewModel
            .routePlanObservable
            .withLatestFrom(LocationService.locationObservable)
            .filter { it.first.any() }
            .map { Pair(it.first[0], it.second) }
            .subscribe {
                val client = MapboxDirections.builder()
                    .origin(com.mapbox.geojson.Point.fromLngLat(
                        it.second.longitude,
                        it.second.latitude
                    ))
                    .destination(com.mapbox.geojson.Point.fromLngLat(
                        it.first.coordinates[0],
                        it.first.coordinates[1]
                    ))
                    .steps(true)
                    .overview(DirectionsCriteria.OVERVIEW_FULL)
                    .profile(DirectionsCriteria.PROFILE_DRIVING)
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build()

                client?.enqueueCall(object : Callback<DirectionsResponse> {
                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {

                        val response = response
                        if(response.body() != null) {
                            val currentRoute = response.body()!!.routes()[0]

                            if (naviRoute == null)
                                naviRoute = NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute)

                            naviRoute!!.removeRoute()
                            naviRoute!!.addRoute(currentRoute)
                        }
                    }
                })
            }
            .addTo(disposable)

        viewModel
            .routePlanObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                symbolManager.delete(annotationList)
                annotationList.clear()

                it.forEach {

                    Glide
                        .with(context)
                        .asBitmap()
                        .load(it.avatar)
                        .apply(RequestOptions.overrideOf(100))
                        .apply(RequestOptions.circleCropTransform())
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap?,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                style.addImage("${it.firstName}_${it.lastName}", resource!!)

                                var symbol = symbolManager.create(
                                    SymbolOptions()
                                        .withLatLng(LatLng(it.coordinates[1], it.coordinates[0]))
                                        .withIconImage("${it.firstName}_${it.lastName}")
                                        .withIconSize(1.3f)
                                        .withZIndex(10))

                                annotationList.add(symbol)
                            }
                        })
                }
            }
            .addTo(disposable)
    }

    @SuppressLint("MissingPermission")
    private fun configureMapBoxLocationComponent() {
        val locationComponent = map?.locationComponent

        // Activate with options
        locationComponent?.activateLocationComponent(this.context!!, map.style!!)
        locationComponent?.isLocationComponentEnabled = true
        locationComponent?.cameraMode = CameraMode.TRACKING
        locationComponent?.renderMode = RenderMode.COMPASS
    }
}