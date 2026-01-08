package com.pepsigo.admin.model

data class InventoryItemDetailUi(
    val itemDetail: InventoryListUi,
    val offer: String,
    val offerDetail: OfferDetailUi?,
    val stockSummary: StockSummaryUi,
    val batches: List<BatchUi>
)

data class OfferDetailUi(
    val salePrice: Double,
    val quantity: Int,
    val customerId: Int,
)

data class StockSummaryUi(
    val totalAvailable: Int,
    val batchesCount: Int,
    val nearestExpiry: String?
)

data class BatchUi(
    val id: Int,
    val expiryDate: String,
    val purchasedQuantity: Int,
    val soldQuantity: Int,
    val availableQuantity: Int,
    val unit: String,
    val costPrice: Double,
    val salePrice: Double,
    val retailPrice: Double,
    val purchasedDate: String,
    val expired: Boolean
)
