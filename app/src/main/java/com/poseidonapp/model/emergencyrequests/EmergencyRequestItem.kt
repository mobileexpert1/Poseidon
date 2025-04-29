package com.poseidonapp.model.emergencyrequests

data class EmergencyRequestItem(
    val jobName: String,
    val dateOfOrder: String,
    val jobPhone: String,
    val orderNumber: String,
    val jobLocation: String,
    val terms: String,
    val phone: String,
    val name: String,
    val orderTakenBy: String,
    val id: String,
    val userId: String,
    val startingDate: String
)
