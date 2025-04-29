package com.poseidonapp.model.applyleave

import com.google.gson.annotations.SerializedName

 class ApplyLeavesResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("data")
    val applyleavesData: ApplyleavesData,

    @field:SerializedName("status")
    val status: Boolean
)