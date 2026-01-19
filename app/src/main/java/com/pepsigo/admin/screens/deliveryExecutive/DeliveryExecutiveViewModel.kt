package com.pepsigo.admin.screens.deliveryExecutive

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.DeliveryExecutiveUseCase
import com.pepsigo.admin.model.DeliveryExecutiveUiModel
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.screens.location.Location
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

sealed class DeliveryExecutiveUiState {

    object Loading : DeliveryExecutiveUiState()

    data class DeliveryExecutiveList(
        val isLoading: Boolean = false,
        val deliveryExecutives: List<DeliveryExecutiveUiModel> = emptyList(),
        val message: String? = null,
        val snackbarMessage: String? = null,
        val isError: Boolean = false
    ):DeliveryExecutiveUiState()

    data class AddDelForm(
        val form: NewDelForm = NewDelForm(),
        val snackbarMessage: String? = null,
        val formErrors: Map<String, String> = emptyMap(),
        val isLoading: Boolean = false
    ):DeliveryExecutiveUiState()

    data class EditDelForm(
        val form: UserForm = UserForm(),
        val locations: List<Location> = emptyList(),
        val formErrors: Map<String, String> = emptyMap(),
        val isEdit : Boolean = true,
        val isVendor: Boolean = false,
       val isLoading: Boolean = false,
        val snackbarMessage: String? = null,

        ): DeliveryExecutiveUiState()
}
data class NewDelForm(
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val password: String = "",
)



