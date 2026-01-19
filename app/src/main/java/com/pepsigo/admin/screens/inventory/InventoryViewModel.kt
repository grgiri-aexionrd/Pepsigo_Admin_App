package com.pepsigo.admin.screens.inventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.AddInventoryRequest
import com.pepsigo.admin.model.InventoryItemDetailUi
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.StockSummaryUi
import com.pepsigo.admin.repository.InventoryRepo
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class InventoryUiState{
    object Loading: InventoryUiState()

    data class InventoryList(
        val items: List<InventoryListUi>,
        val message: String? = null,
        val snackbarMessage: String? = null,
        val isError: Boolean = false
    ): InventoryUiState()

    data class AddEditInventory(
        val form: InventoryForm ,
        val isEdit: Boolean,
        val formErrors: Map<String, String> = emptyMap(),
        val snackbarMessage: String? = null,
        val isError: Boolean = false
//        val isLoading: Boolean = false
    ): InventoryUiState()

    data class InventoryDetails(
        val itemDetails: InventoryItemDetailUi?,
        val snackbarMessage: String? = null,
        val isError: Boolean = false
    ): InventoryUiState()
}

data class InventoryForm(
    val id: Int? = null,
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val gst: String = ""
)

class InventoryViewModel( private val inventoryRepo: InventoryRepo) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var lastListState: InventoryUiState.InventoryList? = null

    init {
        getInventories()
    }

    fun refresh() {
        getInventories()
    }


    fun getInventories(message: String? = null, isError: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            val result = inventoryRepo.fetchInventoryItems()
            result
                .onSuccess { items ->
                    val state = InventoryUiState.InventoryList(
                        items = items,
                        message = null,
                        snackbarMessage = message,
                        isError = isError
                    )
                    lastListState = state
                    _uiState.value = state
                }
                .onFailure { error ->
                    _uiState.value = InventoryUiState.InventoryList(
                        items = emptyList(),
                        message = (error as AppError).userFriendlyMessage,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }
    }

    fun getInventoryById(item: InventoryListUi) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current !is InventoryUiState.InventoryDetails) {
                _uiState.value = InventoryUiState.Loading
            }
            val result = inventoryRepo.fetchInventoryDetails(item.id)
            result
                .onSuccess { item ->
                    Log.d("InventoryViewModel", "getInventoryById: $item")
                    _uiState.value = InventoryUiState.InventoryDetails(
                        itemDetails = item.data
                    )
                }
                .onFailure { error ->
                    Log.d("InventoryViewModel", "getInventoryById: $error")
                    _uiState.value = InventoryUiState.InventoryDetails(
                        itemDetails =InventoryItemDetailUi(
                            itemDetail = item,
                            offer = "",
                            offerDetail = null,
                            stockSummary = StockSummaryUi(
                                totalAvailable = 0,
                                batchesCount = 0,
                                nearestExpiry = ""
                            ),
                            batches = emptyList()
                        ),
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }
    }

    fun addInventory() {
        // Implementation for adding inventory
        _uiState.value = InventoryUiState.AddEditInventory(
            form = InventoryForm(),
            isEdit = false,
            formErrors = emptyMap()
        )
    }

    fun onNameChange(newName: String) { updateForm { copy(name = newName) } }
    fun onQuantityChange(newQuantity: String) { updateForm { copy(quantity = newQuantity) } }
    fun onGstChange(newGst: String) { updateForm { copy(gst = newGst) } }

    fun onUnitChange(newUnit: String) {
        val current = _uiState.value
        if (current is InventoryUiState.AddEditInventory) {
            val updatedForm = current.form.copy(unit = newUnit)
            _uiState.update {
                current.copy(form = updatedForm)
            }
        }
    }

    fun onSaveInventory(form: InventoryForm) {
        Log.d("InventoryViewModel", "Saving inventory: $form")
        val errors = mutableMapOf<String, String>()

        if (form.name.isBlank()) { errors["name"] = "Item Name is required"  }
        if (form.id == null){
            if (form.quantity.isBlank()) {
                errors["quantity"] = "Quantity is required"
            } else {
                val qty = form.quantity.toIntOrNull()
                when {
                    qty == null -> errors["quantity"] = "Quantity must be a number"
                    qty < 0 -> errors["quantity"] = "Quantity cannot be less than 0"
                }
            }
            if (form.unit.isBlank()) { errors["unit"] = "Unit is required" }
        }
        if (form.gst.isBlank()) { errors["gst"] = "GST is required" }

        if (errors.isNotEmpty()) {
            _uiState.update {
                val current = it as InventoryUiState.AddEditInventory
                current.copy(formErrors = errors)
            }
            return
        }
        viewModelScope.launch {
            val result = if (form.id == null) {
                inventoryRepo.addInventoryItem(
                    AddInventoryRequest(
                        itemName = form.name,
                        quantity = form.quantity.toInt(),
                        unit = form.unit,
                        gstPercent = form.gst.toDouble()
                    )
                )
            } else {
                inventoryRepo.editInventoryItem(
                    InventoryListUi(
                        id = form.id,
                        name = form.name,
                        unit = form.unit,
                        gstPercent = form.gst.toDouble(),
                        enabled = true
                    )
                )

            }
            result
                .onSuccess { successResponse ->
                    if (form.id != null) {
                        getInventories(
                            message = successResponse.message,
                            isError = false
                        )
                    } else {
                        _uiState.value = InventoryUiState.AddEditInventory(
                            form = InventoryForm(),
                            isEdit = false,
                            snackbarMessage = successResponse.message,
                            isError = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        val current = it as InventoryUiState.AddEditInventory
                        current.copy(
                            snackbarMessage = ( error as AppError).userFriendlyMessage,
                            isError = true
                        )
                    }
                }
        }
    }

    fun updateInventory(form: InventoryListUi) {
        _uiState.value = InventoryUiState.AddEditInventory(
            form = InventoryForm(
                id = form.id,
                name = form.name,
                unit = form.unit,
                gst = form.gstPercent.toString()
            ),
            isEdit = true,
            formErrors = emptyMap()
        )
    }

    fun toggleInventoryStatus(id: Int) {
        val current = _uiState.value
        if (current !is InventoryUiState.InventoryList) return
        val list = current.items.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        val previousItem = list[index]
        val toggled = previousItem.copy(enabled = !previousItem.enabled, isToggling = true)
        // 1️⃣ Optimistic update
        list[index] = toggled
        _uiState.value = current.copy(items = list)

        viewModelScope.launch {
            Log.d("InventoryViewModel", "Toggling inventory status for id: $id")
            val result = inventoryRepo.toggleInventoryItemStatus(id)
            result
                .onSuccess { successResponse ->
                    Log.d("InventoryViewModel", "toggleInventoryStatus: $successResponse")
                    val updatedList = _uiState.value.let{
                        (it as? InventoryUiState.InventoryList)?.items?.toMutableList()
                    } ?: return@onSuccess
                    val updatedIndex = updatedList.indexOfFirst { it.id == id }
                    updatedList[updatedIndex] = toggled.copy(isToggling = false)
                    _uiState.value = InventoryUiState.InventoryList(
                        items = updatedList,
                        snackbarMessage = successResponse.message,
                        isError = false
                    )

                }
                .onFailure { error ->
                    val updatedList = _uiState.value.let{
                        (it as? InventoryUiState.InventoryList)?.items?.toMutableList()
                    } ?: return@onFailure
                    val updatedIndex = updatedList.indexOfFirst { it.id == id }
                    // Revert the optimistic update
                    updatedList[updatedIndex] = previousItem.copy(isToggling = false)
                    _uiState.value = InventoryUiState.InventoryList(
                        items = updatedList,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )

                }
        }
    }

    fun onBackFromDetails() {
        lastListState?.let {
            _uiState.value = it     // restore list
        }
    }

    fun clearSnackbarMessage() {
        val currentState = _uiState.value
        _uiState.value = when (currentState) {
            is InventoryUiState.AddEditInventory ->
                currentState.copy(snackbarMessage = null)

            is InventoryUiState.InventoryList ->
                currentState.copy(snackbarMessage = null)

            else -> currentState
        }
    }

    private inline fun updateForm(
        block: InventoryForm.() -> InventoryForm
    ){
        val current = _uiState.value
        if (current is InventoryUiState.AddEditInventory) {
            val updatedForm = current.form.block()
            _uiState.update {
                current.copy(form = updatedForm)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val inventoryRepo = application.container.inventoryRepo
                InventoryViewModel(inventoryRepo)
            }

        }
    }


}

