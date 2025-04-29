package com.poseidonapp.model.questions

import com.google.gson.annotations.SerializedName


data class QuestionJson(
    @SerializedName("question")
    var question: String,
    @SerializedName("answer")
    var answer: String,
    @SerializedName("values")
    var values: String,
    @SerializedName("type")
    var type: String
)