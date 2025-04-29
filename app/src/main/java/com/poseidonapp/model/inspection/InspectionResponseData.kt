package com.poseidonapp.model.inspection

data class InspectionResponseData(
    val `data`: Data,
    val error: Error,
    val message: String,
    val status: Boolean
)