package com.pepsigo.admin.repository


import com.pepsigo.admin.model.SalesPurchaseReportUi

interface SalesPurchaseReportRepository {
    suspend fun fetchSalesRegister(
        startDate: String,
        endDate: String,
        customerId: Int?
    ): Result<List<SalesPurchaseReportUi>>

    suspend fun fetchPurchaseRegister(
        startDate: String,
        endDate: String,
        vendorId: Int?
    ): Result<List<SalesPurchaseReportUi>>


}