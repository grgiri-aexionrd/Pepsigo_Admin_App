package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.model.VendorDuesUi
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class OutstandingDuesRepoImpl(private val apiService: ApiService): OutstandingDuesRepo {
    override suspend fun outstandingCustomerDues(customerId: Int?): Result<List<CustomerDuesUi>> {
        return wrapError{
            val response = apiService.outstandingReceivables(customerId)
            Log.d("OutstandingDuesRepo", "Customer dues: $response")
            response.data.map{ it.toDomain()}

        }
    }

    override suspend fun outstandingVendorDues(vendorId: Int?): Result<List<VendorDuesUi>> {
        return wrapError{
            val response = apiService.outstandingPayables(vendorId)
            Log.d("OutstandingDuesRepo", "Vendor dues: $response")
            response.data.map{ it.toDomain() }
        }

    }
}