package com.poseidonapp.model.inspectoracceptreject

data class AcceptRejectResponse(
	val data: AcceptrejectData,
	val message: String,
	val error: Error,
	val status: Boolean
)
