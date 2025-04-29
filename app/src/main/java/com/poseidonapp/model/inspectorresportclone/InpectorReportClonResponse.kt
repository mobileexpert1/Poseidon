package com.poseidonapp.model.inspectorresportclone

import com.google.gson.annotations.SerializedName

 class InpectorReportClonResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("status")
	val status: Boolean
)