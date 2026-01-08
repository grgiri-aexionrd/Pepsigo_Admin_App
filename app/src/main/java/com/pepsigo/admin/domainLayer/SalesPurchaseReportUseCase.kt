package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.repository.SalesPurchaseReportRepository
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.reports.DropDownList

class SalesPurchaseReportUseCase(
    private val userRepository: UserRepository,
    private val salesPurchaseReportRepository: SalesPurchaseReportRepository
) {
    suspend fun getUsers(role: String): Result<List<DropDownList>> {
        val result = userRepository.getUsers(role)
        Log.d("SalesRegisterUseCase", "Fetched users for role $role: $result")
        return result.map { users ->
            users.map { DropDownList(it.id, it.name) }
        }
    }

    suspend fun fetchSalesRegister(
        startDate: String,
        endDate: String,
        id: Int?
    ): Result<List<SalesPurchaseReportUi>> {
        return salesPurchaseReportRepository.fetchSalesRegister(startDate, endDate, id)
    }

    suspend fun fetchPurchaseRegister(
        startDate: String,
        endDate: String,
        id: Int?
    ): Result<List<SalesPurchaseReportUi>> {
        return salesPurchaseReportRepository.fetchPurchaseRegister(startDate, endDate, id)
    }

}