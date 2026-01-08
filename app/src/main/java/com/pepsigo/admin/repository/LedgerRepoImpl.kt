package com.pepsigo.admin.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.mapper.toUi
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.wrapError

data class LedgerInitialUi(
    val id: Int,
    val entries: List<TransactionDetailUi>,
    val balance: String
)

data class TransactionDetailUi(
    val type: String,
    val date: String,
    val ref: String,
    val amount: String
)

class LedgerRepoImpl(private val apiService: ApiService): LedgerRepo {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCustomersLedger(customerId: Int, startDate: String?, endDate: String?): Result<LedgerInitialUi> {
        return wrapError {
            val response = apiService.customerLedger(customerId,startDate,endDate)
            LedgerInitialUi(
                id = response.customerId,
                entries = response.entries.map { it.toUi() },
                balance = response.balance.safeAmount()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getVendorsLedger(vendorId: Int, startDate: String?, endDate: String?): Result<LedgerInitialUi> {
        return wrapError {
            val response = apiService.vendorLedger(vendorId,startDate,endDate)
            LedgerInitialUi(
                id = response.vendorId,
                entries = response.entries.map { it.toUi() },
                balance = response.balance.safeAmount()
            )
        }
    }
}