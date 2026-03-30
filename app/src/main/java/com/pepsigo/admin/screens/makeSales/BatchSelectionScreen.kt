package com.pepsigo.admin.screens.makeSales

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.InventoryBatchUi
import com.pepsigo.admin.model.InventoryBestBatchItemUi
import com.pepsigo.admin.model.SaleInventorySearchItemUi
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchSelectionScreen(
    viewModel: MakeSaleViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val product = state.selectedProduct

    // Load batches when screen opens
    LaunchedEffect(product) {
        if (product != null) {
            viewModel.loadBatchesForProduct()
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.select_batch),
                icon = Icons.Default.Inventory,
                desc = stringResource(R.string.select_batch),
                onBackClick = {
                    viewModel.clearBatchSelection()
                    onNavigateBack()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { paddingValues ->

        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No product selected")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product details section
                item {
                    ProductDetailsCard(product = product)
                }

                // Best batch section (display only - user adds from batch list below)
                item {
                    BestBatchSection(
                        bestBatch = state.bestBatch,
                        isLoading = state.bestBatchLoading,
                        error = state.bestBatchError,
                        onRetry = { viewModel.loadBatchesForProduct() }
                    )
                }

                // All batches section header
                item {
                    Text(
                        text = stringResource(R.string.available_batches),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Loading state for batches
                if (state.batchesLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Error state for batches
                if (state.batchesError != null && !state.batchesLoading) {
                    item {
                        ErrorContent(
                            error = state.batchesError!!,
                            onRetry = { viewModel.loadBatchesForProduct() }
                        )
                    }
                }

                // Empty state
                if (!state.batchesLoading && state.batchesError == null && state.allBatches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_batches_available),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Batches list
                items(state.allBatches, key = { it.batchId }) { batch ->
                    // Get cart quantity for this batch if exists
                    val cartItem = state.cartItems.find {
                        it.inventoryId == product.id && it.batchId == batch.batchId
                    }
                    BatchItemCard(
                        batch = batch,
                        isBestBatch = state.bestBatch?.batchId == batch.batchId,
                        quantityValue = state.batchQuantities[batch.batchId] ?: "",
                        cartQuantity = cartItem?.quantity,
                        onQuantityChange = { viewModel.updateBatchQuantity(batch.batchId, it) },
                        onAddToCart = { viewModel.addToCart(batch.batchId, batch) },
                        onRemoveFromCart = { viewModel.removeFromCart(product.id, batch.batchId) }
                    )
                }

                // Cart summary section for this product
                val productCartItems = state.cartItems.filter { it.inventoryId == product.id }
                if (productCartItems.isNotEmpty()) {
                    item {
                        CartSummaryCard(
                            cartItems = productCartItems,
                            onRemoveItem = { batchId -> viewModel.removeFromCart(product.id, batchId) }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsCard(product: SaleInventorySearchItemUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = product.itemName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailChip(label = "Unit", value = product.unit)
                DetailChip(label = "GST", value = product.gstPercent)
                DetailChip(label = "Stock", value = product.stockSummary.totalAvailable.toString())
            }
            if (product.offerPrice.isNotEmpty() && product.offerPrice != "-") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Offer Price: ${product.offerPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            if (product.isFree) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Free Item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Green,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DetailChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun BestBatchSection(
    bestBatch: InventoryBestBatchItemUi?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp,vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700) // Gold color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.best_batch),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onRetry) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
                bestBatch == null -> {
                    Text(
                        text = stringResource(R.string.no_best_batch),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
                else -> {
                    BestBatchContent(batch = bestBatch)
                }
            }
        }
    }
}

@Composable
private fun BestBatchContent(
    batch: InventoryBestBatchItemUi
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Batch #${batch.batchId}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Available: ${batch.availableQuantity} ${batch.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Sale: ${batch.salePrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (batch.expiryDate.isNotEmpty() && batch.expiryDate != "-") {
                    Text(
                        text = "Exp: ${batch.expiryDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Add from the batch list below",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun BatchItemCard(
    batch: InventoryBatchUi,
    isBestBatch: Boolean = false,
    quantityValue: String,
    cartQuantity: Int? = null,
    onQuantityChange: (String) -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit = {}
) {
    val quantityInt = quantityValue.toIntOrNull() ?: 0
    val isValidQuantity = quantityInt > 0 && quantityInt <= batch.availableQuantity
    val isInCart = cartQuantity != null && cartQuantity > 0

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                batch.isExpired -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                isBestBatch -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Batch #${batch.batchId}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (isBestBatch) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Best Batch",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.height(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Best",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Available: ${batch.availableQuantity} ${batch.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!batch.expiryDate.isNullOrEmpty() && batch.expiryDate != "-") {
                        Text(
                            text = "Expiry: ${batch.expiryDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (batch.isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Sale: ${batch.salePrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Cost: ${batch.costPrice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Retail: ${batch.retailPrice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (batch.isFreeOffer) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Free Offer",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }

            if (batch.isExpired) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expired",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show cart indicator if item is in cart
            if (isInCart) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "In cart: $cartQuantity ${batch.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantityValue,
                    onValueChange = { onQuantityChange(it.filter { c -> c.isDigit() }) },
                    label = { Text(stringResource(R.string.quantity)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = quantityValue.isNotEmpty() && !isValidQuantity,
                    enabled = !batch.isExpired && batch.availableQuantity > 0
                )
                Button(
                    onClick = onAddToCart,
                    enabled = isValidQuantity && !batch.isExpired
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isInCart) stringResource(R.string.update) else stringResource(R.string.add))
                }
                if (isInCart) {
                    IconButton(
                        onClick = onRemoveFromCart
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove from cart",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            if (quantityValue.isNotEmpty() && !isValidQuantity) {
                Text(
                    text = if (quantityInt <= 0) "Enter valid quantity" else "Max available: ${batch.availableQuantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
private fun CartSummaryCard(
    cartItems: List<CartItem>,
    onRemoveItem: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.cart_items),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${cartItems.size} item(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            cartItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Batch #${item.batchId}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${item.quantity} ${item.unit} × ${item.salePrice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(
                        onClick = { onRemoveItem(item.batchId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

