package com.pepsigo.admin.screens.payment

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.DenominationInput
import com.pepsigo.admin.domainLayer.MakePaymentRequest
import com.pepsigo.admin.domainLayer.PaymentUseCase
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Sealed class to represent different payment types
sealed class PaymentType {
    data class SalePayment(val saleId: String, val customerId: String) : PaymentType()
    data class PurchasePayment(val purchaseId: String, val customerId: String) : PaymentType()
    object Standalone : PaymentType()
}

enum class PartyType {
    CUSTOMER,
    VENDOR
}
data class CashDenomination(
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
)

data class DigitalDenomination(
    val card: Double = 0.0,
    val upi: Double = 0.0,
    val netBanking: Double = 0.0,
    val cheque: Double = 0.0,
    val credit: Double = 0.0
)

sealed interface PaymentStep {
    object Entry : PaymentStep
    object Review : PaymentStep
    object Success : PaymentStep
}

data class MakePaymentUiState(
    val step: PaymentStep = PaymentStep.Entry,
    val saleId: String? = null,
    val purchaseId: String? = null,

    val selectedCustomerId: String? = null,
    val selectedCustomerName: String = "",
    val selectedCustomer: DropDownList? = null,

    val partyType: PartyType = PartyType.CUSTOMER,
    val customerSearchQuery: String = "",
    val vendorSearchQuery: String = "",

    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val customerError: String? = null,
    val vendorError: String? = null,

    val totalAmount: String? = null,
    val paymentAmountError: Boolean = false,


    val paymentAmount: String = "",
    val paymentMethod: String = "",
    val transactionType: String = "",
    val refNumber: String = "",
    val cashDenomination: CashDenomination = CashDenomination(),
    val digitalDenomination: DigitalDenomination = DigitalDenomination(),

    val isCustomerEditable: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val retryLoading: Boolean = false

)

