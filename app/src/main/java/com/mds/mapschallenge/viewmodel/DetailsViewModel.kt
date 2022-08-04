package com.mds.mapschallenge.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.repository.routes.RoutesRepository
import kotlinx.coroutines.*

class DetailsViewModel : ViewModel(){
    private var tracking : Polyline?= null
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12


    fun uploadTrackPoints(list : String, googleMap: GoogleMap){

        var points : MutableList<LatLng> = mutableListOf<LatLng>()

        var routes = list.split("|")
        for (rt in routes) {
            var coord = rt.split(",")

             points.add(LatLng(coord[0].toDouble(),coord[1].toDouble()))
        }
        tracking?.points = points

        googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        LatLng(points[0].latitude,points[0].longitude), 20F))

        googleMap?.addMarker(MarkerOptions().position(LatLng(points[0].latitude,points[0].longitude))
                .title("Inicio").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        googleMap?.addMarker(MarkerOptions().position(LatLng(points[routes.size-1].latitude,points[routes.size-1].longitude))
                .title("Fin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        tracking?.tag = "Init location"
        tracking?.endCap = RoundCap()
        tracking?.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        tracking?.color = COLOR_BLACK_ARGB
        tracking?.jointType = JointType.ROUND

        tracking = googleMap.addPolyline(PolylineOptions().addAll(points))
    }


    fun deleteRoute(routesDelete: Routes, application: Application):Boolean{
        runBlocking {
            var scope = CoroutineScope(Dispatchers.IO).async{
            RoutesRepository().deleteRoute(routesDelete,application)
            }
            scope.await()
            return@runBlocking true
        }
        return true
    }


}