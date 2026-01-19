package com.pepsigo.admin.screens.createPurchaseScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.CreatePurchaseUseCase
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePurchaseUi(
    val vendorDropDown: List<DropDownList> = emptyList(),
    val selectedVendor: DropDownList? = null,
    val selectedInventory: ProductList? = null,
    val addEditItemDetails: List<PurchaseItemsUi> = emptyList(),
    val inventoryItem: List<ProductList> = emptyList(),
    val vendorError: String? = null,
    val inventoryError: String? = null,
    val addItemErrors: AddItemErrors = AddItemErrors(),
    val submitErrors: SubmitError = SubmitError(),
    val isRefreshing: Boolean = false,
    val isEditing: Boolean = false,
    val editIndex: Int? = null,
    val snackbarMessage: String? = null,
    val isError: Boolean = false,
)

data class PurchaseItemsUi(
    val id: Int,
    val productName: String? = null,
    val itemQuantity: String,
    val unit: String,
    val gstPercent: String,
    val costPrice: String,
    val salePrice: String,
    val retailPrice: String,
    val expiryDate: String? = null
)
data class ProductList(
    val prodId: Int,
    val prodName: String,
    val unit: String,
    val gstPercent: String,
)
data class AddItemErrors(
    val productError: Boolean = false,
    val quantityError: Boolean = false,
    val costPriceError: Boolean = false,
    val salePriceError: Boolean = false,
    val retailPriceError: Boolean = false,
    val expiryError: Boolean = false // optional
)
data class SubmitError(
    val vendorError: Boolean = false,
    val purchaseDateError: Boolean = false,
)

class CreatePurchaseViewModel(
    private val createPurchaseUseCase: CreatePurchaseUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CreatePurchaseUi())
    val createPurchase = _uiState.asStateFlow()

    init {
        fetchVendorInventoryDetails()
    }

    fun fetchVendorInventoryDetails(){
        viewModelScope.launch {
            val result = createPurchaseUseCase.getVendorsInventory()
            Log.d("CreatePurchaseViewModel", "fetchVendorInventoryDetails: $result")

            _uiState.update {
                it.copy(
                    vendorDropDown = result.vendors,
                    inventoryItem = result.inventory,
                    vendorError = result.vendorError?.userFriendlyMessage,
                    inventoryError = result.inventoryError?.userFriendlyMessage,
                    isRefreshing = false
                )
            }
        }
    }

    fun clearBottomSheet(){
        _uiState.update {
            it.copy(
                isEditing = false,
                editIndex = null,
                selectedInventory = null,
                addItemErrors = AddItemErrors()
            )
        }
    }

    fun updateSelectedVendor(vendor: DropDownList?){
        _uiState.update {
            it.copy(selectedVendor = vendor)
        }
    }

    fun updateSelectedInventory(inventory: ProductList?){
        _uiState.update {
            it.copy(selectedInventory = inventory)
        }
    }

    fun editPurchaseItem(index: Int) {
        val item = _uiState.value.addEditItemDetails[index]
        _uiState.update {
            it.copy(
                isEditing = true,
                editIndex = index,
                selectedInventory = ProductList(
                    prodId = item.id,
                    prodName = item.productName ?: "",
                    unit = item.unit,
                    gstPercent = item.gstPercent
                )

            )
        }
    }

    fun deletePurchaseItem(index: Int){
        val state = _uiState.value
        if (index !in state.addEditItemDetails.indices) return
        val updatedList = state.addEditItemDetails.toMutableList()
        updatedList.removeAt(index)

        _uiState.update {
            it.copy(
                addEditItemDetails = updatedList

            )
        }

    }

    fun savePurchaseItem(
        selectedInv: ProductList?,
        qty: String,
        cost: String,
        sale: String,
        retail: String,
        expiry: String?
    ): Boolean {
        val errors = AddItemErrors(
            productError = selectedInv == null,
            quantityError = qty.isBlank(),
            costPriceError = cost.isBlank(),
            salePriceError = sale.isBlank(),
            retailPriceError = retail.isBlank()
        )
        Log.d("CreatePurchaseViewModel", "savePurchaseItem: $errors")

        // 2️⃣ Update UI state with error fields
        _uiState.update { state ->
            state.copy(addItemErrors = errors)
        }

        // 3️⃣ If any error exists → STOP
        if (errors.productError || errors.quantityError || errors.costPriceError ||
            errors.salePriceError || errors.retailPriceError
        ) return false

        val newItem = PurchaseItemsUi(
            id = selectedInv!!.prodId ,
            productName = selectedInv.prodName,
            itemQuantity = qty,
            unit = selectedInv.unit,
            gstPercent = selectedInv.gstPercent,
            costPrice = cost,
            salePrice = sale,
            retailPrice = retail,
            expiryDate = expiry
        )
        Log.d("CreatePurchaseViewModel", "savePurchaseItem: $newItem")

        val state = _uiState.value
        if ( !state.isEditing ) {
            _uiState.update { state ->
                state.copy(
                    addEditItemDetails = state.addEditItemDetails + newItem,
                    selectedInventory = null,
                    addItemErrors = AddItemErrors()
                )
            }
        }else{
            val index = state.editIndex!!
            val updatedList = state.addEditItemDetails.toMutableList()
            updatedList[index] = newItem

            _uiState.update { state ->
                state.copy(
                    addEditItemDetails = updatedList,
                    isEditing = false,
                    editIndex = null,
                    selectedInventory = null,
                    addItemErrors = AddItemErrors()
                )
            }
        }
        return true

    }

    fun submitPurchase(
        selectedVendor: DropDownList?,
        invoiceNumber: String ,
        purchaseDate: String?,
        addEditItemDetails: List<PurchaseItemsUi>
    ){
        val error = SubmitError(
            vendorError = selectedVendor == null,
            purchaseDateError = purchaseDate == null
        )
        Log.d("CreatePurchaseViewModel", "submitPurchaseError: $error")
        _uiState.update { state ->
            state.copy(submitErrors = error)
        }

        if (error.vendorError || error.purchaseDateError) return
        Log.d("CreatePurchaseViewModel", "submitPurchaseViewModel: $selectedVendor, $invoiceNumber, $purchaseDate, $addEditItemDetails")

        if (purchaseDate == null) return
        if (selectedVendor?.id == null) return

        viewModelScope.launch{
            val result = createPurchaseUseCase.submitPurchase( selectedVendor.id, invoiceNumber, purchaseDate, addEditItemDetails)
            Log.d("CreatePurchaseViewModel", "submitPurchase: $result")
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        selectedVendor = null,
                        selectedInventory = null,
                        addEditItemDetails = emptyList(),
                        addItemErrors = AddItemErrors(),
                        submitErrors = SubmitError(),
                        isEditing = false,
                        editIndex = null,
                        snackbarMessage = it.message,
                        isError = false
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
            }
        }
    }

    fun refresh(){
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        fetchVendorInventoryDetails()
    }

    fun clearSnackbarMessage() {
        _uiState.update { current ->
                current.copy(snackbarMessage = null)
            }
        }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val createPurchaseUseCase = application.container.createPurchaseUseCase
                CreatePurchaseViewModel(createPurchaseUseCase)
            }
        }
    }


}