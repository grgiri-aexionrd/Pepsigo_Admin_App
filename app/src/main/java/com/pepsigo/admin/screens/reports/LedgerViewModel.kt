package com.pepsigo.admin.screens.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.OutstandingDuesUseCase
import com.pepsigo.admin.repository.LedgerRepo
import com.pepsigo.admin.repository.TransactionDetailUi
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DateField {
    FROM, TO
}

data class LedgerUiState(
    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val selectedCustomer: DropDownList? = null,
    val selectedVendor: DropDownList? = null,
    val selectedCustomerError: Boolean = false,
    val selectedVendorError: Boolean = false,
    val customerError: String? = null,
    val vendorError: String? = null,
    val customerFromDate: String = "From",
    val customerToDate: String = "To",
    val vendorFromDate: String = "From",
    val vendorToDate: String = "To",
    val customerLedger: List<TransactionDetailUi> = emptyList(),
    val customerBalance: String = "₹ 0.00",
    val vendorLedger: List<TransactionDetailUi> = emptyList(),
    val vendorBalance: String = "₹ 0.00",
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false,
    val hasCustomerSearched: Boolean = false,
    val hasVendorSearched: Boolean = false

    )

class LedgerViewModel(private val duesUseCase: OutstandingDuesUseCase,
                      private val ledgerRepo: LedgerRepo): ViewModel() {
    private val _uiState = MutableStateFlow(LedgerUiState())
    val ledger = _uiState.asStateFlow()

    init {
        getCustomersVendors()
    }

    fun getCustomersVendors(){
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = duesUseCase.getCustomersVendors()
            Log.d("ledgerViewModel", "getCustomersVendors: $result")

            _uiState.value = _uiState.value.copy(
                customers = result.customers,
                vendors = result.vendors,
                customerError = result.customerError?.userFriendlyMessage,
                vendorError = result.vendorError?.userFriendlyMessage,
                isRefreshing = false,
                isLoading = false
            )

        }

    }

    fun setCustomerFromDate(date: String) {
        _uiState.value = _uiState.value.copy(
            customerFromDate = date
        )
    }

    fun setCustomerToDate(date: String) {
        _uiState.value = _uiState.value.copy(
            customerToDate = date
        )
    }
    fun setVendorFromDate(date: String) {
        _uiState.value = _uiState.value.copy(
            vendorFromDate = date
        )
    }

    fun setVendorToDate(date: String) {
        _uiState.value = _uiState.value.copy(
            vendorToDate = date
        )
    }

    fun updateSelectedCustomer(customer: DropDownList?){
        Log.d("ledgerViewModel", "updateSelectedCustomer: $customer")
        _uiState.update {
            it.copy(selectedCustomer = customer)
        }
    }

    fun updateSelectedVendor(vendor: DropDownList?){
        Log.d("ledgerViewModel", "updateSelectedVendor: $vendor")
        _uiState.update {
            it.copy(selectedVendor = vendor)
        }
    }

    fun getCustomerLedger(dateFrom: String, dateTo: String, customerId: Int?) {
        val current = _uiState.value
        if ( customerId == null ) {
            _uiState.value = current.copy(
                selectedCustomerError = true,
            )
            return
        }

        val fromDate = if (dateFrom == "From") null else dateFrom
        val toDate = if (dateTo == "To") null else dateTo

        _uiState.value = current.copy(
            isLoading = true,
            selectedCustomerError = false,
        )
        Log.d("ledgerViewModel", "getCustomerLedger: $customerId, $fromDate, $toDate")

        viewModelScope.launch {
            val result = ledgerRepo.getCustomersLedger( customerId, startDate = fromDate, endDate = toDate)
            Log.d("ledgerViewModel", "getCustomerLedger: $result")
            result.onSuccess { customerLedger ->
                _uiState.value = current.copy(
                    customerLedger = customerLedger.entries,
                    customerBalance = customerLedger.balance,
                    selectedCustomerError = false,
                    isLoading = false,
                    hasCustomerSearched = true
                )
            }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isLoading = false,
                        hasCustomerSearched = true
                    )
                }
        }

    }

    fun getVendorLedger(dateFrom: String, dateTo: String, vendorId: Int?) {
        val current = _uiState.value
        if ( vendorId == null ) {
            _uiState.value = current.copy(
                selectedVendorError = true,
            )
            return
        }

        val fromDate = if (dateFrom == "From") null else dateFrom
        val toDate = if (dateTo == "To") null else dateTo

        _uiState.value = current.copy(
            isLoading = true,
            selectedVendorError = false,
        )

        Log.d("ledgerViewModel", "getVendorLedger: $vendorId, $fromDate, $toDate")

        viewModelScope.launch {
            val result = ledgerRepo.getVendorsLedger( vendorId, startDate = fromDate, endDate = toDate)
            Log.d("ledgerViewModel", "getVendorLedger: $result")
            result.onSuccess { vendorLedger ->
                _uiState.value = current.copy(
                    vendorLedger = vendorLedger.entries,
                    vendorBalance = vendorLedger.balance,
                    selectedVendorError = false,
                    isLoading = false,
                    hasVendorSearched = true
                )
            }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isLoading = false,
                        hasVendorSearched = true
                    )
                }
        }

        }

    fun refresh(){
        Log.d("LedgerViewModel", "refresh")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            getCustomersVendors()
        }

    }

    fun clearSnackbarMessage() {
        _uiState.update { current ->
            current.copy(snackbarMessage = null)
        }
    }



    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory{
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val duesUseCase = application.container.duesUseCase
                val ledgerRepo = application.container.ledgerRepo
                LedgerViewModel(duesUseCase,ledgerRepo)
            }
        }
    }

}