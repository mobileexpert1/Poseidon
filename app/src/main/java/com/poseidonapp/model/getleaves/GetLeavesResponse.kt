package com.poseidonapp.model.getleaves

data class GetLeavesResponse(
	val data: Data,
	val message: String,
	val error: Error,
	val status: Boolean
)
