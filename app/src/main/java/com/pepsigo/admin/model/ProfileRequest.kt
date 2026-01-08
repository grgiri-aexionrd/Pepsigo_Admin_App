package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    val id: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val role: String,
    val business: String,
    val address1: String,
    val address2: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("is_enabled")
    val enabled: Boolean,
    @SerializedName("full_address")
    val fullAddress: String
)
