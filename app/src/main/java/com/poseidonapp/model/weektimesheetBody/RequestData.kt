package com.poseidonapp.model.weektimesheetBody

data class RequestData(
    val sessionToken: String,
    val weekData: List<WeekTimeSheetBody>
)