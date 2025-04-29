package com.poseidonapp.model.checkroute

import com.google.gson.annotations.SerializedName

data class CheckRouteResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)