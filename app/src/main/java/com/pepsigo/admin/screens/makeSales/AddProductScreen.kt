package com.pepsigo.admin.screens.makeSales

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.SaleInventorySearchItemUi
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: MakeSaleViewModel,
    onNavigateBack: () -> Unit,
    onProductClick: (SaleInventorySearchItemUi) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val refreshState = rememberPullToRefreshState()

    // Debounced search query - only updates after user stops typing
    var debouncedSearchQuery by remember { mutableStateOf(state.productSearchQuery) }

    // Debounce the search query with 400ms delay, but reset immediately when cleared
    LaunchedEffect(state.productSearchQuery) {
        if (state.productSearchQuery.isEmpty()) {
            // Reset immediately when cleared
            debouncedSearchQuery = ""
        } else {
            delay(400L)
            debouncedSearchQuery = state.productSearchQuery
        }
    }

    // Reload full products list when search query becomes empty (cleared via backspace or X button)
    LaunchedEffect(state.productSearchQuery) {
        if (state.productSearchQuery.isEmpty() && state.lastServerSearchQuery != null) {
            // User cleared the search, reload full list
            viewModel.loadProducts()
        }
    }

    // Load products when screen opens (if customer is selected)
    LaunchedEffect(state.selectedCustomer) {
        if (state.selectedCustomer != null && state.products.isEmpty()) {
            viewModel.loadProducts()
        }
    }

    // Filter products based on debounced search query using derivedStateOf
    val displayedProducts by remember(state.products, debouncedSearchQuery) {
        derivedStateOf {
            if (debouncedSearchQuery.length < 3) {
                // Show all products when query is less than 3 characters
                state.products
            } else {
                // Filter locally when query is 3+ characters
                state.products.filter {
                    it.itemName.contains(debouncedSearchQuery, ignoreCase = true)
                }
            }
        }
    }

    // Check if we need to search from server (local filter returned empty)
    // Only search if we haven't already searched for this query
    val shouldSearchServer by remember(displayedProducts, debouncedSearchQuery, state.lastServerSearchQuery, state.productsLoading) {
        derivedStateOf {
            debouncedSearchQuery.length >= 3 &&
                    displayedProducts.isEmpty() &&
                    !state.productsLoading &&
                    state.lastServerSearchQuery != debouncedSearchQuery
        }
    }

    // Trigger server search when local filter is empty and we haven't searched this query yet
    LaunchedEffect(shouldSearchServer) {
        if (shouldSearchServer) {
            viewModel.searchProductsFromServer(debouncedSearchQuery)
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.add_product),
                icon = Icons.Default.Inventory,
                desc = stringResource(R.string.add_product),
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
            // Search bar surface (sticky)
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
                    OutlinedTextField(
                        value = state.productSearchQuery,
                        onValueChange = { viewModel.onProductSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_products)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        },
                        trailingIcon = {
                            if (state.productSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearProductSearch() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )

                    if (state.productSearchQuery.isNotEmpty() && state.productSearchQuery.length < 3) {
                        Text(
                            text = stringResource(R.string.type_more_to_search),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Products list with pull to refresh
            PullToRefreshBox(
                isRefreshing = state.productsLoading,
                onRefresh = {
                    viewModel.clearProductSearch()
                    viewModel.loadProducts(forceRefresh = true)
                },
                state = refreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when {
                        state.productsError != null -> {
                            // Error state
                            item {
                                ErrorContent(
                                    message = state.productsError
                                        ?: stringResource(R.string.error_loading_products),
                                    onRetry = { viewModel.retryLoadProducts() }
                                )
                            }
                        }

                        !state.productsLoading && displayedProducts.isEmpty() -> {
                            // Empty state
                            item {
                                EmptyProductsContent(
                                    hasSearchQuery = state.productSearchQuery.isNotEmpty(),
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        }

                        else -> {
                            // Products list
                            items(displayedProducts, key = { it.id }) { product ->
                                ProductListItem(
                                    product = product,
                                    onClick = { onProductClick(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductListItem(
    product: SaleInventorySearchItemUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product icon
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.itemName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    if (product.isFree) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "FREE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Unit: ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "GST: ${product.gstPercent}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (product.offerPrice.isNotEmpty() && product.offerPrice != "null") {
                        Text(
                            text = "Offer: ${product.offerPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Stock info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Available: ${product.stockSummary.totalAvailable}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.stockSummary.totalAvailable > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Batches: ${product.stockSummary.batchesCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (product.stockSummary.nearestExpiry.isNotEmpty()) {
                        Text(
                            text = "Expiry: ${product.stockSummary.nearestExpiry}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyProductsContent(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasSearchQuery)
                    stringResource(R.string.no_products_found)
                else
                    stringResource(R.string.no_products_available),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
