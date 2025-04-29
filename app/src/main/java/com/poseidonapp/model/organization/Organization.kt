package com.poseidonapp.model.organization

import com.google.gson.annotations.SerializedName

data class Organization(
    @SerializedName("address")
    var address: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("requestEmail")
    var requestEmail: String,
    @SerializedName("requestName")
    var requestName: String,
    @SerializedName("requestPhoneNumber")
    var requestPhoneNumber: String,
    @SerializedName("updatedAt")
    var updatedAt: String
)