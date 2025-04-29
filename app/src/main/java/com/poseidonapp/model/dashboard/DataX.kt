package com.poseidonapp.model.dashboard

import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("addedOn")
    var addedOn: String,
    @SerializedName("addedOnTimeStamp")
    var addedOnTimeStamp: String,
    @SerializedName("addressLine1")
    var addressLine1: String,
    @SerializedName("addressLine2")
    var addressLine2: String,
    @SerializedName("assignTo")
    var assignTo: String,
    @SerializedName("assignedDate")
    var assignedDate: String,
    @SerializedName("assignedTime")
    var assignedTime: String,
    @SerializedName("billedStatus")
    var billedStatus: String,
    @SerializedName("buildingNo")
    var buildingNo: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("commentsStatus")
    var commentsStatus: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("fireExtinguisher")
    var fireExtinguisher: String,
    @SerializedName("fullName")
    var fullName: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("inspectionType")
    var inspectionType: String,
    @SerializedName("inspectorInspectionStatus")
    var inspectorInspectionStatus: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("mobileNumber")
    var mobileNumber: String,
    @SerializedName("modifiedOn")
    var modifiedOn: String,
    @SerializedName("notes")
    var notes: String,
    @SerializedName("notifyOneYear")
    var notifyOneYear: String,
    @SerializedName("organizationId")
    var organizationId: String,
    @SerializedName("organizationName")
    var organizationName: String,
    @SerializedName("postalCode")
    var postalCode: String,
    @SerializedName("profileImage")
    var profileImage: String,
    @SerializedName("questionJson")
    var questionJson: String,
    @SerializedName("remind")
    var remind: String,
    @SerializedName("report")
    var report: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("streetNumber")
    var streetNumber: String,
    @SerializedName("timeTake")
    var timeTake: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("userAddressLine1")
    var userAddressLine1: String,
    @SerializedName("userAddressLine2")
    var userAddressLine2: String,
    @SerializedName("userCity")
    var userCity: String,
    @SerializedName("userEmail")
    var userEmail: String,
    @SerializedName("userGender")
    var userGender: String,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("userMobileNumber")
    var userMobileNumber: String,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("userPostalCode")
    var userPostalCode: String,
    @SerializedName("userState")
    var userState: String,
    @SerializedName("userStreetNumber")
    var userStreetNumber: String,
    @SerializedName("winterization")
    var winterization: String
)