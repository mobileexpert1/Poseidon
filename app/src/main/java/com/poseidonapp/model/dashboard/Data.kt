package com.poseidonapp.model.dashboard

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("inspectorName")
    var inspectorName: String,
    @SerializedName("requests")
    var requests: List<Request>
)