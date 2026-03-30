package com.pepsigo.admin.screens.makeSales

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.InventoryBatchUi
import com.pepsigo.admin.model.InventoryBestBatchItemUi
import com.pepsigo.admin.model.SaleInventorySearchItemUi
import com.pepsigo.admin.model.User
import com.pepsigo.admin.repository.MakeSalesRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MakeSaleUiState(
    // Customer selection
    val customers: List<DropDownList> = emptyList(),
    val selectedCustomer: DropDownList? = null,
    val customerSearchQuery: String = "",
    val customersLoading: Boolean = false,
    val customerError: String? = null,
    val saleDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),

    // Cart items (products added to sale)
    val cartItems: List<CartItem> = emptyList(),

    // Products list (from server)
    val products: List<SaleInventorySearchItemUi> = emptyList(),
    val productsLoading: Boolean = false,
    val productsError: String? = null,

    // Product search
    val productSearchQuery: String = "",
    // Track the last query that was searched on server to prevent duplicate calls
    val lastServerSearchQuery: String? = null,

    // Selected product for batch selection
    val selectedProduct: SaleInventorySearchItemUi? = null,

    // Batch selection state
    val allBatches: List<InventoryBatchUi> = emptyList(),
    val batchesLoading: Boolean = false,
    val batchesError: String? = null,

    val bestBatch: InventoryBestBatchItemUi? = null,
    val bestBatchLoading: Boolean = false,
    val bestBatchError: String? = null,

    // Quantity input per batch (batchId -> quantity string)
    val batchQuantities: Map<Int, String> = emptyMap(),

    // Sale creation state
    val isCreatingSale: Boolean = false,
    val saleCreationError: String? = null,
    val createdSaleId: Int? = null,
    val saleTotalAmount: Double? = null,
    val saleCreationSuccess: Boolean = false,
    val paymentMade: Boolean = false
)

data class CartItem(
    val batchId: Int,
    val inventoryId: Int,
    val itemName: String,
    val quantity: Int,
    val unit: String,
    val gstPercent: String,
    val salePrice: String,
)

