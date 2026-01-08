package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class ExecutiveStatusResponse(
    val message: String,
    val count: Int,
    val data: List<ExecutiveStatusDto>
)

data class ExecutiveStatusDto(
    val id: Int,
    val name: String,
    @SerializedName("mobile_number")
    val mobile: String,
    @SerializedName("is_enabled")
    val enabled: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val route: RouteStatusDto?
)

data class RouteStatusDto(
    val id: Int,
    val name: String,
    @SerializedName("assignment_status")
    val status: String
)