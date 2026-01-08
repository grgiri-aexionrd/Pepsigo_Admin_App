package com.pepsigo.admin.screens.vendors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.EditUserRequest
import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.repository.LocationRepository
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.location.Location
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

sealed class VendorUiState {
    object Loading : VendorUiState()

    data class VendorList(
        val vendors: List<User> ,
        val snackbarMessage: String? = null,
        val isError: Boolean = false
    ) : VendorUiState()

    data class AddEditVendor(
        val form: UserForm, // Replace Any with actual VendorForm data class
        val locations: List<Location>, // Replace Any with actual LocationRequest data class
        val isEdit: Boolean,
        val isVendor: Boolean,
        val formErrors: Map<String, String> = emptyMap()
    ): VendorUiState()

    data class Success(val message: String) : VendorUiState()

    data class Error(val message: AppError) : VendorUiState()
}


class VendorViewModel(
    private val repository: UserRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _vendorUiState = MutableStateFlow<VendorUiState>(VendorUiState.Loading)
    val vendorUiState = _vendorUiState.asStateFlow()

    init {
        getVendors()
    }

    fun getVendors(message: String? = null, isError: Boolean = false) {
        viewModelScope.launch {
            _vendorUiState.value = VendorUiState.Loading
            val result = repository.getUsers(role = "vendor")
            result
                .onSuccess { vendors ->
                    _vendorUiState.value = VendorUiState.VendorList(
                        vendors = vendors, snackbarMessage = message, isError = isError)
                }
                .onFailure { error ->
//                    _vendorUiState.value = VendorUiState.Error(error as AppError)
                    _vendorUiState.value = VendorUiState.VendorList(
                        vendors = emptyList(),
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }

    }

    fun addVendors() {
        viewModelScope.launch {
           when( val result = locationRepository.getLocations()) {
               is LocationResult.Success -> {
                   val locations = result.data
                   _vendorUiState.value = VendorUiState.AddEditVendor(UserForm(), locations, isEdit = false, isVendor = true)
               }
               is LocationResult.Error -> {
                   _vendorUiState.value = VendorUiState.Error(AppError.Unknown(result.message, result.throwable))
               }
           }
        }
    }

    fun editVendor(vendor: User) {
        viewModelScope.launch {

            when (val result = locationRepository.getLocations()) {
                is LocationResult.Success -> {
                    val locations = result.data
                    val vendorForm = UserForm(
                        id = vendor.id,
                        name = vendor.name,
                        businessName = vendor.businessName,
                        mobile = vendor.mobile,
                        email = vendor.email,
                        address1 = vendor.address1,
                        address2 = vendor.address2,
                        state = vendor.state,
                        pincode = vendor.pincode,
                        locationId = vendor.locationId
                    )
                    _vendorUiState.value = VendorUiState.AddEditVendor(
                        vendorForm,
                        locations,
                        isEdit = true,
                        isVendor = true
                    )
                }

                is LocationResult.Error -> {
                    _vendorUiState.value =
                        VendorUiState.Error(AppError.Unknown(result.message, result.throwable))
                }
            }
        }
    }

    fun updateFormField(property: KProperty1<UserForm, String?>, value: String) {
        val currentState = _vendorUiState.value
        if (currentState is VendorUiState.AddEditVendor) {
            val updatedForm = when (property.name) {
                "name" -> currentState.form.copy(name = value)
                "businessName" -> currentState.form.copy(businessName = value)
                "mobile" -> currentState.form.copy(mobile = value)
                "email" -> currentState.form.copy(email = value)
                "address1" -> currentState.form.copy(address1 = value)
                "address2" -> currentState.form.copy(address2 = value)
                "state" -> currentState.form.copy(state = value)
                "pincode" -> currentState.form.copy(pincode = value)
                else -> currentState.form
            }
            _vendorUiState.value = currentState.copy(form = updatedForm)
        }
    }

    fun saveVendor(form: UserForm) {
        val errors = mutableMapOf<String, String>()

        if (form.name.isBlank()) errors["name"] = "Name cannot be empty"
        if (form.mobile.isBlank()) errors["mobile"] = "Mobile cannot be empty"
        if (form.address1.isBlank() || form.address2.isBlank()) errors["address1"] = "Address cannot be empty"
        if (form.state.isBlank()) errors["state"] = "State cannot be empty"
        if (form.pincode.isBlank()) errors["pincode"] = "Pincode cannot be empty"

        if (form.businessName.isNullOrBlank() ) errors["businessName"] = "Business name is required"

        if (errors.isNotEmpty()) {

            _vendorUiState.value = (_vendorUiState.value as? VendorUiState.AddEditVendor)?.copy(
                formErrors = errors
            ) ?: return
            return
        }
        viewModelScope.launch {
            val result = if (form.id == null) {
                Log.d("VendorViewModel", "Adding vendor : $form")
                repository.addVendor(
                    form = AddUserRequest(
                        name = form.name,
                        businessName = form.businessName,
                        mobile = form.mobile,
                        address1 = form.address1,
                        address2 = form.address2,
                        state = form.state,
                        pincode = form.pincode,
                        locationId = form.locationId
                    )
                )
            } else {
                Log.d("VendorViewModel", "Updating vendor : $form")
                repository.updateUser(
                    form = EditUserRequest(
                        id  = form.id,
                        userDetail = AddUserRequest(
                            name = form.name,
                            businessName = form.businessName,
                            mobile = form.mobile,
                            email = form.email,
                            address1 = form.address1,
                            address2 = form.address2,
                            state = form.state,
                            pincode = form.pincode,
                            locationId = form.locationId
                        )
                    )
                )
            }
            result
                .onSuccess { successResponse ->
//                    _vendorUiState.value = VendorUiState.Success(successResponse.message)
                    getVendors(successResponse.message, isError = false)
                }
                .onFailure { error ->
//                    _vendorUiState.value = VendorUiState.Error(error as AppError)
                    val appError = error as AppError
                    getVendors(message = appError.userFriendlyMessage, isError = true)
                }
        }
    }

    fun updateLocation(id: Int?) {
        val currentState = _vendorUiState.value
        if (currentState is VendorUiState.AddEditVendor) {
            val updatedForm = currentState.form.copy(locationId = id)
            _vendorUiState.value = currentState.copy(form = updatedForm)
        }
    }

    fun toggleVendorStatus(id: Int) {
        viewModelScope.launch {
            val result = repository.toggleUserStatus(id)
            result
                .onSuccess {
                    getVendors()
                }
                .onFailure { error ->
                    _vendorUiState.value = VendorUiState.Error(error as AppError)
                }
        }
    }

    fun clearSnackbarMessage() {
        val currentState = _vendorUiState.value
        if (currentState is VendorUiState.VendorList) {
            _vendorUiState.value = currentState.copy(snackbarMessage = null)
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.userRepository
                val locationRepository = application.container.locationRepository
                VendorViewModel(repository, locationRepository)
            }
        }
    }
}