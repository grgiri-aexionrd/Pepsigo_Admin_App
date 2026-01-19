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
import com.pepsigo.admin.model.CustomerDues
import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.model.VendorDues
import com.pepsigo.admin.model.VendorDuesUi
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OutstandingDuesUiState(
    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val selectedCustomer: DropDownList? = null,
    val selectedVendor: DropDownList? = null,
    val selectedCustomerError: Boolean = false,
    val selectedVendorError: Boolean = false,
    val customerError: String? = null,
    val vendorError: String? = null,
    val customerDues: List<CustomerDuesUi> = emptyList(),
    val vendorDues: List<VendorDuesUi> = emptyList(),
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false,
    val customerDuesFetched: Boolean = false,
    val vendorDuesFetched: Boolean = false
)

class OutstandingDuesViewModel( private val duesUseCase: OutstandingDuesUseCase ): ViewModel() {
    private val _uiState = MutableStateFlow(OutstandingDuesUiState())
    val dues = _uiState.asStateFlow()

    init {
        getCustomersVendors()
    }

    fun getCustomersVendors(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = duesUseCase.getCustomersVendors()
            Log.d("OutstandingDuesViewModel", "getCustomersVendors: $result")

            _uiState.value = _uiState.value.copy(
                customers = result.customers,
                vendors = result.vendors,
                customerError = result.customerError?.userFriendlyMessage,
                vendorError = result.vendorError?.userFriendlyMessage,
                selectedCustomer = null,
                selectedVendor = null,
                isRefreshing = false,
                isLoading = false,
                isError = false,
                snackbarMessage = null,
                customerDuesFetched = false,
                vendorDuesFetched = false
            )
        }
    }

    fun updateSelectedCustomer(customer: DropDownList?){
        Log.d("OutstandingDuesViewModel", "updateSelectedCustomer: $customer")
        _uiState.update {
            it.copy(selectedCustomer = customer)
        }
    }

    fun updateSelectedVendor(vendor: DropDownList?){
        Log.d("OutstandingDuesViewModel", "updateSelectedVendor: $vendor")
        _uiState.update {
            it.copy(selectedVendor = vendor)
        }
    }

    fun getCustomerDues(customerId: Int?) {
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true)
        if (customerId == null) {
            _uiState.value = current.copy(
                selectedCustomerError = true,
                isLoading = false,
                customerDuesFetched = false
            )
            return
        }
        _uiState.value = current.copy(
            selectedCustomerError = false,
        )
        Log.d("OutstandingDuesViewModel", "selectedCustomer: $customerId")

        val id = if (customerId == -1) null else customerId

        viewModelScope.launch {
            val result = duesUseCase.fetchCustomerDues(id)
            Log.d("OutstandingDuesViewModel", "getCustomerDues: $result")
            result.onSuccess { customerDues ->
                _uiState.value = current.copy(
                    customerDues = customerDues,
                    selectedCustomerError = false,
                    isLoading = false,
                    customerDuesFetched = true,
                    snackbarMessage = null
                )
            }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        customerDuesFetched = true
                    )
                }
        }

    }

    fun getVendorDues(vendorId: Int?) {
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true)
        if (vendorId == null) {
            _uiState.value = current.copy(
                selectedVendorError = true,
                isLoading = false,
                vendorDuesFetched = false
            )
            return
        }
        _uiState.value = current.copy(
            selectedVendorError = false,
        )
        Log.d("OutstandingDuesViewModel", "selectedVendor: $vendorId")
        val id = if (vendorId == -1) null else vendorId
        viewModelScope.launch {
            val result = duesUseCase.fetchVendorDues(id)
            Log.d("OutstandingDuesViewModel", "getVendorDues: $result")
            result.onSuccess { vendorDues ->
                _uiState.value = current.copy(
                    vendorDues = vendorDues,
                    selectedVendorError = false,
                    isLoading = false,
                    vendorDuesFetched = true,
                    snackbarMessage = null
                )
            }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        vendorDuesFetched = true
                    )
                }
        }


    }


    fun refresh(){
        Log.d("OutstandingDuesViewModel", "refresh:")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            getCustomersVendors()
        }

    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory{
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val duesUseCase = application.container.duesUseCase
                OutstandingDuesViewModel(duesUseCase)
            }
        }
    }

}