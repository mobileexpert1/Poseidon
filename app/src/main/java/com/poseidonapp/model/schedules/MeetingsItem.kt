package com.poseidonapp.model.schedules

import com.google.gson.annotations.SerializedName

 class MeetingsItem(

    @SerializedName("meetingTime")
    val meetingTime: String,

    @SerializedName("note")
    val note: String,

    @SerializedName("attachment")
    val attachment: String,

    @SerializedName("meetingDate")
    val meetingDate: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("eventType")
    val eventType: String
)