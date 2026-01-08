package com.pepsigo.admin.repository

import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.model.VendorDuesUi

interface OutstandingDuesRepo {

    suspend fun outstandingCustomerDues(customerId: Int?): Result<List<CustomerDuesUi>>
    suspend fun outstandingVendorDues(vendorId: Int?): Result<List<VendorDuesUi>>

}