package com.poseidonapp.model.clockInlistscreen

import com.google.gson.annotations.SerializedName

 class ClockInListScreenResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("data")
    val clockinData: ClockinData,

    @field:SerializedName("status")
    val status: Boolean
)