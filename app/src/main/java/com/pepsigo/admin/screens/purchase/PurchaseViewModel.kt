package com.pepsigo.admin.screens.purchase

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.AmountSummaryUi
import com.pepsigo.admin.model.ItemsDetailUi
import com.pepsigo.admin.model.PurchaseDetailUi
import com.pepsigo.admin.model.PurchaseUi
import com.pepsigo.admin.model.PurchaseUiModel
import com.pepsigo.admin.repository.PurchaseRepo
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PurchaseUiState {

    object Loading : PurchaseUiState()

    data class Success(
        val purchases: Flow<PagingData<PurchaseUiModel>>
    ) : PurchaseUiState()

    data class PurchaseDetails(
        val purchase: PurchaseDetailUi,
        val snackbarMessage: String? = null,
        val isError: Boolean = false,
        val isLoading: Boolean = false,
        val isReturn: Boolean = false,
        val isReturnSummary: Boolean = false,
        val returnItemList: List<ReturnItemList> = emptyList()
    ): PurchaseUiState()


    data class Error(
        val error: AppError,
        val canRetry: Boolean = true
    ) : PurchaseUiState()
}
data class ReturnItemList(
    val invId: Int,
    val name: String,
    val quantity: String,
)

class PurchaseViewModel( private val purchase: PurchaseRepo): ViewModel() {
    private val _uiState = MutableStateFlow<PurchaseUiState>(PurchaseUiState.Loading)
    val uiState: StateFlow<PurchaseUiState> = _uiState

    private val _selectedItems = mutableStateMapOf<Int, ReturnItemList>()
    val selectedItems: Map<Int, ReturnItemList> get() = _selectedItems

    init {
        loadPurchases()
    }

