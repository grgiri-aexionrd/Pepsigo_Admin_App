package com.pepsigo.admin.model

import com.pepsigo.admin.domainLayer.StockStatus

data class StockSummaryData(
    val itemId: Int,
    val itemName: String,
    val unit: String,
    val gstPercent: Double,
    val availableQuantity: Int,
    val stockStatus: StockStatus,
)