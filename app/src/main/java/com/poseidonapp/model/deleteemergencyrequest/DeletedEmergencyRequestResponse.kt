package com.poseidonapp.model.deleteemergencyrequest

data class DeletedEmergencyRequestResponse(
    val data: Data,
    val message: String,
    val error: Error,
    val status: Boolean
)
