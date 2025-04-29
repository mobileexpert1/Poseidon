package com.poseidonapp.model.homepage

import com.google.gson.annotations.SerializedName

 class HomeResponse(

	@field:SerializedName("data")
	val homeData: HomeData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("status")
	val status: Boolean
)