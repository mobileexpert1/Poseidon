package com.poseidonapp.model.addRequest


import com.google.gson.annotations.SerializedName

data class AddRequestResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)