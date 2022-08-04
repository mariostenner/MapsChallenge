package com.mds.mapschallenge.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.mds.mapschallenge.model.MatrixMaps
import java.lang.StringBuilder

class App() {

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        internal fun getLocationPermission(context: Context) : Boolean{
            return ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

    }
}