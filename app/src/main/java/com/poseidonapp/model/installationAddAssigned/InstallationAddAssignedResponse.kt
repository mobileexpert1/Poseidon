package com.poseidonapp.model.installationAddAssigned

import com.google.gson.annotations.SerializedName

 class InstallationAddAssignedResponse(

	@SerializedName("message")
	val message: String,

	@SerializedName("error")
	val error: Error,

	@SerializedName("data")
	val dataAddAssigned: DataAddAssigned,

	@SerializedName("status")
	val status: Boolean
)