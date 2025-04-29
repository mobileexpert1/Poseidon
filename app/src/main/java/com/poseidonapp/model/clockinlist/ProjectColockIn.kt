package com.poseidonapp.model.clockinlist

import com.google.gson.annotations.SerializedName

 class ProjectColockIn(

	@field:SerializedName("timeDifferece")
	val timeDifferece: Int,

	@field:SerializedName("projectId")
	val projectId: Int
)