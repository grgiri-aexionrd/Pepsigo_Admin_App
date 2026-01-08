package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.model.VendorDuesUi
import com.pepsigo.admin.repository.OutstandingDuesRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class OutstandingDuesInitialUi(
    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val customerError: AppError? = null,
    val vendorError: AppError? = null,
)

class OutstandingDuesUseCase(
    private val userRepository: UserRepository,
    private val outstandingDuesRepo: OutstandingDuesRepo
) {

    // fetching customer,vendors data to show in dropdown
    suspend fun getCustomersVendors(): OutstandingDuesInitialUi {
        val customerDeferred = coroutineScope {
            async { userRepository.getUsers("customer") }
        }

        val vendorDeferred = coroutineScope {
            async { userRepository.getUsers("vendor") }
        }

        val customerResult = customerDeferred.await()
        val vendorResult = vendorDeferred.await()

        Log.d("OutstandingDuesUseCase", "Fetched customers: $customerResult")
        Log.d("OutstandingDuesUseCase", "Fetched vendors: $vendorResult")

        val customerDropdown = customerResult.map { users ->
            users.map { DropDownList(it.id, it.name) }
        }
        Log.d("OutstandingDuesUseCase", "Mapped customers: $customerDropdown")

        val vendorDropdown = vendorResult.map { users ->
            users.map { DropDownList(it.id, it.name) }
        }
        Log.d("OutstandingDuesUseCase", "Mapped vendors: $vendorDropdown")

        return OutstandingDuesInitialUi(
            customers = customerDropdown.getOrNull() ?: emptyList(),
            vendors = vendorDropdown.getOrNull() ?: emptyList(),
            customerError = customerDropdown.exceptionOrNull() as? AppError,
            vendorError = vendorDropdown.exceptionOrNull() as? AppError
        )


    }

    // fetch customerDues
    suspend fun fetchCustomerDues(customerId: Int?): Result<List<CustomerDuesUi>> {
        return outstandingDuesRepo.outstandingCustomerDues(customerId)
    }

    suspend fun fetchVendorDues(vendorId: Int?): Result<List<VendorDuesUi>> {
        return outstandingDuesRepo.outstandingVendorDues(vendorId)

    }


}