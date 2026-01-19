package com.pepsigo.admin.screens.customer


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.constants.UserFormFields
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.screens.location.Location

@Composable
fun CustomerFormScreen(
    form: UserForm,

    viewModel: CustomerViewModel,
    locations: List<Location>,
    locationError: String?,
    formErrors : Map<String, String>,
    isEdit: Boolean,
    onPickLocation: () -> Unit,
    onSave: (UserForm) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
//    var formState by remember { mutableStateOf(form) }
//    var selectedLocationName by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(modifier = modifier.padding(16.dp)
        .verticalScroll(scrollState)) {

        // Loop over all string-based form fields
        UserFormFields.forEach { field ->
            val value = field.property.get(form)
            FormTextField(
                value = value,
                onValueChange = { newValue ->
                    viewModel.updateFormField(field.property, newValue)
                },
                isError = formErrors.containsKey(field.property.name),

                label = field.label,
                keyboardType = field.keyboardType
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        // Email field
        if (isEdit) {
            FormTextField(
                value = form.email,
                onValueChange = { viewModel.updateFormField(UserForm::email, it) },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
        }

        // Location Coordinates and Pick Location Button

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Coordinates field
            OutlinedTextField(
                value = form.coordinates,       // e.g. "0.00, 0.00"
                onValueChange = {},             // read-only
                label = { Text("Location Coordinates") },
                enabled = false,
                modifier = Modifier.weight(1f)  // take remaining space
            )

            // Pick Location Button with icon
            Button(
                onClick = {  onPickLocation() },
                modifier = Modifier.height(40.dp) // match OutlinedTextField height
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pick Location"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pick")
            }
        }

        // Location Dropdown
        LocationDropdown(
            selectedName = locations.find { it.id == form.locationId }?.name ?: "",
            onLocationSelected = { location ->
                viewModel.updateLocation(location.id)
            },
            locations = locations
        )
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { onCancel()  }) { Text("Cancel") }
            Button(onClick = { onSave(form) }) { Text("Save") }
        }
    }

}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    selectedName: String,
    onLocationSelected: (Location) -> Unit,
    locations: List<Location>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            label = { Text("Location") },
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,enabled = true)
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location.name) },
                    onClick = {
                        onLocationSelected(location)
                        expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Clear Selection") },
                onClick = {
                    onLocationSelected(Location(0, "", true))
                    expanded = false
                }
            )
        }
    }
}