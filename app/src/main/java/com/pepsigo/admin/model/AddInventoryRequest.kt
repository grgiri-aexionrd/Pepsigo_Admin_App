package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class AddInventoryRequest(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("opening_quantity")
    val quantity: Int,
    val unit: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
)
