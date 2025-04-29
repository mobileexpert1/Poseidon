package com.poseidonapp.model.building

import com.google.gson.annotations.SerializedName

data class BuildingResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)