package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class RouteAddEditRequest(
    val name: String,
    @SerializedName("location_ids")
    val locationIds: List<Int>
)
