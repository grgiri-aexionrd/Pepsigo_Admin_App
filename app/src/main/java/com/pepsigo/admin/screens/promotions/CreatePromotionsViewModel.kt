package com.pepsigo.admin.screens.promotions


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.CreatePromotionsUseCase
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePromotionsUiState(
    val customerDropDown: List<DropDownList> = emptyList(),
    val inventoryDropDown: List<InventoryListUi> = emptyList(),
    val customerDropDownError: String? = null,
    val inventoryDropDownError: String? = null,
    val customerSearchQuery: String = "",
    val inventorySearchQuery: String = "",
    val selectedInventory: InventoryListUi? = null,
    val selectedCustomer: DropDownList? = null,
    val selectedCustomerError: Boolean = false,
    val selectedInventoryError: Boolean = false,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val isError: Boolean = false,
    val isRefreshing: Boolean = false
)

data class PromotionDetail(
    val quantity: String = "0",
    val quantityError: Boolean = false,
    val salePrice: String ="0.00",
    val salePriceError: Boolean = false,
    val isFreeProduct: Boolean = false,
    val isLoading: Boolean = false,
)

class CreatePromotionsViewModel(private val useCase: CreatePromotionsUseCase): ViewModel(){
    private val _uiState = MutableStateFlow(CreatePromotionsUiState())
    val uiState = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(PromotionDetail())
    val detailState = _detailState.asStateFlow()

    init {
        getCustomersInventory()
    }

    fun getCustomersInventory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = useCase.getCustomersInventory()
            _uiState.value = _uiState.value.copy(
                customerDropDown = result.customer,
                inventoryDropDown = result.inventory,
                customerDropDownError = result.customerError?.userFriendlyMessage,
                inventoryDropDownError = result.inventoryError?.userFriendlyMessage,
                isLoading = false,
                isError = false,
                isRefreshing = false
            )

        }
    }

    fun updateCustomerSearch(query: String) {
        _uiState.update { it.copy(customerSearchQuery = query) }
    }

    fun updateInventorySearch(query: String) {
        _uiState.update { it.copy(inventorySearchQuery = query) }
    }


    fun updateSelectedCustomer(customer: DropDownList?){
        _uiState.update {
            it.copy(selectedCustomer = customer)        }
    }

    fun updateSelectedInventory(inventory: InventoryListUi?){
        _uiState.update {
            it.copy(selectedInventory = inventory)        }
    }

    fun onCheckedChange(isChecked: Boolean){
        _detailState.update {
            it.copy(isFreeProduct = isChecked,
                // If FREE product
                salePrice = if (isChecked) "0.00" else it.salePrice,
                salePriceError = if (isChecked) false else it.salePriceError,

                // If NOT free product
                quantity = if (!isChecked) "0" else it.quantity,
                quantityError = if (!isChecked) false else it.quantityError
            )
        }

    }

    fun onQuantityChange(quantity: String) {
        _detailState.update {
            it.copy(quantity = quantity)
        }
    }

    fun onSalePriceChange(salePrice: String) {
        _detailState.update {
            it.copy(salePrice = salePrice)
        }
    }

    fun addOffer(){

        _detailState.update {
            it.copy(isLoading = true)
        }
        val current = _uiState.value
        val detailCurrent = _detailState.value
        if(current.selectedCustomer == null || current.selectedInventory == null ){
            _uiState.update {
                it.copy(selectedCustomerError = current.selectedCustomer == null,
                    selectedInventoryError = current.selectedInventory == null)
            }
            _detailState.update {
                it.copy(isLoading = false)
            }
            return
        }

        if(detailCurrent.isFreeProduct && detailCurrent.quantity.toInt() <= 0 ){
            _detailState.update {
                it.copy(
                    // minimum quantity needed is 1 check with backend.
                    quantityError = true,
                    isLoading = false)
            }
            Log.d("CreatePromotionsViewModel", "quantity error")
            return
        }

        if (!detailCurrent.isFreeProduct && detailCurrent.salePrice.toDouble() <= 0 ) {
            _detailState.update {
                it.copy(
                    salePriceError = true,
                    isLoading = false
                )
            }
            Log.d("CreatePromotionsViewModel", "sale price error")
            return
        }

        viewModelScope.launch {
            Log.d("CreatePromotionsViewModel", "viewmodel reached")
            val result = useCase.addOffer(
                customerId = current.selectedCustomer.id!!,
                inventoryId = current.selectedInventory.id,
                quantity = detailCurrent.quantity.toInt(),
                autoAdd = detailCurrent.isFreeProduct,
                salePrice = detailCurrent.salePrice.toDouble()
            )
            result.onSuccess {
                _detailState.update {
                    it.copy(isLoading = false)
                }
                _uiState.update {
                    it.copy(snackbarMessage = "Offer added successfully",
                        isError = false)
                }
            }
            result.onFailure { error ->
                _detailState.update {
                    it.copy(isLoading = false)
                }
                _uiState.update {
                    it.copy(snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true)
                }

            }

        }



    }



    fun clearSnackbarMessage() {
        _uiState.update { current ->
            current.copy(snackbarMessage = null)
        }
    }

    fun refresh(){
        getCustomersInventory()
    }



    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val useCase = application.container.createPromotionsUseCase
                CreatePromotionsViewModel(useCase)
            }
        }
    }
}

