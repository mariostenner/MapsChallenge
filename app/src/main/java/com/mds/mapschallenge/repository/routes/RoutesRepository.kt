package com.mds.mapschallenge.repository.routes

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mds.mapschallenge.model.MatrixAPI

import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.repository.RoutesDatabase
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoutesRepository() {

    private val BASE_URL = "https://maps.googleapis.com/"
    private lateinit var routesDAO: RoutesDAO

        fun saveRoute(route: Routes,application: Application) {
            var db = RoutesDatabase.getDatabase(application)
            routesDAO = db.RoutesDAO()
            return routesDAO.insert(route)

        }

        fun getRoutes(application: Application): List<Routes> {
            var db = RoutesDatabase.getDatabase(application)
            routesDAO = db.RoutesDAO()
            var response : List<Routes> = routesDAO.selectAll()
            return response
        }

        fun deleteRoute(route: Routes,application: Application){
            var db = RoutesDatabase.getDatabase(application)
            routesDAO = db.RoutesDAO()
            return routesDAO.delete(route)
        }

         fun getTimeKm(): MatrixAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MatrixAPI::class.java)
        }
    //}
}