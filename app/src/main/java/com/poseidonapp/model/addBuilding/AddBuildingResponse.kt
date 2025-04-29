package com.poseidonapp.model.addbuilding


import com.google.gson.annotations.SerializedName

data class AddBuildingResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)