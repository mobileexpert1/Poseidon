package com.poseidonapp.model.systemManagement


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("systemLocation")
    var systemLocation: List<SystemLocation>
)