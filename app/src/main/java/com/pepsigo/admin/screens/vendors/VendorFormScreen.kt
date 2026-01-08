package com.pepsigo.admin.screens.vendors

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pepsigo.admin.screens.commonComponents.UserFormScreen

@Composable
fun VendorFormScreen(
    viewModel: VendorViewModel,
    state: VendorUiState.AddEditVendor,
    onPickLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    UserFormScreen(
        form = state.form,
        locations = state.locations,
        isEdit = state.isEdit,
        isVendor = state.isVendor,
        showLocationDropdown = true,
        formErrors = state.formErrors,
        showLocationPicker = false,
        onPickLocation = onPickLocation,
        onValueChange = { property, value ->
            viewModel.updateFormField(property, value)
        },
        onLocationChange = {  id ->
            viewModel.updateLocation(id)
        },
        onSave = {  form -> viewModel.saveVendor(form) },
        onCancel = { viewModel.getVendors() },
        modifier = modifier
    )

    BackHandler {
        viewModel.getVendors() // navigate back to list state
    }
}