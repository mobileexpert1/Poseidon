package com.poseidonapp.model.login

import com.google.gson.annotations.SerializedName

 class InspectorData(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("mobileNumber")
	val mobileNumber: String,

	@field:SerializedName("postalCode")
	val postalCode: String,

	@field:SerializedName("latitude")
	val latitude: String,

	@field:SerializedName("profileImage")
	val profileImage: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("modifiedOn")
	val modifiedOn: String,

	@field:SerializedName("deleteStatus")
	val deleteStatus: String,

	@field:SerializedName("uniqueToken")
	val uniqueToken: String,

	@field:SerializedName("addressLine1")
	val addressLine1: String,

	@field:SerializedName("addressLine2")
	val addressLine2: String,

	@field:SerializedName("isSallaried")
	val isSallaried: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("state")
	val state: String,

	@field:SerializedName("hourlyPrice")
	val hourlyPrice: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("longitude")
	val longitude: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("streetNumber")
	val streetNumber: String,

	@field:SerializedName("userName")
	val userName: String,

	@field:SerializedName("addedOn")
	val addedOn: String,

	@field:SerializedName("deviceToken")
	val deviceToken: String,

	@field:SerializedName("sessionToken")
	val sessionToken: String,

	@field:SerializedName("addedOnTimeStamp")
	val addedOnTimeStamp: String,

	@field:SerializedName("noOfVacation")
	val noOfVacation: String,

	@field:SerializedName("status")
	val status: String
)