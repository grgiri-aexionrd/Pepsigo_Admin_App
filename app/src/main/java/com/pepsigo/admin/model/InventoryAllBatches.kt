package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class InventoryAllBatchesResponse(
    @SerializedName("inventory_id") val inventoryId: Int,
    @SerializedName("item_name") val itemName: String,
    @SerializedName("customer_id") val customerId: Int?,
    @SerializedName("offer_applied") val offerApplied: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("batches") val batches: List<InventoryBatchDto>
)

data class InventoryBatchDto(
    @SerializedName("batch_id") val batchId: Int,
    @SerializedName("expiry_date") val expiryDate: String?,
    @SerializedName("item_quantity") val itemQuantity: Int,
    @SerializedName("sold_quantity") val soldQuantity: Int,
    @SerializedName("available_quantity") val availableQuantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("cost_price") val costPrice: Double,
    @SerializedName("sale_price") val salePrice: Double,
    @SerializedName("retail_price") val retailPrice: Double,
    @SerializedName("purchase_date") val purchaseDate: String,
    @SerializedName("is_expired") val isExpired: Boolean,
    @SerializedName("is_free_offer") val isFreeOffer: Boolean
)
