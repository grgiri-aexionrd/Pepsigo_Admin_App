package com.pepsigo.admin.screens.sales

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.AmountSummaryUi
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesItemsDetailUi
import com.pepsigo.admin.model.SalesUi
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.repository.SalesRepo
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SalesViewModel(private val salesRepo: SalesRepo) : ViewModel() {

    // List state - persists even when viewing details
    private val _listState = MutableStateFlow<SalesUiState>(SalesUiState.Loading)
    val listState: StateFlow<SalesUiState> = _listState.asStateFlow()

    // Details state - separate from list
    private val _detailsState = MutableStateFlow(SalesDetailsState())
    val detailsState: StateFlow<SalesDetailsState> = _detailsState.asStateFlow()

    // Current screen mode
    private val _screenMode = MutableStateFlow(SalesScreenMode.LIST)
    val screenMode: StateFlow<SalesScreenMode> = _screenMode.asStateFlow()

    // Selected items for return
    private val _selectedItems = mutableStateMapOf<Int, SalesReturnItemList>()
    val selectedItems: Map<Int, SalesReturnItemList> get() = _selectedItems

    init {
        loadSales()
    }

    fun loadSales() {
        viewModelScope.launch {
            _listState.value = SalesUiState.Loading

            val result = wrapError {
                salesRepo.getSales()
            }
            result.fold(
                onSuccess = { pagingFlow ->
                    _listState.value = SalesUiState.Success(
                        salesList = pagingFlow.cachedIn(viewModelScope)
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    _listState.value = SalesUiState.Error(appError)
                }
            )
        }
    }

    fun getSaleDetails(saleItem: SalesUiModel) {
        viewModelScope.launch {
            // Set loading state for details
            _detailsState.update { it.copy(isLoading = true) }
            _screenMode.value = SalesScreenMode.DETAILS

            val result = salesRepo.getSalesById(saleItem.id)

            result.onSuccess { item ->
                _detailsState.value = SalesDetailsState(
                    sale = item,
                    deliveryExec = saleItem.deliveryBoy,
                    isReturn = false,
                    isReturnSummary = false,
                    isLoading = false,
                    snackbarMessage = null,
                    isError = false,
                    isPaymentMade = false
                )
            }.onFailure { error ->
                _detailsState.value = SalesDetailsState(
                    sale = SalesDetailUi(
                        sales = SalesUi(
                            salesId = saleItem.id,
                            invoiceNumber = saleItem.invoiceNumber,
                            saleDate = saleItem.saleDate,
                            invoiceStatus = saleItem.invoiceStatus
                        ),
                        customer = saleItem.customer,
                        amountSummary = AmountSummaryUi(
                            subTotal = saleItem.subTotal,
                            discountBt = saleItem.discountBt,
                            discountAt = saleItem.discountAt,
                            taxAmount = saleItem.taxAmount,
                            totalAmount = saleItem.totalAmount
                        ),
                        salesItems = emptyList(),
                    ),
                    deliveryExec = saleItem.deliveryBoy,
                    snackbarMessage = (error as AppError).userFriendlyMessage,
                    isError = true,
                    isLoading = false,
                    isReturn = false,
                    isPaymentMade = false
                )
            }
        }
    }

    fun goBackToList() {
        _screenMode.value = SalesScreenMode.LIST
        // Clear details state
        _detailsState.value = SalesDetailsState()
        _selectedItems.clear()
    }

    fun exitReturnMode() {
        _selectedItems.clear()
        _detailsState.update {
            it.copy(
                isReturn = false,
                isReturnSummary = false,
                isReturnSuccess = false,
                returnMessage = null,
                returnResponse = null,
                returnItemsTotalAmount = 0.0,
                returnItemList = emptyList()
            )
        }
        _screenMode.value = SalesScreenMode.DETAILS
    }

    fun exitReturnSuccess() {
        _selectedItems.clear()
        _detailsState.update {
            it.copy(
                isReturn = false,
                isReturnSummary = false,
                isReturnSuccess = false,
                returnMessage = null,
                returnResponse = null,
                returnItemsTotalAmount = 0.0,
                returnItemList = emptyList(),

            )
        }
        _screenMode.value = SalesScreenMode.LIST
    }

    fun showReturnScreen(sale: SalesDetailUi) {
        _detailsState.update {
            it.copy(
                sale = sale,
                isReturn = true,
                isReturnSummary = false
            )
        }
        _screenMode.value = SalesScreenMode.RETURN
    }

    fun toggleItemSelection(item: SalesItemsDetailUi, checked: Boolean) {
        if (checked) {
            _selectedItems[item.inventory.invId] = SalesReturnItemList(
                invId = item.inventory.invId,
                name = item.inventory.name,
                quantity = "1"
            )
        } else {
            _selectedItems.remove(item.inventory.invId)
        }
    }

    fun updateQuantity(item: SalesItemsDetailUi, qty: Int) {
        val existing = _selectedItems[item.inventory.invId]
        if (existing != null) {
            _selectedItems[item.inventory.invId] = existing.copy(
                quantity = qty.toString()
            )
        }
    }

    fun showReturnSummary() {
        // If no items selected → show message
        if (_selectedItems.isEmpty()) {
            _detailsState.update {
                it.copy(
                    snackbarMessage = "Please select at least one item to return",
                    isError = true
                )
            }
            return
        }
        _detailsState.update {
            it.copy(
                isReturn = false,
                isReturnSummary = true,
                returnItemList = _selectedItems.values.toList()
            )
        }
        _screenMode.value = SalesScreenMode.RETURN_SUMMARY
    }

    fun returnSale(returnItemList: List<SalesReturnItemList>, saleId: Int) {
        _detailsState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = salesRepo.returnSale(returnItemList, saleId)
            result.onSuccess { response ->
                val itemsTotal = response.data?.items?.sumOf { it.itemTotalAmount } ?: 0.0
                _selectedItems.clear()
                _detailsState.update {
                    it.copy(
                        isReturn = false,
                        isReturnSummary = false,
                        isReturnSuccess = true,
                        returnMessage = response.message,
                        returnResponse = response.data,
                        returnItemsTotalAmount = itemsTotal,
                        returnItemList = emptyList(),
                        snackbarMessage = response.message ?: "Return successful",
                        isLoading = false,
                        isError = false
                    )
                }
                _screenMode.value = SalesScreenMode.RETURN_SUCCESS
            }.onFailure { error ->
                _detailsState.update {
                    it.copy(
                        snackbarMessage = (error as? AppError)?.userFriendlyMessage ?: error.message ?: "Unknown error",
                        isLoading = false,
                        isError = true
                    )
                }
            }
        }
    }

    fun cancelSale(id: Int) {
        viewModelScope.launch {
            _detailsState.update { it.copy(isLoading = true) }

            val result = salesRepo.cancelSale(id)
            result.onSuccess { message ->
                _detailsState.update {
                    it.copy(
                        sale = it.sale?.copy(
                            sales = it.sale.sales.copy(
                                invoiceStatus = "Cancelled"
                            )
                        ),
                        snackbarMessage = message.message,
                        isLoading = false,
                        isError = false
                    )
                }
            }
            result.onFailure { error ->
                _detailsState.update {
                    it.copy(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isLoading = false,
                        isError = true
                    )
                }
            }

        }
    }

    fun clearSnackbarMessage() {
        _detailsState.update { it.copy(snackbarMessage = null) }
    }

    fun markPaymentMade() {
        _detailsState.update { it.copy(isPaymentMade = true) }
    }

    fun retry() {
        loadSales()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val sales = application.container.salesRepo
                SalesViewModel(sales)
            }
        }
    }
}