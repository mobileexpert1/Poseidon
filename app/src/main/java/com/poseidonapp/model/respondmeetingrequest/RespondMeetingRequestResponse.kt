package com.poseidonapp.model.respondmeetingrequest

import com.google.gson.annotations.SerializedName

 class RespondMeetingRequestResponse(

    @field:SerializedName("data")
    val respondmeetingData: RespondmeetingData,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("status")
    val status: Boolean
)