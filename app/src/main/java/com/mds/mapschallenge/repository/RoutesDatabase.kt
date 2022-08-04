package com.mds.mapschallenge.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.repository.routes.RoutesDAO

@Database(entities = [Routes::class],version = 1, exportSchema = false)
abstract class RoutesDatabase : RoomDatabase(){
    abstract fun RoutesDAO() : RoutesDAO

    companion object{
        @Volatile
        private var INSTANCE: RoutesDatabase? = null

        fun getDatabase(context: Context): RoutesDatabase{
//            val tempInstance = INSTANCE
//            if(tempInstance != null){
//                return  tempInstance
//            }
//            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutesDatabase::class.java,
                    "route_database"
                ).build()
                INSTANCE = instance
                return instance
            //}
        }
    }
}