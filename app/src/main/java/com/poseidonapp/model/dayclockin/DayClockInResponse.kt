package com.poseidonapp.model.dayclockin

import com.google.gson.annotations.SerializedName

data class DayClockInResponse(

    @field:SerializedName("data")
    val dayclockinData: DayclockinData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: Error? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)




