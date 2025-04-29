package com.poseidonapp.model.clockinlist

import com.google.gson.annotations.SerializedName

 class ClockinlistData(

	@field:SerializedName("projects")
	val projects: ArrayList<ProjectsItem>,

	@field:SerializedName("projectColockIn")
	val projectColockIn: ProjectColockIn
)