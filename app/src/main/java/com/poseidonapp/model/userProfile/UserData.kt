package com.poseidonapp.model.userProfile

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("addedOn")
    var addedOn: String,
    @SerializedName("addedOnTimeStamp")
    var addedOnTimeStamp: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("addressLine1")
    var addressLine1: String,
    @SerializedName("addressLine2")
    var addressLine2: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("deleteStatus")
    var deleteStatus: String,
    @SerializedName("deviceToken")
    var deviceToken: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("hourlyPrice")
    var hourlyPrice: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("isSallaried")
    var isSallaried: String,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("mobileNumber")
    var mobileNumber: String,
    @SerializedName("modifiedOn")
    var modifiedOn: String,
    @SerializedName("noOfVacation")
    var noOfVacation: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("postalCode")
    var postalCode: String,
    @SerializedName("profileImage")
    var profileImage: String,
    @SerializedName("role")
    var role: String,
    @SerializedName("sessionToken")
    var sessionToken: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("streetNumber")
    var streetNumber: String,
    @SerializedName("uniqueToken")
    var uniqueToken: String,
    @SerializedName("userName")
    var userName: String
)