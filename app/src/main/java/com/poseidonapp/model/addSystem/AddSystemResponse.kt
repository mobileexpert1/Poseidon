package com.poseidonapp.model.addsystemManagement


import com.google.gson.annotations.SerializedName

data class AddSystemResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)