package com.pepsigo.admin.model

data class InventoryAllBatchesUi (
    val inventoryId: Int,
    val itemName: String,
    val customerId: Int?,
    val offerApplied: Boolean,
    val count: Int,
    val batches: List<InventoryBatchUi>
)

data class InventoryBatchUi (
    val batchId: Int,
    val expiryDate: String?,
    val itemQuantity: Int,
    val soldQuantity: Int,
    val availableQuantity: Int,
    val unit: String,
    val costPrice: String,
    val salePrice: String,
    val retailPrice: String,
    val purchaseDate: String,
    val isExpired: Boolean,
    val isFreeOffer: Boolean
)
