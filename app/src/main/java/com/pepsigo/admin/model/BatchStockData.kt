package com.pepsigo.admin.model


import com.pepsigo.admin.domainLayer.ExpiryStatus
import com.pepsigo.admin.domainLayer.StockStatus


data class BatchStockData(
    val count: Int,
    val totalAvailableQuantity: Int = 0,
    val data : List<BatchStockDetail>
)


data class BatchStockDetail(
    val batchId: Int,
    val itemName: String,
    val expiryDate: String?,
    val expiryStatus: ExpiryStatus,
    val availableQuantity: Int,
    val stockStatus: StockStatus,
    val unit: String,
    val costPrice: Double,
    val salePrice: Double,
    )