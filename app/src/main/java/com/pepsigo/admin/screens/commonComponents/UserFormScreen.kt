package com.pepsigo.admin.screens.commonComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.constants.UserFormFields
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.screens.customer.FormTextField
import com.pepsigo.admin.screens.customer.LocationDropdown
import com.pepsigo.admin.screens.location.Location
import com.pepsigo.admin.screens.reports.DropDownErrorCard
import kotlin.reflect.KProperty1

@Composable
fun UserFormScreen(
    form: UserForm,
    locations: List<Location>,
    locationError: String?,
    isEdit: Boolean,
    isVendor: Boolean,
    showLocationDropdown: Boolean,
    formErrors : Map<String, String>,
    showLocationPicker: Boolean,
    onPickLocation: () -> Unit,
    onValueChange: (KProperty1<UserForm, String?>, String) -> Unit,
    onLocationChange: (Int) -> Unit,
    onSave: (UserForm) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (isVendor) {
            FormTextField(
                value = form.businessName ?: "",
                onValueChange = { onValueChange(UserForm::businessName, it) },
                isError = formErrors.containsKey("businessName"),
                errorMessage = formErrors["businessName"],
                label = "BusinessName",
                keyboardType = KeyboardType.Text
            )
        }
        // Generic form fields
        UserFormFields.forEach { field ->
            val value = field.property.get(form)
            FormTextField(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(field.property, newValue)
                },
                isError = formErrors.containsKey(field.property.name),
                errorMessage = formErrors[field.property.name],
                label = field.label,
                keyboardType = field.keyboardType
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email field only for edit
        if (isEdit) {
            FormTextField(
                value = form.email,
                onValueChange = { onValueChange(UserForm::email, it) },
                isError = formErrors.containsKey("email"),
                errorMessage = formErrors["email"],
                label = "Email",
                keyboardType = KeyboardType.Email
            )
        }

        // Location row
        if (showLocationPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = form.coordinates,
                    onValueChange = {},
                    label = { Text("Location Coordinates") },
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { onPickLocation() }, modifier = Modifier.height(40.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Pick Location")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pick")
                }
            }
        }

        // Location Dropdown
        if (showLocationDropdown) {
            if (locationError == null) {
                LocationDropdown(
                    selectedName = locations.find { it.id == form.locationId }?.name ?: "",
                    onLocationSelected = { location -> onLocationChange(location.id) },
                    locations = locations
                )
            }else{
                DropDownErrorCard(locationError)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onCancel) { Text("Cancel") }
            Button(onClick = { onSave(form) }) { Text("Save") }
        }
    }
}


