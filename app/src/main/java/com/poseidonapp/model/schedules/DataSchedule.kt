package com.poseidonapp.model.schedules

import com.google.gson.annotations.SerializedName

 class DataSchedule(

    @SerializedName("meetings")
    val meetings: List<MeetingsItem>,

    @SerializedName("dates")
    val dates: List<String>
)