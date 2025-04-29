package com.poseidonapp.model.homepage

import com.google.gson.annotations.SerializedName

 class ExpenseDataItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)