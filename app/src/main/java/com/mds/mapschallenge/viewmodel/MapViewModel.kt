package com.mds.mapschallenge.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.repository.routes.RoutesRepository
import com.mds.mapschallenge.ui.fragment.MapFragment
import com.mds.mapschallenge.util.App
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class MapViewModel() : ViewModel() {

    private var tracking : Polyline ?= null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12
    private var cameraPosition: CameraPosition? = null
    private val defaultLocation = LatLng(20.674318, -103.387479)

    private lateinit var lastLocation : LatLng
    internal var map : GoogleMap? = null
    private lateinit var context : Context
    private var activity : Activity = Activity()

    private lateinit var locationCallback: LocationCallback
    private var app = App

    private var time : String = ""
    var miliseconds = 0L

    var getKm = RoutesRepository().getTimeKm()


    var job : Job? = null
    var job2 : Job? = null
    private val loading = MutableLiveData<Boolean>()
    private val countryLoadError = MutableLiveData<String?>()


    companion object{
        internal var locationPermissionGranted = false
        private val TAG = MapFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 18
        const val KEY_CAMERA_POSITION = "camera_position"
        const val KEY_LOCATION = "location"
        var lastKnownLocation: Location? = null
    }

    constructor(context: Context, activity : Activity,map: GoogleMap?) : this() {
        this.context = context
        this.activity = activity
        this.map = map
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    lastLocation = LatLng(location.latitude, location.longitude)
                    updateTrack()
                }
            }
        }
    }
    fun init(context: Context, savedInstanceState:Bundle?):Boolean{
        try {
            if (app.getLocationPermission(context)) {
                locationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    App.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }

            if (savedInstanceState != null) {
                lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
                cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            }

            return true
        }catch(e: java.lang.Exception){return false}

    }

    fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                if(app.getLocationPermission(context)) {
                    locationPermissionGranted = true
                }else{
                    ActivityCompat.requestPermissions(
                        activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        App.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

     fun updateTrack(){
        var points = tracking?.points
        points?.add(lastLocation)
        tracking?.points = points
    }

    fun saveTrack(nameRoute : String){
        var points = tracking?.points
        points?.add(lastLocation)
        tracking?.points = points

        runBlocking {
            job = CoroutineScope(Dispatchers.IO).async {

                    var latLng = points as(List<LatLng>)
                    var origins = "${latLng[0].latitude.toString()},${latLng[0].longitude.toString()}"
                    var destinations = ""
                    var token = "AIzaSyB-cLUEYdj2o4MnS1Gkq1zbr6d4JTORtAI"

                        for (value in 1 until latLng.size) {
                            destinations += "${latLng.get(value).latitude},${latLng.get(value).longitude}|"
                        }
                        destinations = destinations.substring(0,destinations.length-1)


                    val kmresponse = getKm.getMatrixMaps("metric",origins,destinations,token)
                    var meters: Double = 0.0
                    var size = kmresponse.body()!!.rows[0].elements.size
                        for (dist in 0 until size) {
                            var matrix = kmresponse.body()!!.rows[0].elements[0].distance.text
                            meters += matrix.substring(0, matrix.length - 2).toDouble()
                        }

                    var stringRoutes = ""

                        for (cord in latLng){
                            stringRoutes += "${cord.latitude},${cord.longitude}|"
                        }

                    val routeSave = Routes(0,nameRoute,meters,time,stringRoutes.substring(0,stringRoutes.length-1))

                        println(meters)

                    RoutesRepository().saveRoute(routeSave,activity.application)

                        time = ""
            }
        }

        clearInfo()
    }

    fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = it.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", it.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        try {
            var locationRequest : LocationRequest = LocationRequest()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 5L
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

            if (locationPermissionGranted){
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                    Looper.getMainLooper())
                counter(true)
            }

        }catch (e:Exception){

        }
    }


    fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

    }

    fun addPolyline(googleMap: GoogleMap) {

        tracking?.tag = "Init location"
        tracking?.startCap = RoundCap()
        tracking?.endCap = RoundCap()
        tracking?.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        tracking?.color = COLOR_BLACK_ARGB
        tracking?.jointType = JointType.ROUND
        tracking = googleMap.addPolyline(PolylineOptions())
    }

    fun clearInfo(){
        tracking!!.points.clear()
        map!!.clear()
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun counter(flag : Boolean){
        if(flag){
            miliseconds = 0
            job2 = CoroutineScope(Dispatchers.Main).launch {
                        while(flag){
                            miliseconds += 1
                            delay(1000L)
                            println(miliseconds)
                        }

            }
        }else {
                var milisc = miliseconds * 1000L

                 time = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(milisc),
                        TimeUnit.MILLISECONDS.toMinutes(milisc) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisc)),
                        TimeUnit.MILLISECONDS.toSeconds(milisc) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisc)));
                miliseconds = 0
                job2!!.cancel()
        }
    }

}
