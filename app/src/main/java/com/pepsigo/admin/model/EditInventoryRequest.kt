package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class EditInventoryRequest(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
)
