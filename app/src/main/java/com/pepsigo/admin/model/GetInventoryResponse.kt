package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class GetInventoryResponse(
    val count: Int,
    val data: List<InventoryItem>

)
data class InventoryItem(
    val id: Int,
    @SerializedName("item_name")
    val name: String,
    val unit: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
    @SerializedName("is_enabled")
    val enabled: Boolean
)