package com.poseidonapp.model.addexpense

import com.google.gson.annotations.SerializedName

 class AddExpenseResponse(

     @field:SerializedName("data")
	val addexpenseData: AddexpenseData,

     @field:SerializedName("message")
	val message: String,

     @field:SerializedName("error")
	val error: Error,

     @field:SerializedName("status")
	val status: Boolean
)