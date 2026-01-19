package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    val id: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val role: String,
    val business: String? , // nullable
    val address1: String? , // nullable
    val address2: String?, // nullable
    val state: String?, //nullable
    val pincode: String?, // nullable
    val latitude: Double?, // nullable
    val longitude: Double?, // nullable
    @SerializedName("is_enabled")
    val enabled: Boolean,
    @SerializedName("full_address")
    val fullAddress: String? = null // not present in backend to give data
)
