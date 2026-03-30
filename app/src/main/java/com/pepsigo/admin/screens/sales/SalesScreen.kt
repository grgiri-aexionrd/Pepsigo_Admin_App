package com.pepsigo.admin.screens.sales

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.pepsigo.admin.utils.toAppError

@Composable
fun SalesScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCreateSale: () -> Unit,
    onNavigateToMakePayment: (Int, Int, Double) -> Unit,
    onNavigateToPrintInvoice: (Int) -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val detailsState by viewModel.detailsState.collectAsState()
    val screenMode by viewModel.screenMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.sales),
                icon = Icons.Default.PointOfSale,
                desc = stringResource(id = R.string.sales),
                onBackClick = {
                    when (screenMode) {
                        SalesScreenMode.RETURN -> viewModel.exitReturnMode()
                        SalesScreenMode.RETURN_SUMMARY -> {
                            val sale = detailsState.sale
                            if (sale != null) {
                                viewModel.showReturnScreen(sale)
                            } else {
                                viewModel.exitReturnMode()
                            }
                        }
                        SalesScreenMode.DETAILS -> viewModel.goBackToList()
                        SalesScreenMode.LIST -> onNavigateBack()
                        else -> { viewModel.goBackToList() }
                    }
                }
            )
        },
        floatingActionButton = {
            if (screenMode == SalesScreenMode.LIST && listState is SalesUiState.Success) {
                FloatingActionButton(
                    onClick = onNavigateToCreateSale,
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (detailsState.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (detailsState.isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
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
            when (screenMode) {
                SalesScreenMode.LIST -> {
                    // Show list based on listState
                    when (val state = listState) {
                        is SalesUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(id = R.string.loading))
                            }
                        }

                        is SalesUiState.Error -> {
                            ErrorView(
                                message = state.error.userFriendlyMessage,
                                onRetry = { viewModel.retry() }
                            )
                        }

                        is SalesUiState.Success -> {
                            val sales = state.salesList.collectAsLazyPagingItems()
                            val isRefreshing = sales.loadState.refresh is LoadState.Loading
                            PullToRefreshBox(
                                isRefreshing = isRefreshing,
                                onRefresh = { sales.refresh() },
                                state = refreshState,
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
                    }
                }

                SalesScreenMode.DETAILS -> {
                    if (detailsState.isLoading && detailsState.sale == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (detailsState.sale != null && detailsState.deliveryExec != null) {
                        SalesDetails(
                            sale = detailsState.sale!!,
                            deliveryExec = detailsState.deliveryExec!!,
                            onCancelClick = { id ->
                                Log.d("SalesScreen", "To cancel item : $id")
                                viewModel.cancelSale(id)
                            },
                            onReturnClick = { sale ->
                                Log.d("SalesScreen", "To return item : $sale")
                                viewModel.showReturnScreen(sale)
                            },
                            onPrintInvoice = { saleId ->
                                onNavigateToPrintInvoice(saleId)
                            },
                            onAttachPayment = { saleId, customerId, amount ->
                                onNavigateToMakePayment(saleId, customerId, amount)
                            },
                            modifier = Modifier.padding(16.dp),
                            isLoading = detailsState.isLoading
                        )
                    }
                }

                SalesScreenMode.RETURN -> {
                    if (detailsState.sale != null) {
                        SalesReturnScreen(
                            returnItem = detailsState.sale!!,
                            onCheckedChange = { item, checked ->
                                Log.d("SalesScreen", "Item checked: $checked")
                                viewModel.toggleItemSelection(item, checked)
                            },
                            onQuantityChange = { item, qty ->
                                Log.d("SalesScreen", "Quantity changed: $qty")
                                viewModel.updateQuantity(item, qty)
                            },
                            currentSelection = viewModel.selectedItems,
                            onReturnCancel = {
                                viewModel.exitReturnMode()
                            },
                            onReturn = {
                                viewModel.showReturnSummary()
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                SalesScreenMode.RETURN_SUMMARY -> {
                    if (detailsState.sale != null) {
                        SalesReturnSummaryScreen(
                            saleId = detailsState.sale!!.sales.salesId,
                            invoiceNumber = detailsState.sale!!.sales.invoiceNumber,
                            returnSummaryItem = detailsState.returnItemList,
                            onReturnCancel = {
                                viewModel.showReturnScreen(detailsState.sale!!)
                            },
                            onReturn = {
                                viewModel.returnSale(
                                    detailsState.returnItemList,
                                    detailsState.sale!!.sales.salesId
                                )
                            },
                            isLoading = detailsState.isLoading,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                SalesScreenMode.RETURN_SUCCESS -> {
                    if (detailsState.returnResponse != null) {
                        SalesReturnSuccessScreen(
                            returnMessage = detailsState.returnMessage!!,
                            returnResponse = detailsState.returnResponse!!,
                            returnItemsTotalAmount = detailsState.returnItemsTotalAmount,
                            isPaymentMade = detailsState.isPaymentMade,
                            onPrintInvoice = {
                                onNavigateToPrintInvoice(detailsState.returnResponse!!.id)
                            },
                            onMakePayment = { saleId, customerId, amount ->
                                onNavigateToMakePayment(saleId, customerId, amount)
                            },
                            markPaymentMade = {
                                viewModel.markPaymentMade()
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Handle snackbar messages
            detailsState.snackbarMessage?.let { message ->
                LaunchedEffect(message) {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    viewModel.clearSnackbarMessage()
                }
            }
        }

        BackHandler {
            when (screenMode) {
                SalesScreenMode.RETURN -> viewModel.exitReturnMode()
                SalesScreenMode.RETURN_SUMMARY -> viewModel.exitReturnMode()
                SalesScreenMode.RETURN_SUCCESS -> viewModel.exitReturnSuccess()
                SalesScreenMode.DETAILS -> viewModel.goBackToList()
                SalesScreenMode.LIST -> onNavigateBack()
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