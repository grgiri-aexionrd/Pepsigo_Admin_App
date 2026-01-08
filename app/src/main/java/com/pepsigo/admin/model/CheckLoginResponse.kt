package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class CheckLoginResponse(
    val authenticated: Boolean,
    val user: UserDetails
)

data class UserDetails(
    val id: Int,
    val name: String,
    val role: String,
    val email: String,
    val mobile: String,
    @SerializedName("is_enabled")
    val enabled: Boolean
)
