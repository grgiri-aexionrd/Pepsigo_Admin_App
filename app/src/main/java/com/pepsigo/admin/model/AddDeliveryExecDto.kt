package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class AddDeliveryExecDto(
    val name: String,
    val email: String,
    @SerializedName("mobile_number")
    val mobile: String,
    val password: String
)
