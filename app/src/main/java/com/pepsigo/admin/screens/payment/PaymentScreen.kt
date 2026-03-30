package com.pepsigo.admin.screens.payment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.pepsigo.admin.R
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.utils.toAppError

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (PaymentUiModel) -> Unit = {},
    onNavigateToMakePayment: () -> Unit = {}
) {
    val paymentState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val refreshStateHolder = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.payments),
                icon = Icons.Default.Payments,
                desc = stringResource(id = R.string.payments),
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMakePayment) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.make_payment))
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
            when (val state = paymentState) {
                is PaymentUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.loading))
                    }
                }

                is PaymentUiState.Error -> {
                    PaymentErrorView(
                        message = state.error.userFriendlyMessage,
                        onRetry = { viewModel.retry() }
                    )
                }

                is PaymentUiState.Success -> {
                    val payments = state.payments.collectAsLazyPagingItems()
                    val refreshState = payments.loadState.refresh
                    val isRefreshing = refreshState is LoadState.Loading

                    // ✅ CATCH + LOG THE REAL ERROR CAUSE
                    if (refreshState is LoadState.Error) {
                        Log.e(
                            "PagingError",
                            "Payments refresh failed",
                            refreshState.error
                        )
                    }
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { payments.refresh() },
                        state = refreshStateHolder
                    ) {
                        when (refreshState) {
                            is LoadState.Error -> {
                                val appError = refreshState.error.toAppError()
                                PaymentErrorView(
                                    message = appError.userFriendlyMessage,
                                    onRetry = { payments.retry() }
                                )
                            }
                            is LoadState.NotLoading -> {
                                PaymentList(
                                    payments = payments,
                                    filterState = filterState,
                                    onItemClick = { item ->
                                        viewModel.selectPayment(item)
                                        onNavigateToDetail(item)
                                    },
                                    onApplyFilter = { filter ->
                                        viewModel.applyFilter(filter)
                                    },
                                    onClearFilter = {
                                        viewModel.clearFilter()
                                    },
                                    onFetchUsers = {
                                        viewModel.fetchUsersForFilter()
                                    }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.Red)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}
