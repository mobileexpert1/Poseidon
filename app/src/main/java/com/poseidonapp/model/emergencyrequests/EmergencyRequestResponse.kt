package com.poseidonapp.model.emergencyrequests

data class EmergencyRequestResponse(
    val data: Data,
    val message: String,
    val error: Error,
    val status: Boolean
)
