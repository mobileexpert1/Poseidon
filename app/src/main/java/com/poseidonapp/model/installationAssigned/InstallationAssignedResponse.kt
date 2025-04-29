package com.poseidonapp.model.installationAssigned

import com.google.gson.annotations.SerializedName

 class InstallationAssignedResponse(

	@SerializedName("data")
	val dataInstallationAssigned: DataInstallationAssigned,

	@SerializedName("message")
	val message: String,

	@SerializedName("error")
	val error: Error,

	@SerializedName("status")
	val status: Boolean
)