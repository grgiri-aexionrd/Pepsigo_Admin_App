package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class LocationDto(
    val id: Int,
    val name: String,
    @SerializedName("is_enabled")
    val enabled: Boolean
)
