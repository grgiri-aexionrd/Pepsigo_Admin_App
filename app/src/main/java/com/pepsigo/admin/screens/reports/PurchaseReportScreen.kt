package com.pepsigo.admin.screens.reports

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.DockedDatePicker
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.NoDataCard
import com.pepsigo.admin.screens.commonComponents.ReportCard
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun PurchaseReportScreen(
    viewModel: PurchaseReportViewModel,
    onNavigateBack: () -> Unit
) {
    val purchaseReport by viewModel.purchaseReport.collectAsState()
    val refreshState = rememberPullToRefreshState()

    val filteredVendors by remember {
        derivedStateOf {
            if (purchaseReport.searchQuery.isBlank()) {
                purchaseReport.dropDown.take(50)
            } else {
                purchaseReport.dropDown
                    .filter {
                        it.name.contains(purchaseReport.searchQuery, true)
                    }
                    .take(50)
            }
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.purchase_register),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.purchase_register),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = purchaseReport.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                stickyHeader {
                    // datefilter, customer selection
                    FilterHeader(
                        fromDate = purchaseReport.fromDate,
                        toDate = purchaseReport.toDate,
                        error = purchaseReport.DateError,
                        onFromDateSelected = { viewModel.setFromDate(it) },
                        onToDateSelected = { viewModel.setToDate(it) },
                        dropDown = filteredVendors,
                        dropDownLabel = stringResource(R.string.select_vendor),
                        dropDownError = purchaseReport.dropDownError,
                        searchQuery = purchaseReport.searchQuery,
                        selected = purchaseReport.selected,
                        onSelected = { viewModel.updateSelected(it) },
                        onFetchClick = { dateFrom, dateTo, customerId ->
                            viewModel.fetchPurchaseRegister(dateFrom, dateTo, customerId)
                        },
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        isLoading = purchaseReport.isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if(purchaseReport.snackbarMessage != null){
                    item {
                        DropDownErrorCard(purchaseReport.snackbarMessage!!)
                    }
                }

                if ( purchaseReport.reportFetched && purchaseReport.report.isEmpty()) {

                    item {
                        NoDataCard(message = stringResource(R.string.no_sales_data))
                    }
                } else {
                    items(purchaseReport.report) { reportItem ->
                        ReportCard(
                            item = reportItem,
                            title = { it.invoiceNumber },
                            subtitle = { it.businessName },
                            status = { it.invoiceStatus },
                            dateValue = { it.saleDate },
                            amountValue = { "₹${it.totalAmount}" },
                            bottomItems = {
                                listOf(
                                    stringResource(R.string.sub_total) to "₹${it.subTotal}",
                                    stringResource(R.string.discount) to "₹${it.discountBeforeTax}",
                                    stringResource(R.string.tax) to "₹${it.tax}"
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
        }
    }
}