class MakeSaleViewModel(
    private val userRepository: UserRepository,
    private val makeSalesRepo: MakeSalesRepo
) : ViewModel() {

    private val _state = MutableStateFlow(MakeSaleUiState())
    val state: StateFlow<MakeSaleUiState> = _state.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            _state.update { it.copy(customersLoading = true, customerError = null) }
            val result = userRepository.getUsers("customer")
            result.fold(
                onSuccess = { customers ->
                    _state.update {
                        it.copy(
                            customers = customers.map { user ->
                                DropDownList(id = user.id, name = user.businessName.ifEmpty { user.name })
                            },
                            customersLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            customersLoading = false,
                            customerError = (error as AppError).userFriendlyMessage
                        )
                    }
                }
            )
        }
    }

    fun onCustomerSelected(customer: DropDownList?) {
        _state.update { it.copy(selectedCustomer = customer) }
    }

    fun setSaleDate(date: String){
        _state.update { it.copy(saleDate = date) }
    }

    fun onCustomerSearchQueryChange(query: String) {
        _state.update { it.copy(customerSearchQuery = query) }
    }

    fun onProductSearchQueryChange(query: String) {
        _state.update { it.copy(productSearchQuery = query) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadProducts(forceRefresh: Boolean = false, query: String? = null) {
        val customerId = _state.value.selectedCustomer?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(productsLoading = true, productsError = null) }
            val result = makeSalesRepo.searchInventory(customerId, query)
            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            products = response.data,
                            productsLoading = false,
                            // Reset lastServerSearchQuery when loading full list
                            lastServerSearchQuery = if (query == null) null else it.lastServerSearchQuery
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            productsLoading = false,
                            productsError = (error as AppError).userFriendlyMessage
                        )
                    }
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchProductsFromServer(query: String) {
        val customerId = _state.value.selectedCustomer?.id ?: return
        // Prevent duplicate calls for the same query
        if (_state.value.lastServerSearchQuery == query && !_state.value.productsLoading) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(productsLoading = true, productsError = null, lastServerSearchQuery = query) }
            val result = makeSalesRepo.searchInventory(customerId, query)
            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            products = response.data,
                            productsLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            productsLoading = false,
                            productsError = (error as AppError).userFriendlyMessage
                        )
                    }
                }
            )
        }
    }

    fun onProductClick(product: SaleInventorySearchItemUi) {
        _state.update { it.copy(selectedProduct = product) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadBatchesForProduct() {
        val product = _state.value.selectedProduct ?: return
        val customerId = _state.value.selectedCustomer?.id

        // Load all batches
        viewModelScope.launch {
            _state.update { it.copy(batchesLoading = true, batchesError = null) }
            val result = makeSalesRepo.getInventoryBatches(product.id, customerId)
            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            allBatches = response.batches,
                            batchesLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            batchesLoading = false,
                            batchesError = (error as AppError).userFriendlyMessage
                        )
                    }
                }
            )
        }

        // Load best batch
        viewModelScope.launch {
            _state.update { it.copy(bestBatchLoading = true, bestBatchError = null) }
            val result = makeSalesRepo.getInventoryBestBatchForCustomer(product.id, customerId)
            Log.d("InventoryBestBatchUi", "$result")
            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            bestBatch = response.batch,
                            bestBatchLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            bestBatchLoading = false,
                            bestBatchError = (error as AppError).userFriendlyMessage
                        )
                    }
                }
            )
        }
    }

    fun updateBatchQuantity(batchId: Int, quantity: String) {
        _state.update {
            it.copy(batchQuantities = it.batchQuantities + (batchId to quantity))
        }
    }

    fun addToCart(batchId: Int, batch: InventoryBatchUi) {
        val product = _state.value.selectedProduct ?: return
        val quantityStr = _state.value.batchQuantities[batchId] ?: return
        val quantity = quantityStr.toIntOrNull() ?: return
        if (quantity <= 0 || quantity > batch.availableQuantity) return

        _state.update { currentState ->
            // Check if item with same inventoryId and batchId already exists
            val existingIndex = currentState.cartItems.indexOfFirst {
                it.inventoryId == product.id && it.batchId == batchId
            }

            val updatedCartItems = if (existingIndex >= 0) {
                // Update existing item's quantity
                currentState.cartItems.mapIndexed { index, item ->
                    if (index == existingIndex) {
                        item.copy(quantity = quantity)
                    } else {
                        item
                    }
                }
            } else {
                // Add new item
                val cartItem = CartItem(
                    batchId = batchId,
                    inventoryId = product.id,
                    itemName = product.itemName,
                    quantity = quantity,
                    unit = batch.unit,
                    gstPercent = product.gstPercent,
                    salePrice = batch.salePrice
                )
                currentState.cartItems + cartItem
            }

            currentState.copy(cartItems = updatedCartItems)
        }
    }

    fun removeFromCart(inventoryId: Int, batchId: Int) {
        _state.update { currentState ->
            currentState.copy(
                cartItems = currentState.cartItems.filterNot {
                    it.inventoryId == inventoryId && it.batchId == batchId
                },
                // Also clear the quantity input for this batch
                batchQuantities = currentState.batchQuantities - batchId
            )
        }
    }

    fun updateCartItemQuantity(inventoryId: Int, batchId: Int, newQuantity: Int) {
        _state.update { currentState ->
            currentState.copy(
                cartItems = currentState.cartItems.map { item ->
                    if (item.inventoryId == inventoryId && item.batchId == batchId) {
                        item.copy(quantity = newQuantity)
                    } else {
                        item
                    }
                }
            )
        }
    }


    fun clearBatchSelection() {
        _state.update {
            it.copy(
                selectedProduct = null,
                allBatches = emptyList(),
                bestBatch = null,
                batchesError = null,
                bestBatchError = null,
                batchQuantities = emptyMap()
            )
        }
    }

    fun retryLoadCustomers() {
        loadCustomers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun retryLoadProducts() {
        loadProducts(forceRefresh = true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clearProductSearch() {
        _state.update { it.copy(productSearchQuery = "", lastServerSearchQuery = null) }
        // Reload full products list
        loadProducts()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSale() {
        val customerId = _state.value.selectedCustomer?.id ?: return
        val saleDate = _state.value.saleDate
        val cartItems = _state.value.cartItems

        if (cartItems.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isCreatingSale = true, saleCreationError = null) }

            val result = makeSalesRepo.createSale(customerId, saleDate, cartItems)

            result.fold(
                onSuccess = { response ->
                    // Response is SalesResponse<SalesReturnResponse>
                    val saleData = response
                    _state.update {
                        it.copy(
                            isCreatingSale = false,
                            saleCreationSuccess = true,
                            createdSaleId = saleData.id,
                            saleTotalAmount = saleData.totalAmount,
                            saleCreationError = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isCreatingSale = false,
                            saleCreationSuccess = false,
                            saleCreationError = (error as? AppError)?.userFriendlyMessage ?: "Failed to create sale"
                        )
                    }
                }
            )
        }
    }

    fun clearSaleCreationState() {
        _state.update {
            it.copy(
                saleCreationSuccess = false,
                saleCreationError = null,
                createdSaleId = null,
                saleTotalAmount = null
            )
        }
    }

    fun markPaymentMade() {
        _state.update { it.copy(paymentMade = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                MakeSaleViewModel(
                    userRepository = application.container.userRepository,
                    makeSalesRepo = application.container.makeSalesRepo
                )
            }
        }
    }

}