package com.poseidonapp.model.inspectionHeadsQuestions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class QuestionJsonItem(

//	 @field:SerializedName("question")
	val question: String,
//	 @field:SerializedName("answer")
	 var answer: String,

//	 @field:SerializedName("values")
	 val values: String,

//	 @field:SerializedName("type")
	 val type: String

)
//): Serializable
