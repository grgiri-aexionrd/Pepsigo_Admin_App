package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class SaleInventorySearchResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<SaleInventorySearchItemDto>
)

data class SaleInventorySearchItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("item_name") val itemName: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("gst_percent") val gstPercent: Int,
    @SerializedName("offer_price") val offerPrice: Double?,
    @SerializedName("is_free") val isFree: Boolean,
    @SerializedName("stock_summary") val stockSummary: SaleInventoryStockSummaryDto
)

data class SaleInventoryStockSummaryDto(
    @SerializedName("total_available") val totalAvailable: Int,
    @SerializedName("batches_count") val batchesCount: Int,
    @SerializedName("nearest_expiry") val nearestExpiry: String?
)
