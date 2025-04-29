package com.poseidonapp.model.systemManagement


import com.google.gson.annotations.SerializedName

data class SystemLocation(
    @SerializedName("addressId")
    var addressId: String,
    @SerializedName("assignedQuestion")
    var assignedQuestion: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("sectionName")
    var sectionName: String,
    @SerializedName("systemDescription")
    var systemDescription: String,
    @SerializedName("systemId")
    var systemId: String,
    @SerializedName("type")
    var type: String
)