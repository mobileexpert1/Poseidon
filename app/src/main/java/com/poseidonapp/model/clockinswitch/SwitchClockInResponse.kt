package com.poseidonapp.model.clockinswitch

import com.google.gson.annotations.SerializedName

 class SwitchClockInResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("data")
    val switchclockinData: SwitchclockinData,

    @field:SerializedName("status")
    val status: Boolean
)