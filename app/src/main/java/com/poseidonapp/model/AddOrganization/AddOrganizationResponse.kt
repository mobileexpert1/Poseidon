package com.poseidonapp.model.addOrganization


import com.google.gson.annotations.SerializedName

data class AddOrganizationResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)