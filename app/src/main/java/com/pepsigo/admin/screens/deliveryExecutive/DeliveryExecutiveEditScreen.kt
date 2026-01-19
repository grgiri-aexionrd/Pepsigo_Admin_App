package com.pepsigo.admin.screens.deliveryExecutive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.screens.commonComponents.UserFormScreen


@Composable
fun DeliveryExecutiveEditScreen(
    viewModel: DeliveryExecutiveViewModel,
    state: DeliveryExecutiveUiState.EditDelForm,
    onSave: (UserForm) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation for Delivery Executive Edit Screen

    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){

        Text(text = "Edit Details",
            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier
//                .padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        UserFormScreen(
            form = state.form,
            locations = state.locations,
            isEdit = state.isEdit,
            isVendor = false,
            showLocationDropdown = false,
            formErrors = state.formErrors,
            showLocationPicker = false,
            onPickLocation = { /* Do Nothing */ },
            onValueChange = { property, value ->
                viewModel.updateFormField(property, value)
            },
            onLocationChange = { /* Do Nothing */ },
            onSave = { form -> onSave(form) },
            onCancel = {  viewModel.getDeliveryExecutives()  },
            locationError = null,
            modifier = modifier
        )
    }

    BackHandler {
        viewModel.getDeliveryExecutives() // navigate back to list state
    }

}