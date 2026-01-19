package com.pepsigo.admin.screens.vendors

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.screens.commonComponents.UserFormScreen
import kotlin.reflect.KProperty1

@Composable
fun VendorFormScreen(
//    viewModel: VendorViewModel,
    state: VendorUiState.AddEditVendor,
    onPickLocation: () -> Unit,
    onValueChange: (property: KProperty1<UserForm, String?>, value: String) -> Unit,
    onLocationChange: (id: Int?) -> Unit,
    onSave: (form: UserForm) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    UserFormScreen(
        form = state.form,
        locations = state.locations,
        locationError = state.locationError,
        isEdit = state.isEdit,
        isVendor = state.isVendor,
        showLocationDropdown = true,
        formErrors = state.formErrors,
        showLocationPicker = false,
        onPickLocation = onPickLocation,
        onValueChange = onValueChange,
        onLocationChange = onLocationChange,
        onSave = onSave,
        onCancel = onCancel,
        modifier = modifier
    )

    BackHandler {
        onBack() // navigate back to list state
    }
}