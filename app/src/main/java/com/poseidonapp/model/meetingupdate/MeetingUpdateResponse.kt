package com.poseidonapp.model.meetingupdate

data class MeetingUpdateResponse(
	val data: Data,
	val message: String,
	val error: Error,
	val status: Boolean
)
