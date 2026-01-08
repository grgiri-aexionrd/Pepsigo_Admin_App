package com.pepsigo.admin.mapper

import com.pepsigo.admin.domainLayer.resolveStockStatus
import com.pepsigo.admin.model.StockSummaryData
import com.pepsigo.admin.model.StockSummaryItemDto

fun StockSummaryItemDto.toDomain(): StockSummaryData {
    return StockSummaryData(
        itemId = itemId,
        itemName = itemName,
        unit = unit,
        gstPercent = gstPercent,
        availableQuantity = availableQuantity,
        stockStatus = resolveStockStatus(availableQuantity)
    )

}