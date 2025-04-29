package com.poseidonapp.model.feedback

import com.google.gson.annotations.SerializedName

 class FeedBackResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("data")
    val dataFeedBack: DataFeedBack,

    @SerializedName("status")
    val status: Boolean
)