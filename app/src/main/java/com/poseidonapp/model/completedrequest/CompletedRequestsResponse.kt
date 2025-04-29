package com.poseidonapp.model.completedrequest

data class CompletedRequestsResponse(
    val data: Data,
    val message: String,
    val error: Error,
    val status: Boolean
)
