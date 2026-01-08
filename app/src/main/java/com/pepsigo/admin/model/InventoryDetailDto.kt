package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

//data class InventoryDetailDto(
//    val data : InventoryItemDetailDto
//)

data class InventoryItemDetailDto(
    val id: Int,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("opening_quantity")
    val quantity: Int,
    val unit: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
    @SerializedName("is_enabled")
    val enabled: Boolean,
    @SerializedName("offer_status")
    val offerStatus: String,
    @SerializedName("offer_details")
    val offerDetails: OfferDetailDto?,
    @SerializedName("stock_summary")
    val stockSummary: StockSummaryDto,
    val batches: List<BatchDto> = emptyList(),
)

data class OfferDetailDto(
    @SerializedName("sale_price")
    val salePrice: Double,
    val quantity: Int,
    @SerializedName("customer_id")
    val customerId: Int,
)

data class StockSummaryDto(
    @SerializedName("total_available")
    val totalAvailable: Int,
    @SerializedName("batches_count")
    val batchesCount: Int,
    @SerializedName("nearest_expiry")
    val expiryDate: String?
)

data class BatchDto(
    @SerializedName("batch_id")
    val id: Int,
    @SerializedName("expiry_date")
    val expiryDate: String,
    @SerializedName("purchased_quantity")
    val purchasedQuantity: Int,
    @SerializedName("sold_quantity")
    val soldQuantity: Int,
    @SerializedName("available_quantity")
    val availableQuantity: Int,
    val unit: String,
    @SerializedName("cost_price")
    val costPrice: Double,
    @SerializedName("sale_price")
    val salePrice: Double,
    @SerializedName("retail_price")
    val retailPrice: Double,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("purchase_date")
    val purchasedDate: String,
    @SerializedName("is_expired")
    val expired: Boolean

)


