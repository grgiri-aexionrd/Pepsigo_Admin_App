package com.pepsigo.admin.screens.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.constants.unitList
import com.pepsigo.admin.screens.customer.FormTextField

@Composable
fun AddEditInventoryScreen(
    viewModel: InventoryViewModel,
    form: InventoryForm,
    isEdit: Boolean,
    formErrors: Map<String, String>,
//    onFormFieldChange: (String, Any) -> Unit,
    onSave: (InventoryForm) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            text = if (isEdit) stringResource(R.string.edit_inventory) else stringResource(R.string.add_inventory),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        Column {
            Text(
                text = stringResource(R.string.item_name),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
            FormTextField(
                value = form.name,
                onValueChange = viewModel::onNameChange,
                isError = formErrors.containsKey("name"),
                errorMessage = formErrors["name"],
                label = stringResource(R.string.item_name),
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.padding(8.dp))
            if (!isEdit) {
                Text(
                    text = stringResource(R.string.opening_quantity)
                )
                FormTextField(
                    value = form.quantity,
                    onValueChange = viewModel::onQuantityChange,
                    isError = formErrors.containsKey("quantity"),
                    errorMessage = formErrors["quantity"],
                    label = stringResource(R.string.opening_quantity),
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.unit)
                )
                // dropdown for unit selection
                UnitDropDown(
                    selectedUnit = form.unit,
                    onUnitSelected =  viewModel::onUnitChange ,
                    isError = formErrors.containsKey("unit")
                )

                Spacer(modifier = Modifier.padding(8.dp))
            }

            Text(
                text = stringResource(R.string.gst_percent)
            )
            FormTextField(
                value = form.gst,
                onValueChange = viewModel::onGstChange,
                isError = formErrors.containsKey("gst"),
                errorMessage = formErrors["gst"],
                label = stringResource(R.string.gst_percent),
                keyboardType = KeyboardType.Number
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
//            Button(onClick = {}) {
//                Text(text = "Cancel")
//            }
            Button(onClick = { onSave(form) },
                enabled = !isLoading) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else
                    Text(text = "Save")
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropDown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ){
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = { },
            label = { Text(stringResource(R.string.select_unit)) },
            readOnly = true,
            isError = isError,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,enabled = true)      // ✅ Required
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
                expanded = expanded,
        onDismissRequest = { expanded = false }
        ){
            unitList.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }

        }

    }



}