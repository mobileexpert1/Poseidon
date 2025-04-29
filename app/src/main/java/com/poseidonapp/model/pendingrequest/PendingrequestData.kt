package com.poseidonapp.model.pendingrequest

import com.google.gson.annotations.SerializedName

 class PendingrequestData(

    @field:SerializedName("meetings")
    val meetings: List<MeetingsItem>
)