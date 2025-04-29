package com.poseidonapp.model.pendingrequest

import com.google.gson.annotations.SerializedName

class PendingRequestResponse(

    @field:SerializedName("data")
    val pendingrequestData: PendingrequestData,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("status")
    val status: Boolean
)