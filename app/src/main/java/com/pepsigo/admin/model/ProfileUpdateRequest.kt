package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class ProfileUpdateRequest(
    @SerializedName("business_name")
    val business: String,
    val name: String,
    val mobile: String,
    val address1: String,
    val address2: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double
)
