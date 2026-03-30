package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class InventoryBestBatchResponse(
    // Present when no batch found
    val message: String? = null,

    @SerializedName("inventory_id") val inventoryId: Int,
    @SerializedName("customer_id") val customerId: Int?,

    // These fields are only present when a batch is found
    @SerializedName("item_name") val itemName: String? = null,
    @SerializedName("offer_applied") val offerApplied: Boolean? = null,
    @SerializedName("is_free_offer") val isFreeOffer: Boolean? = null,
    val batch: InventoryBestBatchDto? = null
)

data class InventoryBestBatchDto(
    @SerializedName("batch_id") val batchId: Int,
    @SerializedName("expiry_date") val expiryDate: String?,
    @SerializedName("available_quantity") val availableQuantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("cost_price") val costPrice: Double,
    @SerializedName("sale_price") val salePrice: Double,
    @SerializedName("retail_price") val retailPrice: Double,
    @SerializedName("purchase_date") val purchaseDate: String,
)
