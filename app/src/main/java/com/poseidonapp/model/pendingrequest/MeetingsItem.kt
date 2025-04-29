package com.poseidonapp.model.pendingrequest

import com.google.gson.annotations.SerializedName

 class MeetingsItem(

    @field:SerializedName("meetingTime")
    val meetingTime: String,

    @field:SerializedName("note")
    val note: String,

    @field:SerializedName("attachment")
    val attachment: String,

    @field:SerializedName("meetingDate")
    val meetingDate: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("eventType")
    val eventType: String
)