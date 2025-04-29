package com.poseidonapp.model.inspectionnotification

data class InspectionNotificationResponse(
	val data: Data,
	val message: String,
	val error: Error,
	val status: Boolean
)
