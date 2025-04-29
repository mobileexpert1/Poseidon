package com.poseidonapp.model.login

import com.google.gson.annotations.SerializedName

 class LoginResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("data")
	val loginData: LoginData,

	@field:SerializedName("status")
	val status: Boolean
)