package com.pepsigo.admin.model

data class SaleInventorySearchUi(
    val count: Int,
    val data: List<SaleInventorySearchItemUi>
)

data class SaleInventorySearchItemUi(
    val id: Int,
    val itemName: String,
    val unit: String,
    val gstPercent: String,
    val offerPrice: String,
    val isFree: Boolean,
    val stockSummary: SaleInventoryStockSummaryUi
)

data class SaleInventoryStockSummaryUi(
    val totalAvailable: Int,
    val batchesCount: Int,
    val nearestExpiry: String
)