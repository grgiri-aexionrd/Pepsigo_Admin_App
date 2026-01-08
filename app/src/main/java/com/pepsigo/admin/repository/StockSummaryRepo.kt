package com.pepsigo.admin.repository

import com.pepsigo.admin.model.StockSummaryData

interface StockSummaryRepo {
    suspend fun getStockSummary(): Result<List<StockSummaryData>>
}