class DeliveryExecutiveViewModel(
    private val deliveryUseCase: DeliveryExecutiveUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<DeliveryExecutiveUiState>(
        DeliveryExecutiveUiState.Loading
    )
    val deliveryUiState = _uiState.asStateFlow()

    init {
        getDeliveryExecutives()
    }

    fun refresh() {
        getDeliveryExecutives()
    }

    fun getDeliveryExecutives() {
        _uiState.value = DeliveryExecutiveUiState.Loading
        viewModelScope.launch {
            val result = deliveryUseCase()
            result
                .onSuccess { executivesResult ->
                    _uiState.value = DeliveryExecutiveUiState.DeliveryExecutiveList(
                        isLoading = false,
                        deliveryExecutives = executivesResult.data,
                        message = executivesResult.error?.userFriendlyMessage,
                        snackbarMessage = executivesResult.error?.userFriendlyMessage,
                        isError = executivesResult.error != null
                    )
                }
                .onFailure { error ->
                    _uiState.value = DeliveryExecutiveUiState.DeliveryExecutiveList(
                        isLoading = false,
                        message = (error as AppError).userFriendlyMessage,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }
    }

    fun getDeliveryExecutiveById(id:Int) {
        _uiState.value = DeliveryExecutiveUiState.EditDelForm(isLoading = true)

        viewModelScope.launch {
            val result = deliveryUseCase.getDeliveryExecutiveById(id)
            result
                .onSuccess { user ->
                    _uiState.value = DeliveryExecutiveUiState.EditDelForm(
                        form = UserForm(
                            id = user.id,
                            name = user.name,
                            businessName = user.businessName,
                            mobile = user.mobile,
                            email = user.email,
                            address1 = user.address1,
                            address2 = user.address2,
                            state = user.state,
                            pincode = user.pincode,
                            locationId = user.locationId,
                            coordinates = "${user.latitude}, ${user.longitude}"
                        ),
                        isEdit = true,
                        isVendor = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = DeliveryExecutiveUiState.DeliveryExecutiveList(
                        isLoading = false,
                        message = (error as AppError).userFriendlyMessage,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        isError = true
                    )
                }
        }

    }

    fun addDeliveryExecutive() {
        _uiState.value = DeliveryExecutiveUiState.AddDelForm()
    }

    fun onNameChange(name: String) {updateAddDelForm { copy(form = form.copy(name = name)) }}
    fun onEmailChange(email: String) {updateAddDelForm { copy(form = form.copy(email = email)) }}
    fun onMobileChange(mobile: String) {updateAddDelForm { copy(form = form.copy(mobile = mobile)) }}
    fun onPasswordChange(password: String) {updateAddDelForm { copy(form = form.copy(password = password)) }}

    fun saveDeliveryExecutive(form: NewDelForm) {
        val current = _uiState.value as DeliveryExecutiveUiState.AddDelForm

        val errors= mutableMapOf<String, String>()
        if (form.name.isBlank()) errors["name"] = "Name is required"
        if (form.email.isBlank()) errors["email"] = "Email is required"
        if (form.mobile.isBlank()) errors["mobile"] = "Mobile is required"
        if (form.password.isBlank() || form.password.length < 6) errors["password"] = "Password is required, min 6 characters"

        if (errors.isNotEmpty()) {
            _uiState.value = (_uiState.value as? DeliveryExecutiveUiState.AddDelForm)?.copy(
                formErrors = errors
            ) ?: _uiState.value
            return
        }

        _uiState.value = current.copy(isLoading = true)
        Log.d("DeliveryExecutiveViewModel", "Saving delivery executive : $form")

        viewModelScope.launch {
            val result = deliveryUseCase.addDeliveryExecutive(form)
            result
                .onSuccess { successResponse ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        snackbarMessage = successResponse.message,
                        formErrors = emptyMap(),
                        form = NewDelForm()

                    )
                }
                .onFailure { error ->
                    val appError = error as AppError
                    _uiState.value = current.copy(
                        isLoading = false,
                        snackbarMessage = appError.userFriendlyMessage
                    )
                }
        }

    }

    fun updateFormField(property: KProperty1<UserForm, String?>, value: String) {
        val current = _uiState.value
        if (current is DeliveryExecutiveUiState.EditDelForm) {
            val updatedForm = when (property.name) {
                "name" -> current.form.copy(name = value)
                "mobile" -> current.form.copy(mobile = value)
                "email" -> current.form.copy(email = value)
                "address1" -> current.form.copy(address1 = value)
                "address2" -> current.form.copy(address2 = value)
                "state" -> current.form.copy(state = value)
                "pincode" -> current.form.copy(pincode = value)
                else -> current.form
            }
            _uiState.value = current.copy(form = updatedForm)
        }

    }

    fun updateDeliveryExecutive(form: UserForm) {
        Log.d("DeliveryExecutiveViewModel", "Updating delivery executive : $form")

        val current = _uiState.value as DeliveryExecutiveUiState.EditDelForm
        val errors= mutableMapOf<String, String>()
        if (form.name.isBlank()) errors["name"] = "Name is required"
        if (form.email.isBlank()) errors["email"] = "Email is required"
        if (form.mobile.isBlank() || form.mobile.length < 10 ) errors["mobile"] = "Mobile is required"

        if (errors.isNotEmpty()) {
            _uiState.value = (_uiState.value as? DeliveryExecutiveUiState.EditDelForm)?.copy(
                formErrors = errors
            ) ?: _uiState.value
            return
        }

        _uiState.value = current.copy(isLoading = true)
        viewModelScope.launch {
            val result = deliveryUseCase.updateDeliveryExecutive(form)
            result
                .onSuccess {
                    _uiState.value = current.copy(
                        isLoading = false,
                        snackbarMessage = it.message,
                        formErrors = emptyMap()
                    )

                }
                .onFailure { error ->
                    val appError = error as AppError
                    _uiState.value = current.copy(
                        isLoading = false,
                        snackbarMessage = appError.userFriendlyMessage
                    )
                }
        }


    }

    fun toggleDeliveryExecutiveStatus(deliveryExecutive: DeliveryExecutiveUiModel) {
        val current = _uiState.value
        if (current !is DeliveryExecutiveUiState.DeliveryExecutiveList) return

        val list =current.deliveryExecutives.toMutableList()
        val index = list.indexOfFirst { it.id == deliveryExecutive.id }
//        if (index == -1) return
        val previous = list[index]
        val toggled = previous.copy(
            enabled = !previous.enabled,
            isToggling = true
        )
        // 1️⃣ Optimistic update
        list[index] = toggled
        _uiState.value = current.copy(
            deliveryExecutives = list
        )

        // 2️⃣ Make API call
            viewModelScope.launch {
                val result = deliveryUseCase.toggleDeliveryExecutiveStatus(deliveryExecutive.id)

                result
                    .onSuccess { successResponse ->
                        val updatedList = _uiState.value.let {
                            (it as? DeliveryExecutiveUiState.DeliveryExecutiveList)?.deliveryExecutives?.toMutableList()
                        } ?: return@onSuccess
                        val currentIndex = updatedList.indexOfFirst { it.id == deliveryExecutive.id }
                        updatedList[currentIndex] = toggled.copy(isToggling = false)

                        _uiState.value = current.copy(
                            deliveryExecutives = updatedList,
                            snackbarMessage = successResponse.message
                        )

                    }
                    .onFailure { error ->
                        val appError = error as AppError
                        val revertList = _uiState.value.let {
                            (it as? DeliveryExecutiveUiState.DeliveryExecutiveList)?.deliveryExecutives?.toMutableList()
                        } ?: return@onFailure
                        val currentIndex = revertList.indexOfFirst { it.id == deliveryExecutive.id }
                        revertList[currentIndex] = previous.copy(isToggling = false)

                        _uiState.value = current.copy(
                            deliveryExecutives = revertList,
                            isLoading = false,
                            snackbarMessage = appError.userFriendlyMessage,
                            isError = true
                        )
                    }
            }

    }






    private inline fun updateAddDelForm(
        block: DeliveryExecutiveUiState.AddDelForm.() -> DeliveryExecutiveUiState.AddDelForm
    ) {
        val current = _uiState.value
        if (current is DeliveryExecutiveUiState.AddDelForm) {
            _uiState.value = current.block()
        }
    }


    fun clearSnackbarMessage() {
        when (val current = _uiState.value) {
            is DeliveryExecutiveUiState.DeliveryExecutiveList -> {
                _uiState.value = current.copy(snackbarMessage = null)
            }

            is DeliveryExecutiveUiState.AddDelForm -> {
                _uiState.value = current.copy(snackbarMessage = null)
            }

            else -> {}
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val deliveryUseCase = application.container.deliveryUseCase
                DeliveryExecutiveViewModel(deliveryUseCase)
            }
        }
    }
}


