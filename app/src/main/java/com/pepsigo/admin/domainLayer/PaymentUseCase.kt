package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.repository.PaymentRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.repository.PaymentUiResult
import com.pepsigo.admin.model.PaymentUpdateUiModel
import com.pepsigo.admin.screens.payment.PaymentType
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class PaymentInitialUi(
    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val selectedUser: DropDownList? = null,
    val customerError: AppError? = null,
    val vendorError: AppError? = null,
    val isCustomerEditable: Boolean = false,
)

// Data class for the payment request
data class MakePaymentRequest(
    val saleId: Int? = null,
    val purchaseId: Int? = null,
    val customerId: Int? = null,
    val amount: Double,
    val paymentMethod: String,
    val refNumber: String? = null,
    val transactionType: String,
    val denomination: DenominationInput? = null
)

data class DenominationInput(
    val denom2000: Int = 0,
    val denom500: Int = 0,
    val denom200: Int = 0,
    val denom100: Int = 0,
    val denom50: Int = 0,
    val denom20: Int = 0,
    val denom10: Int = 0,
    val denom5: Int = 0,
    val denom2: Int = 0,
    val denom1: Int = 0,
    val card: Double = 0.00,
    val upi: Double = 0.00,
    val netBanking: Double = 0.00,
    val cheque: Double = 0.00,
    val credit: Double = 0.00
)

class PaymentUseCase(
    private val paymentRepo: PaymentRepo,
    private val userRepository: UserRepository
) {
    suspend fun getInitialUsers(mode: PaymentType): PaymentInitialUi = coroutineScope {

        when (mode) {

            is PaymentType.SalePayment -> {
                val result = userRepository.getUserById(mode.customerId.toInt())

                val dropdown = result.map {
                     DropDownList(it.id, it.name) }


                PaymentInitialUi(
                    selectedUser = dropdown.getOrNull(),
                    isCustomerEditable = false,
                    customerError = dropdown.exceptionOrNull() as? AppError
                )
            }

            is PaymentType.PurchasePayment -> {
                val result = userRepository.getUserById(mode.customerId.toInt())

                val dropdown = result.map {
                     DropDownList(it.id, it.name) }


                PaymentInitialUi(
                    selectedUser = dropdown.getOrNull(),
                    isCustomerEditable = false,
                    vendorError = dropdown.exceptionOrNull() as? AppError
                )
            }

            PaymentType.Standalone -> {
                val customersDeferred = async {
                    userRepository.getUsers("customer")
                }

                val vendorsDeferred = async {
                    userRepository.getUsers("vendor")
                }

                val customerResult = customersDeferred.await()
                val vendorResult = vendorsDeferred.await()

                val customerDropdown = customerResult.map { users ->
                    users.map { DropDownList(it.id, it.name) }
                }

                val vendorDropdown = vendorResult.map { users ->
                    users.map { DropDownList(it.id, it.name) }
                }

                Log.d("PaymentUseCase", "getInitialUsers: $customerResult")
                Log.d("PaymentUseCase", "getInitialUsers: $vendorResult")


                PaymentInitialUi(
                    customers = customerDropdown.getOrNull().orEmpty(),
                    vendors = vendorDropdown.getOrNull().orEmpty(),
                    isCustomerEditable = true,
                    customerError = customerDropdown.exceptionOrNull() as? AppError,
                    vendorError = vendorDropdown.exceptionOrNull() as? AppError
                )
            }
        }

    }

    suspend fun createPayment(request: MakePaymentRequest): Result<PaymentUiResult<PaymentUpdateUiModel>> {
        return paymentRepo.createPayment(request)
    }
}
