package com.poseidonapp.model.userProfile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)