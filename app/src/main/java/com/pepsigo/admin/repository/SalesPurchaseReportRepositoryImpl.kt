package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class SalesPurchaseReportRepositoryImpl(private val apiService: ApiService): SalesPurchaseReportRepository {
    override suspend fun fetchSalesRegister(
        startDate: String,
        endDate: String,
        customerId: Int?
    ) : Result<List<SalesPurchaseReportUi>> {
        return wrapError {
            val response = apiService.getSalesRegister(startDate, endDate, customerId)
            Log.d("SalesRegisterRepository", "Fetched sales register: $response")
            response.data.map { it.toDomain() }
        }
    }

    override suspend fun fetchPurchaseRegister(
        startDate: String,
        endDate: String,
        vendorId: Int?
    ): Result<List<SalesPurchaseReportUi>> {
        return wrapError {
            val response = apiService.getPurchaseRegister(startDate, endDate, vendorId)
            Log.d("PurchaseRegisterRepository", "Fetched purchase register: $response")
            response.data.map { it.toDomain() }
        }
    }
}