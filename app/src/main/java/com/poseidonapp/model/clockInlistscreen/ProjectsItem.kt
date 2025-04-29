package com.poseidonapp.model.clockInlistscreen

import com.google.gson.annotations.SerializedName

 class ProjectsItem(

    @field:SerializedName("address")
    val address: String,

    @field:SerializedName("distance")
    val distance: String,

    @field:SerializedName("mobileNumber")
    val mobileNumber: String,

    @field:SerializedName("fullName")
    val fullName: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("clockedInStatus")
    val clockedInStatus: Int,

    @field:SerializedName("email")
    val email: String,

    var isChecked:Boolean=false
)