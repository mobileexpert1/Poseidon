package com.poseidonapp.model.building

import com.google.gson.annotations.SerializedName

data class Building(
    @SerializedName("addressId")
    var addressId: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String
)