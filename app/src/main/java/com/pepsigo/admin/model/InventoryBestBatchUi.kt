package com.pepsigo.admin.model

data class InventoryBestBatchUi (
    val message: String? = null,
    val inventoryId: Int,
    val itemName: String?,
    val customerId: Int?,
    val offerApplied: Boolean?,
    val isFreeOffer: Boolean?,
    val batch: InventoryBestBatchItemUi? = null
)

data class InventoryBestBatchItemUi (
    val batchId: Int,
    val expiryDate: String,
    val availableQuantity: Int,
    val unit: String,
    val costPrice: String,
    val salePrice: String,
    val retailPrice: String,
    val purchaseDate: String,
)