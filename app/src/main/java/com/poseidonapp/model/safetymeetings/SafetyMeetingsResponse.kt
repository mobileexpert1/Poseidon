package com.poseidonapp.model.safetymeetings

data class SafetyMeetingsResponse(
	val data: Data,
	val message: String,
	val error: Error,
	val status: Boolean
)
