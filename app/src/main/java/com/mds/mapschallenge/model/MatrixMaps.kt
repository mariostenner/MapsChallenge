package com.mds.mapschallenge.model

import com.google.gson.annotations.SerializedName

data class MatrixMaps(
    @SerializedName("rows")
    var rows : List<Rows>
)

data class Rows(
    @SerializedName("elements")
    var elements : List<Elements>
)

data class Elements(
    @SerializedName("distance")
    var distance : Distance
)

data class Distance(
    @SerializedName("text")
    var text : String
)