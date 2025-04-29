package com.poseidonapp.model.dayclockin

import com.google.gson.annotations.SerializedName

data class DayclockinData(

    @field:SerializedName("punch")
    val punch: Boolean? = null
)