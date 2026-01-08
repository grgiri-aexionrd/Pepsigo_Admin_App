package com.pepsigo.admin.repository

import com.pepsigo.admin.model.BatchStockData
import com.pepsigo.admin.model.BatchStockItem

interface BatchStockRepo {
    suspend fun getBatchStock(id:Int?): Result<BatchStockData>
}


