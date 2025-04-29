package com.poseidonapp.model.installationAssigned

import com.google.gson.annotations.SerializedName

 class AssignedRequestItem(

	@SerializedName("date")
	val date: String,

	@SerializedName("address")
	val address: String,

	@SerializedName("city")
	val city: String,

	@SerializedName("postalCode")
	val postalCode: String,

	@SerializedName("assignedTo")
	val assignedTo: String,

	@SerializedName("createdAt")
	val createdAt: String,

	@SerializedName("phoneNumber")
	val phoneNumber: String,

	@SerializedName("propertyName")
	val propertyName: String,

	@SerializedName("report")
	val report: String,

	@SerializedName("id")
	val id: String,

	@SerializedName("state")
	val state: String,

	@SerializedName("email")
	val email: String,

	@SerializedName("updatedAt")
	val updatedAt: String,

	@SerializedName("assignedTime")
	val assignedTime: String
)