    fun loadPurchases() {
        viewModelScope.launch {
            _uiState.value = PurchaseUiState.Loading

            val result = wrapError {
                purchase.getPurchases()  // Just returns Flow, no network call yet until collected
            }
            result.fold(
                onSuccess = { pagingFlow ->
                    _uiState.value = PurchaseUiState.Success(
                        purchases = pagingFlow.cachedIn(viewModelScope)
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    _uiState.value = PurchaseUiState.Error(appError)
                }
            )
        }

    }

    fun retry() {
        loadPurchases()
    }

    fun getPurchaseDetails(purchaseItem: PurchaseUiModel) {
        viewModelScope.launch {
            _uiState.value = PurchaseUiState.Loading
            val result = purchase.getPurchaseById(purchaseItem.id)

            result.onSuccess { item ->
                Log.d("PurchaseViewModel", "getPurchaseDetails: $item")
                _uiState.value = PurchaseUiState.PurchaseDetails(item)
            }.onFailure { error ->
                Log.d("PurchaseViewModel", "getPurchaseDetails: $error")
                _uiState.value = PurchaseUiState.PurchaseDetails(
                    purchase = PurchaseDetailUi(
                        purchase = PurchaseUi(
                            purchaseId = purchaseItem.id,
                            invoiceNumber = purchaseItem.invoiceNumber,
                            purchaseDate = purchaseItem.purchaseDate,
                            invoiceStatus = purchaseItem.invoiceStatus
                        ),
                        vendor = purchaseItem.vendor,
                        amountSummary = AmountSummaryUi(
                            subTotal = purchaseItem.subTotal,
                            discountBt = purchaseItem.discountBt,
                            discountAt = purchaseItem.discountAt,
                            taxAmount = purchaseItem.taxAmount,
                            totalAmount = purchaseItem.totalAmount
                        ),
                        purchasedItems = emptyList(),
                        hasSales = false
                    ),
                    snackbarMessage = (error as AppError).userFriendlyMessage,
                    isError = true,
                    isLoading = false,
                    isReturn = false
                )

            }

        }
    }

    fun cancelPurchase(id: Int) {
        viewModelScope.launch {
            // Start loading
            _uiState.update { current ->
                if (current is PurchaseUiState.PurchaseDetails) {
                    current.copy(isLoading = true)
                } else current
            }
            val result = purchase.cancelPurchase(id)
            Log.d("PurchaseViewModel", "cancelPurchase: $result")
            result.onSuccess { message ->
                _uiState.update { current ->
                    if (current is PurchaseUiState.PurchaseDetails) {
                        current.copy(
                            purchase = current.purchase.copy(
                                purchase = current.purchase.purchase.copy(
                                    invoiceStatus = "Cancelled"
                                )
                            ),
                            snackbarMessage = message.message,
                            isLoading = false,
                            isError = false
                        )
                    } else current
                }
            }
            result.onFailure { error ->
                _uiState.update { current ->
                    if (current is PurchaseUiState.PurchaseDetails) {
                        current.copy(
                            snackbarMessage = (error as AppError).userFriendlyMessage,
                            isLoading = false,
                            isError = true
                        )
                    } else current
                }
            }

        }

    }

    fun showReturnScreen(returnItem: PurchaseDetailUi) {
        _uiState.update { current ->
            if (current is PurchaseUiState.PurchaseDetails) {
                current.copy(
                    purchase = returnItem,
                    isReturn = true,
                    isReturnSummary = false
                )
            } else current
        }
    }

    fun toggleItemSelection(item: ItemsDetailUi, checked: Boolean) {
        if (checked) {
            _selectedItems[item.inventory.invId] = ReturnItemList(
                invId = item.inventory.invId,
                name = item.inventory.name,
                quantity = "1"
            )
        } else {
            _selectedItems.remove(item.inventory.invId)
        }
    }

    fun updateQuantity(item: ItemsDetailUi, qty: Int) {
        val existing = _selectedItems[item.inventory.invId]
        if (existing != null) {
            _selectedItems[item.inventory.invId] = existing.copy(
                quantity = qty.toString()
            )
        }
    }

    fun showReturnSummary() {
        // 1️⃣ If no items selected → return (or show message)
        if (_selectedItems.isEmpty()) {
            // Option A: show snackbar
            _uiState.update { state ->
                (state as PurchaseUiState.PurchaseDetails).copy(
                    snackbarMessage = "Please select at least one item to return",
                    isError = true
                )
            }
            return
        }
        _uiState.update { state ->
            Log.d("PurchaseViewModel", "showReturnSummary: ${_selectedItems.values.toList()}")
            (state as PurchaseUiState.PurchaseDetails).copy(
                isReturn = false,
                isReturnSummary = true,
                returnItemList = _selectedItems.values.toList()
            )
        }
    }


    fun returnPurchase(returnItem: List<ReturnItemList>, purchaseId: Int) {
        _uiState.update { current ->
            if (current is PurchaseUiState.PurchaseDetails) {
                current.copy(isLoading = true)
            } else current
        }

        viewModelScope.launch {
            val result = purchase.returnPurchase(returnItem, purchaseId)
            Log.d("PurchaseViewModel", "returnPurchaseResult: $result")
            result.onSuccess { message ->
                _selectedItems.clear()
                _uiState.update {
                    (it as PurchaseUiState.PurchaseDetails).copy(
                        isReturn = false,
                        isReturnSummary = false,
                        returnItemList = emptyList(),
                        snackbarMessage = message.message,
                        isLoading = false,
                        isError = false
                    )
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    (it as PurchaseUiState.PurchaseDetails).copy(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isLoading = false,
                        isError = true
                    )
                }
            }
        }
    }




    fun exitReturnMode() {
        _selectedItems.clear()
        _uiState.update {
            (it as PurchaseUiState.PurchaseDetails).copy(
                isReturn = false,
                isReturnSummary = false,
                returnItemList = emptyList()
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { current ->
            if (current is PurchaseUiState.PurchaseDetails) {
                current.copy(snackbarMessage = null)
            } else current
        }
    }



    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val purchase = application.container.purchaseRepo
                PurchaseViewModel(purchase)
            }
        }
    }


}