package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName


// gets user details from the api

data class UserDto(
    val id: Int,
    val name: String,
    @SerializedName("business_name")
    val businessName: String?,
    val address1: String?,
    val address2: String?,
    val state: String?,
    val pincode: String?,
    val latitude: Double?,
    val longitude: Double?,
    val role: String,
    @SerializedName("is_enabled")
    val enabled: Boolean,
    @SerializedName("mobile_number")
    val mobile: String,
    @SerializedName("mobile_number_verified_at")
    val mobileNumberVerified: String?,
    @SerializedName("location_id")
    val locationId: Int?,
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerified: String?

)