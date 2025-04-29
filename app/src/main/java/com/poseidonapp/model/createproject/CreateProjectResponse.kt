package com.poseidonapp.model.createproject

import com.google.gson.annotations.SerializedName

data class CreateProjectResponse(

    @field:SerializedName("createproject_data")
    val createprojectData: CreateprojectData,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Error,

    @field:SerializedName("status")
    val status: Boolean
)



