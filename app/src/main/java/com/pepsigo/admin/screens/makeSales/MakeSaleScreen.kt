package com.pepsigo.admin.screens.makeSales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ModalDatePicker
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.screens.commonComponents.SearchDropDown
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeSaleScreen(
    viewModel: MakeSaleViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToSummary: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    // calendar icon + clickable date text
    val showPicker = remember { mutableStateOf(false) }

    // Filter customers based on search query
    val filteredCustomers by remember(state.customers, state.customerSearchQuery) {
        derivedStateOf {
            if (state.customerSearchQuery.isBlank()) {
                state.customers.take(50)
            } else {
                state.customers
                    .filter { it.name.contains(state.customerSearchQuery, ignoreCase = true) }
                    .take(50)
            }
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.make_sale),
                icon = Icons.Default.ShoppingCart,
                desc = stringResource(R.string.make_sale),
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sticky surface for customer selection
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.select_customer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Pick date",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = state.saleDate,
                            modifier = Modifier
                                .clickable { showPicker.value = true }
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.customerError != null) {
                        Text(
                            text = state.customerError?:"Customer Loading error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = { viewModel.retryLoadCustomers() }) {
                            Text(stringResource(R.string.retry))
                        }
                    } else {
                        SearchDropDown(
                            filteredDropDown = filteredCustomers,
                            error = state.customersLoading,
                            label = stringResource(R.string.customer),
                            searchQuery = state.customerSearchQuery,
                            selected = state.selectedCustomer,
                            onSelected = { viewModel.onCustomerSelected(it) },
                            onSearchChange = { viewModel.onCustomerSearchQueryChange(it) },
                            labelExtractor = { it.name }
                        )
                    }
                }
            }
            if (showPicker.value) {
                ModalDatePicker(
                    onDateSelected = { date ->
                    viewModel.setSaleDate(date)
                        showPicker.value = false
                    },
                    onDismiss = { showPicker.value = false }
                )
            }

            // Cart area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Cart header with Add Item button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.cart_items),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${state.cartItems.size})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onNavigateToAddProduct,
                        enabled = state.selectedCustomer != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_item),
                            tint = if (state.selectedCustomer != null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                if (state.selectedCustomer == null) {
                    Text(
                        text = stringResource(R.string.select_customer_first),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cart items or empty state
                if (state.cartItems.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.no_items_in_cart),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(R.string.add_items_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Show cart items
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(state.cartItems, key = { "${it.inventoryId}_${it.batchId}" }) { cartItem ->
                            CartItemRow(
                                cartItem = cartItem,
                                onQuantityUpdate = { newQuantity ->
                                    viewModel.updateCartItemQuantity(
                                        cartItem.inventoryId,
                                        cartItem.batchId,
                                        newQuantity
                                    )
                                },
                                onDelete = {
                                    viewModel.removeFromCart(cartItem.inventoryId, cartItem.batchId)
                                }
                            )
                        }
                    }

                    // Grand Total Section
                    Spacer(modifier = Modifier.height(8.dp))
                    GrandTotalSection(cartItems = state.cartItems)

                    // Action buttons
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        Button(
                            onClick = onNavigateToSummary,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Buy Now")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityUpdate: (Int) -> Unit,
    onDelete: () -> Unit
) {
    // Edit mode state
    var isEditing by remember { mutableStateOf(false) }
    var editQuantity by remember(cartItem.quantity) { mutableStateOf(cartItem.quantity.toString()) }

    // Calculate values
    val salePriceValue = cartItem.salePrice.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
    val gstPercentValue = cartItem.gstPercent.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
    val displayQuantity = if (isEditing) editQuantity.toIntOrNull() ?: cartItem.quantity else cartItem.quantity
    val subtotal = salePriceValue * displayQuantity
    val tax = (gstPercentValue / 100) * subtotal
    val total = subtotal + tax

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header row with item name, batch number and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItem.itemName + " (InvId: ${cartItem.inventoryId})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Batch #${cartItem.batchId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    if (isEditing) {
                        // Cancel edit
                        IconButton(onClick = {
                            isEditing = false
                            editQuantity = cartItem.quantity.toString()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        // Confirm edit
                        IconButton(onClick = {
                            val newQty = editQuantity.toIntOrNull()
                            if (newQty != null && newQty > 0) {
                                onQuantityUpdate(newQty)
                                isEditing = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirm",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        // Edit button
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Delete button
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity row - editable or display
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = editQuantity,
                        onValueChange = { editQuantity = it.filter { c -> c.isDigit() } },
                        label = { Text(stringResource(R.string.quantity)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = editQuantity.toIntOrNull()?.let { it <= 0 } ?: true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cartItem.unit,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Price details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$displayQuantity ${cartItem.unit} × ${cartItem.salePrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${"%.2f".format(subtotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tax row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GST (${cartItem.gstPercent})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${"%.2f".format(tax)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(4.dp))

            // Total row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GrandTotalSection(cartItems: List<CartItem>) {
    // Calculate grand totals
    var grandSubtotal = 0.0
    var grandTax = 0.0

    cartItems.forEach { cartItem ->
        val salePriceValue = cartItem.salePrice.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val gstPercentValue = cartItem.gstPercent.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val subtotal = salePriceValue * cartItem.quantity
        val tax = (gstPercentValue / 100) * subtotal
        grandSubtotal += subtotal
        grandTax += tax
    }

    val grandTotal = grandSubtotal + grandTax

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Subtotal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.sub_total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "₹${"%.2f".format(grandSubtotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tax row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.tax),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "₹${"%.2f".format(grandTax)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Grand Total row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total_amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "₹${"%.2f".format(grandTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
