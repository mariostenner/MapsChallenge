package com.mds.mapschallenge.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.repository.routes.RoutesRepository
import kotlinx.coroutines.*
import java.lang.Exception

class RecordListViewModel : ViewModel() {

    //var routes = Routes("Esta es la ruta al parque","Recorrido de 20Km")
    //private var list = List(20) {routes}

    var job : Job ?= null
    private lateinit var repository : RoutesRepository
    companion object{

    }

    fun getRoutes(application: Application): LiveData<List<Routes>> {
        var itemsTest : MutableLiveData<List<Routes>> = MutableLiveData<List<Routes>>()
        runBlocking {
        job = CoroutineScope(Dispatchers.IO).async {
                repository = RoutesRepository()
                var response = repository.getRoutes(application)
                itemsTest.postValue(response)
        }
    }
        return itemsTest

    }
}