package com.poseidonapp.model.addSystemreport

import com.google.gson.annotations.SerializedName

data class AddSystemFromReportResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)