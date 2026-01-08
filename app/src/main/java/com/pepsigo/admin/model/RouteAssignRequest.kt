package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class RouteAssignRequest(
    @SerializedName("delivery_executive_id")
    val delExecId: Int,
    @SerializedName("route_id")
    val routeId: Int
)
