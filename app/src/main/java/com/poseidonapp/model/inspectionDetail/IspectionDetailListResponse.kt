package com.poseidonapp.model.inspectionDetail

data class IspectionDetailListResponse(
    val `data`: Data,
    val error: Error,
    val message: String,
    val status: Boolean
)