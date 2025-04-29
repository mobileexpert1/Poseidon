package com.poseidonapp.model.logout

import com.google.gson.annotations.SerializedName

 class LogoutResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("data")
	val logoutData: LogoutData,

	@field:SerializedName("status")
	val status: Boolean
)