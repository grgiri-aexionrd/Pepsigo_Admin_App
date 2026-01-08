package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class RouteAssignResponse(
    val id: Int,
    @SerializedName("delivery_executive_id")
    val delExecId: Int,
    @SerializedName("route_id")
    val routeId: Int,
    val status: String,
    val route: RouteDto,
    val deliveryExecutive: UserDto
)

data class RouteDto(
    val id: Int,
    val name: String,
    @SerializedName("is_enabled")
    val isEnabled: Boolean,
)
