package com.poseidonapp.model.organization

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("organizations")
    var organizations: ArrayList<Organization>
)