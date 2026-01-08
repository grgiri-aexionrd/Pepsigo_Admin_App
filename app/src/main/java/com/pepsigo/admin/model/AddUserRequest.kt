package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class AddUserRequest(
    val name: String,
    @SerializedName("mobile_number")
    val mobile: String,
    val email: String?=null,
    val address1: String,
    val address2: String,
    val state: String,
    val pincode: String,
    @SerializedName("location_id")
    val locationId: Int?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("business_name")
    val businessName: String? = null
)
