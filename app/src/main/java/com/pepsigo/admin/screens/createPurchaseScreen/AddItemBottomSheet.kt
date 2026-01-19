package com.pepsigo.admin.screens.createPurchaseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.constants.DateSelectionMode
import com.pepsigo.admin.screens.commonComponents.DockedDatePicker
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.reports.DropDownErrorCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    show: Boolean,
    uiState: CreatePurchaseUi ,
    updateSelectedInventory: (ProductList?) -> Unit,
    onDismiss: () -> Unit,
    onSave: (
        selectedInv: ProductList?,
        qty: String,
        cost: String,
        sale: String,
        retail: String,
        expiry: String?
    ) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var salePrice by remember { mutableStateOf("") }
    var retailPrice by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf<String?>(null) }


    if(!show) return

    // Prefill when editing
    LaunchedEffect(show) {
        if (uiState.isEditing) {
            val item = uiState.addEditItemDetails[uiState.editIndex!!]

            quantity = item.itemQuantity
            costPrice = item.costPrice
            salePrice = item.salePrice
            retailPrice = item.retailPrice
            expiryDate = item.expiryDate

        } else {
            quantity = ""
            costPrice = ""
            salePrice = ""
            retailPrice = ""
            expiryDate = null
            updateSelectedInventory(null)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.add_item),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "close")
                }
            }
            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Inventory DropDown
                item {
                    Text(stringResource(R.string.inventory))
                    if(uiState.inventoryError != null){
                        DropDownErrorCard(error = uiState.inventoryError)
                    }else {
                        DropDown(
                            dropDown = uiState.inventoryItem,
                            error = uiState.addItemErrors.productError,
                            label = "",
                            selected = uiState.selectedInventory,
                            onSelected = { updateSelectedInventory(it) },
                            labelExtractor = { it.prodName },
                            isCreatePurchase = true
                        )
                    }
                }

                //Quantity
                item {
                    Text("Quantity *")
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter quantity") },
                        isError = uiState.addItemErrors.quantityError,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                }

                //unit
                item {
                    Text("Unit *")
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val units = listOf("Pcs", "Box", "Case", "Bundle")
                        units.forEach { unit ->
                            FilterChip(
                                onClick = {},
                                label = { Text(unit) },
                                selected = uiState.selectedInventory?.unit == unit,
                                leadingIcon = {
                                    if (uiState.selectedInventory?.unit == unit) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                //Gst
                item {
                    Text("GST % *")
                    OutlinedTextField(
                        value = uiState.selectedInventory?.gstPercent ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Enter GST percentage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }

                //cost price
                item {
                    Text("Cost Price *")
                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        placeholder = { Text("Enter cost price") },
                        isError = uiState.addItemErrors.costPriceError,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }

                //sale price
                item {
                    Text("Sale Price *")
                    OutlinedTextField(
                        value = salePrice,
                        onValueChange = { salePrice = it },
                        placeholder = { Text("Enter sale price") },
                        isError = uiState.addItemErrors.salePriceError,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }

                //retail price
                item {
                    Text("Retail Price *")
                    OutlinedTextField(
                        value = retailPrice,
                        onValueChange = { retailPrice = it },
                        placeholder = { Text("Enter retail price") },
                        isError = uiState.addItemErrors.retailPriceError,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // expiry date
                item {
                    Text(
                        text = "Expiry Date (Optional)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DockedDatePicker(
                        label = expiryDate,
                        error = false,
                        modifier = Modifier.fillMaxWidth(),
                        onDateSelected = { expiryDate = it },
                        mode = DateSelectionMode.TODAY_OR_FUTURE
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }

                Button(
                    onClick = {         onSave(
                        uiState.selectedInventory,
                        quantity,
                        costPrice,
                        salePrice,
                        retailPrice,
                        expiryDate
                    ) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Item")
                }
            }
        }

    }
}