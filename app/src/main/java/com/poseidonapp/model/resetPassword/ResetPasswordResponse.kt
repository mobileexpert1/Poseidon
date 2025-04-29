package com.poseidonapp.model.resetPassword


import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)