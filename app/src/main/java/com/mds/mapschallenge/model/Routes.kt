package com.mds.mapschallenge.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "routes")
data class Routes(
    @PrimaryKey(autoGenerate = true)
    var Id : Int,

    @NotNull
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    var RouteName: String,

    @NotNull
    @ColumnInfo(name = "km")
    @SerializedName("km")
    @Expose
    var KmTraveled: Double,

    @NotNull
    @ColumnInfo(name = "time")
    @SerializedName("time")
    @Expose
    var TimeTraveled: String,

    @NotNull
    @ColumnInfo(name = "track")
    @SerializedName("track")
    @Expose
    var TrackPoints: String

)