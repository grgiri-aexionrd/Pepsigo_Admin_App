package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class DeliveryPerformanceResponse(
    val count:Int,
    val data: List<DeliveryPerformanceData>
)

data class DeliveryPerformanceData(
    @SerializedName("executive_id") val executiveId: Int,
    @SerializedName("executive_name") val executiveName: String,
    @SerializedName("routes_assigned")
    val routesAssigned: Int,
    @SerializedName("routes_completed")
    val routesCompleted: String,
    @SerializedName("sales_total")
    val salesTotal: String
)