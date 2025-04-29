package com.poseidonapp.model.bodyQuestionjson

import com.poseidonapp.model.inspectionHeadsQuestions.DataItem

data class QuestionJson(
    val status: Boolean,
    val message: String,
    val data: List<DataItem>
)