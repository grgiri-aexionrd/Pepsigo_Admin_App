package com.pepsigo.admin.screens.sales

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
import androidx.compose.material.icons.filled.PointOfSale
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
fun SalesScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCreateSale: () -> Unit
) {

    // need to add pull to refresh to sync latest data
    val salesState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.sales),
                icon = Icons.Default.PointOfSale,
                desc = stringResource(id = R.string.sales),
                onBackClick = {
                    when (val details = salesState) {
                        is SalesUiState.SalesDetails -> {
                            when {
                                details.isReturn -> {
                                    viewModel.exitReturnMode()
                                }

                                details.isReturnSummary -> {
//                                    viewModel.showReturnScreen(details.sale)
                                }
                                else -> viewModel.loadSales()
                            }
                        }

                        else -> { onNavigateBack() }
                    }
                }
            )
        },
        floatingActionButton = {
            if (salesState is SalesUiState.Success) {
                FloatingActionButton(
                    onClick = onNavigateToCreateSale,
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = when(salesState) {
                    is SalesUiState.SalesDetails -> (salesState as SalesUiState.SalesDetails).isError
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
                when (val state = salesState) {
                    is SalesUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.loading))
                        }
                    }

                    is SalesUiState.Error -> {
                        val error = state.error
                        ErrorView(
                            message = error.userFriendlyMessage,
                            onRetry = { viewModel.retry() }
                        )
                    }

                    is SalesUiState.Success -> {
                        val sales = state.sales.collectAsLazyPagingItems()
                        val isRefreshing = sales.loadState.refresh is LoadState.Loading
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { sales.refresh() },
                            state = refreshState,
//                            modifier = Modifier.padding(innerPadding)
                        ) {
                            when (val refresh = sales.loadState.refresh) {
                                is LoadState.Error -> {
                                    val appError = refresh.error.toAppError()

                                    ErrorView(
                                        message = appError.userFriendlyMessage,
                                        onRetry = { sales.retry() }
                                    )
                                }
                                is LoadState.NotLoading -> {
                                    SalesList(
                                        sales,
                                        modifier = Modifier.padding(16.dp),
                                        onItemClick = { item ->
                                            Log.d("SalesScreen", "Item clicked: $item")
                                            viewModel.getSaleDetails(item)
                                        }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    is SalesUiState.SalesDetails -> {
                        Log.d("SalesScreen", "SalesDetails: $state")
                        when {
                            state.isReturn -> {
//                                ReturnScreen(
//                                    returnItem = state.sale,
//                                    returnItemList = state.returnItemList,
//                                    onCheckedChange = { item, checked ->
//                                        Log.d("SalesScreen", "Item checked: $checked")
//                                        viewModel.toggleItemSelection(item, checked)
//                                    },
//                                    onQuantityChange = { item, qty ->
//                                        Log.d("SalesScreen", "Quantity changed: $qty")
//                                        viewModel.updateQuantity(item, qty)
//                                    },
//                                    currentSelection = viewModel.selectedItems,
//                                    onReturnCancel = {
//                                        viewModel.exitReturnMode()
//                                    },
//                                    onReturn = {
//                                        viewModel.showReturnSummary()
//                                    },
//                                    modifier = Modifier.padding(16.dp),
//                                )

                            }

                            state.isReturnSummary -> {
//                                Log.d("SalesScreen", "ReturnSummary: $state")
//                                ReturnSummaryScreen(
//                                    saleId = state.sale.sale.saleId,
//                                    invoiceNumber = state.sale.sale.invoiceNumber,
//                                    returnSummaryItem = state.returnItemList,
//                                    onReturnCancel = {
//                                        viewModel.showReturnScreen(state.sale)
//                                    },
//                                    onReturn = {
//                                        viewModel.returnSale(
//                                            state.returnItemList,
//                                            state.sale.sale.saleId
//                                        )
//                                    },
//                                    modifier = Modifier.padding(16.dp)
//                                )
                            }

                            else -> {
//                                SalesDetails(
//                                    sale = state.sale,
//                                    onCancelClick = { id ->
//                                        Log.d("SalesScreen", "To cancel item : $id")
//                                        viewModel.cancelSale(id)
//                                    },
//                                    onReturnClick = {
//                                        Log.d("SalesScreen", "To return item : $it")
//                                        viewModel.showReturnScreen(it)
////                            viewModel.returnSale(it)
//                                    },
//                                    modifier = Modifier.padding(16.dp),
//                                    isLoading = state.isLoading
//                                )
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
                when (salesState) {
                    is SalesUiState.SalesDetails if (salesState as SalesUiState.SalesDetails).isReturn -> {
                        viewModel.exitReturnMode()
                    }

                    is SalesUiState.SalesDetails if (salesState as SalesUiState.SalesDetails).isReturnSummary -> {
//                        viewModel.showReturnScreen((salesState as SalesUiState.SalesDetails).sale)
                    }

                    // If user is inside sale details but not return mode → go back to list
                    is SalesUiState.SalesDetails -> {
                        viewModel.loadSales()
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