package com.poseidonapp.model.clockInlistscreen

import com.google.gson.annotations.SerializedName

 class ClockinData(

    @field:SerializedName("projects")
    val projects: List<ProjectsItem>,

    @field:SerializedName("projectColockIn")
    val projectColockIn: ProjectColockIn
)