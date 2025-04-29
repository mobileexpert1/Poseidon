package com.poseidonapp.model.building

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("buildings")
    var buildings: List<Building>
)