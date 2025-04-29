package com.poseidonapp.model.inspectionHeadsQuestions

 class QuestionsListResponse(
	val data: List<DataItem>,
	val message: String,
	val error: Error,
	val status: Boolean
)
