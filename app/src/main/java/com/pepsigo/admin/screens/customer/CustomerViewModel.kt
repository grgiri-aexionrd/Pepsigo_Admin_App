package com.pepsigo.admin.screens.customer

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

sealed class CustomerUiState {
    object Loading : CustomerUiState()

    data class CustomerList(
        val customers: List<User>
    ) : CustomerUiState()

    data class AddEditCustomer(
        val form: UserForm,
        val locations: List<Location>,
        val isEdit: Boolean
    ): CustomerUiState()

    data class Error(val message: AppError) : CustomerUiState()
}

class CustomerViewModel(private val repository: UserRepository,
    private val locationRepository: LocationRepository): ViewModel() {

    private val _customerUiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Loading)
    val customerUiState = _customerUiState.asStateFlow()

    init {
        getCustomers()
    }

    fun getCustomers() {
        viewModelScope.launch {
            _customerUiState.value = CustomerUiState.Loading
            val result = repository.getUsers(role = "customer")
            result
                .onSuccess { customers ->
                    _customerUiState.value = CustomerUiState.CustomerList(customers)
                }
                .onFailure { error ->
                    _customerUiState.value = CustomerUiState.Error(error as AppError)
                }
        }

    }

    fun addCustomers() {
        viewModelScope.launch {
            when (val result = locationRepository.getLocations()) {
                is LocationResult.Success -> {
                    _customerUiState.value = CustomerUiState.AddEditCustomer(
                        form = UserForm(),
                        locations = result.data,
                        isEdit = false
                    )
                }
                is LocationResult.Error -> {
                    _customerUiState.value =
                        CustomerUiState.Error(AppError.Unknown(result.message, result.throwable))
                }
            }


        }
    }

    fun updateFormField(field: KProperty1<UserForm, String>, newValue: String) {
        val current = _customerUiState.value
        if (current is CustomerUiState.AddEditCustomer) {
            val updatedForm = when (field) {
                UserForm::name -> current.form.copy(name = newValue)
                UserForm::mobile -> current.form.copy(mobile = newValue)
                UserForm::address1 -> current.form.copy(address1 = newValue)
                UserForm::address2 -> current.form.copy(address2 = newValue)
                UserForm::state -> current.form.copy(state = newValue)
                UserForm::pincode -> current.form.copy(pincode = newValue)
                else -> current.form
            }
            _customerUiState.value = current.copy(form = updatedForm)
        }
    }

    fun editCustomer(form: User) {
        viewModelScope.launch {
            when (val result = locationRepository.getLocations()) {
                is LocationResult.Success -> {
                    val locations = result.data

                    val customerForm = UserForm(
                        id = form.id,
                        name = form.name,
                        mobile = form.mobile,
                        email = form.email,
                        address1 = form.address1,
                        address2 = form.address2,
                        state = form.state,
                        pincode = form.pincode,
                        locationId = form.locationId,
                        coordinates = "${form.latitude}, ${form.longitude}"
                    )
                    _customerUiState.value = CustomerUiState.AddEditCustomer(
                        form = customerForm,
                        locations = locations,
                        isEdit = true
                    )
                }

                is LocationResult.Error -> {
                    _customerUiState.value = CustomerUiState.Error(
                        AppError.Unknown(
                            result.message,
                            result.throwable
                        )
                    )
                }
            }
        }
    }


        fun saveCustomer(form: UserForm) {
            viewModelScope.launch {
                val result = if (form.id == null) {
                    repository.addCustomer(
                        form = AddUserRequest(
                            name = form.name,
                            mobile = form.mobile,
                            address1 = form.address1,
                            address2 = form.address2,
                            state = form.state,
                            pincode = form.pincode,
                            locationId = form.locationId,
                            latitude = form.coordinates.split(",")[0].trim().toDoubleOrNull(),
                            longitude = form.coordinates.split(",")[1].trim().toDoubleOrNull()
                        )
                    )
                } else {
                    repository.updateUser(
                        form = EditUserRequest(
                            id = form.id,
                            userDetail = AddUserRequest(
                                name = form.name,
                                mobile = form.mobile,
                                email = form.email,
                                address1 = form.address1,
                                address2 = form.address2,
                                state = form.state,
                                pincode = form.pincode,
                                locationId = form.locationId,
                                latitude = form.coordinates.split(",")[0].trim().toDoubleOrNull(),
                                longitude = form.coordinates.split(",")[1].trim().toDoubleOrNull()

                            )
                        )
                    )
                }
                result
                    .onSuccess {
                        getCustomers()
                    }
                    .onFailure { error ->
                        _customerUiState.value = CustomerUiState.Error(error as AppError)
                    }
            }

        }

        fun updateLocation(locationId: Int?) {
            val current = _customerUiState.value
            if (current is CustomerUiState.AddEditCustomer) {
                _customerUiState.value = current.copy(
                    form = current.form.copy(locationId = locationId)
                )
            }
        }

        fun updateFormCoordinates(lat: Double, lng: Double) {
            val current = _customerUiState.value
            if (current is CustomerUiState.AddEditCustomer) {
                Log.d("CustomerViewModel", "Updating coordinates to: $lat, $lng")
                val updatedLocation = "%.5f, %.5f".format(lat, lng)
                _customerUiState.value = current.copy(
                    form = current.form.copy(coordinates = updatedLocation)
                )
            }
        }

        fun toggleCustomerStatus(id: Int) {
            viewModelScope.launch {
                val result = repository.toggleUserStatus(id)
                result
                    .onSuccess {
                        getCustomers()
                    }
                    .onFailure { error ->
                        _customerUiState.value = CustomerUiState.Error(error as AppError)
                    }

            }
        }



        companion object {
            val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as AdminAppApplication)
                    val repository = application.container.userRepository
                    val locationRepository = application.container.locationRepository
                    CustomerViewModel(repository, locationRepository)
                }
            }
        }

    }
