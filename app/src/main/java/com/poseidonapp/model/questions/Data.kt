package com.poseidonapp.model.questions

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("isInclude")
    var isInclude: String,
    @SerializedName("questionJson")
    var questionJson: List<QuestionJson>,
    @SerializedName("sectionName")
    var sectionName: String,
    @SerializedName("systemId")
    var systemId: String,
    @SerializedName("systemLocationText")
    var systemLocationText: String
)