package com.mds.mapschallenge.model

import retrofit2.Response
import retrofit2.http.*
import java.lang.StringBuilder

interface MatrixAPI {
    @GET("/maps/api/distancematrix/json")
    suspend fun getMatrixMaps(@Query("units") units : String,
                              @Query("origins") origins : String,
                              @Query("destinations") destinations : String,
                              @Query("key") token : String  ): Response<MatrixMaps>
}