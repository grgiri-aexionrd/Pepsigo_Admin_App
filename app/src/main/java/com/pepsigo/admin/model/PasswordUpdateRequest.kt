package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PasswordUpdateRequest(
    @SerializedName("current_Password")
    val currentPassword: String,
    @SerializedName("new_Password")
    val newPassword: String
)
