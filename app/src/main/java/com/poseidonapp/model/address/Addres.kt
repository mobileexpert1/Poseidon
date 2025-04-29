package com.poseidonapp.model.address


import com.google.gson.annotations.SerializedName

data class Addres(
    @SerializedName("addressLine")
    var addressLine: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("organizationsId")
    var organizationsId: String,
    @SerializedName("postalCode")
    var postalCode: String,
    @SerializedName("state")
    var state: String
)