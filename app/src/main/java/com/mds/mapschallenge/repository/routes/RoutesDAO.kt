package com.mds.mapschallenge.repository.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.mds.mapschallenge.model.Routes
import retrofit2.http.DELETE

@Dao
interface RoutesDAO {

    @Query("SELECT * FROM routes")
    fun selectAll(): List<Routes>

    @Delete
    fun delete(vararg route: Routes)

    @Insert
    fun insert(vararg route : Routes)

}