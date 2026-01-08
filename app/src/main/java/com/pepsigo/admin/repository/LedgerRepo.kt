package com.pepsigo.admin.repository

import com.pepsigo.admin.model.CustomerLedgerResponse
import com.pepsigo.admin.model.VendorLedgerResponse

interface LedgerRepo {
    suspend fun getCustomersLedger(customerId: Int,startDate: String?,endDate: String?): Result<LedgerInitialUi>
    suspend fun getVendorsLedger(vendorId: Int,startDate: String?,endDate: String?): Result<LedgerInitialUi>
}



