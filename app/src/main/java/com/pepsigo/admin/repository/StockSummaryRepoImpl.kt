package com.pepsigo.admin.repository

import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.StockSummaryData
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class StockSummaryRepoImpl(private val apiService: ApiService) : StockSummaryRepo{
    override suspend fun getStockSummary(): Result<List<StockSummaryData>> {
        return wrapError {
            val response = apiService.getStockSummary()
            response.data.map{it.toDomain()}
        }
    }
}