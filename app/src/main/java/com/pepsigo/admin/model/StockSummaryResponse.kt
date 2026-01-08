package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class StockSummaryResponse(
    val count: Int,
    val data: List<StockSummaryItemDto>
)

data class StockSummaryItemDto (
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_name")
    val itemName: String,
    val unit: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
    @SerializedName("available_quantity")
    val availableQuantity: Int,
)
