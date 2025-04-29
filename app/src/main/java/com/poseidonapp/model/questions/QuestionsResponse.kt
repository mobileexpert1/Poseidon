package com.poseidonapp.model.questions

import com.google.gson.annotations.SerializedName

data class QuestionsResponse(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("error")
    var error: Error,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)