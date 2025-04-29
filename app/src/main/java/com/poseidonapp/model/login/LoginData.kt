package com.poseidonapp.model.login

import com.google.gson.annotations.SerializedName

 class LoginData(

	@field:SerializedName("inspectorData")
	val inspectorData: InspectorData,

	@field:SerializedName("token")
	val token: String
)