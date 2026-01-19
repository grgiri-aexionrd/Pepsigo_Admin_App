package com.pepsigo.admin.screens.purchase

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.ui.theme.inversePrimaryLight
import com.pepsigo.admin.utils.toAppError

@Composable
fun PurchaseScreen(
    viewModel: PurchaseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCreatePurchase: () -> Unit
) {

    // need to add pull to refresh to sync latest data
    val purchaseState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.purchase),
                icon = Icons.Default.ShoppingCart,
                desc = stringResource(id = R.string.purchase),
                onBackClick = {
                    when (val details = purchaseState) {
                        is PurchaseUiState.PurchaseDetails -> {
                            when {
                                details.isReturn -> {
                                    viewModel.exitReturnMode()
                                }

                                details.isReturnSummary -> {
                                    viewModel.showReturnScreen(details.purchase)
                                }
                                else -> viewModel.loadPurchases()
                            }
                        }

                        else -> { onNavigateBack() }
                    }
                }
            )
        },
        floatingActionButton = {
            if (purchaseState is PurchaseUiState.Success) {
                FloatingActionButton(
                    onClick = onNavigateToCreatePurchase,
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = when(purchaseState) {
                    is PurchaseUiState.PurchaseDetails -> (purchaseState as PurchaseUiState.PurchaseDetails).isError
                    else -> false
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                color = Color.Transparent
            ) {
                when (val state = purchaseState) {
                    is PurchaseUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.loading))
                        }
                    }

                    is PurchaseUiState.Error -> {
                        val error = state.error
                        ErrorView(
                            message = error.userFriendlyMessage,
                            onRetry = { viewModel.retry() }
                        )
                    }

                    is PurchaseUiState.Success -> {
                        val purchases = state.purchases.collectAsLazyPagingItems()
                        val isRefreshing = purchases.loadState.refresh is LoadState.Loading
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { purchases.refresh() },
                            state = refreshState,
//                            modifier = Modifier.padding(innerPadding)
                        ) {
                            when (val refresh = purchases.loadState.refresh) {
                                is LoadState.Error -> {
                                    val appError = refresh.error.toAppError()

                                    ErrorView(
                                        message = appError.userFriendlyMessage,
                                        onRetry = { purchases.retry() }
                                    )
                                }
                                is LoadState.NotLoading -> {
                                    PurchaseList(
                                        purchases,
                                        modifier = Modifier.padding(16.dp),
                                        onItemClick = { item ->
                                            Log.d("PurchaseScreen", "Item clicked: $item")
                                            viewModel.getPurchaseDetails(item)
                                        }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    is PurchaseUiState.PurchaseDetails -> {
                        Log.d("PurchaseScreen", "PurchaseDetails: $state")
                        when {
                            state.isReturn -> {
                                ReturnScreen(
                                    returnItem = state.purchase,
                                    returnItemList = state.returnItemList,
                                    onCheckedChange = { item, checked ->
                                        Log.d("PurchaseScreen", "Item checked: $checked")
                                        viewModel.toggleItemSelection(item, checked)
                                    },
                                    onQuantityChange = { item, qty ->
                                        Log.d("PurchaseScreen", "Quantity changed: $qty")
                                        viewModel.updateQuantity(item, qty)
                                    },
                                    currentSelection = viewModel.selectedItems,
                                    onReturnCancel = {
                                        viewModel.exitReturnMode()
                                    },
                                    onReturn = {
                                        viewModel.showReturnSummary()
                                    },
                                    modifier = Modifier.padding(16.dp),
                                )

                            }

                            state.isReturnSummary -> {
                                Log.d("PurchaseScreen", "ReturnSummary: $state")
                                ReturnSummaryScreen(
                                    purchaseId = state.purchase.purchase.purchaseId,
                                    invoiceNumber = state.purchase.purchase.invoiceNumber,
                                    returnSummaryItem = state.returnItemList,
                                    onReturnCancel = {
                                        viewModel.showReturnScreen(state.purchase)
                                    },
                                    onReturn = {
                                        viewModel.returnPurchase(
                                            state.returnItemList,
                                            state.purchase.purchase.purchaseId
                                        )
                                    },
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            else -> {
                                PurchaseDetails(
                                    purchase = state.purchase,
                                    onCancelClick = { id ->
                                        Log.d("PurchaseScreen", "To cancel item : $id")
                                        viewModel.cancelPurchase(id)
                                    },
                                    onReturnClick = {
                                        Log.d("PurchaseScreen", "To return item : $it")
                                        viewModel.showReturnScreen(it)
//                            viewModel.returnPurchase(it)
                                    },
                                    modifier = Modifier.padding(16.dp),
                                    isLoading = state.isLoading
                                )
                            }
                        }
                        state.snackbarMessage?.let { message ->
                            LaunchedEffect(message) {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.clearSnackbarMessage()
                            }
                        }
                    }

                    else -> {}
                }

            }
            BackHandler {
                when (purchaseState) {
                    is PurchaseUiState.PurchaseDetails if (purchaseState as PurchaseUiState.PurchaseDetails).isReturn -> {
                        viewModel.exitReturnMode()
                    }

                    is PurchaseUiState.PurchaseDetails if (purchaseState as PurchaseUiState.PurchaseDetails).isReturnSummary -> {
                        viewModel.showReturnScreen((purchaseState as PurchaseUiState.PurchaseDetails).purchase)
                    }

                    // If user is inside purchase details but not return mode → go back to list
                    is PurchaseUiState.PurchaseDetails -> {
                        viewModel.loadPurchases()
                    }

                    // Any other state → go back normally
                    else -> onNavigateBack()
                }
            }

        }
    }





@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.Red)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}



