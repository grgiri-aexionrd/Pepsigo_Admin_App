package com.pepsigo.admin.screens.payment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.model.UpdatePaymentRequest
import com.pepsigo.admin.repository.PaymentRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaymentFilterState(
    val date: String? = null,
    val transactionType: String? = null,
    val customers: List<DropDownList> = emptyList(),
    val vendors: List<DropDownList> = emptyList(),
    val selectedUser: DropDownList? = null,
    val selectedUserType: String? = null, // "customer" or "vendor"
    val customerId: Int? = null,
    val isLoadingUsers: Boolean = false,
    val userError: String? = null
)

sealed class PaymentUiState {
    data object Loading : PaymentUiState()
    data class Success(val payments: Flow<PagingData<PaymentUiModel>>) : PaymentUiState()
    data class Error(val error: AppError) : PaymentUiState()
}

sealed class PaymentDetailUiState {
    data object Idle : PaymentDetailUiState()
    data object Loading : PaymentDetailUiState()
    data class Success(val payment: PaymentUiModel) : PaymentDetailUiState()
    data class Error(val error: AppError, val payment: PaymentUiModel) : PaymentDetailUiState()
}

sealed class PaymentUpdateState {
    data object Idle : PaymentUpdateState()
    data object Loading : PaymentUpdateState()
    data class Success(val message: String? = null) : PaymentUpdateState()
    data class Error(val error: AppError) : PaymentUpdateState()
}

sealed class PaymentCancelState {
    data object Idle : PaymentCancelState()
    data object Loading : PaymentCancelState()
    data object Success : PaymentCancelState()
    data class Error(val error: AppError) : PaymentCancelState()
}

