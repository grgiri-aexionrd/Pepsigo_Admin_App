package com.pepsigo.admin.screens.routes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DeliveryExecDropdown(
    selectedDeliveryBoy: String?,
    onDeliveryExecChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedDeliveryBoy ?: "",
            onValueChange = { }, // Read-only, selection happens via dropdown
            label = { Text("Delivery Executive") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Example list, replace with viewModel-provided list
            listOf("Ramesh", "Suresh", "Mahesh").forEach { boy ->
                DropdownMenuItem(
                    text = { Text(boy) },
                    onClick = {
                        onDeliveryExecChange(boy)
                        expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Clear Selection") },
                onClick = {
                    onDeliveryExecChange(null)
                    expanded = false
                }
            )
        }
    }
}
