package com.poseidonapp.model.onRoute

import com.google.gson.annotations.SerializedName

data class OnRouteResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)