package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class FCMTokenUpdateRequest(
    @SerializedName("fcm_token")
    val fcmToken: String
)