package com.poseidonapp.model.schedules

import com.google.gson.annotations.SerializedName

 class ScheduleResponse(

    @SerializedName("data")
    val dataSchedule: DataSchedule,

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("status")
    val status: Boolean
)