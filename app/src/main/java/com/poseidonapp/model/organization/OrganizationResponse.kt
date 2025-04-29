package com.poseidonapp.model.organization

import com.google.gson.annotations.SerializedName

data class OrganizationResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)