class MakePaymentViewModel(private val paymentUseCase: PaymentUseCase,
                           savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extract navigation arguments
    val saleId: String? =
        savedStateHandle.get<String>("saleId")?.takeIf { it.isNotBlank() }

    val purchaseId: String? =
        savedStateHandle.get<String>("purchaseId")?.takeIf { it.isNotBlank() }

    val customerId: String? =
        savedStateHandle.get<String>("customerId")?.takeIf { it.isNotBlank() }

    val amount: String? =
        savedStateHandle.get<String>("amount")?.takeIf { it.isNotBlank() }

    private var currentPaymentType: PaymentType = PaymentType.Standalone


    private val _uiState = MutableStateFlow(MakePaymentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        determinePaymentTypeAndLoadData()
    }

    private fun determinePaymentTypeAndLoadData() {
        currentPaymentType = when{
            saleId != null && customerId != null ->  PaymentType.SalePayment(saleId, customerId)

            purchaseId != null && customerId != null -> PaymentType.PurchasePayment(purchaseId, customerId)

            else -> PaymentType.Standalone

        }
        Log.d("MakePaymentViewModel", "determinePaymentTypeAndLoadData: $currentPaymentType")
        loadPaymentData(currentPaymentType)
    }

    fun loadPaymentData(mode: PaymentType) {

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    customerError = null,
                    vendorError = null
                )
            }

            val initialUi = paymentUseCase.getInitialUsers(mode)
            Log.d("MakePaymentViewModel", "saleId: $saleId")
            Log.d("MakePaymentViewModel", "purchaseId: $purchaseId")
            Log.d("MakePaymentViewModel", "customerId: $customerId")
            Log.d("MakePaymentViewModel", "amount: $amount")
            Log.d("MakePaymentViewModel", "loadPaymentData: $initialUi")


            _uiState.update {
                it.copy(
                    saleId = saleId,
                    purchaseId = purchaseId,
                    totalAmount = amount,
                    customers = initialUi.customers,
                    vendors = initialUi.vendors,
                    isCustomerEditable = initialUi.isCustomerEditable,
                    selectedCustomer = initialUi.selectedUser,
                    customerError = initialUi.customerError?.userFriendlyMessage,
                    vendorError = initialUi.vendorError?.userFriendlyMessage,
                    retryLoading = false
                )
            }
        }
    }

    fun retryLoadParties() {
        _uiState.update {
            it.copy(retryLoading = true)
        }
        loadPaymentData(currentPaymentType)
    }

    fun onPartyTypeChanged(type: PartyType) {
        _uiState.update {
            it.copy(
                partyType = type,
                selectedCustomer = null,
                selectedCustomerId = null,
                selectedCustomerName = "",
                customerSearchQuery = "",
                vendorSearchQuery = ""
            )
        }
    }

    fun onUserSelected(item: DropDownList?) {
        _uiState.update {
            it.copy(
                selectedCustomer = item,
                )
        }
    }

    fun onCustomerSearchQueryChange(query: String) {
        _uiState.update { it.copy(customerSearchQuery = query) }
    }

    fun onVendorSearchQueryChange(query: String) {
        _uiState.update { it.copy(vendorSearchQuery = query) }
    }

    fun onPaymentAmountChange(amount: String) {
        _uiState.update { it.copy(paymentAmount = amount) }
    }

    fun onPaymentMethodChange(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun onTransactionTypeChange(type: String) {
        _uiState.update { it.copy(transactionType = type) }
    }

    fun onRefNumberChange(number: String) {
        _uiState.update { it.copy(refNumber = number) }
    }

    fun updateCashDenomination(
        denom: (CashDenomination) -> CashDenomination
    ) {
        _uiState.update { state ->
            state.copy(
                cashDenomination = denom(state.cashDenomination)
            )
        }
    }

    fun updateDigitalDenomination(
        denom: (DigitalDenomination) -> DigitalDenomination
    ) {
        _uiState.update { state ->
            state.copy(
                digitalDenomination = denom(state.digitalDenomination)
            )
        }
    }

    fun validatePayment() {
        val cash = uiState.value.cashDenomination
        val digital = uiState.value.digitalDenomination

        val cashSum = (
                (cash.denom2000.toDouble() * 2000.0) +
                        (cash.denom500.toDouble() * 500.0) +
                        (cash.denom200.toDouble() * 200.0) +
                        (cash.denom100.toDouble() * 100.0) +
                        (cash.denom50.toDouble() * 50.0) +
                        (cash.denom20.toDouble() * 20.0) +
                        (cash.denom10.toDouble() * 10.0) +
                        (cash.denom5.toDouble() * 5.0) +
                        (cash.denom2.toDouble() * 2.0) +
                        (cash.denom1.toDouble() * 1.0)
                )

        // Exclude credit; include card, upi, netBanking and cheque
        val paymentSum = cashSum +
                digital.card +
                digital.upi +
                digital.netBanking +
                digital.cheque

        val expected = uiState.value.paymentAmount.toDoubleOrNull() ?: 0.0
        Log.d("MakePaymentViewModel", "expected: $expected")
        Log.d("MakePaymentViewModel", "paid: $paymentSum")


        if (expected != (paymentSum )) {
            _uiState.update {
                it.copy(
                    paymentAmountError = true,
                    error = "Payment amount does not match denominations (excluding credit)"
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                paymentAmountError = false,
                error = null,
                step = PaymentStep.Review   // 👈 IMPORTANT
            )

        }
    }

    fun createPayment() {
        val cash = uiState.value.cashDenomination
        val digital = uiState.value.digitalDenomination

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = paymentUseCase.createPayment(
                MakePaymentRequest(
                    saleId = saleId?.toInt(),
                    purchaseId = purchaseId?.toInt(),
                    customerId = uiState.value.selectedCustomer?.id,
                    amount = uiState.value.paymentAmount.toDouble(),
                    paymentMethod = uiState.value.paymentMethod,
                    refNumber = uiState.value.refNumber.ifBlank { null },
                    transactionType = uiState.value.transactionType,
                    denomination = DenominationInput(
                        denom2000 = cash.denom2000,
                        denom500 = cash.denom500,
                        denom200 = cash.denom200,
                        denom100 = cash.denom100,
                        denom50 = cash.denom50,
                        denom20 = cash.denom20,
                        denom10 = cash.denom10,
                        denom5 = cash.denom5,
                        denom2 = cash.denom2,
                        denom1 = cash.denom1,
                        card = digital.card,
                        upi = digital.upi,
                        netBanking = digital.netBanking,
                        cheque = digital.cheque,
                        credit = digital.credit
                    )
                )
            )
            Log.d("MakePaymentViewModel", "createPayment: $result")
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = PaymentStep.Success
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = (error as AppError).userFriendlyMessage
                    )
                }
            }
        }
    }

    fun goBackToEntry() {
        _uiState.update {
            it.copy(step = PaymentStep.Entry)
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val paymentUseCase = application.container.paymentUseCase
                MakePaymentViewModel(
                    paymentUseCase,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }

}