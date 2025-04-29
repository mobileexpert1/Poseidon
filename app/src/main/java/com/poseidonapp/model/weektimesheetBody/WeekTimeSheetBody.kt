package com.poseidonapp.model.weektimesheetBody

data class WeekTimeSheetBody(
	val jobName: String,
	val inTime: String,
	val hrs: String,
	val descriptionOfWork: String,
	val weekName: String,
	val outTime: String
)