class PaymentViewModel(
    private val paymentRepo: PaymentRepo,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(PaymentFilterState())
    val filterState: StateFlow<PaymentFilterState> = _filterState.asStateFlow()

    // Payment detail state
    private val _detailState = MutableStateFlow<PaymentDetailUiState>(PaymentDetailUiState.Idle)
    val detailState: StateFlow<PaymentDetailUiState> = _detailState.asStateFlow()

    // Payment update state
    private val _updateState = MutableStateFlow<PaymentUpdateState>(PaymentUpdateState.Idle)
    val updateState: StateFlow<PaymentUpdateState> = _updateState.asStateFlow()

    // Payment cancel state
    private val _cancelState = MutableStateFlow<PaymentCancelState>(PaymentCancelState.Idle)
    val cancelState: StateFlow<PaymentCancelState> = _cancelState.asStateFlow()

    // Store the selected item from the list
    private var selectedPayment: PaymentUiModel? = null

    init {
        loadPayments()

    }

    fun applyFilter(filter: PaymentFilterState) {
        _filterState.value = filter
        loadPayments(
            transactionType = filter.transactionType,
            customerId = filter.customerId,
            date = filter.date
        )
    }

    fun clearFilter() {
        _filterState.value = _filterState.value.copy(
            date = null,
            transactionType = null,
            selectedUser = null,
            selectedUserType = null,
            customerId = null
        )
        loadPayments()
    }

    /**
     * Fetch customers and vendors for filter dropdown
     */
    fun fetchUsersForFilter() {
        viewModelScope.launch {
            _filterState.value = _filterState.value.copy(isLoadingUsers = true, userError = null)
            try {
                val customerResult = userRepository.getUsers("customer")
                val vendorResult = userRepository.getUsers("vendor")

                val customers = customerResult.getOrNull()?.map {
                    DropDownList(it.id, it.name)
                } ?: emptyList()

                val vendors = vendorResult.getOrNull()?.map {
                    DropDownList(it.id, it.name)
                } ?: emptyList()

                val error = when {
                    customerResult.isFailure && vendorResult.isFailure -> "Failed to load users"
                    customerResult.isFailure -> "Failed to load customers"
                    vendorResult.isFailure -> "Failed to load vendors"
                    else -> null
                }

                _filterState.value = _filterState.value.copy(
                    customers = customers,
                    vendors = vendors,
                    isLoadingUsers = false,
                    userError = error
                )
            } catch (e: Exception) {
                _filterState.value = _filterState.value.copy(
                    isLoadingUsers = false,
                    userError = e.message ?: "Unknown error"
                )
            }
        }
    }



    private fun loadPayments(
        transactionType: String? = null,
        customerId: Int? = null,
        date: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading

            val result = wrapError {
                paymentRepo.getPayments(
                    transactionType = transactionType,
                    customerId = customerId,
                    date = date
                )
            }
            Log.d("PaymentViewModel", "loadPaymentsResult: $result")
            result.fold(
                onSuccess = { pagingFlow ->
                    Log.d("PaymentViewModel", "loadPaymentsSuccess: $pagingFlow")
                    _uiState.value = PaymentUiState.Success(
                        payments = pagingFlow.cachedIn(viewModelScope)
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    Log.d("PaymentViewModel", "loadPaymentsAppError: $appError")
                    _uiState.value = PaymentUiState.Error(appError)
                }
            )
        }
    }

    fun retry() {
        val currentFilter = _filterState.value
        loadPayments(
            transactionType = currentFilter.transactionType,
            customerId = currentFilter.customerId,
            date = currentFilter.date
        )
    }

    // Payment Detail Methods

    /**
     * Called when user clicks on a payment item in the list.
     * Stores the item and fetches fresh data from server.
     */
    fun selectPayment(payment: PaymentUiModel) {
        selectedPayment = payment
        _detailState.value = PaymentDetailUiState.Loading
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchPaymentDetail(payment)
        } else {
            // Fallback for older API - just use the item from list
            _detailState.value = PaymentDetailUiState.Success(payment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPaymentDetail(item: PaymentUiModel) {
        viewModelScope.launch {
            val result = paymentRepo.getPaymentById(item.id)
            Log.d("PaymentViewModel", "fetchPaymentDetail: $result")
            result.fold(
                onSuccess = { freshPayment ->
                    // Compare fresh data with the item from list
                    if (freshPayment != item) {
                        // Data changed, use fresh data from server
                        _detailState.value = PaymentDetailUiState.Success(freshPayment)
                    } else {
                        // Data is same, use the item from list
                        _detailState.value = PaymentDetailUiState.Success(item)
                    }
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    // On error, show the item from list with error message
                    _detailState.value = PaymentDetailUiState.Error(appError, item)
                }
            )
        }
    }


    /**
     * Retry fetching payment detail
     */
    fun retryPaymentDetail() {
        selectedPayment?.let { payment ->
            _detailState.value = PaymentDetailUiState.Loading
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fetchPaymentDetail(payment)
            } else {
                _detailState.value = PaymentDetailUiState.Success(payment)
            }
        }
    }

    /**
     * Clear detail state when navigating back from detail screen
     */
    fun clearDetailState() {
        _detailState.value = PaymentDetailUiState.Idle
        selectedPayment = null
    }

    /**
     * Update payment with the given request
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePayment(id: Int, request: UpdatePaymentRequest) {
        viewModelScope.launch {
            _updateState.value = PaymentUpdateState.Loading
            val result = paymentRepo.updatePayment(id, request)
            Log.d("PaymentViewModel", "updatePayment: $result")
            result.fold(
                onSuccess = { updatedPayment ->
                    _updateState.value = PaymentUpdateState.Success(updatedPayment.message)
                    val updated = updatedPayment.data ?: return@fold
                    val current = selectedPayment ?: return@fold

                    // Also update the detail state with the new data
                    val newPayment = current.copy(
                        amount = updated.amount,
                        paymentMethod = updated.paymentMethod,
                        denomination = updated.denomination,
                        refNumber = updated.refNumber
                    )
                    selectedPayment = newPayment

                    _detailState.value = PaymentDetailUiState.Success(newPayment)

                    // Refresh the list
                    loadPayments(
                        transactionType = _filterState.value.transactionType,
                        customerId = _filterState.value.customerId,
                        date = _filterState.value.date
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    _updateState.value = PaymentUpdateState.Error(appError)
                }
            )
        }
    }

    /**
     * Cancel payment
     */
    fun cancelPayment(id: Int) {
        viewModelScope.launch {
            _cancelState.value = PaymentCancelState.Loading
            val result = paymentRepo.cancelPayment(id)
            result.fold(
                onSuccess = {
                    _cancelState.value = PaymentCancelState.Success
                    // Refresh the list
                    loadPayments(
                        transactionType = _filterState.value.transactionType,
                        customerId = _filterState.value.customerId,
                        date = _filterState.value.date
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    _cancelState.value = PaymentCancelState.Error(appError)
                }
            )
        }
    }

    /**
     * Clear update state
     */
    fun clearUpdateState() {
        _updateState.value = PaymentUpdateState.Idle
    }

    /**
     * Clear cancel state
     */
    fun clearCancelState() {
        _cancelState.value = PaymentCancelState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val paymentRepo = application.container.paymentRepo
                val userRepository = application.container.userRepository
                PaymentViewModel(paymentRepo, userRepository)
            }
        }
    }
}
