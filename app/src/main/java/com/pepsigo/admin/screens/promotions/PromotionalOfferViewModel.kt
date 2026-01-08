package com.pepsigo.admin.screens.promotions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.domainLayer.PromotionalOfferUseCase
import com.pepsigo.admin.model.User
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PromotionalOfferUiState(
    val customerDropDown: List<User> = emptyList(),
    val selectedCustomer: User? = null,
    val offerList: List<OfferUi> = emptyList(),
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val isError: Boolean = false
)

class PromotionalOfferViewModel(private val useCase: PromotionalOfferUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(PromotionalOfferUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCustomers()
        }

    fun getCustomers(){
        viewModelScope.launch{
            val result = useCase.getCustomers()
            Log.d("PromotionalOfferViewModel", "getCustomers: $result")
            result
                .onSuccess { customers ->
                    _uiState.value = _uiState.value.copy(
                        customerDropDown = customers,
                        isError = false
                    )
                }
                .onFailure {error ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }
    }

    fun updateSelectedCustomer(customer: User?){
        Log.d("PromotionalOfferViewModel", "updateSelectedCustomer: $customer")
        _uiState.update {
            it.copy(selectedCustomer = customer)        }
    }

    fun getOffers(customerId: Int?) {
        Log.d("PromotionalOfferViewModel", "getOffers: $customerId")
        _uiState.update {
            it.copy(isLoading = true)
        }
        if (customerId == null) return
        viewModelScope.launch {
            val result = useCase.getPromotionalOffers(customerId)
            Log.d("PromotionalOfferViewModel", "getOffers: $result")
            result.onSuccess { offerList ->
                _uiState.value = _uiState.value.copy(
                    offerList = offerList,
                    isLoading = false,
                )
            }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isLoading = false,
                        isError = true
                    )
                }

        }
    }


    fun clearSnackbarMessage() {
        _uiState.update { current ->
            current.copy(snackbarMessage = null)
        }
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val useCase = application.container.promotionalOfferUseCase
                PromotionalOfferViewModel(useCase)
            }
        }
    }




}