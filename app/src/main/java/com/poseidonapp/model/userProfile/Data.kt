package com.poseidonapp.model.userProfile

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("userData")
    var userData: UserData
)