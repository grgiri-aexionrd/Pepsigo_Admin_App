package com.pepsigo.admin.screens.reports

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.ItemWiseSalesData
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWiseSalesScreen(
    viewModel: ItemWiseSalesViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.item_wise_sales),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.item_wise_sales),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = state.isLoading,
            onRefresh = {  viewModel.fetch(state.fromDate, state.toDate)  },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(inversePrimaryLight.copy(alpha = 0.35f))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                stickyHeader {
                    PaymentTitleCard(
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        error = state.isDateError,
                        onFromDateSelected = { viewModel.setFromDate(it) },
                        onToDateSelected = { viewModel.setToDate(it) },
                        onGetDetailsClicked = { viewModel.fetch(state.fromDate, state.toDate) }
                    )
                    ItemSalesTableHeader()
                }

                //
                if (state.isError) {
                    item {
                        Text(
                            text = state.snackbarMessage ?: "Error",
                            modifier = Modifier.align(Alignment.Center)
                                .padding(12.dp)
                        )
                    }
                }

                val list = state.data?.data ?: emptyList()
                items(list) { exec ->
                    ItemSalesTableRow(exec)
                }
                // footer / loading overlay
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemSalesTableHeader(){
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Item",
                modifier = Modifier.weight(0.35f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Unit",
                modifier = Modifier.weight(0.25f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Quantity",
                modifier = Modifier.weight(0.25f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sales Total",
                modifier = Modifier.weight(0.3f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider()
    }

}

@Composable
fun ItemSalesTableRow(exec: ItemWiseSalesData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = exec.itemName,
            modifier = Modifier.weight(0.35f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start
        )
        Text(
            text = exec.unit,
            modifier = Modifier.weight(0.25f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = exec.totalQty,
            modifier = Modifier.weight(0.25f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "₹${exec.totalSales}",
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )

    }
    HorizontalDivider()

}