package com.poseidonapp.model.clockinlist

import com.google.gson.annotations.SerializedName

 class ClockInListResponse(

	@field:SerializedName("data")
	val clockinlistData: ClockinlistData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("status")
	val status: Boolean
)