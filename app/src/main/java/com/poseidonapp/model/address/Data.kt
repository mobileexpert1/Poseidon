package com.poseidonapp.model.address


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    var address: List<Addres>
)