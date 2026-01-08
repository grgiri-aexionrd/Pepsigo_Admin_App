package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class GetRoutesDto(
    val id: Int,
    val name: String,
    @SerializedName("is_enabled")
    val isEnabled: Boolean,
    @SerializedName("route_locations")
    val routeLocations: List<LocationList>
)

data class LocationList(
    val id:Int,
    @SerializedName("route_id")
    val routeID: Int,
    @SerializedName("location_id")
    val locationId: Int,
    @SerializedName("order_id")
    val orderId: Int,
    val location: LocationDto
)


