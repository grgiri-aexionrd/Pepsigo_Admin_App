package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.domainLayer.ExpiryStatus
import com.pepsigo.admin.domainLayer.StockStatus
import com.pepsigo.admin.model.BatchStockData
import com.pepsigo.admin.model.BatchStockDetail
import com.pepsigo.admin.model.BatchStockItem
import com.pepsigo.admin.model.BatchStockResponse
import com.pepsigo.admin.utils.safeDate

@RequiresApi(Build.VERSION_CODES.O)
fun BatchStockResponse.toDomain(): BatchStockData {
    return BatchStockData(
        count = count,
        data = data.map { it.toDomain() }
    )

}

@RequiresApi(Build.VERSION_CODES.O)
fun BatchStockItem.toDomain(): BatchStockDetail {
    return BatchStockDetail(
        batchId = batchId,
        itemName = itemName,
        expiryDate = expiryDate,
        expiryStatus = ExpiryStatus.UNKNOWN,
        availableQuantity = availableQuantity,
        stockStatus = StockStatus.UNKNOWN,
        unit = unit,
        costPrice = costPrice,
        salePrice = salePrice
    )
}