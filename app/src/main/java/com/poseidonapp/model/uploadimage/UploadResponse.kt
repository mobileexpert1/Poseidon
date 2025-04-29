package com.poseidonapp.model.uploadimage

data class UploadResponse(
	val data: Data,
	val message: String,
	val error: Error,
	val status: Boolean
)
