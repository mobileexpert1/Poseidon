package com.poseidonapp.model.homepage

import com.google.gson.annotations.SerializedName

 class HomeData(

	@field:SerializedName("projectCount")
	val projectCount: String,

	@field:SerializedName("profilePic")
	val profilePic: String,

	@field:SerializedName("punchData")
	val punchData: Long,

	@field:SerializedName("approvedLeaveCount")
	val approvedLeaveCount: String,

	@field:SerializedName("expenseData")
	val expenseData: List<ExpenseDataItem>,

	@field:SerializedName("userName")
	val userName: String
)