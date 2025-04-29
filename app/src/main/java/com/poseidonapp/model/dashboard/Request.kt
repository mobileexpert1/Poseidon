package com.poseidonapp.model.dashboard

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("assignedDate")
    var assignedDate: String,
    @SerializedName("data")
    var `data`: List<DataX>
)