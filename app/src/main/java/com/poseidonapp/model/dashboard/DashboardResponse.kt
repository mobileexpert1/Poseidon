package com.poseidonapp.model.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)