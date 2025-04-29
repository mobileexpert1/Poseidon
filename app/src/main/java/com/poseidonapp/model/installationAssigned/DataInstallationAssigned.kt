package com.poseidonapp.model.installationAssigned

import com.google.gson.annotations.SerializedName

 class DataInstallationAssigned(

	@SerializedName("assignedRequest")
	val assignedRequest: List<AssignedRequestItem>
)